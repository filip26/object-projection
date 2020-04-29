package com.apicatalog.projection.property.target;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ExtractionError;
import com.apicatalog.projection.ProjectionExtractor;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.object.ObjectType;

public final class ObjectExtractor implements TargetExtractor {

	final Logger logger = LoggerFactory.getLogger(ObjectExtractor.class);

	final String projectionName;
	
	ProjectionExtractor<Object> extractor;

	public ObjectExtractor(final String projectionName) {
		this.projectionName = projectionName;
	}
	
	@Override
	public Optional<Object> extract(final ObjectType sourceType, final Object object, final ExtractionContext context) throws ExtractionError {

		if (logger.isDebugEnabled()) {
			logger.debug("Extract {} from {}, reference = true", sourceType, object.getClass().getSimpleName());
		}
		
		if (extractor == null) {
			throw new ExtractionError("Projection " + object.getClass().getCanonicalName() +  " is not set.");
		}
			
		extractor.extract(
						object, 
						context
							.accept(null, sourceType.getType(), sourceType.getComponentType())
						);
						
		return Optional.ofNullable(context.remove(null, sourceType.getType(), sourceType.getComponentType()).orElse(null));
	}
	
	@SuppressWarnings("unchecked")
	public void setProjection(Projection<?> projection) {
		this.extractor = (ProjectionExtractor<Object>) projection.getExtractor().orElse(null);
	}

	@Override
	public String getProjectionName() {
		return projectionName;
	}
}
