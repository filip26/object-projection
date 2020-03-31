package com.apicatalog.projection.mapping;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.Path;

public interface SourceMapping {

	Class<?> getTargetClass();
	Class<?> getTargetComponentClass();

	Object compose(Path path, ContextObjects context) throws ProjectionError;

	void decompose(Object object, ContextObjects context) throws ProjectionError;
	
	AccessMode getAccessMode();

}
