package com.apicatalog.projection.annotated;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.ExtractionError;
import com.apicatalog.projection.Projection;
import com.apicatalog.projection.Registry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.objects.ObjectsCollection;
import com.apicatalog.projection.projections.NameOverrideTo;
import com.apicatalog.projection.projections.RefCollectionTo;
import com.apicatalog.projection.source.SourceType;

public class RefCollectionTest {

	Registry projections;
	
	@Before
	public void setup() throws CompositionError, ProjectionError {
		projections = Registry.newInstance()
						.register(RefCollectionTo.class)
						.register(NameOverrideTo.class)
						;
	}
	
    @Test
    public void testCompose() throws CompositionError, ConverterError {
    	
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
    public void testComposerSources() throws CompositionError, ConverterError {
    	
    	Projection<RefCollectionTo> projection = projections.get(RefCollectionTo.class);
    	
    	Assert.assertNotNull(projection);
    	Assert.assertTrue(projection.getComposer().isPresent());    	
    	Assert.assertNotNull(projection.getComposer().get().getSourceTypes());
    	
    	Assert.assertEquals(1, projection.getComposer().get().getSourceTypes().size());
    	Assert.assertTrue(projection.getComposer().get().getSourceTypes().contains(SourceType.of(ObjectsCollection.class)));
    }
    
    @Test
    public void testComposerDependencies() throws CompositionError, ConverterError {
    	
    	Projection<RefCollectionTo> projection = projections.get(RefCollectionTo.class);
    	
    	Assert.assertNotNull(projection);
    	Assert.assertTrue(projection.getComposer().isPresent());    	
    	Assert.assertNotNull(projection.getComposer().get().getDependencies());
    	
    	Assert.assertEquals(1, projection.getComposer().get().getDependencies().size());
    	Assert.assertTrue(projection.getComposer().get().getDependencies().contains(NameOverrideTo.class.getCanonicalName()));
    }

    @Test
    public void testExtract() throws ExtractionError, ConverterError {
    	
    	RefCollectionTo to = new RefCollectionTo();
    	to.items = new ArrayList<>();
    	
    	NameOverrideTo to2 = new NameOverrideTo();
    	to2.projectedString = "ABC";

    	NameOverrideTo to3 = new NameOverrideTo();
    	to3.projectedString = "XYZ";
    	
    	to.items.add(to2);
    	to.items.add(to3);

    	Optional<ObjectsCollection> object = projections.get(RefCollectionTo.class).extract(to, ObjectsCollection.class);

    	Assert.assertTrue(object.isPresent());
    	Assert.assertNotNull(object.get().items);
    	Assert.assertEquals(2, object.get().items.size());
    	
    	Iterator<BasicTypes> it = object.get().items.iterator();
    	
    	BasicTypes oa1 = it.next();
    	Assert.assertNotNull(oa1);
    	
    	Assert.assertEquals(to2.projectedString, oa1.stringValue);
    	    	
    	BasicTypes oa2 = it.next();
    	Assert.assertNotNull(oa2);

    	
    	Assert.assertEquals(to3.projectedString, oa2.stringValue);
    			
    }
    
    @Test
    public void testExtractorSources() throws CompositionError, ConverterError {
    	
    	Projection<RefCollectionTo> projection = projections.get(RefCollectionTo.class);
    	
    	Assert.assertNotNull(projection);
    	Assert.assertTrue(projection.getExtractor().isPresent());    	
    	Assert.assertNotNull(projection.getExtractor().get().getSourceTypes());
    	
    	Assert.assertEquals(1, projection.getExtractor().get().getSourceTypes().size());
    	Assert.assertTrue(projection.getExtractor().get().getSourceTypes().contains(SourceType.of(ObjectsCollection.class)));
    }

    @Test
    public void testExtractorDependencies() throws CompositionError, ConverterError {
    	
    	Projection<RefCollectionTo> projection = projections.get(RefCollectionTo.class);
    	
    	Assert.assertNotNull(projection);
    	Assert.assertTrue(projection.getExtractor().isPresent());    	
    	Assert.assertNotNull(projection.getExtractor().get().getDependencies());
    	
    	Assert.assertEquals(1, projection.getExtractor().get().getDependencies().size());
    	Assert.assertTrue(projection.getExtractor().get().getDependencies().contains(NameOverrideTo.class.getCanonicalName()));
    }

}
