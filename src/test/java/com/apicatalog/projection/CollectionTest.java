package com.apicatalog.projection;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.converter.ConvertorError;
import com.apicatalog.projection.mapper.ProjectionMapper;
import com.apicatalog.projection.objects.ObjectBasicTypes;
import com.apicatalog.projection.objects.TestCollectionObject;
import com.apicatalog.projection.projections.ProjectionBasicTypesNameOverride;
import com.apicatalog.projection.projections.TestProjectionC1;

public class CollectionTest {

	ProjectionFactory projections;
	ProjectionMapper mapper;
	
	@Before
	public void setup() {
		projections = new ProjectionFactory();
		mapper = new ProjectionMapper(projections);	
		
		projections.add(mapper.getMapping(TestProjectionC1.class));
		projections.add(mapper.getMapping(ProjectionBasicTypesNameOverride.class));
	}
	
    @Test
    public void testComposition() throws ProjectionError, ConvertorError {
    	
    	ObjectBasicTypes oa = new ObjectBasicTypes();
    	oa.booleanValue = true;
    	oa.doubleValue = 123.456d;
    	
    	TestCollectionObject oc = new TestCollectionObject();
    	oc.items = new ArrayList<>();
    	oc.items.add(oa);
    	
    	TestProjectionC1 ca = projections.compose(TestProjectionC1.class, oc);
    	
    	Assert.assertNotNull(ca);
    	Assert.assertNotNull(ca.items);    	
    	Assert.assertEquals(1, ca.items.size());
    	
    	ProjectionBasicTypesNameOverride pa = ca.items.iterator().next();
    	
    	Assert.assertEquals(oa.booleanValue, pa.projectedBoolean);
    	Assert.assertEquals(oa.doubleValue, pa.projectedDouble);
    }
    
//    @Test
//    public void testDecomposition() throws ProjectionError, ConvertorError {
//    	
//    	TestProjectionC1 ca = new TestProjectionC1();
//    	ca.items = new ArrayList<>();
//    	
//    	ProjectionBasicTypesNameOverride pa1 = new ProjectionBasicTypesNameOverride();
//    	pa1.projectedString = "ABC";
//
//    	ProjectionBasicTypesNameOverride pa2 = new ProjectionBasicTypesNameOverride();
//    	pa2.projectedString = "XYZ";
//    	
//    	ca.items.add(pa1);
//    	ca.items.add(pa2);
//
//    	Object[] oo = projections.decompose(ca);
//    	
//    	Assert.assertNotNull(oo);
//    	Assert.assertEquals(1, oo.length);
//    	Assert.assertEquals(TestCollectionObject.class, oo[0].getClass());
//    	
//    	TestCollectionObject oc = (TestCollectionObject)oo[0];
//    	Assert.assertNotNull(oc.items);
//    	Assert.assertEquals(2, oc.items.size());
//    	
//    	Iterator<ObjectBasicTypes> it = oc.items.iterator();
//    	
//    	ObjectBasicTypes oa1 = it.next();
//    	
//    	Assert.assertEquals(pa1.projectedString, oa1.stringValue);
//    	    	
//    	ObjectBasicTypes oa2 = it.next();
//    	
//    	Assert.assertEquals(pa2.projectedString, oa2.stringValue);
//    			
//    }
}
