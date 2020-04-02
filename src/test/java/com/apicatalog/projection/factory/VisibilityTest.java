package com.apicatalog.projection.factory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.mapper.ProjectionMapper;
import com.apicatalog.projection.objects.Object1;
import com.apicatalog.projection.objects.Object2;
import com.apicatalog.projection.projections.Object1To;
import com.apicatalog.projection.projections.Object2To;
import com.apicatalog.projection.projections.TypeObjectTo;


public class VisibilityTest {

	ProjectionFactory projections;
	ProjectionMapper mapper;
	
	@Before
	public void setup() {
		projections = new ProjectionFactory();
		mapper = new ProjectionMapper(projections);	
		
		projections.add(mapper.getProjection(Object1To.class));
		projections.add(mapper.getProjection(Object2To.class));
		projections.add(mapper.getProjection(TypeObjectTo.class));
	}
	
    @Test
    public void testComposition() throws ProjectionError, ConverterError {
    	
    	Object1 o1 =new Object1();
    	o1.id = "Object 1";

    	Object2 o2 =new Object2();
    	o2.id = "Object 2";

    	o1.object2 = o2;
    	
    	Object1To projection = projections.compose(Object1To.class, o1);
    	
    	Assert.assertNotNull(projection);
    	
    	Assert.assertEquals(o1.id, projection.id);
    	
    	Assert.assertNotNull(projection.object2);
    	
    	Assert.assertNull(projection.object2.id);    	
    }

    @Test
    public void testComposition1() throws ProjectionError, ConverterError {

    	Object2 o2 =new Object2();
    	o2.id = "Object 2";

    	Object2To projection = projections.compose(Object2To.class, o2);
    	
    	Assert.assertNotNull(projection);
    	
    	Assert.assertEquals(o2.id, projection.id);
    	
    	Assert.assertNull(projection.object3);
        	
    }
}
