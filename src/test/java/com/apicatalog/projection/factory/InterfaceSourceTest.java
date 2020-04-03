package com.apicatalog.projection.factory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.Interface1Impl;
import com.apicatalog.projection.projections.Interface1To;

public class InterfaceSourceTest {

	ProjectionFactory projections;
	
	@Before
	public void setup() {
		projections = ProjectionFactory.newInstance();
		
		projections.add(Interface1To.class);
	}
	
    @Test
    public void testComposition() throws ProjectionError, ConverterError {
    	
    	Interface1Impl i1 = new Interface1Impl();
    	i1.setId("987654321");
    	
    	Interface1To projection = projections.compose(
    									Interface1To.class,
    									i1
    									);
    	
    	Assert.assertNotNull(projection);
    	Assert.assertEquals(Long.valueOf(i1.getId()), projection.id);
    }

//TODO
//    @Test
//    public void testDecomposition() throws ProjectionError, ConverterError {
//    	
//    	Interface1To to = new Interface1To();
//    	to.id = 951846237l;
//    	
//    	Object[] objects  = projections.decompose(to);
//    	
//    	Assert.assertNotNull(objects);
//    	Assert.assertEquals(1, objects.length);
//    	
//    	Assert.assertTrue(Interface1.class.isInstance(objects[0]));
//    	
//    }
    
}
