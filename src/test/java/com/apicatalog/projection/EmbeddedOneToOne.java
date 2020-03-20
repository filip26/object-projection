package com.apicatalog.projection;

import java.time.Instant;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.apicatalog.projection.fnc.InvertibleFunctionError;
import com.apicatalog.projection.scanner.ProjectionScanner;

@RunWith(JUnit4.class)
public class EmbeddedOneToOne {

	ProjectionFactory projection;
	
	@Before
	public void setup() {
		ProjectionScanner scanner = new ProjectionScanner();
		
		ProjectionIndex index = new ProjectionIndex();
		index.add(scanner.scan(TestProjectionAA.class));
		index.add(scanner.scan(TestProjectionA.class));
		
		projection = new ProjectionFactory(index);
	}
	
    @Test
    public void testComposition() throws ProjectionError, InvertibleFunctionError {
    	
    	TestObjectA oa = new TestObjectA();
    	oa.booleanValue = true;
    	oa.doubleValue = 123.456d;
    	oa.instantValue = Instant.now();
    	oa.longValue = 123456l;
    	oa.stringValue = "ABCDEF";

    	TestObjectAA oaa = new TestObjectAA();
    	oaa.objectA = oa;

    	TestProjectionAA pa = projection.compose(TestProjectionAA.class, oaa);
    	
    	Assert.assertNotNull(pa);
    	Assert.assertNotNull(pa.a);
    	
    	Assert.assertEquals(oa.stringValue, pa.a.projectedString);
    	Assert.assertEquals(oa.booleanValue, pa.a.projectedBoolean);
    	Assert.assertEquals(oa.doubleValue, pa.a.projectedDouble);
    	Assert.assertEquals(oa.instantValue, pa.a.projectedInstant);
    	Assert.assertEquals(oa.longValue, pa.a.projectedLong);
    }
    
    @Test
    public void testDecomposition() throws ProjectionError, InvertibleFunctionError {
    	
    	TestProjectionA pa = new TestProjectionA();
    	pa.projectedBoolean = true;
    	pa.projectedDouble = 123.456d;
    	pa.projectedInstant = Instant.now();
    	pa.projectedLong = 123456l;
    	pa.projectedString = "ABCDEF";

    	TestProjectionAA paa = new TestProjectionAA();
    	paa.a = pa;

    	Object[] oo = projection.decompose(paa);
    	
    	Assert.assertNotNull(oo);
    	Assert.assertEquals(1, oo.length);
    	Assert.assertEquals(TestObjectAA.class, oo[0].getClass());
    	
    	TestObjectAA oaa = (TestObjectAA)oo[0];
    	Assert.assertNotNull(oaa.objectA);

    	Assert.assertEquals(paa.a.projectedString, oaa.objectA.stringValue);
    	Assert.assertEquals(paa.a.projectedBoolean, oaa.objectA.booleanValue);
    	Assert.assertEquals(paa.a.projectedDouble, oaa.objectA.doubleValue);
    	Assert.assertEquals(paa.a.projectedInstant, oaa.objectA.instantValue);
    	Assert.assertEquals(paa.a.projectedLong, oaa.objectA.longValue);
    }
}
