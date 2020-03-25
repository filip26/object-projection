package com.apicatalog.projection;

import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.converter.ConvertorError;
import com.apicatalog.projection.mapper.ProjectionMapper;
import com.apicatalog.projection.objects.ObjectBasicTypes;
import com.apicatalog.projection.objects.ObjectReference;
import com.apicatalog.projection.projections.ProjectionBasicTypesNameOverride;
import com.apicatalog.projection.projections.TestProjectionAA;
import com.apicatalog.projection.projections.TestProjectionAI;

public class EmbeddedOneToOneTest {

	ProjectionFactory projections;
	ProjectionMapper mapper;
	
	@Before
	public void setup() {
		projections = new ProjectionFactory();
		mapper = new ProjectionMapper(projections);
		
		projections.add(mapper.getMapping(TestProjectionAA.class));
		projections.add(mapper.getMapping(TestProjectionAI.class));
		projections.add(mapper.getMapping(ProjectionBasicTypesNameOverride.class));
	}	
	
    @Test
    public void testComposition() throws ProjectionError, ConvertorError {
    	
    	ObjectBasicTypes oa = new ObjectBasicTypes();
    	oa.longValue = 123456l;

    	ObjectBasicTypes oa2 = new ObjectBasicTypes();
    	oa2.longValue = 987654l;

    	ObjectReference oaa = new ObjectReference();
    	oaa.stringValue = "inherit me";
    	oaa.objectA = oa;

    	TestProjectionAA pa = projections.compose(TestProjectionAA.class, oaa, oa2);
    	
    	Assert.assertNotNull(pa);
    	Assert.assertNotNull(pa.ai);
    	
    	Assert.assertEquals(oa.longValue, pa.ai.projectedLong);
    	Assert.assertEquals(oaa.stringValue, pa.ai.inheritedValue);
    	
//    	Assert.assertNotNull(pa.aa);
//    	Assert.assertEquals(oa2.longValue, pa.aa.projectedLong);
    }
    
//    @Test
//    public void testDecomposition() throws ProjectionError, ConvertorError {
//    	
//    	TestProjectionAI pa = new TestProjectionAI();
//    	pa.projectedLong = 123456l;
//    	pa.inheritedValue = "inherit me";
//
//    	TestProjectionAA paa = new TestProjectionAA();
//    	paa.ai = pa;
//
//    	Object[] oo = projections.decompose(paa);
//    	
//    	Assert.assertNotNull(oo);
//    	Stream.of(oo).forEach(System.out::println);
//    	Assert.assertEquals(1, oo.length);
//    	Assert.assertEquals(ObjectReference.class, oo[0].getClass());
//    	
//    	ObjectReference oaa = (ObjectReference)oo[0];
//    	Assert.assertNotNull(oaa.objectA);
//
//    	Assert.assertEquals(paa.ai.projectedLong, oaa.objectA.longValue);
//    	Assert.assertEquals(paa.ai.inheritedValue, oaa.stringValue);
//    }
}

