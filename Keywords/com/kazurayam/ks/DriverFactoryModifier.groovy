package com.kazurayam.ks

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.constants.StringConstants
import com.kms.katalon.core.driver.IDriverType;
import com.kms.katalon.core.exception.StepFailedException;
import com.kms.katalon.core.logging.LogLevel;
import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.core.webui.driver.WebMobileDriverFactory;
import com.kms.katalon.core.webui.driver.WebUIDriverType
import com.kms.katalon.core.webui.driver.chrome.ChromeDriverUtil
import com.kms.katalon.core.webui.driver.edge.EdgeDriverUtil
import com.kms.katalon.core.webui.driver.firefox.FirefoxDriverUtil
import com.kms.katalon.core.webui.util.FileExcutableUtil;
import com.kms.katalon.core.webui.util.OSUtil

public class DriverFactoryModifier {
	
	/**
	 * 
	 * @param driverName one of "Chrome", "Chrome (headless)", "Firefox", "Firefox (headless)", "Edge Chromium"
	 */
	@Keyword
	public static void apply(String driverName) {
		Objects.requireNonNull(driverName)
		WebUIDriverTypeModifier.apply()
		if (WebUIDriverType.isDefinedDriverName(driverName)) {
			WebUIDriverType driverType = WebUIDriverType.valueOfByDriverName(driverName)
			apply(driverType)
		} else {
			throw new IllegalArgumentException(driverName + " is not a valid WebUIDriverType")
		}
	}

	/**
	 * modify DriverFactory.openWebDriver() method dynamically so that it opens a browser of the driver type specified.
	 * 
	 * @param driverType 
	 */
	public static void apply(WebUIDriverType driverType) {
		Objects.requireNonNull(driverType)
		//
		DriverFactory.metaClass.'static'.openWebDriver = { ->
			try {
				WebDriver webDriver;
				if (DriverFactory.isUsingExistingDriver()) {
					webDriver = DriverFactory.startExistingBrowser();
				} else {
					String remoteWebDriverUrl = DriverFactory.getRemoteWebDriverServerUrl();
					if (StringUtils.isNotEmpty(remoteWebDriverUrl)) {
						webDriver = DriverFactory.startRemoteBrowser();
					} else {
						println "[DriverFactory#openWebDriver] DriverFactory.getExecutedBrowser(): " + DriverFactory.getExecutedBrowser()
						webDriver = DriverFactory.startNewBrowser(DriverFactory.getExecutedBrowser());
					}
	
					DriverFactory.saveWebDriverSessionData(webDriver);
					DriverFactory.changeWebDriver(webDriver);
				}
	
				return webDriver;
			} catch (Error e) {
				DriverFactory.logger.logMessage(LogLevel.WARNING, e.getMessage(), e);
				throw new StepFailedException(e);
			}
		}
		//
		DriverFactory.metaClass.'static'.getExecutedBrowser = { ->
			IDriverType webDriverType = null;
			if (DriverFactory.isUsingExistingDriver()) {
				webDriverType = WebUIDriverType.fromStringValue(RunConfiguration.getExistingSessionDriverType());
			}
	
			if (webDriverType != null) {
				return webDriverType;
			}
	
			String remoteWebDriverUrl = DriverFactory.getRemoteWebDriverServerUrl();
			
			String driverConnectorProperty = null;
			String driverTypeString = null
			if (StringUtils.isNotBlank(remoteWebDriverUrl)) {
				driverConnectorProperty = RunConfiguration.REMOTE_DRIVER_PROPERTY  // Remote
				driverTypeString = RunConfiguration.getDriverSystemProperty(driverConnectorProperty, DriverFactory.EXECUTED_BROWSER_PROPERTY)
			} else {
				driverConnectorProperty = DriverFactory.WEB_UI_DRIVER_PROPERTY  // WebUI
				// Here is a hack!
				//driverTypeString = RunConfiguration.getDriverSystemProperty(driverConnectorProperty, DriverFactory.EXECUTED_BROWSER_PROPERTY)
				driverTypeString = driverType.name()
			}
			if (driverTypeString != null) {
				webDriverType = WebUIDriverType.valueOf(driverTypeString);
			}
	
			if (webDriverType == null && RunConfiguration.getDriverSystemProperty(DriverFactory.MOBILE_DRIVER_PROPERTY,
					WebMobileDriverFactory.EXECUTED_MOBILE_PLATFORM) != null) {
				webDriverType = WebUIDriverType.valueOf(RunConfiguration.getDriverSystemProperty(DriverFactory.MOBILE_DRIVER_PROPERTY,
						WebMobileDriverFactory.EXECUTED_MOBILE_PLATFORM));
			}
	
			return webDriverType;
		}
	}
}
