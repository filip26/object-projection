package com.apicatalog.projection.mapping;

import com.apicatalog.projection.annotation.IFunction;

public class SourceMapping {

	Class<?> objectClass;
	
	String propertyName;
	
	String qualifier;
	
	Boolean optional;
	
	IFunction[] functions;

	public Class<?> getSourceClass() {
		return objectClass;
	}

	public void setSourceClass(Class<?> objectClass) {
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
		return optional != null && optional;
	}
	
	public void setOptional(Boolean optional) {
		this.optional = optional;
	}
}
