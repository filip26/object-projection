package com.apicatalog.projection.annotated;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.projections.invalid.ConstantConversionErrorTo;
import com.apicatalog.projection.projections.invalid.EmptyTo;
import com.apicatalog.projection.projections.invalid.SourceConversionErrorTo;
import com.apicatalog.projection.projections.invalid.UnmappableSourcePropertyTo;
import com.apicatalog.projection.projections.invalid.UnmappableSourcesPropertyTo;


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
    		registry.register(EmptyTo.class);
    		
    		Assert.fail();
    		
    	} catch (ProjectionError e) {
    	}
    }

    @Test
    public void testUnmappableSourceProperty() {
    
    	try {
    		registry.register(UnmappableSourcePropertyTo.class);
    		
    		Assert.fail();
    		
    	} catch (ProjectionError e) {
    	}
    }

    @Test
    public void testSourceConversionError() {
    
    	try {
    		registry.register(SourceConversionErrorTo.class);
    		
    		Assert.fail();
    		
    	} catch (ProjectionError e) {
    	}
    }

    @Test
    public void testConstantConversionError() {
    
    	try {
    		registry.register(ConstantConversionErrorTo.class);
    		
    		Assert.fail();
    		
    	} catch (ProjectionError e) {
    	}
    }

    @Test
    public void testUnmappableSourcesProperty() {
    
    	try {
    		registry.register(UnmappableSourcesPropertyTo.class);
    		
    		Assert.fail();
    		
    	} catch (ProjectionError e) {
    		e.printStackTrace();
    	}
    }
  
}