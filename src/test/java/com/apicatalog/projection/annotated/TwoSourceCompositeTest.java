package com.apicatalog.projection.annotated;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.objects.Reference;
import com.apicatalog.projection.projections.CompositeTo;

public class TwoSourceCompositeTest {

	ProjectionRegistry projections;

	@Before
	public void setup() {
		projections = ProjectionRegistry.newInstance()				
						.register(CompositeTo.class)
						;
	}	
	
    @Test
    public void testCompose() throws ProjectionError, ConverterError {
    	
    	BasicTypes source1 = new BasicTypes();
    	source1.longValue = 123456l;

    	Reference source2 = new Reference();
    	source2.stringValue = "s2value";

    	CompositeTo projection = projections.compose(CompositeTo.class, source1, source2);
    	
    	Assert.assertNotNull(projection);
    	
    	Assert.assertEquals(source1.longValue, projection.source1);
    	Assert.assertEquals(source2.stringValue, projection.source2);    	
    }
    
    @Test
    public void testExtract() throws ProjectionError, ConverterError {
    	
    	CompositeTo projection = new CompositeTo();
    	projection.source1 = 123456l;
    	projection.source2 = "source 2 value";

    	BasicTypes object1 = projections.extract(projection, BasicTypes.class);
		checkBasic(object1, projection.source1);
		
    	Reference object2 = projections.extract(projection, Reference.class);
		checkReference(object2, projection.source2);
    }
    
    static void checkReference(Object object, String ref) {
    	Assert.assertEquals(Reference.class, object.getClass());
    	Reference source2 = (Reference)object;
    	Assert.assertEquals(ref, source2.stringValue);
    }
    
    static void checkBasic(Object object, Long ref) {
    	Assert.assertEquals(BasicTypes.class, object.getClass());
    	BasicTypes source1 = (BasicTypes)object;
    	Assert.assertEquals(ref, source1.longValue);    	
    }
    
}

