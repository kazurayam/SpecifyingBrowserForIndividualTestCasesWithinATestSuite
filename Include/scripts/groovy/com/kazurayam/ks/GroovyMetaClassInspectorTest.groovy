package com.kazurayam.ks

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.driver.WebUIDriverType

@RunWith(JUnit4.class)
public class GroovyMetaClassInspectorTest {

	@Test
	public void testImplementation() {
		DriverFactoryModifier.runWith(WebUIDriverType.FIREFOX_DRIVER)
		GroovyMetaClassInspector.inspect(DriverFactory.metaClass)
	}
	
}
