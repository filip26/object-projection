package com.apicatalog.projection.annotated;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.ExtractionError;
import com.apicatalog.projection.Projection;
import com.apicatalog.projection.Registry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.objects.Reference;
import com.apicatalog.projection.projections.CompositeTo;

public class TwoSourceCompositeTest {

	Projection<CompositeTo> projection;

	@Before
	public void setup() throws CompositionError, ProjectionError {
		projection = Projection.scan(CompositeTo.class).build(Registry.newInstance());
	}	
	
    @Test
    public void testCompose() throws CompositionError, ConverterError {
    	
    	BasicTypes source1 = new BasicTypes();
    	source1.longValue = 123456l;

    	Reference source2 = new Reference();
    	source2.stringValue = "s2value";

    	CompositeTo to = projection.compose(source1, source2);
    	
    	Assert.assertNotNull(to);
    	
    	Assert.assertEquals(source1.longValue, to.source1);
    	Assert.assertEquals(source2.stringValue, to.source2);    	
    }
    
    @Test
    public void testExtract() throws ExtractionError, ConverterError {
    	
    	CompositeTo to = new CompositeTo();
    	to.source1 = 123456l;
    	to.source2 = "source 2 value";

    	BasicTypes object1 = projection.extract(to, BasicTypes.class).orElse(null);
		checkBasic(object1, to.source1);
		
    	Reference object2 = projection.extract(to, Reference.class).orElse(null);
		checkReference(object2, to.source2);
    }
    
    static void checkReference(Object object, String ref) {
    	Assert.assertNotNull(object);
    	Assert.assertEquals(Reference.class, object.getClass());
    	Reference source2 = (Reference)object;
    	Assert.assertEquals(ref, source2.stringValue);
    }
    
    static void checkBasic(Object object, Long ref) {
    	Assert.assertNotNull(object);
    	Assert.assertEquals(BasicTypes.class, object.getClass());
    	BasicTypes source1 = (BasicTypes)object;
    	Assert.assertEquals(ref, source1.longValue);    	
    }
    
}

