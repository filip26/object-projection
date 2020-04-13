package com.apicatalog.projection.builder.writer;

import java.util.ArrayList;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.conversion.UnknownConversion;
import com.apicatalog.projection.conversion.implicit.TypeConversions;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.ConstantPropertyWriter;
import com.apicatalog.projection.property.target.TargetWriter;

public final class ConstantPropertyWriterBuilder {

	final Logger logger = LoggerFactory.getLogger(ConstantPropertyWriterBuilder.class);

	String[] constants;
	
	Setter setter;
	
	boolean reference;
	
	protected ConstantPropertyWriterBuilder() {
	}
	
	public static final ConstantPropertyWriterBuilder newInstance() {
		return new ConstantPropertyWriterBuilder();
	}
	
	public Optional<ConstantPropertyWriter> build(final ProjectionRegistry registry) throws ProjectionError {
		
		final ConstantPropertyWriter property = new ConstantPropertyWriter();
		
		final Optional<TargetWriter> targetWriter = TargetWriterBuilder.newInstance()
										.setter(setter, reference)
										.build(registry);

		if (targetWriter.isEmpty()) {
			return Optional.empty();
		}

		// set constant values
		property.setConstants(constants);
		
		// set target writer
		property.setTargetWriter(targetWriter.get());
		
		try {
			// set source conversion if needed
			property.setConversions(buildChain(constants, registry.getTypeConversions(), targetWriter.get().getType()));
			
			return Optional.of(property);
			
		} catch (UnknownConversion e) {
			throw new ProjectionError(e);
		}
	}	
	
	final Conversion[] buildChain(final String[] constants,final TypeConversions typeConversions, final ObjectType targetType) throws UnknownConversion {

		ObjectType sourceType = ObjectType.of(String[].class);

		final ArrayList<Conversion> conversions = new ArrayList<>(1);
		
		if (constants.length == 1 && !targetType.isArray() && !targetType.isCollection()) {
			conversions.add(c -> ((String[])c)[0]);		// reduce to one string constant
			sourceType = ObjectType.of(String.class);
		}

		typeConversions.get(
				sourceType,
				targetType
				)
			.ifPresent(conversions::add);
		
		return conversions.toArray(new Conversion[0]);
	}
	
	public ConstantPropertyWriterBuilder targetSetter(final Setter targetSetter, final boolean reference) {
		this.setter = targetSetter;
		this.reference = reference;
		return this;
	}
	
	public ConstantPropertyWriterBuilder constants(final String[] constants) {
		this.constants = constants;
		return this;
	}
}
