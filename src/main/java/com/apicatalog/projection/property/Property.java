package com.apicatalog.projection.property;

import java.util.Collection;

import com.apicatalog.projection.source.SourceType;

public interface Property {

	Collection<SourceType> getSourceTypes();

//TODO
//	ObjectType getTargetType();
//	
	String getName();

}