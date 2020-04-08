package com.apicatalog.projection.builder.api;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.builder.ConstantPropertyBuilder;
import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.objects.ObjectUtils;
import com.apicatalog.projection.objects.getter.FieldGetter;
import com.apicatalog.projection.objects.getter.Getter;
import com.apicatalog.projection.objects.getter.MethodGetter;
import com.apicatalog.projection.objects.setter.FieldSetter;
import com.apicatalog.projection.objects.setter.MethodSetter;
import com.apicatalog.projection.objects.setter.Setter;
import com.apicatalog.projection.property.ProjectionProperty;

public class MappedPropertyBuilderApi<P> {
	
	final ProjectionBuilder<P> projectionBuilder;
	
	SourcePropertyBuilderApi<P> sourcePropertyBuilder;
	ProvidedPropertyBuilderApi<P> providedPropertyBuilder;
	SourcesPropertyBuilderApi<P> sourcesPropertyBuilder;

	ConstantPropertyBuilder constantBuilder;
	
	String targetPropertyName;
	
	boolean reference;
	
	protected MappedPropertyBuilderApi(ProjectionBuilder<P> projection, String propertyName, boolean reference) {
		this.projectionBuilder = projection;
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
				StringUtils.isNotBlank(sourceProperty) ? sourceProperty : targetPropertyName
				);
		this.sourcePropertyBuilder = builder;
		return builder;
	}

	public SourcesPropertyBuilderApi<P> sources() {
		SourcesPropertyBuilderApi<P> builder = new SourcesPropertyBuilderApi<>(projectionBuilder, targetPropertyName);
		this.sourcesPropertyBuilder = builder;
		return builder;
	}

	public Projection<P> build(ProjectionRegistry factory, TypeAdapters typeAdapters) throws ProjectionError {
		return projectionBuilder.build(factory, typeAdapters);
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
	
	protected Optional<ProjectionProperty> buildProperty(ProjectionRegistry registry, TypeAdapters typeAdapters) throws ProjectionError {
		
		final Field field = ObjectUtils.getProperty(projectionBuilder.projectionClass(), targetPropertyName);
		
		ObjectType targetType = getTypeOf(field, reference);

		// extract setter
		final Setter targetSetter = FieldSetter.from(field, targetType);
		
		if  (Optional.ofNullable(sourcePropertyBuilder).isPresent()) {

			// extract getter
			final Getter targetGetter = FieldGetter.from(field, targetType);

			sourcePropertyBuilder.targetGetter(targetGetter);
			sourcePropertyBuilder.targetSetter(targetSetter);
			
			return sourcePropertyBuilder.buildProperty(registry, typeAdapters);
			
		} else 	if (Optional.ofNullable(providedPropertyBuilder).isPresent()) {

			// extract getter
			final Getter targetGetter = FieldGetter.from(field, targetType);

			providedPropertyBuilder.targetGetter(targetGetter);
			providedPropertyBuilder.targetSetter(targetSetter);
			
			return providedPropertyBuilder.buildProperty(registry, typeAdapters);
			
		} else 	if (Optional.ofNullable(constantBuilder).isPresent()) {
			
			constantBuilder.targetSetter(targetSetter);
			return constantBuilder.build(registry, typeAdapters);
			
		} else 	if  (Optional.ofNullable(sourcesPropertyBuilder).isPresent()) {

			// extract getter
			final Getter targetGetter = FieldGetter.from(field, targetType);

			sourcesPropertyBuilder.targetGetter(targetGetter);
			sourcesPropertyBuilder.targetSetter(targetSetter);
			
			return sourcesPropertyBuilder.buildProperty(registry, typeAdapters);

		}
		return Optional.empty();
	}
	
	protected static final ObjectType getTypeOf(Field field, boolean reference) {
		
		Class<?> objectClass = field.getType();
		Class<?> componentClass = null;
		
		if (Collection.class.isAssignableFrom(field.getType())) {
			componentClass = (Class<?>)((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
		}
		
		return ObjectType.of(objectClass, componentClass, reference);
	}
	
	protected static final ObjectType getTypeOf(Method method, boolean reference) {
		
		Class<?> objectClass = method.getReturnType();
		Class<?> componentClass = null;

		if (Collection.class.isAssignableFrom(method.getReturnType())) {
			componentClass = (Class<?>)((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
		}
		
		return ObjectType.of(objectClass, componentClass, reference);
	}

	protected static final Getter getGetter(Class<?> objectClass, final String name, boolean reference) {

		final Field sourceField = ObjectUtils.getProperty(objectClass, name);
		
		if (sourceField != null) {
			return FieldGetter.from(sourceField, getTypeOf(sourceField, reference));
		}

		// look for getter method
		final Method sourceGetter = ObjectUtils.getMethod(objectClass, "get".concat(StringUtils.capitalize(name)));
		
		if (sourceGetter != null) {
			return MethodGetter.from(sourceGetter, name, getTypeOf(sourceGetter, reference));
		}

		return null;
	}
	
	protected static final Setter getSetter(Class<?> objectClass, final String name, boolean reference) {

		final Field sourceField = ObjectUtils.getProperty(objectClass, name);
		
		if (sourceField != null) {
			return FieldSetter.from(sourceField, getTypeOf(sourceField, reference));
		}

		// look for getter method
		final Method sourceGetter = ObjectUtils.getMethod(objectClass, "set".concat(StringUtils.capitalize(name)));
		
		if (sourceGetter != null) {
			return MethodSetter.from(sourceGetter, name, getTypeOf(sourceGetter, reference));
		}

		return null;
	}
	
}
