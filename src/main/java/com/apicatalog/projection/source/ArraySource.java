package com.apicatalog.projection.source;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.mapping.ConverterMapping;
import com.apicatalog.projection.mapping.ReducerMapping;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.objects.ProjectionQueue;
import com.apicatalog.projection.reducer.ReducerError;
import com.apicatalog.projection.target.TargetAdapter;

public class ArraySource implements Source {

	final Logger logger = LoggerFactory.getLogger(ArraySource.class);

	final TypeAdapters typeAdapters;
	
	Source[] sources;
	
	ReducerMapping reduction;
	
	ConverterMapping[] conversions;
	
	TargetAdapter targetAdapter;

	ObjectType targetType;
	
	Set<Integer> visibleLevels;
	
	boolean optional;

	public ArraySource(final TypeAdapters typeAdapters) {
		this.typeAdapters = typeAdapters;
	}
	
	@Override
	public Object read(ProjectionQueue queue, ContextObjects context) throws ProjectionError {
		
		if (!isReadable()) {
			return null;
		}
		
		logger.debug("Read {} source(s), optional = {}, depth = {}", sources.length, optional, queue.length());

		final Object[] sourceObjects = new Object[sources.length];
	
		for (int i = 0; i < sources.length; i++) {
			sourceObjects[i] = sources[i].read(queue, context);
		}
		
		try {
			Object object = reduction
								.getReducer()
								.reduce((Object[])
									typeAdapters
										.convert(
											reduction.getSourceType().getObjectClass(),
											reduction.getSourceType().getObjectComponentClass(),
											sourceObjects
											)
									);
			
			// apply explicit conversions
			if (conversions != null) {
	
					for (ConverterMapping conversion : conversions) {
						object = conversion.getConverter().forward(object);
					}
			}
			
			return object;
			
		} catch (ConverterError | ReducerError e) {
			throw new ProjectionError(e);
		}
	}

	@Override
	public void write(ProjectionQueue queue, Object object, ContextObjects context) throws ProjectionError {
		logger.debug("Write {}, {} sources(s), optional = {}, depth = {}", object, sources.length, optional, queue.length());

		try {
			Object[] sourceObjects = reduction
										.getReducer()
										.expand(
											typeAdapters
												.convert(
													reduction.getTargetType().getObjectClass(),
													reduction.getTargetType().getObjectComponentClass(),
													object
													)
												);
			
			// apply explicit conversions in reverse order
			if (conversions != null) {
					for (int i=conversions.length - 1; i >= 0; i--) {
						object = conversions[i].getConverter().backward(object);
					}
			}

			for (int i = 0; i < sources.length; i++) {
				sources[i].write(queue, sourceObjects[i], context);
			}				

		} catch (ConverterError | ReducerError e) {
			throw new ProjectionError(e);
		}
	}
	
	public void setVisible(final Set<Integer> levels) {
		this.visibleLevels = levels;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}
	
	public void setSources(Source[] sources) {
		this.sources = sources;
	}
	
	public void setTargetAdapter(TargetAdapter targetAdapter) {
		this.targetAdapter = targetAdapter;
	}
	
	public void setConversions(ConverterMapping[] conversions) {
		this.conversions = conversions;
	}
	
	public void setReduction(ReducerMapping reduction) {
		this.reduction = reduction;
	}

	@Override
	public boolean isReadable() {
		return true;	//FIXME
	}

	@Override
	public boolean isWritable() {
		return true;	//FIXME
	}

	public void setTargetType(ObjectType targetType) {
		this.targetType = targetType;
	}

	public ConverterMapping[] getConversions() {
		return conversions;
	}
	
	public ReducerMapping getReduction() {
		return reduction;
	}

	@Override
	public ObjectType getTargetType() {
		return targetType;
	}
}
