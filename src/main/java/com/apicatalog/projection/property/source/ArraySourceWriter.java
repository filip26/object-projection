package com.apicatalog.projection.property.source;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.source.SourceType;

public final class ArraySourceWriter implements SourceWriter {

	final Logger logger = LoggerFactory.getLogger(ArraySourceWriter.class);

	SourceWriter[] sources;
	
	Collection<SourceType> sourceTypes;
	
	Collection<Conversion<Object, Object>> conversions;
	
	ObjectType targetType;
	
	Set<Integer> visibleLevels;
	
	boolean optional;
	
	@Override
	public void write(final ExtractionContext context, final Object object) throws ProjectionError {

		if (logger.isDebugEnabled()) {
			logger.debug("Write {}, {} sources(s), optional = {}", object, sources.length, optional);
		}
		
		Optional<Object> value = Optional.ofNullable(object);
		
		if (value.isEmpty()) {
			return;
		}
		
		try {			
			// apply conversions
			if (conversions != null) {
				
				for (final Conversion<Object, Object> conversion : conversions) {
					
					value = Optional.ofNullable(conversion.convert(value.get()));
					
					if (value.isEmpty()) {
						return;
					}
				}
			}

			final Object[] sourceObjects = value.map(Object[].class::cast).get();
			
			for (int i = 0; i < sources.length; i++) {
				
				if (i >= sourceObjects.length || sourceObjects[i] == null) {
					continue;
				}
				
				sources[i].write(context, sourceObjects[i]);
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
		this.sourceTypes = new HashSet<>();
		
		for (SourceWriter writer : sources) {
			this.sourceTypes.addAll(writer.getSourceTypes());
		}
		
	}
	
	public void setTargetType(ObjectType targetType) {
		this.targetType = targetType;
	}

	@Override
	public ObjectType getTargetType() {
		return targetType;
	}

	@Override
	public boolean isAnyTypeOf(final SourceType... sourceTypes) {
		return Arrays.stream(sources).anyMatch(s -> s.isAnyTypeOf(sourceTypes));
	}
	
	public void setConversions(Collection<Conversion<Object, Object>> conversions) {
		this.conversions = conversions;
	}

	@Override
	public Collection<SourceType> getSourceTypes() {
		return sourceTypes;
	}
}
