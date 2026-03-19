import com.kazurayam.ks.DriverFactoryModifier
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.driver.WebUIDriverType
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

DriverFactoryModifier.runWith(WebUIDriverType.CHROME_DRIVER)

WebUI.comment("DriverFactory is now configured to open  " + WebUIDriverType.CHROME_DRIVER)
