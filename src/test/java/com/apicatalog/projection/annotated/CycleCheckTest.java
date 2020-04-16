package com.apicatalog.projection.annotated;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.InterwiredObject1;
import com.apicatalog.projection.objects.InterwiredObject2;
import com.apicatalog.projection.projections.InterwiredProjection1;
import com.apicatalog.projection.projections.InterwiredProjection2;


public class CycleCheckTest {

	ProjectionRegistry projections;
	
	@Before
	public void setup() throws ProjectionError, ProjectionBuilderError {
		projections = ProjectionRegistry.newInstance()		
						.register(InterwiredProjection1.class)
						.register(InterwiredProjection2.class)
						;
	}
	
    @Test
    public void testCompose1() throws ProjectionError, ConverterError {
    	
    	InterwiredObject1 o1 = new InterwiredObject1();
    	o1.id = "Object 1";

    	InterwiredObject2 o2 = new InterwiredObject2();
    	o2.id = "Object 2";

    	o1.object2 = o2;
    	o2.object1 = o1;
    	
    	InterwiredProjection1 projection = projections.get(InterwiredProjection1.class).compose(o1, o2);
    	
    	Assert.assertNotNull(projection);
    	
    	Assert.assertEquals(o1.id, projection.id);
    	
    	Assert.assertNotNull(projection.object2);
    	
    	Assert.assertEquals(o2.id, projection.object2.id);
    	
    	Assert.assertNull(projection.object2.object1);    	
    }

    @Test
    public void testCompose2() throws ProjectionError, ConverterError {
    	
    	InterwiredObject1 o1 = new InterwiredObject1();
    	o1.id = "Object 1";

    	InterwiredObject2 o2 = new InterwiredObject2();
    	o2.id = "Object 2";

    	o1.object2 = o2;
    	o2.object1 = o1;
    	
    	InterwiredProjection2 projection = projections.get(InterwiredProjection2.class).compose(o2, o1);
    	
    	Assert.assertNotNull(projection);
    	
    	Assert.assertEquals(o2.id, projection.id);
    	
    	Assert.assertNotNull(projection.object1);
    	
    	Assert.assertEquals(o1.id, projection.object1.id);
    	
    	Assert.assertNull(projection.object1.object2);    	
    }
}
