package com.apicatalog.projection.builder.reader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.conversion.UnknownConversion;
import com.apicatalog.projection.conversion.explicit.ForwardExplicitConversion;
import com.apicatalog.projection.conversion.implicit.TypeConversions;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.property.source.SingleSourceReader;
import com.apicatalog.projection.source.SourceType;

public class SingleSourceReaderBuilder {

	final Logger logger = LoggerFactory.getLogger(SingleSourceReaderBuilder.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	boolean optional;
	
	String qualifier;
	
	AccessMode mode;
	
	Collection<ConverterMapping> converters;
	
	Class<?> sourceObjectClass;
	
	Getter sourceGetter;
	
	ObjectType targetType;

	protected SingleSourceReaderBuilder() {
		this.mode = AccessMode.READ_ONLY;
		this.optional = false;
	}
	
	public static SingleSourceReaderBuilder newInstance() {
		return new SingleSourceReaderBuilder();
	}

	public Optional<SingleSourceReader> build(TypeConversions typeConverters) throws ProjectionError {

		// no getter ? 
		if (sourceGetter == null || targetType == null) {
			// nothing to do with this
			return Optional.empty();
		}

		final SingleSourceReader source = new SingleSourceReader();

		source.setSourceObjectType(SourceType.of(qualifier, sourceObjectClass));
		
		// set source access
		switch (mode) {
		
		case READ_ONLY:
		case READ_WRITE:
			source.setGetter(sourceGetter);
			break;
			
		default:
			// nothing to do with this
			return Optional.empty(); 
		}			

		// set default source type for an array of sources
		source.setType(targetType);

		try {
			// set conversions to apply
			source.setType(buildChain(source, converters, typeConverters));
	
			// set optional 
			source.setOptional(optional);
	
			return Optional.of(source);
			
		} catch (UnknownConversion e) {
			throw new ProjectionError(e);
		}
	}
	
	public SingleSourceReaderBuilder mode(AccessMode mode) {
		this.mode = mode;
		return this;
	}
	
	public SingleSourceReaderBuilder optional(boolean optional) {
		this.optional = optional;
		return this;
	}
	
	public SingleSourceReaderBuilder qualifier(String qualifier) {
		this.qualifier = StringUtils.isBlank(qualifier) ? null : qualifier.strip();
		return this;
	}
	
	public SingleSourceReaderBuilder converters(Collection<ConverterMapping> converters) {
		this.converters = converters;
		return this;
	}
	
	public SingleSourceReaderBuilder objectClass(Class<?> sourceObjectClass) {
		this.sourceObjectClass = sourceObjectClass;
		return this;
	}
	
	public SingleSourceReaderBuilder getter(Getter getter) {
		this.sourceGetter = getter;
		return this;
	}
	
	public SingleSourceReaderBuilder targetType(ObjectType targetType) {
		this.targetType= targetType;
		return this;
	}	
	
	final ObjectType buildChain(final SingleSourceReader source, final Collection<ConverterMapping> converters, final TypeConversions typeConversions) throws UnknownConversion {

		final ArrayList<Conversion> conversions = new ArrayList<>((converters != null ? converters.size() : 0) * 2 + 1);

		if (converters == null || converters.isEmpty()) {
			
			if (Object.class == targetType.getType()) {
				return sourceGetter.getType();
			}

			typeConversions.get(
					sourceGetter.getType(),
					targetType)
				.ifPresent(conversions::add);


			source.setConversions(conversions.toArray(new Conversion[0]));
			
			
			return targetType;
		}
		
		final Iterator<ConverterMapping> it = converters.iterator();
		
		ConverterMapping mapping = it.next();
		
		typeConversions.get(sourceGetter.getType(), mapping.getSourceType()).ifPresent(conversions::add);
		
		conversions.add(ForwardExplicitConversion.of(mapping.getConversion()));

		while (it.hasNext()) {
			ConverterMapping next = it.next();

			typeConversions.get(
					mapping.getTargetType(),
					next.getSourceType())
				.ifPresent(conversions::add);
			
			conversions.add(ForwardExplicitConversion.of(next.getConversion()));			

			mapping = next;
		}
		
		typeConversions.get(
				mapping.getTargetType(),
				targetType)
			.ifPresent(conversions::add);


		source.setConversions(conversions.toArray(new Conversion[0]));
		return targetType;
	}
}
