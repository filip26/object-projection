package com.apicatalog.projection;

import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.mapper.ProjectionMapper;
import com.apicatalog.projection.objects.BasicTypes;
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
    public void testComposition() throws ProjectionError, ConverterError {
    	
    	BasicTypes source1 = new BasicTypes();
    	source1.longValue = 123456l;

    	ObjectReference source2 = new ObjectReference();
    	source2.stringValue = "s2value";

    	TwoSourceComposite projection = projections.compose(TwoSourceComposite.class, source1, source2);
    	
    	Assert.assertNotNull(projection);
    	
    	Assert.assertEquals(source1.longValue, projection.source1);
    	Assert.assertEquals(source2.stringValue, projection.source2);    	
    }
    
    @Test
    public void testDecomposition() throws ProjectionError, ConverterError {
    	
    	TwoSourceComposite projection = new TwoSourceComposite();
    	projection.source1 = 123456l;
    	projection.source2 = "source 2 value";

    	Object[] objects = projections.decompose(projection);
    	
    	Assert.assertNotNull(objects);
    	Assert.assertEquals(2, objects.length);

    	assertNotNull(objects[0]);
    	assertNotNull(objects[1]);
    	
    	if (BasicTypes.class.isInstance(objects[0])) {
    		checkBasic(objects[0], projection.source1);
    		checkReference(objects[1], projection.source2);
    	} else {
    		checkReference(objects[0], projection.source2);
    		checkBasic(objects[1], projection.source1);
    	}
    }
    
    static void checkReference(Object object, String ref) {
    	Assert.assertEquals(ObjectReference.class, object.getClass());
    	ObjectReference source2 = (ObjectReference)object;
    	Assert.assertEquals(ref, source2.stringValue);
    }
    
    static void checkBasic(Object object, Long ref) {
    	Assert.assertEquals(BasicTypes.class, object.getClass());
    	BasicTypes source1 = (BasicTypes)object;
    	Assert.assertEquals(ref, source1.longValue);    	
    }
    
}

