package com.kazurayam.ks

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.driver.WebUIDriverType
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

@RunWith(JUnit4.class)
public class DriverFactoryModifierTest {
	
	@Test
	public void test_runWith() {
		String json1 = new GroovyMetaClassInspector().toJson(DriverFactory.metaClass)
		//
		DriverFactoryModifier.runWith(WebUIDriverType.FIREFOX_DRIVER)
		//
		String json2 = new GroovyMetaClassInspector().toJson(DriverFactory.metaClass)
		verifyMethodsOfDriverFactory(json1, json2)
		//
		WebUI.openBrowser('http://example.com/')
		WebUI.verifyElementPresent(makeTestObject("to1", "//h1[contains(., 'Example Domain')]"), 10)
		WebUI.closeBrowser()
	}

	private TestObject makeTestObject(String id, String xpath) {
		TestObject tObj = new TestObject(id)
		tObj.addProperty("xpath", ConditionType.EQUALS, xpath)
		return tObj
	}
	
	private void verifyMethodsOfDriverFactory(String json1, String json2) {
		println "BEFORE:\n" + json1;
		println "AFTER:\n" + json2;
		assertTrue(json2.contains("openWebDriver") &&
				json2.contains("org.codehaus.groovy.runtime.metaclass.ClosureStaticMetaMethod") &&
				json2.contains("[name: openWebDriver params: [] returns: class java.lang.Object owner: class com.kms.katalon.core.webui.driver.DriverFactory]")
				)
	}
}
