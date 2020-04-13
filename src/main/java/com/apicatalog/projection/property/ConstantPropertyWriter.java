package com.apicatalog.projection.property;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.property.target.TargetWriter;

public class ConstantPropertyWriter implements PropertyWriter {

	final Logger logger = LoggerFactory.getLogger(ConstantPropertyWriter.class);

	String[] constants;
	
	Conversion[] conversions;
	
	TargetWriter targetWriter;
	
	Set<Integer> visibleLevels;

	@Override
	public void write(ProjectionStack queue, CompositionContext context) throws ProjectionError {
		
		if (targetWriter == null) {
			return;
		}

		logger.debug("Forward constant = {}, depth = {}", constants, queue.length());

		Optional<Object> object = Optional.of(constants);
		
		// apply explicit conversions
		if (conversions != null) {
			try {
				for (final Conversion conversion : conversions) {
					if (object.isEmpty()) {
						break;
					}
					object = Optional.ofNullable(conversion.convert(object.get()));
				}
			} catch (ConverterError e) {
				throw new ProjectionError(e);
			}
		}
		
		if (object.isPresent()) {
			targetWriter.write(queue, context, object.get());
		}
	}

	public void setConstants(String[] constants) {
		this.constants = constants;
	}

	public void setTargetWriter(TargetWriter targetWriter) {
		this.targetWriter = targetWriter;
	}
	
	public void setConversions(Conversion[] conversions) {
		this.conversions = conversions;
	}
	
	@Override
	public boolean isVisible(int depth) {
		return visibleLevels == null || visibleLevels.isEmpty() || visibleLevels.contains(depth);
	}
	
	public void setVisibility(final Set<Integer> levels) {
		this.visibleLevels = levels;
	}
}
