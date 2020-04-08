package com.apicatalog.projection.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.adapter.ProjectionAdapter;
import com.apicatalog.projection.adapter.ProjectionsAdapter;
import com.apicatalog.projection.adapter.SingleProjectionAdapter;
import com.apicatalog.projection.adapter.TargetTypeConverter;
import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.type.adapter.TypeAdapters;

public class TargetBuilder {

	final Logger logger = LoggerFactory.getLogger(TargetBuilder.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	ObjectType sourceType;
	
	ObjectType targetType;
	boolean targetReference;
		
	protected TargetBuilder() {
	}
	
	public static final TargetBuilder newInstance() {
		return new TargetBuilder();
	}
		
	public ProjectionAdapter build(ProjectionRegistry factory, TypeAdapters typeAdapters) {

		if (targetReference) {

			if (targetType.isCollection()) {
				return new ProjectionsAdapter(factory, typeAdapters, /*sourceType,*/ targetType);
			}

			return new SingleProjectionAdapter(factory, targetType);
		}

		return new TargetTypeConverter(typeAdapters, targetType);
	}
	
	public TargetBuilder source(ObjectType source) {
		this.sourceType = source;
		return this;
	}
	
	public TargetBuilder target(ObjectType target, boolean reference) {
		this.targetType = target;
		this.targetReference = reference;
		return this;
	}
}
