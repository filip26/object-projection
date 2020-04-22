package com.apicatalog.projection.annotated;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.Object1;
import com.apicatalog.projection.objects.Object2;
import com.apicatalog.projection.projections.Object1To;
import com.apicatalog.projection.projections.Object2To;
import com.apicatalog.projection.projections.TypeObjectTo;


public class VisibilityTest {

	ProjectionRegistry projections;
	
	@Before
	public void setup() throws CompositionError, ProjectionError {
		projections = ProjectionRegistry.newInstance()
						.register(Object1To.class)
						.register(Object2To.class)
						.register(TypeObjectTo.class)
						;
	}
	
    @Test
    public void testCompose1() throws CompositionError, ConverterError {
    	
    	Object1 o1 =new Object1();
    	o1.id = "Object #1";

    	Object2 o2 =new Object2();
    	o2.id = "Object #2";

    	o1.object2 = o2;
    	
    	Object1To projection = projections.get(Object1To.class).compose(o1);
    	
    	Assert.assertNotNull(projection);
    	
    	Assert.assertEquals(o1.id, projection.id);
    	
    	Assert.assertNotNull(projection.object2);
    	
    	Assert.assertNull(projection.object2.id);    	
    }

    @Test
    public void testCompose2() throws CompositionError, ConverterError {

    	Object2 o2 =new Object2();
    	o2.id = "Object 2";

    	Object2To projection = projections.get(Object2To.class).compose(o2);
    	
    	Assert.assertNotNull(projection);
    	
    	Assert.assertEquals(o2.id, projection.id);
    	
    	Assert.assertNull(projection.object3);
        	
    }
}
