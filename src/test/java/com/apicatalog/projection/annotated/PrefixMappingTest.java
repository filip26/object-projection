package com.apicatalog.projection.annotated;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.Object1;
import com.apicatalog.projection.projections.PrefixTo;


public class PrefixMappingTest {

	ProjectionRegistry projections;
	
	@Before
	public void setup() throws ProjectionError, ProjectionBuilderError {
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
    public void testExtraction() throws ProjectionError, ConverterError {
    	
    	PrefixTo projection = new PrefixTo();
    	projection.id = "At the beginning of the New World";
    	
    	Object1 object = projections.extract(projection, Object1.class);
    	
    	Assert.assertNotNull(object);
    	
    	Assert.assertEquals("of the New World", object.id);
    }
}
