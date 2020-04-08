package com.apicatalog.projection.property.source;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.objects.ObjectUtils;
import com.apicatalog.projection.objects.getter.Getter;
import com.apicatalog.projection.objects.setter.Setter;
import com.apicatalog.projection.source.SourceType;

public final class SingleSource implements Source {

	final Logger logger = LoggerFactory.getLogger(SingleSource.class);

	final TypeAdapters typeAdapters;	//TODO use concrete adapters set during mapping
	
	Getter getter;
	Setter setter;

	SourceType sourceType;
	
	ObjectType targetType;
	
	ConverterMapping[] conversions;
	
	boolean optional;

	public SingleSource(TypeAdapters typeAdapters) {
		this.typeAdapters = typeAdapters;
	}
	
	@Override
	public Optional<Object> read(ProjectionStack queue, CompositionContext context) throws ProjectionError {
		
		if (!isReadable()) {
			return Optional.empty();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Read {}.{}, optional = {}, depth = {}", sourceType, getter.getName(), optional, queue.length());
		}

		final Optional<Object> instance = context.get(sourceType);

		if (instance.isEmpty()) {
			if (optional) {
				return Optional.empty();
			}
			throw new ProjectionError("Source instance of " + sourceType + ",  is not present.");
		}

		// get source value
		Optional<Object> object = getter.get(instance.get());

		if (object.isEmpty()) {
			return Optional.empty();
		}

		logger.trace("{}.{} = {}", sourceType, getter.getName(), object.get());
		
		
		// apply explicit conversions
		if (conversions != null) {
			try {
				for (ConverterMapping conversion : conversions) {
					if (object.isPresent()) {
						object = Optional.ofNullable(conversion.getConverter().forward(typeAdapters.convert(conversion.getSourceType(), object.get())));
					}
				}
			} catch (ConverterError e) {
				throw new ProjectionError(e);
			}

		}

		return object;
	}

	@Override
	public void write(ProjectionStack queue, ExtractionContext context, Object object) throws ProjectionError {
		
		if (!isWritable() || !context.isAccepted(sourceType)) {
			return;
		}
		
		logger.debug("Write {} to {}.{}, optional = {}, depth = {}", object, sourceType, setter.getName(), optional, queue.length());

		// apply explicit conversions in reverse order
		if (conversions != null) {
			try {
				for (int i=conversions.length - 1; i >= 0; i--) {
					object = conversions[i].getConverter().backward(object);
				}
			} catch (ConverterError e) {
				throw new ProjectionError(e);
			}
		}
		
		Optional<?> instance =  context.get(sourceType);

		if (instance.isEmpty()) {
			
			Optional<Class<?>> instanceClass = context.getAssignableType(sourceType);
			
			if (instanceClass.isEmpty()) {
				return;
			}
			
			instance = Optional.of(ObjectUtils.newInstance(instanceClass.get()));
			context.set(sourceType.getName(), instance.get());
		}

		setter.set(instance.get(), typeAdapters.convert(setter.getType().getType(), setter.getType().getComponentClass(), object));
	}
	
	public void setConversions(ConverterMapping[] conversions) {
		this.conversions = conversions;
	}
	
	public void setGetter(Getter getter) {
		this.getter = getter;
	}
	
	public void setSetter(Setter setter) {
		this.setter = setter;
	}
		
	public void setOptional(boolean optional) {
		this.optional = optional;
	}
	
	@Override
	public boolean isReadable() {
		return getter != null;
	}

	@Override
	public boolean isWritable() {
		return setter != null;
	}

	public void setTargetType(ObjectType targetType) {
		this.targetType = targetType;
	}
	
	public ObjectType getTargetType() {
		return targetType;
	}
	
	public ConverterMapping[] getConversions() {
		return conversions;
	}
	
	public void setSourceType(SourceType sourceType) {
		this.sourceType = sourceType;
	}

	@Override
	public boolean isAnyTypeOf(SourceType... sourceTypes) {
		for (SourceType type : sourceTypes) {
			if (type.isAssignableFrom(sourceType)) {
				return true;
			}
		}
		return false;
	}
}
