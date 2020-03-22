package com.apicatalog.projection;

import com.apicatalog.projection.annotation.IFunction;

public class PropertyMapping {

	Class<?> objectClass;
	
	String propertyName;
	
	String qualifier;
	
	IFunction[] functions;

	boolean optional;

	public Class<?> getObjectClass() {
		return objectClass;
	}

	public void setObjectClass(Class<?> objectClass) {
		this.objectClass = objectClass;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	public IFunction[] getFunctions() {
		return functions;
	}

	public void setFunctions(IFunction[] functions) {
		this.functions = functions;
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}
}
