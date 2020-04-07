package com.apicatalog.projection.objects.getter;

import java.util.Optional;

import com.apicatalog.projection.objects.ObjectType;

public interface Getter {

	Optional<Object> get(Object object);
	
	ObjectType getType();
	
	String getName();

}
