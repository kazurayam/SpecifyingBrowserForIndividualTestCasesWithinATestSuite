package com.kazurayam.ks


import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

import java.nio.file.Files
import java.nio.file.Path

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.util.OSUtil

@RunWith(JUnit4.class)
public class WebDriverPathHelperTest {

	@Test
	public void test_getCodeSourcePathOf() {
		Path jarPath = WebDriverPathHelper.getCodeSourcePathOf(DriverFactory.class)
		assertTrue(jarPath.toString() + " does not exit", Files.exists(jarPath))
	}
	
	@Test
	public void test_getInstallationDir() {
		Path installationDir = WebDriverPathHelper.getInstallationDir()
		assertTrue(installationDir.toString() + " does not exit", Files.exists(installationDir))
		if (OSUtil.isMac()) {
			assertEquals("/Applications/Katalon Studio.app", installationDir.toString())
		}
	}
	
	@Test
	public void test_getChromeDriverPath() {
		Path chromeDriverPath = WebDriverPathHelper.getChromeDriverPath()
		assertTrue(chromeDriverPath.toString() + " does not exist", Files.exists(chromeDriverPath))
	}
	
	@Test
	public void test_getFirefoxDriverPath() {
		Path firefoxDriverPath = WebDriverPathHelper.getChromeDriverPath()
		assertTrue(firefoxDriverPath.toString() + " does not exist", Files.exists(firefoxDriverPath))
	}

}
