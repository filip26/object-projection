package com.apicatalog.projection.annotated;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.ExtractionError;
import com.apicatalog.projection.Registry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.projections.AccessModeTo;

public class AccessModeTest {

	Registry projections;
	
	@Before
	public void setup() throws CompositionError, ProjectionError {
		projections = Registry.newInstance()
						.register(AccessModeTo.class);
	}
	
    @Test
    public void testCompose() throws CompositionError, ConverterError {
    	
    	BasicTypes o1 = new BasicTypes();
    	o1.stringValue = "ABC123";
    	o1.longValue = 951l;
    	o1.booleanValue = true;
    	
    	AccessModeTo projection = projections.get(AccessModeTo.class).compose(
    									AccessModeTo.class,
    									o1
    									);
    	
    	Assert.assertNotNull(projection);
    	Assert.assertEquals(o1.stringValue, projection.stringValue);
    	Assert.assertNull(projection.longValue);
    	Assert.assertTrue(projection.booleanValue);
    }
 
    @Test
    public void testExtract() throws ExtractionError, ConverterError {
    	
    	AccessModeTo to = new AccessModeTo();
    	to.stringValue = "ABC123";
    	to.longValue = 951l;
    	to.booleanValue = true;

    	BasicTypes object = projections.get(AccessModeTo.class).extract(to, BasicTypes.class).orElse(null);
    	
    	Assert.assertNotNull(object);
    	Assert.assertNull(object.stringValue);
    	Assert.assertEquals(to.longValue, object.longValue);
    	Assert.assertTrue(object.booleanValue);
    }
}
