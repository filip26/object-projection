package com.apicatalog.projection.property.source;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.object.ObjectType;

public final class ArraySourceReader implements SourceReader {

	final Logger logger = LoggerFactory.getLogger(ArraySourceReader.class);

	SourceReader[] sources;
	
	Conversion[] conversions;
	
	ObjectType targetType;
	
	Set<Integer> visibleLevels;
	
	boolean optional;
	
	@Override
	public Optional<Object> read(ProjectionStack queue, CompositionContext context) throws ProjectionError {
		
		logger.debug("Read {} source(s), optional = {}, depth = {}", sources.length, optional, queue.length());

		try {

			final Object[] sourceObjects = new Object[sources.length];
		
			for (int i = 0; i < sources.length; i++) {				
				sourceObjects[i]= sources[i].read(queue, context).orElse(null);
			}

			Object object = sourceObjects; 
			
			// apply explicit conversions
			if (conversions != null) {
				for (Conversion conversion : conversions) {
					object = conversion.convert(object);
				}
			}
			
			return Optional.ofNullable(object);
			
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
	
	public void setSources(SourceReader[] sources) {
		this.sources = sources;
	}
	
	public void setTargetType(ObjectType targetType) {
		this.targetType = targetType;
	}

	@Override
	public ObjectType getTargetType() {
		return targetType;
	}

	public void setConversions(Conversion[] conversions) {
		this.conversions = conversions;
	}
}
