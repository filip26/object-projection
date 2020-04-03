package com.apicatalog.projection.builder;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.beans.FieldGetter;
import com.apicatalog.projection.beans.FieldSetter;
import com.apicatalog.projection.beans.Getter;
import com.apicatalog.projection.beans.Setter;
import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.property.SourceProperty;
import com.apicatalog.projection.source.Source;

public class SourcePropertyBuilder {

	final Logger logger = LoggerFactory.getLogger(SourcePropertyBuilder.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	Field field;
	
	Source source;
	
	AccessMode mode;
	
	ObjectType targetType;
	
	protected SourcePropertyBuilder() {

	}
	
	public static final SourcePropertyBuilder newInstance() {
		return new SourcePropertyBuilder();
	}
			
	public SourceProperty build(ProjectionFactory factory, TypeAdapters typeAdapters) {
		
		final SourceProperty property = new SourceProperty();
							
		if (source == null) {
			logger.warn(SOURCE_IS_MISSING, field.getName());
			return null;
		}

		property.setSource(source);

		// extract setter/getter
		final Getter targetGetter = FieldGetter.from(field, targetType);
		final Setter targetSetter = FieldSetter.from(field, targetType);

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

//		property.setTargetAdapter(
//				TargetBuilder.newInstance()
//					.source(source.getType())
//					.target(ObjectType.of(targetSetter.getValueClass(), targetSetter.getValueComponentClass()))
//					.build(factory, typeAdapters)
//					);

		return property;		
	}

	public SourcePropertyBuilder source(Source source) {
		this.source = source;
		return this;
	}
	
	public SourcePropertyBuilder source(AccessMode mode) {
		this.mode = mode;
		return this;
	}

}
