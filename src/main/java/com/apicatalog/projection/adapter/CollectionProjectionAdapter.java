package com.apicatalog.projection.adapter;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.adapter.type.TypeAdaptersLegacy;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.object.ObjectType;

@Deprecated
public class CollectionProjectionAdapter   {

	final Logger logger = LoggerFactory.getLogger(CollectionProjectionAdapter.class);

	final ProjectionRegistry factory;
	
	final TypeAdaptersLegacy typeAdapters;	//TODO use just concrete adapter(s), not whole factory
	
	final ObjectType targetType;
	
	public CollectionProjectionAdapter(ProjectionRegistry factory, ObjectType targetType) {
		this.factory = factory;
		this.typeAdapters = new TypeAdaptersLegacy();
		
		this.targetType = targetType;
	}
	
	
	public Object forward(ProjectionStack queue, Object object, CompositionContext context) throws ProjectionError {
		
		logger.debug("Convert {} to {}, depth = {}, reference = true, collection = true", object.getClass().getSimpleName(), targetType, queue.length());

		final Collection<?> sourceCollection = (Collection<?>)typeAdapters.convert(ArrayList.class, Object.class, object);
		
		final Collection<Object> collection = new ArrayList<>();

		@SuppressWarnings("unchecked")
		final Projection<Object> projection = (Projection<Object>) factory.get(targetType.getComponentType());
		
		if (projection == null) {
			throw new ProjectionError("Projection " + targetType.getComponentType().getCanonicalName() +  " is not present.");
		}
		
		// compose a projection from each object in the collection
		for (final Object item : sourceCollection) {				
			collection.add(projection
							.compose(
								queue,										
								(new CompositionContext(context)).put(item)
								)
							);
		}
		
		return collection;
	}

	
	public Object backward(ObjectType sourceType, Object object, ExtractionContext context) throws ProjectionError {
		logger.debug("Convert {} to {}, reference = true, collection = true", targetType, sourceType);
		
		@SuppressWarnings("unchecked")
		final Projection<Object> projection = (Projection<Object>) factory.get(targetType.getComponentType()); 
		
		if (projection == null) {
			throw new ProjectionError("Projection " + targetType.getComponentType().getCanonicalName() +  " is not present.");
		}
		

		final Collection<Object> collection = new ArrayList<>();

		final Collection<?> sourceCollection = (Collection<?>)typeAdapters.convert(ArrayList.class, targetType.getComponentType(), object);

		final Class<?> componentClass = sourceType.getComponentType();
				
		// extract objects from each projection in the collection
		for (final Object item : sourceCollection) {
			
			projection.extract(item, context.accept(null, componentClass, null));
			
			context.remove(null, componentClass, null).ifPresent(collection::add);
		}
		
		return collection;
	}
}
