package com.apicatalog.projection.mapper.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.converter.ConverterConfig;
import com.apicatalog.projection.mapping.ReducerMapping;
import com.apicatalog.projection.mapping.ReductionMapping;
import com.apicatalog.projection.objects.ObjectUtils;
import com.apicatalog.projection.reducer.Reducer;
import com.apicatalog.projection.reducer.ReducerError;

@Deprecated
public class ReductionMappingImpl implements ReductionMapping {

	final Logger logger = LoggerFactory.getLogger(ReductionMappingImpl.class);
	
	final TypeAdapters typeAdapters;

	//TODO !!! use ConverterFactory|Index!!!! a conversion utilizes a convertor
	
	final ReducerMapping reducerMapping;
	final String[] config;

	public ReductionMappingImpl(ReducerMapping reducerMapping, TypeAdapters typeAdapters, String[] config) {
		this.reducerMapping = reducerMapping;
		this.typeAdapters = typeAdapters;
		this.config = config;
	}
	
	@Override
	public Object reduce(Object...objects) throws ProjectionError {
		
		logger.debug("{}.reduce({}, {})", reducerMapping.getReducerClass().getSimpleName(), objects, config);
		
		final Reducer<Object, Object> reducer = (Reducer<Object, Object>) ObjectUtils.newInstance(reducerMapping.getReducerClass());	//TODO re-use preconstructed instances

		ConverterConfig ctx = new ConverterConfig(config);

		try {
			reducer.initReducer(ctx);

			final Object result = reducer.reduce((Object[])typeAdapters.convert(reducerMapping.getSourceClass(), reducerMapping.getSourceComponentClass(), objects));
			
			logger.trace("  result={}", result);
			
			return result;
			
		} catch (ReducerError e) {
			throw new ProjectionError(e);
		}
			
	}

	@Override
	public Object[] expand(Object object) throws ProjectionError {

		logger.debug("{}.expand({}, {})", reducerMapping.getReducerClass().getSimpleName(), object, config);
		
		final Reducer<Object, Object> reducer = (Reducer<Object, Object>) ObjectUtils.newInstance(reducerMapping.getReducerClass());	//TODO re-use preconstructed instances

		ConverterConfig ctx = new ConverterConfig(config);

		try {
			reducer.initReducer(ctx);
				
			final Object[] result = reducer.expand(typeAdapters.convert(reducerMapping.getTargetClass(), reducerMapping.getTargetComponentClass(), object));
			
			logger.trace("  result={}", result);
			
			return result;
			
		} catch (ReducerError e) {
			throw new ProjectionError(e);
		}			
	}
	
	@Override
	public ReducerMapping getReducerMapping() {
		return reducerMapping;
	}
}
