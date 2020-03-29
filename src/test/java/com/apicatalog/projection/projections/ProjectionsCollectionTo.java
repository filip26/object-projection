package com.apicatalog.projection.projections;

import java.util.Collection;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.objects.ObjectsCollection;

@Projection(ObjectsCollection.class)
public class ProjectionsCollectionTo {

	public Collection<NameOverrideTo> items;

}
