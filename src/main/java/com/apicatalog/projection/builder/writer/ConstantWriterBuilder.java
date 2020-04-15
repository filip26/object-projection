package com.apicatalog.projection.builder.writer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.conversion.TypeConversions;
import com.apicatalog.projection.conversion.UnknownConversion;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.ConstantPropertyWriter;

public final class ConstantWriterBuilder {

	final Logger logger = LoggerFactory.getLogger(ConstantWriterBuilder.class);

	String[] constants;
	
	Setter targetSetter;
	
	boolean targetReference;
	
	protected ConstantWriterBuilder() {
	}
	
	public static final ConstantWriterBuilder newInstance() {
		return new ConstantWriterBuilder();
	}
	
	public Optional<ConstantPropertyWriter> build(final ProjectionRegistry registry) throws ProjectionBuilderError {
		
		final ConstantPropertyWriter property = new ConstantPropertyWriter();
		
		ObjectType sourceTargetType = targetSetter.getType();
		
		if (targetReference) {
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
			
		} catch (UnknownConversion e) {
			throw new ProjectionBuilderError(e);
		}
	}	
	
	final Collection<Conversion<Object, Object>> buildChain(final String[] constants,final TypeConversions typeConversions, final ObjectType targetType) throws UnknownConversion {

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
	
	public ConstantWriterBuilder targetSetter(final Setter targetSetter, final boolean reference) {
		this.targetSetter = targetSetter;
		this.targetReference = reference;
		return this;
	}
	
	public ConstantWriterBuilder constants(final String[] constants) {
		this.constants = constants;
		return this;
	}
}
