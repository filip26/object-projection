package com.apicatalog.projection;

import org.junit.Test;

import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.converter.std.Prefix;
import com.apicatalog.projection.converter.std.Suffix;
import com.apicatalog.projection.objects.Object1;
import com.apicatalog.projection.objects.SimpleObject;
import com.apicatalog.projection.projections.Object1To;
import com.apicatalog.projection.projections.SimpleObjectTo;

public class ProjectionBuilderTest {


	@Test
	public void test1() {
		final Projection<SimpleObjectTo> projection = 
				ProjectionBuilder
					.of(SimpleObjectTo.class)
					
					.map("i1").source(SimpleObject.class)
					.map("s1").source(SimpleObject.class)
					
					.build(new TypeAdapters());
		

	}

	@Test
	public void test2() {
		final Projection<SimpleObjectTo> projection = 
				ProjectionBuilder
					.of(SimpleObjectTo.class)
					
					.map("i1").source("s1", SimpleObject.class)
					.map("s1").source("i1", SimpleObject.class)
					
					.build(new TypeAdapters());
	}

	@Test
	public void test3() {
		final Projection<SimpleObjectTo> projection = 
				ProjectionBuilder
					.of(SimpleObjectTo.class)
					
					.map("s1").source(SimpleObject.class)
								.conversion(Prefix.class, "StringToPrepend")
								.conversion(Suffix.class, "StringToAppend")
								.optional()
								
					.map("i1").source(SimpleObject.class)
					
					.build(new TypeAdapters());
	}

	@Test
	public void test4() {
		final Projection<SimpleObjectTo> projection = 
				ProjectionBuilder
					.of(SimpleObjectTo.class)
					
					.map("s1").provided()
								.optional()
								.qualifier("string1")
								
					.map("i1").source(SimpleObject.class)
					
					.build(new TypeAdapters());
	}

	@Test
	public void test5() {
		final Projection<Object1To> projection = 
				ProjectionBuilder
					.of(Object1To.class)
					
					.ref("object2").source(Object1.class).optional()

					.map("id").source(Object1.class)
					
					.build(new TypeAdapters());
	}

}
