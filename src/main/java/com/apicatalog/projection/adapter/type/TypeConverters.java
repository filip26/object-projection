package com.apicatalog.projection.adapter.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.adapter.Conversion;
import com.apicatalog.projection.adapter.ImplicitConversion;
import com.apicatalog.projection.adapter.type.legacy.BooleanAdapter;
import com.apicatalog.projection.adapter.type.legacy.DoubleAdapter;
import com.apicatalog.projection.adapter.type.legacy.FloatAdapter;
import com.apicatalog.projection.adapter.type.legacy.InstantAdapter;
import com.apicatalog.projection.adapter.type.legacy.IntegerAdapter;
import com.apicatalog.projection.adapter.type.legacy.LongAdapter;
import com.apicatalog.projection.adapter.type.legacy.StringAdapter;
import com.apicatalog.projection.adapter.type.legacy.UriAdapter;
import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.property.source.ArraySource;

public class TypeConverters {
	
	final Logger logger = LoggerFactory.getLogger(TypeConverters.class);

	final Map<Class<?>, TypeAdapter<?>> adapters;
	
	public TypeConverters() {
		this.adapters = new LinkedHashMap<>();

		add(new StringAdapter())
			.add(new BooleanAdapter())
			.add(new LongAdapter())
			.add(new InstantAdapter())
			.add(new DoubleAdapter())
			.add(new IntegerAdapter())
			.add(new FloatAdapter())
			.add(new UriAdapter())
			.add(new Object2String())
			;
	}
	
	public TypeConverters add(TypeAdapter<?> adapter) {
		adapters.put(adapter.consumes(), adapter);
		return this;
	}
	
	public Optional<Conversion<Object, Object>> get(Collection<ObjectType> sources, ObjectType target)  {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Get converter from {} to {}", sources, target);
		}
		
		if (sources.isEmpty() || target == null) {
			throw new IllegalArgumentException();
		}

		if (sources.size() > 1 && !target.isArray()) {
			return Optional.empty();
		}
		
		if (sources.size() == 1 && !target.isArray()) {
			System.out.println("TODOOOOOOOOOO");	//FIXME
		}
		
		final Collection<Conversion<Object, Object>> conversions = new ArrayList<>();
		
		for (ObjectType source : sources) {
			Conversion<Object, Object> conversion = get(source.getType(), target.getType().getComponentType()).orElse(null);
			conversions.add(conversion);
		}
		
		
		final MixedArray2Array mixedArray = new MixedArray2Array();
		
		mixedArray.setConversions(conversions.toArray(new Conversion[0]));
		
		mixedArray.setTargetType(target.getType().getComponentType());
		
		return Optional.of((Conversion)mixedArray);
	}

	public Optional<Conversion<Object, Object>> get(Class<?> source, Class<?> target) {

		if (source == null || target == null) {
			throw new IllegalArgumentException();
		}
		
		if (target.isAssignableFrom(source)) {
			return Optional.empty();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Get converter from {} to {}", source.getCanonicalName(), target.getCanonicalName());
		}
		
		TypeAdapter<Object> typeAdapter = (TypeAdapter<Object>) adapters.get(source);

		if (typeAdapter == null) {
			return Optional.empty();
		}
		
		return Optional.of(ImplicitConversion.of(typeAdapter, (Class<Object>)target));
	}

	public Optional<Conversion<Object, Object>> get(ObjectType sourceType, ObjectType targetType) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Get converter from {} to {}", sourceType, targetType);
		}

		return Optional.empty();
	}

	public Optional<Conversion<Object, Object>> get(ObjectType source, Collection<ObjectType> targets) {
		if (logger.isDebugEnabled()) {
			logger.debug("Get converter from {} to {}", source, targets);
		}
		
		if (targets.isEmpty() || source == null) {
			throw new IllegalArgumentException();
		}

		if (targets.size() > 1 && !source.isArray()) {
			return Optional.empty();
		}
		
		if (targets.size() == 1 && !source.isArray()) {
			System.out.println("TODOOOOOOOOOO");	//FIXME
		}
		
		final Collection<Conversion<Object, Object>> conversions = new ArrayList<>();
		
		for (ObjectType target : targets) {
			Conversion<Object, Object> conversion = get(source.getType().getComponentType(), target.getType()).orElse(null);
			conversions.add(conversion);
		}
		
		
		final MixedArray2Array mixedArray = new MixedArray2Array();
		
		mixedArray.setConversions(conversions.toArray(new Conversion[0]));
		
		mixedArray.setTargetType(Object.class);
		
		return Optional.of((Conversion)mixedArray);

	}
}
