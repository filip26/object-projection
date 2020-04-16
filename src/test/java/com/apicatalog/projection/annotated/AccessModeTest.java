package com.apicatalog.projection.annotated;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.projections.AccessModeTo;

public class AccessModeTest {

	ProjectionRegistry projections;
	
	@Before
	public void setup() throws ProjectionError, ProjectionBuilderError {
		projections = ProjectionRegistry.newInstance()
						.register(AccessModeTo.class);
	}
	
    @Test
    public void testCompose() throws ProjectionError, ConverterError {
    	
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
    public void testExtract() throws ProjectionError, ConverterError {
    	
    	AccessModeTo to = new AccessModeTo();
    	to.stringValue = "ABC123";
    	to.longValue = 951l;
    	to.booleanValue = true;

    	BasicTypes object = projections.get(AccessModeTo.class).extract(to, BasicTypes.class);
    	
    	Assert.assertNotNull(object);
    	Assert.assertNull(object.stringValue);
    	Assert.assertEquals(to.longValue, object.longValue);
    	Assert.assertTrue(object.booleanValue);
    }
}
