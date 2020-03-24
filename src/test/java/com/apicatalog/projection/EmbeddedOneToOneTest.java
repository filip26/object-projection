package com.apicatalog.projection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.converter.InvertibleFunctionError;
import com.apicatalog.projection.mapper.ProjectionMapper;
import com.apicatalog.projection.mapping.MappingIndex;
import com.apicatalog.projection.objects.ObjectBasicTypes;
import com.apicatalog.projection.objects.ObjectReference;
import com.apicatalog.projection.projections.ProjectionBasicTypesNameOverride;
import com.apicatalog.projection.projections.TestProjectionAA;
import com.apicatalog.projection.projections.TestProjectionAI;

public class EmbeddedOneToOneTest {

	ProjectionFactory projection;
	
	@Before
	public void setup() {
		ProjectionMapper scanner = new ProjectionMapper();
		
		MappingIndex index = new MappingIndex();
		index.add(scanner.getMapping(TestProjectionAA.class));
		index.add(scanner.getMapping(TestProjectionAI.class));
		index.add(scanner.getMapping(ProjectionBasicTypesNameOverride.class));
		
		projection = new ProjectionFactory(index);
	}
	
    @Test
    public void testComposition() throws ProjectionError, InvertibleFunctionError {
    	
    	ObjectBasicTypes oa = new ObjectBasicTypes();
    	oa.longValue = 123456l;

    	ObjectBasicTypes oa2 = new ObjectBasicTypes();
    	oa2.longValue = 987654l;

    	ObjectReference oaa = new ObjectReference();
    	oaa.stringValue = "inherit me";
    	oaa.objectA = oa;

    	TestProjectionAA pa = projection.compose(TestProjectionAA.class, oaa, oa2);
    	
    	Assert.assertNotNull(pa);
    	Assert.assertNotNull(pa.ai);
    	
    	Assert.assertEquals(oa.longValue, pa.ai.projectedLong);
    	Assert.assertEquals(oaa.stringValue, pa.ai.inheritedValue);
    	
    	Assert.assertNotNull(pa.aa);
    	Assert.assertEquals(oa2.longValue, pa.aa.projectedLong);
    }
    
    @Test
    public void testDecomposition() throws ProjectionError, InvertibleFunctionError {
    	
    	TestProjectionAI pa = new TestProjectionAI();
    	pa.projectedLong = 123456l;
    	pa.inheritedValue = "inherit me";

    	TestProjectionAA paa = new TestProjectionAA();
    	paa.ai = pa;

    	Object[] oo = projection.decompose(paa);
    	
    	Assert.assertNotNull(oo);
    	Assert.assertEquals(1, oo.length);
    	Assert.assertEquals(ObjectReference.class, oo[0].getClass());
    	
    	ObjectReference oaa = (ObjectReference)oo[0];
    	Assert.assertNotNull(oaa.objectA);

    	Assert.assertEquals(paa.ai.projectedLong, oaa.objectA.longValue);
    	Assert.assertEquals(paa.ai.inheritedValue, oaa.stringValue);
    }
}

