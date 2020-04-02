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
		logger.debug("Write {}, {} sources(s), optional = {}, depth = {}", object, sources.length, optional, queue.length());

		Object[] sourceObjects = reduction.expand(object);
		
		// apply explicit conversions in reverse order
		if (conversions != null) {
			for (int i=conversions.length - 1; i >= 0; i--) {
				object = conversions[i].backward(object);
			}
		}

		for (int i = 0; i < sources.length; i++) {
			sources[i].write(queue, sourceObjects[i], context);
		}				
	}
	
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
