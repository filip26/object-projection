package com.apicatalog.projection.property.target;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.objects.ProjectionQueue;

public class TargetProjectedCollectionConverter implements TargetAdapter {

	final Logger logger = LoggerFactory.getLogger(TargetProjectedCollectionConverter.class);

	final ProjectionRegistry factory;
	
	final TypeAdapters typeAdapters;	//TODO use just concrete adapter(s), not whole factory
	
	final ObjectType sourceType;
	
	final ObjectType targetType;
	
	public TargetProjectedCollectionConverter(ProjectionRegistry factory, TypeAdapters typeAdapters, ObjectType sourceType, ObjectType targetType) {
		this.factory = factory;
		this.typeAdapters = typeAdapters;
		
		this.sourceType = sourceType;
		this.targetType = targetType;
	}
	
	@Override
	public Object forward(ProjectionQueue queue, Object object, CompositionContext context) throws ProjectionError {
		
		logger.debug("Convert {} to {}, depth = {}, reference = true, collection = true", sourceType, targetType, queue.length());

		final Collection<?> sourceCollection = (Collection<?>)typeAdapters.convert(ArrayList.class, Object.class, object);
		
		final Collection<Object> collection = new ArrayList<>();

		@SuppressWarnings("unchecked")
		final Projection<Object> projection = (Projection<Object>) factory.get(targetType.getObjectComponentClass());
		
		if (projection == null) {
			throw new ProjectionError("Projection " + targetType.getObjectComponentClass().getCanonicalName() +  " is not present.");
		}
		
		// compose a projection from each object in the collection
		for (final Object item : sourceCollection) {				
			collection.add(projection
							.compose(
								queue,										
								(new CompositionContext(context)).addOrReplace(item, null)
								)
							);
		}
		
		return collection;
	}

	@Override
	public Object backward(Object object, ExtractionContext context) throws ProjectionError {
		logger.debug("Convert {} to {}, reference = true, collection = true", targetType, sourceType);
		
		@SuppressWarnings("unchecked")
		final Projection<Object> projection = (Projection<Object>) factory.get(targetType.getObjectComponentClass()); 
		
		if (projection == null) {
			throw new ProjectionError("Projection " + targetType.getObjectComponentClass().getCanonicalName() +  " is not present.");
		}
		

		final Collection<Object> collection = new ArrayList<>();

		Collection<?> sourceCollection = (Collection<?>)typeAdapters.convert(ArrayList.class, targetType.getObjectComponentClass(), object);
		
		// extract objects from each projection in the collection
		for (final Object item : sourceCollection) {	//TODO

			projection.extract(item, context.accept(null, sourceType.getObjectComponentClass(), null));
			collection.add(context.remove(null, sourceType.getObjectComponentClass(), null));
		}
		
		return collection;
	}
}
