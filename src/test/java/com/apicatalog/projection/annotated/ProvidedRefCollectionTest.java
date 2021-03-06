package com.apicatalog.projection.annotated;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.ExtractionError;
import com.apicatalog.projection.Registry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.SimpleObject;
import com.apicatalog.projection.projections.ProvidedRefCollectionTo;
import com.apicatalog.projection.projections.SimpleObjectTo;
import com.apicatalog.projection.source.SourceObject;

public class ProvidedRefCollectionTest {

	Registry projections;
	
	@Before
	public void setup() throws CompositionError, ProjectionError {
		projections = Registry.newInstance();		
		
		projections
			.register(ProvidedRefCollectionTo.class)
			.register(SimpleObjectTo.class);
	}
	
    @Test
    public void testCompose1() throws CompositionError, ConverterError {
    	
    	Collection<SimpleObject> items = new ArrayList<>();
    	
    	ProvidedRefCollectionTo projection = projections.get(ProvidedRefCollectionTo.class).compose(SourceObject.of("items", items));
    	
    	Assert.assertNotNull(projection);
    	
    	Assert.assertNotNull(projection.items);
    	Assert.assertEquals(0, projection.items.size());
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
    	
    	ProvidedRefCollectionTo projection = projections.get(ProvidedRefCollectionTo.class).compose(SourceObject.of("items", items));
    	
    	Assert.assertNotNull(projection);
    	
    	Assert.assertNotNull(projection.items);
    	Assert.assertEquals(2, projection.items.size());
    	
    	Iterator<SimpleObjectTo> it = projection.items.iterator();
    	
    	SimpleObjectTo p1 = it.next(); 
    	Assert.assertNotNull(p1);
    	Assert.assertEquals(o1.i1, p1.i1);
    	Assert.assertEquals(o1.s1, p1.s1);

    	SimpleObjectTo p2 = it.next(); 
    	Assert.assertNotNull(p2);
    	Assert.assertEquals(o2.i1, p2.i1);
    	Assert.assertEquals(o2.s1, p2.s1);
    }
    
    @Test
    public void testExtract() throws ExtractionError, ConverterError {
    	
    	ProvidedRefCollectionTo to = new ProvidedRefCollectionTo();
    	to.items = new ArrayList<>();
    	
    	SimpleObjectTo to1 = new SimpleObjectTo();
    	to1.i1 = 123;
    	to1.s1 = "X";
    	
    	to.items.add(to1);

    	Collection<SimpleObject> c1 = projections.get(ProvidedRefCollectionTo.class).extractCollection(to, "items", SimpleObject.class).orElse(null);

    	Assert.assertNotNull(c1);
    	Assert.assertEquals(1, c1.size());
    	
    	SimpleObject o1 = c1.iterator().next();
    	
    	Assert.assertEquals(to1.i1, o1.i1);
    	Assert.assertEquals(to1.s1, o1.s1);
    }
}
