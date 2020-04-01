package com.apicatalog.projection;

public interface ObjectReduction {

	public Class<?> getSourceClass();

	public Object reduce(Object...sourceObjects);

	public Object[] expand(Object object);

}
