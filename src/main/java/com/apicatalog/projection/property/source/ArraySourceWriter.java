package com.apicatalog.projection.property.source;

import java.util.Arrays;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.source.SourceType;

public final class ArraySourceWriter implements SourceWriter {

	final Logger logger = LoggerFactory.getLogger(ArraySourceWriter.class);

	SourceWriter[] sources;
	
	Conversion[] conversions;
	
	ObjectType targetType;
	
	Set<Integer> visibleLevels;
	
	boolean optional;
	
	@Override
	public void write(ProjectionStack queue, ExtractionContext context, Object object) throws ProjectionError {
		
		logger.debug("Write {}, {} sources(s), optional = {}, depth = {}", object, sources.length, optional, queue.length());

		try {			
			// apply explicit conversions
			if (conversions != null) {
				for (final Conversion conversion : conversions) {
					object = conversion.convert(object);
				}
			}

			final Object[] sourceObjects = (Object[])object;
			
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
	
	public void setSources(SourceWriter[] sources) {
		this.sources = sources;
	}
	
	public void setTargetType(ObjectType targetType) {
		this.targetType = targetType;
	}

	@Override
	public ObjectType getType() {
		return targetType;
	}

	@Override
	public boolean isAnyTypeOf(final SourceType... sourceTypes) {
		return Arrays.stream(sources).anyMatch(s -> s.isAnyTypeOf(sourceTypes));
	}
	
	public void setConversions(Conversion[] conversions) {
		this.conversions = conversions;
	}
}
