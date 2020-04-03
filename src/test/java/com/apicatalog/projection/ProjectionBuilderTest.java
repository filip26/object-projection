package com.apicatalog.projection;

import org.junit.Assert;
import org.junit.Test;

import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.builder.api.ProjectionBuilder;
import com.apicatalog.projection.converter.std.Prefix;
import com.apicatalog.projection.converter.std.Suffix;
import com.apicatalog.projection.converter.std.UriTemplate;
import com.apicatalog.projection.objects.NamedObject;
import com.apicatalog.projection.objects.Object1;
import com.apicatalog.projection.objects.SimpleObject;
import com.apicatalog.projection.projections.Object1To;
import com.apicatalog.projection.projections.SimpleObjectTo;

public class ProjectionBuilderTest {


	@Test
	public void test1() throws ProjectionError {
		final Projection<SimpleObjectTo> projection = 
				ProjectionBuilder
					.bind(SimpleObjectTo.class)
					
					.map("i1").source(SimpleObject.class)
					.map("s1").source(SimpleObject.class)
					
					.build(ProjectionFactory.newInstance(), new TypeAdapters());
		
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
					
					.build(ProjectionFactory.newInstance(), new TypeAdapters());
		
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
					
					.build(ProjectionFactory.newInstance(), new TypeAdapters());

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
					
					.build(ProjectionFactory.newInstance(), new TypeAdapters());

		Assert.assertNotNull(projection);

		SimpleObject object1 = new SimpleObject();
		object1.i1 = 443546356;
		
		SimpleObjectTo to = projection.compose(object1, NamedObject.of("string1", "provided-string"));
		
		Assert.assertNotNull(to);;
		Assert.assertEquals(object1.i1, to.i1);
		Assert.assertEquals("provided-string", to.s1);
	}

	@Test
	public void test5() throws ProjectionError {
		final Projection<Object1To> projection = 
				ProjectionBuilder
					.bind(Object1To.class)
					
					.map("object2").source(Object1.class).optional()

					.map("id").source(Object1.class)
					
					.build(ProjectionFactory.newInstance(), new TypeAdapters());
	}

	
	@Test
	public void test6() throws ProjectionError {
		final Projection<Object1To> projection = 
				ProjectionBuilder
					.bind(Object1To.class)
					
					.map("id").constant("StringContant")
					
					.build(ProjectionFactory.newInstance(), new TypeAdapters());
	}


	@Test
	public void test7() throws ProjectionError {
		final Projection<Object1To> projection = 
				ProjectionBuilder
					.bind(Object1To.class)
					
					.map("id")
						.source(Object1.class)
						.source(Object1.class, "i1")
						.reduce(UriTemplate.class, "/{}/{}")
						.conversion(Prefix.class, "https://example.org")
						
					.build(ProjectionFactory.newInstance(), new TypeAdapters());
	}

	@Test
	public void test8() throws ProjectionError {
		final Projection<SimpleObjectTo> projection = 
				ProjectionBuilder
					.bind(SimpleObjectTo.class)					
					.build(ProjectionFactory.newInstance(), new TypeAdapters());
		
		Assert.assertNull(projection);
	}

}
