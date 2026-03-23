# \[Katalon Studio\] How to specify Browser type for individual Test Cases within a Test Suite

## Problem to solve

In Dec 2018 in the Katalon Community forum, there was a post by @Timo\_Kuisma:

<https://forum.katalon.com/t/specify-profiles-for-individual-test-cases-within-a-test-suite/15747>

The original poster wrote that he wants to run several test cases in one Test Suite with different "profile" files.

Katalon Studio does not support this. You can select only one Profile for all Test Cases in a Test Suite run. The following screenshot shows, once you choose the ProfileX, then the Test Case `main/TC1` will run with the `ProfileX` 3 times. It is boring.

![TS1 with ProfileX](https://kazurayam.github.io/SpecifyingBrowserForIndividualTestCasesWithinATestSuite/images/TS1_with_ProfileX.png)

As for @Timo\_Kuisma’s requirement, I have already developed a solution. See the following GitHub repository:

[ExecutionProfileLoader](https://github.com/kazurayam/ExecutionProfilesLoader)

This library enables my Test Case script to load any Execution Profile programatically runtime.

Issue resolved? --- the half resolved, still remains another half.

**I want to run several test cases in one Test Suite with different browser type --- Firefox, Edge Chromium and Chrome.**

Katalon Studio does not support this. You can select only one browser type for all Test Cases in a Test Suite run. As the following screenshot shows, once you choose Firefox, then the Test Case `main/TC1` will run 3 times with Firefox browser. Very boring.

![TS1 with Firefox](https://kazurayam.github.io/SpecifyingBrowserForIndividualTestCasesWithinATestSuite/images/TS1_with_Firefox.png)

## Solution

I have developed a Test Suite

- `Test Suites/TS2`

The `TS2` contains the Test Case `main/TC1` 3 times, and execute the 1st with Firefox, execute the 2nd with Edge Chromium, and the 3rd with Chrome.

![TS2](https://kazurayam.github.io/SpecifyingBrowserForIndividualTestCasesWithinATestSuite/images/TS2.png)

You will find that a few more steps of Test Case are inserted in the above link:https://github.com/kazurayam/SpecifyingBrowserForIndividualTestCasesWithinATestSuite/Test Suites/main/TS2.ts\[`Test Suites/TS2`\].

- `Test Cases/main/modifyRunConfiguration`

- `Test Cases/main/runWith/Firefox`

- `Test Cases/main/runWith/EdgeChromium`

- `Test Cases/main/runWith/Chrome`

These steps implement my black magic of custom Groovy programming.

The `TS2` worked fine. When I run it, I saw 3 different browsers (Firefox, Edge Chromium, Chrome) opened and closed.

## Description

Please read the source codes and the inline comments.

### Test Cases/main/modifyRunConfiguration/

    import java.nio.file.Files
    import java.nio.file.Path
    import java.nio.file.Paths

    import com.kazurayam.ks.RunConfigurationModifier
    import com.kazurayam.ks.WebDriverPathHelper
    import com.kms.katalon.core.configuration.RunConfiguration
    import com.kms.katalon.core.webui.constants.StringConstants
    import com.kms.katalon.core.webui.driver.DriverFactory

    // add a few methods into the RunConfiguraiton object dynamically
    RunConfigurationModifier.apply()

    // inject the path information of ChromeDriver, FirefoxDriver and EdgeChromiumDriver for WebUI testing into the RunConfiguration
    RunConfigurationModifier.injectWebDriverPaths()

    // serialize the modified RunConfigurtion into JSON
    String json = RunConfiguration.toJson()

    // write the JSON into a file for view
    Path buildDir = Paths.get(RunConfiguration.getProjectDir()).resolve('build')
    Files.createDirectories(buildDir)
    Path outFile = buildDir.resolve("execution.properties.json")
    outFile.toFile().text = json

### Test Cases/main/runWith/Firefox

    import com.kazurayam.ks.DriverFactoryModifier
    import com.kms.katalon.core.webui.driver.WebUIDriverType
    import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

    // modify the DriverFactory so that WebUI.openBrowser() opens Chrome browser
    DriverFactoryModifier.apply(WebUIDriverType.CHROME_DRIVER)

    WebUI.comment("DriverFactory is now configured to open  " + WebUIDriverType.CHROME_DRIVER)

The "runWith" script for Edge Chromium and Chrome look similar.

### Custom Groovy classes

#### Keywords/com/kazurayam/ks/DriverFactoryModifier.groovy

    package com.kazurayam.ks

    import org.apache.commons.lang3.StringUtils;
    import org.openqa.selenium.WebDriver

    import com.kms.katalon.core.annotation.Keyword
    import com.kms.katalon.core.configuration.RunConfiguration;
    import com.kms.katalon.core.driver.IDriverType;
    import com.kms.katalon.core.exception.StepFailedException;
    import com.kms.katalon.core.logging.LogLevel;
    import com.kms.katalon.core.webui.driver.DriverFactory;
    import com.kms.katalon.core.webui.driver.WebMobileDriverFactory;
    import com.kms.katalon.core.webui.driver.WebUIDriverType

    /**
     * Modify the `com.kms.katalon.core.webui.driver.DriverFactory` class 
     * dynamically using Groovy's Meta-programming technique.
     */
    public class DriverFactoryModifier {
        
        /**
         * Will modify DriverFactory.openWebDriver() method dynamically so that it opens a browser 
         * of the driver type specified.
         * 
         * @param driverName one of "Chrome", "Chrome (headless)", "Firefox", "Firefox (headless)", "Edge Chromium"
         */
        @Keyword
        public static void apply(String driverName) {
            Objects.requireNonNull(driverName)
            WebUIDriverTypeModifier.apply()
            if (WebUIDriverType.isDefinedDriverName(driverName)) {
                WebUIDriverType driverType = WebUIDriverType.valueOfByDriverName(driverName)
                apply(driverType)
            } else {
                throw new IllegalArgumentException(driverName + " is not a valid WebUIDriverType")
            }
        }

        /**
         * Will modify DriverFactory.openWebDriver() method dynamically so that it opens a browser 
         * of the driver type specified.
         * 
         * @param driverType 
         */
        public static void apply(WebUIDriverType driverType) {
            Objects.requireNonNull(driverType)
            //
            DriverFactory.metaClass.'static'.openWebDriver = { ->
                /* the following closure code is just the same as the original. No difference. 
                 * However I needed to create this meta method.
                 * Why? 
                 * I want to let the DriverFactory#openWebDriver() to call the meta method 
                 * getExecutedBrowser() defined bellow. In order to do that, I needed to make 
                 * this meta method openWebDriver(). Unless I create the meta method openWebDriver(),
                 * then the normal openWebDriver() will never call the meta method getExecutedBrowser().
                 * I found this behavior by experiment. No document explains this. 
                 * It is very strange. But this is given. I just accept it.
                 */
                try {
                    WebDriver webDriver;
                    if (DriverFactory.isUsingExistingDriver()) {
                        webDriver = DriverFactory.startExistingBrowser();
                    } else {
                        String remoteWebDriverUrl = DriverFactory.getRemoteWebDriverServerUrl();
                        if (StringUtils.isNotEmpty(remoteWebDriverUrl)) {
                            webDriver = DriverFactory.startRemoteBrowser();
                        } else {
                            webDriver = DriverFactory.startNewBrowser(DriverFactory.getExecutedBrowser());
                        }
        
                        DriverFactory.saveWebDriverSessionData(webDriver);
                        DriverFactory.changeWebDriver(webDriver);
                    }
        
                    return webDriver;
                } catch (Error e) {
                    DriverFactory.logger.logMessage(LogLevel.WARNING, e.getMessage(), e);
                    throw new StepFailedException(e);
                }
            }

            //
            DriverFactory.metaClass.'static'.getExecutedBrowser = { ->
                IDriverType webDriverType = null;
                if (DriverFactory.isUsingExistingDriver()) {
                    webDriverType = WebUIDriverType.fromStringValue(RunConfiguration.getExistingSessionDriverType());
                }
        
                if (webDriverType != null) {
                    return webDriverType;
                }
        
                String remoteWebDriverUrl = DriverFactory.getRemoteWebDriverServerUrl();
                
                String driverConnectorProperty = null;
                String driverTypeString = null
                if (StringUtils.isNotBlank(remoteWebDriverUrl)) {
                    driverConnectorProperty = RunConfiguration.REMOTE_DRIVER_PROPERTY  // Remote
                    driverTypeString = RunConfiguration.getDriverSystemProperty(driverConnectorProperty, DriverFactory.EXECUTED_BROWSER_PROPERTY)
                } else {
                    driverConnectorProperty = DriverFactory.WEB_UI_DRIVER_PROPERTY  // WebUI
                    /* 
                     * Here is a great hack!
                     * Will force `DriverFactory.openWebDriver()` to disregard the browser type selected in the GUI.
                     * Will tell `DriverFactory.openWebDriver()` to open the browser type specified by `DriverFactoryModifier.apply(driverType)`.
                     */
                    //driverTypeString = RunConfiguration.getDriverSystemProperty(driverConnectorProperty, DriverFactory.EXECUTED_BROWSER_PROPERTY)
                    driverTypeString = driverType.name()
                }
                if (driverTypeString != null) {
                    webDriverType = WebUIDriverType.valueOf(driverTypeString);
                }
        
                if (webDriverType == null && RunConfiguration.getDriverSystemProperty(DriverFactory.MOBILE_DRIVER_PROPERTY,
                        WebMobileDriverFactory.EXECUTED_MOBILE_PLATFORM) != null) {
                    webDriverType = WebUIDriverType.valueOf(RunConfiguration.getDriverSystemProperty(DriverFactory.MOBILE_DRIVER_PROPERTY,
                            WebMobileDriverFactory.EXECUTED_MOBILE_PLATFORM));
                }
        
                return webDriverType;
            }
        }
    }

#### Keywords/com/kazurayam/ks/GroovyMetaClassInspector.groovy

    package com.kazurayam.ks

    import groovy.json.JsonOutput;
    import java.util.stream.Collectors;

    /**
     * Groovy language provides "MetaClass" of all Java or Groovy classes accessible runtime,
     * which gives us the chances to modify the method implementation dynamically.
     * 
     * This GroovyMetaClassInspector provides a helper method that supports debugging "MetaClass".
     */
    public class GroovyMetaClassInspector {
        
        private int methodNameColumnWidth = 36;
        
        private MetaClass targetMetaClass = null;
        
        public GroovyMetaClassInspector() {}
        
        public void setMethodNameColumnWidth(int width) {
            this.methodNameColumnWidth = width
        }
        
        /**
         * returns a JSON string which includes all the methods implemented in the metaClass; 
         * both genuine ones and the dynamically added ones. 
         * The JSON output makes the list of methods visible.
         * Useful for debugging the Metaprogramming.
         *  
         * @param metaClass
         * @return a JSON representation of the methods of the metaClass
         */
        public String toJson(MetaClass metaClass) {
            Objects.requireNonNull(metaClass)
            this.targetMetaClass = metaClass
            //
            List<Map> methodMaps = metaClass.getMethods()
                .stream()
                .map({ MetaMethod it ->
                    return toSignature(it)
                })
                .sorted()
                .collect(Collectors.toList())
            assert methodMaps.size() > 0
            //
            List<Map> metaMethodMaps = metaClass.getMetaMethods()
                .stream()
                .map({ MetaMethod it -> 
                    return toSignature(it)
                })
                .sorted()
                .collect(Collectors.toList())
            assert metaMethodMaps.size() > 0
            //
            Map<String, Object> mp = new HashMap<>()
            //mp.put("metaMethods", metaMethodMaps);
            mp.put("methods", methodMaps);
            return JsonOutput.prettyPrint(JsonOutput.toJson(mp))
        }
        
        /**
         * Given the name of method "openWebDriver", then will return a string like:
         * 
         * "openWebDriver                       public static org.openqa.selenium.WebDriver com.kms.katalon.core.webui.driver.DriverFactory.openWebDriver() throws java.lang.Exception",
         * 
         * This string is good for sorting the lines by the method name alphabetically
         * 
         * @param metaMethod 
         */
        public String toSignature(MetaMethod metaMethod) {
            StringBuilder sb = new StringBuilder()
            sb.append(metaMethod.getName())
            sb.append(fillWhitespace(metaMethod.getName()))
            sb.append(metaMethod.toString())
            return sb.toString()
        }
        
        /**
         * Given item is "123456789012345678901234567890" and the methodNameColumnWidth is set 36, 
         * then will return a string of "123456789012345678901234567890      " appended 6 whitespace as filler.            
         * @param item
         * @return a string appended a sequence of whitespace
         */
        public String fillWhitespace(String item) {
            StringBuilder sb = new StringBuilder()
            methodNameColumnWidth.times { sb.append(' ') }
            int filler = methodNameColumnWidth - item.length()
            if (filler > 0) {
                return sb.toString().substring(0, methodNameColumnWidth - item.length())
            } else {
                return ''
            }
        }
    }

#### Keywords/com/kazurayam/ks/RunConfigurationModifier.groovy

    package com.kazurayam.ks

    import com.fasterxml.jackson.databind.ObjectMapper
    import com.fasterxml.jackson.databind.SerializationFeature
    import com.kms.katalon.core.annotation.Keyword
    import com.kms.katalon.core.configuration.RunConfiguration
    import com.kms.katalon.core.webui.constants.StringConstants
    import com.kms.katalon.core.webui.driver.DriverFactory

    /**
     * This class modifies the RunConfiguration class using Groovy's Metaprogramming techique.
     */
    public class RunConfigurationModifier {
        
        public static void apply() {
            String wanted = """{
      "execution" : {
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
            /*
             * Katalon Studio generates a 'RunConfiguration' instance with the following information contained for example:
             * ```
             * { 
             *   "execution" : {
             *     "drivers" : {
             *       "system" : {
             *         "WebUI" : {
             *           "broserType": "CHROME_DRIVER,
             *           "chromeDriverPath" : "/Applications/Katalon Studio.app/Contents/Eclipse/configuration/resources/drivers/chromedriver_mac/chromedriver",
             *         }
             *       }
             *     }
             * ```
             * 
             * I found a problem here. Katalon Studio generates a `RunConfiguration` object that contains only one driver path; 
             * the path of the WebDriver that I selected in the GUI to run the test with. 
             * If I chose Chrome in the GUI, then the 'RunConfiguration' object does not contain the path information of 
             * FireFoxDriver and EdgeChromiumDriver. This is a shortage for me (kazurayam).
             * 
             * The `RunConfiguration.injectWebDriverPath(String key, String value)` method injects the path information 
             * of WebDriver. A Test Case script can get the WebDriver's path information 
             * using the `com.kazurayam.ks.WebDriverPathHelper` class.
             */
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
            
            /*
             * The `RunConfiguration.toJson()` returns a JSON string which serialize all of the settings information 
             * of a project passed from Katalon Studio to user's test scripts. In the output JSON, the keys are sorted
             * in alphabetical ascending order.
             */
            RunConfiguration.metaClass.'static'.toJson = { ->
                ObjectMapper om = new ObjectMapper()
                om.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
                om.configure(SerializationFeature.INDENT_OUTPUT, true)
                String json = om.writeValueAsString(RunConfiguration.localExecutionSettingMapStorage)
                return json
            }
        }

        /**
         * inject the path information of the ChromeDriver binary, the FirefoxDriver binary and the EdgeChromiumDriver binary.
         * This must be called after `RunConfigurationModifier.apply()` has been called.
         * 
         */
        @Keyword
        public static void injectWebDriverPaths() {
            RunConfigurationModifier.apply()  // to make sure that the injectWebDriverPath(String,String) method has been added
            RunConfiguration.injectWebDriverPath("${DriverFactory.CHROME_DRIVER_PATH_PROPERTY}", "${WebDriverPathHelper.getChromeDriverPath().toString()}")
            RunConfiguration.injectWebDriverPath("${StringConstants.CONF_PROPERTY_GECKO_DRIVER_PATH}", "${WebDriverPathHelper.getFirefoxDriverPath().toString()}")
            RunConfiguration.injectWebDriverPath("${DriverFactory.EDGE_CHROMIUM_DRIVER_PATH_PROPERTY}", "${WebDriverPathHelper.getEdgeChromiumDriverPath().toString()}")
        }
    }

#### Keywords/com/kazurayam/ks/WebDriverPathHelper.groovy

    package com.kazurayam.ks

    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.security.CodeSource;

    import com.kms.katalon.core.webui.driver.DriverFactory;
    import com.kms.katalon.core.webui.util.OSUtil;

    /**
     * This class enables you to find out the path of WebDriver binaries bundled in the current Katalon Studio installation.
     * 
     */
    public class WebDriverPathHelper {

        /**
         * Only Mac is supported. Still TODO for Windows and Linux.
         * 
         * @return the path of ChromeDriver binary in the current Katalon Studio installation
         */
        public static Path getChromeDriverPath() {
            if (OSUtil.isMac()) {
                // /Applications/Katalon Studio.app/Contents/Eclipse/configuration/resources/drivers/chromedriver_mac
                Path installationDir = getInstallationDir()
                return installationDir.resolve("Contents/Eclipse/configuration/resources/drivers/chromedriver_mac/chromedriver")
            } else {
                throw new UnsupportedOperationException("TODO")
            }
        }
        
        /**
         * Only Mac is supported. Still TODO for Windows and Linux.
         * 
         * @return the path of FirefoxDriver binary in the current Katalon Studio installation
         */
        public static Path getFirefoxDriverPath() {
            if (OSUtil.isMac()) {
                // firefox_mac/geckodriver
                Path installationDir = getInstallationDir()
                return installationDir.resolve("Contents/Eclipse/configuration/resources/drivers/firefox_mac/geckodriver")
            } else {
                throw new UnsupportedOperationException("TODO")
            }
        }
        
        /**
         * Only Mac is supported. Still TODO for Windows and Linux.
         * 
         * @return the path of EdgeChromiumDriver binary in the current Katalon Studio installation
         */
        public static Path getEdgeChromiumDriverPath() {
            if (OSUtil.isMac()) {
                // edgechromium_mac/msedgedriver
                Path installationDir = getInstallationDir()
                return installationDir.resolve("Contents/Eclipse/configuration/resources/drivers/edgechromium_mac/msedgedriver")
            } else {
                throw new UnsupportedOperationException("TODO")
            }
        }
        
        /**
         * 
         * @param clazz
         * @return the path of the jar file which contains the class specified
         */
        private static Path getCodeSourcePathOf(Class<?> clazz) {
            CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
            URL url = codeSource.getLocation();
            try {
                return Paths.get(url.toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        
        /**
         * 
         * @return the path of the directory where Katalon Studio is installed.
         *     e.g. on Mac, usually "/Applications/Katalon Studio.app"
         */
        private static Path getInstallationDir() {
            Path webuiJar = getCodeSourcePathOf(DriverFactory.class)   // /Applications/Katalon Studio.app/Contents/Eclipse/plugins/com.kms.katalon.core.webui_1.0.0.202602260431.jar
            Path pluginsDir = webuiJar.getParent()
            Path eclipseDir = pluginsDir.getParent()
            Path contentsDir = eclipseDir.getParent()
            return contentsDir.getParent()            // /Applications/Katalon Studio.app
        }
        
    }

#### Keywords/com/kazurayam/ks/WebUIDriverTypeModifier.groovy

    package com.kazurayam.ks

    import java.util.stream.Collectors

    import com.kms.katalon.core.webui.driver.WebUIDriverType

    /**
     * Modifies `com.kms.katalon.core.webui.driver.WebUIDriverType` class using Groovy's Metaprogramming technique.
     */
    public class WebUIDriverTypeModifier {

        public static void apply() {
            /*
             * Given 'Chrome' as dirverName, returns WebUIDriverType.CHROME_DRIVER
             */
            WebUIDriverType.metaClass.static.valueOfByDriverName = { driverName ->
                List<WebUIDriverType> list = WebUIDriverType.values() as List
                List<WebUIDriverType> filtered = list.stream()
                        .filter({ dt ->
                            dt.getDriverName().equals(driverName)
                        })
                        .collect(Collectors.toList())
                if (filtered.size() > 0) {
                    return filtered.get(0)
                } else {
                    return null
                }
            }
            /*
             * Given 'Chrome" as dirverName, returns true.
             * Gievn 'undefined' as the driverName, returns false
             */
            WebUIDriverType.metaClass.static.isDefinedDriverName = { driverName ->
                WebUIDriverType dt = WebUIDriverType.valueOfByDriverName(driverName)
                return (dt != null)
            }
            /*
             * returns the list of driver names defined.
             * 
             * @returns `[Android, Chrome, Chrome (headless), Edge, Edge Chromium, Firefox, Firefox (headless), IE, Kobiton Device, Remote, Remote Chrome, Remote Firefox, Safari, TestCloud, iOS]`
             */
            WebUIDriverType.metaClass.static.getDriverNames = { ->
                List<WebUIDriverType> list = WebUIDriverType.values() as List
                List<String> sorted = list.stream()
                    .map({ dt -> dt.getDriverName() })
                    .sorted()
                    .collect(Collectors.toList())
                return sorted
            }
        }
    }

#### Keywords/com/kms/katalon/core/webui/keyword/builtin/OpenBrowserKeyword.groovy

    package com.kms.katalon.core.webui.keyword.builtin

    import java.text.MessageFormat

    import com.kms.katalon.core.annotation.internal.Action
    import com.kms.katalon.core.configuration.RunConfiguration
    import com.kms.katalon.core.event.EventBusSingleton
    import com.kms.katalon.core.event.TestingEvent
    import com.kms.katalon.core.event.TestingEventType
    import com.kms.katalon.core.exception.StepFailedException
    import com.kms.katalon.core.keyword.internal.SupportLevel
    import com.kms.katalon.core.model.FailureHandling
    import com.kms.katalon.core.model.TakeScreenshotOption
    import com.kms.katalon.core.trace.TraceDebug
    import com.kms.katalon.core.trace.TraceHolder
    import com.kms.katalon.core.util.internal.PathUtil
    import com.kms.katalon.core.webui.constants.StringConstants
    import com.kms.katalon.core.webui.driver.DriverFactory
    import com.kms.katalon.core.webui.keyword.internal.WebUIAbstractKeyword
    import com.kms.katalon.core.webui.keyword.internal.WebUIKeywordMain
    import com.kms.katalon.core.webui.trace.HarTracer
    import com.kms.katalon.core.webui.trace.TraceSession

    /**
     * Almost identical to the Katalon's original except that "@CompileStatic" annotations are commented out.
     * This code was necessary to be in the Keywords folder so that a call to the `WebUI.openBrowser('')` keyword is linked with 
     * the DriverFactory class modified by `DriverFactoryModifier.apply()`.
     * If this code is not there, then a call to `WebUI.openBrowser()` will be linked to the Katalon's original `DriverFactory` implementation.
     */

    @Action(value = "openBrowser")
    public class OpenBrowserKeyword extends WebUIAbstractKeyword {

        //@CompileStatic
        @Override
        public SupportLevel getSupportLevel(Object ...params) {
            return super.getSupportLevel(params)
        }

        //@CompileStatic
        @Override
        public Object execute(Object ...params) {
            String rawUrl = (String) params[0]
            FailureHandling flowControl = (FailureHandling)(params.length > 1 && params[1] instanceof FailureHandling ? params[1] : RunConfiguration.getDefaultFailureHandling())
            openBrowser(rawUrl,flowControl)
        }

        //@CompileStatic
        public void openBrowser(String rawUrl, FailureHandling flowControl) throws StepFailedException {
            WebUIKeywordMain.runKeyword({
                logger.logDebug(StringConstants.KW_LOG_INFO_OPENING_BROWSER)
                DriverFactory.openWebDriver()
                if (rawUrl != null && !rawUrl.isEmpty()) {
                    try {
                        TraceSession session = (TraceSession) TraceHolder.session
                        if (session != null && HarTracer.attachIfNeeded(session)) {
                            HarTracer.ensureCacheDisabled()
                            TraceDebug.writeLine("har: attached before openBrowser navigation")
                        }
                    } catch (Throwable ignored) {
                    }
                }
                if (rawUrl != null && !rawUrl.isEmpty()) {
                    URL url = PathUtil.getUrl(rawUrl, "http")
                    logger.logDebug(MessageFormat.format(StringConstants.KW_LOG_INFO_NAVIGATING_BROWSER_TO, url.toString()))
                    DriverFactory.getWebDriver().get(url.toString())
                }
                
                TestingEvent browserOpenedEvent = new TestingEvent(TestingEventType.BROWSER_OPENED, webDriver);
                EventBusSingleton.getInstance().getEventBus().post(browserOpenedEvent);
                
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_BROWSER_IS_OPENED_W_URL, rawUrl))
            }, flowControl, TakeScreenshotOption.NONE, (rawUrl != null) ? MessageFormat.format(StringConstants.KW_MSG_UNABLE_TO_OPEN_BROWSER_W_URL, rawUrl) : StringConstants.KW_MSG_UNABLE_TO_OPEN_BROWSER)
        }
    }

## Conclusion

I don’t think the original idea (run Test Cases with different borwser in a Test Suite) is pragmatic. It is no use. It was technically interesting to develop this project. The `TS2` worked fine. So I am contented with this time of experiment.

I would stop my hands on this project for now.

A restricting faster for me is that the `com.kms.katalon.core.webui.keyword.builtin.OpenBrowserKeyord` class is coded with `@CompileStatic` annotations. This makes my works very dirty. I do not like to publish this project because of this restriction.
