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
	public void setup() {
		projections = ProjectionRegistry.newInstance();
		
		projections.register(TestProjectionAF.class);
	}
			
    @Test
    public void testComposition() throws ProjectionError, ConverterError {
    	
    	BasicTypes oa = new BasicTypes();
    	oa.stringValue = "ABCDEF";
    	
    	TestProjectionAF pa = projections.compose(TestProjectionAF.class, oa);
    	
    	Assert.assertNotNull(pa);
    	
    	Assert.assertEquals(oa.stringValue, pa.originString);
    	Assert.assertEquals(oa.stringValue.concat("GHIJKL"), pa.modifiedString);
    	Assert.assertEquals(oa.stringValue.concat("GHIJKL").concat("MNOPQR"), pa.modified2xString);
    }
    
    @Test
    public void testDecomposition() throws ProjectionError, ConverterError {
    	
    	TestProjectionAF pa = new TestProjectionAF();
    	pa.originString = "ABCDEF";
    	pa.modifiedString = "ABCDEFGHIJKL";
    	pa.modified2xString = "ABCDEFGHIJKLMNOPQR";
    	
    	Object[] oo = projections.decompose(pa);
    	
    	Assert.assertNotNull(oo);
    	Assert.assertEquals(1, oo.length);
    	Assert.assertEquals(BasicTypes.class, oo[0].getClass());
    	
    	BasicTypes oa = (BasicTypes)oo[0];

    	Assert.assertEquals("ABCDEF", oa.stringValue);
    }
}
