package com.apicatalog.projection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.converter.InvertibleFunctionError;
import com.apicatalog.projection.mapper.ProjectionMapper;
import com.apicatalog.projection.mapping.MappingIndex;
import com.apicatalog.projection.objects.ObjectBasicTypes;
import com.apicatalog.projection.projections.TestProjectionAF;

public class OneToOneWithFncTest {

	ProjectionFactory projection;
	
	@Before
	public void setup() {
		ProjectionMapper scanner = new ProjectionMapper();
		
		MappingIndex index = new MappingIndex();
		index.add(scanner.getMapping(TestProjectionAF.class));
		
		projection = new ProjectionFactory(index);
	}
	
    @Test
    public void testComposition() throws ProjectionError, InvertibleFunctionError {
    	
    	ObjectBasicTypes oa = new ObjectBasicTypes();
    	oa.stringValue = "ABCDEF";
    	
    	TestProjectionAF pa = projection.compose(TestProjectionAF.class, oa);
    	
    	Assert.assertNotNull(pa);
    	
    	Assert.assertEquals(oa.stringValue, pa.originString);
    	Assert.assertEquals(oa.stringValue.concat("GHIJKL"), pa.modifiedString);
    	Assert.assertEquals(oa.stringValue.concat("GHIJKL").concat("MNOPQR"), pa.modified2xString);
    }
    
    @Test
    public void testDecomposition() throws ProjectionError, InvertibleFunctionError {
    	
    	TestProjectionAF pa = new TestProjectionAF();
    	pa.originString = "ABCDEF";
    	pa.modifiedString = "ABCDEFGHIJKL";
    	pa.modified2xString = "ABCDEFGHIJKLMNOPQR";
    	
    	Object[] oo = projection.decompose(pa);
    	
    	Assert.assertNotNull(oo);
    	Assert.assertEquals(1, oo.length);
    	Assert.assertEquals(ObjectBasicTypes.class, oo[0].getClass());
    	
    	ObjectBasicTypes oa = (ObjectBasicTypes)oo[0];

    	Assert.assertEquals("ABCDEF", oa.stringValue);
    }
}
