package com.apicatalog.projection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.converter.InvertibleFunctionError;
import com.apicatalog.projection.mapper.ProjectionMapper;
import com.apicatalog.projection.mapping.MappingIndex;
import com.apicatalog.projection.objects.ObjectBasicTypes;
import com.apicatalog.projection.objects.ObjectReference;
import com.apicatalog.projection.projections.ProjectionSourcesAndFunction;

public class MultipleSourcesTest {

	ProjectionFactory projection;
	
	@Before
	public void setup() {
		ProjectionMapper scanner = new ProjectionMapper();
		
		MappingIndex index = new MappingIndex();
		index.add(scanner.getMapping(ProjectionSourcesAndFunction.class));
		
		projection = new ProjectionFactory(index);
	}
	
    @Test
    public void testComposition() throws ProjectionError, InvertibleFunctionError {
    	
    	ObjectBasicTypes oa = new ObjectBasicTypes();
    	oa.longValue = 123l;

    	ObjectReference oaa = new ObjectReference();
    	oaa.stringValue = "ABC"; 

    	ProjectionSourcesAndFunction pa = projection.compose(ProjectionSourcesAndFunction.class, oa, oaa);
    	
    	Assert.assertNotNull(pa);
    	Assert.assertEquals(oa.longValue + oaa.stringValue + "!@#", pa.longstring);
    	Assert.assertEquals(oaa.stringValue + oa.longValue, pa.stringlong);    	
    }
    
    @Test
    public void testDecomposition() throws ProjectionError, InvertibleFunctionError {
    	
    	ProjectionSourcesAndFunction pa = new ProjectionSourcesAndFunction();
    	pa.longstring = "123ABC";
    	pa.stringlong = "ABC123";
    	
    	Object[] oo = projection.decompose(pa);
    	
    	Assert.assertNotNull(oo);
    	Assert.assertEquals(2, oo.length);
    	Assert.assertNotNull(oo[0]);
    	Assert.assertEquals(ObjectBasicTypes.class, oo[0].getClass());
    	Assert.assertNotNull(oo[1]);
    	Assert.assertEquals(ObjectReference.class, oo[1].getClass());
    }
}
