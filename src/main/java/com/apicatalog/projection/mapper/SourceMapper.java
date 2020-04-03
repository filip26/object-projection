package com.apicatalog.projection.mapper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.annotation.Conversion;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.beans.FieldGetter;
import com.apicatalog.projection.beans.FieldSetter;
import com.apicatalog.projection.beans.Getter;
import com.apicatalog.projection.beans.Setter;
import com.apicatalog.projection.builder.TargetBuilder;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.property.ProjectionProperty;
import com.apicatalog.projection.property.SourceProperty;
import com.apicatalog.projection.reducer.ReducerError;
import com.apicatalog.projection.source.ArraySource;
import com.apicatalog.projection.source.SingleSource;

public class SourceMapper {

	final Logger logger = LoggerFactory.getLogger(SourceMapper.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	final TypeAdapters typeAdapters;
	final ProjectionFactory factory;
	
	final ConversionMapper conversionMapper;
	final ReductionMapper reductionMapper;
	
	public SourceMapper(ProjectionFactory index, TypeAdapters typeAdapters) {
		this.factory = index;
		this.typeAdapters = typeAdapters;
		
		this.conversionMapper = new ConversionMapper(index, typeAdapters);
		this.reductionMapper = new ReductionMapper(index, typeAdapters);
	}
		
	ProjectionProperty getSourcesPropertyMapping(final Field field, final Class<?> defaultSourceClass) {
		
		final Sources sourcesAnnotation = field.getAnnotation(Sources.class);

		final SourceProperty property = new SourceProperty();

		final ArraySource arraySource = getArraySource(sourcesAnnotation, field, defaultSourceClass);

		if (arraySource == null) {
			logger.warn(SOURCE_IS_MISSING, field.getName());
			return null;				
		}
		
		property.setSource(arraySource);

		// extract setter/getter
		final Getter targetGetter = FieldGetter.from(field, PropertyMapper.getTypeOf(field));
		final Setter targetSetter = FieldSetter.from(field, PropertyMapper.getTypeOf(field));

		property.setTargetSetter(targetSetter);			
		property.setTargetGetter(targetGetter);
		
		property.setTargetAdapter(
				TargetBuilder.newInstance()
					.source(ObjectType.of(arraySource.getTargetClass(), arraySource.getTargetComponentClass()))
					.target(targetSetter.getType())
					.build(factory, typeAdapters)
					);

		return property;
	}
	
	ProjectionProperty getSourcePropertyMapping(final Field field, final Class<?> defaultSourceClass) {
		
		final Source sourceAnnotation = field.getAnnotation(Source.class);
		
		final SourceProperty property = new SourceProperty();


		final SingleSource source = 
					getSingleSource( 
						sourceAnnotation,
						field,
						defaultSourceClass
						); 
		
		if (source == null) {
			logger.warn(SOURCE_IS_MISSING, field.getName());
			return null;
		}

		property.setSource(source);

		// extract setter/getter
		final Getter targetGetter = FieldGetter.from(field, PropertyMapper.getTypeOf(field));
		final Setter targetSetter = FieldSetter.from(field, PropertyMapper.getTypeOf(field));

		// set access mode
		switch (sourceAnnotation.mode()) {
		case READ_ONLY:
			property.setTargetSetter(targetSetter);
			break;
			
		case WRITE_ONLY:
			property.setTargetGetter(targetGetter);
			break;
		
		case READ_WRITE:
			property.setTargetSetter(targetSetter);			
			property.setTargetGetter(targetGetter);
			break;
		}			

		property.setTargetAdapter(
				TargetBuilder.newInstance()
					.source(ObjectType.of(source.getTargetClass(), source.getTargetComponentClass()))
					.target(targetSetter.getType())
					.build(factory, typeAdapters)
					);

		return property;		
	}

	SingleSource getSingleSource(Source sourceAnnotation, Field field, Class<?> defaultSourceClass) {
				
		Class<?> sourceObjectClass = defaultSourceClass;
		
		// override source property class
		if (!Class.class.equals(sourceAnnotation.type())) {
			sourceObjectClass = sourceAnnotation.type();
		}
		
		if (sourceObjectClass == null) {
			logger.warn(SOURCE_IS_MISSING, field.getName());
			return null;
		}

		// set default source object property name -> use the same name
		String sourceFieldName = field.getName();
				
		// override source property name
		if (StringUtils.isNotBlank(sourceAnnotation.value())) {
			sourceFieldName = sourceAnnotation.value();
		}
		
		return getSingleSource(
					sourceObjectClass, 
					sourceFieldName, 
					sourceAnnotation.optional(), 
					sourceAnnotation.qualifier(), 
					sourceAnnotation.mode(),
					sourceAnnotation.map()
					);
	}
	
	public SingleSource getSingleSource(Class<?> sourceObjectClass, String sourceFieldName, boolean optional, String qualifier, AccessMode mode, Conversion[] conversions) {
		
		final SingleSource source = new SingleSource(typeAdapters);

		source.setObjectClass(sourceObjectClass);

		// extract setter/getter
		final Getter sourceGetter = PropertyMapper.getGetter(sourceObjectClass, sourceFieldName);
		final Setter sourceSetter = PropertyMapper.getSetter(sourceObjectClass, sourceFieldName);

		// no setter nor getter? 
		if (sourceGetter == null && sourceSetter == null) {
			// nothing to do with this
			return null;
		}
		
		// set source access
		switch (mode) {
		case READ_ONLY:
			source.setGetter(sourceGetter);
			break;
			
		case WRITE_ONLY:
			source.setSetter(sourceSetter);
			break;
		
		case READ_WRITE:
			source.setGetter(sourceGetter);
			source.setSetter(sourceSetter);
			break;
		}			

		// set conversions to apply
		if (Optional.ofNullable(conversions).isPresent()) {
			try {
				source.setConversions(conversionMapper.getConverterMapping(conversions));
				
			} catch (ConverterError | ProjectionError e) {
				logger.error("Property " + sourceFieldName + " is ignored.", e);
				return null;
			}
		}

		// set optional 
		source.setOptional(optional);
		
		// set qualifier
		source.setQualifier(qualifier);
		
		// set target class
		Class<?> targetClass = sourceGetter != null ? sourceGetter.getType().getObjectClass() : sourceSetter.getType().getObjectClass();
		Class<?> targetComponentClass = sourceGetter != null ? sourceGetter.getType().getObjectComponentClass() : sourceSetter.getType().getObjectComponentClass();

		source.setTargetClass(targetClass);
		source.setTargetComponentClass(targetComponentClass);

		// extract actual target object class 
		if (source.getConversions() != null) {
			Stream.of(source.getConversions())
					.reduce((first, second) -> second)
					.ifPresent(m -> { 
 
								source.setTargetClass(m.getSourceClass());
								source.setTargetComponentClass(m.getSourceComponentClass());
								});
					}
		
		return source;
	}

	ArraySource getArraySource(final Sources sourcesAnnotation, final Field field, final Class<?> defaultSourceObjectClass) {
		
		final ArraySource source = new ArraySource(typeAdapters);
		
		SingleSource[] sources = Arrays.stream(sourcesAnnotation.value())
										.map(s -> getSingleSource(s, field, defaultSourceObjectClass))
										.collect(Collectors.toList())
										.toArray(new SingleSource[0])
										;
		
		if (sources.length == 0) {
			logger.warn(SOURCE_IS_MISSING, field.getName());
			return null;
		}

		source.setSources(sources);
		
		try {
			// set reduction
			source.setReduction(reductionMapper.getReductionMapping(sourcesAnnotation.reduce()));
			
			// set conversions to apply
			source.setConversions(conversionMapper.getConverterMapping(sourcesAnnotation.map()));
			
		} catch (ConverterError | ReducerError | ProjectionError e) {
			logger.error("Property " + field.getName() + " is ignored.", e);
			return null;
		}

		// set optional 
		source.setOptional(sourcesAnnotation.optional());
				
		// set target class
		Class<?> targetClass =  source.getReduction().getTargetClass();
		Class<?> targetComponentClass = source.getReduction().getTargetComponentClass();
		
		
		source.setTargetClass(targetClass);
		source.setTargetComponentClass(targetComponentClass);
		
		// extract actual target object class 
		if (source.getConversions() != null) {
			Stream.of(source.getConversions())
					.reduce((first, second) -> second)
					.ifPresent(m -> { 
 
								source.setTargetClass(m.getSourceClass());
								source.setTargetComponentClass(m.getSourceComponentClass());
								});
					}
		
		return source;
	}	
}
