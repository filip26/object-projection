package com.apicatalog.projection.source;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.mapping.ConversionMapping;
import com.apicatalog.projection.mapping.ReductionMapping;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.ProjectionQueue;
import com.apicatalog.projection.target.TargetAdapter;

public class ArraySource implements Source {

	final Logger logger = LoggerFactory.getLogger(ArraySource.class);

	Source[] sources;
	
	ReductionMapping reduction;
	
	ConversionMapping[] conversions;
	
	TargetAdapter targetAdapter;

	Class<?> targetClass;
	Class<?> targetComponentClass;
	
	Set<Integer> visibleLevels;
	
	boolean optional;

	@Override
	public Object read(ProjectionQueue queue, ContextObjects context) throws ProjectionError {
		
		if (!isReadable()) {
			return null;
		}
		
		logger.debug("Read {} source(s), optional = {}, depth = {}", sources.length, optional, queue.length());

		final Object[] sourceObjects = new Object[sources.length];
	
		for (int i = 0; i < sources.length; i++) {
			sourceObjects[i] = sources[i].read(queue, context);
		}
	
		Object object = reduction.reduce(sourceObjects);
		
		// apply explicit conversions
		if (conversions != null) {
			for (ConversionMapping conversion : conversions) {
				object = conversion.forward(object);
			}
		}

		return object;
	}

	@Override
	public void write(ProjectionQueue queue, Object object, ContextObjects context) throws ProjectionError {
		// TODO Auto-generated method stub
		
	}
//	@Override
//	public void forward(ProjectionQueue queue, ContextObjects context) throws ProjectionError {
//		
////		final Object[] sourceObjects = new Object[sources.length];
////		
////		for (int i = 0; i < sources.length; i++) {
////			sourceObjects[i] = sources[i].read(context);
////		}
////		
////		Object object = reduction.reduce(sourceObjects);
////		
////		for (int i = 0; i < conversions.length; i++) {
////			object = conversions[i].forward(object);
////		}
////		
////		object = targetAdapter.construct(queue, object, context);
////		
////		targetSetter.set(queue.peek(), object);
//	}
//
//	@Override
//	public void backward(ProjectionQueue queue, ContextObjects context) throws ProjectionError {
//
////		Object object = targetGetter.get(queue.peek());
////		
////		object = targetAdapter.deconstruct(object, context);
////		
////		for (int i=conversions.length; i > 0; --i) {
////			object = conversions[i].backward(object);
////		}
////		
////		Object[] sourceObjects = reduction.expand(object);
////		
////		for (int i = 0; i < sources.length; i++) {
////			sources[i].write(context, sourceObjects);
////		}
//	}
	
	public void setVisible(final Set<Integer> levels) {
		this.visibleLevels = levels;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}
	
	public void setSources(Source[] sources) {
		this.sources = sources;
	}
	
	public void setTargetAdapter(TargetAdapter targetAdapter) {
		this.targetAdapter = targetAdapter;
	}
	
	public void setConversions(ConversionMapping[] conversions) {
		this.conversions = conversions;
	}
	
	public void setReduction(ReductionMapping reduction) {
		this.reduction = reduction;
	}

	@Override
	public boolean isReadable() {
		return true;	//FIXME
	}

	@Override
	public boolean isWritable() {
		return true;	//FIXME
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}
	
	public Class<?> getTargetComponentClass() {
		return targetComponentClass;
	}
	
	public void setTargetClass(Class<?> targetClass) {
		this.targetClass = targetClass;
	}
	
	public void setTargetComponentClass(Class<?> targetComponentClass) {
		this.targetComponentClass = targetComponentClass;
	}
}
