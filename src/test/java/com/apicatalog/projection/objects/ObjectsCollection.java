package com.apicatalog.projection.objects;

import java.util.Collection;

public class ObjectsCollection {

	public String id;
	
	public Collection<BasicTypes> items;

	@Override
	public String toString() {
		return "ObjectsCollection [id=" + id + ", items=" + items + "]";
	}
}
