package com.apicatalog.projection.builder.api;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.builder.ConversionBuilder;
import com.apicatalog.projection.builder.SingleSourceBuilder;
import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.objects.getter.Getter;
import com.apicatalog.projection.objects.setter.Setter;
import com.apicatalog.projection.property.source.Source;

public class SourcesBuilderApi<P> {
	
	ProjectionBuilder<P> projectionBuilder;

	final LinkedList<SourceHolder> sourceHolders;
	
	final String targetPropertyName;
	
	protected SourcesBuilderApi(ProjectionBuilder<P> projection, String targetPropertyName) {
		this.projectionBuilder = projection;
		this.sourceHolders = new LinkedList<>();
		this.targetPropertyName = targetPropertyName;
	}

	public SourcesBuilderApi<P> optional() {
		sourceHolders.getLast().builder.optional(true);		
		return this;
	}

	public SourcesBuilderApi<P> required() {
		sourceHolders.getLast().builder.optional(false);
		return this;
	}
	
	public SourcesBuilderApi<P> readOnly() {
		sourceHolders.getLast().builder.mode(AccessMode.READ_ONLY);
		return this;
	}

	public SourcesBuilderApi<P> writeOnly() {
		sourceHolders.getLast().builder.mode(AccessMode.WRITE_ONLY);
		return this;
	}

	public SourcesBuilderApi<P> source(Class<?> sourceClass, String sourceProperty) {
		sourceHolders.add(new SourceHolder(sourceClass, StringUtils.isNotBlank(sourceProperty) ? sourceProperty : targetPropertyName));
		return this;
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

	public SourcesBuilderApi<P> conversion(Class<? extends Converter<?, ?>> converter, String...params) {
		sourceHolders.getLast().conversions.add(ConversionBuilder.newInstance().converter(converter).parameters(params));
		return this;
	}

	protected Optional<Source> buildSource(TypeAdapters typeAdapters, SourceHolder sourceHolder) throws ProjectionError {

		List<ConverterMapping> converters = new ArrayList<>(sourceHolder.conversions.size());
				
		try {
			
			for (ConversionBuilder cb : sourceHolder.conversions) {
				converters.add(cb.build());
			}
			
		} catch (ConverterError e) {
			throw new ProjectionError(e);
		}
		
		// extract setter/getter
		final Getter sourceGetter = MappedPropertyBuilderApi.getGetter(sourceHolder.objectClass, sourceHolder.propertyName, false);
		final Setter sourceSetter = MappedPropertyBuilderApi.getSetter(sourceHolder.objectClass, sourceHolder.propertyName, false);
		
		return sourceHolder.builder
							.objectClass(sourceHolder.objectClass)
							.getter(sourceGetter)
							.setter(sourceSetter)
							.converters(converters.toArray(new ConverterMapping[0]))
							.build(typeAdapters).map(Source.class::cast)
							;	
	}

	protected Source[] buildSources(TypeAdapters typeAdapters) throws ProjectionError {
		final ArrayList<Source> sources = new ArrayList<>(sourceHolders.size());
		
		for (SourceHolder holder : sourceHolders) {
			buildSource(typeAdapters, holder).ifPresent(sources::add);
		}
		return sources.toArray(new Source[0]);
	}
	
	class SourceHolder {
		
		protected List<ConversionBuilder> conversions;
		SingleSourceBuilder builder;
		Class<?> objectClass;
		String propertyName;
		
		public SourceHolder(Class<?> objectClass, String propertyName) {
			this.objectClass = objectClass;
			this.propertyName = propertyName;
			this.builder = SingleSourceBuilder.newInstance();
			this.conversions = new ArrayList<>(5);
		}
	}
	
}
