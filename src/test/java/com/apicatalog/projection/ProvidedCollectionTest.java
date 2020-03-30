package com.apicatalog.projection;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.mapper.ProjectionMapper;
import com.apicatalog.projection.projections.StringCollectionTo;

public class ProvidedCollectionTest {

	ProjectionFactory projections;
	ProjectionMapper mapper;
	
	@Before
	public void setup() {
		projections = new ProjectionFactory();
		mapper = new ProjectionMapper(projections);	
		
		projections.add(mapper.getMapping(StringCollectionTo.class));
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
    	
    	Assert.assertEquals(to.href, objects[0]);
    	
    	Assert.assertTrue(Collection.class.isInstance(objects[1]));
    	Assert.assertArrayEquals(new String[] {"10", "20", "30"}, ((Collection<String>)objects[1]).toArray(new String[0]));
    }    
    
}
