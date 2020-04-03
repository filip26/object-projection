package com.apicatalog.projection;

import org.junit.Assert;
import org.junit.Test;

import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.converter.std.Prefix;
import com.apicatalog.projection.converter.std.Suffix;
import com.apicatalog.projection.converter.std.UriTemplate;
import com.apicatalog.projection.objects.Object1;
import com.apicatalog.projection.objects.SimpleObject;
import com.apicatalog.projection.projections.Object1To;
import com.apicatalog.projection.projections.SimpleObjectTo;
import com.apicatalog.projection.property.SourceProperty;
import com.apicatalog.projection.source.SingleSource;

public class ProjectionBuilderTest {


	@Test
	public void test1() {
		final Projection<SimpleObjectTo> projection = 
				ProjectionBuilder
					.bind(SimpleObjectTo.class)
					
					.map("i1").source(SimpleObject.class)
					
					.build(ProjectionFactory.newInstance(), new TypeAdapters());
		
		Assert.assertNotNull(projection);
		
		Assert.assertEquals(SimpleObjectTo.class, projection.getProjectionClass());
		
		Assert.assertNotNull(projection.getProperties());
		
		Assert.assertEquals(1, projection.getProperties().length);
		
		Assert.assertNotNull(projection.getProperties()[0]);
		Assert.assertTrue(SourceProperty.class.isInstance(projection.getProperties()[0]));
		
		SourceProperty sourceProperty = (SourceProperty)projection.getProperties()[0];
		
//		Assert.assertNotNull(sourceProperty.getSource());
//		Assert.assertTrue(SingleSource.class.isInstance(sourceProperty.getSource()));
//		
//		SingleSource singleSource = (SingleSource)sourceProperty.getSource();
//		
//		Assert.assertEquals(SimpleObject.class, singleSource.getObjectClass());
//		
//		Assert.assertNotNull(singleSource.getGetter());
//		Assert.assertEquals("i1", singleSource.getGetter().getName());
//		Assert.assertEquals(Integer.class, singleSource.getGetter().getType().getObjectClass());
//		Assert.assertNull(singleSource.getGetter().getType().getObjectComponentClass());
//		
//		Assert.assertEquals(Integer.class, singleSource.getTargetType().getObjectClass());
//		Assert.assertNull(singleSource.getTargetType().getObjectComponentClass());
//		
//		Assert.assertTrue(singleSource.isReadable());
//		Assert.assertTrue(singleSource.isWritable());
//		
//		Assert.assertNull(singleSource.getConversions());
//		
//		Assert.assertNull(sourceProperty.getTargetAdapter());
		
//FIXME		Assert.assertNotNull(sourceProperty.getTargetGetter());
		
		
		
//FIXME		Assert.assertNotNull(sourceProperty.getTargetSetter());
	}

	@Test
	public void test2() {
		final Projection<SimpleObjectTo> projection = 
				ProjectionBuilder
					.bind(SimpleObjectTo.class)
					
					.map("i1").source(SimpleObject.class, "s1")
					.map("s1").source(SimpleObject.class, "i1")
					
					.build(ProjectionFactory.newInstance(), new TypeAdapters());
	}

	@Test
	public void test3() {
		final Projection<SimpleObjectTo> projection = 
				ProjectionBuilder
					.bind(SimpleObjectTo.class)
					
					.map("s1").source(SimpleObject.class)
								.conversion(Prefix.class, "StringToPrepend")
								.conversion(Suffix.class, "StringToAppend")
								.optional()
								
					.map("i1").source(SimpleObject.class)
					
					.build(ProjectionFactory.newInstance(), new TypeAdapters());
	}

	@Test
	public void test4() {
		final Projection<SimpleObjectTo> projection = 
				ProjectionBuilder
					.bind(SimpleObjectTo.class)
					
					.map("s1").provided()
								.optional()
								.qualifier("string1")
								
					.map("i1").source(SimpleObject.class)
					
					.build(ProjectionFactory.newInstance(), new TypeAdapters());
	}

	@Test
	public void test5() {
		final Projection<Object1To> projection = 
				ProjectionBuilder
					.bind(Object1To.class)
					
					.map("object2").source(Object1.class).optional()

					.map("id").source(Object1.class)
					
					.build(ProjectionFactory.newInstance(), new TypeAdapters());
	}

	
	@Test
	public void test6() {
		final Projection<Object1To> projection = 
				ProjectionBuilder
					.bind(Object1To.class)
					
					.map("id").constant("StringContant")
					
					.build(ProjectionFactory.newInstance(), new TypeAdapters());
	}


	@Test
	public void test7() {
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
	public void test8() {
		final Projection<SimpleObjectTo> projection = 
				ProjectionBuilder
					.bind(SimpleObjectTo.class)					
					.build(ProjectionFactory.newInstance(), new TypeAdapters());
		
		Assert.assertNull(projection);
	}

}
