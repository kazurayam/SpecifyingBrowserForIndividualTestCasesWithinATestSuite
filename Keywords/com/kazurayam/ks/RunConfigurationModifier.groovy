package com.kazurayam.ks

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.webui.constants.StringConstants
import com.kms.katalon.core.webui.driver.DriverFactory

public class RunConfigurationModifier {
	
	
	@Keyword
	public static void injectWebDriverPaths() {
		Path temp = Files.createTempFile("execution.properties-", "json")
		injectWebDriverPaths(temp)	
	}
	
	public static void injectWebDriverPaths(Path temp) {
		Objects.requireNonNull(temp)
		String propertyConfigFileContent = """{
  "execution" : {
    "drivers" : {
      "system" : {
	    "WebUI" : {
          "${DriverFactory.CHROME_DRIVER_PATH_PROPERTY}": "${WebDriverPathHelper.getChromeDriverPath().toString()}",
          "${StringConstants.CONF_PROPERTY_GECKO_DRIVER_PATH}": "${WebDriverPathHelper.getFirefoxDriverPath().toString()}",
          "${DriverFactory.EDGE_CHROMIUM_DRIVER_PATH_PROPERTY}": "${WebDriverPathHelper.getEdgeChromiumDriverPath().toString()}"
        }
      }
    }
  }
}
""";
		temp.toFile().text = propertyConfigFileContent;
		assert Files.exists(temp);
		//
		RunConfiguration.setExecutionSettingFile(temp.toString())
	}
}
