package com.apicatalog.projection.mapper.mapping;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.converter.ConvertorError;
import com.apicatalog.projection.mapping.PropertyMapping;
import com.apicatalog.projection.mapping.SourceMapping;
import com.apicatalog.projection.mapping.TargetMapping;
import com.apicatalog.projection.objects.SourceObjects;

public class PropertyMappingImpl implements PropertyMapping {

	final Logger logger = LoggerFactory.getLogger(PropertyMappingImpl.class);

	String name;

	SourceMapping source;
		
	TargetMapping target;

	@Override
	public Object compose(int level, SourceObjects sources) throws ProjectionError, ConvertorError {

		logger.debug("Compose property {} value at level {}", name, level);
		
		// get source value
		Optional<Object> value = Optional.ofNullable(source.compose(sources));

		if (value.isEmpty()) {
			logger.trace("  value = null");
			return null;
		}
		
		final Object propertyValue = value.get();
		
		logger.trace("  value = {}", propertyValue);
		
		return target.construct(level, propertyValue, sources);
	}
	
	@Override
	public void decompose(final Object object, SourceObjects sources) throws ProjectionError, ConvertorError {

		logger.debug("Decompose {} = {}", name, object);
		
		// get target value
		Optional<Object> value = Optional.ofNullable(target.deconstruct(object, sources));

		if (value.isEmpty()) {
			logger.trace("  = null");
			return;
		}
		
		Object sourceObject = value.get();
		
		logger.trace("  = {}", sourceObject);

		source.decompose(sourceObject, sources);
		
//		if (value.isEmpty())	
//		
//		
//		// is the target a reference on a projection
//		if (target.isReference()) { 
//
//			// is the target a collection of references?
//			if (target.isCollection()) {
//				
//				final Collection<Object> collection = new ArrayList<>();
//				
//				for (Object item : ((Collection<Object>)object)) {	//FIXME type check, implicit conversion					
////					Optional.ofNullable(target.getReference().decompose(item))
////							.ifPresent(collection::add);
//				}
//				
//				//TODO merge same values
//				
////				value = Optional.ofNullable(collection);
//
//			} else {
//
//				Object[] v = target.getReference().decompose(object);
//				
////				value = Optional.ofNullable(v[0]);			//FIXME hack!
////				if (v.length > 1) {	
////					sources.addOrReplace(v[1]);
////				}
//			}
//		}
//		
////		if (value.isEmpty()) {
////			return;
////		}
////		
////		source.decompose(value.get());
//		
//		return sources;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public SourceMapping getSource() {
		return source;
	}

	public void setSource(SourceMapping source) {
		this.source = source;
	}

	@Override
	public TargetMapping getTarget() {
		return target;
	}

	public void setTarget(TargetMapping target) {
		this.target = target;
	}
}
