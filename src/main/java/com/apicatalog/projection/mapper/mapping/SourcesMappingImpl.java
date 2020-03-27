package com.apicatalog.projection.mapper.mapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.mapping.ConversionMapping;
import com.apicatalog.projection.mapping.ReductionMapping;
import com.apicatalog.projection.mapping.SourceMapping;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.Path;

public class SourcesMappingImpl implements SourceMapping {

	final Logger logger = LoggerFactory.getLogger(SourcesMappingImpl.class);

	Collection<SourceMapping> mappings;

	ReductionMapping reduction;
	ConversionMapping[] conversions;

	Boolean optional;

	@Override
	public Object compose(Path path, ContextObjects sources) throws ProjectionError {

		final List<Object> values = new ArrayList<>();
		
		for (SourceMapping source : mappings) {
			Optional.ofNullable(source.compose(path, sources))
					.ifPresent(values::add);
		}

		// apply reduction
		Object value = reduction.reduce(values.toArray(new Object[0]));

		// apply explicit conversions
		if (conversions != null) {
			for (ConversionMapping conversion : conversions) {
				value = conversion.forward(value);
			}
		}

		return value;
	}

	@Override
	public void decompose(Path path, Object[] objects, ContextObjects sources) throws ProjectionError {
		
		logger.debug("Decompose {}, optional={}", objects, optional);

		if (objects == null || objects.length != 1) {	//FIXME hack
			return;
		}
		
		Optional<Object> value = Optional.of(objects[0]);

		// apply explicit deconversions
		if (Optional.ofNullable(conversions).isPresent()) {
			
			// reverse order
			final ArrayList<ConversionMapping> revConversions = new ArrayList<>(Arrays.asList(conversions));
			Collections.reverse(revConversions);
			
			for (ConversionMapping conversion : revConversions) {
				
				value = Optional.ofNullable(conversion.backward(value.get()));
				
				if (value.isEmpty()) {
					break;
				}
			}
		}

		if (value.isEmpty()) {
			logger.trace("  = null");
			return;
		}

		// apply reduction
		final Object[] sourceValues = reduction.expand(value.get());
		int it = 0;
		
		for (SourceMapping sourceMapping : mappings) {
			if (it > sourceValues.length) {
				continue;
			}
			sourceMapping.decompose(path, new Object[] {sourceValues[it++]}, sources);
		}
	}

	public Collection<SourceMapping> getSources() {
		return mappings;
	}

	public void setSources(Collection<SourceMapping> sources) {
		this.mappings = sources;
	}

	public Boolean getOptional() {
		return optional;
	}

	public void setOptional(Boolean optional) {
		this.optional = optional;
	}

	public 	ConversionMapping[] getConversions() {
		return conversions;
	}

	public void setConversions(ConversionMapping[] conversions) {
		this.conversions = conversions;
	}

	public ReductionMapping getReduction() {
		return reduction;
	}

	public void setReduction(ReductionMapping reduction) {
		this.reduction = reduction;
	}
}
