package com.apicatalog.projection.annotated;

import java.time.Instant;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.projections.NameOverrideTo;


public class PropertyNameOverrideTest {

	ProjectionRegistry projections;
	
	@Before
	public void setup() {
		projections = ProjectionRegistry.newInstance();
		
		projections.add(NameOverrideTo.class);
	}
	
    @Test
    public void testComposition() throws ProjectionError, ConverterError {
    	
    	BasicTypes oa = new BasicTypes();
    	oa.booleanValue = true;
    	oa.doubleValue = 123.456d;
    	oa.instantValue = Instant.now();
    	oa.longValue = 123456l;
    	oa.stringValue = "ABCDEF";
    	
    	NameOverrideTo pa = projections.compose(NameOverrideTo.class, oa);
    	
    	Assert.assertNotNull(pa);
    	
    	Assert.assertEquals(oa.stringValue, pa.projectedString);
    	Assert.assertEquals(oa.booleanValue, pa.projectedBoolean);
    	Assert.assertEquals(oa.doubleValue, pa.projectedDouble);
    	Assert.assertEquals(oa.instantValue, pa.projectedInstant);
    	Assert.assertEquals(oa.longValue, pa.projectedLong);
    }
    
    @Test
    public void testDecomposition() throws ProjectionError, ConverterError {
    	
    	NameOverrideTo pa = new NameOverrideTo();
    	pa.projectedBoolean = true;
    	pa.projectedDouble = 123.456d;
    	pa.projectedInstant = Instant.now();
    	pa.projectedLong = 123456l;
    	pa.projectedString = "ABCDEF";
    	
    	Object[] oo = projections.decompose(pa);
    	
    	Assert.assertNotNull(oo);
    	Assert.assertEquals(1, oo.length);
    	Assert.assertEquals(BasicTypes.class, oo[0].getClass());
    	
    	BasicTypes oa = (BasicTypes)oo[0];

    	Assert.assertEquals(pa.projectedString, oa.stringValue);
    	Assert.assertEquals(pa.projectedBoolean, oa.booleanValue);
    	Assert.assertEquals(pa.projectedDouble, oa.doubleValue);
    	Assert.assertEquals(pa.projectedInstant, oa.instantValue);
    	Assert.assertEquals(pa.projectedLong, oa.longValue);
    }
}
