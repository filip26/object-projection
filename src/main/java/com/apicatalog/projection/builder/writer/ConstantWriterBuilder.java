package com.apicatalog.projection.builder.writer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Registry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.conversion.ConversionNotFound;
import com.apicatalog.projection.conversion.TypeConversions;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.ConstantPropertyWriter;

public final class ConstantWriterBuilder {

	final Logger logger = LoggerFactory.getLogger(ConstantWriterBuilder.class);

	String[] constants;
	
	Setter targetSetter;
	
	String targetProjectionName;
	
	protected ConstantWriterBuilder() {
	}
	
	public static final ConstantWriterBuilder newInstance() {
		return new ConstantWriterBuilder();
	}
	
	public Optional<ConstantPropertyWriter> build(final Registry registry) throws ProjectionError {
		
		final ConstantPropertyWriter property = new ConstantPropertyWriter();
		
		ObjectType sourceTargetType = targetSetter.getType();
		
		if (StringUtils.isNotBlank(targetProjectionName)) {
			
			if (targetSetter.getType().isCollection()) {
				sourceTargetType = ObjectType.of(targetSetter.getType().getType(), Object.class);
				
			} else if (targetSetter.getType().isArray()) {
				sourceTargetType = ObjectType.of(Object[].class);
				
			} else {
				sourceTargetType = ObjectType.of(Object.class);
			}
		}

		// set constant values
		property.setConstants(constants);
		
		// set target writer
		property.setTargetSetter(targetSetter);
		
		try {
			// set source conversion if needed
			property.setConversions(buildChain(constants, registry.getTypeConversions(), sourceTargetType));
			
			return Optional.of(property);
			
		} catch (ConversionNotFound e) {
			throw new ProjectionError("Can not build constant property.", e);
		}
	}	
	
	final Collection<Conversion<Object, Object>> buildChain(final String[] constants,final TypeConversions typeConversions, final ObjectType targetType) throws ConversionNotFound {

		ObjectType sourceType = ObjectType.of(String[].class);

		final ArrayList<Conversion<Object, Object>> conversions = new ArrayList<>(1);
		
		if (constants.length == 1 && !targetType.isArray() && !targetType.isCollection()) {
			conversions.add(c -> ((String[])c)[0]);		// reduce to one string constant
			sourceType = ObjectType.of(String.class);
		}

		typeConversions.get(
				sourceType,
				targetType
				)
			.ifPresent(conversions::add);
		
		return conversions;
	}
	
	public ConstantWriterBuilder targetSetter(final Setter targetSetter) {
		this.targetSetter = targetSetter;
		return this;
	}
	
	public ConstantWriterBuilder targetProjection(final String targetProjectionName) {
		this.targetProjectionName = targetProjectionName;
		return this;
	}
	
	public ConstantWriterBuilder constants(final String[] constants) {
		this.constants = constants;
		return this;
	}
}
