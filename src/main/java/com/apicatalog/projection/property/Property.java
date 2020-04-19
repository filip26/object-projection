package com.apicatalog.projection.property;

import java.util.Collection;

import com.apicatalog.projection.source.SourceType;

public interface Property {

	Collection<SourceType> getSourceTypes();
	
	Collection<String> getDependencies();

//TODO
//	ObjectType getTargetType();
//	
	String getName();

}