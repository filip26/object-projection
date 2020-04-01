package com.apicatalog.projection.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ObjectConversion;
import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.annotation.Constant;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Provided;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.annotation.Visibility;
import com.apicatalog.projection.mapper.mapping.PropertyMappingImpl;
import com.apicatalog.projection.objects.ObjectUtils;
import com.apicatalog.projection.objects.access.FieldGetter;
import com.apicatalog.projection.objects.access.FieldSetter;
import com.apicatalog.projection.objects.access.Getter;
import com.apicatalog.projection.objects.access.MethodGetter;
import com.apicatalog.projection.objects.access.MethodSetter;
import com.apicatalog.projection.objects.access.Setter;
import com.apicatalog.projection.property.ConstantProperty;
import com.apicatalog.projection.property.ProjectionProperty;
import com.apicatalog.projection.property.ProvidedObjectProperty;
import com.apicatalog.projection.property.ProvidedProjectionProperty;
import com.apicatalog.projection.property.SourceProperty;
import com.apicatalog.projection.target.TargetAdapter;
import com.apicatalog.projection.target.TargetProjectionConverter;
import com.apicatalog.projection.target.TargetTypeConverter;

public class PropertyMapper {

	final Logger logger = LoggerFactory.getLogger(PropertyMapper.class);
	
	final TypeAdapters typeAdapters;
	final ProjectionFactory factory;
	
	public PropertyMapper(ProjectionFactory index) {
		this.factory = index;
		this.typeAdapters = new TypeAdapters();
	}
	
	ProjectionProperty getPropertyMapping(final Field field, final Class<?> defaultSourceClass) {

		Optional<ProjectionProperty> mapping = Optional.empty();
		
		// single source? 
		if (field.isAnnotationPresent(Source.class)) {
			mapping = Optional.ofNullable(getSourcePropertyMapping(field, defaultSourceClass));

		// multiple sources?
		} else if (field.isAnnotationPresent(Sources.class) ) {
//			mapping = Optional.ofNullable(getSourcesPropertyMapping(field, defaultSourceClass));

		// provided -> global source
		} else if (field.isAnnotationPresent(Provided.class)) {
			mapping = Optional.ofNullable(getProvidedMapping(field));
			
		// constant value
		} else if (field.isAnnotationPresent(Constant.class)) {
			mapping = Optional.ofNullable(getConstantMapping(field));

		// direct mapping or a reference
		} else {
			mapping = Optional.ofNullable(getDefaultPropertyMapping(field, defaultSourceClass));
		}
		
		if (field.isAnnotationPresent(Visibility.class)) {
			mapping.ifPresent(m -> m.setVisible(
										IntStream.of(field.getAnnotation(Visibility.class).level())
												.boxed()
												.collect(Collectors.toSet())
							));
		}
		
		return mapping.orElse(null);
	}
	
	PropertyMappingImpl getSourcesPropertyMapping(final Field field, final Class<?> defaultSourceClass) {
		
		final Sources sources = field.getAnnotation(Sources.class);
		
//		final Optional<SourceMapping> sourceMappings = 
//					Optional.ofNullable( 
//								getSourcesMapping(
//										sources,  
//										field,
//										defaultSourceClass
//										)
//									);
		
//		if (sourceMappings.isEmpty()) {
//			return null;				
//		}

		final PropertyMappingImpl propertyMapping = new PropertyMappingImpl();
		
		// set projection property name
		propertyMapping.setName(field.getName());

		// set projection property value sources
//		sourceMappings.ifPresent(propertyMapping::setSource);
		
		return propertyMapping;
	}
	
	ProjectionProperty getSourcePropertyMapping(final Field field, final Class<?> defaultSourceClass) {
		
		final Source source = field.getAnnotation(Source.class);
		
		final SourceProperty property = new SourceProperty();
		
		// set default source object class
		Optional.ofNullable(defaultSourceClass).ifPresent(property::setSourceObjectClass);
		
		// override source property class
		if (!Class.class.equals(source.type())) {
			property.setSourceObjectClass(source.type());
		}
		
		if (property.getSourceObjectClass() == null) {
			logger.warn("Source class is missing. Property {} is ignored.", field.getName());
			return null;
		}

		// set default source object property name -> use the same name
		String sourceFieldName = field.getName();
				
		// override source property name
		if (StringUtils.isNotBlank(source.value())) {
			sourceFieldName = source.value();
		}

		// extract setter/getter
		final Getter sourceGetter = getGetter(property.getSourceObjectClass(), sourceFieldName);
		final Setter sourceSetter = getSetter(property.getSourceObjectClass(), sourceFieldName);


		//FIXME if (!setSourceGetterSetter(property, sourceFieldName)) {
			
			// no setter nor getter? nothing to do with this 
			//return null;
		//}
		
		// set source object qualifier
		if (StringUtils.isNotBlank(source.qualifier())) {
			property.setQualifier(source.qualifier());
		}
		
		
		// set conversions to apply
		property.setConversions(new ObjectConversion[0]);
//FIXME		property.setConversions(getConversionMapping(source.map()));
		
		// set optional 
		property.setOptional(source.optional());
		
		// set qualifier
		property.setSourceObjectQualifier(source.qualifier());

		// extract setter/getter
		final Getter targetGetter = FieldGetter.from(field);
		final Setter targetSetter = FieldSetter.from(field);

		// set access mode
		switch (source.mode()) {
		case READ_ONLY:
			property.setSourceGetter(sourceGetter);
			property.setTargetSetter(targetSetter);
			break;
			
		case WRITE_ONLY:
			property.setSourceSetter(sourceSetter);
			property.setTargetGetter(targetGetter);
			break;
		
		case READ_WRITE:
			property.setSourceGetter(sourceGetter);
			property.setTargetSetter(targetSetter);
			
			property.setSourceSetter(sourceSetter);
			property.setTargetGetter(targetGetter);
			break;
		}			

		property.setTargetAdapter(getTargetConverter(
										sourceGetter.getValueClass(), 
										sourceGetter.getValueComponentClass(), 
										targetSetter.getValueClass(), 
										targetSetter.getValueComponentClass()
										)
				);


		return property;
		
		// set default target object class
//		property.setTargetClass(property.getGetter().getValueClass());
//		property.setTargetComponentClass(property.getGetter().getValueComponentClass());
//		
//		// extract actual target object class 
//		if (property.getConversions() != null) {
//			
//			Stream.of(property.getConversions())
//					.reduce((first, second) -> second)
//					.map(ConversionMapping::getConverterMapping)
//					.ifPresent(m -> { 
//								sourceMapping.setTargetClass(m.getSourceClass());
//								sourceMapping.setTargetComponentClass(m.getSourceComponentClass());
//								});
//		}
//

//		final Optional<SourceMapping> sourceMapping = 
//				Optional.ofNullable(
//							getSourceMapping(
//										source, 
//										field,
//										defaultSourceClass
//									));
//		
//		if (sourceMapping.isEmpty()) {
//			return null;
//		}
//
//		final PropertyMappingImpl propertyMapping = new PropertyMappingImpl();
//		
//		// set projection property name
//		propertyMapping.setName(field.getName());
//
//		// set projection property value sources
////		sourceMapping.ifPresent(propertyMapping::setSource);
//
//		return propertyMapping;
	}

	ProjectionProperty getDefaultPropertyMapping(final Field field, final Class<?> defaultSourceClass) {

		if (defaultSourceClass == null) {
			logger.warn("Source class is missing. Property {} is ignored.", field.getName());
			return null;				
		}

		final SourceProperty property = new SourceProperty();
		property.setSourceObjectClass(defaultSourceClass);

		// extract setter/getter
		final Getter sourceGetter = getGetter(property.getSourceObjectClass(), field.getName());
		final Setter sourceSetter = getSetter(property.getSourceObjectClass(), field.getName());

		// set source access
		property.setSourceGetter(sourceGetter);
		property.setSourceSetter(sourceSetter);
		
		//TODO check if getter/setter exists
				
		property.setConversions(new ObjectConversion[0]);

		final Getter targetGetter = FieldGetter.from(field);
		final Setter targetSetter = FieldSetter.from(field);

		property.setTargetGetter(targetGetter);
		property.setTargetSetter(targetSetter);

		property.setTargetAdapter(getTargetConverter(
							sourceGetter.getValueClass(), 
							sourceGetter.getValueComponentClass(), 
							targetSetter.getValueClass(), 
							targetSetter.getValueComponentClass()
						)
				);

		return property;

//		// set default source object class
//		sourceMapping.setSourceObjectClass(defaultSourceClass);
//		
////		if (!setGetterSetter(sourceMapping, field.getName())) {
////			return null;
////		}
//		

	}

	ProjectionProperty getProvidedMapping(Field field) {
		
		final Provided provided = field.getAnnotation(Provided.class);
		
		if (field.getType().isAnnotationPresent(Projection.class)) {
			return getProvidedProjection(provided, field);
		}
		
		final ProvidedObjectProperty property = new ProvidedObjectProperty();
		
		final Getter targetGetter = FieldGetter.from(field);
		final Setter targetSetter = FieldSetter.from(field);
		
		// set access mode
		switch (provided.mode()) {
		case READ_ONLY:
			property.setTargetSetter(targetSetter);
			break;
			
		case WRITE_ONLY:
			property.setTargetGetter(targetGetter);
			break;
		
		case READ_WRITE:
			property.setTargetGetter(targetGetter);
			property.setTargetSetter(targetSetter);
			break;
		}			

		// set qualifier
		property.setSourceObjectQualifier(provided.qualifier());

		property.setTargetAdapter(getTargetConverter(null, null, targetSetter.getValueClass(), targetSetter.getValueComponentClass()));
		
		return property;
//		sourceMapping.setOptional(provided.optional());
		
	}
	
	ProjectionProperty getProvidedProjection(Provided provided, Field field) {
		
		final ProvidedProjectionProperty property = new ProvidedProjectionProperty(factory);

		// set access mode
		switch (provided.mode()) {
		case READ_ONLY:
			property.setTargetSetter(FieldSetter.from(field));
			break;
			
		case WRITE_ONLY:
			property.setTargetGetter(FieldGetter.from(field));
			break;
		
		case READ_WRITE:
			property.setTargetGetter(FieldGetter.from(field));
			property.setTargetSetter(FieldSetter.from(field));
			break;
		}	

		// set qualifier
		property.setSourceObjectQualifier(provided.qualifier());

		return property;
	}

	ProjectionProperty getConstantMapping(Field field) {
		
		final Constant constant = field.getAnnotation(Constant.class);
		
		final ConstantProperty property = new ConstantProperty();
		
		// set constant values
		property.setConstants(constant.value());
		
		Class<?> targetComponentClass = null; 
		
		if (Collection.class.isAssignableFrom(field.getType())) {
			// set target component class
			targetComponentClass = ((Class<?>)((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]);
		}

		property.setTargetAdapter(getTargetConverter(null, null, field.getType(), targetComponentClass));

		// set target setter
		property.setTargetSetter(new FieldSetter(field));
		
		return property;
	}
	
	Getter getGetter(Class<?> objectClass, final String name) {

		final Field sourceField = ObjectUtils.getProperty(objectClass, name);
		
		if (sourceField != null) {
			return FieldGetter.from(sourceField);
		}

		// look for getter method
		final Method sourceGetter = ObjectUtils.getMethod(objectClass, "get".concat(StringUtils.capitalize(name)));
		
		if (sourceGetter != null) {
			return new MethodGetter(sourceGetter, name);
		}

		return null;
	}
	
	Setter getSetter(Class<?> objectClass, final String name) {

		final Field sourceField = ObjectUtils.getProperty(objectClass, name);
		
		if (sourceField != null) {
			return FieldSetter.from(sourceField);
		}

		// look for getter method
		final Method sourceGetter = ObjectUtils.getMethod(objectClass, "set".concat(StringUtils.capitalize(name)));
		
		if (sourceGetter != null) {
			return new MethodSetter(sourceGetter, name);
		}

		return null;
	}

	TargetAdapter getTargetConverter(Class<?> sourceClass, Class<?> sourceComponentClass, Class<?> targetClass, Class<?> targetComponentClass) {

		if (targetClass.isAnnotationPresent(Projection.class)) {
			return new TargetProjectionConverter(factory, sourceClass, sourceComponentClass, targetClass, targetComponentClass);
		}
		
		return new TargetTypeConverter(typeAdapters, sourceClass, sourceComponentClass, targetClass, targetComponentClass);

	}
}
