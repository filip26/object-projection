package com.apicatalog.projection.annotated;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.ExtractionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.projections.Object2CollectionTo;

public class Object2CollectionTest {

	ProjectionRegistry projections;
	
	@Before
	public void setup() throws CompositionError, ProjectionError {
		projections = ProjectionRegistry.newInstance();
		
		projections.register(Object2CollectionTo.class);
	}
			
    @Test
    public void testCompose() throws CompositionError, ConverterError {
    	
    	BasicTypes object = new BasicTypes();
    	object.doubleValue = 345.56;
    	
    	Object2CollectionTo to = projections.get(Object2CollectionTo.class).compose(object);
    	
    	Assert.assertNotNull(to);
    	Assert.assertNotNull(to.collection);
    	
    	Assert.assertArrayEquals(new String[] { "345.56" }, to.collection.toArray(new String[0]));
    }
    
    @Test
    public void testExtract() throws ExtractionError {
    	
    	ArrayList<String> col = new ArrayList<>();
    	col.add("9.0023");

    	Object2CollectionTo to = new Object2CollectionTo();
    	to.collection = col;

    	BasicTypes object = projections.get(Object2CollectionTo.class).extract(to, BasicTypes.class).orElse(null);
    	
    	Assert.assertNotNull(object);
    	Assert.assertEquals(Double.valueOf(9.0023d), object.doubleValue);
    	
    }
}
