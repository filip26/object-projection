package com.apicatalog.projection.property.target;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.object.ObjectType;

public final class CollectionComposer implements TargetComposer {

	final Logger logger = LoggerFactory.getLogger(CollectionComposer.class);
	
	final String projectionName;
	
	final ObjectType targetType;
	
	Projection<?> projection;
	
	public CollectionComposer(final ObjectType targetType, final String projectionName) {
		this.targetType = targetType;
		this.projectionName = projectionName;
	}

	@Override
	public Optional<Object> compose(final ProjectionStack stack, final Object object, final CompositionContext context) throws CompositionError {
	
		if (logger.isDebugEnabled()) {
			logger.debug("Compose {} to {}, depth = {}, reference = true, collection = true", object.getClass().getSimpleName(), targetType, stack.length());
		}

		if (object == null) {
			return Optional.empty();
		}

		if (projection == null) {
			throw new CompositionError("Projection " + projectionName +  " is not set.");
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

		// compose a projection from each object in the collection
		for (final Object item : sourceCollection) {				
			collection.add(projection.getComposer()
							.orElseThrow(() -> new CompositionError("Projection " + projectionName + " composer is not set."))
							.compose(
								stack,										
								(new CompositionContext(context)).put(item)
								)
							);
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
		final Object out = java.lang.reflect.Array.newInstance(type, in.length);
		System.arraycopy(in, 0, out, 0, in.length);
		return (P[])out;
	}

	public void setProjection(Projection<?> projection) {
		this.projection = projection;
	}

	@Override
	public String getProjectionName() {
		return projectionName;
	}
}
