package com.apicatalog.projection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.mapper.ProjectionMapper;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.objects.Reference;
import com.apicatalog.projection.projections.NameOverrideTo;
import com.apicatalog.projection.projections.ReferenceTo;
import com.apicatalog.projection.projections.CompositeTo;

public class ReferenceCompositeTest {

	ProjectionFactory projections;
	ProjectionMapper mapper;
	
	@Before
	public void setup() {
		projections = new ProjectionFactory();
		mapper = new ProjectionMapper(projections);
		
		projections.add(mapper.getMapping(ReferenceTo.class));
		projections.add(mapper.getMapping(CompositeTo.class));
		projections.add(mapper.getMapping(NameOverrideTo.class));
	}	
	
    @Test
    public void testComposition() throws ProjectionError, ConverterError {
    	
    	BasicTypes object1 = new BasicTypes();
    	object1.longValue = 123456l;

    	BasicTypes object2 = new BasicTypes();
    	object2.longValue = 987654l;

    	Reference object3 = new Reference();
    	object3.stringValue = "inherit me";
    	object3.objectA = object1;

    	ReferenceTo projection = projections.compose(ReferenceTo.class, object3, object2);
    	
    	Assert.assertNotNull(projection);
    	Assert.assertNotNull(projection.ref);
    	
    	Assert.assertEquals(object1.longValue, projection.ref.source1);
    	Assert.assertEquals(object3.stringValue, projection.ref.source2);    	
    }
    
    @Test
    public void testDecomposition() throws ProjectionError, ConverterError {
    	
    	CompositeTo projection1 = new CompositeTo();
    	projection1.source1 = 123456l;
    	projection1.source2 = "source 2 value";

    	ReferenceTo projection2 = new ReferenceTo();
    	projection2.ref = projection1;

    	Object[] objects = projections.decompose(projection2);
    	
    	Assert.assertNotNull(objects);

    	Assert.assertEquals(1, objects.length);
    	Assert.assertEquals(Reference.class, objects[0].getClass());
    	
    	Reference object = (Reference)objects[0];
    	Assert.assertNotNull(object.objectA);

    	Assert.assertEquals(projection2.ref.source1, object.objectA.longValue);
    	Assert.assertEquals(projection2.ref.source2, object.stringValue);
    }
    
    @Test
    public void testExtraction() throws ProjectionError, ConverterError {
    	
    	CompositeTo projection1 = new CompositeTo();
    	projection1.source1 = 123456l;
    	projection1.source2 = "source 2 value";

    	ReferenceTo projection2 = new ReferenceTo();
    	projection2.ref = projection1;

    	Reference object = projections.extract(Reference.class, projection2);
    	
    	Assert.assertNotNull(object);

    	Assert.assertEquals(projection2.ref.source2, object.stringValue);
    	
    	Assert.assertNotNull(object.objectA);

    	Assert.assertEquals(projection2.ref.source1, object.objectA.longValue);

    }
}
