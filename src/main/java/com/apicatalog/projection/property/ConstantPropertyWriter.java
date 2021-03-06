package com.apicatalog.projection.property;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.object.ObjectError;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.source.SourceType;

public final class ConstantPropertyWriter implements PropertyWriter {

	final Logger logger = LoggerFactory.getLogger(ConstantPropertyWriter.class);

	String[] constants;
	
	Collection<Conversion<Object, Object>> conversions;
	
	Setter targetSetter;
	
	Set<Integer> visibleLevels;

	@Override
	public void write(final ProjectionStack stack, final CompositionContext context) throws CompositionError {
		
		if (targetSetter == null) {
			return;
		}

		logger.debug("Forward constant = {}, depth = {}", constants, stack.length());

		Optional<Object> object = Optional.of(constants);
				
		// apply conversions
		if (conversions != null) {
			try {
				for (final Conversion<Object, Object> conversion : conversions) {
					if (object.isEmpty()) {
						break;
					}
					object = Optional.ofNullable(conversion.convert(object.get()));
				}
			} catch (ConverterError e) {
				throw new CompositionError(e);
			}
		}
		
		if (object.isPresent()) {
			try {
				targetSetter.set(stack.peek(), object.get());
			} catch (ObjectError e) {
				throw new CompositionError("Can not set " + object.get() + " to " + stack.peek().getClass().getCanonicalName() + "." + targetSetter.getName() + ".");
			}
		}
	}

	public void setConstants(String[] constants) {
		this.constants = constants;
	}

	public void setTargetSetter(Setter targetSetter) {
		this.targetSetter = targetSetter;
	}
	
	public void setConversions(Collection<Conversion<Object, Object>> conversions) {
		this.conversions = conversions;
	}
	
	@Override
	public boolean isVisible(int depth) {
		return visibleLevels == null || visibleLevels.isEmpty() || visibleLevels.contains(depth);
	}
	
	public void setVisibility(final Set<Integer> levels) {
		this.visibleLevels = levels;
	}

	@Override
	public Collection<SourceType> getSourceTypes() {
		return Collections.emptySet();
	}

	@Override
	public String getName() {
		return targetSetter.getName();
	}

	@Override
	public String getDependency() {
		return null;
	}
}
