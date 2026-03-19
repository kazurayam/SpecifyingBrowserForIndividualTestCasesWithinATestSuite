package com.kazurayam.ks

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNull
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertFalse

import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import com.kms.katalon.core.webui.driver.WebUIDriverType

@RunWith(JUnit4.class)
public class WebUIDriverTypeModifierTest {
	
	@BeforeClass
	public static void beforeClass() {
		WebUIDriverTypeModifier.apply()
	}
	
	@Test
	public void test_isDefinedDriverName_Chrome() {
		assertTrue(WebUIDriverType.isDefinedDriverName("Chrome"))
	}
	
	@Test
	public void test_isDefinedDriverName_Firefox() {
		assertTrue(WebUIDriverType.isDefinedDriverName("Firefox"))
	}
	
	@Test
	public void test_isDefinedDriverName_undefined() {
		assertFalse(WebUIDriverType.isDefinedDriverName("undefined"))
	}
	
	@Test
	public void test_valueOfByDriverName_ChromeHeadless() {
		assertNotNull(WebUIDriverType.valueOfByDriverName("Chrome (headless)"))
	}
	
	@Test
	public void test_valueOfByDriverName_undefined() {
		assertNull(WebUIDriverType.valueOfByDriverName("undefined"))
	}
	
	@Test
	public void test_getDriverNames() {
		List<String> driverNames = WebUIDriverType.getDriverNames()
		println driverNames
		assertTrue(driverNames.contains("Chrome"))
		assertTrue(driverNames.contains("Chrome (headless)"))
		assertTrue(driverNames.contains("Firefox"))
		assertTrue(driverNames.contains("Firefox (headless)"))
		assertTrue(driverNames.contains("Edge Chromium"))
	}
}
