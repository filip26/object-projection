package com.apicatalog.projection.property.source;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.source.SourceType;
import com.apicatalog.projection.type.adapter.TypeAdapters;

public final class ArraySource implements Source {

	final Logger logger = LoggerFactory.getLogger(ArraySource.class);

	final TypeAdapters typeAdapters;
	
	Source[] sources;
	
	ConverterMapping[] conversions;
	
	ObjectType targetType;
	
	Set<Integer> visibleLevels;
	
	boolean optional;
	
	boolean readable;
	boolean writable;

	public ArraySource(final TypeAdapters typeAdapters) {
		this.typeAdapters = typeAdapters;
	}
	
	@Override
	public Optional<Object> read(ProjectionStack queue, CompositionContext context) throws ProjectionError {
		
		if (!isReadable()) {
			return Optional.empty();
		}
		
		logger.debug("Read {} source(s), optional = {}, depth = {}", sources.length, optional, queue.length());

		final Object[] sourceObjects = new Object[sources.length];
	
		for (int i = 0; i < sources.length; i++) {
			sourceObjects[i] = sources[i].read(queue, context).orElse(null);
		}
		
		try {
			Object object = sourceObjects; 
			
			// apply explicit conversions
			if (conversions != null) {
	
					for (ConverterMapping conversion : conversions) {
						object = conversion.getConverter().forward(object);
					}
			}
			
			return Optional.ofNullable(object);
			
		} catch (ConverterError e) {
			throw new ProjectionError(e);
		}
	}

	@Override
	public void write(ProjectionStack queue, ExtractionContext context, Object object) throws ProjectionError {
		logger.debug("Write {}, {} sources(s), optional = {}, depth = {}", object, sources.length, optional, queue.length());

		try {			
			// apply explicit conversions in reverse order
			if (conversions != null) {
					for (int i=conversions.length - 1; i >= 0; i--) {
						object = conversions[i].getConverter().backward(object);
					}
			}

			Object[] sourceObjects = (Object[])object;	//TODO
			
			for (int i = 0; i < sources.length; i++) {
				sources[i].write(queue, context, sourceObjects[i]);
			}				

		} catch (ConverterError e) {
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
	
	public void setConversions(ConverterMapping[] conversions) {
		this.conversions = conversions;
	}
	
	@Override
	public boolean isReadable() {
		return writable;
	}

	@Override
	public boolean isWritable() {
		return readable;
	}

	public void setTargetType(ObjectType targetType) {
		this.targetType = targetType;
	}

	public ConverterMapping[] getConversions() {
		return conversions;
	}

	@Override
	public ObjectType getTargetType() {
		return targetType;
	}

	@Override
	public boolean isAnyTypeOf(SourceType... sourceTypes) {
		for (Source source : sources) {
			if (source.isAnyTypeOf(sourceTypes)) {
				return true;
			}
		}
		return false;
	}
	
	public void setReadable(boolean readable) {
		this.readable = readable;
	}
	
	public void setWritable(boolean writable) {
		this.writable = writable;
	}
}
