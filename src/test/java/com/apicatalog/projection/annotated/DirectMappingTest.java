package com.apicatalog.projection.annotated;

import java.time.Instant;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.projections.TypeObjectTo;


public class DirectMappingTest {

	ProjectionRegistry projections;
	
	@Before
	public void setup() {
		projections = ProjectionRegistry.newInstance()
						.register(TypeObjectTo.class);
	}
	
    @Test
    public void testComposition() throws ProjectionError, ConverterError {
    	
    	BasicTypes object = new BasicTypes();
    	object.booleanValue = true;
    	object.doubleValue = 123.456d;
    	object.instantValue = Instant.now();
    	object.longValue = 123456l;
    	object.stringValue = "ABCDEF";

    	TypeObjectTo projection = projections.compose(TypeObjectTo.class, object);
    	
    	Assert.assertNotNull(projection);
    	
    	Assert.assertEquals(object.stringValue, projection.stringValue);
    	Assert.assertEquals(object.booleanValue, projection.booleanValue);
    	Assert.assertEquals(object.doubleValue, projection.doubleValue);
    	Assert.assertEquals(object.instantValue, projection.instantValue);
    	Assert.assertEquals(object.longValue, projection.longValue);
    }
    
    @Test
    public void testExtraction() throws ProjectionError, ConverterError {
    	
    	TypeObjectTo projection = new TypeObjectTo();
    	projection.booleanValue = true;
    	projection.doubleValue= 123.456d;
    	projection.instantValue = Instant.now();
    	projection.longValue = 123456l;
    	projection.stringValue = "ABCDEF";
    	
    	BasicTypes object = projections.extract(projection, BasicTypes.class);
    	
    	Assert.assertNotNull(object);
    	
    	Assert.assertEquals(projection.stringValue, object.stringValue);
    	Assert.assertEquals(projection.booleanValue, object.booleanValue);
    	Assert.assertEquals(projection.doubleValue, object.doubleValue);
    	Assert.assertEquals(projection.instantValue, object.instantValue);
    	Assert.assertEquals(projection.longValue, object.longValue);
    }

}
