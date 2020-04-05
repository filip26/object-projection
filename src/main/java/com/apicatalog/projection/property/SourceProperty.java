package com.apicatalog.projection.property;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.ProjectionContext;
import com.apicatalog.projection.objects.ProjectionQueue;
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
	public void forward(ProjectionQueue queue, ProjectionContext context) throws ProjectionError {

		if (!source.isReadable() || targetSetter == null) {
			return;
		}
		
		logger.debug("Forward {} : {}, depth = {}", targetSetter.getName(), targetSetter.getType(), queue.length());

		// get source value
		Object object = source.read(queue, context);
		
		if (object == null) {
			return;
		}

		object = targetAdapter.forward(queue, object, context);

		logger.trace("{} : {} = {}", targetSetter.getName(), targetSetter.getType(), object);

		targetSetter.set(queue.peek(), object);
		
	}

	@Override
	public void backward(ProjectionQueue queue, ProjectionContext context) throws ProjectionError {

		if (!source.isWritable() || targetGetter == null) {
			return;
		}
		
		logger.debug("Backward {} : {}, depth = {}", targetGetter.getName(), targetGetter.getType(), queue.length());

		Object object = targetGetter.get(queue.peek());
		
		if (object == null) {
			return;
		}

		object = targetAdapter.backward(object, context);

		source.write(queue, object, context);
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
	
	public void setVisible(final Set<Integer> levels) {
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
