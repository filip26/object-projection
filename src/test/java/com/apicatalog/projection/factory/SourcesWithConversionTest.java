package com.apicatalog.projection.factory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.mapper.ProjectionMapper;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.objects.Reference;
import com.apicatalog.projection.projections.SourcesReduceMapTo;

public class SourcesWithConversionTest {

	ProjectionFactory projections;
	ProjectionMapper mapper;
	
	@Before
	public void setup() {
		projections = new ProjectionFactory();
		mapper = new ProjectionMapper(projections);		
		
		projections.add(mapper.getProjection(SourcesReduceMapTo.class));
	}
		
    @Test
    public void testComposition() throws ProjectionError, ConverterError {
    	
    	BasicTypes oa = new BasicTypes();
    	oa.longValue = 123l;

    	Reference oaa = new Reference();
    	oaa.stringValue = "ABC"; 

    	SourcesReduceMapTo pa = projections.compose(SourcesReduceMapTo.class, oa, oaa);
    	
    	Assert.assertNotNull(pa);
    	Assert.assertEquals(oa.longValue + oaa.stringValue + "!@#", pa.longstring);
    	Assert.assertEquals(oaa.stringValue + oa.longValue, pa.stringlong);    	
    }
}
