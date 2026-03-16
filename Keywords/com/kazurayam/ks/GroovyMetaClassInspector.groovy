package com.kazurayam.ks

public class GroovyMetaClassInspector {

	/**
	 *
	 * @param metaClass
	 */
	public static void inspect(MetaClass metaClass) {
		List<MetaMethod> metaMethods = metaClass.getMetaMethods()
		println "metaMethods.size(): " + metaMethods.size()
		List<String> metaMethodNames = []
		metaMethods.each { it ->
			metaMethodNames.add(it.getName())
		}
		metaMethodNames.sort().each { it ->
			println it
		}
		
		List<MetaMethod> methods = metaClass.getMethods()
		println "methods.size(): " + methods.size()
		List<String> methodNames = []
		methods.each { it ->
			methodNames.add(it.getName())
		}
		methodNames.sort().each { it ->
			println it
		}
		
		List<MetaProperty> properties = metaClass.getProperties()
		println "properties.size(): " + properties.size()
	}

}
