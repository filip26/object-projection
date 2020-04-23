package com.apicatalog.projection.annotated;

import java.time.Instant;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.ExtractionError;
import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.projections.NameOverrideTo;


public class PropertyNameOverrideTest {

	Projection<NameOverrideTo> projection;
	
	@Before
	public void setup() throws CompositionError, ProjectionError {
		projection = Projection.scan(NameOverrideTo.class).build(ProjectionRegistry.newInstance());
	}
	
    @Test
    public void testCompose() throws CompositionError, ConverterError {
    	
    	BasicTypes object = new BasicTypes();
    	object.booleanValue = true;
    	object.doubleValue = 123.456d;
    	object.instantValue = Instant.now();
    	object.longValue = 123456l;
    	object.stringValue = "ABCDEF";
    	
    	NameOverrideTo to = projection.compose(object);
    	
    	Assert.assertNotNull(to);
    	
    	Assert.assertEquals(object.stringValue, to.projectedString);
    	Assert.assertEquals(object.booleanValue, to.projectedBoolean);
    	Assert.assertEquals(object.doubleValue, to.projectedDouble);
    	Assert.assertEquals(object.instantValue, to.projectedInstant);
    	Assert.assertEquals(object.longValue, to.projectedLong);
    }
    
    @Test
    public void testExtract() throws ExtractionError, ConverterError {
    	
    	NameOverrideTo to = new NameOverrideTo();
    	to.projectedBoolean = true;
    	to.projectedDouble = 123.456d;
    	to.projectedInstant = Instant.now();
    	to.projectedLong = 123456l;
    	to.projectedString = "ABCDEF";
    	
    	BasicTypes object = projection.extract(to, BasicTypes.class).orElse(null);
    	
    	Assert.assertNotNull(object);
    	Assert.assertEquals(to.projectedString, object.stringValue);
    	Assert.assertEquals(to.projectedBoolean, object.booleanValue);
    	Assert.assertEquals(to.projectedDouble, object.doubleValue);
    	Assert.assertEquals(to.projectedInstant, object.instantValue);
    	Assert.assertEquals(to.projectedLong, object.longValue);
    }
}
