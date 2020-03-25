package com.apicatalog.projection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.converter.ConvertorError;
import com.apicatalog.projection.mapper.ProjectionMapper;
import com.apicatalog.projection.objects.ObjectBasicTypes;
import com.apicatalog.projection.objects.ObjectReference;
import com.apicatalog.projection.projections.BasicPropertyNameOverride;
import com.apicatalog.projection.projections.ContainsReferencedProjection;
import com.apicatalog.projection.projections.TwoSourceComposite;

public class ReferencedCompositeTest {

	ProjectionFactory projections;
	ProjectionMapper mapper;
	
	@Before
	public void setup() {
		projections = new ProjectionFactory();
		mapper = new ProjectionMapper(projections);
		
		projections.add(mapper.getMapping(ContainsReferencedProjection.class));
		projections.add(mapper.getMapping(TwoSourceComposite.class));
		projections.add(mapper.getMapping(BasicPropertyNameOverride.class));
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

    	ContainsReferencedProjection pa = projections.compose(ContainsReferencedProjection.class, oaa, oa2);
    	
    	Assert.assertNotNull(pa);
    	Assert.assertNotNull(pa.ref);
    	
    	Assert.assertEquals(oa.longValue, pa.ref.source1);
    	Assert.assertEquals(oaa.stringValue, pa.ref.source2);
    	
//    	Assert.assertNotNull(pa.aa);
//    	Assert.assertEquals(oa2.longValue, pa.aa.projectedLong);
    }
    
    @Test
    public void testDecomposition() throws ProjectionError, ConvertorError {
    	
    	TwoSourceComposite pa = new TwoSourceComposite();
    	pa.source1 = 123456l;
    	pa.source2 = "source 2 value";

    	ContainsReferencedProjection paa = new ContainsReferencedProjection();
    	paa.ref = pa;

    	Object[] oo = projections.decompose(paa);
    	
    	Assert.assertNotNull(oo);

    	Assert.assertEquals(1, oo.length);
    	Assert.assertEquals(ObjectReference.class, oo[0].getClass());
    	
    	ObjectReference oaa = (ObjectReference)oo[0];
    	Assert.assertNotNull(oaa.objectA);

    	Assert.assertEquals(paa.ref.source1, oaa.objectA.longValue);
    	Assert.assertEquals(paa.ref.source2, oaa.stringValue);
    }
}

