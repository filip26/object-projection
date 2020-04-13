package com.apicatalog.projection.annotated;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.objects.Reference;
import com.apicatalog.projection.projections.UriTemplateConversion;

public class UriTemplateConversionTest {

	ProjectionRegistry projections;
	
	@Before
	public void setup() throws ProjectionError {
		projections = ProjectionRegistry.newInstance()
						.register(UriTemplateConversion.class)
						;
	}
		
    @Test
    public void testCompose() throws ProjectionError, ConverterError {
    	
    	BasicTypes oa = new BasicTypes();
    	oa.longValue = 123l;

    	Reference oaa = new Reference();
    	oaa.stringValue = "ABC"; 

    	UriTemplateConversion pa = projections.compose(UriTemplateConversion.class, oa, oaa);
    	
    	Assert.assertNotNull(pa);
    	Assert.assertEquals("https://www.example.org/" + oa.longValue + "/" + oaa.stringValue, pa.href);    	
    }

    @Test
    public void testExtract() throws ProjectionError, ConverterError {
    	UriTemplateConversion projection = new UriTemplateConversion();
    	projection.href = "https://www.example.org/123456/ABC";
    	
    	BasicTypes object1 = projections.extract(projection, BasicTypes.class);
		checkBasic(object1, 123456l);
		
    	Reference object2 = projections.extract(projection, Reference.class);    	
		checkReference(object2, "ABC");
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
