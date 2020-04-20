package com.apicatalog.projection;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.objects.SimpleObject;
import com.apicatalog.projection.source.SourceObject;

public class MapProjectionBuilderTest {
	
	@Test
	public void test1c() throws ProjectionBuilderError, ProjectionError {
		final Projection<Map<String, Object>> projection = 
				Projection
					.hashMap()
						.mapString("s1").source(SimpleObject.class)
						.mapInteger("i1").source(SimpleObject.class)
					
						.build(ProjectionRegistry.newInstance());
		
		Assert.assertNotNull(projection);

		SimpleObject object1 = new SimpleObject();
		object1.i1 = 443546356;
		object1.s1 = "string-1";
		
		Map<String, Object> map = projection.compose(object1);
		
		Assert.assertNotNull(map);;
		Assert.assertEquals(object1.s1, map.get("s1"));
		Assert.assertEquals(object1.i1, map.get("i1"));
	}
	

	@Test
	public void test1e() throws ProjectionBuilderError, ProjectionError {
		
		final Projection<Map<String, Object>> projection = 
				Projection
					.hashMap()					
						.mapInteger("i1").source(SimpleObject.class)
						.mapString("s1").source(SimpleObject.class)
						
						.build(ProjectionRegistry.newInstance());
		
		Assert.assertNotNull(projection);

		Map<String, Object> map = new HashMap<>();
		map.put("i1", 443546356);
		map.put("s1", "string-1");
				
		SimpleObject object1 = projection.extract(map, SimpleObject.class);
		
		Assert.assertNotNull(object1);
		Assert.assertEquals(map.get("i1"), object1.i1);
		Assert.assertEquals(map.get("s1"), object1.s1);
	}
	
	@Test
	public void test2c() throws ProjectionBuilderError, ProjectionError {
		final Projection<Map<String, Object>> projection = 
				Projection
					.hashMap()
						.mapString("s1").source(SimpleObject.class, "i1")
						.mapInteger("i1").source(SimpleObject.class, "s1")
					
						.build(ProjectionRegistry.newInstance());
		
		Assert.assertNotNull(projection);

		SimpleObject object1 = new SimpleObject();
		object1.i1 = 443546356;
		object1.s1 = "9516284";
		
		Map<String, Object> map = projection.compose(object1);
		
		Assert.assertNotNull(map);;
		Assert.assertEquals(Integer.valueOf(object1.s1), map.get("i1"));
		Assert.assertEquals(object1.i1.toString(), map.get("s1"));
	}
	
	@Test
	public void test2e() throws ProjectionBuilderError, ProjectionError {
		
		final Projection<Map<String, Object>> projection = 
				Projection
					.hashMap()					
						.mapInteger("i1").source(SimpleObject.class, "s1")
						.mapString("s1").source(SimpleObject.class, "i1")
						
						.build(ProjectionRegistry.newInstance());
		
		Assert.assertNotNull(projection);

		Map<String, Object> map = new HashMap<>();
		map.put("i1", 443546356);
		map.put("s1", "3674787");
				
		SimpleObject object1 = projection.extract(map, SimpleObject.class);
		
		Assert.assertNotNull(object1);
		Assert.assertEquals(Integer.valueOf((String)map.get("s1")), object1.i1);
		Assert.assertEquals(map.get("i1").toString(), object1.s1);
	}
	
	@Test
	public void test3c() throws ProjectionBuilderError, ProjectionError {
		final Projection<Map<String, Object>> projection = 
				Projection
					.hashMap("p1")
						.mapInteger("i1").constant("12345")
					
						.build(ProjectionRegistry.newInstance());
		
		Assert.assertNotNull(projection);

		Map<String, Object> map = projection.compose();
		
		Assert.assertNotNull(map);;
		Assert.assertEquals(12345, map.get("i1"));
	}

	@Test
	public void test3e() throws ProjectionBuilderError, ProjectionError {
		final Projection<Map<String, Object>> projection = 
				Projection
					.hashMap()
						.mapInteger("i1").constant("12345")
					
						.build(ProjectionRegistry.newInstance());
		
		Assert.assertNotNull(projection);

		Map<String, Object> to = new HashMap<>();
		to.put("i1", 443546356);

		Integer i = projection.extract(to, Integer.class);
		
		Assert.assertNull(i);

		Assert.assertNull(projection.getExtractor());
	}

	@Test
	public void test4c() throws ProjectionBuilderError, ProjectionError {
		final Projection<Map<String, Object>> projection = 
				Projection
					.hashMap()
						.mapFloat("f").provided()
						.mapBoolean("b1").provided()
						.mapBoolean("b2").provided("b2")
					
						.build(ProjectionRegistry.newInstance());
		
		Assert.assertNotNull(projection);

		Map<String, Object> map = projection.compose(12.34f, true, SourceObject.of("b2", Boolean.FALSE));
		
		Assert.assertNotNull(map);;
		Assert.assertEquals(12.34f, map.get("f"));
		Assert.assertTrue((boolean) map.get("b1"));
		Assert.assertFalse((boolean) map.get("b2"));
	}

	@Test
	public void test4e() throws ProjectionBuilderError, ProjectionError {
		final Projection<Map<String, Object>> projection = 
				Projection
					.hashMap()
						.mapFloat("f").provided()
						.mapBoolean("b1").provided()
						.mapBoolean("b2").provided("b2")
					
						.build(ProjectionRegistry.newInstance());
		
		Assert.assertNotNull(projection);
		
		Map<String, Object> map = new HashMap<>();
		map.put("b2", Boolean.TRUE);
		map.put("b1", Boolean.FALSE);

		Boolean b1 = projection.extract(map, Boolean.class);
		Assert.assertNotNull(b1);
		Assert.assertFalse(b1);
		
		Boolean b2 = projection.extract(map, "b2", Boolean.class);
		Assert.assertNotNull(b2);;
		Assert.assertTrue(b2);
	}

}
