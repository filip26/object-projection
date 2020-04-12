package com.apicatalog.projection.conversion.implicit;

import java.lang.reflect.Array;

import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.converter.ConverterError;

@Deprecated
public class MixedArray2Array implements Conversion { 

	final TypeConversion[] conversions;
	
	final Class<?> targetType;
	
	public MixedArray2Array(TypeConversion[] conversions, Class<?> targetType) {
		this.conversions = conversions;
		this.targetType = targetType;
	}
	
	@Override
	public Object convert(Object object) throws ConverterError {
		
		Object[] objects = (Object[])object;
		
		final Object[] target = (Object[]) Array.newInstance(targetType, objects.length);

		for (int i = 0; i < objects.length; i++) {
			target[i] = conversions != null && conversions[i] != null ? conversions[i].convert(objects[i]) : objects[i];
		}

		return target;
	}
}
