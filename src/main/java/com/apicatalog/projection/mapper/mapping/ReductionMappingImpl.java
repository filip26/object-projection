package com.apicatalog.projection.mapper.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ObjectUtils;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.converter.ContextValue;
import com.apicatalog.projection.converter.Reducer;
import com.apicatalog.projection.converter.ReducerError;
import com.apicatalog.projection.mapping.ReductionMapping;

public class ReductionMappingImpl implements ReductionMapping {

	final Logger logger = LoggerFactory.getLogger(ReductionMappingImpl.class);

	//TODO !!! use ConverterFactory|Index!!!! a conversion utilizes a convertor
	
	final Class<? extends Reducer> reducerClass;
	final String[] context;

	public ReductionMappingImpl(Class<? extends Reducer> reducerClass, String[] context) {
		this.reducerClass = reducerClass;
		this.context = context;
	}
	
	@Override
	public Object reduce(Object...objects) throws ProjectionError {
		
		logger.debug("{}.reduce({}, {})", reducerClass.getSimpleName(), objects, context);
		
		final Reducer reducer = ObjectUtils.newInstance(reducerClass);	//TODO re-use preconstructed instances

		ContextValue ctx = new ContextValue();
		ctx.setValues(context);

		try {
			reducer.init(ctx);
				
			final Object result = reducer.reduce(objects);
			
			logger.trace("  result={}", result);
			
			return result;
			
		} catch (ReducerError e) {
			throw new ProjectionError(e);
		}
			
	}

	@Override
	public Object[] expand(Object object) throws ProjectionError {

		logger.debug("{}.expand({}, {})", reducerClass.getSimpleName(), object, context);
		
		final Reducer reducer = ObjectUtils.newInstance(reducerClass);	//TODO re-use preconstructed instances

		ContextValue ctx = new ContextValue();
		ctx.setValues(context);

		try {
			reducer.init(ctx);
				
			final Object[] result = reducer.expand(object);
			
			logger.trace("  result={}", result);
			
			return result;
			
		} catch (ReducerError e) {
			throw new ProjectionError(e);
		}			
	}
}
