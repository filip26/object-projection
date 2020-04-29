package com.apicatalog.projection.property.target;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ExtractionError;
import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionExtractor;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.object.ObjectType;

public final class CollectionExtractor implements TargetExtractor {

	final Logger logger = LoggerFactory.getLogger(CollectionExtractor.class);
	
	final String projectionName;
	final ObjectType targetType;
	
	ProjectionExtractor<Object> extractor;
	
	public CollectionExtractor(final ObjectType targetType, final String projectionName) { 
		this.targetType = targetType;
		this.projectionName = projectionName;
	}
	
	@Override
	public Optional<Object> extract(final ObjectType sourceType, final Object object, final ExtractionContext context) throws ExtractionError {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Extract {} to {}, reference = true, collection = true", targetType, sourceType);
		}
		
		if (object == null) {
			return Optional.empty();
		}
		
		if (extractor == null) {
			throw new ExtractionError("Projection " + projectionName +  " is not set.");
		}
		
		final Collection<?> sourceCollection;		
		
		if (Collection.class.isInstance(object)) {
			sourceCollection = (Collection<?>)object;
			
		} else if (object.getClass().isArray()) {
			sourceCollection = Arrays.asList((Object[])object);
			
		} else {
			throw new IllegalStateException();
		}
			
		final Collection<Object> collection = new ArrayList<>();

		final Class<?> componentClass = sourceType.getComponentType();
				
		// extract objects from each projection in the collection
		for (final Object item : sourceCollection) {
			
			extractor.extract(item, context.accept(null, componentClass, null));
			
			context.remove(null, componentClass, null).ifPresent(collection::add);
		}

		if (targetType.isCollection()) {
			return Optional.of(collection);	
		}
		
		if (targetType.isArray()) {
			return Optional.of(typedArray(collection.toArray(), targetType.getType().getComponentType()));
		}
		
		throw new IllegalStateException();
	}
	
	@SuppressWarnings("unchecked")
	<P> P[] typedArray(final Object[] in, final Class<P> type) {
		final Object out =  java.lang.reflect.Array.newInstance(type, in.length);
		System.arraycopy(in, 0, out, 0, in.length);
		return (P[])out;
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
