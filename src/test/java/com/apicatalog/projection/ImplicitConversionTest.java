package com.apicatalog.projection;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.converter.InvertibleFunctionError;
import com.apicatalog.projection.objects.ObjectBasicTypes;

public class ImplicitConversionTest {

	ProjectionFactory projection;
	
	@Before
	public void setup() {
//		ProjectionScanner scanner = new ProjectionScanner();
//		
//		MappingIndex index = new MappingIndex();
//		index.add(scanner.scan(TestProjectionAC.class));
//		
//		projection = new ProjectionFactory(index);
	}
	
    @Test
    public void testComposition() throws ProjectionError, InvertibleFunctionError {
    	
    	ObjectBasicTypes oa = new ObjectBasicTypes();
    	oa.instantValue = Instant.now();
    	oa.longValue = 123456l;
    	oa.stringValue = "true";
    	
//    	TestProjectionAC pa = projection.compose(TestProjectionAC.class, oa);
//    	
//    	Assert.assertNotNull(pa);
//    	
//    	Assert.assertEquals(oa.longValue.toString(), pa.projectedString);
//    	Assert.assertEquals(Boolean.TRUE, pa.projectedBoolean);
//    	Assert.assertEquals((Long)oa.instantValue.toEpochMilli(), pa.projectedLong);
    }
    
    @Test
    public void testDecomposition() throws ProjectionError, InvertibleFunctionError {
    	
//    	TestProjectionAC pa = new TestProjectionAC();
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
