import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.driver.DriverFactory

WebUI.openBrowser('')
WebUI.setViewPortSize(800, 600)
WebUI.navigateToUrl('https://example.com/')
WebUI.delay(1)
WebUI.closeBrowser()