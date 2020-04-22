package com.apicatalog.projection.annotated;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.objects.UriObject;
import com.apicatalog.projection.projections.ImplicitConversionTo;
import com.apicatalog.projection.projections.UriTo;

public class ImplicitConversionTest {

	ProjectionRegistry projections;
	
	@Before
	public void setup() throws CompositionError, ProjectionError {
		projections = ProjectionRegistry.newInstance()
						.register(ImplicitConversionTo.class)
						.register(UriTo.class)
						;
	}
	
    @Test
    public void testCompose() throws CompositionError, ConverterError {
    	
    	BasicTypes object = new BasicTypes();
    	object.instantValue = Instant.now();
    	object.longValue = 123456l;
    	object.integerValue = 1;
    	object.stringValue = "0.103";
    	object.booleanValue = true;
    	object.stringArray = new String[] { "item 1", "item 2", "item 3" };
    	object.stringCollection = Arrays.asList("10", "20");
    	
    	ImplicitConversionTo projection = projections.get(ImplicitConversionTo.class).compose(object);
    	
    	Assert.assertNotNull(projection);
    	
    	Assert.assertEquals(object.longValue.toString(), projection.stringValue);
    	Assert.assertEquals(Boolean.TRUE, projection.booleanValue);
    	Assert.assertEquals((Long)object.instantValue.toEpochMilli(), projection.longValue);
    	Assert.assertEquals((Float)1.0f, projection.floatValue);
    	Assert.assertEquals((Double)0.103d, projection.doubleValue);
    	
    	Assert.assertNotNull(projection.stringCollection);
    	
    	Assert.assertEquals(3, projection.stringCollection.size());
    	
    	Iterator<String> it = projection.stringCollection.iterator();
    	
    	Assert.assertEquals("item 1", it.next());
    	Assert.assertEquals("item 2", it.next());
    	Assert.assertEquals("item 3", it.next());
    	
    	Assert.assertNotNull(projection.longArray);
    	
    	Assert.assertEquals(2, projection.longArray.length);
    	Assert.assertEquals(Long.valueOf(10l), projection.longArray[0]);
    	Assert.assertEquals(Long.valueOf(20l), projection.longArray[1]);
    }
    
    @Test
    public void testExtract() throws CompositionError, ConverterError {
    	
    	ImplicitConversionTo projection = new ImplicitConversionTo();
    	projection.stringValue = "987654";
    	projection.booleanValue = true;
    	projection.longValue = Instant.now().toEpochMilli();
    	projection.floatValue = 0.0f;
    	projection.doubleValue = 1.23d;
    	projection.stringCollection = Arrays.asList("1 item", "2 item", "3 item");

    	BasicTypes object = projections.get(ImplicitConversionTo.class).extract(projection, BasicTypes.class).orElse(null);
    	Assert.assertNotNull(object);
    	
    	Assert.assertEquals("1.23", object.stringValue);
    	Assert.assertEquals(Instant.ofEpochMilli(projection.longValue), object.instantValue);
    	Assert.assertEquals((Long)987654l, object.longValue);
    	Assert.assertEquals((Integer)1, object.integerValue);
    	Assert.assertEquals(Boolean.FALSE, object.booleanValue);
    	
    	Assert.assertNotNull(object.stringArray);
    	Assert.assertEquals(3, object.stringArray.length);

    	Assert.assertEquals("1 item", object.stringArray[0]);
    	Assert.assertEquals("2 item", object.stringArray[1]);
    	Assert.assertEquals("3 item", object.stringArray[2]);
    }
       
    @Test
    public void testCompose2() throws CompositionError, ConverterError {
    	
    	UriObject object = new UriObject();
		object.uri = URI.create("https://example.org/a/b/c");
    	
    	UriTo projection = projections.get(UriTo.class).compose(object);
    	
    	Assert.assertNotNull(projection);
		Assert.assertEquals(object.uri.toString(), projection.uri);
    }
    
    @Test
    public void testExtract2() throws CompositionError, ConverterError {
    	
    	UriTo to = new UriTo();
		to.uri = "https://example.org/a/b/c";
    	
    	UriObject object = projections.get(UriTo.class).extract(to, UriObject.class).orElse(null);
    	
    	Assert.assertNotNull(object);
    	Assert.assertEquals(UriObject.class, object.getClass());
		Assert.assertEquals(URI.create(to.uri), object.uri);
    }
}
