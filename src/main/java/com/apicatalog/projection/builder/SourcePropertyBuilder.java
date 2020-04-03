package com.apicatalog.projection.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.objects.getter.Getter;
import com.apicatalog.projection.objects.setter.Setter;
import com.apicatalog.projection.property.SourceProperty;
import com.apicatalog.projection.source.Source;

public class SourcePropertyBuilder {

	final Logger logger = LoggerFactory.getLogger(SourcePropertyBuilder.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	Source source;
	
	AccessMode mode;
	
	ObjectType targetType;
	
	Getter targetGetter;
	Setter targetSetter;

	protected SourcePropertyBuilder() {

	}
	
	public static final SourcePropertyBuilder newInstance() {
		return new SourcePropertyBuilder();
	}
			
	public SourceProperty build(ProjectionFactory factory, TypeAdapters typeAdapters) {
		

		//TODO check target setter getter presence
		
		if (source == null) {
			logger.warn(SOURCE_IS_MISSING, targetSetter != null ? targetSetter.getName() : targetGetter.getName());
			return null;
		}

		final SourceProperty property = new SourceProperty();

		property.setSource(source);
		
		// set access mode
		switch (mode) {
		case READ_ONLY:
			property.setTargetSetter(targetSetter);
			break;
			
		case WRITE_ONLY:
			property.setTargetGetter(targetGetter);
			break;
		
		case READ_WRITE:
			property.setTargetSetter(targetSetter);			
			property.setTargetGetter(targetGetter);
			break;
		}			

		property.setTargetAdapter(
				TargetBuilder.newInstance()
					.source(source.getTargetType())
					.target(targetSetter.getType())
					.build(factory, typeAdapters)
					);

		return property;		
	}

	public SourcePropertyBuilder source(Source source) {
		this.source = source;
		return this;
	}
	
	public SourcePropertyBuilder mode(AccessMode mode) {
		this.mode = mode;
		return this;
	}
	
	public SourcePropertyBuilder targetType(ObjectType targetType) {
		this.targetType = targetType;
		return this;
	}

	public SourcePropertyBuilder targetGetter(Getter getter) {
		this.targetGetter = getter;
		return this;
	}

	public SourcePropertyBuilder targetSetter(Setter setter) {
		this.targetSetter = setter;
		return this;
	}

}
