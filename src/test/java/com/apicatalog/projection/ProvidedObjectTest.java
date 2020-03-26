package com.apicatalog.projection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.mapper.ProjectionMapper;
import com.apicatalog.projection.objects.ObjectBasicTypes;
import com.apicatalog.projection.objects.SimpleObject;
import com.apicatalog.projection.projections.ProvidedObjectProperty;

public class ProvidedObjectTest {

	ProjectionFactory projections;
	ProjectionMapper mapper;
	
	@Before
	public void setup() {
		projections = new ProjectionFactory();
		mapper = new ProjectionMapper(projections);		
		
		projections.add(mapper.getMapping(ProvidedObjectProperty.class));
	}
	
    @Test
    public void testComposition() throws ProjectionError, ConverterError {
    	
    	ObjectBasicTypes object1 = new ObjectBasicTypes();
    	object1.stringValue = "A B C D E";
    	
    	SimpleObject object2 = new SimpleObject();
    	object2.i1 = 13;
    	object2.s1 = "! @ #";
    	
    	ProvidedObjectProperty projection = projections.compose(ProvidedObjectProperty.class, object1, object2);
    	
    	Assert.assertNotNull(projection);
    	
    	Assert.assertEquals(object1.stringValue, projection.title);
    	
    	Assert.assertNotNull(projection.object);
    	
    	Assert.assertEquals(object2.i1, projection.object.i1);
    	Assert.assertEquals(object2.s1, projection.object.s1);

    }
    
    @Test
    public void testDecomposition() throws ProjectionError, ConverterError {
    	
    	ProvidedObjectProperty projection = new ProvidedObjectProperty();
    	projection.title = "QWERTY ZXCVBN";

    	SimpleObject object2 = new SimpleObject();
    	object2.i1 = 13;
    	object2.s1 = "! @ #";

    	projection.object = object2;
    	
    	Object[] objects = projections.decompose(projection);
    	
    	Assert.assertNotNull(objects);
    	Assert.assertEquals(2, objects.length);
    	
    	Assert.assertNotNull(objects[0]);
    	Assert.assertNotNull(objects[1]);
    	
    	if (ObjectBasicTypes.class.isInstance(objects[1])) {
    		Object tmp = objects[0];
    		objects[0] = objects[1];
    		objects[1] = tmp;
    	}
    	
    	Assert.assertEquals(ObjectBasicTypes.class, objects[0].getClass());
    	
    	ObjectBasicTypes object1ref = (ObjectBasicTypes)objects[0];

    	Assert.assertEquals(projection.title, object1ref.stringValue);    	
    	
    	Assert.assertEquals(SimpleObject.class, objects[1].getClass());
    	
    	SimpleObject object2ref = (SimpleObject)objects[1];
    	
    	Assert.assertEquals(object2.i1, object2ref.i1);
    	Assert.assertEquals(object2.s1, object2ref.s1);

    }
}
