package com.apicatalog.projection;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.mapper.ProjectionMapper;
import com.apicatalog.projection.objects.ObjectBasicTypes;
import com.apicatalog.projection.objects.ObjectsCollection;
import com.apicatalog.projection.projections.NameOverrideTo;
import com.apicatalog.projection.projections.ProjectionsCollectionTo;

public class CollectionTest {

	ProjectionFactory projections;
	ProjectionMapper mapper;
	
	@Before
	public void setup() {
		projections = new ProjectionFactory();
		mapper = new ProjectionMapper(projections);	
		
		projections.add(mapper.getMapping(ProjectionsCollectionTo.class));
		projections.add(mapper.getMapping(NameOverrideTo.class));
	}
	
    @Test
    public void testComposition() throws ProjectionError, ConverterError {
    	
    	ObjectBasicTypes oa = new ObjectBasicTypes();
    	oa.booleanValue = true;
    	oa.doubleValue = 123.456d;
    	
    	ObjectsCollection oc = new ObjectsCollection();
    	oc.items = new ArrayList<>();
    	oc.items.add(oa);
    	
    	ProjectionsCollectionTo ca = projections.compose(ProjectionsCollectionTo.class, oc);
    	
    	Assert.assertNotNull(ca);
    	Assert.assertNotNull(ca.items);    	
    	Assert.assertEquals(1, ca.items.size());
    	
    	NameOverrideTo pa = ca.items.iterator().next();
    	
    	Assert.assertEquals(oa.booleanValue, pa.projectedBoolean);
    	Assert.assertEquals(oa.doubleValue, pa.projectedDouble);
    }
    
    @Test
    public void testDecomposition() throws ProjectionError, ConverterError {
    	
    	ProjectionsCollectionTo ca = new ProjectionsCollectionTo();
    	ca.items = new ArrayList<>();
    	
    	NameOverrideTo pa1 = new NameOverrideTo();
    	pa1.projectedString = "ABC";

    	NameOverrideTo pa2 = new NameOverrideTo();
    	pa2.projectedString = "XYZ";
    	
    	ca.items.add(pa1);
    	ca.items.add(pa2);

    	Object[] oo = projections.decompose(ca);
    	
    	Assert.assertNotNull(oo);
    	Assert.assertEquals(1, oo.length);
    	Assert.assertEquals(ObjectsCollection.class, oo[0].getClass());
    	
    	ObjectsCollection oc = (ObjectsCollection)oo[0];
    	Assert.assertNotNull(oc.items);
    	Assert.assertEquals(2, oc.items.size());
    	
    	Iterator<ObjectBasicTypes> it = oc.items.iterator();
    	
    	ObjectBasicTypes oa1 = it.next();
    	
    	Assert.assertEquals(pa1.projectedString, oa1.stringValue);
    	    	
    	ObjectBasicTypes oa2 = it.next();
    	
    	Assert.assertEquals(pa2.projectedString, oa2.stringValue);
    			
    }
}
