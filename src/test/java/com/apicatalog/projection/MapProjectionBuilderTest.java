package com.apicatalog.projection;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.objects.SimpleObject;

public class MapProjectionBuilderTest {
	
	@Test
	public void test1c() throws ProjectionBuilderError, ProjectionError {
		final Projection<Map<String, Object>> projection = 
				Projection
					.hashMap()
						.map("s1", String.class).source(SimpleObject.class)
						.map("i1", Integer.class).source(SimpleObject.class)
					
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
					
					.map("i1", Integer.class).source(SimpleObject.class)
					.map("s1", String.class).source(SimpleObject.class)
					
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
						.map("s1", String.class).source(SimpleObject.class, "i1")
						.map("i1", Integer.class).source(SimpleObject.class, "s1")
					
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
					
					.map("i1", Integer.class).source(SimpleObject.class, "s1")
					.map("s1", String.class).source(SimpleObject.class, "i1")
					
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
}
