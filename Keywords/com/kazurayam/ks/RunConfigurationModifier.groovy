package com.kazurayam.ks

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.webui.constants.StringConstants
import com.kms.katalon.core.webui.driver.DriverFactory

public class RunConfigurationModifier {
	
	
	@Keyword
	public static void apply() {
		String propertyConfigFileContent = """{
  "execution" : {
    ...
    "drivers" : {
      "system" : {
	    "WebUI" : {
          "broserType": "CHROME_DRIVER,
          "${DriverFactory.CHROME_DRIVER_PATH_PROPERTY}": "${WebDriverPathHelper.getChromeDriverPath().toString()}",
          "${StringConstants.CONF_PROPERTY_GECKO_DRIVER_PATH}": "${WebDriverPathHelper.getFirefoxDriverPath().toString()}",
          "${DriverFactory.EDGE_CHROMIUM_DRIVER_PATH_PROPERTY}": "${WebDriverPathHelper.getEdgeChromiumDriverPath().toString()}"
        }
      }
    }
  }
}
""";
		// The problem is that I want to inject the Web Driver Path information 
		// into the RunConfiguration's private storage "localExecutionSettingMapStorage" dynamically at Test Case's runtime. 
		// How can I do it?

		RunConfiguration.metaClass.'static'.injectWebDriverPath = { String key, String value ->
			Map<String, Object> execution = RunConfiguration.localExecutionSettingMapStorage.get("execution")
			assert execution != null
			Map<String, Object> drivers = execution.get("drivers")
			assert drivers != null
			Map<String, Object> system = drivers.get("system")
			assert system != null
			Map<String, Object> webui = system.get("WebUI")
			assert webui != null
			webui.put(key, value)
		}
	}
}
