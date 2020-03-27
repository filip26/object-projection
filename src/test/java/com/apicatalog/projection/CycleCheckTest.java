package com.apicatalog.projection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.mapper.ProjectionMapper;
import com.apicatalog.projection.objects.InterwiredObject1;
import com.apicatalog.projection.objects.InterwiredObject2;
import com.apicatalog.projection.projections.InterwiredProjection1;
import com.apicatalog.projection.projections.InterwiredProjection2;


public class CycleCheckTest {

	ProjectionFactory projections;
	ProjectionMapper mapper;
	
	@Before
	public void setup() {
		projections = new ProjectionFactory();
		mapper = new ProjectionMapper(projections);	
		
		projections.add(mapper.getMapping(InterwiredProjection1.class));
		projections.add(mapper.getMapping(InterwiredProjection2.class));
	}
	
    @Test
    public void testComposition() throws ProjectionError, ConverterError {
    	
    	InterwiredObject1 o1 = new InterwiredObject1();
    	o1.id = "Object 1";

    	InterwiredObject2 o2 = new InterwiredObject2();
    	o2.id = "Object 2";

    	o1.object2 = o2;
    	o2.object1 = o1;
    	
    	InterwiredProjection1 projection = projections.compose(InterwiredProjection1.class, o1, o2);
    	
    	Assert.assertNotNull(projection);
    	
    	Assert.assertEquals(o1.id, projection.id);
    	
    	Assert.assertNotNull(projection.object2);
    	
    	Assert.assertEquals(o2.id, projection.object2.id);
    	
    	Assert.assertNull(projection.object2.object1);    	
    }

    @Test
    public void testComposition1() throws ProjectionError, ConverterError {
    	
    	InterwiredObject1 o1 = new InterwiredObject1();
    	o1.id = "Object 1";

    	InterwiredObject2 o2 = new InterwiredObject2();
    	o2.id = "Object 2";

    	o1.object2 = o2;
    	o2.object1 = o1;
    	
    	InterwiredProjection2 projection = projections.compose(InterwiredProjection2.class, o2, o1);
    	
    	Assert.assertNotNull(projection);
    	
    	Assert.assertEquals(o2.id, projection.id);
    	
    	Assert.assertNotNull(projection.object1);
    	
    	Assert.assertEquals(o1.id, projection.object1.id);
    	
    	Assert.assertNull(projection.object1.object2);    	
    }
}
