package com.apicatalog.projection;

import java.time.Instant;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.apicatalog.projection.ifnc.InvertibleFunctionError;
import com.apicatalog.projection.scanner.ProjectionScanner;

@RunWith(JUnit4.class)
public class PropertyNameOverrideTest {

	ProjectionFactory projection;
	
	@Before
	public void setup() {
		ProjectionScanner scanner = new ProjectionScanner();
		
		MetaProjectionIndex index = new MetaProjectionIndex();
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
    	
    	TestProjectionA pa = projection.compose(TestProjectionA.class, oa);
    	
    	Assert.assertNotNull(pa);
    	
    	Assert.assertEquals(oa.stringValue, pa.projectedString);
    	Assert.assertEquals(oa.booleanValue, pa.projectedBoolean);
    	Assert.assertEquals(oa.doubleValue, pa.projectedDouble);
    	Assert.assertEquals(oa.instantValue, pa.projectedInstant);
    	Assert.assertEquals(oa.longValue, pa.projectedLong);
    }
    
    @Test
    public void testDecomposition() throws ProjectionError, InvertibleFunctionError {
    	
    	TestProjectionA pa = new TestProjectionA();
    	pa.projectedBoolean = true;
    	pa.projectedDouble = 123.456d;
    	pa.projectedInstant = Instant.now();
    	pa.projectedLong = 123456l;
    	pa.projectedString = "ABCDEF";
    	
    	Object[] oo = projection.decompose(pa);
    	
    	Assert.assertNotNull(oo);
    	Assert.assertEquals(1, oo.length);
    	Assert.assertEquals(TestObjectA.class, oo[0].getClass());
    	
    	TestObjectA oa = (TestObjectA)oo[0];

    	Assert.assertEquals(pa.projectedString, oa.stringValue);
    	Assert.assertEquals(pa.projectedBoolean, oa.booleanValue);
    	Assert.assertEquals(pa.projectedDouble, oa.doubleValue);
    	Assert.assertEquals(pa.projectedInstant, oa.instantValue);
    	Assert.assertEquals(pa.projectedLong, oa.longValue);
    }
}
