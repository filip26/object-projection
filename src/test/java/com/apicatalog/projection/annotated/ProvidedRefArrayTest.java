package com.apicatalog.projection.annotated;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.Registry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.SimpleObject;
import com.apicatalog.projection.projections.ProvidedRefArrayTo;
import com.apicatalog.projection.projections.SimpleObjectTo;
import com.apicatalog.projection.source.SourceObject;

public class ProvidedRefArrayTest {

	Registry projections;
	
	@Before
	public void setup() throws CompositionError, ProjectionError {
		projections = Registry.newInstance();		
		
		projections
			.register(ProvidedRefArrayTo.class)
			.register(SimpleObjectTo.class);
	}
	
    @Test
    public void testCompose1() throws CompositionError, ConverterError {
    	
    	SimpleObject[] items = new SimpleObject[0];
    	
    	ProvidedRefArrayTo projection = projections.get(ProvidedRefArrayTo.class).compose(SourceObject.of("items", items));
    	
    	Assert.assertNotNull(projection);
    	
    	Assert.assertNotNull(projection.items);
    	Assert.assertEquals(0, projection.items.length);
    }

    @Test
    public void testCompose3() throws CompositionError, ConverterError {
    	
    	Collection<SimpleObject> items = new ArrayList<>();
    	
    	ProvidedRefArrayTo projection = projections.get(ProvidedRefArrayTo.class).compose(SourceObject.of("items", items));
    	
    	Assert.assertNotNull(projection);
    	
    	Assert.assertNotNull(projection.items);
    	Assert.assertEquals(0, projection.items.length);
    }

    @Test
    public void testCompose2() throws CompositionError, ConverterError {
    	
    	Collection<SimpleObject> items = new ArrayList<>();
    	
    	SimpleObject o1 = new SimpleObject();
    	o1.i1 = 1;
    	o1.s1 = "s1";

    	SimpleObject o2 = new SimpleObject();
    	o2.i1 = 2;
    	o2.s1 = "s2";
    	
    	items.add(o1);
    	items.add(o2);
    	
    	ProvidedRefArrayTo projection = projections.get(ProvidedRefArrayTo.class).compose(SourceObject.of("items", items));
    	
    	Assert.assertNotNull(projection);
    	
    	Assert.assertNotNull(projection.items);
    	Assert.assertEquals(2, projection.items.length);
    	
    	SimpleObjectTo p1 = projection.items[0]; 
    	Assert.assertNotNull(p1);
    	Assert.assertEquals(o1.i1, p1.i1);
    	Assert.assertEquals(o1.s1, p1.s1);

    	SimpleObjectTo p2 = projection.items[1]; 
    	Assert.assertNotNull(p2);
    	Assert.assertEquals(o2.i1, p2.i1);
    	Assert.assertEquals(o2.s1, p2.s1);
    }
    
//FIXME    @Test
//    public void testExtract() throws ProjectionError, ConverterError {
//    	
//    	ProvidedRefArrayTo to = new ProvidedRefArrayTo();
//    	to.items = new SimpleObjectTo[1];
//    	
//    	SimpleObjectTo to1 = new SimpleObjectTo();
//    	to1.i1 = 123;
//    	to1.s1 = "X";
//    	
//    	to.items[0] = to1;
//
//    	SimpleObject[] c1 = projections.get(ProvidedRefArrayTo.class).extract(to, "items", SimpleObject[].class);
//
//    	Assert.assertNotNull(c1);
//    	Assert.assertEquals(1, c1.length);
//    	
//    	SimpleObject o1 = c1[0];
//    	
//    	Assert.assertEquals(to1.i1, o1.i1);
//    	Assert.assertEquals(to1.s1, o1.s1);
//    }
}
