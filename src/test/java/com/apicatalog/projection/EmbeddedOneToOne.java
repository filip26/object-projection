package com.apicatalog.projection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.apicatalog.projection.ifnc.InvertibleFunctionError;
import com.apicatalog.projection.mapping.MappingIndex;
import com.apicatalog.projection.scanner.ProjectionScanner;

@RunWith(JUnit4.class)
public class EmbeddedOneToOne {

	ProjectionFactory projection;
	
	@Before
	public void setup() {
		ProjectionScanner scanner = new ProjectionScanner();
		
		MappingIndex index = new MappingIndex();
		index.add(scanner.scan(TestProjectionAA.class));
		index.add(scanner.scan(TestProjectionAI.class));
		index.add(scanner.scan(TestProjectionA.class));
		
		projection = new ProjectionFactory(index);
	}
	
    @Test
    public void testComposition() throws ProjectionError, InvertibleFunctionError {
    	
    	TestObjectA oa = new TestObjectA();
    	oa.longValue = 123456l;

    	TestObjectA oa2 = new TestObjectA();
    	oa2.longValue = 987654l;

    	TestObjectAA oaa = new TestObjectAA();
    	oaa.stringValue = "inherit me";
    	oaa.objectA = oa;

    	TestProjectionAA pa = projection.compose(TestProjectionAA.class, oaa, oa2);
    	
    	Assert.assertNotNull(pa);
    	Assert.assertNotNull(pa.ai);
    	
    	Assert.assertEquals(oa.longValue, pa.ai.projectedLong);
    	Assert.assertEquals(oaa.stringValue, pa.ai.inheritedValue);
    }
    
    @Test
    public void testDecomposition() throws ProjectionError, InvertibleFunctionError {
    	
    	TestProjectionAI pa = new TestProjectionAI();
    	pa.projectedLong = 123456l;
    	pa.inheritedValue = "inherit me";

    	TestProjectionAA paa = new TestProjectionAA();
    	paa.ai = pa;

    	Object[] oo = projection.decompose(paa);
    	
    	Assert.assertNotNull(oo);
    	Assert.assertEquals(1, oo.length);
    	Assert.assertEquals(TestObjectAA.class, oo[0].getClass());
    	
    	TestObjectAA oaa = (TestObjectAA)oo[0];
    	Assert.assertNotNull(oaa.objectA);

    	Assert.assertEquals(paa.ai.projectedLong, oaa.objectA.longValue);
    	Assert.assertEquals(paa.ai.inheritedValue, oaa.stringValue);
    }
}

