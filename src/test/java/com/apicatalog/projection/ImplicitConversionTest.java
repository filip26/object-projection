package com.apicatalog.projection;

import java.time.Instant;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.mapper.ProjectionMapper;
import com.apicatalog.projection.objects.ObjectBasicTypes;
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
    	
    	ObjectBasicTypes object = new ObjectBasicTypes();
    	object.instantValue = Instant.now();
    	object.longValue = 123456l;
    	object.integerValue = 1;
    	object.stringValue = "0.103";
    	object.booleanValue = true;
    	object.stringArray = new String[] { "item 1", "item 2", "item 3" };
    	
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
    }
    
    @Test
    public void testDecomposition() throws ProjectionError, ConverterError {
    	
    	ImplicitConversionTo projection = new ImplicitConversionTo();
    	projection.stringValue = "987654";
    	projection.booleanValue = true;
    	projection.longValue = Instant.now().toEpochMilli();
    	projection.floatValue = 0.0f;
    	projection.doubleValue = 1.23d;

    	Object[] objects = projections.decompose(projection);
    	
    	Assert.assertNotNull(objects);
    	Assert.assertEquals(1, objects.length);
    	Assert.assertEquals(ObjectBasicTypes.class, objects[0].getClass());
    	
    	ObjectBasicTypes object = (ObjectBasicTypes)objects[0];

    	Assert.assertEquals("1.23", object.stringValue);
    	Assert.assertEquals(Instant.ofEpochMilli(projection.longValue), object.instantValue);
    	Assert.assertEquals((Long)987654l, object.longValue);
    	Assert.assertEquals((Integer)1, object.integerValue);
    	Assert.assertEquals(Boolean.FALSE, object.booleanValue);
    }
}
