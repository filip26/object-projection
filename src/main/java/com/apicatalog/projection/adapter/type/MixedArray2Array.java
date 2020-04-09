package com.apicatalog.projection.adapter.type;

import java.lang.reflect.Array;

import com.apicatalog.projection.adapter.Conversion;
import com.apicatalog.projection.converter.ConverterError;

public class MixedArray2Array implements Conversion<Object[], Object[]> { 

	Conversion<Object, Object>[] conversions;
	
	Class<?> targetType;
	
	@Override
	public Object[] convert(Object[] objects) throws ConverterError {
		
		final Object[] target = (Object[]) Array.newInstance(targetType, objects.length);

		for (int i = 0; i < objects.length; i++) {
			target[i] = conversions != null && conversions[i] != null ? conversions[i].convert(objects[i]) : objects[i];
		}

		return target;
	}
	
	public void setConversions(Conversion<Object, Object>[] conversions) {
		this.conversions = conversions;
	}
	
	public void setTargetType(Class<?> targetType) {
		this.targetType = targetType;
	}
}
