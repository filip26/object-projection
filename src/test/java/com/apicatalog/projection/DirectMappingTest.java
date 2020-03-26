package com.apicatalog.projection;

import java.time.Instant;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.mapper.ProjectionMapper;
import com.apicatalog.projection.objects.ObjectBasicTypes;
import com.apicatalog.projection.projections.BasicTypes;


public class DirectMappingTest {

	ProjectionFactory projections;
	ProjectionMapper mapper;
	
	@Before
	public void setup() {
		projections = new ProjectionFactory();
		mapper = new ProjectionMapper(projections);	
		
		projections.add(mapper.getMapping(BasicTypes.class));
	}
	
    @Test
    public void testComposition() throws ProjectionError, ConverterError {
    	
    	ObjectBasicTypes oa = new ObjectBasicTypes();
    	oa.booleanValue = true;
    	oa.doubleValue = 123.456d;
    	oa.instantValue = Instant.now();
    	oa.longValue = 123456l;
    	oa.stringValue = "ABCDEF";

    	BasicTypes pa = projections.compose(BasicTypes.class, oa);
    	
    	Assert.assertNotNull(pa);
    	
    	Assert.assertEquals(oa.stringValue, pa.stringValue);
    	Assert.assertEquals(oa.booleanValue, pa.booleanValue);
    	Assert.assertEquals(oa.doubleValue, pa.doubleValue);
    	Assert.assertEquals(oa.instantValue, pa.instantValue);
    	Assert.assertEquals(oa.longValue, pa.longValue);
    }
    
    @Test
    public void testDecomposition() throws ProjectionError, ConverterError {
    	
    	BasicTypes pa = new BasicTypes();
    	pa.booleanValue = true;
    	pa.doubleValue= 123.456d;
    	pa.instantValue = Instant.now();
    	pa.longValue = 123456l;
    	pa.stringValue = "ABCDEF";
    	
    	Object[] oo = projections.decompose(pa);
    	
    	Assert.assertNotNull(oo);
    	
    	Assert.assertEquals(1, oo.length);
    	Assert.assertEquals(ObjectBasicTypes.class, oo[0].getClass());
    	
    	ObjectBasicTypes oa = (ObjectBasicTypes)oo[0];

    	Assert.assertEquals(pa.stringValue, oa.stringValue);
    	Assert.assertEquals(pa.booleanValue, oa.booleanValue);
    	Assert.assertEquals(pa.doubleValue, oa.doubleValue);
    	Assert.assertEquals(pa.instantValue, oa.instantValue);
    	Assert.assertEquals(pa.longValue, oa.longValue);
    }
}
