package com.apicatalog.projection.mapper.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.converter.ConvertorError;
import com.apicatalog.projection.mapping.ConversionMapping;
import com.apicatalog.projection.mapping.SourceMapping;
import com.apicatalog.projection.objects.SourceObjects;

public class SourcesMappingImpl implements SourceMapping {

	Collection<SourceMapping> mappings;
	
	Boolean optional;
	
	ConversionMapping[] conversions;

	@Override
	public Object compose(SourceObjects sources) throws ProjectionError, ConvertorError {

		final List<Object> values = new ArrayList<>();
		
		for (SourceMapping source : mappings) {
			Optional.ofNullable(source.compose(sources))
					.ifPresent(values::add);
		}
		
		Object value = values;
		
		// apply explicit conversions
		if (conversions != null) {
			for (ConversionMapping conversion : conversions) {
				value = conversion.forward(value);
			}
		}

		return value;
	}

	@Override
	public void decompose(Object[] object, SourceObjects sources) {
		// TODO Auto-generated method stub
		return;
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

	@Override
	public Class<?> getSourceClass() {
		// TODO Auto-generated method stub
		return null;
	}
}
