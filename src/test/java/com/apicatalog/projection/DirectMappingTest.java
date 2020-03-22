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
public class DirectMappingTest {

	ProjectionFactory projection;
	
	@Before
	public void setup() {
		ProjectionScanner scanner = new ProjectionScanner();
		
		ProjectionIndex index = new ProjectionIndex();
		index.add(scanner.scan(TestProjectionDA.class));
		
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
    	
    	TestProjectionDA pa = projection.compose(TestProjectionDA.class, oa);
    	
    	Assert.assertNotNull(pa);
    	
    	Assert.assertEquals(oa.stringValue, pa.stringValue);
    	Assert.assertEquals(oa.booleanValue, pa.booleanValue);
    	Assert.assertEquals(oa.doubleValue, pa.doubleValue);
    	Assert.assertEquals(oa.instantValue, pa.instantValue);
    	Assert.assertEquals(oa.longValue, pa.longValue);
    }
    
    @Test
    public void testDecomposition() throws ProjectionError, InvertibleFunctionError {
    	
    	TestProjectionDA pa = new TestProjectionDA();
    	pa.booleanValue = true;
    	pa.doubleValue= 123.456d;
    	pa.instantValue = Instant.now();
    	pa.longValue = 123456l;
    	pa.stringValue = "ABCDEF";
    	
    	Object[] oo = projection.decompose(pa);
    	
    	Assert.assertNotNull(oo);
    	Assert.assertEquals(1, oo.length);
    	Assert.assertEquals(TestObjectA.class, oo[0].getClass());
    	
    	TestObjectA oa = (TestObjectA)oo[0];

    	Assert.assertEquals(pa.stringValue, oa.stringValue);
    	Assert.assertEquals(pa.booleanValue, oa.booleanValue);
    	Assert.assertEquals(pa.doubleValue, oa.doubleValue);
    	Assert.assertEquals(pa.instantValue, oa.instantValue);
    	Assert.assertEquals(pa.longValue, oa.longValue);
    }
}
