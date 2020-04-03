package com.apicatalog.projection.mapper;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.annotation.Reduction;
import com.apicatalog.projection.mapping.ReductionMapping;
import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.objects.ObjectUtils;
import com.apicatalog.projection.reducer.Reducer;
import com.apicatalog.projection.reducer.ReducerConfig;
import com.apicatalog.projection.reducer.ReducerError;

public class ReductionMapper {

	final Logger logger = LoggerFactory.getLogger(ReductionMapper.class);
	
	final TypeAdapters typeAdapters;
	final ProjectionFactory factory;
	
	public ReductionMapper(ProjectionFactory factory, TypeAdapters typeAdapters) {
		this.factory = factory;
		this.typeAdapters = typeAdapters;
	}
	
	public ReductionMapping getReductionMapping(Reduction reduction) throws ProjectionError, ReducerError {
		
		ReductionMapping reducer = new ReductionMapping();

		@SuppressWarnings("unchecked")
		Reducer<Object, Object> instance = (Reducer<Object, Object>) ObjectUtils.newInstance(reduction.type());
		
		instance.initReducer(new ReducerConfig(reduction.value()));
		
		reducer.setReducer(instance);

		//FIXME checks
		
		Class<?> sourceClass = (Class<?>)(((ParameterizedType) reduction.type().getGenericInterfaces()[0]).getActualTypeArguments()[0]); 
		
		// get an array of it
		sourceClass = Array.newInstance(sourceClass, 0).getClass();
		
		reducer.setSourceType(ObjectType.of(sourceClass));
		reducer.setTargetType(ObjectType.of((Class<?>)(((ParameterizedType) reduction.type().getGenericInterfaces()[0]).getActualTypeArguments()[1])));
		
		return reducer;
	}	
}
