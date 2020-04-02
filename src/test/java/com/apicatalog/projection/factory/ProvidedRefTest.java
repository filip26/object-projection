package com.apicatalog.projection.factory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.mapper.ProjectionMapper;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.objects.SimpleObject;
import com.apicatalog.projection.projections.ProvidedReferefenceTo;
import com.apicatalog.projection.projections.SimpleObjectTo;

public class ProvidedRefTest {

	ProjectionFactory projections;
	ProjectionMapper mapper;
	
	@Before
	public void setup() {
		projections = new ProjectionFactory();
		mapper = new ProjectionMapper(projections);		
		
		projections.add(mapper.getProjection(ProvidedReferefenceTo.class));
		projections.add(mapper.getProjection(SimpleObjectTo.class));
	}
	
    @Test
    public void testComposition() throws ProjectionError, ConverterError {
    	
    	BasicTypes object1 = new BasicTypes();
    	object1.stringValue = "A B C D E";
    	
    	SimpleObject object2 = new SimpleObject();
    	object2.i1 = 13;
    	object2.s1 = "! @ #";
    	
    	ProvidedReferefenceTo projection = projections.compose(ProvidedReferefenceTo.class, object1, object2);
    	
    	Assert.assertNotNull(projection);
    	
    	Assert.assertEquals(object1.stringValue, projection.title);
    	
    	Assert.assertNotNull(projection.projection);
    	
    	Assert.assertEquals(object2.i1, projection.projection.i1);
    	Assert.assertEquals(object2.s1, projection.projection.s1);

    }
    
    @Test
    public void testDecomposition() throws ProjectionError, ConverterError {
    	
    	ProvidedReferefenceTo projection1 = new ProvidedReferefenceTo();
    	projection1.title = "QWERTY ZXCVBN";

    	SimpleObjectTo projection2 = new SimpleObjectTo();
    	projection2.i1 = 13;
    	projection2.s1 = "! @ #";

    	projection1.projection = projection2;
    	
    	Object[] objects = projections.decompose(projection1);
    	
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

    	Assert.assertEquals(projection1.title, object1ref.stringValue);    	
    	
    	Assert.assertEquals(SimpleObject.class, objects[1].getClass());
    	
    	SimpleObject object2ref = (SimpleObject)objects[1];
    	
    	Assert.assertEquals(projection2.i1, object2ref.i1);
    	Assert.assertEquals(projection2.s1, object2ref.s1);

    }
}
