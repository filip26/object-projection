package com.apicatalog.projection.object.getter;

import java.util.Optional;

import com.apicatalog.projection.object.ObjectError;
import com.apicatalog.projection.object.ObjectType;

public interface Getter {

	Optional<Object> get(Object object) throws ObjectError;
	
	ObjectType getType();
	
	String getName();

}
