package com.apicatalog.projection.annotated;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.Object1;
import com.apicatalog.projection.projections.ConstantTo;

public class ConstantTest {

	Projection<ConstantTo> projection;
	
	@Before
	public void setup() throws CompositionError, ProjectionError {
		projection = Projection.scan(ConstantTo.class).build(ProjectionRegistry.newInstance());
	}
	
    @Test
    public void testCompose() throws CompositionError, ConverterError {
    	
    	Object1 o1 = new Object1();
    	o1.id = "ABC123";
    	
    	ConstantTo to = projection.compose(o1);
    	
    	Assert.assertNotNull(to);
    	Assert.assertEquals(o1.id, to.id);
    	
    	Assert.assertEquals(Long.valueOf(1234567890l), to.longValue);
    	
    	Assert.assertNotNull(to.stringArray);
    	Assert.assertArrayEquals(new String[] { "s1", "s2", "s3" }, to.stringArray);
    	
    	Assert.assertNotNull(to.booleanCollection);
    	Assert.assertArrayEquals(new Boolean[] { true, false, true, true }, to.booleanCollection.toArray(new Boolean[0]));

    }
    
    @Test
    public void testExtract1() throws CompositionError, ConverterError {
    	
    	ConstantTo to = new ConstantTo();
    	to.id = "https://example.org/c/";
    	to.stringArray = new String[] {"10", "20", "30"};
    	to.booleanCollection = Arrays.asList(false, true);
    	
    	Object1 object = projection.extract(to, Object1.class).orElse(null);
    	
    	Assert.assertNotNull(object);
    	
    	Assert.assertTrue(Object1.class.isInstance(object));
    	Assert.assertEquals(to.id, object.id);
    	Assert.assertNull(object.object2);
    }
    
    @Test
    public void testExtract2() throws CompositionError, ConverterError {
    	
    	ConstantTo to = new ConstantTo();
    	to.stringArray = new String[] {"10", "20", "30"};
    	to.booleanCollection = Arrays.asList(false, true);
    	
    	Object1 object = projection.extract(to, Object1.class).orElse(null);

    	Assert.assertNull(object);
    }
}
