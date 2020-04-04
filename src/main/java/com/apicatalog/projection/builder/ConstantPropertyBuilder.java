package com.apicatalog.projection.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.objects.setter.Setter;
import com.apicatalog.projection.property.ConstantProperty;
import com.apicatalog.projection.property.ProjectionProperty;

public class ConstantPropertyBuilder {

	final Logger logger = LoggerFactory.getLogger(ConstantPropertyBuilder.class);

	String[] constants;
	
	Setter targetSetter;
	
	protected ConstantPropertyBuilder() {
	}
	
	public static final ConstantPropertyBuilder newInstance() {
		return new ConstantPropertyBuilder();
	}
	
	public ProjectionProperty build(ProjectionRegistry registry, TypeAdapters typeAdapters) {
		
		final ConstantProperty property = new ConstantProperty();
		
		// set constant values
		property.setConstants(constants);
		
		property.setTargetAdapter(
				TargetBuilder.newInstance()
					.source(ObjectType.of(String[].class))
					.target(targetSetter.getType())
					.build(registry, typeAdapters)
					);

		// set target setter
		property.setTargetSetter(targetSetter);
		
		return property;
	}	
	
	public ConstantPropertyBuilder targetSetter(Setter targetSetter) {
		this.targetSetter = targetSetter;
		return this;
	}
	
	public ConstantPropertyBuilder constants(String[] constants) {
		this.constants = constants;
		return this;
	}
}
