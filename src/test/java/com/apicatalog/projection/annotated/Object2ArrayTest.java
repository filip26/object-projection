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
import com.apicatalog.projection.objects.BasicTypes;
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
    public void testCompose1() throws CompositionError, ConverterError {
    	
    	Env env = new Env(URI.create("1"));
    	
    	Object2ArrayTo to = projections.get(Object2ArrayTo.class).compose(env);
    	
    	Assert.assertNotNull(to);
    	
    	Assert.assertEquals("1/a/b/c", to.href);
    	Assert.assertNull(to.longArray);
    }
    
    @Test
    public void testExtract1() throws ExtractionError, ConverterError {
    	
    	Object2ArrayTo to = new Object2ArrayTo();
    	to.href = "1/a/b/c";
    	
    	Env object = projections.get(Object2ArrayTo.class).extract(to, Env.class).orElse(null);
    	
    	Assert.assertNull(object);
    }
    
    @Test
    public void testCompose2() throws CompositionError, ConverterError {
    	
    	BasicTypes object = new BasicTypes();
    	object.booleanValue = true;
    	
    	Object2ArrayTo to = projections.get(Object2ArrayTo.class).compose(object);
    	
    	Assert.assertNotNull(to);
    	Assert.assertNotNull(to.longArray);
    	Assert.assertArrayEquals(new Long[] {1l}, to.longArray);
    }

    @Test
    public void testExtract2() throws ExtractionError, ConverterError {
    	
    	Object2ArrayTo to = new Object2ArrayTo();
    	to.longArray = new Long[] {0l};
    	
    	BasicTypes object = projections.get(Object2ArrayTo.class).extract(to, BasicTypes.class).orElse(null);
    	
    	Assert.assertNotNull(object);
    	Assert.assertNotNull(object.booleanValue);
    	Assert.assertFalse(object.booleanValue);
    }
}
