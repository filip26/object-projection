package com.apicatalog.projection.property;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ObjectConversion;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.ObjectUtils;
import com.apicatalog.projection.objects.ProjectionQueue;
import com.apicatalog.projection.objects.access.Getter;
import com.apicatalog.projection.objects.access.Setter;
import com.apicatalog.projection.target.TargetAdapter;

public class SourceProperty implements ProjectionProperty {

	final Logger logger = LoggerFactory.getLogger(SourceProperty.class);

	Getter sourceGetter;
	Setter sourceSetter;
	
	ObjectConversion[] conversions;
	TargetAdapter targetAdapter;
	
	Getter targetGetter;
	Setter targetSetter;
	
	Class<?> sourceObjectClass;
	
	String sourceObjectQualifier;
	
	boolean optional;

	Set<Integer> visibleLevels;
	
	@Override
	public void forward(ProjectionQueue queue, ContextObjects context) throws ProjectionError {

		if (sourceGetter == null) {
			return;
		}
		
		logger.debug("Forward {}.{}, qualifier = {}, optional = {}, depth = {}", sourceObjectClass.getSimpleName(), sourceGetter.getName(), sourceObjectQualifier, optional, queue.length());

		final Optional<Object> instance = Optional.ofNullable(context.get(sourceObjectClass, sourceObjectQualifier));

		if (instance.isEmpty()) {
			if (optional) {
				return;
			}
			throw new ProjectionError("Source instance of " + sourceObjectClass.getCanonicalName() + ", qualifier=" + sourceObjectQualifier + ",  is not present.");
		}

		// get source value
		Object object = sourceGetter.get(instance.get());

		logger.trace("{}.{} = {}", sourceObjectClass.getSimpleName(), targetSetter.getName(), object);
		
		if (object == null) {
			return;
		}
		
		// apply explicit conversions
		if (conversions != null) {
			for (ObjectConversion conversion : conversions) {
				object = conversion.forward(object);
			}
		}

		object = targetAdapter.forward(queue, object, context);

		targetSetter.set(queue.peek(), object);
		
	}

	@Override
	public void backward(ProjectionQueue queue, ContextObjects context) throws ProjectionError {

		if (sourceSetter == null) {
			return;
		}
		

		logger.debug("Backward {}.{}, qualifier = {}, optional = {}, depth = {}", sourceObjectClass.getSimpleName(), sourceSetter.getName(), sourceObjectQualifier, optional, queue.length());

		Object object = targetGetter.get(queue.peek());
		
		if (object == null) {
			return;
		}
		
		object = targetAdapter.backward(object, context);
		
		for (int i=conversions.length; i > 0; --i) {
			object = conversions[i].backward(object);
		}
	
		Object instance = context.get(sourceObjectClass, sourceObjectQualifier);
		
		if (instance == null) {
			instance = ObjectUtils.newInstance(sourceObjectClass);
			context.addOrReplace(instance, sourceObjectQualifier);
		}
		
		sourceSetter.set(instance, object);
	}
	
	public void setConversions(ObjectConversion[] conversions) {
		this.conversions = conversions;
	}
	
	public void setTargetAdapter(TargetAdapter targetAdapter) {
		this.targetAdapter = targetAdapter;
	}
	
	public void setTargetGetter(Getter targetGetter) {
		this.targetGetter = targetGetter;
	}
	
	public void setTargetSetter(Setter targetSetter) {
		this.targetSetter = targetSetter;
	}
	
	public void setQualifier(String qualifier) {
		this.sourceObjectQualifier = qualifier;
	}
	
	public void setSourceGetter(Getter sourceGetter) {
		this.sourceGetter = sourceGetter;
	}
	
	public void setSourceSetter(Setter sourceSetter) {
		this.sourceSetter = sourceSetter;
	}
	
	public void setSourceObjectClass(Class<?> sourceObjectClass) {
		this.sourceObjectClass = sourceObjectClass;
	}

	public Class<?> getSourceObjectClass() {
		return sourceObjectClass;
	}
	
	public void setOptional(boolean optional) {
		this.optional = optional;
	}
	@Override
	public boolean isVisible(int depth) {
		return visibleLevels == null || visibleLevels.isEmpty() || visibleLevels.contains(depth);
	}
	
	public void setVisible(final Set<Integer> levels) {
		this.visibleLevels = levels;
	}
	
	public void setSourceObjectQualifier(String sourceObjectQualifier) {
		this.sourceObjectQualifier = sourceObjectQualifier;
	}
}
