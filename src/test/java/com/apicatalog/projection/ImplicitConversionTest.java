package com.apicatalog.projection;

import java.time.Instant;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.mapper.ProjectionMapper;
import com.apicatalog.projection.objects.ObjectBasicTypes;
import com.apicatalog.projection.projections.BasicTypesImplicitConversion;

public class ImplicitConversionTest {

	ProjectionFactory projections;
	ProjectionMapper mapper;
	
	@Before
	public void setup() {
		projections = new ProjectionFactory();
		mapper = new ProjectionMapper(projections);		
		
		projections.add(mapper.getMapping(BasicTypesImplicitConversion.class));
	}
	
    @Test
    public void testComposition() throws ProjectionError, ConverterError {
    	
    	ObjectBasicTypes oa = new ObjectBasicTypes();
    	oa.instantValue = Instant.now();
    	oa.longValue = 123456l;
    	oa.integerValue = 1;
    	oa.stringValue = "0.103";
    	oa.booleanValue = true;
    	
    	BasicTypesImplicitConversion pa = projections.compose(BasicTypesImplicitConversion.class, oa);
    	
    	Assert.assertNotNull(pa);
    	
    	Assert.assertEquals(oa.longValue.toString(), pa.stringValue);
    	Assert.assertEquals(Boolean.TRUE, pa.booleanValue);
    	Assert.assertEquals((Long)oa.instantValue.toEpochMilli(), pa.longValue);
    	Assert.assertEquals((Float)1.0f, pa.floatValue);
    	Assert.assertEquals((Double)0.103d, pa.doubleValue);
    }
    
    @Test
    public void testDecomposition() throws ProjectionError, ConverterError {
    	
    	BasicTypesImplicitConversion projection = new BasicTypesImplicitConversion();
    	projection.stringValue = "987654";
    	projection.booleanValue = true;
    	projection.longValue = Instant.now().toEpochMilli();
    	projection.floatValue = 0.0f;
    	projection.doubleValue = 1.23d;

    	Object[] objects = projections.decompose(projection);
    	
    	Assert.assertNotNull(objects);
    	Assert.assertEquals(1, objects.length);
    	Assert.assertEquals(ObjectBasicTypes.class, objects[0].getClass());
    	
    	ObjectBasicTypes object = (ObjectBasicTypes)objects[0];

    	Assert.assertEquals("1.23", object.stringValue);
    	Assert.assertEquals(Instant.ofEpochMilli(projection.longValue), object.instantValue);
    	Assert.assertEquals((Long)987654l, object.longValue);
    	Assert.assertEquals((Integer)1, object.integerValue);
    	Assert.assertEquals(Boolean.FALSE, object.booleanValue);
    }
}
