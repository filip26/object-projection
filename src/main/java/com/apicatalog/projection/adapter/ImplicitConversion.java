package com.apicatalog.projection.adapter;

import com.apicatalog.projection.adapter.type.TypeAdapter;
import com.apicatalog.projection.adapter.type.TypeAdapterError;
import com.apicatalog.projection.converter.ConverterError;

public class ImplicitConversion<S, T> implements Conversion<S, T> {

	final TypeAdapter<S> adapter;
	final Class<T> targetType;

	protected ImplicitConversion(TypeAdapter<S> adapter, Class<T> targetType) {
		this.adapter = adapter;
		this.targetType = targetType;
	}
	
	public static <S, T> ImplicitConversion<S, T> of(TypeAdapter<S> adapter, Class<T> targetType) {
		return new ImplicitConversion<>(adapter, targetType);
	}

	@Override
	public T convert(S object) throws ConverterError {
		try {
			return (T)adapter.convert(targetType, object);
			
		} catch (TypeAdapterError e) {
			throw new ConverterError(e);
		}
	}
	
}
