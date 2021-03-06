package com.apicatalog.projection.annotated;

import java.time.Instant;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.ExtractionError;
import com.apicatalog.projection.Registry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.projections.TypeObjectTo;


public class DirectMappingTest {

	Registry projections;
	
	@Before
	public void setup() throws CompositionError, ProjectionError {
		projections = Registry.newInstance()
						.register(TypeObjectTo.class);
	}
	
    @Test
    public void testCompose() throws CompositionError, ConverterError {
    	
    	BasicTypes object = new BasicTypes();
    	object.booleanValue = true;
    	object.doubleValue = 123.456d;
    	object.instantValue = Instant.now();
    	object.longValue = 123456l;
    	object.stringValue = "ABCDEF";

    	TypeObjectTo to = projections.get(TypeObjectTo.class).compose(object);
    	
    	Assert.assertNotNull(to);
    	
    	Assert.assertEquals(object.stringValue, to.stringValue);
    	Assert.assertEquals(object.booleanValue, to.booleanValue);
    	Assert.assertEquals(object.doubleValue, to.doubleValue);
    	Assert.assertEquals(object.instantValue, to.instantValue);
    	Assert.assertEquals(object.longValue, to.longValue);
    }
    
    @Test
    public void testExtract() throws ExtractionError, ConverterError {
    	
    	TypeObjectTo to = new TypeObjectTo();
    	to.booleanValue = true;
    	to.doubleValue= 123.456d;
    	to.instantValue = Instant.now();
    	to.longValue = 123456l;
    	to.stringValue = "ABCDEF";
    	
    	BasicTypes object = projections.get(TypeObjectTo.class).extract(to, BasicTypes.class).orElse(null);
    	
    	Assert.assertNotNull(object);
    	
    	Assert.assertEquals(to.stringValue, object.stringValue);
    	Assert.assertEquals(to.booleanValue, object.booleanValue);
    	Assert.assertEquals(to.doubleValue, object.doubleValue);
    	Assert.assertEquals(to.instantValue, object.instantValue);
    	Assert.assertEquals(to.longValue, object.longValue);
    }

}
