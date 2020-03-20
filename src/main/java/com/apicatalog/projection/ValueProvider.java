package com.apicatalog.projection;

import com.apicatalog.projection.fnc.InvertibleFunction;

public class ValueProvider {

	Class<?> sourceClass;
	String sourcePropertyName;
	
	String qualifier;
	
	String valueId;

	public Class<?> getSourceClass() {
		return sourceClass;
	}

	public String getQualifier() {
		return qualifier;
	}

	public String getSourcePropertyName() {
		return sourcePropertyName;
	}

	public String getValueId() {
		return valueId;
	}
	
	public void setSourceClass(Class<?> sourceClass) {
		this.sourceClass = sourceClass;
	}

	public void setSourcePropertyName(String sourcePropertyName) {
		this.sourcePropertyName = sourcePropertyName;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	public void setValueId(String valueId) {
		this.valueId = valueId;
	}

	public InvertibleFunction[] getFunctions() {
		// TODO Auto-generated method stub
		return null;
	}
}
