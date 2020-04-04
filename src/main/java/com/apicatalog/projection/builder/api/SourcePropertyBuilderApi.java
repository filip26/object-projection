package com.apicatalog.projection.builder.api;

import java.util.ArrayList;
import java.util.List;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.builder.ConversionBuilder;
import com.apicatalog.projection.builder.SingleSourceBuilder;
import com.apicatalog.projection.builder.SourcePropertyBuilder;
import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.objects.getter.Getter;
import com.apicatalog.projection.objects.setter.Setter;
import com.apicatalog.projection.property.ProjectionProperty;
import com.apicatalog.projection.property.source.Source;

public class SourcePropertyBuilderApi<P> {
	
	final ProjectionBuilder<P> projectionBuilder;
	
	SingleSourceBuilder sourceBuilder;
	
	SourcePropertyBuilder sourcePropertyBuilder;
	
	List<ConversionBuilder> conversionBuilder;
	
	Class<?> sourceObjectClass;
	String sourcePropertyName;
	
	protected SourcePropertyBuilderApi(ProjectionBuilder<P> projectionBuilder, Class<?> sourceObjectClass, String sourcePropertyName) {
		this.projectionBuilder = projectionBuilder;
		this.sourceBuilder = SingleSourceBuilder.newInstance().objectClass(sourceObjectClass);
		this.sourcePropertyBuilder = SourcePropertyBuilder.newInstance();
		this.sourceObjectClass = sourceObjectClass;
		this.sourcePropertyName = sourcePropertyName;
		this.conversionBuilder = new ArrayList<>();
	}
	
	public SourcePropertyBuilderApi<P> optional() {
		sourceBuilder = sourceBuilder.optional(true);
		return this;
	}

	public SourcePropertyBuilderApi<P> required() {
		sourceBuilder = sourceBuilder.optional(false);
		return this;
	}
	
	public SourcePropertyBuilderApi<P> readOnly() {
		sourceBuilder = sourceBuilder.mode(AccessMode.READ_ONLY);
		sourcePropertyBuilder = sourcePropertyBuilder.mode(AccessMode.READ_ONLY);
		return this;
	}

	public SourcePropertyBuilderApi<P> writeOnly() {
		sourceBuilder = sourceBuilder.mode(AccessMode.WRITE_ONLY);
		sourcePropertyBuilder = sourcePropertyBuilder.mode(AccessMode.WRITE_ONLY);
		return this;
	}

	public SourcePropertyBuilderApi<P> qualifier(String qualifier) {
		sourceBuilder = sourceBuilder.qualifier(qualifier);
		return this;
	}
	
	protected SourcePropertyBuilderApi<P> targetGetter(Getter targetGetter) {
		sourcePropertyBuilder = sourcePropertyBuilder.targetGetter(targetGetter); 
		return this;
	}

	protected SourcePropertyBuilderApi<P> targetSetter(Setter targetSetter) {
		sourcePropertyBuilder = sourcePropertyBuilder.targetSetter(targetSetter);
		return this;
	}
	
	public MappedPropertyBuilderApi<P> map(String propertyName) {
		return projectionBuilder.map(propertyName);
	}

	public Projection<P> build(ProjectionRegistry factory, TypeAdapters typeAdapters) throws ProjectionError {
		return projectionBuilder.build(factory, typeAdapters);
	}

	public SourcePropertyBuilderApi<P> conversion(Class<? extends Converter<?, ?>> converter, String...params) {
		conversionBuilder.add(ConversionBuilder.newInstance().converter(converter).parameters(params));
		return this;
	}

	protected Source buildSource(TypeAdapters typeAdapters, SingleSourceBuilder sourceBuilder) throws ProjectionError {

		ConverterMapping[] converters = new ConverterMapping[conversionBuilder.size()];
		
		try {
			int i=0;
			for (ConversionBuilder cb : conversionBuilder) {
				converters[i++] = cb.build();
			}
			
		} catch (ConverterError e) {
			throw new ProjectionError(e);
		}
		
		// extract setter/getter
		final Getter sourceGetter = MappedPropertyBuilderApi.getGetter(sourceObjectClass, sourcePropertyName, false);
		final Setter sourceSetter = MappedPropertyBuilderApi.getSetter(sourceObjectClass, sourcePropertyName, false);
		
		return sourceBuilder
							.getter(sourceGetter)
							.setter(sourceSetter)
							.converters(converters)
							.build(typeAdapters)
							;		
	}	

	protected ProjectionProperty buildProperty(ProjectionRegistry factory, TypeAdapters typeAdapters) throws ProjectionError {				
		return sourcePropertyBuilder
					.source(buildSource(typeAdapters, sourceBuilder))
					.build(factory, typeAdapters);
	}
}