package com.apicatalog.projection;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.apicatalog.projection.fnc.InvertibleFunctionError;
import com.apicatalog.projection.scanner.ProjectionScanner;

@RunWith(JUnit4.class)
public class CollectionTest {

	ProjectionFactory projection;
	
	@Before
	public void setup() {
		ProjectionScanner scanner = new ProjectionScanner();
		
		ProjectionIndex index = new ProjectionIndex();
		index.add(scanner.scan(TestProjectionC1.class));
		index.add(scanner.scan(TestProjectionA.class));
		
		projection = new ProjectionFactory(index);
	}
	
    @Test
    public void testComposition() throws ProjectionError, InvertibleFunctionError {
    	
    	TestObjectA oa = new TestObjectA();
    	oa.booleanValue = true;
    	oa.doubleValue = 123.456d;
    	
    	TestCollectionObject oc = new TestCollectionObject();
    	oc.items = new ArrayList<>();
    	oc.items.add(oa);
    	
    	TestProjectionC1 ca = projection.compose(TestProjectionC1.class, oc);
    	
    	Assert.assertNotNull(ca);
    	Assert.assertNotNull(ca.items);    	
    	Assert.assertEquals(1, ca.items.size());
    	
    	TestProjectionA pa = ca.items.iterator().next();
    	
    	Assert.assertEquals(oa.booleanValue, pa.projectedBoolean);
    	Assert.assertEquals(oa.doubleValue, pa.projectedDouble);
    }
    
    @Test
    public void testDecomposition() throws ProjectionError, InvertibleFunctionError {
    	
    	TestProjectionC1 ca = new TestProjectionC1();
    	ca.items = new ArrayList<>();
    	
    	TestProjectionA pa1 = new TestProjectionA();
    	pa1.projectedString = "ABC";

    	TestProjectionA pa2 = new TestProjectionA();
    	pa2.projectedString = "XYZ";
    	
    	ca.items.add(pa1);
    	ca.items.add(pa2);

    	Object[] oo = projection.decompose(ca);
    	
    	Assert.assertNotNull(oo);
    	Assert.assertEquals(1, oo.length);
    	Assert.assertEquals(TestCollectionObject.class, oo[0].getClass());
    	
    	TestCollectionObject oc = (TestCollectionObject)oo[0];
    	Assert.assertNotNull(oc.items);
    	Assert.assertEquals(2, oc.items.size());
    	
    	Iterator<TestObjectA> it = oc.items.iterator();
    	
    	TestObjectA oa1 = it.next();
    	
    	Assert.assertEquals(pa1.projectedString, oa1.stringValue);
    	    	
    	TestObjectA oa2 = it.next();
    	
    	Assert.assertEquals(pa2.projectedString, oa2.stringValue);
    			
    }
}
