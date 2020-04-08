package com.apicatalog.projection.builder.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.builder.ArraySourceBuilder;
import com.apicatalog.projection.builder.ConversionBuilder;
import com.apicatalog.projection.builder.ReductionBuilder;
import com.apicatalog.projection.builder.SourcePropertyBuilder;
import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.objects.getter.Getter;
import com.apicatalog.projection.objects.setter.Setter;
import com.apicatalog.projection.property.ProjectionProperty;
import com.apicatalog.projection.property.source.ArraySource;
import com.apicatalog.projection.property.source.Source;
import com.apicatalog.projection.reducer.Reducer;
import com.apicatalog.projection.reducer.ReducerError;
import com.apicatalog.projection.reducer.ReducerMapping;

public class SourcesPropertyBuilderApi<P> {
	
	ProjectionBuilder<P> projectionBuilder;

	final List<ConversionBuilder> conversionBuilder;

	SourcesBuilderApi<P> sourcesBuilderApi;
	
	SourcePropertyBuilder sourcePropertyBuilder;
	
	ArraySourceBuilder arraySourceBuilder;
	
	ReductionBuilder reductionBuilder;
	
	final String projectionPropertyName;
	
	protected SourcesPropertyBuilderApi(ProjectionBuilder<P> projection, String projectionPropertyName) {
		this.projectionBuilder = projection;
		this.conversionBuilder = new ArrayList<>();
		this.sourcePropertyBuilder = SourcePropertyBuilder.newInstance();
		this.arraySourceBuilder = ArraySourceBuilder.newInstance();
		this.reductionBuilder = ReductionBuilder.newInstance();
		this.projectionPropertyName = projectionPropertyName;
	}

	public SourcesPropertyBuilderApi<P> optional() {
		arraySourceBuilder.optional(true);
		return this;
	}

	public SourcesPropertyBuilderApi<P> required() {
		arraySourceBuilder.optional(false);
		return this;
	}

	public SourcesBuilderApi<P> source(Class<?> sourceClass, String sourceProperty) {

		sourcesBuilderApi = new SourcesBuilderApi<>(projectionBuilder, projectionPropertyName)
								.source(sourceClass, sourceProperty);
		return sourcesBuilderApi;
	}

	public SourcesBuilderApi<P> source(Class<?> sourceClass) {
		return source(sourceClass, null);
	}
	
	public MappedPropertyBuilderApi<P> map(String propertyName) {
		return projectionBuilder.map(propertyName);
	}
	
	public Projection<P> build(ProjectionRegistry factory, TypeAdapters typeAdapters) throws ProjectionError {
		return projectionBuilder.build(factory, typeAdapters);
	}

	public SourcesPropertyBuilderApi<P> conversion(Class<? extends Converter<?, ?>> converter, String...params) {
		conversionBuilder.add(ConversionBuilder.newInstance().converter(converter).parameters(params));
		return this;
	}

	public SourcesPropertyBuilderApi<P> reduce(Class<? extends Reducer<?, ?>> reducer, String...params) {
		reductionBuilder.converter(reducer).parameters(params);
		return this;
	}

	protected SourcesPropertyBuilderApi<P> targetGetter(Getter targetGetter) {
		sourcePropertyBuilder = sourcePropertyBuilder.targetGetter(targetGetter); 
		return this;
	}

	protected SourcesPropertyBuilderApi<P> targetSetter(Setter targetSetter) {
		sourcePropertyBuilder = sourcePropertyBuilder.targetSetter(targetSetter);
		return this;
	}
	
	protected SourcesPropertyBuilderApi<P> targetReference(boolean reference) {
		sourcePropertyBuilder = sourcePropertyBuilder.targetReference(reference);
		return this;
	}

	protected Optional<ProjectionProperty> buildProperty(ProjectionRegistry factory, TypeAdapters typeAdapters) throws ProjectionError {

		if (Optional.ofNullable(sourcesBuilderApi).isEmpty()) {
			return Optional.empty();
		}
		
		final ConverterMapping[] converters = new ConverterMapping[conversionBuilder.size()];
		
		ReducerMapping reducer;
		
		try {
			int i = 0;
			for (ConversionBuilder cb : conversionBuilder) {
				converters[i++] = cb.build();
			}
			
			reducer = reductionBuilder.build();
			
		} catch (ConverterError | ReducerError e) {
			throw new ProjectionError(e);
		}

		Source[] sources = sourcesBuilderApi.buildSources(typeAdapters);

		Optional<ArraySource> source = arraySourceBuilder
								.sources(sources)
								.converters(converters)
								.reducer(reducer)
								.build(typeAdapters);
		
		if (source.isEmpty()) {
			return Optional.empty();
		}
		
		return sourcePropertyBuilder
				.source(source.get())
				.build(factory, typeAdapters).map(ProjectionProperty.class::cast);
	}
}
