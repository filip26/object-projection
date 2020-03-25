package com.apicatalog.projection;

import java.time.Instant;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.converter.ConvertorError;
import com.apicatalog.projection.mapper.ProjectionMapper;
import com.apicatalog.projection.objects.ObjectBasicTypes;
import com.apicatalog.projection.projections.BasicPropertyNameOverride;


public class PropertyNameOverrideTest {

	ProjectionFactory projections;
	ProjectionMapper mapper;
	
	@Before
	public void setup() {
		projections = new ProjectionFactory();
		mapper = new ProjectionMapper(projections);
		
		projections.add(mapper.getMapping(BasicPropertyNameOverride.class));
	}
	
    @Test
    public void testComposition() throws ProjectionError, ConvertorError {
    	
    	ObjectBasicTypes oa = new ObjectBasicTypes();
    	oa.booleanValue = true;
    	oa.doubleValue = 123.456d;
    	oa.instantValue = Instant.now();
    	oa.longValue = 123456l;
    	oa.stringValue = "ABCDEF";
    	
    	BasicPropertyNameOverride pa = projections.compose(BasicPropertyNameOverride.class, oa);
    	
    	Assert.assertNotNull(pa);
    	
    	Assert.assertEquals(oa.stringValue, pa.projectedString);
    	Assert.assertEquals(oa.booleanValue, pa.projectedBoolean);
    	Assert.assertEquals(oa.doubleValue, pa.projectedDouble);
    	Assert.assertEquals(oa.instantValue, pa.projectedInstant);
    	Assert.assertEquals(oa.longValue, pa.projectedLong);
    }
    
    @Test
    public void testDecomposition() throws ProjectionError, ConvertorError {
    	
    	BasicPropertyNameOverride pa = new BasicPropertyNameOverride();
    	pa.projectedBoolean = true;
    	pa.projectedDouble = 123.456d;
    	pa.projectedInstant = Instant.now();
    	pa.projectedLong = 123456l;
    	pa.projectedString = "ABCDEF";
    	
    	Object[] oo = projections.decompose(pa);
    	
    	Assert.assertNotNull(oo);
    	Assert.assertEquals(1, oo.length);
    	Assert.assertEquals(ObjectBasicTypes.class, oo[0].getClass());
    	
    	ObjectBasicTypes oa = (ObjectBasicTypes)oo[0];

    	Assert.assertEquals(pa.projectedString, oa.stringValue);
    	Assert.assertEquals(pa.projectedBoolean, oa.booleanValue);
    	Assert.assertEquals(pa.projectedDouble, oa.doubleValue);
    	Assert.assertEquals(pa.projectedInstant, oa.instantValue);
    	Assert.assertEquals(pa.projectedLong, oa.longValue);
    }
}
