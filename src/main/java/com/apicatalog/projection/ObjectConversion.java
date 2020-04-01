package com.apicatalog.projection;

public interface ObjectConversion {

	Class<?> getSourceClass();

	Object forward(Object object);

	Object backward(Object object);
	
}
