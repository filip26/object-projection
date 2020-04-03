package com.apicatalog.projection.builder.api;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionBuilder;
import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.builder.PropertyBuilder;
import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.mapper.SourceMapper;
import com.apicatalog.projection.property.ProjectionProperty;
import com.apicatalog.projection.property.SourceProperty;
import com.apicatalog.projection.source.SingleSource;

public class SourcePropertyBuilderApi<P> implements PropertyBuilder {
	
	ProjectionBuilder<P> projectionBuilder;
	
	String targetPropertyName;
	
	String sourcePropertyName;
	
	Class<?> sourceObjectClass;

	boolean reference;
	
	protected SourcePropertyBuilderApi(ProjectionBuilder<P> projectionBuilder, String targetPropertyName, boolean reference, Class<?> sourceObjectClass, String sourcePropertyName) {
		this.projectionBuilder = projectionBuilder;
		this.targetPropertyName = targetPropertyName;
		this.reference = reference;
		this.sourcePropertyName = sourcePropertyName;
		this.sourceObjectClass = sourceObjectClass;
	}
	

	public SourcePropertyBuilderApi<P> optional() {

		
		return this;
	}

	public SourcePropertyBuilderApi<P> required() {

		
		return this;
	}


	public SourcePropertyBuilderApi<P> qualifier(String qualifier) {

		
		return this;
	}
	
	public NamedPropertyBuilderApi<P> map(String propertyName) {
		return projectionBuilder.map(propertyName);
	}

	
	public Projection<P> build(ProjectionFactory factory, TypeAdapters typeAdapters) {
		return projectionBuilder.build(factory, typeAdapters);
	}


	public SourcePropertyBuilderApi<P> conversion(Class<? extends Converter<?, ?>> converter, String...params) {

		return this;
	}



	public SourcesPropertyBuilderApi<P> source(Class<?> sourceClass, String sourceProperty) {

		
		return new SourcesPropertyBuilderApi<>(projectionBuilder);
	}

	public SourcesPropertyBuilderApi<P> source(Class<?> sourceClass) {

		
		return new SourcesPropertyBuilderApi<>(projectionBuilder);
	}


	@Override
	public ProjectionProperty getProperty(ProjectionFactory factory, TypeAdapters typeAdapters) {
		
		SourceProperty sourceProperty = new SourceProperty();

		//FIXME
		SingleSource source = (new SourceMapper(factory, typeAdapters)).getSingleSource(sourceObjectClass, sourcePropertyName, false, null, AccessMode.READ_WRITE, null);
		
		sourceProperty.setSource(source);

		return sourceProperty;
	}	
}
