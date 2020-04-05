package com.apicatalog.projection.annotated;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.Object1;
import com.apicatalog.projection.projections.PrefixTo;


public class PrefixMappingTest {

	ProjectionRegistry projections;
	
	@Before
	public void setup() {
		projections = ProjectionRegistry.newInstance();
		
		projections.register(PrefixTo.class);
	}
	
    @Test
    public void testComposition() throws ProjectionError, ConverterError {
    	
    	Object1 o1 = new Object1();
    	o1.id = "of the New World";
    	
    	PrefixTo projection = projections.compose(PrefixTo.class, o1);
    	
    	Assert.assertNotNull(projection);
    	
    	Assert.assertEquals("At the beginning of the New World", projection.id);
    }
    
    @Test
    public void testDecomposition() throws ProjectionError, ConverterError {
    	
    	PrefixTo projection = new PrefixTo();
    	projection.id = "At the beginning of the New World";
    	
    	Object[] objects = projections.decompose(projection);
    	
    	Assert.assertNotNull(objects);
    	
    	Assert.assertEquals(1, objects.length);
    	Assert.assertEquals(Object1.class, objects[0].getClass());
    	
    	Object1 o1 = (Object1)objects[0];

    	Assert.assertEquals("of the New World", o1.id);
    }
}
