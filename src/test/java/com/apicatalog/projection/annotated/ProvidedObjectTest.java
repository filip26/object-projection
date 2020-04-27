package com.apicatalog.projection.annotated;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.ExtractionError;
import com.apicatalog.projection.Registry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.objects.SimpleObject;
import com.apicatalog.projection.projections.ProvidedObjectTo;

public class ProvidedObjectTest {

	Registry projections;
	
	@Before
	public void setup() throws CompositionError, ProjectionError {
		projections = Registry.newInstance();
		
		projections.register(ProvidedObjectTo.class);
	}
	
    @Test
    public void testCompose() throws CompositionError, ConverterError {
    	
    	BasicTypes object1 = new BasicTypes();
    	object1.stringValue = "A B C D E";
    	
    	SimpleObject object2 = new SimpleObject();
    	object2.i1 = 13;
    	object2.s1 = "! @ #";
    	
    	ProvidedObjectTo projection = projections.get(ProvidedObjectTo.class).compose(object1, object2);
    	
    	Assert.assertNotNull(projection);
    	
    	Assert.assertEquals(object1.stringValue, projection.title);
    	
    	Assert.assertNotNull(projection.object);
    	
    	Assert.assertEquals(object2.i1, projection.object.i1);
    	Assert.assertEquals(object2.s1, projection.object.s1);

    }
    
    @Test
    public void testExtract() throws ExtractionError, ConverterError {
    	
    	ProvidedObjectTo projection = new ProvidedObjectTo();
    	projection.title = "QWERTY ZXCVBN";

    	SimpleObject object2 = new SimpleObject();
    	object2.i1 = 13;
    	object2.s1 = "! @ #";

    	projection.object = object2;
    	
    	BasicTypes object1ref = projections.get(ProvidedObjectTo.class).extract(projection, BasicTypes.class).orElse(null);
    	Assert.assertNotNull(object1ref);
    	Assert.assertEquals(projection.title, object1ref.stringValue);    	
    	
    	SimpleObject object2ref = projections.get(ProvidedObjectTo.class).extract(projection, SimpleObject.class).orElse(null);
    	Assert.assertNotNull(object2ref);
    	Assert.assertEquals(object2.i1, object2ref.i1);
    	Assert.assertEquals(object2.s1, object2ref.s1);

    }
}
