package com.apicatalog.projection.mapper;

import java.time.Instant;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.mapping.ProjectionMapping;
import com.apicatalog.projection.mapping.PropertyMapping;
import com.apicatalog.projection.mapping.SourceMapping;
import com.apicatalog.projection.mapping.TargetMapping;
import com.apicatalog.projection.objects.ObjectBasicTypes;
import com.apicatalog.projection.projections.ProjectionBasicTypes;
import com.apicatalog.projection.projections.TestProjectionAF;

public class MapperTest {

	ProjectionFactory projections;
	ProjectionMapper mapper;
	
	@Before
	public void setup() {
		projections = new ProjectionFactory();
		mapper = new ProjectionMapper(projections);	
	}

    @Test
    public void testBasicTypes1() {
    	
    	final ProjectionMapping<ProjectionBasicTypes> projection = mapper.getMapping(ProjectionBasicTypes.class);
    	
    	Assert.assertNotNull(projection);
    	Assert.assertEquals(ProjectionBasicTypes.class, projection.getProjectionClass());
    	
    	Assert.assertNotNull(projection.getProperties());
    	Assert.assertEquals(7, projection.getProperties().size());
    	
    	Iterator<PropertyMapping> it = projection.getProperties().iterator();
    	
    	PropertyMapping pm = it.next();
		checkProperty(pm, "integerValue");
		checkSource(pm.getSource(), "integerValue", ObjectBasicTypes.class);
		checkTarget(pm.getTarget(), false, false, Integer.class, null);

    	pm = it.next();
		checkProperty(pm, "longValue");
		checkSource(pm.getSource(), "longValue", ObjectBasicTypes.class);		
		checkTarget(pm.getTarget(), false, false, Long.class, null);

    	pm = it.next();
		checkProperty(pm, "stringValue");
		checkSource(pm.getSource(), "stringValue", ObjectBasicTypes.class);
		checkTarget(pm.getTarget(), false, false, String.class, null);

    	pm = it.next();
		checkProperty(pm, "booleanValue");
		checkSource(pm.getSource(), "booleanValue", ObjectBasicTypes.class);
		checkTarget(pm.getTarget(), false, false, Boolean.class, null);

    	pm = it.next();
		checkProperty(pm, "instantValue");
		checkSource(pm.getSource(), "instantValue", ObjectBasicTypes.class);
		checkTarget(pm.getTarget(), false, false, Instant.class, null);

    	pm = it.next();
		checkProperty(pm, "floatValue");
		checkSource(pm.getSource(), "floatValue", ObjectBasicTypes.class);
		checkTarget(pm.getTarget(), false, false, Float.class, null);

    	pm = it.next();
		checkProperty(pm, "doubleValue");
		checkSource(pm.getSource(), "doubleValue", ObjectBasicTypes.class);
		checkTarget(pm.getTarget(), false, false, Double.class, null);
    }
	
    @Test
    public void testScanAF() {
    	
    	final ProjectionMapping<TestProjectionAF> projection = mapper.getMapping(TestProjectionAF.class);
    	
    	Assert.assertNotNull(projection);
    	Assert.assertEquals(TestProjectionAF.class, projection.getProjectionClass());

    	final Collection<PropertyMapping> properties = projection.getProperties();
    	
    	Assert.assertNotNull(properties);
    	Assert.assertEquals(3, properties.size());
    	
    	final Iterator<PropertyMapping> it = properties.iterator();
    	
    	final PropertyMapping pm1 = it.next();
		checkProperty(pm1, "originString");
		checkSource(pm1.getSource(), "stringValue", ObjectBasicTypes.class);
		checkTarget(pm1.getTarget(), false, false, String.class, null);

    	final PropertyMapping pm2 = it.next();
		checkProperty(pm2, "modifiedString");
		checkSource(pm2.getSource(), "stringValue", ObjectBasicTypes.class);
		checkTarget(pm2.getTarget(), false, false, String.class, null);
    	
    	final PropertyMapping pm3 = it.next();
		checkProperty(pm3, "modified2xString");
		checkSource(pm3.getSource(), "stringValue", ObjectBasicTypes.class);
		checkTarget(pm3.getTarget(), false, false, String.class, null);
    }
    
    public void checkProperty(PropertyMapping property, String name) {
    	Assert.assertNotNull(property);
    	Assert.assertEquals(name, property.getName());
    }

    public void checkSource(SourceMapping sources, String name, Class<?> sourceClass) {
    	Assert.assertNotNull(sources);
    	//TODO
    }

    public void checkTarget(TargetMapping target, boolean isCollection, boolean isReference, Class<?> targetClass, Class<?> itemClass) {
    	Assert.assertNotNull(target);
    	Assert.assertEquals(isCollection, target.isCollection());
    	Assert.assertEquals(isReference, target.isReference());
    	Assert.assertEquals(targetClass, target.getTargetClass());
    	Assert.assertEquals(itemClass, target.getItemClass());
    }
}
