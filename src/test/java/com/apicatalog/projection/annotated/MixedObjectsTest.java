package com.apicatalog.projection.annotated;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.ExtractionError;
import com.apicatalog.projection.Registry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.Object3;
import com.apicatalog.projection.objects.Object4;
import com.apicatalog.projection.projections.Object3To;
import com.apicatalog.projection.projections.Object4To;

public class MixedObjectsTest {

	Registry projections;
	
	@Before
	public void setup() throws CompositionError, ProjectionError {
		projections = Registry.newInstance();
		
		projections
			.register(Object3To.class)
			.register(Object4To.class)
			;
	}
	
    @Test
    public void testCompose() throws CompositionError, ConverterError {
    	
    	Object3 o3 = new Object3();
    	o3.id = "id-3";
    	o3.name1 = "name-1";
    	
    	Object4 o4 = new Object4();
    	o4.name2 = "name-2";
    	o4.name3 = "name-3";
    	
    	o3.object4 = o4;
    	
    	Object3To projection = projections.get(Object3To.class).compose(o3);
    	
    	Assert.assertNotNull(projection);
    	Assert.assertEquals(o3.id, projection.id);
    	
    	Assert.assertNotNull(projection.object4);
    	Assert.assertEquals(o3.name1, projection.object4.name1);
    	Assert.assertEquals(o4.name2, projection.object4.name2);
    	Assert.assertEquals(o4.name3, projection.object4.name3);

    }
    
    @Test
    public void testExtract1() throws ExtractionError, ConverterError {
    	
    	Object3To to3 = new Object3To();
    	to3.id = "id-3";
    	
    	Object4To to4 = new Object4To();
    	to4.name1 = "name-1";
    	to4.name2 = "name-2";
    	to4.name3 = "name-3";
    	
    	to3.object4 = to4;

    	Object3 object3 = projections.get(Object3To.class).extract(to3, Object3.class).orElse(null);
    	    	
    	Assert.assertNotNull(object3);
    	Assert.assertEquals(to3.id, object3.id);
    	Assert.assertEquals(to4.name1, object3.name1);
    	
    	Assert.assertNotNull(object3.object4);
    	Assert.assertEquals(to4.name2, object3.object4.name2);
    	Assert.assertEquals(to4.name3, object3.object4.name3);
    }
    
    @Test
    public void testExtract2() throws ExtractionError, ConverterError {
    	
    	Object3To to3 = new Object3To();
    	to3.id = "id-3";
    	
    	Object4To to4 = new Object4To();
    	to4.name1 = "name-1";
    	
    	to3.object4 = to4;

    	Object3 object3 = projections.get(Object3To.class).extract(to3, Object3.class).orElse(null);
    	    	
    	Assert.assertNotNull(object3);
    	Assert.assertEquals(to3.id, object3.id);
    	Assert.assertEquals(to4.name1, object3.name1);
    	
    	Assert.assertNull(object3.object4);
    }
    
    @Test
    public void testExtract3() throws ExtractionError, ConverterError {
    	
    	Object3To to3 = new Object3To();
    	to3.id = "id-3";
    	
    	Object4To to4 = new Object4To();
    	to4.name1 = "name-1";
    	to4.name2 = "name-2";
    	to4.name3 = "name-3";
    	
    	to3.object4 = to4;

    	Object3 object3 = projections.get(Object3To.class).extract(to3, Object3.class).orElse(null);
    	
    	Assert.assertNotNull(object3);
    	Assert.assertEquals(to3.id, object3.id);
    	Assert.assertEquals(to4.name1, object3.name1);
    	
    	Assert.assertNotNull(object3.object4);
    	Assert.assertEquals(to4.name2, object3.object4.name2);
    	Assert.assertEquals(to4.name3, object3.object4.name3);    	
    }
}
