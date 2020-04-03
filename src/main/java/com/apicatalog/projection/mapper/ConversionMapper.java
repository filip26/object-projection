package com.apicatalog.projection.mapper;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionFactory;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.annotation.Conversion;
import com.apicatalog.projection.builder.ConversionBuilder;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.mapping.ConverterMapping;

public class ConversionMapper {

	final Logger logger = LoggerFactory.getLogger(ConversionMapper.class);
	
	final TypeAdapters typeAdapters;
	final ProjectionFactory factory;
	
	public ConversionMapper(ProjectionFactory index, TypeAdapters typeAdapters) {
		this.factory = index;
		this.typeAdapters = typeAdapters;
	}
	
	public ConverterMapping[] getConverterMapping(Conversion[] conversions) throws ConverterError, ProjectionError {

		if (conversions.length == 0) {
			return new ConverterMapping[0];
		}

		final List<ConverterMapping> converters = new ArrayList<>();
		
		for (final Conversion conversion : conversions) {
			converters.add(
					ConversionBuilder
							.newInstance()
							.converter(conversion.type())
							.parameters(conversion.value())
							.build(typeAdapters)
							);
		}

		return converters.isEmpty() ? null : converters.toArray(new ConverterMapping[0]);
	}
}
