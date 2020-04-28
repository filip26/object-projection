package com.apicatalog.projection;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.objects.BasicTypes;
import com.apicatalog.projection.objects.SimpleObject;
import com.apicatalog.projection.source.SourceObject;

public class MapProjectionBuilderTest {
	
	@Test
	public void test1c() throws ProjectionError, CompositionError {
		final Projection<Map<String, Object>> projection = 
				Projection
					.hashMap()
						.mapString("s1").source(SimpleObject.class)
						.mapInteger("i1").source(SimpleObject.class)
					
						.build(Registry.newInstance());
		
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
	public void test1e() throws ProjectionError, ExtractionError {
		
		final Projection<Map<String, Object>> projection = 
				Projection
					.hashMap()					
						.mapInteger("i1").source(SimpleObject.class)
						.mapString("s1").source(SimpleObject.class)
						
						.build(Registry.newInstance());
		
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
	public void test2c() throws ProjectionError, CompositionError {
		final Projection<Map<String, Object>> projection = 
				Projection
					.hashMap()
						.mapString("s1").source(SimpleObject.class, "i1")
						.mapInteger("i1").source(SimpleObject.class, "s1")
					
						.build(Registry.newInstance());
		
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
	public void test2e() throws ProjectionError, ExtractionError {
		
		final Projection<Map<String, Object>> projection = 
				Projection
					.hashMap()					
						.mapInteger("i1").source(SimpleObject.class, "s1")
						.mapString("s1").source(SimpleObject.class, "i1")
						
						.build(Registry.newInstance());
		
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
	public void test3c() throws ProjectionError, CompositionError {
		final Projection<Map<String, Object>> projection = 
				Projection
					.hashMap("p1")
						.mapLong("i1").constant("12345")
					
						.build(Registry.newInstance());
		
		Assert.assertNotNull(projection);

		Map<String, Object> map = projection.compose();
		
		Assert.assertNotNull(map);;
		Assert.assertEquals(12345l, map.get("i1"));
	}

	@Test
	public void test3e() throws ProjectionError, ExtractionError {
		final Projection<Map<String, Object>> projection = 
				Projection
					.hashMap()
						.mapLong("i1").constant("12345")
					
						.build(Registry.newInstance());
		
		Assert.assertNotNull(projection);

		Map<String, Object> to = new HashMap<>();
		to.put("i1", 443546356);

		Long i = projection.extract(to, Long.class).orElse(null);
		
		Assert.assertNull(i);

		Assert.assertTrue(projection.getExtractor().isEmpty());
	}

	@Test
	public void test4c() throws ProjectionError, CompositionError {
		final Projection<Map<String, Object>> projection = 
				Projection
					.hashMap()
						.mapFloat("f").provided()
						.mapBoolean("b1").provided()
						.mapBoolean("b2").provided("b2")
					
						.build(Registry.newInstance());
		
		Assert.assertNotNull(projection);

		Map<String, Object> map = projection.compose(12.34f, true, SourceObject.of("b2", Boolean.FALSE));
		
		Assert.assertNotNull(map);;
		Assert.assertEquals(12.34f, map.get("f"));
		Assert.assertTrue((boolean) map.get("b1"));
		Assert.assertFalse((boolean) map.get("b2"));
	}

	@Test
	public void test4e() throws ProjectionError, ExtractionError {
		final Projection<Map<String, Object>> projection = 
				Projection
					.hashMap()
						.mapFloat("f").provided()
						.mapBoolean("b1").provided()
						.mapBoolean("b2").provided("b2")
					
						.build(Registry.newInstance());
		
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
	public void test5c() throws ProjectionError, CompositionError {
		final Projection<Map<String, Object>> projection = 
				Projection
					.hashMap()
						.mapCollection("s", Collection.class, String.class)
							.sources()
								.source(SimpleObject.class, "s1")
								.source(SimpleObject.class, "i1")
									.conversion(Integer.class, Integer.class)
										.forward(i -> i + 100)

						.build(Registry.newInstance());
		
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
		Assert.assertEquals(Integer.valueOf(o1.i1 + 100).toString(), it.next());
	}

	@Test
	public void test5e() throws ProjectionError, ExtractionError {
		final Projection<Map<String, Object>> projection = 
				Projection
					.hashMap()
						.mapCollection("s", Collection.class, String.class)
							.sources()
								.source(SimpleObject.class, "s1")
								.source(SimpleObject.class, "i1")
									.conversion(Integer.class, Integer.class)
										.backward(i -> i - 100)
					
						.build(Registry.newInstance());
		
		Assert.assertNotNull(projection);

		Map<String, Object> map = new HashMap<>();
		ArrayList<String> list = new ArrayList<>();
		list.add("abcdef");
		list.add("123456");
		map.put("s",list);
		
		SimpleObject o1 = projection.extract(map, SimpleObject.class).orElse(null);
		
		Assert.assertNotNull(o1);
		
		Assert.assertEquals(Integer.valueOf(123456 - 100), o1.i1);
		Assert.assertEquals("abcdef", o1.s1);
	}

	@Test
	public void test6c() throws ProjectionError, CompositionError {
		
		Registry registry = Registry.newInstance();
		
		final Projection<Map<String, Object>> projection1 = 
				Projection
					.hashMap("p1")
						.mapLong("i1").source(BasicTypes.class, "integerValue")
						.mapDouble("d1").source(BasicTypes.class, "doubleValue")
						.build(registry);

		Assert.assertNotNull(projection1);
		
		final Projection<Map<String, Object>> projection2 = 
				Projection
					.hashMap()
						.mapReference("p", "p1").provided()
						.build(registry);
		
		Assert.assertNotNull(projection2);

		BasicTypes object = new BasicTypes();
		object.integerValue = 123;
		object.doubleValue = 0.98d;
		
		Map<String, Object> map1 = projection2.compose(object);
		
		Assert.assertNotNull(map1);;
		Assert.assertNotNull(map1.get("p"));
		Assert.assertTrue(Map.class.isInstance(map1.get("p")));
		
		@SuppressWarnings("unchecked")
		Map<String, Object> map2 = (Map<String, Object>) map1.get("p"); 
		
		Assert.assertEquals(Long.valueOf(object.integerValue), map2.get("i1"));
		Assert.assertEquals(object.doubleValue, map2.get("d1"));
	}

	@Test
	public void test6e() throws ProjectionError, ExtractionError {
		
		Registry registry = Registry.newInstance();
		
		final Projection<Map<String, Object>> projection1 = 
				Projection
					.hashMap("p1")
						.mapLong("i1").source(BasicTypes.class, "integerValue")
						.mapDouble("d1").source(BasicTypes.class, "doubleValue")
						.build(registry);

		Assert.assertNotNull(projection1);
		
		final Projection<Map<String, Object>> projection2 = 
				Projection
					.hashMap()
						.mapReference("p", "p1").provided()
						.build(registry);
		
		Assert.assertNotNull(projection2);

		Map<String, Object> map1 = new HashMap<>();
		
		Map<String, Object> map2 = new HashMap<>();
		map2.put("i1", 352452l);
		map2.put("d1", 2.323d);
		
		map1.put("p", map2);
				
		BasicTypes object1 = projection2.extract(map1, BasicTypes.class).orElse(null);
		
		Assert.assertNotNull(object1);
		Assert.assertEquals(Integer.valueOf(((Long)map2.get("i1")).intValue()), object1.integerValue);
		Assert.assertEquals(map2.get("d1"), object1.doubleValue);
	}

	@Test
	public void test7c() throws ProjectionError, CompositionError {
		
		final Projection<Map<String, Object>> projection = 
				Projection
					.hashMap()
						.mapString("s1").source(BasicTypes.class, "floatValue")
						.mapDate("d1").source(BasicTypes.class, "instantValue")
					
						.build(Registry.newInstance());
		
		Assert.assertNotNull(projection);

		BasicTypes object1 = new BasicTypes();
		object1.floatValue = 0.32f;
		object1.instantValue = Instant.now();
		
		Map<String, Object> map = projection.compose(object1);
		
		Assert.assertNotNull(map);;
		Assert.assertEquals(object1.floatValue.toString(), map.get("s1"));
		Assert.assertEquals(Date.from(object1.instantValue), map.get("d1"));
	}
	

	@Test
	public void test7e() throws ProjectionError, ExtractionError {
		
		final Projection<Map<String, Object>> projection = 
				Projection
					.hashMap()
						.mapString("s1").source(BasicTypes.class, "floatValue")
						.mapDate("d1").source(BasicTypes.class, "instantValue")
					
						.build(Registry.newInstance());
		
		Assert.assertNotNull(projection);

		Map<String, Object> map = new HashMap<>();
		map.put("s1", "1.23");
		map.put("d1", new Date());
				
		BasicTypes object1 = projection.extract(map, BasicTypes.class).orElse(null);
		
		Assert.assertNotNull(object1);
		Assert.assertEquals(Float.valueOf((String)map.get("s1")), object1.floatValue);
		Assert.assertEquals(((Date)map.get("d1")).toInstant(), object1.instantValue);
	}
	
	@Test
	public void test8c() throws ProjectionError, CompositionError {
		
		Registry registry = Registry.newInstance();
		
		final Projection<Map<String, Object>> projection1 = 
				Projection
					.hashMap("p1")
						.mapInstant("instantValue").source(BasicTypes.class)
						.build(registry);

		Assert.assertNotNull(projection1);
		
		final Projection<Map<String, Object>> projection2 = 
				Projection
					.hashMap()
						.mapReference("c1", ArrayList.class, "p1").provided("items")
						.build(registry);
		
		Assert.assertNotNull(projection2);

		BasicTypes object = new BasicTypes();
		object.instantValue = Instant.now();
		
		Map<String, Object> map1 = projection2.compose(SourceObject.of("items", Arrays.asList(new BasicTypes[] { object })));
		
		Assert.assertNotNull(map1);;
		Assert.assertNotNull(map1.get("c1"));
		Assert.assertTrue(Collection.class.isInstance(map1.get("c1")));
		
		@SuppressWarnings("unchecked")
		Collection<Map<String, Object>> items = (Collection<Map<String, Object>>) map1.get("c1"); 
		
		Assert.assertEquals(1, items.size());

		Map<String, Object> imap = items.iterator().next();
		
		Assert.assertEquals(object.instantValue, imap.get("instantValue"));
	}
	
	@Test
	public void test8e() throws ProjectionError, ExtractionError {
		
		Registry registry = Registry.newInstance();
		
		final Projection<Map<String, Object>> projection1 = 
				Projection
					.hashMap("p1")
						.mapDate("dateValue").source(BasicTypes.class)
						.build(registry);

		Assert.assertNotNull(projection1);
		
		final Projection<Map<String, Object>> projection2 = 
				Projection
					.hashMap()
						.mapReference("c1", ArrayList.class, "p1").provided("items")
						.build(registry);
		
		Assert.assertNotNull(projection2);

		Map<String, Object> item1 = new HashMap<>();
		item1.put("dateValue", new Date());

		Map<String, Object> item2 = new HashMap<>();
		item2.put("dateValue", new Date());

		ArrayList<Map<String, Object>> items = new ArrayList<>();
		items.add(item1);
		items.add(item2);
		
		Map<String, Object> to = new HashMap<>();
		to.put("c1", items);
		
		Collection<BasicTypes> objects = projection2.extractCollection(to, "items", BasicTypes.class).orElse(null);

		Assert.assertNotNull(objects);
		Assert.assertEquals(2, objects.size());

		BasicTypes[] array = objects.toArray(BasicTypes[]::new);
		
		Assert.assertEquals(item1.get("dateValue"), array[0].dateValue);
		Assert.assertEquals(item2.get("dateValue"), array[1].dateValue);
	}
}
