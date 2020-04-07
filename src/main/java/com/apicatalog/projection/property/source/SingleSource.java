package com.apicatalog.projection.property.source;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.objects.ObjectUtils;
import com.apicatalog.projection.objects.ProjectionQueue;
import com.apicatalog.projection.objects.getter.Getter;
import com.apicatalog.projection.objects.setter.Setter;

public final class SingleSource implements Source {

	final Logger logger = LoggerFactory.getLogger(SingleSource.class);

	final TypeAdapters typeAdapters;	//FIXME use concrete adapters set during mapping
	
	Getter getter;
	Setter setter;
	
	Class<?> objectClass;
	
	ObjectType targetType;
	
	String qualifier;

	ConverterMapping[] conversions;
	
	boolean optional;

	public SingleSource(TypeAdapters typeAdapters) {
		this.typeAdapters = typeAdapters;
	}
	
	@Override
	public Object read(ProjectionQueue queue, CompositionContext context) throws ProjectionError {
		
		if (!isReadable()) {
			return null;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Read {}.{}, qualifier = {}, optional = {}, depth = {}", objectClass.getSimpleName(), getter.getName(), Optional.ofNullable(qualifier).orElse("n/a"), optional, queue.length());
		}

		final Optional<Object> instance = Optional.ofNullable(context.get(objectClass, qualifier));

		if (instance.isEmpty()) {
			if (optional) {
				return null;
			}
			throw new ProjectionError("Source instance of " + objectClass.getCanonicalName() + ", qualifier=" + qualifier + ",  is not present.");
		}

		// get source value
		Optional<Object> object = getter.get(instance.get());

		if (object.isEmpty()) {
			return null;
		}

		logger.trace("{}.{} = {}", objectClass.getSimpleName(), getter.getName(), object.get());
		
		
		// apply explicit conversions
		if (conversions != null) {
			try {
				for (ConverterMapping conversion : conversions) {
					object = Optional.ofNullable(conversion.getConverter().forward(typeAdapters.convert(conversion.getSourceType(), object.get())));
				}
			} catch (ConverterError e) {
				throw new ProjectionError(e);
			}

		}

		return object.orElse(null);
	}

	@Override
	public void write(ProjectionQueue queue, Object object, ExtractionContext context) throws ProjectionError {
		
		if (!isWritable() || !context.isAccepted(qualifier, objectClass, null)) {
			return;
		}
		
		logger.debug("Write {} to {}.{}, qualifier = {}, optional = {}, depth = {}", object, objectClass.getSimpleName(), setter.getName(), qualifier, optional, queue.length());

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
		
		Optional<?> instance =  Optional.ofNullable(context.get(qualifier, objectClass, null));

		if (instance.isEmpty()) {
			instance = Optional.of(ObjectUtils.newInstance(context.getAssignableType(qualifier, objectClass, null)));
			context.set(qualifier, instance.get());
		}
		setter.set(instance.get(), typeAdapters.convert(setter.getType().getObjectClass(), setter.getType().getObjectComponentClass(), object));
	}
	
	public void setConversions(ConverterMapping[] conversions) {
		this.conversions = conversions;
	}
	
	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}
	
	public void setGetter(Getter getter) {
		this.getter = getter;
	}
	
	public void setSetter(Setter setter) {
		this.setter = setter;
	}
	
	public void setObjectClass(Class<?> bbjectClass) {
		this.objectClass = bbjectClass;
	}

	public Class<?> getObjectClass() {
		return objectClass;
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

	public Getter getGetter() {
		return getter;
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
}
