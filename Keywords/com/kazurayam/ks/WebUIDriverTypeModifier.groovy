package com.kazurayam.ks

import java.util.stream.Collectors

import com.kms.katalon.core.webui.driver.WebUIDriverType

public class WebUIDriverTypeModifier {

	public static void apply() {
		
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

		WebUIDriverType.metaClass.static.isDefinedDriverName = { driverName ->
			WebUIDriverType dt = WebUIDriverType.valueOfByDriverName(driverName)
			return (dt != null)
		}
		
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
