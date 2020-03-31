package com.apicatalog.projection.mapper.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.mapping.SourceMapping;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.Path;

public class ConstantMappingImpl implements SourceMapping {

	final Logger logger = LoggerFactory.getLogger(ConstantMappingImpl.class);
	
	final TypeAdapters typeAdapters;
	
	Class<?> targetClass;
	Class<?> targetComponentClass;
	
	String[] constants;
		
	public ConstantMappingImpl(final TypeAdapters typeAdapters) {
		this.typeAdapters = typeAdapters;
	}
	
	@Override
	public Object compose(Path path, ContextObjects contextObjects) throws ProjectionError {

		logger.debug("Compose path = {}, constant = {}", path.length(), constants);

		return typeAdapters.convert(targetClass, targetComponentClass, constants);
	}
	
	@Override
	public void decompose(Path path, Object object, ContextObjects contextObjects) throws ProjectionError {
		// nothing to decompose, it's constant
	}
	
	@Override
	public Class<?> getTargetClass() {
		return targetClass;
	}
	
	@Override
	public Class<?> getTargetComponentClass() {
		return targetComponentClass;
	}
	
	public void setTargetClass(Class<?> targetClass) {
		this.targetClass = targetClass;
	}
	
	public void setTargetComponentClass(Class<?> targetComponentClass) {
		this.targetComponentClass = targetComponentClass;
	}
	
	public String[] getConstants() {
		return constants;
	}
	
	public void setConstants(String[] constants) {
		this.constants = constants;
	}
	
	@Override
	public AccessMode getAccessMode() {
		return AccessMode.READ_ONLY;
	}
}
