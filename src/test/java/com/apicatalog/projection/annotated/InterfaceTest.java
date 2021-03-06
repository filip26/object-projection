package com.apicatalog.projection.annotated;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.ExtractionError;
import com.apicatalog.projection.Projection;
import com.apicatalog.projection.Registry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.Interface1;
import com.apicatalog.projection.objects.Interface1Impl;
import com.apicatalog.projection.projections.Interface1To;

public class InterfaceTest {

	Projection<Interface1To> projection;
	
	@Before
	public void setup() throws CompositionError, ProjectionError {
		projection = Projection.scan(Interface1To.class).build(Registry.newInstance());
	}
	
    @Test
    public void testCompose() throws CompositionError, ConverterError {
    	
    	Interface1Impl i1 = new Interface1Impl();
    	i1.setId("987654321");
    	
    	Interface1To to = projection.compose(i1);
    	
    	Assert.assertNotNull(to);
    	Assert.assertEquals(Long.valueOf(i1.getId()), to.id);
    }


    @Test
    public void testExtract() throws ExtractionError, ConverterError {
    	
    	Interface1To to = new Interface1To();
    	to.id = 951846237l;
    	
    	Interface1 object  = projection.extract(to, Interface1Impl.class).orElse(null);
    	
    	Assert.assertNotNull(object);
    	Assert.assertEquals(Long.toString(to.id), object.getId());
    	
    }
    
}
