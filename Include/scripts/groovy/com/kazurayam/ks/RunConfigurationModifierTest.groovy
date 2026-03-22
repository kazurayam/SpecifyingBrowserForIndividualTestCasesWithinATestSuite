package com.kazurayam.ks

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.webui.driver.chrome.ChromeDriverUtil
import com.kms.katalon.core.webui.driver.edge.EdgeDriverUtil
import com.kms.katalon.core.webui.driver.firefox.FirefoxDriverUtil

@RunWith(JUnit4.class)
public class RunConfigurationModifierTest {
	
	@Test
	public void test_injectWebDriverPaths() {
		Path buildDir = Paths.get(RunConfiguration.getProjectDir()).resolve("build")
		Files.createDirectories(buildDir)
		Path temp = buildDir.resolve("_execution.properties")
		RunConfigurationModifier.injectWebDriverPaths(temp)
		println temp.toFile().text
		//
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
	}

}
