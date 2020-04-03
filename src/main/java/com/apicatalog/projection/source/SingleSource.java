package com.apicatalog.projection.source;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.beans.Getter;
import com.apicatalog.projection.beans.Setter;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.mapping.ConverterMapping;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.ObjectUtils;
import com.apicatalog.projection.objects.ProjectionQueue;

public class SingleSource implements Source {

	final Logger logger = LoggerFactory.getLogger(SingleSource.class);

	final TypeAdapters typeAdapters;	//FIXME use concrete adapters set during mapping
	
	Getter getter;
	Setter setter;
	
	Class<?> objectClass;
	
	Class<?> targetClass;
	Class<?> targetComponentClass;
	
	String qualifier;

	ConverterMapping[] conversions;
	
	boolean optional;

	public SingleSource(TypeAdapters typeAdapters) {
		this.typeAdapters = typeAdapters;
	}
	
	@Override
	public Object read(ProjectionQueue queue, ContextObjects context) throws ProjectionError {
		
		if (!isReadable()) {
			return null;
		}
		
		logger.debug("Read {}.{}, qualifier = {}, optional = {}, depth = {}", objectClass.getSimpleName(), getter.getName(), qualifier, optional, queue.length());

		final Optional<Object> instance = Optional.ofNullable(context.get(objectClass, qualifier));

		if (instance.isEmpty()) {
			if (optional) {
				return null;
			}
			throw new ProjectionError("Source instance of " + objectClass.getCanonicalName() + ", qualifier=" + qualifier + ",  is not present.");
		}

		// get source value
		Object object = getter.get(instance.get());

		logger.trace("{}.{} = {}", objectClass.getSimpleName(), getter.getName(), object);
		
		if (object == null) {
			return null;
		}
		
		// apply explicit conversions
		if (conversions != null) {
			try {
				for (ConverterMapping conversion : conversions) {
					object = conversion.getConverter().forward(object);
				}
			} catch (ConverterError e) {
				throw new ProjectionError(e);
			}

		}

		return object;
	}

	@Override
	public void write(ProjectionQueue queue, Object object, ContextObjects context) throws ProjectionError {
		
		if (!isWritable()) {
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
	
		Object instance = context.get(objectClass, qualifier);
		
		if (instance == null) {
			instance = ObjectUtils.newInstance(objectClass);
			context.addOrReplace(instance, qualifier);
		}
		
		setter.set(instance, typeAdapters.convert(setter.getType().getObjectClass(), setter.getType().getObjectComponentClass(), object));		
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

	public Class<?> getTargetClass() {
		return targetClass;
	}
	
	public Class<?> getTargetComponentClass() {
		return targetComponentClass;
	}
	
	public void setTargetClass(Class<?> targetClass) {
		this.targetClass = targetClass;
	}
	
	public void setTargetComponentClass(Class<?> targetComponentClass) {
		this.targetComponentClass = targetComponentClass;
	}
	
	public ConverterMapping[] getConversions() {
		return conversions;
	}
}
