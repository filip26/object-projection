package com.apicatalog.projection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.mapper.ProjectionMapper;
import com.apicatalog.projection.objects.Object3;
import com.apicatalog.projection.objects.Object4;
import com.apicatalog.projection.projections.Object3To;
import com.apicatalog.projection.projections.Object4To;

public class MixedObjectsTest {

	ProjectionFactory projections;
	ProjectionMapper mapper;
	
	@Before
	public void setup() {
		projections = new ProjectionFactory();
		mapper = new ProjectionMapper(projections);	
		
		projections.add(mapper.getMapping(Object3To.class));
		projections.add(mapper.getMapping(Object4To.class));
	}
	
    @Test
    public void testComposition() throws ProjectionError, ConverterError {
    	
    	Object3 o3 = new Object3();
    	o3.id = "id-3";
    	o3.name1 = "name-1";
    	
    	Object4 o4 = new Object4();
    	o4.name2 = "name-2";
    	o4.name3 = "name-3";
    	
    	o3.object4 = o4;
    	
    	Object3To projection = projections.compose(
    									Object3To.class,
    									o3
    									);
    	
    	Assert.assertNotNull(projection);
    	Assert.assertEquals(o3.id, projection.id);
    	
    	Assert.assertNotNull(projection.object4);
    	Assert.assertEquals(o3.name1, projection.object4.name1);
    	Assert.assertEquals(o4.name2, projection.object4.name2);
    	Assert.assertEquals(o4.name3, projection.object4.name3);

    }
    
    @Test
    public void testDecomposition() throws ProjectionError, ConverterError {
    	
    	Object3To to3 = new Object3To();
    	to3.id = "id-3";
    	
    	Object4To to4 = new Object4To();
    	to4.name1 = "name-1";
    	to4.name2 = "name-2";
    	to4.name3 = "name-3";
    	
    	to3.object4 = to4;

    	Object[] objects = projections.decompose(to3);
    	
    	Assert.assertNotNull(objects);
    	Assert.assertEquals(1, objects.length);
    	
    	Assert.assertNotNull(objects[0]);
    	Assert.assertTrue(Object3.class.isInstance(objects[0]));
    	
    	Object3 object3 = (Object3)objects[0];
    	
    	Assert.assertNotNull(object3);
    	Assert.assertEquals(to3.id, object3.id);
    	Assert.assertEquals(to4.name1, object3.name1);
    	
    	Assert.assertNotNull(object3.object4);
    	Assert.assertEquals(to4.name2, object3.object4.name2);
    	Assert.assertEquals(to4.name3, object3.object4.name3);
    }
    
    @Test
    public void testExtraction() throws ProjectionError, ConverterError {
    	
    	Object3To to3 = new Object3To();
    	to3.id = "id-3";
    	
    	Object4To to4 = new Object4To();
    	to4.name1 = "name-1";
    	to4.name2 = "name-2";
    	to4.name3 = "name-3";
    	
    	to3.object4 = to4;

    	Object3 object3 = projections.extract(Object3.class, to3);
    	
    	Assert.assertNotNull(object3);
    	Assert.assertEquals(to3.id, object3.id);
    	Assert.assertEquals(to4.name1, object3.name1);
    	
    	Assert.assertNotNull(object3.object4);
    	Assert.assertEquals(to4.name2, object3.object4.name2);
    	Assert.assertEquals(to4.name3, object3.object4.name3);    	
    }
}
