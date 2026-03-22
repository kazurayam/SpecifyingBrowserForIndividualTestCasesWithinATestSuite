package com.kazurayam.ks

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.network.ProxyInformation
import com.kms.katalon.core.webui.constants.StringConstants
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.driver.chrome.ChromeDriverUtil
import com.kms.katalon.core.webui.driver.edge.EdgeDriverUtil
import com.kms.katalon.core.webui.driver.firefox.FirefoxDriverUtil

@RunWith(JUnit4.class)
public class RunConfigurationModifierTest {
	
	@BeforeClass
	public static void beforeClass() {
		RunConfigurationModifier.apply()
		RunConfiguration.injectWebDriverPath("${DriverFactory.CHROME_DRIVER_PATH_PROPERTY}", "${WebDriverPathHelper.getChromeDriverPath().toString()}")
		RunConfiguration.injectWebDriverPath("${StringConstants.CONF_PROPERTY_GECKO_DRIVER_PATH}", "${WebDriverPathHelper.getFirefoxDriverPath().toString()}")
		RunConfiguration.injectWebDriverPath("${DriverFactory.EDGE_CHROMIUM_DRIVER_PATH_PROPERTY}", "${WebDriverPathHelper.getEdgeChromiumDriverPath().toString()}")
	}
	
	@Test
	public void test_injectWebDriverPaths() {
		String chromeDriverPath = ChromeDriverUtil.getChromeDriverPath()
		//println chromeDriverPath
		assertNotNull("ChromeDriverUtil.getChromeDriverPath() returned null", chromeDriverPath)
		assertTrue("chromeDriverPath string is empty", chromeDriverPath.length() > 0)
		Path chromeDriver = Paths.get(chromeDriverPath)
		assertTrue(chromeDriver.toString() + " does not exist", Files.exists(chromeDriver))
		//
		String firefoxDriverPath = FirefoxDriverUtil.getDriverPath()
		assertNotNull("FirefoxDriverUtil.getDriverPath() returned null", firefoxDriverPath)
		Path firefoxDriver = Paths.get(firefoxDriverPath)
		assertTrue(firefoxDriver.toString() + " does not exist", Files.exists(firefoxDriver))
		//
		String edgeDriverPath = EdgeDriverUtil.getEdgeChromiumDriverPath()
		assertNotNull("EdgeDriverUtil.getEdgeChromiumDriverPath() returned null", edgeDriverPath)
		Path edgeDriver = Paths.get(edgeDriverPath)
		assertTrue(edgeDriver.toString() + " does not exist", Files.exists(edgeDriver))
		//
	}
	
	@Test
	public void test_getExecutionProperites_after_injection() {
		Map<String, Object> executionProperties = RunConfiguration.getExecutionProperties()
		assertNotNull("executionProperites is null", executionProperties)
		println "executionProperties: " + executionProperties
	}
	
	@Test
	public void test_getExecutionGeneralProperties_after_injection() {
		Map<String, Object> generalProperties = RunConfiguration.getExecutionGeneralProperties()
		println "generalProperties: " + generalProperties.toString()
		assertTrue("generalProperties does not contain the key proxy", generalProperties.containsKey(RunConfiguration.PROXY_PROPERTY))
	}
	
	@Test
	public void test_getProxyInformation_after_injection() {
		ProxyInformation proxy = RunConfiguration.getProxyInformation()
		assertNotNull("proxy is null", proxy)
	}

}
