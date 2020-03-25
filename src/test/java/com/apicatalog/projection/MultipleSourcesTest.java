package com.apicatalog.projection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.converter.ConvertorError;
import com.apicatalog.projection.mapper.ProjectionMapper;
import com.apicatalog.projection.objects.ObjectBasicTypes;
import com.apicatalog.projection.objects.ObjectReference;
import com.apicatalog.projection.projections.ProjectionSourcesAndFunction;

public class MultipleSourcesTest {

	ProjectionFactory projections;
	ProjectionMapper mapper;
	
	@Before
	public void setup() {
		projections = new ProjectionFactory();
		mapper = new ProjectionMapper(projections);		
		
		projections.add(mapper.getMapping(ProjectionSourcesAndFunction.class));
	}
		
    @Test
    public void testComposition() throws ProjectionError, ConvertorError {
    	
    	ObjectBasicTypes oa = new ObjectBasicTypes();
    	oa.longValue = 123l;

    	ObjectReference oaa = new ObjectReference();
    	oaa.stringValue = "ABC"; 

    	ProjectionSourcesAndFunction pa = projections.compose(ProjectionSourcesAndFunction.class, oa, oaa);
    	
    	Assert.assertNotNull(pa);
    	Assert.assertEquals(oa.longValue + oaa.stringValue + "!@#", pa.longstring);
    	Assert.assertEquals(oaa.stringValue + oa.longValue, pa.stringlong);    	
    }
}
