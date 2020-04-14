package com.apicatalog.projection.annotated;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.projections.TestProjectionAF;

public class OneToOneWithFncTest {

	ProjectionRegistry projections;
	
	@Before
	public void setup() throws ProjectionError {
		projections = ProjectionRegistry.newInstance();
		
		projections.register(TestProjectionAF.class);
	}
			
    @Test
    public void testCompose() throws ProjectionError, ConverterError {
    	
    	BasicTypes oa = new BasicTypes();
    	oa.stringValue = "ABCDEF";
    	
    	TestProjectionAF pa = projections.compose(TestProjectionAF.class, oa);
    	
    	Assert.assertNotNull(pa);
    	
    	Assert.assertEquals(oa.stringValue, pa.originString);
    	Assert.assertEquals(oa.stringValue.concat("GHIJKL"), pa.modifiedString);
    	Assert.assertEquals(oa.stringValue.concat("GHIJKL").concat("MNOPQR"), pa.modified2xString);
    }
    
    @Test
    public void testExtract() throws ProjectionError, ConverterError {
    	
    	TestProjectionAF pa = new TestProjectionAF();
    	pa.originString = "ABCDEF";
    	pa.modifiedString = "ABCDEFGHIJKL";
    	pa.modified2xString = "ABCDEFGHIJKLMNOPQR";
    	
    	BasicTypes object = projections.extract(pa, BasicTypes.class);
    	
    	Assert.assertNotNull(object);
    	Assert.assertEquals("ABCDEF", object.stringValue);
    }
}
