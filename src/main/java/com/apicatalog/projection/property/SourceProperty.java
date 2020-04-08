package com.apicatalog.projection.property;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.objects.getter.Getter;
import com.apicatalog.projection.objects.setter.Setter;
import com.apicatalog.projection.property.source.Source;
import com.apicatalog.projection.property.target.TargetAdapter;

public class SourceProperty implements ProjectionProperty {

	final Logger logger = LoggerFactory.getLogger(SourceProperty.class);

	Source source;
	
	TargetAdapter targetAdapter;
	
	Getter targetGetter;
	Setter targetSetter;
	
	Set<Integer> visibleLevels;
	
	@Override
	public void forward(ProjectionStack queue, CompositionContext context) throws ProjectionError {

		if (!source.isReadable() || targetSetter == null) {
			return;
		}
		
		logger.debug("Forward {} : {}, depth = {}", targetSetter.getName(), targetSetter.getType(), queue.length());

		// get source value
		Optional<Object> object = source.read(queue, context);
		
		if (object.isEmpty()) {
			return;
		}

		object = Optional.ofNullable(targetAdapter.forward(queue, object.get(), context));
		
		if (object.isPresent()) {
			if (logger.isTraceEnabled()) {
				logger.trace("{} : {} = {}", targetSetter.getName(), targetSetter.getType(), object.get());	
			}
			
			targetSetter.set(queue.peek(), object.get());
		}
	}

	@Override
	public void backward(ProjectionStack queue, ExtractionContext context) throws ProjectionError {

		if (!source.isWritable() || targetGetter == null || !source.isAnyTypeOf(context.accepted())) {
			return;
		}
		
		logger.debug("Backward {} : {}, depth = {}", targetGetter.getName(), targetGetter.getType(), queue.length());

		Optional<Object> object = targetGetter.get(queue.peek());

		if (object.isEmpty()) {
			return;
		}

		if (targetAdapter != null) {
			object = Optional.ofNullable(targetAdapter.backward(object.get(), context));
		}

		if (object.isPresent()) {
			source.write(queue, context, object.get());
		}
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

	public void setSource(Source source) {
		this.source = source;
	}
	
	@Override
	public boolean isVisible(int depth) {
		return visibleLevels == null || visibleLevels.isEmpty() || visibleLevels.contains(depth);
	}
	
	public void setVisibility(final Set<Integer> levels) {
		this.visibleLevels = levels;
	}
	
	public Source getSource() {
		return source;
	}
	
	public TargetAdapter getTargetAdapter() {
		return targetAdapter;
	}
	
	public Getter getTargetGetter() {
		return targetGetter;
	}
	
	public Setter getTargetSetter() {
		return targetSetter;
	}
}
