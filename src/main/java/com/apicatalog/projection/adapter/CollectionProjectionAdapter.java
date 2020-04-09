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
import com.apicatalog.projection.objects.ObjectType;

public class CollectionProjectionAdapter implements ProjectionAdapter {

	final Logger logger = LoggerFactory.getLogger(CollectionProjectionAdapter.class);

	final ProjectionRegistry factory;
	
	final TypeAdaptersLegacy typeAdapters;	//TODO use just concrete adapter(s), not whole factory
	
	final ObjectType targetType;
	
	public CollectionProjectionAdapter(ProjectionRegistry factory, TypeAdaptersLegacy typeAdapters, ObjectType targetType) {
		this.factory = factory;
		this.typeAdapters = typeAdapters;
		
		this.targetType = targetType;
	}
	
	@Override
	public Object forward(ProjectionStack queue, Object object, CompositionContext context) throws ProjectionError {
		
		logger.debug("Convert {} to {}, depth = {}, reference = true, collection = true", object.getClass().getSimpleName(), targetType, queue.length());

		final Collection<?> sourceCollection = (Collection<?>)typeAdapters.convert(ArrayList.class, Object.class, object);
		
		final Collection<Object> collection = new ArrayList<>();

		@SuppressWarnings("unchecked")
		final Projection<Object> projection = (Projection<Object>) factory.get(targetType.getComponentClass());
		
		if (projection == null) {
			throw new ProjectionError("Projection " + targetType.getComponentClass().getCanonicalName() +  " is not present.");
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

	@Override
	public Object backward(ObjectType sourceType, Object object, ExtractionContext context) throws ProjectionError {
		logger.debug("Convert {} to {}, reference = true, collection = true", targetType, sourceType);
		
		@SuppressWarnings("unchecked")
		final Projection<Object> projection = (Projection<Object>) factory.get(targetType.getComponentClass()); 
		
		if (projection == null) {
			throw new ProjectionError("Projection " + targetType.getComponentClass().getCanonicalName() +  " is not present.");
		}
		

		final Collection<Object> collection = new ArrayList<>();

		final Collection<?> sourceCollection = (Collection<?>)typeAdapters.convert(ArrayList.class, targetType.getComponentClass(), object);

		final Class<?> componentClass = sourceType.getComponentClass();
				
		// extract objects from each projection in the collection
		for (final Object item : sourceCollection) {
			
			projection.extract(item, context.accept(null, componentClass, null));
			
			context.remove(null, componentClass, null).ifPresent(collection::add);
		}
		
		return collection;
	}
}
