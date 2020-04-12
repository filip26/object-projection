package com.apicatalog.projection.builder.api;

import java.lang.reflect.Field;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.builder.ConstantPropertyBuilder;
import com.apicatalog.projection.conversion.implicit.TypeConversions;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.getter.FieldGetter;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.FieldSetter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.ProjectionProperty;

public class MappedPropertyBuilderApi<P> {
	
	final ProjectionBuilder<P> projectionBuilder;
	
	final TypeConversions typeConversions;
	
	SourcePropertyBuilderApi<P> sourcePropertyBuilder;
	ProvidedPropertyBuilderApi<P> providedPropertyBuilder;
	SourcesPropertyBuilderApi<P> sourcesPropertyBuilder;

	ConstantPropertyBuilder constantBuilder;
	
	String targetPropertyName;
	
	boolean reference;
	
	protected MappedPropertyBuilderApi(ProjectionBuilder<P> projection, TypeConversions typeConversions, String propertyName, boolean reference) {
		this.projectionBuilder = projection;
		this.typeConversions = typeConversions;
		this.targetPropertyName = propertyName;
		this.reference = reference;
	}
	
	public SourcePropertyBuilderApi<P> source(Class<?> sourceClass) {
		return source(sourceClass, null);
	}

	public SourcePropertyBuilderApi<P> source(Class<?> sourceClass, String sourceProperty) {

		SourcePropertyBuilderApi<P> builder = new SourcePropertyBuilderApi<>(
				projectionBuilder,
				sourceClass,
				// use the same name if source property name is not present
				StringUtils.isNotBlank(sourceProperty) ? sourceProperty : targetPropertyName,
				typeConversions
				);
		this.sourcePropertyBuilder = builder;
		return builder;
	}

	public SourcesPropertyBuilderApi<P> sources() {
		SourcesPropertyBuilderApi<P> builder = new SourcesPropertyBuilderApi<>(projectionBuilder, targetPropertyName, typeConversions);
		this.sourcesPropertyBuilder = builder;
		return builder;
	}

	public Projection<P> build(ProjectionRegistry factory) throws ProjectionError {
		return projectionBuilder.build(factory);
	}

	public ProvidedPropertyBuilderApi<P> provided() {
		return provided(null);
	}
	
	public ProvidedPropertyBuilderApi<P> provided(String qualifier) {
		ProvidedPropertyBuilderApi<P> builder = new ProvidedPropertyBuilderApi<>(projectionBuilder).qualifier(qualifier);
		this.providedPropertyBuilder = builder;
		return builder;
	}

	public ProjectionBuilder<P> constant(String...values) {
		this.constantBuilder = ConstantPropertyBuilder.newInstance().constants(values);
		
		return projectionBuilder;
	}
	
	protected Optional<ProjectionProperty> buildProperty(ProjectionRegistry registry) throws ProjectionError {
		
		final Field field = ObjectUtils.getProperty(projectionBuilder.projectionClass(), targetPropertyName);
		
		ObjectType targetType = ObjectUtils.getTypeOf(field);

		// extract setter
		final Setter targetSetter = FieldSetter.from(field, targetType);
		
		if  (Optional.ofNullable(sourcePropertyBuilder).isPresent()) {

			// extract getter
			final Getter targetGetter = FieldGetter.from(field, targetType);

			sourcePropertyBuilder.targetGetter(targetGetter);
			sourcePropertyBuilder.targetSetter(targetSetter);
			sourcePropertyBuilder.targetReference(reference);
			
			return sourcePropertyBuilder.buildProperty(registry);
			
		} else 	if (Optional.ofNullable(providedPropertyBuilder).isPresent()) {

			// extract getter
			final Getter targetGetter = FieldGetter.from(field, targetType);

			providedPropertyBuilder.targetGetter(targetGetter);
			providedPropertyBuilder.targetSetter(targetSetter);
			providedPropertyBuilder.targetReference(reference);
			
			return providedPropertyBuilder.buildProperty(registry);
			
		} else 	if (Optional.ofNullable(constantBuilder).isPresent()) {
			
			constantBuilder.targetSetter(targetSetter, reference);
			
			return constantBuilder.build(registry);
			
		} else 	if  (Optional.ofNullable(sourcesPropertyBuilder).isPresent()) {

			// extract getter
			final Getter targetGetter = FieldGetter.from(field, targetType);

			sourcesPropertyBuilder.targetGetter(targetGetter);
			sourcesPropertyBuilder.targetSetter(targetSetter);
			sourcesPropertyBuilder.targetReference(reference);
			
			return sourcesPropertyBuilder.buildProperty(registry);

		}
		return Optional.empty();
	}	
}
