package com.apicatalog.projection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.converter.ConvertorError;
import com.apicatalog.projection.mapper.ProjectionMapper;
import com.apicatalog.projection.objects.ObjectBasicTypes;
import com.apicatalog.projection.objects.ObjectReference;
import com.apicatalog.projection.projections.ProjectionUrlPatternFnc;

public class UrlPatternFncTest {

	ProjectionFactory projections;
	ProjectionMapper mapper;
	
	@Before
	public void setup() {
		projections = new ProjectionFactory();
		mapper = new ProjectionMapper(projections);	
		
		projections.add(mapper.getMapping(ProjectionUrlPatternFnc.class));
	}
		
    @Test
    public void testComposition() throws ProjectionError, ConvertorError {
    	
    	ObjectBasicTypes oa = new ObjectBasicTypes();
    	oa.longValue = 123l;

    	ObjectReference oaa = new ObjectReference();
    	oaa.stringValue = "ABC"; 

    	ProjectionUrlPatternFnc pa = projections.compose(ProjectionUrlPatternFnc.class, oa, oaa);
    	
    	Assert.assertNotNull(pa);
    	Assert.assertEquals("https://www.example.org/" + oa.longValue + "/" + oaa.stringValue, pa.href);    	
    }
    
}
