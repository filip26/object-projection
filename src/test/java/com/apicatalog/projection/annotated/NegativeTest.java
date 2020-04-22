package com.apicatalog.projection.annotated;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.projections.EmptyProjection;


public class NegativeTest {

	ProjectionRegistry registry;
	
	@Before
	public void setup() {
		registry = ProjectionRegistry.newInstance();
	}
	
    @Test
    public void testNull() throws ProjectionError {
    
    	try {
    		registry.register((Class<?>)null);
    		
    		Assert.fail();
    		
    	} catch (IllegalArgumentException e) {
    	}
    }

    @Test
    public void testUnannotated() {
    
    	try {
    		registry.register(NegativeTest.class);
    		
    		Assert.fail();
    		
    	} catch (ProjectionError e) {
    	}
    }

    @Test
    public void testEmpty() {
    
    	try {
    		registry.register(EmptyProjection.class);
    		
    		Assert.fail();
    		
    	} catch (ProjectionError e) {
    	}
    }

}
