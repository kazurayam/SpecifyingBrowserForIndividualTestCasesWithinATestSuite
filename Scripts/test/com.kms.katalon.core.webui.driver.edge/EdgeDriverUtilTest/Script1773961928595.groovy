import com.kms.katalon.core.webui.driver.edge.EdgeDriverUtil
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

def path = EdgeDriverUtil.getEdgeChromiumDriverPath()
WebUI.comment("path: " + path)
println "path: " + path
