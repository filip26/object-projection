package com.apicatalog.projection.property;

import java.util.Set;

import com.apicatalog.projection.ObjectConversion;
import com.apicatalog.projection.ObjectReduction;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.ProjectionQueue;
import com.apicatalog.projection.objects.access.Getter;
import com.apicatalog.projection.objects.access.Setter;
import com.apicatalog.projection.target.TargetAdapter;

public class SourcesProperty implements ProjectionProperty {

	Getter[] sourceGetters;
	Setter[] sourceSetters;
	
	ObjectConversion[][] sourceConversions;
	
	ObjectReduction reduction;
	
	ObjectConversion[] conversions;
	
	TargetAdapter targetAdapter;

	Getter targetGetter;
	Setter targetSetter;

	Set<Integer> visibleLevels;
	
	@Override
	public void forward(ProjectionQueue queue, ContextObjects context) throws ProjectionError {
		
//		final Object[] sourceObjects = new Object[sources.length];
//		
//		for (int i = 0; i < sources.length; i++) {
//			sourceObjects[i] = sources[i].read(context);
//		}
//		
//		Object object = reduction.reduce(sourceObjects);
//		
//		for (int i = 0; i < conversions.length; i++) {
//			object = conversions[i].forward(object);
//		}
//		
//		object = targetAdapter.construct(queue, object, context);
//		
//		targetSetter.set(queue.peek(), object);
	}

	@Override
	public void backward(ProjectionQueue queue, ContextObjects context) throws ProjectionError {

//		Object object = targetGetter.get(queue.peek());
//		
//		object = targetAdapter.deconstruct(object, context);
//		
//		for (int i=conversions.length; i > 0; --i) {
//			object = conversions[i].backward(object);
//		}
//		
//		Object[] sourceObjects = reduction.expand(object);
//		
//		for (int i = 0; i < sources.length; i++) {
//			sources[i].write(context, sourceObjects);
//		}
	}
	
	@Override
	public boolean isVisible(int depth) {
		return visibleLevels == null || visibleLevels.isEmpty() || visibleLevels.contains(depth);
	}
	
	public void setVisible(final Set<Integer> levels) {
		this.visibleLevels = levels;
	}


}
