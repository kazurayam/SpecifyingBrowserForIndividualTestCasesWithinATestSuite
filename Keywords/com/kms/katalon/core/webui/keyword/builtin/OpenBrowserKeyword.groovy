package com.kms.katalon.core.webui.keyword.builtin

import java.text.MessageFormat

import org.openqa.selenium.WebDriver
import com.kms.katalon.core.annotation.internal.Action
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.event.EventBusSingleton
import com.kms.katalon.core.event.TestingEvent
import com.kms.katalon.core.event.TestingEventType
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.keyword.internal.SupportLevel
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.model.TakeScreenshotOption
import com.kms.katalon.core.util.internal.PathUtil
import com.kms.katalon.core.webui.constants.StringConstants
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.internal.WebUIAbstractKeyword
import com.kms.katalon.core.webui.keyword.internal.WebUIKeywordMain
import com.kms.katalon.core.trace.TraceDebug
import com.kms.katalon.core.trace.TraceHolder
import com.kms.katalon.core.webui.trace.HarTracer
import com.kms.katalon.core.webui.trace.TraceSession

import groovy.transform.CompileStatic

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
