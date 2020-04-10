package com.apicatalog.projection.property.source;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.source.SourceType;

public final class ArraySource implements Source {

	final Logger logger = LoggerFactory.getLogger(ArraySource.class);

	Source[] sources;
	
	Conversion[] readConversions;
	Conversion[] writeConversions;
	
	ObjectType targetType;
	
	Set<Integer> visibleLevels;
	
	boolean optional;
	
	boolean readable;
	boolean writable;

	@Override
	public Optional<Object> read(ProjectionStack queue, CompositionContext context) throws ProjectionError {
		
		if (!isReadable()) {
			return Optional.empty();
		}
		
		logger.debug("Read {} source(s), optional = {}, depth = {}", sources.length, optional, queue.length());

		try {

			final Object[] sourceObjects = new Object[sources.length];
		
			for (int i = 0; i < sources.length; i++) {				
				sourceObjects[i]= sources[i].read(queue, context).orElse(null);
			}

			Object object = sourceObjects; 
			
			// apply explicit conversions
			if (readConversions != null) {
				for (Conversion conversion : readConversions) {
					object = conversion.convert(object);
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
			// apply explicit conversions
			if (writeConversions != null) {
					for (Conversion conversion : writeConversions) {
						object = conversion.convert(object);
					}
			}

			Object[] sourceObjects = (Object[])object;
			
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

	@Override
	public ObjectType getTargetType() {
		return targetType;
	}

	@Override
	public boolean isAnyTypeOf(SourceType... sourceTypes) {
		return Arrays.stream(sources).anyMatch(s -> s.isAnyTypeOf(sourceTypes));
	}
	
	public void setReadable(boolean readable) {
		this.readable = readable;
	}
	
	public void setWritable(boolean writable) {
		this.writable = writable;
	}
	
	public void setReadConversions(Conversion[] readConversions) {
		this.readConversions = readConversions;
	}
	
	public void setWriteConversions(Conversion[] writeConversions) {
		this.writeConversions = writeConversions;
	}
}
