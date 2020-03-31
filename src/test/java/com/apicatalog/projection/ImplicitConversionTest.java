package com.apicatalog.projection;

import java.time.Instant;
import java.util.Arrays;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.mapper.ProjectionMapper;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.projections.ImplicitConversionTo;

public class ImplicitConversionTest {

	ProjectionFactory projections;
	ProjectionMapper mapper;
	
	@Before
	public void setup() {
		projections = new ProjectionFactory();
		mapper = new ProjectionMapper(projections);		
		
		projections.add(mapper.getMapping(ImplicitConversionTo.class));
	}
	
    @Test
    public void testComposition() throws ProjectionError, ConverterError {
    	
    	BasicTypes object = new BasicTypes();
    	object.instantValue = Instant.now();
    	object.longValue = 123456l;
    	object.integerValue = 1;
    	object.stringValue = "0.103";
    	object.booleanValue = true;
    	object.stringArray = new String[] { "item 1", "item 2", "item 3" };
    	object.stringCollection = Arrays.asList("10", "20");
    	
    	ImplicitConversionTo projection = projections.compose(ImplicitConversionTo.class, object);
    	
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
    public void testDecomposition() throws ProjectionError, ConverterError {
    	
    	ImplicitConversionTo projection = new ImplicitConversionTo();
    	projection.stringValue = "987654";
    	projection.booleanValue = true;
    	projection.longValue = Instant.now().toEpochMilli();
    	projection.floatValue = 0.0f;
    	projection.doubleValue = 1.23d;
    	projection.stringCollection = Arrays.asList("1 item", "2 item", "3 item");

    	Object[] objects = projections.decompose(projection);
    	
    	Assert.assertNotNull(objects);
    	Assert.assertEquals(1, objects.length);
    	Assert.assertEquals(BasicTypes.class, objects[0].getClass());
    	
    	BasicTypes object = (BasicTypes)objects[0];

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
    public void testExtraction() throws ProjectionError, ConverterError {
    	
    	ImplicitConversionTo projection = new ImplicitConversionTo();
    	projection.stringValue = "987654";
    	projection.booleanValue = true;
    	projection.longValue = Instant.now().toEpochMilli();
    	projection.floatValue = 0.0f;
    	projection.doubleValue = 1.23d;
    	projection.stringCollection = Arrays.asList("1 item", "2 item", "3 item");

    	BasicTypes object = projections.extract(BasicTypes.class, projection);
    	
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
}
