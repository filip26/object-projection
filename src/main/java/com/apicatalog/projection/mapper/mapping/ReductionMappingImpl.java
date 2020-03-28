package com.apicatalog.projection.mapper.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ObjectUtils;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.converter.ConverterConfig;
import com.apicatalog.projection.converter.Reducer;
import com.apicatalog.projection.converter.ReducerError;
import com.apicatalog.projection.converter.ReducerMapping;
import com.apicatalog.projection.mapping.ReductionMapping;

public class ReductionMappingImpl implements ReductionMapping {

	final Logger logger = LoggerFactory.getLogger(ReductionMappingImpl.class);
	
	final TypeAdapters typeAdapters;

	//TODO !!! use ConverterFactory|Index!!!! a conversion utilizes a convertor
	
	final ReducerMapping<?, ?> reducerMapping;
	final String[] config;

	public ReductionMappingImpl(ReducerMapping<?, ?> reducerMapping, TypeAdapters typeAdapters, String[] config) {
		this.reducerMapping = reducerMapping;
		this.typeAdapters = typeAdapters;
		this.config = config;
	}
	
	@Override
	public Object reduce(Object...objects) throws ProjectionError {
		
		logger.debug("{}.reduce({}, {})", reducerMapping.getReducerClass().getSimpleName(), objects, config);
		
		final Reducer reducer = ObjectUtils.newInstance(reducerMapping.getReducerClass());	//TODO re-use preconstructed instances

		ConverterConfig ctx = new ConverterConfig();
		ctx.setValues(config);

		try {
			reducer.initReducer(ctx);
				System.out.println(">>>> " + reducerMapping.getSourceClass() + ", " + objects);
			final Object result = reducer.reduce((Object[])typeAdapters.convert(reducerMapping.getSourceClass(), objects));
			
			logger.trace("  result={}", result);
			
			return result;
			
		} catch (ReducerError e) {
			throw new ProjectionError(e);
		}
			
	}

	@Override
	public Object[] expand(Object object) throws ProjectionError {

		logger.debug("{}.expand({}, {})", reducerMapping.getReducerClass().getSimpleName(), object, config);
		
		final Reducer reducer = ObjectUtils.newInstance(reducerMapping.getReducerClass());	//TODO re-use preconstructed instances

		ConverterConfig ctx = new ConverterConfig();
		ctx.setValues(config);

		try {
			reducer.initReducer(ctx);
				
			final Object[] result = reducer.expand(typeAdapters.convert(reducerMapping.getTargetClass(), object));
			
			logger.trace("  result={}", result);
			
			return result;
			
		} catch (ReducerError e) {
			throw new ProjectionError(e);
		}			
	}
}
