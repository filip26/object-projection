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
import com.apicatalog.projection.projections.ProvidedReferefenceTo;
import com.apicatalog.projection.projections.SimpleObjectTo;

public class ProvidedRefTest {

	Registry projections;
	
	@Before
	public void setup() throws CompositionError, ProjectionError {
		projections = Registry.newInstance()
						.register(ProvidedReferefenceTo.class)
						.register(SimpleObjectTo.class)
						;
	}
	
    @Test
    public void testCompose() throws CompositionError, ConverterError {
    	
    	BasicTypes object1 = new BasicTypes();
    	object1.stringValue = "A B C D E";
    	
    	SimpleObject object2 = new SimpleObject();
    	object2.i1 = 13;
    	object2.s1 = "! @ #";
    	
    	ProvidedReferefenceTo projection = projections.get(ProvidedReferefenceTo.class).compose(object1, object2);
    	
    	Assert.assertNotNull(projection);
    	
    	Assert.assertEquals(object1.stringValue, projection.title);
    	
    	Assert.assertNotNull(projection.projection);
    	
    	Assert.assertEquals(object2.i1, projection.projection.i1);
    	Assert.assertEquals(object2.s1, projection.projection.s1);

    }
    
    @Test
    public void testExtract() throws ExtractionError, ConverterError {
    	
    	ProvidedReferefenceTo projection1 = new ProvidedReferefenceTo();
    	projection1.title = "QWERTY ZXCVBN";

    	SimpleObjectTo projection2 = new SimpleObjectTo();
    	projection2.i1 = 13;
    	projection2.s1 = "! @ #";

    	projection1.projection = projection2;
    	
    	BasicTypes object1ref = projections.get(ProvidedReferefenceTo.class).extract(projection1, BasicTypes.class).orElse(null);
    	Assert.assertNotNull(object1ref);
    	Assert.assertEquals(projection1.title, object1ref.stringValue);
    	
    	SimpleObject object2ref = projections.get(ProvidedReferefenceTo.class).extract(projection1, SimpleObject.class).orElse(null);
    	Assert.assertNotNull(object1ref);
    	Assert.assertEquals(projection2.i1, object2ref.i1);
    	Assert.assertEquals(projection2.s1, object2ref.s1);

    }
}
