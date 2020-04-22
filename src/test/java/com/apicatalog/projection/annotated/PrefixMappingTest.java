package com.apicatalog.projection.annotated;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.Object1;
import com.apicatalog.projection.projections.PrefixTo;


public class PrefixMappingTest {

	ProjectionRegistry projections;
	
	@Before
	public void setup() throws CompositionError, ProjectionError {
		projections = ProjectionRegistry.newInstance();
		
		projections.register(PrefixTo.class);
	}
	
    @Test
    public void testComposition() throws CompositionError, ConverterError {
    	
    	Object1 o1 = new Object1();
    	o1.id = "of the New World";
    	
    	PrefixTo projection = projections.get(PrefixTo.class).compose(o1);
    	
    	Assert.assertNotNull(projection);
    	
    	Assert.assertEquals("At the beginning of the New World", projection.id);
    }
    
    @Test
    public void testExtraction() throws CompositionError, ConverterError {
    	
    	PrefixTo projection = new PrefixTo();
    	projection.id = "At the beginning of the New World";
    	
    	Object1 object = projections.get(PrefixTo.class).extract(projection, Object1.class).orElse(null);
    	
    	Assert.assertNotNull(object);
    	
    	Assert.assertEquals("of the New World", object.id);
    }
}
