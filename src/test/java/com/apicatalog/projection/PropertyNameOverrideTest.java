package com.apicatalog.projection;

import java.time.Instant;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.converter.InvertibleFunctionError;
import com.apicatalog.projection.mapper.ProjectionMapper;
import com.apicatalog.projection.mapping.MappingIndex;
import com.apicatalog.projection.objects.ObjectBasicTypes;
import com.apicatalog.projection.projections.ProjectionBasicTypesNameOverride;


public class PropertyNameOverrideTest {

	ProjectionFactory projection;
	
	@Before
	public void setup() {
		ProjectionMapper scanner = new ProjectionMapper();
		
		MappingIndex index = new MappingIndex();
		index.add(scanner.getMapping(ProjectionBasicTypesNameOverride.class));
		
		projection = new ProjectionFactory(index);
	}
	
    @Test
    public void testComposition() throws ProjectionError, InvertibleFunctionError {
    	
    	ObjectBasicTypes oa = new ObjectBasicTypes();
    	oa.booleanValue = true;
    	oa.doubleValue = 123.456d;
    	oa.instantValue = Instant.now();
    	oa.longValue = 123456l;
    	oa.stringValue = "ABCDEF";
    	
    	ProjectionBasicTypesNameOverride pa = projection.compose(ProjectionBasicTypesNameOverride.class, oa);
    	
    	Assert.assertNotNull(pa);
    	
    	Assert.assertEquals(oa.stringValue, pa.projectedString);
    	Assert.assertEquals(oa.booleanValue, pa.projectedBoolean);
    	Assert.assertEquals(oa.doubleValue, pa.projectedDouble);
    	Assert.assertEquals(oa.instantValue, pa.projectedInstant);
    	Assert.assertEquals(oa.longValue, pa.projectedLong);
    }
    
    @Test
    public void testDecomposition() throws ProjectionError, InvertibleFunctionError {
    	
    	ProjectionBasicTypesNameOverride pa = new ProjectionBasicTypesNameOverride();
    	pa.projectedBoolean = true;
    	pa.projectedDouble = 123.456d;
    	pa.projectedInstant = Instant.now();
    	pa.projectedLong = 123456l;
    	pa.projectedString = "ABCDEF";
    	
    	Object[] oo = projection.decompose(pa);
    	
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
