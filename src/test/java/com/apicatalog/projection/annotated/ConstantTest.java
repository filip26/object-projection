package com.apicatalog.projection.annotated;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.Object1;
import com.apicatalog.projection.projections.ConstantTo;

public class ConstantTest {

	ProjectionRegistry projections;
	
	@Before
	public void setup() {
		projections = ProjectionRegistry.newInstance()
						.register(ConstantTo.class);
	}
	
    @Test
    public void testComposition() throws ProjectionError, ConverterError {
    	
    	Object1 o1 = new Object1();
    	o1.id = "ABC123";
    	
    	ConstantTo projection = projections.compose(
    									ConstantTo.class,
    									o1
    									);
    	
    	Assert.assertNotNull(projection);
    	Assert.assertEquals(o1.id, projection.id);
    	
    	Assert.assertEquals(Long.valueOf(1234567890l), projection.longValue);
    	
    	Assert.assertNotNull(projection.stringArray);
    	Assert.assertArrayEquals(new String[] { "s1", "s2", "s3" }, projection.stringArray);
    	
    	Assert.assertNotNull(projection.booleanCollection);
    	Assert.assertArrayEquals(new Boolean[] { true, false, true, true }, projection.booleanCollection.toArray(new Boolean[0]));

    }
    
    @Test
    public void testExtraction1() throws ProjectionError, ConverterError {
    	
    	ConstantTo to = new ConstantTo();
    	to.id = "https://example.org/c/";
    	to.stringArray = new String[] {"10", "20", "30"};
    	to.booleanCollection = Arrays.asList(false, true);
    	
    	Object1 object = projections.extract(to, Object1.class);
    	
    	Assert.assertNotNull(object);
    	
    	Assert.assertTrue(Object1.class.isInstance(object));
    	Assert.assertEquals(to.id, object.id);
    	Assert.assertNull(object.object2);
    }
    
    @Test
    public void testExtraction2() throws ProjectionError, ConverterError {
    	
    	ConstantTo to = new ConstantTo();
    	to.stringArray = new String[] {"10", "20", "30"};
    	to.booleanCollection = Arrays.asList(false, true);
    	
    	Object1 object = projections.extract(to, Object1.class);

    	Assert.assertNotNull(object);
    }
}
