package com.apicatalog.projection.mapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.objects.SimpleObject;

public class ProjectionMapperTest {

	ProjectionRegistry projections;
	ProjectionMapper mapper;
	
	@Before
	public void setup() {
		projections = ProjectionRegistry.newInstance();
		mapper = projections.getMapper();
	}

	@Test
	public void testUnmapped() {
		Projection<SimpleObject> projection = mapper.getProjection(SimpleObject.class);
		Assert.assertNull(projection);
	}
	
//    @Test
//    public void testBasicTypes1() {
//
//    	final ProjectionImpl<TypeObjectTo> projection = (ProjectionImpl<TypeObjectTo>) mapper.getProjection(TypeObjectTo.class);
//    	
//    	Assert.assertNotNull(projection);
//    	Assert.assertEquals(TypeObjectTo.class, projection.getProjectionClass());
//    	
//    	Assert.assertNotNull(projection.getProperties());
//    	Assert.assertEquals(7, projection.getProperties().length);
//    	
//    	
//    	SourceProperty pm = (SourceProperty) projection.getProperties()[0];
//		checkProperty(pm, "integerValue");
//		checkSource(pm.getSource(), "integerValue", BasicTypes.class);
//		checkTarget(pm.getTargetAdapter(), false, false, Integer.class, null);
//
//    	pm = it.next();
//		checkProperty(pm, "longValue");
//		checkSource(pm.getSource(), "longValue", BasicTypes.class);		
//		checkTarget(pm.getTarget(), false, false, Long.class, null);
//
//    	pm = it.next();
//		checkProperty(pm, "stringValue");
//		checkSource(pm.getSource(), "stringValue", BasicTypes.class);
//		checkTarget(pm.getTarget(), false, false, String.class, null);
//
//    	pm = it.next();
//		checkProperty(pm, "booleanValue");
//		checkSource(pm.getSource(), "booleanValue", BasicTypes.class);
//		checkTarget(pm.getTarget(), false, false, Boolean.class, null);
//
//    	pm = it.next();
//		checkProperty(pm, "instantValue");
//		checkSource(pm.getSource(), "instantValue", BasicTypes.class);
//		checkTarget(pm.getTarget(), false, false, Instant.class, null);
//
//    	pm = it.next();
//		checkProperty(pm, "floatValue");
//		checkSource(pm.getSource(), "floatValue", BasicTypes.class);
//		checkTarget(pm.getTarget(), false, false, Float.class, null);
//
//    	pm = it.next();
//		checkProperty(pm, "doubleValue");
//		checkSource(pm.getSource(), "doubleValue", BasicTypes.class);
//		checkTarget(pm.getTarget(), false, false, Double.class, null);
//    }
//	
//    @Test
//    public void testScanAF() {
//    	
//    	final ObjectProjection<TestProjectionAF> projection = mapper.getMapping(TestProjectionAF.class);
//    	
//    	Assert.assertNotNull(projection);
//    	Assert.assertEquals(TestProjectionAF.class, projection.getProjectionClass());
//
//    	final Collection<PropertyMapping> properties = projection.getProperties();
//    	
//    	Assert.assertNotNull(properties);
//    	Assert.assertEquals(3, properties.size());
//    	
//    	final Iterator<PropertyMapping> it = properties.iterator();
//    	
//    	final PropertyMapping pm1 = it.next();
//		checkProperty(pm1, "originString");
//		checkSource(pm1.getSource(), "stringValue", BasicTypes.class);
//		checkTarget(pm1.getTarget(), false, false, String.class, null);
//
//    	final PropertyMapping pm2 = it.next();
//		checkProperty(pm2, "modifiedString");
//		checkSource(pm2.getSource(), "stringValue", BasicTypes.class);
//		checkTarget(pm2.getTarget(), false, false, String.class, null);
//    	
//    	final PropertyMapping pm3 = it.next();
//		checkProperty(pm3, "modified2xString");
//		checkSource(pm3.getSource(), "stringValue", BasicTypes.class);
//		checkTarget(pm3.getTarget(), false, false, String.class, null);
//    }
//    
//    public void checkProperty(ProjectionProperty property, String name) {
//    	Assert.assertNotNull(property);
//    	Assert.assertEquals(name, property.getName());
//    }
//
//    public void checkSource(Source sources, String name, Class<?> sourceClass) {
//    	Assert.assertNotNull(sources);
//    	//TODO
//    }
//
//    public void checkTarget(TargetMapping target, boolean isCollection, boolean isReference, Class<?> targetClass, Class<?> itemClass) {
//    	Assert.assertNotNull(target);
//    	Assert.assertEquals(isCollection, ((TargetMappingImpl)target).isCollection());
//    	Assert.assertEquals(isReference, ((TargetMappingImpl)target).isReference());
//    	Assert.assertEquals(targetClass, target.getTargetClass());
//    	Assert.assertEquals(itemClass, target.getTargetComponentClass());
//    }
}
