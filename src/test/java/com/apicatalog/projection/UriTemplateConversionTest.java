package com.apicatalog.projection;

import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.mapper.ProjectionMapper;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.objects.ObjectReference;
import com.apicatalog.projection.projections.UriTemplateConversion;

public class UriTemplateConversionTest {

	ProjectionFactory projections;
	ProjectionMapper mapper;
	
	@Before
	public void setup() {
		projections = new ProjectionFactory();
		mapper = new ProjectionMapper(projections);	
		
		projections.add(mapper.getMapping(UriTemplateConversion.class));
	}
		
    @Test
    public void testComposition() throws ProjectionError, ConverterError {
    	
    	BasicTypes oa = new BasicTypes();
    	oa.longValue = 123l;

    	ObjectReference oaa = new ObjectReference();
    	oaa.stringValue = "ABC"; 

    	UriTemplateConversion pa = projections.compose(UriTemplateConversion.class, oa, oaa);
    	
    	Assert.assertNotNull(pa);
    	Assert.assertEquals("https://www.example.org/" + oa.longValue + "/" + oaa.stringValue, pa.href);    	
    }

    @Test
    public void testDecomposition() throws ProjectionError, ConverterError {
    	UriTemplateConversion projection = new UriTemplateConversion();
    	projection.href = "https://www.example.org/123456/ABC";
    	
    	Object[] objects = projections.decompose(projection);
    	
    	Assert.assertNotNull(objects);
    	Assert.assertEquals(2, objects.length);
    	
    	assertNotNull(objects[0]);
    	assertNotNull(objects[1]);
    	
    	if (BasicTypes.class.isInstance(objects[0])) {
    		checkBasic(objects[0], 123456l);
    		checkReference(objects[1], "ABC");
    	} else {
    		checkReference(objects[0], "ABC");
    		checkBasic(objects[1], 123456l);
    	}
    }
    
    static void checkReference(Object object, String ref) {
    	Assert.assertEquals(ObjectReference.class, object.getClass());
    	ObjectReference source2 = (ObjectReference)object;
    	Assert.assertEquals(ref, source2.stringValue);
    }
    
    static void checkBasic(Object object, Long ref) {
    	Assert.assertEquals(BasicTypes.class, object.getClass());
    	BasicTypes source1 = (BasicTypes)object;
    	Assert.assertEquals(ref, source1.longValue);    	
    }
    
    	
}
