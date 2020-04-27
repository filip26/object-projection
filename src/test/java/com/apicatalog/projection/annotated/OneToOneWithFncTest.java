package com.apicatalog.projection.annotated;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.ExtractionError;
import com.apicatalog.projection.Registry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.projections.TestProjectionAF;

public class OneToOneWithFncTest {

	Registry projections;
	
	@Before
	public void setup() throws CompositionError, ProjectionError {
		projections = Registry.newInstance();
		
		projections.register(TestProjectionAF.class);
	}
			
    @Test
    public void testCompose() throws CompositionError, ConverterError {
    	
    	BasicTypes oa = new BasicTypes();
    	oa.stringValue = "ABCDEF";
    	
    	TestProjectionAF pa = projections.get(TestProjectionAF.class).compose(oa);
    	
    	Assert.assertNotNull(pa);
    	
    	Assert.assertEquals(oa.stringValue, pa.originString);
    	Assert.assertEquals(oa.stringValue.concat("GHIJKL"), pa.modifiedString);
    	Assert.assertEquals(oa.stringValue.concat("GHIJKL").concat("MNOPQR"), pa.modified2xString);
    }
    
    @Test
    public void testExtract() throws ExtractionError, ConverterError {
    	
    	TestProjectionAF pa = new TestProjectionAF();
    	pa.originString = "ABCDEF";
    	pa.modifiedString = "ABCDEFGHIJKL";
    	pa.modified2xString = "ABCDEFGHIJKLMNOPQR";
    	
    	BasicTypes object = projections.get(TestProjectionAF.class).extract(pa, BasicTypes.class).orElse(null);
    	
    	Assert.assertNotNull(object);
    	Assert.assertEquals("ABCDEF", object.stringValue);
    }
}
