package com.apicatalog.projection.mapper;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.annotation.Reduction;
import com.apicatalog.projection.mapper.mapping.ReductionMappingImpl;
import com.apicatalog.projection.mapping.ReducerMapping;
import com.apicatalog.projection.mapping.ReductionMapping;

public class ReductionMapper {

	final Logger logger = LoggerFactory.getLogger(ReductionMapper.class);
	
	final TypeAdapters typeAdapters;
	final ProjectionFactory factory;
	
	public ReductionMapper(ProjectionFactory factory, TypeAdapters typeAdapters) {
		this.factory = factory;
		this.typeAdapters = typeAdapters;
	}
	
	public ReductionMapping getReductionMapping(Reduction reduction) {
		
		//FIXME use ConverterFactory
		ReducerMapping reducer = new ReducerMapping();

		reducer.setReducerClass(reduction.type());
		//FIXME checks
		
		Class<?> sourceClass = (Class<?>)(((ParameterizedType) reduction.type().getGenericInterfaces()[0]).getActualTypeArguments()[0]); 
		
		// get an array of it
		sourceClass = Array.newInstance(sourceClass, 0).getClass();
		
		reducer.setSourceClass(sourceClass);
		reducer.setTargetClass((Class<?>)(((ParameterizedType) reduction.type().getGenericInterfaces()[0]).getActualTypeArguments()[1]));

		
		return new ReductionMappingImpl(reducer, typeAdapters, reduction.value());
	}
	
}
