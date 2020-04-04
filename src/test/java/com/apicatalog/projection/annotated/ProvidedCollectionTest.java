package com.apicatalog.projection.annotated;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.NamedObject;
import com.apicatalog.projection.projections.StringCollectionTo;

public class ProvidedCollectionTest {

	ProjectionRegistry projections;
	
	@Before
	public void setup() {
		projections = ProjectionRegistry.newInstance();
		
		projections.add(StringCollectionTo.class);
	}
	
    @Test
    public void testComposition() throws ProjectionError, ConverterError {
    	
    	String href = "https://example.org/provided";

    	Collection<Long> items = Arrays.asList(123l, 234l);
    	
    	StringCollectionTo projection = projections.compose(
    									StringCollectionTo.class, 
    									NamedObject.of("items", items), 
    									NamedObject.of("href",  href)
    									);
    	
    	Assert.assertNotNull(projection);
    	Assert.assertEquals(href, projection.href);
    	
    	Assert.assertNotNull(projection.items);
    	Assert.assertArrayEquals(items.stream().map(l -> Long.toString(l)).collect(Collectors.toList()).toArray(new String[0]), projection.items.toArray(new String[0]));
    }
    
    @Test
    public void testDecomposition() throws ProjectionError, ConverterError {
    	
    	StringCollectionTo to = new StringCollectionTo();
    	to.href = "https://example.org/provided";
    	to.items = Arrays.asList("10", "20", "30"); 
    	
    	Object[] objects = projections.decompose(to);
    	
    	Assert.assertNotNull(objects);
    	Assert.assertEquals(2, objects.length);
    	
    	Assert.assertTrue(NamedObject.class.isInstance(objects[0]));    	
    	@SuppressWarnings("unchecked")
		NamedObject<Object> object1 = (NamedObject<Object>)objects[0];
    	
    	Assert.assertEquals("href", object1.getName());
    	Assert.assertEquals(to.href, object1.getObject());

    	Assert.assertTrue(NamedObject.class.isInstance(objects[1]));
    	@SuppressWarnings("unchecked")
		NamedObject<Object> object2 = (NamedObject<Object>)objects[1];
    	
    	Assert.assertEquals("items", object2.getName());
    	Assert.assertTrue(Collection.class.isInstance(object2.getObject()));
    	Assert.assertArrayEquals(new String[] {"10", "20", "30"}, ((Collection<?>)object2.getObject()).toArray(new String[0]));
    }    
    
}
