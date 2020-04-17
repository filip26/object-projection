package com.apicatalog.projection;

import java.net.URI;

import org.junit.Assert;
import org.junit.Test;

import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.api.object.impl.ProjectionApiImpl;
import com.apicatalog.projection.converters.Prefix;
import com.apicatalog.projection.converters.Suffix;
import com.apicatalog.projection.converters.UriTemplate;
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
	public void test1c() throws ProjectionBuilderError, ProjectionError {
		final Projection<SimpleObjectTo> projection = 
				Projection
					.bind(SimpleObjectTo.class)
					
					.map("i1").source(SimpleObject.class)
					.map("s1").source(SimpleObject.class)
					
					.build(ProjectionRegistry.newInstance());
		
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
	public void test1e() throws ProjectionBuilderError, ProjectionError {
		final Projection<SimpleObjectTo> projection = 
				Projection
					.bind(SimpleObjectTo.class)
					
					.map("i1").source(SimpleObject.class)
					.map("s1").source(SimpleObject.class)
					
					.build(ProjectionRegistry.newInstance());
		
		Assert.assertNotNull(projection);

		SimpleObjectTo to = new SimpleObjectTo();
		to.i1 = 443546356;
		to.s1 = "string-1";
				
		SimpleObject object1 = projection.extract(to, SimpleObject.class);
		
		Assert.assertNotNull(object1);
		Assert.assertEquals(to.i1, object1.i1);
		Assert.assertEquals(to.s1, object1.s1);
	}
	@Test
	public void test2c() throws ProjectionBuilderError, ProjectionError {
		final Projection<SimpleObjectTo> projection = 
				Projection
					.bind(SimpleObjectTo.class)
					
					.map("i1").source(SimpleObject.class, "s1")
					.map("s1").source(SimpleObject.class, "i1")
					
					.build(ProjectionRegistry.newInstance());
		
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
	public void test2e() throws ProjectionBuilderError, ProjectionError {
		final Projection<SimpleObjectTo> projection = 
				Projection
					.bind(SimpleObjectTo.class)
					
					.map("i1").source(SimpleObject.class, "s1")
					.map("s1").source(SimpleObject.class, "i1")
					
					.build(ProjectionRegistry.newInstance());
		
		Assert.assertNotNull(projection);

		SimpleObjectTo to = new SimpleObjectTo();
		to.i1 = 443546356;
		to.s1 = "57566735";
		
		SimpleObject object1 = projection.extract(to, SimpleObject.class);
		
		Assert.assertNotNull(object1);
		Assert.assertEquals(Integer.valueOf(to.s1), object1.i1);
		Assert.assertEquals(String.valueOf(to.i1), object1.s1);
	}

	@Test
	public void test3() throws ProjectionBuilderError, ProjectionError {
		final Projection<SimpleObjectTo> projection = 
				Projection
					.bind(SimpleObjectTo.class)
					
					.map("s1").source(SimpleObject.class)
								.conversion(Prefix.class, "StringToPrepend")
								.conversion(Suffix.class, "StringToAppend")
								.optional()
								
					.map("i1").source(SimpleObject.class)
					
					.build(ProjectionRegistry.newInstance());

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
	public void test4() throws ProjectionBuilderError, ProjectionError {
		final Projection<SimpleObjectTo> projection = 
				Projection
					.bind(SimpleObjectTo.class)
					
					.map("s1").provided("string1").optional()
								
					.map("i1").source(SimpleObject.class)
					
					.build(ProjectionRegistry.newInstance());

		Assert.assertNotNull(projection);

		SimpleObject object1 = new SimpleObject();
		object1.i1 = 443546356;
		
		SimpleObjectTo to = projection.compose(object1, SourceObject.of("string1", "provided-string"));
		
		Assert.assertNotNull(to);;
		Assert.assertEquals(object1.i1, to.i1);
		Assert.assertEquals("provided-string", to.s1);
	}

	@Test
	public void test5c1() throws ProjectionBuilderError, ProjectionError {
		
		ProjectionRegistry registry = ProjectionRegistry.newInstance();
		
		Projection<Object1To> projection1 =
				Projection
					.bind(Object1To.class)
					
					.map("object2", true).source(Object1.class).optional()

					.map("id").source(Object1.class)
					
					.build(registry)
					;

		Assert.assertNotNull(projection1);
		
		Assert.assertNotNull(
				Projection
					.bind(Object2To.class)
					
					.map("id").source(Object2.class)
					
					.build(registry)
					);

		Object1 object1 = new Object1();
		object1.id = "AREW2324E";
		
		Object2 object2 = new Object2();
		object2.id = "3GFD42E";
		
		object1.object2 = object2;
		
		Object1To to = projection1.compose(object1);
		
		Assert.assertNotNull(to);;
		Assert.assertEquals(object1.id, to.id);
		
		Assert.assertNotNull(to.object2);
		Assert.assertEquals(object2.id, to.object2.id);		
	}

	@Test
	public void test5e1() throws ProjectionBuilderError, ProjectionError {
		
		ProjectionRegistry registry = ProjectionRegistry.newInstance();
		
		Projection<Object1To> projection1 =
				Projection
					.bind(Object1To.class)
					
					.map("object2", true).source(Object1.class).optional()

					.map("id").source(Object1.class)
					
					.build(registry)
					;
				
		Assert.assertNotNull(projection1);

		Assert.assertNotNull(
				Projection
					.bind(Object2To.class)
					
					.map("id").source(Object2.class)
					
					.build(registry)
					);
		
		Object1To to1 = new Object1To();
		to1.id = "AREW2324E";
		
		Object2To to2 = new Object2To();
		to2.id = "3GFD42E";
		
		to1.object2 = to2;
		
		Object1 object1 = projection1.extract(to1, Object1.class);
		
		Assert.assertNotNull(object1);;
		Assert.assertEquals(to1.id, object1.id);
		
		Assert.assertNotNull(object1.object2);
		Assert.assertEquals(to2.id, object1.object2.id);		
	}
	@Test
	public void test5c2() throws ProjectionBuilderError, ProjectionError {
		
		ProjectionRegistry registry = ProjectionRegistry.newInstance();

		Projection<Object1To> projection1 =
				Projection
					.bind(Object1To.class)
					
					.map("object2", true).source(Object1.class).optional()

					.map("id").source(Object1.class)
					
					.build(registry)
					;

		Assert.assertNotNull(projection1);
		
		Assert.assertNotNull(
				Projection
					.bind(Object2To.class)
					
					.map("id").source(Object2.class)
					
					.build(registry)
					);

		Object1 object1 = new Object1();
		object1.id = "AREW2324E";	
		object1.object2 = null;
		
		Object1To to = projection1.compose(object1);
		
		Assert.assertNotNull(to);;
		Assert.assertEquals(object1.id, to.id);
		
		Assert.assertNull(to.object2);
	}
	@Test
	public void test6() throws ProjectionBuilderError, ProjectionError {
		final Projection<Object1To> projection = 
				Projection
					.bind(Object1To.class)
					
					.map("id").constant("StringContant")
					
					.build(ProjectionRegistry.newInstance());
		
		Assert.assertNotNull(projection);
		
		Object1To to = projection.compose();
		Assert.assertNotNull(to);;
		Assert.assertEquals("StringContant", to.id);
	}

	@Test
	public void test7() throws ProjectionBuilderError, ProjectionError {
		final Projection<Object1To> projection = 
				Projection
					.bind(Object1To.class)
					
					.map("id")
						.sources()
							.conversion(UriTemplate.class, "/{}/{}")							
							.conversion(Prefix.class, "https://example.org")

							.source(Object1.class)
							
							.source(Object1.class, "id")
								.conversion(Prefix.class, "1")
								.conversion(Suffix.class, "2")
						
					.build(ProjectionRegistry.newInstance());
		
		Assert.assertNotNull(projection);
		
		Object1 object1 = new Object1();
		object1.id = "A";
		
		Object1To to = projection.compose(object1);
				
		Assert.assertNotNull(to);
		
		Assert.assertEquals("https://example.org/A/1A2", to.id);
	}

	@Test
	public void test8() throws ProjectionBuilderError {
		final Projection<SimpleObjectTo> projection = 
				Projection
					.bind(SimpleObjectTo.class)					
					.build(ProjectionRegistry.newInstance());
		
		Assert.assertNull(projection);
	}

	@Test
	public void test9() throws ProjectionBuilderError, ProjectionError {
		
		ProjectionRegistry registry = ProjectionRegistry.newInstance();
		
		Projection<Object1To> projection1 =
				ProjectionApiImpl
					.bind(Object1To.class)
					
					.map("object2", true).source(Object1.class).optional()

					.map("id").provided()
					
					.build(registry)
					;
		
		Assert.assertNotNull(projection1);

		Assert.assertNotNull(
				Projection
					.bind(Object2To.class)
					
					.map("id").provided()
					
					.build(registry)
					);
		
		Object1 object1 = new Object1();
		
		Object2 object2 = new Object2();
		
		object1.object2 = object2;
		
		Object1To to = projection1.compose(object1, "AREW2324E");
		
		Assert.assertNotNull(to);;
		Assert.assertEquals("AREW2324E", to.id);
		
		Assert.assertNotNull(to.object2);
		Assert.assertEquals("AREW2324E", to.object2.id);		
	}

	@Test
	public void test10() throws ProjectionBuilderError, ProjectionError {
		
		ProjectionRegistry registry = ProjectionRegistry.newInstance();
		
		Projection<Object1To> projection1 =
				Projection
					.bind(Object1To.class)
					
					.map("object2", true).source(Object1.class).optional()

					.map("id").provided("id")
					
					.build(registry)
					;

		Assert.assertNotNull(projection1);
		
		Assert.assertNotNull(
				Projection
					.bind(Object2To.class)
					
					.map("id").provided("id")
					
					.build(registry)
					);
		
		Object1 object1 = new Object1();
		
		Object2 object2 = new Object2();
		
		object1.object2 = object2;
		
		Object1To to = projection1.compose(object1,  SourceObject.of("id", "AREW2324E"));
		
		Assert.assertNotNull(to);;
		Assert.assertEquals("AREW2324E", to.id);
		
		Assert.assertNotNull(to.object2);
		Assert.assertEquals("AREW2324E", to.object2.id);		
	}
	
	@Test
	public void test11() throws ProjectionBuilderError, ProjectionError {
		
		ProjectionRegistry registry = ProjectionRegistry.newInstance();
		
		Projection<Object1To> projection1 =
				Projection
					.bind(Object1To.class)
					
					.map("object2", true).provided("obj2")

					.map("id").provided("id")
					
					.build(registry)
					;

		Assert.assertNotNull(projection1);
		
		Assert.assertNotNull(
				ProjectionApiImpl
					.bind(Object2To.class)
					
					.map("id").provided("id")
					
					.build(registry)
					);
		
		Object1 object1 = new Object1();
		
		Object2 object2 = new Object2();
				
		Object1To to = projection1.compose( 
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
	public void test12() throws ProjectionBuilderError, ProjectionError {
		
		ProjectionRegistry registry = ProjectionRegistry.newInstance();
		
		Projection<UriTo> projection =
				Projection
					.bind(UriTo.class)
					
					.map("uri")
						.source(UriObject.class)
						.conversion(Suffix.class, "/d/e/f")
					
					.build(registry)
					;
		
		Assert.assertNotNull(projection);

		UriObject object1 = new UriObject();
		object1.uri = URI.create("https://example.org/a/b/c");
		
		UriTo to = projection.compose(object1);
		
		Assert.assertNotNull(to);;
		Assert.assertEquals(object1.uri.toString() + "/d/e/f", to.uri);		
	}
	
	@Test
	public void test13() throws ProjectionBuilderError, ProjectionError {
		
		ProjectionRegistry registry = ProjectionRegistry.newInstance();
		
		Assert.assertNotNull( 
				Projection
					.bind(Object1To.class)
					
					.map("object2", true).provided("obj2")

					.map("id").provided("id")
					
					.build(registry)
					);

		Assert.assertNotNull(
				ProjectionApiImpl
					.bind(Object2To.class)
					
					.map("id").provided("id")
					
					.build(registry)
					);
		
		Object1To to = new Object1To();
		to.id = "AREW2324E";
		
		Object2To to2 = new Object2To();
		to2.id = "3GFD42EE7";
		to.object2  = to2;
		
		String id = registry.get(Object1To.class).extract(to, "id", String.class);
		Assert.assertEquals(to.id, id);

		String id2 = registry.get(Object1To.class).extract(to, "obj2.id", String.class);
		Assert.assertEquals(to2.id, id2);	
	}
	
	@Test
	public void test14() throws ProjectionBuilderError, ProjectionError {
		final Projection<SimpleObjectTo> projection = 
				Projection
					.bind(SimpleObjectTo.class)
					.map("s1")
						.sources()
							.conversion(String[].class, String.class)
								.forward(sources -> sources[1] + sources[0])

							.source(SimpleObject.class)
								.conversion(String.class, String.class)
									.forward(String::toUpperCase)
									
							.source(SimpleObject.class, "i1")
					
					.build(ProjectionRegistry.newInstance());
		
		Assert.assertNotNull(projection);

		SimpleObject object1 = new SimpleObject();
		object1.i1 = 443546356;
		object1.s1 = "string-1";
		
		SimpleObjectTo to = projection.compose(object1);
		
		Assert.assertNotNull(to);;
		Assert.assertEquals(object1.i1 + object1.s1.toUpperCase(), to.s1);
	}
}
