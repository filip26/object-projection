package com.apicatalog.projection.conversion.implicit;

import com.apicatalog.projection.adapter.type.TypeAdapter;
import com.apicatalog.projection.adapter.type.TypeAdapterError;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.converter.ConverterError;

public class TypeConversion implements Conversion<Object, Object> {

	final TypeAdapter<Object> adapter;
	final Class<?> targetType;

	protected TypeConversion(TypeAdapter<Object> adapter, Class<?> targetType) {
		this.adapter = adapter;
		this.targetType = targetType;
	}
	
	public static TypeConversion of(TypeAdapter<Object> adapter, Class<?> targetType) {
		return new TypeConversion(adapter, targetType);
	}

	@Override
	public Object convert(Object object) throws ConverterError {
		try {
			return adapter.convert(targetType, object);
			
		} catch (TypeAdapterError e) {
			throw new ConverterError(e);
		}
	}
	
}
