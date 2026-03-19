package com.kazurayam.ks

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.driver.IDriverType;
import com.kms.katalon.core.webui.driver.WebUIDriverType
import com.kms.katalon.core.exception.StepFailedException;
import com.kms.katalon.core.logging.LogLevel;
import com.kms.katalon.core.webui.driver.DriverFactory;

import org.codehaus.groovy.ast.ClassNode

public class DriverFactoryModifier {
	
	/**
	 * 
	 * @param driverName one of "Chrome", "Chrome (headless)", "Firefox", "Firefox (headless)", "Edge Chromium"
	 */
	@Keyword
	public static void runWith(String driverName) {
		Objects.requireNonNull(driverName)
		WebUIDriverTypeModifier.apply()
		if (WebUIDriverType.isDefinedDriverName(driverName)) {
			WebUIDriverType driverType = WebUIDriverType.valueOfByDriverName(driverName)
			runWith(driverType)
		} else {
			throw new IllegalArgumentException(driverName + " is not a valid WebUIDriverType")
		}
	}

	/**
	 * modify DriverFactory.openWebDriver() method dynamically so that it opens a browser of the driver type specified.
	 * 
	 * @param driverType 
	 */
	public static void runWith(WebUIDriverType driverType) {
		Objects.requireNonNull(driverType)
		DriverFactory.metaClass.'static'.openWebDriver = { ->
			/**
			 * Open a new WebDriver based on the RunConfiguration
			 */
			try {
				WebDriver webDriver;
				if (DriverFactory.isUsingExistingDriver()) {
					println "[DriverFactoryModifier#runWith] isUsingExistingDriver: " + DriverFactory.isUsingExistingDriver()
					webDriver = DriverFactory.startExistingBrowser();
				} else {
					String remoteWebDriverUrl = DriverFactory.getRemoteWebDriverServerUrl();
					if (StringUtils.isNotEmpty(remoteWebDriverUrl)) {
						println "[DriverFactoryModifier#runWith] isNotEmpty(remoteWebDriverUrl): " + StringUtils.isNotEmpty(remoteWebDriverUrl)
						webDriver = DriverFactory.startRemoteBrowser();
					} else {
						// Here I hacked!
						// webDriver = DriverFactory.startNewBrowser(DriverFactory.getExecutedBrowser());
						webDriver = DriverFactory.startNewBrowser(driverType);
					}
					DriverFactory.saveWebDriverSessionData(webDriver);
					DriverFactory.changeWebDriver(webDriver);
				}
				return webDriver
			} catch (Error e) {
				DriverFactory.logger.logMessage(LogLevel.WARNING, e.getMessage(), e);
				throw new StepFailedException(e);
			}
		}
	}
	
}
