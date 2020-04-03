package com.apicatalog.projection.annotated;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.projections.AccessModeTo;

public class AccessModeTest {

	ProjectionRegistry projections;
	
	@Before
	public void setup() {
		projections = ProjectionRegistry.newInstance()
						.add(AccessModeTo.class);
	}
	
    @Test
    public void testComposition() throws ProjectionError, ConverterError {
    	
    	BasicTypes o1 = new BasicTypes();
    	o1.stringValue = "ABC123";
    	o1.longValue = 951l;
    	o1.booleanValue = true;
    	
    	AccessModeTo projection = projections.compose(
    									AccessModeTo.class,
    									o1
    									);
    	
    	Assert.assertNotNull(projection);
    	Assert.assertEquals(o1.stringValue, projection.stringValue);
    	Assert.assertNull(projection.longValue);
    	Assert.assertTrue(projection.booleanValue);
    }
    
    @Test
    public void testDecomposition1() throws ProjectionError, ConverterError {
    	
    	AccessModeTo to = new AccessModeTo();
    	to.stringValue = "ABC123";
    	to.longValue = 951l;
    	to.booleanValue = true;

    	Object[] objects = projections.decompose(to);
    	
    	Assert.assertNotNull(objects);
    	Assert.assertEquals(1, objects.length);
    	
    	Assert.assertNotNull(objects[0]);
    	Assert.assertTrue(BasicTypes.class.isInstance(objects[0]));
    	Assert.assertNull(((BasicTypes)objects[0]).stringValue);
    	Assert.assertEquals(to.longValue, ((BasicTypes)objects[0]).longValue);
    	Assert.assertTrue(((BasicTypes)objects[0]).booleanValue);
    }
    
    @Test
    public void testExtraction() throws ProjectionError, ConverterError {
    	
    	AccessModeTo to = new AccessModeTo();
    	to.stringValue = "ABC123";
    	to.longValue = 951l;
    	to.booleanValue = true;

    	BasicTypes object = projections.extract(BasicTypes.class, to);
    	
    	Assert.assertNotNull(object);
    	Assert.assertNull(object.stringValue);
    	Assert.assertEquals(to.longValue, object.longValue);
    	Assert.assertTrue(object.booleanValue);
    }
}
