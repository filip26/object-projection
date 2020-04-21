package com.apicatalog.projection.annotated;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.objects.Reference;
import com.apicatalog.projection.projections.CompositeTo;
import com.apicatalog.projection.projections.NameOverrideTo;
import com.apicatalog.projection.projections.ReferenceTo;

public class ReferenceCompositeTest {

	ProjectionRegistry projections;
	
	@Before
	public void setup() throws ProjectionError, ProjectionBuilderError {
		projections = ProjectionRegistry.newInstance()		
						.register(ReferenceTo.class)
						.register(CompositeTo.class)
						.register(NameOverrideTo.class)
						;
	}
	
    @Test
    public void testCompose() throws ProjectionError, ConverterError {
    	
    	BasicTypes object1 = new BasicTypes();
    	object1.longValue = 123456l;

    	BasicTypes object2 = new BasicTypes();
    	object2.longValue = 987654l;

    	Reference object3 = new Reference();
    	object3.stringValue = "inherit me";
    	object3.objectA = object1;

    	ReferenceTo projection = projections.get(ReferenceTo.class).compose(object3, object2);
    	
    	Assert.assertNotNull(projection);
    	Assert.assertNotNull(projection.ref);
    	
    	Assert.assertEquals(object1.longValue, projection.ref.source1);
    	Assert.assertEquals(object3.stringValue, projection.ref.source2);    	
    }
    
    @Test
    public void testExtract() throws ProjectionError, ConverterError {
    	
    	CompositeTo projection1 = new CompositeTo();
    	projection1.source1 = 123456l;
    	projection1.source2 = "source 2 value";

    	ReferenceTo projection2 = new ReferenceTo();
    	projection2.ref = projection1;

    	Reference object = projections.get(ReferenceTo.class).extract(projection2, Reference.class).orElse(null);
    	
    	Assert.assertNotNull(object);

    	Assert.assertEquals(projection2.ref.source2, object.stringValue);
    	
    	Assert.assertNotNull(object.objectA);

    	Assert.assertEquals(projection2.ref.source1, object.objectA.longValue);

    }
}

