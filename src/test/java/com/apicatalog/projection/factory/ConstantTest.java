package com.apicatalog.projection.factory;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.mapper.ProjectionMapper;
import com.apicatalog.projection.objects.Object1;
import com.apicatalog.projection.projections.ConstantTo;

public class ConstantTest {

	ProjectionFactory projections;
	ProjectionMapper mapper;
	
	@Before
	public void setup() {
		projections = new ProjectionFactory();
		mapper = new ProjectionMapper(projections);	
		
		projections.add(mapper.getProjection(ConstantTo.class));
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
    public void testDecomposition1() throws ProjectionError, ConverterError {
    	
    	ConstantTo to = new ConstantTo();
    	to.id = "https://example.org/c/";
    	to.stringArray = new String[] {"10", "20", "30"};
    	to.booleanCollection = Arrays.asList(false, true);
    	
    	Object[] objects = projections.decompose(to);
    	
    	Assert.assertNotNull(objects);
    	Assert.assertEquals(1, objects.length);
    	
    	Assert.assertNotNull(objects[0]);
    	Assert.assertTrue(Object1.class.isInstance(objects[0]));
    	Assert.assertEquals(to.id, ((Object1)objects[0]).id);
    	Assert.assertNull(((Object1)objects[0]).object2);
    }    

    
    @Test
    public void testDecomposition2() throws ProjectionError, ConverterError {
    	
    	ConstantTo to = new ConstantTo();
    	to.stringArray = new String[] {"10", "20", "30"};
    	to.booleanCollection = Arrays.asList(false, true);
    	
    	Object[] objects = projections.decompose(to);

    	Assert.assertNotNull(objects);
    	Assert.assertEquals(0, objects.length);    	
    }
}
