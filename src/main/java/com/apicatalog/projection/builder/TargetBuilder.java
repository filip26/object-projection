package com.apicatalog.projection.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.target.TargetAdapter;
import com.apicatalog.projection.target.TargetProjectedCollectionConverter;
import com.apicatalog.projection.target.TargetProjectionConverter;
import com.apicatalog.projection.target.TargetTypeConverter;

public class TargetBuilder {

	final Logger logger = LoggerFactory.getLogger(TargetBuilder.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	ObjectType sourceType;
	ObjectType targetType;
		
	protected TargetBuilder() {
	}
	
	public static final TargetBuilder newInstance() {
		return new TargetBuilder();
	}
		
	public TargetAdapter build(ProjectionFactory factory, TypeAdapters typeAdapters) {

		if (targetType.isReference()) {

			if (targetType.isCollection()) {
				return new TargetProjectedCollectionConverter(factory, typeAdapters, sourceType, targetType);
			}

			return new TargetProjectionConverter(factory, sourceType, targetType);
		}

		return new TargetTypeConverter(typeAdapters, sourceType, targetType);
	}
	
	public TargetBuilder source(ObjectType source) {
		this.sourceType = source;
		return this;
	}
	
	public TargetBuilder target(ObjectType target) {
		this.targetType = target;
		return this;
	}
}
