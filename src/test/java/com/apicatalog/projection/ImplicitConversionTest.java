package com.apicatalog.projection;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.converter.ConvertorError;
import com.apicatalog.projection.mapper.ProjectionMapper;
import com.apicatalog.projection.objects.ObjectBasicTypes;

public class ImplicitConversionTest {

	ProjectionFactory projections;
	ProjectionMapper mapper;
	
	@Before
	public void setup() {
		projections = new ProjectionFactory();
		mapper = new ProjectionMapper(projections);		
		
//		projections.add(mapper.getMapping(TestProjectionAC.class));
	}
	
    @Test
    public void testComposition() throws ProjectionError, ConvertorError {
    	
    	ObjectBasicTypes oa = new ObjectBasicTypes();
    	oa.instantValue = Instant.now();
    	oa.longValue = 123456l;
    	oa.stringValue = "true";
    	
//FIXME    	TestProjectionAC pa = projection.compose(TestProjectionAC.class, oa);
//    	
//    	Assert.assertNotNull(pa);
//    	
//    	Assert.assertEquals(oa.longValue.toString(), pa.projectedString);
//    	Assert.assertEquals(Boolean.TRUE, pa.projectedBoolean);
//    	Assert.assertEquals((Long)oa.instantValue.toEpochMilli(), pa.projectedLong);
    }
    
    @Test
    public void testDecomposition() throws ProjectionError, ConvertorError {
    	
//FIXME    	TestProjectionAC pa = new TestProjectionAC();
//    	pa.projectedBoolean = true;
//    	pa.projectedLong = Instant.now().toEpochMilli();
//    	pa.projectedString = "789";
//    	
//    	Object[] oo = projection.decompose(pa);
//    	
//    	Assert.assertNotNull(oo);
//    	Assert.assertEquals(1, oo.length);
//    	Assert.assertEquals(ObjectBasicTypes.class, oo[0].getClass());
//    	
//    	ObjectBasicTypes oa = (ObjectBasicTypes)oo[0];
//
//    	Assert.assertEquals("true", oa.stringValue);
//    	Assert.assertEquals(Instant.ofEpochMilli(pa.projectedLong), oa.instantValue);
//    	Assert.assertEquals((Long)789l, oa.longValue);
    }
}
