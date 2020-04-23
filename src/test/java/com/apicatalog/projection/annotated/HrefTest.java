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
import com.apicatalog.projection.projections.HrefTo;

public class HrefTest {

	ProjectionRegistry projections;
	
	@Before
	public void setup() throws CompositionError, ProjectionError {
		projections = ProjectionRegistry.newInstance();
		
		projections.register(HrefTo.class);
	}
			
    @Test
    public void testCompose() throws CompositionError, ConverterError {
    	
    	Env env = new Env(URI.create("/1/2"));
    	
    	HrefTo to = projections.get(HrefTo.class).compose(env);
    	
    	Assert.assertNotNull(to);
    	
    	Assert.assertEquals("/1/2/a/b/c", to.href);
    }
    
    @Test
    public void testExtract() throws ExtractionError, ConverterError {
    	
    	HrefTo to = new HrefTo();
    	to.href = "/1/2/a/b/c";
    	
    	Env object = projections.get(HrefTo.class).extract(to, Env.class).orElse(null);
    	
    	Assert.assertNull(object);
    }
}
