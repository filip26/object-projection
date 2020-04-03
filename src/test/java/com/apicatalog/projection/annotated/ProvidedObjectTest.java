package com.apicatalog.projection.annotated;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.objects.SimpleObject;
import com.apicatalog.projection.projections.ProvidedObjectTo;

public class ProvidedObjectTest {

	ProjectionRegistry projections;
	
	@Before
	public void setup() {
		projections = ProjectionRegistry.newInstance();
		
		projections.add(ProvidedObjectTo.class);
	}
	
    @Test
    public void testComposition() throws ProjectionError, ConverterError {
    	
    	BasicTypes object1 = new BasicTypes();
    	object1.stringValue = "A B C D E";
    	
    	SimpleObject object2 = new SimpleObject();
    	object2.i1 = 13;
    	object2.s1 = "! @ #";
    	
    	ProvidedObjectTo projection = projections.compose(ProvidedObjectTo.class, object1, object2);
    	
    	Assert.assertNotNull(projection);
    	
    	Assert.assertEquals(object1.stringValue, projection.title);
    	
    	Assert.assertNotNull(projection.object);
    	
    	Assert.assertEquals(object2.i1, projection.object.i1);
    	Assert.assertEquals(object2.s1, projection.object.s1);

    }
    
    @Test
    public void testDecomposition() throws ProjectionError, ConverterError {
    	
    	ProvidedObjectTo projection = new ProvidedObjectTo();
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
    	
    	if (BasicTypes.class.isInstance(objects[1])) {
    		Object tmp = objects[0];
    		objects[0] = objects[1];
    		objects[1] = tmp;
    	}
    	
    	Assert.assertEquals(BasicTypes.class, objects[0].getClass());
    	
    	BasicTypes object1ref = (BasicTypes)objects[0];

    	Assert.assertEquals(projection.title, object1ref.stringValue);    	
    	
    	Assert.assertEquals(SimpleObject.class, objects[1].getClass());
    	
    	SimpleObject object2ref = (SimpleObject)objects[1];
    	
    	Assert.assertEquals(object2.i1, object2ref.i1);
    	Assert.assertEquals(object2.s1, object2ref.s1);

    }
}
