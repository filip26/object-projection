package com.apicatalog.projection.objects;

public class Reference {
	
	public BasicTypes objectA;

	public String stringValue;

	public BasicTypes getObjectA() {
		return objectA;
	}

	public void setObjectA(BasicTypes objectA) {
		this.objectA = objectA;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	@Override
	public String toString() {
		return "Reference [objectA=" + objectA + ", stringValue=" + stringValue + "]";
	}
}
