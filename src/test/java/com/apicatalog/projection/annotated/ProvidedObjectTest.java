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
		
		projections.register(ProvidedObjectTo.class);
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
    public void testExtraction() throws ProjectionError, ConverterError {
    	
    	ProvidedObjectTo projection = new ProvidedObjectTo();
    	projection.title = "QWERTY ZXCVBN";

    	SimpleObject object2 = new SimpleObject();
    	object2.i1 = 13;
    	object2.s1 = "! @ #";

    	projection.object = object2;
    	
    	BasicTypes object1ref = new BasicTypes();
    	SimpleObject object2ref = new SimpleObject();
    	
    	projections.extract(projection, object1ref, object2ref);
    	

    	Assert.assertEquals(projection.title, object1ref.stringValue);    	
    	    	
    	Assert.assertEquals(object2.i1, object2ref.i1);
    	Assert.assertEquals(object2.s1, object2ref.s1);

    }
}
