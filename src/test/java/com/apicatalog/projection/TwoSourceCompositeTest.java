package com.apicatalog.projection;

import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.converter.ConvertorError;
import com.apicatalog.projection.mapper.ProjectionMapper;
import com.apicatalog.projection.objects.ObjectBasicTypes;
import com.apicatalog.projection.objects.ObjectReference;
import com.apicatalog.projection.projections.TwoSourceComposite;

public class TwoSourceCompositeTest {

	ProjectionFactory projections;
	ProjectionMapper mapper;
	
	@Before
	public void setup() {
		projections = new ProjectionFactory();
		mapper = new ProjectionMapper(projections);
		
		projections.add(mapper.getMapping(TwoSourceComposite.class));
	}	
	
    @Test
    public void testComposition() throws ProjectionError, ConvertorError {
    	
    	ObjectBasicTypes source1 = new ObjectBasicTypes();
    	source1.longValue = 123456l;

    	ObjectReference source2 = new ObjectReference();
    	source2.stringValue = "s2value";

    	TwoSourceComposite projection = projections.compose(TwoSourceComposite.class, source1, source2);
    	
    	Assert.assertNotNull(projection);
    	
    	Assert.assertEquals(source1.longValue, projection.source1);
    	Assert.assertEquals(source2.stringValue, projection.source2);    	
    }
    
    @Test
    public void testDecomposition() throws ProjectionError, ConvertorError {
    	
    	TwoSourceComposite projection = new TwoSourceComposite();
    	projection.source1 = 123456l;
    	projection.source2 = "source 2 value";

    	Object[] objects = projections.decompose(projection);
    	
    	Assert.assertNotNull(objects);
    	Assert.assertEquals(2, objects.length);

    	assertNotNull(objects[0]);
    	Assert.assertEquals(ObjectBasicTypes.class, objects[0].getClass());
    	ObjectBasicTypes source1 = (ObjectBasicTypes)objects[0];
    	Assert.assertEquals(projection.source1, source1.longValue);

    	assertNotNull(objects[1]);
    	Assert.assertEquals(ObjectReference.class, objects[1].getClass());
    	ObjectReference source2 = (ObjectReference)objects[1];
    	Assert.assertEquals(projection.source2, source2.stringValue);
    }
}

