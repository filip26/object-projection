package com.apicatalog.projection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
				
		SimpleObject object1 = projection.extract(map, SimpleObject.class).orElse(null);
		
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
				
		SimpleObject object1 = projection.extract(map, SimpleObject.class).orElse(null);
		
		Assert.assertNotNull(object1);
		Assert.assertEquals(Integer.valueOf((String)map.get("s1")), object1.i1);
		Assert.assertEquals(map.get("i1").toString(), object1.s1);
	}
	
	@Test
	public void test3c() throws ProjectionBuilderError, ProjectionError {
		final Projection<Map<String, Object>> projection = 
				Projection
					.hashMap("p1")
						.mapLong("i1").constant("12345")
					
						.build(ProjectionRegistry.newInstance());
		
		Assert.assertNotNull(projection);

		Map<String, Object> map = projection.compose();
		
		Assert.assertNotNull(map);;
		Assert.assertEquals(12345l, map.get("i1"));
	}

	@Test
	public void test3e() throws ProjectionBuilderError, ProjectionError {
		final Projection<Map<String, Object>> projection = 
				Projection
					.hashMap()
						.mapLong("i1").constant("12345")
					
						.build(ProjectionRegistry.newInstance());
		
		Assert.assertNotNull(projection);

		Map<String, Object> to = new HashMap<>();
		to.put("i1", 443546356);

		Long i = projection.extract(to, Long.class).orElse(null);
		
		Assert.assertNull(i);

		Assert.assertTrue(projection.getExtractor().isEmpty());
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

		Boolean b1 = projection.extract(map, Boolean.class).orElse(null);
		Assert.assertNotNull(b1);
		Assert.assertFalse(b1);
		
		Boolean b2 = projection.extract(map, "b2", Boolean.class).orElse(null);
		Assert.assertNotNull(b2);;
		Assert.assertTrue(b2);
	}

	@Test
	public void test5c() throws ProjectionBuilderError, ProjectionError {
		final Projection<Map<String, Object>> projection = 
				Projection
					.hashMap()
						.mapCollection("s", Collection.class, String.class)
							.sources()
								.source(SimpleObject.class, "s1")
								.source(SimpleObject.class, "i1")
					
						.build(ProjectionRegistry.newInstance());
		
		Assert.assertNotNull(projection);

		SimpleObject o1 = new SimpleObject();
		o1.i1 = 123456;
		o1.s1 = "abcdef";
		
		Map<String, Object> map = projection.compose(o1);
		
		Assert.assertNotNull(map);
		Assert.assertNotNull(map.get("s"));
		Assert.assertNotNull(Collection.class.isInstance(map.get("s")));
		
		@SuppressWarnings("unchecked")
		Collection<String> col = (Collection<String>) map.get("s");
		
		Assert.assertEquals(2, col.size());
		
		Iterator<String> it = col.iterator();
		
		Assert.assertEquals(o1.s1, it.next());
		Assert.assertEquals(o1.i1.toString(), it.next());
	}

	@Test
	public void test5e() throws ProjectionBuilderError, ProjectionError {
		final Projection<Map<String, Object>> projection = 
				Projection
					.hashMap()
						.mapCollection("s", Collection.class, String.class)
							.sources()
								.source(SimpleObject.class, "s1")
								.source(SimpleObject.class, "i1")
					
						.build(ProjectionRegistry.newInstance());
		
		Assert.assertNotNull(projection);

		Map<String, Object> map = new HashMap<>();
		ArrayList<String> list = new ArrayList<>();
		list.add("abcdef");
		list.add("123456");
		map.put("s",list);
		
		SimpleObject o1 = projection.extract(map, SimpleObject.class).orElse(null);
		
		Assert.assertNotNull(o1);
		
		Assert.assertEquals(Integer.valueOf(123456), o1.i1);
		Assert.assertEquals("abcdef", o1.s1);
	}
}
