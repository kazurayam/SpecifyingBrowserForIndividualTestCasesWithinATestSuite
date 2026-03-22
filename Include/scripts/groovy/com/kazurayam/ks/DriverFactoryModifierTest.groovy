package com.kazurayam.ks

import static org.junit.Assert.assertEquals

import java.util.stream.Collectors

import org.junit.Ignore
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
	
	@Ignore
	@Test
	public void test_apply() {
		String json1 = new GroovyMetaClassInspector().toJson(DriverFactory.metaClass)
		//
		RunConfigurationModifier.injectWebDriverPaths()
		DriverFactoryModifier.apply(WebUIDriverType.FIREFOX_DRIVER)
		//
		String json2 = new GroovyMetaClassInspector().toJson(DriverFactory.metaClass)
		verifyMethodsOfDriverFactory(json1, json2)
		//
		def browser = DriverFactory.getExecutedBrowser()
		println "browser: " + browser.getName()
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
		List<String> filtered = new StringReader(json2).readLines().stream()
			.filter({ line ->
				line.contains("getExecutedBrowser") &&
				line.contains("org.codehaus.groovy.runtime.metaclass.ClosureStaticMetaMethod") &&
				line.contains("[name: getExecutedBrowser params: [] returns: class java.lang.Object owner: class com.kms.katalon.core.webui.driver.DriverFactory]")
			})
			.collect(Collectors.toList())
		assertEquals(1, filtered.size())
	}
	
}
