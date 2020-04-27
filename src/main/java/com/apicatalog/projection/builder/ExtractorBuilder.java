package com.apicatalog.projection.builder;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Registry;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.property.target.CollectionExtractor;
import com.apicatalog.projection.property.target.ObjectExtractor;
import com.apicatalog.projection.property.target.TargetExtractor;

public final class ExtractorBuilder {

	final Logger logger = LoggerFactory.getLogger(ExtractorBuilder.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	Getter getter;
	
	String targetProjectionName; 

	protected ExtractorBuilder() {
	}
	
	public static final ExtractorBuilder newInstance() {
		return new ExtractorBuilder();
	}
		
	public Optional<TargetExtractor> build(Registry registry) {
		
		if (getter == null) {
			return Optional.empty();
		}

		if (StringUtils.isNotBlank(targetProjectionName)) {
			if (getter.getType().isCollection()) {
				return collection(targetProjectionName, registry);
			}
			if (getter.getType().isArray()) {
				return collection(targetProjectionName, registry);
			}
			
			return object(targetProjectionName, registry);
		}

		return Optional.empty();
	}
	
	public ExtractorBuilder getter(Getter getter) {
		this.getter = getter;
		return this;
	}

	public ExtractorBuilder targetProjection(String targetProjectionName) {
		this.targetProjectionName = targetProjectionName;
		return this;
	}

	final Optional<TargetExtractor> collection(final String projectionName, final Registry registry) {
		
		final CollectionExtractor extractor = new CollectionExtractor(getter.getType(), projectionName);
		
		registry.request(projectionName, extractor::setProjection);
		
		return Optional.of(extractor);		
	}

	final Optional<TargetExtractor> object(final String projectionName, final Registry registry) {
		
		final ObjectExtractor extractor = new ObjectExtractor(projectionName);
		
		registry.request(projectionName, extractor::setProjection);
		
		return Optional.of(extractor);		
	}

}
