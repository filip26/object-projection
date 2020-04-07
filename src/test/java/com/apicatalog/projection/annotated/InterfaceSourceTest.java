package com.apicatalog.projection.annotated;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.Interface1;
import com.apicatalog.projection.objects.Interface1Impl;
import com.apicatalog.projection.projections.Interface1To;

public class InterfaceSourceTest {

	ProjectionRegistry projections;
	
	@Before
	public void setup() {
		projections = ProjectionRegistry.newInstance();
		
		projections.register(Interface1To.class);
	}
	
    @Test
    public void testCompose() throws ProjectionError, ConverterError {
    	
    	Interface1Impl i1 = new Interface1Impl();
    	i1.setId("987654321");
    	
    	Interface1To projection = projections.compose(
    									Interface1To.class,
    									i1
    									);
    	
    	Assert.assertNotNull(projection);
    	Assert.assertEquals(Long.valueOf(i1.getId()), projection.id);
    }


//    @Test
//    public void testExtract() throws ProjectionError, ConverterError {
//    	
//    	Interface1To to = new Interface1To();
//    	to.id = 951846237l;
//    	
//    	Interface1 object  = projections.extract(to, Interface1Impl.class);
//    	
//    	Assert.assertNotNull(object);
//    	Assert.assertEquals(to.id, object.getId());
//    	
//    }
    
}
