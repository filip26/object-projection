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
		
		projections.register(NameOverrideTo.class);
	}
	
    @Test
    public void testCompose() throws ProjectionError, ConverterError {
    	
    	BasicTypes object = new BasicTypes();
    	object.booleanValue = true;
    	object.doubleValue = 123.456d;
    	object.instantValue = Instant.now();
    	object.longValue = 123456l;
    	object.stringValue = "ABCDEF";
    	
    	NameOverrideTo projection = projections.compose(NameOverrideTo.class, object);
    	
    	Assert.assertNotNull(projection);
    	
    	Assert.assertEquals(object.stringValue, projection.projectedString);
    	Assert.assertEquals(object.booleanValue, projection.projectedBoolean);
    	Assert.assertEquals(object.doubleValue, projection.projectedDouble);
    	Assert.assertEquals(object.instantValue, projection.projectedInstant);
    	Assert.assertEquals(object.longValue, projection.projectedLong);
    }
    
    @Test
    public void testExtract() throws ProjectionError, ConverterError {
    	
    	NameOverrideTo to = new NameOverrideTo();
    	to.projectedBoolean = true;
    	to.projectedDouble = 123.456d;
    	to.projectedInstant = Instant.now();
    	to.projectedLong = 123456l;
    	to.projectedString = "ABCDEF";
    	
    	BasicTypes object = projections.extract(to, BasicTypes.class);
    	
    	Assert.assertNotNull(object);
    	Assert.assertEquals(to.projectedString, object.stringValue);
    	Assert.assertEquals(to.projectedBoolean, object.booleanValue);
    	Assert.assertEquals(to.projectedDouble, object.doubleValue);
    	Assert.assertEquals(to.projectedInstant, object.instantValue);
    	Assert.assertEquals(to.projectedLong, object.longValue);
    }
}
