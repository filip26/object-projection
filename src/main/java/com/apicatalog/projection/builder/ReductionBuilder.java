package com.apicatalog.projection.builder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.objects.ObjectUtils;
import com.apicatalog.projection.reducer.Reducer;
import com.apicatalog.projection.reducer.ReducerConfig;
import com.apicatalog.projection.reducer.ReducerError;
import com.apicatalog.projection.reducer.ReducerMapping;

public class ReductionBuilder {

	final Logger logger = LoggerFactory.getLogger(ReductionBuilder.class);
	
	Class<?> reducerClass;
	String[] parameters;
	
	protected ReductionBuilder() {
	}
	
	public static final ReductionBuilder newInstance() {
		return new ReductionBuilder();
	}
		
	public ReducerMapping build(TypeAdapters typeAdapters) throws ReducerError, ProjectionError {
		
		ReducerMapping reducer = new ReducerMapping();
		
		@SuppressWarnings("unchecked")
		Reducer<Object, Object> instance = (Reducer<Object, Object>) ObjectUtils.newInstance(reducerClass);
		
		instance.initReducer(new ReducerConfig(parameters));
		
		reducer.setReducer(instance);

		Type sourceType = ((ParameterizedType) reducerClass.getGenericInterfaces()[0]).getActualTypeArguments()[0];
		
//		Class<?> sourceClass = null;
		Class<?> sourceComponentClass = null;
		
		if (ParameterizedType.class.isInstance(sourceType)) {
//			sourceClass = (Class<?>)((ParameterizedType)sourceType).getRawType();
			sourceComponentClass = (Class<?>)((ParameterizedType)sourceType).getActualTypeArguments()[0];
			
		} else {
//			sourceClass = (Class<?>) sourceType;
		}
		
		Type targetType = ((ParameterizedType) reducerClass.getGenericInterfaces()[0]).getActualTypeArguments()[1];
		
		Class<?> targetClass = null;
		Class<?> targetComponentClass = null;
		
		if (ParameterizedType.class.isInstance(targetType)) {
			targetClass = (Class<?>)((ParameterizedType)targetType).getRawType();
			targetComponentClass = (Class<?>)((ParameterizedType)targetType).getActualTypeArguments()[0];
			
		} else {
			targetClass = (Class<?>) targetType;
		}

		//FIXME hack
		try {
			reducer.setSourceType(ObjectType.of(reducerClass.getMethod("expand", (Class<?>)targetType).getReturnType(), sourceComponentClass));
			
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException();
		}
		
		reducer.setTargetType(ObjectType.of(targetClass, targetComponentClass));
		
		return reducer;		
	}
	
	public ReductionBuilder converter(Class<?> converterClass) {
		this.reducerClass = converterClass;
		return this;
	}
	
	public ReductionBuilder parameters(String[] parameters) {
		this.parameters = parameters;
		return this;
	}
}
