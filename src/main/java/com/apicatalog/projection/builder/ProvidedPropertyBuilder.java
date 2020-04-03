package com.apicatalog.projection.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.objects.getter.Getter;
import com.apicatalog.projection.objects.setter.Setter;
import com.apicatalog.projection.property.ProjectionProperty;
import com.apicatalog.projection.property.ProvidedObjectProperty;
import com.apicatalog.projection.property.ProvidedProjectionProperty;

public class ProvidedPropertyBuilder {

	final Logger logger = LoggerFactory.getLogger(ProvidedPropertyBuilder.class);

	Getter targetGetter;
	Setter targetSetter;
	
	AccessMode mode;
	
	String qualifier;
	
	boolean optional;
	
	protected ProvidedPropertyBuilder() {
		this.mode = AccessMode.READ_WRITE;
		this.optional = false;
	}
	
	public static final ProvidedPropertyBuilder newInstance() {
		return new ProvidedPropertyBuilder();
	}
	
	public ProjectionProperty build(ProjectionFactory factory, TypeAdapters typeAdapters) {

		if (targetSetter == null && targetGetter == null) {
			return null;
		}
		
		final ObjectType targetType = targetGetter != null ? targetGetter.getType() : targetSetter.getType(); 
		
		if (targetType.isReference() && !targetType.isCollection()) {
			return buildReference(factory, typeAdapters);
		}
		
		final ProvidedObjectProperty property = new ProvidedObjectProperty();
				
		// set access mode
		switch (mode) {
		case READ_ONLY:
			property.setTargetSetter(targetSetter);
			break;
			
		case WRITE_ONLY:
			property.setTargetGetter(targetGetter);
			break;
		
		case READ_WRITE:
			property.setTargetGetter(targetGetter);
			property.setTargetSetter(targetSetter);
			break;
		}			

		// set qualifier
		property.setSourceObjectQualifier(qualifier);

		property.setOptional(optional);

		property.setTargetAdapter(
					TargetBuilder.newInstance()
						.source(targetSetter != null ? targetSetter.getType() : targetGetter.getType())
						.target(targetSetter != null ? targetSetter.getType() : targetGetter.getType())
						.build(factory, typeAdapters)
						);

		return property;
	}
	
	ProjectionProperty buildReference(ProjectionFactory factory, TypeAdapters typeAdapters) {
		
		final ProvidedProjectionProperty property = new ProvidedProjectionProperty(factory);

		// set access mode
		switch (mode) {
		case READ_ONLY:
			property.setTargetSetter(targetSetter);
			break;
			
		case WRITE_ONLY:
			property.setTargetGetter(targetGetter);
			break;
		
		case READ_WRITE:
			property.setTargetGetter(targetGetter);
			property.setTargetSetter(targetSetter);
			break;
		}	

		// set qualifier
		property.setSourceObjectQualifier(qualifier);

		property.setOptional(optional);

		return property;
	}
	
	public ProvidedPropertyBuilder mode(AccessMode mode) {
		this.mode = mode;
		return this;
	}
	
	public ProvidedPropertyBuilder targetGetter(Getter getter) {
		this.targetGetter = getter;
		return this;
	}

	public ProvidedPropertyBuilder targetSetter(Setter setter) {
		this.targetSetter = setter;
		return this;
	}

	public ProvidedPropertyBuilder optional(boolean optional) {
		this.optional = optional;
		return this;
	}

	public ProvidedPropertyBuilder qualifier(String qualifier) {
		this.qualifier = qualifier;
		return this;
	}

}
