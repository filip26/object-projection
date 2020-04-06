package com.apicatalog.projection;

import java.net.URI;

import org.junit.Assert;
import org.junit.Test;

import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.builder.api.ProjectionBuilder;
import com.apicatalog.projection.converter.std.Prefix;
import com.apicatalog.projection.converter.std.Suffix;
import com.apicatalog.projection.converter.std.UriTemplate;
import com.apicatalog.projection.objects.Object1;
import com.apicatalog.projection.objects.Object2;
import com.apicatalog.projection.objects.SimpleObject;
import com.apicatalog.projection.objects.UriObject;
import com.apicatalog.projection.projections.Object1To;
import com.apicatalog.projection.projections.Object2To;
import com.apicatalog.projection.projections.SimpleObjectTo;
import com.apicatalog.projection.projections.UriTo;
import com.apicatalog.projection.source.SourceObject;

public class ProjectionBuilderTest {


	@Test
	public void test1() throws ProjectionError {
		final Projection<SimpleObjectTo> projection = 
				ProjectionBuilder
					.bind(SimpleObjectTo.class)
					
					.map("i1").source(SimpleObject.class)
					.map("s1").source(SimpleObject.class)
					
					.build(ProjectionRegistry.newInstance(), new TypeAdapters());
		
		Assert.assertNotNull(projection);

		SimpleObject object1 = new SimpleObject();
		object1.i1 = 443546356;
		object1.s1 = "string-1";
		
		SimpleObjectTo to = projection.compose(object1);
		
		Assert.assertNotNull(to);;
		Assert.assertEquals(object1.i1, to.i1);
		Assert.assertEquals(object1.s1, to.s1);
	}

	@Test
	public void test2() throws ProjectionError {
		final Projection<SimpleObjectTo> projection = 
				ProjectionBuilder
					.bind(SimpleObjectTo.class)
					
					.map("i1").source(SimpleObject.class, "s1")
					.map("s1").source(SimpleObject.class, "i1")
					
					.build(ProjectionRegistry.newInstance(), new TypeAdapters());
		
		Assert.assertNotNull(projection);

		SimpleObject object1 = new SimpleObject();
		object1.i1 = 443546356;
		object1.s1 = "57566735";
		
		SimpleObjectTo to = projection.compose(object1);
		
		Assert.assertNotNull(to);;
		Assert.assertEquals(Integer.valueOf(object1.s1), to.i1);
		Assert.assertEquals(String.valueOf(object1.i1), to.s1);
	}

	@Test
	public void test3() throws ProjectionError {
		final Projection<SimpleObjectTo> projection = 
				ProjectionBuilder
					.bind(SimpleObjectTo.class)
					
					.map("s1").source(SimpleObject.class)
								.conversion(Prefix.class, "StringToPrepend")
								.conversion(Suffix.class, "StringToAppend")
								.optional()
								
					.map("i1").source(SimpleObject.class)
					
					.build(ProjectionRegistry.newInstance(), new TypeAdapters());

		Assert.assertNotNull(projection);

		SimpleObject object1 = new SimpleObject();
		object1.i1 = 443546356;
		object1.s1 = "57566735";
		
		SimpleObjectTo to = projection.compose(object1);
		
		Assert.assertNotNull(to);;
		Assert.assertEquals(object1.i1, to.i1);
		Assert.assertEquals("StringToPrepend" + object1.s1 + "StringToAppend", to.s1);
	}

	@Test
	public void test4() throws ProjectionError {
		final Projection<SimpleObjectTo> projection = 
				ProjectionBuilder
					.bind(SimpleObjectTo.class)
					
					.map("s1").provided()
								.optional()
								.qualifier("string1")
								
					.map("i1").source(SimpleObject.class)
					
					.build(ProjectionRegistry.newInstance(), new TypeAdapters());

		Assert.assertNotNull(projection);

		SimpleObject object1 = new SimpleObject();
		object1.i1 = 443546356;
		
		SimpleObjectTo to = projection.compose(object1, SourceObject.of("string1", "provided-string"));
		
		Assert.assertNotNull(to);;
		Assert.assertEquals(object1.i1, to.i1);
		Assert.assertEquals("provided-string", to.s1);
	}

	@Test
	public void test51() throws ProjectionError {
		
		ProjectionRegistry registry = ProjectionRegistry.newInstance();
		
		Assert.assertNotNull( 
				ProjectionBuilder
					.bind(Object1To.class)
					
					.map("object2", true).source(Object1.class).optional()

					.map("id").source(Object1.class)
					
					.build(registry, new TypeAdapters())
					);

		Assert.assertNotNull(
				ProjectionBuilder
					.bind(Object2To.class)
					
					.map("id").source(Object2.class)
					
					.build(registry, new TypeAdapters())
					);
		
		Object1 object1 = new Object1();
		object1.id = "AREW2324E";
		
		Object2 object2 = new Object2();
		object2.id = "3GFD42E";
		
		object1.object2 = object2;
		
		Object1To to = registry.compose(Object1To.class, object1);
		
		Assert.assertNotNull(to);;
		Assert.assertEquals(object1.id, to.id);
		
		Assert.assertNotNull(to.object2);
		Assert.assertEquals(object2.id, to.object2.id);		
	}

	@Test
	public void test52() throws ProjectionError {
		
		ProjectionRegistry registry = ProjectionRegistry.newInstance();

		Assert.assertNotNull( 
				ProjectionBuilder
					.bind(Object1To.class)
					
					.map("object2", true).source(Object1.class).optional()

					.map("id").source(Object1.class)
					
					.build(registry, new TypeAdapters())
					);

		Assert.assertNotNull(
				ProjectionBuilder
					.bind(Object2To.class)
					
					.map("id").source(Object2.class)
					
					.build(registry, new TypeAdapters())
					);

		Object1 object1 = new Object1();
		object1.id = "AREW2324E";	
		object1.object2 = null;
		
		Object1To to = registry.compose(Object1To.class, object1);
		
		Assert.assertNotNull(to);;
		Assert.assertEquals(object1.id, to.id);
		
		Assert.assertNull(to.object2);
	}
	@Test
	public void test6() throws ProjectionError {
		final Projection<Object1To> projection = 
				ProjectionBuilder
					.bind(Object1To.class)
					
					.map("id").constant("StringContant")
					
					.build(ProjectionRegistry.newInstance(), new TypeAdapters());
		
		Assert.assertNotNull(projection);
		
		Object1To to = projection.compose();
		Assert.assertNotNull(to);;
		Assert.assertEquals("StringContant", to.id);
	}

	@Test
	public void test7() throws ProjectionError {
		final Projection<Object1To> projection = 
				ProjectionBuilder
					.bind(Object1To.class)
					
					.map("id")
						.sources()
							.reduce(UriTemplate.class, "/{}/{}")							
							.conversion(Prefix.class, "https://example.org")

							.source(Object1.class)
							
							.source(Object1.class, "id")
								.conversion(Prefix.class, "1")
								.conversion(Suffix.class, "2")
						
					.build(ProjectionRegistry.newInstance(), new TypeAdapters());
		
		Assert.assertNotNull(projection);
		
		Object1 object1 = new Object1();
		object1.id = "A";
		
		Object1To to = projection.compose(object1);
				
		Assert.assertNotNull(to);
		
		Assert.assertEquals("https://example.org/A/1A2", to.id);
	}

	@Test
	public void test8() throws ProjectionError {
		final Projection<SimpleObjectTo> projection = 
				ProjectionBuilder
					.bind(SimpleObjectTo.class)					
					.build(ProjectionRegistry.newInstance(), new TypeAdapters());
		
		Assert.assertNull(projection);
	}

	@Test
	public void test9() throws ProjectionError {
		
		ProjectionRegistry registry = ProjectionRegistry.newInstance();
		
		Assert.assertNotNull( 
				ProjectionBuilder
					.bind(Object1To.class)
					
					.map("object2", true).source(Object1.class).optional()

					.map("id").provided()
					
					.build(registry, new TypeAdapters())
					);

		Assert.assertNotNull(
				ProjectionBuilder
					.bind(Object2To.class)
					
					.map("id").provided()
					
					.build(registry, new TypeAdapters())
					);
		
		Object1 object1 = new Object1();
		
		Object2 object2 = new Object2();
		
		object1.object2 = object2;
		
		Object1To to = registry.compose(Object1To.class, object1, "AREW2324E");
		
		Assert.assertNotNull(to);;
		Assert.assertEquals("AREW2324E", to.id);
		
		Assert.assertNotNull(to.object2);
		Assert.assertEquals("AREW2324E", to.object2.id);		
	}

	@Test
	public void test10() throws ProjectionError {
		
		ProjectionRegistry registry = ProjectionRegistry.newInstance();
		
		Assert.assertNotNull( 
				ProjectionBuilder
					.bind(Object1To.class)
					
					.map("object2", true).source(Object1.class).optional()

					.map("id").provided("id")
					
					.build(registry, new TypeAdapters())
					);

		Assert.assertNotNull(
				ProjectionBuilder
					.bind(Object2To.class)
					
					.map("id").provided("id")
					
					.build(registry, new TypeAdapters())
					);
		
		Object1 object1 = new Object1();
		
		Object2 object2 = new Object2();
		
		object1.object2 = object2;
		
		Object1To to = registry.compose(Object1To.class, object1,  SourceObject.of("id", "AREW2324E"));
		
		Assert.assertNotNull(to);;
		Assert.assertEquals("AREW2324E", to.id);
		
		Assert.assertNotNull(to.object2);
		Assert.assertEquals("AREW2324E", to.object2.id);		
	}
	
	@Test
	public void test11() throws ProjectionError {
		
		ProjectionRegistry registry = ProjectionRegistry.newInstance();
		
		Assert.assertNotNull( 
				ProjectionBuilder
					.bind(Object1To.class)
					
					.map("object2", true).provided("obj2")

					.map("id").provided("id")
					
					.build(registry, new TypeAdapters())
					);

		Assert.assertNotNull(
				ProjectionBuilder
					.bind(Object2To.class)
					
					.map("id").provided("id")
					
					.build(registry, new TypeAdapters())
					);
		
		Object1 object1 = new Object1();
		
		Object2 object2 = new Object2();
				
		Object1To to = registry.compose(
							Object1To.class, 
							object1,
							SourceObject.of("obj2", object2),
							SourceObject.of("id", "AREW2324E"),
							SourceObject.of("obj2.id", "3GFD42EE7")
							);
		
		Assert.assertNotNull(to);;
		Assert.assertEquals("AREW2324E", to.id);
		
		Assert.assertNotNull(to.object2);
		Assert.assertEquals("3GFD42EE7", to.object2.id);		
	}
	
	@Test
	public void test12() throws ProjectionError {
		
		ProjectionRegistry registry = ProjectionRegistry.newInstance();
		
		Assert.assertNotNull( 
				ProjectionBuilder
					.bind(UriTo.class)
					
					.map("uri")
						.source(UriObject.class)
						.conversion(Suffix.class, "/d/e/f")
					
					.build(registry, new TypeAdapters())
					);

		UriObject object1 = new UriObject();
		object1.uri = URI.create("https://example.org/a/b/c");
		
		UriTo to = registry.compose(
							UriTo.class, 
							object1
							);
		
		
		Assert.assertNotNull(to);;
		Assert.assertEquals(object1.uri.toString() + "/d/e/f", to.uri);		
	}
}
