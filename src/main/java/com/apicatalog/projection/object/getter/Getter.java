package com.apicatalog.projection.object.getter;

import java.util.Optional;

import com.apicatalog.projection.object.ObjectType;

public interface Getter {

	Optional<Object> get(Object object);
	
	ObjectType getType();
	
	String getName();

}
