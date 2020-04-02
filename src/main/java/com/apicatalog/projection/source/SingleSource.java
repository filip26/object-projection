package com.apicatalog.projection.source;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.mapping.ConversionMapping;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.ObjectUtils;
import com.apicatalog.projection.objects.ProjectionQueue;
import com.apicatalog.projection.objects.access.Getter;
import com.apicatalog.projection.objects.access.Setter;

public class SingleSource implements Source {

	final Logger logger = LoggerFactory.getLogger(SingleSource.class);

	Getter getter;
	Setter setter;
	
	Class<?> objectClass;
	
	Class<?> targetClass;
	Class<?> targetComponentClass;
	
	String qualifier;

	ConversionMapping[] conversions;
	
	boolean optional;

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
			for (ConversionMapping conversion : conversions) {
				object = conversion.forward(object);
			}
		}

		return object;
	}

	@Override
	public void write(ProjectionQueue queue, Object object, ContextObjects context) throws ProjectionError {
		
		if (!isWritable()) {
			return;
		}
		
		logger.debug("Write {}.{}, qualifier = {}, optional = {}, depth = {}", objectClass.getSimpleName(), setter.getName(), qualifier, optional, queue.length());

		// apply explicit conversions in reverse order
		if (conversions != null) {
			for (int i=conversions.length; i > 0; --i) {
				object = conversions[i].backward(object);
			}
		}
	
		Object instance = context.get(objectClass, qualifier);
		
		if (instance == null) {
			instance = ObjectUtils.newInstance(objectClass);
			context.addOrReplace(instance, qualifier);
		}
		
		setter.set(instance, object);		
	}
	
	public void setConversions(ConversionMapping[] conversions) {
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
}
