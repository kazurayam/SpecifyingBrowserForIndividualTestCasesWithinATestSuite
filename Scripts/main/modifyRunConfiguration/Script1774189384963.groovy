import com.kazurayam.ks.RunConfigurationModifier
import com.kazurayam.ks.WebDriverPathHelper
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.webui.constants.StringConstants
import com.kms.katalon.core.webui.driver.DriverFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

RunConfigurationModifier.apply()
RunConfiguration.injectWebDriverPath("${DriverFactory.CHROME_DRIVER_PATH_PROPERTY}", "${WebDriverPathHelper.getChromeDriverPath().toString()}")
RunConfiguration.injectWebDriverPath("${StringConstants.CONF_PROPERTY_GECKO_DRIVER_PATH}", "${WebDriverPathHelper.getFirefoxDriverPath().toString()}")
RunConfiguration.injectWebDriverPath("${DriverFactory.EDGE_CHROMIUM_DRIVER_PATH_PROPERTY}", "${WebDriverPathHelper.getEdgeChromiumDriverPath().toString()}")

String json = RunConfiguration.toJson()

Path buildDir = Paths.get(RunConfiguration.getProjectDir()).resolve('build')
Files.createDirectories(buildDir)
Path outFile = buildDir.resolve("execution.properties.json")
outFile.toFile().text = json

