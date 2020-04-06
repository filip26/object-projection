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
import com.apicatalog.projection.projections.StringCollectionTo;
import com.apicatalog.projection.source.SourceObject;

public class ProvidedCollectionTest {

	ProjectionRegistry projections;
	
	@Before
	public void setup() {
		projections = ProjectionRegistry.newInstance();
		
		projections.register(StringCollectionTo.class);
	}
	
    @Test
    public void testComposition() throws ProjectionError, ConverterError {
    	
    	String href = "https://example.org/provided";

    	Collection<Long> items = Arrays.asList(123l, 234l);
    	
    	StringCollectionTo projection = projections.compose(
    									StringCollectionTo.class, 
    									SourceObject.of("items", items), 
    									SourceObject.of("href",  href)
    									);
    	
    	Assert.assertNotNull(projection);
    	Assert.assertEquals(href, projection.href);
    	
    	Assert.assertNotNull(projection.items);
    	Assert.assertArrayEquals(items.stream().map(l -> Long.toString(l)).collect(Collectors.toList()).toArray(new String[0]), projection.items.toArray(new String[0]));
    }
    
    @Test
    public void testExtraction() throws ProjectionError, ConverterError {
    	
    	StringCollectionTo to = new StringCollectionTo();
    	to.href = "https://example.org/provided";
    	to.items = Arrays.asList("10", "20", "30"); 
    	
    	String href = projections.extract(to, "href", String.class);
    	Assert.assertNotNull(href);
    	Assert.assertEquals(to.href, href);
    	
    	Collection<String> items = projections.extractCollection(to, "items", String.class);
    	Assert.assertNotNull(items);
    	Assert.assertArrayEquals(new String[] {"10", "20", "30"}, items.toArray(new String[0]));
    }    
    
}
