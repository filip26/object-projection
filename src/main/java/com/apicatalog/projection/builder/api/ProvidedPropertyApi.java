package com.apicatalog.projection.builder.api;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.builder.reader.ProvidedPropertyReaderBuilder;
import com.apicatalog.projection.builder.writer.ProvidedPropertyWriterBuilder;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public final class ProvidedPropertyApi<P> extends ValueProviderApi<P> {
	
	final ProjectionBuilder<P> projectionBuilder;

	final ProvidedPropertyReaderBuilder providedPropertyReaderBuilder;
	final ProvidedPropertyWriterBuilder providedPropertyWriterBuilder;
	
	protected ProvidedPropertyApi(final ProjectionBuilder<P> projection) {
		this.projectionBuilder = projection;
		this.providedPropertyReaderBuilder = ProvidedPropertyReaderBuilder.newInstance();
		this.providedPropertyWriterBuilder = ProvidedPropertyWriterBuilder.newInstance();
	}
	
	public ProvidedPropertyApi<P> optional() {
		providedPropertyReaderBuilder.optional(true);
		providedPropertyWriterBuilder.optional(true);
		return this;
	}

	public ProvidedPropertyApi<P> required() {
		providedPropertyReaderBuilder.optional(false);
		providedPropertyWriterBuilder.optional(false);
		return this;
	}

	public ProvidedPropertyApi<P> qualifier(final String qualifier) {
		
		final String name = StringUtils.isNotBlank(qualifier) ? qualifier : null;
		
		providedPropertyReaderBuilder.qualifier(name);
		providedPropertyWriterBuilder.qualifier(name);
		return this;
	}

	public PropertyApi<P> map(final String propertyName) {
		return projectionBuilder.map(propertyName);
	}

	public Projection<P> build(final ProjectionRegistry registry) throws ProjectionError {
		return projectionBuilder.build(registry);
	}	
	
	@Override
	protected Optional<PropertyReader> buildyReader(final ProjectionRegistry registry) throws ProjectionError {
		return providedPropertyReaderBuilder.build(registry).map(PropertyReader.class::cast);
	}

	@Override
	protected Optional<PropertyWriter> buildyWriter(final ProjectionRegistry registry) throws ProjectionError {
		return providedPropertyWriterBuilder.build(registry).map(PropertyWriter.class::cast);
	}
	
	@Override
	protected ProvidedPropertyApi<P> targetGetter(final Getter targetGetter) {
		providedPropertyReaderBuilder.targetGetter(targetGetter);
		return this;
	}

	@Override
	protected ProvidedPropertyApi<P> targetSetter(final Setter targetSetter) {
		providedPropertyWriterBuilder.targetSetter(targetSetter);
		return this;
	}
	
	@Override
	protected ProvidedPropertyApi<P> targetReference(final boolean reference) {
		providedPropertyReaderBuilder.targetReference(reference);
		providedPropertyWriterBuilder.targetReference(reference);
		return this;
	}
}
