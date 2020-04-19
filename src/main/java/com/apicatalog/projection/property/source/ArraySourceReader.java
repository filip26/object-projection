package com.apicatalog.projection.property.source;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.source.SourceType;

public final class ArraySourceReader implements SourceReader {

	final Logger logger = LoggerFactory.getLogger(ArraySourceReader.class);

	SourceReader[] sources;
	
	Collection<Conversion<Object, Object>> conversions;
	
	Collection<SourceType> sourceTypes;
	
	ObjectType targetType;
	
	Set<Integer> visibleLevels;
	
	boolean optional;
	
	@Override
	public Optional<Object> read(final CompositionContext context) throws ProjectionError {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Read {} source(s), optional = {}", sources.length, optional);
		}

		try {

			final Object[] sourceObjects = new Object[sources.length];
		
			for (int i = 0; i < sources.length; i++) {				
				sourceObjects[i] = sources[i].read(context).orElse(null);
			}

			Optional<Object> object = Optional.of(sourceObjects); 
						
			// apply conversions
			if (conversions != null) {
				for (final Conversion<Object, Object> conversion : conversions) {
					
					object = Optional.ofNullable(conversion.convert(object.get()));
					
					if (object.isEmpty()) {
						return Optional.empty();
					}
				}
			}
			
			return object;
			
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
		
		this.sourceTypes = new HashSet<>();
		
		for (SourceReader reader : sources) {
			this.sourceTypes.addAll(reader.getSourceTypes());
		}
	}
	
	public void setTargetType(ObjectType targetType) {
		this.targetType = targetType;
	}

	@Override
	public ObjectType getTargetType() {
		return targetType;
	}

	public void setConversions(Collection<Conversion<Object, Object>> conversions) {
		this.conversions = conversions;
	}

	@Override
	public Collection<SourceType> getSourceTypes() {
		return sourceTypes;
	}
}
