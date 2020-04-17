package com.apicatalog.projection;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.objects.SimpleObject;

public class MapProjectionBuilderTest {
	
	@Test
	public void test1() throws ProjectionBuilderError, ProjectionError {
		final Projection<Map<String, Object>> projection = 
				Projection
					.hashMap()
						.map("s1", String.class)
								.source(SimpleObject.class)
						.map("i1", Integer.class)
								.source(SimpleObject.class)
					
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
}
