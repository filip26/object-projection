package com.apicatalog.projection.annotated;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.objects.ObjectsCollection;
import com.apicatalog.projection.projections.NameOverrideTo;
import com.apicatalog.projection.projections.RefCollectionTo;
import com.apicatalog.projection.source.SourceType;

public class RefCollectionTest {

	ProjectionRegistry projections;
	
	@Before
	public void setup() throws ProjectionError, ProjectionBuilderError {
		projections = ProjectionRegistry.newInstance()
						.register(RefCollectionTo.class)
						.register(NameOverrideTo.class)
						;
	}
	
    @Test
    public void testCompose() throws ProjectionError, ConverterError {
    	
    	BasicTypes oa = new BasicTypes();
    	oa.booleanValue = true;
    	oa.doubleValue = 123.456d;
    	
    	ObjectsCollection oc = new ObjectsCollection();
    	oc.items = new ArrayList<>();
    	oc.items.add(oa);
    	
    	RefCollectionTo ca = projections.get(RefCollectionTo.class).compose(oc);
    	
    	Assert.assertNotNull(ca);
    	Assert.assertNotNull(ca.items);    	
    	Assert.assertEquals(1, ca.items.size());
    	
    	NameOverrideTo pa = ca.items.iterator().next();
    	
    	Assert.assertEquals(oa.booleanValue, pa.projectedBoolean);
    	Assert.assertEquals(oa.doubleValue, pa.projectedDouble);
    }

    @Test
    public void testComposerSources() throws ProjectionError, ConverterError {
    	
    	Projection<RefCollectionTo> projection = projections.get(RefCollectionTo.class);
    	
    	Assert.assertNotNull(projection);
    	Assert.assertNotNull(projection.getComposer());    	
    	Assert.assertNotNull(projection.getComposer().getSourceTypes());
    	
    	Assert.assertEquals(1, projection.getComposer().getSourceTypes().size());
    	Assert.assertTrue(projection.getComposer().getSourceTypes().contains(SourceType.of(ObjectsCollection.class)));
    }
    
    @Test
    public void testComposerDependencies() throws ProjectionError, ConverterError {
    	
    	Projection<RefCollectionTo> projection = projections.get(RefCollectionTo.class);
    	
    	Assert.assertNotNull(projection);
    	Assert.assertNotNull(projection.getComposer());    	
    	Assert.assertNotNull(projection.getComposer().getDependencies());
    	
    	Assert.assertEquals(1, projection.getComposer().getDependencies().size());
    	Assert.assertTrue(projection.getComposer().getDependencies().contains(NameOverrideTo.class.getCanonicalName()));
    }

    @Test
    public void testExtract() throws ProjectionError, ConverterError {
    	
    	RefCollectionTo to = new RefCollectionTo();
    	to.items = new ArrayList<>();
    	
    	NameOverrideTo to2 = new NameOverrideTo();
    	to2.projectedString = "ABC";

    	NameOverrideTo to3 = new NameOverrideTo();
    	to3.projectedString = "XYZ";
    	
    	to.items.add(to2);
    	to.items.add(to3);

    	ObjectsCollection object = projections.get(RefCollectionTo.class).extract(to, ObjectsCollection.class);

    	Assert.assertNotNull(object);
    	Assert.assertNotNull(object.items);
    	Assert.assertEquals(2, object.items.size());
    	
    	Iterator<BasicTypes> it = object.items.iterator();
    	
    	BasicTypes oa1 = it.next();
    	Assert.assertNotNull(oa1);
    	
    	Assert.assertEquals(to2.projectedString, oa1.stringValue);
    	    	
    	BasicTypes oa2 = it.next();
    	Assert.assertNotNull(oa2);

    	
    	Assert.assertEquals(to3.projectedString, oa2.stringValue);
    			
    }
    
    @Test
    public void testExtractorSources() throws ProjectionError, ConverterError {
    	
    	Projection<RefCollectionTo> projection = projections.get(RefCollectionTo.class);
    	
    	Assert.assertNotNull(projection);
    	Assert.assertNotNull(projection.getExtractor());    	
    	Assert.assertNotNull(projection.getExtractor().getSourceTypes());
    	
    	Assert.assertEquals(1, projection.getExtractor().getSourceTypes().size());
    	Assert.assertTrue(projection.getExtractor().getSourceTypes().contains(SourceType.of(ObjectsCollection.class)));
    }

    @Test
    public void testExtractorDependencies() throws ProjectionError, ConverterError {
    	
    	Projection<RefCollectionTo> projection = projections.get(RefCollectionTo.class);
    	
    	Assert.assertNotNull(projection);
    	Assert.assertNotNull(projection.getExtractor());    	
    	Assert.assertNotNull(projection.getExtractor().getDependencies());
    	
    	Assert.assertEquals(1, projection.getExtractor().getDependencies().size());
    	Assert.assertTrue(projection.getExtractor().getDependencies().contains(NameOverrideTo.class.getCanonicalName()));
    }

}
