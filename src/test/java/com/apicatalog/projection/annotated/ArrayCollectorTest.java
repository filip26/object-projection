package com.apicatalog.projection.annotated;

import java.time.Instant;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.ExtractionError;
import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.projections.ArrayCollectorTo;

public class ArrayCollectorTest {

	Projection<ArrayCollectorTo> projection;
	
	@Before
	public void setup() throws CompositionError, ProjectionError {
		projection = Projection.scan(ArrayCollectorTo.class).build(ProjectionRegistry.newInstance());		
	}
	
    @Test
    public void testCompose() throws CompositionError, ConverterError {
    	
    	BasicTypes o1 = new BasicTypes();
    	o1.integerValue = 12345;
    	o1.booleanValue = true;
    	o1.instantValue = Instant.now();
    	o1.doubleValue = 12.34d;
    	o1.stringArray = new String[] { "s1", "s2" };
    	
    	ArrayCollectorTo to = projection.compose(o1);
    	
    	Assert.assertNotNull(to);
    	
    	Assert.assertNotNull(to.stringCollection);
    	
    	Assert.assertArrayEquals(
    			new String[] {o1.integerValue.toString(), o1.booleanValue.toString() },
    			to.stringCollection.toArray(new String[0]));
    	
    	Assert.assertNotNull(to.objectArray);
    	
    	Assert.assertArrayEquals(
    			new Object[] {o1.doubleValue, o1.booleanValue, o1.instantValue, o1.stringArray },
    			to.objectArray);
    }
    
    @Test
    public void testExtract() throws ExtractionError, ConverterError {
    	
    	ArrayCollectorTo to = new ArrayCollectorTo();
    	to.objectArray = new Object[] { 1.234d, false, Instant.now(), new String[] { "s1", "s2", "s3" }};
    	to.stringCollection = Arrays.asList(new String[] { "951", "false" });

    	BasicTypes object = projection.extract(to, BasicTypes.class).orElse(null);

    	Assert.assertNotNull(object);
    	
    	Assert.assertEquals(to.objectArray[0], object.doubleValue);
    	Assert.assertEquals(to.objectArray[1], object.booleanValue);
    	Assert.assertEquals(to.objectArray[2], object.instantValue);
    	Assert.assertArrayEquals((String[])to.objectArray[3], object.stringArray);
    	
    	Assert.assertEquals(Integer.valueOf(to.stringCollection.toArray(new String[0])[0]), object.integerValue);
    	Assert.assertEquals(Boolean.valueOf(to.stringCollection.toArray(new String[0])[1]), object.booleanValue);    	
    }
}
