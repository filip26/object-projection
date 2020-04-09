package com.apicatalog.projection.converter;

import com.apicatalog.projection.objects.ObjectType;

public interface ConverterMapping {

	Converter<Object, Object> getConversion();

	ObjectType getSourceType();
	
	ObjectType getTargetType();
}
