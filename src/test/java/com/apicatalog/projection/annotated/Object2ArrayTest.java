package com.apicatalog.projection.annotated;

import java.net.URI;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.ExtractionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.Env;
import com.apicatalog.projection.projections.Object2ArrayTo;

public class Object2ArrayTest {

	ProjectionRegistry projections;
	
	@Before
	public void setup() throws CompositionError, ProjectionError {
		projections = ProjectionRegistry.newInstance();
		
		projections.register(Object2ArrayTo.class);
	}
			
    @Test
    public void testCompose() throws CompositionError, ConverterError {
    	
    	Env env = new Env(URI.create("/1/2"));
    	
    	Object2ArrayTo to = projections.get(Object2ArrayTo.class).compose(env);
    	
    	Assert.assertNotNull(to);
    	
    	Assert.assertEquals("/1/2/a/b/c", to.href);
    }
    
    @Test
    public void testExtract() throws ExtractionError, ConverterError {
    	
    	Object2ArrayTo to = new Object2ArrayTo();
    	to.href = "/1/2/a/b/c";
    	
    	Env object = projections.get(Object2ArrayTo.class).extract(to, Env.class).orElse(null);
    	
    	Assert.assertNull(object);
    }
}
