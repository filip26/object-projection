package com.apicatalog.projection.conversion;

import java.net.URI;
import java.time.Instant;
import java.util.Date;

public final class SimpleTypeConversions {

	protected SimpleTypeConversions() {}
	
	public static final Conversion<Object, Object> get(final Class<?> source, final Class<?> target) {
		
		if (source == String.class) return fromString(target);
		if (source == Integer.class) return fromInteger(target);
		if (source == Long.class) return fromLong(target);
		if (source == Float.class) return fromFloat(target);
		if (source == Double.class) return fromDouble(target);
		if (source == Boolean.class) return fromBoolean(target);
		if (source == Instant.class) return fromInstant(target);
		if (source == Date.class) return fromDate(target);
				
		return fromObject(target);
	}
	
	static final Conversion<Object, Object> fromString(final Class<?> target) {

		if (target == Byte.class) return o -> Byte.valueOf((String)o);
		if (target == Short.class) return o -> Short.valueOf((String)o);
		if (target == Long.class) return o -> Long.valueOf((String)o);
		if (target == Integer.class) return o -> Integer.valueOf((String)o);
		if (target == Float.class) return o -> Float.valueOf((String)o);
		if (target == Double.class) return o -> Double.valueOf((String)o);
		if (target == Boolean.class) return stringToBoolean();
		if (target == URI.class) return o -> URI.create((String)o);
		
		return fromObject(target);
	}

	private static Conversion<Object, Object> stringToBoolean() {
		return o -> (
					"true".equalsIgnoreCase((String)o)					 
					|| "yes".equalsIgnoreCase((String)o)
					|| "T".equalsIgnoreCase((String)o)
					|| "Y".equalsIgnoreCase((String)o)
					|| "1".equalsIgnoreCase((String)o)
					);		
	}

	static final Conversion<Object, Object> fromInteger(final Class<?> target) {
		 
		if (target == Byte.class) return o -> ((Integer)o).byteValue();
		if (target == Short.class) return o -> ((Integer)o).shortValue();
		if (target == Long.class) return o -> ((Integer)o).longValue();
		if (target == Float.class) return o -> ((Integer)o).floatValue();
		if (target == Double.class) return o -> ((Integer)o).doubleValue();
		if (target == Boolean.class) return o -> ((Integer)o) == 0 ? Boolean.FALSE : Boolean.TRUE;

		return fromObject(target);
	}

	static final Conversion<Object, Object> fromLong(final Class<?> target) {
 
		if (target == Byte.class) return o -> ((Long)o).byteValue();
		if (target == Short.class) return o -> ((Long)o).shortValue();
		if (target == Integer.class) return o -> ((Long)o).intValue();
		if (target == Float.class) return o -> ((Long)o).floatValue();
		if (target == Double.class) return o -> ((Long)o).doubleValue();
		if (target == Boolean.class) return o -> ((Long)o) == 0 ? Boolean.FALSE : Boolean.TRUE;
		if (target == Instant.class) return o -> Instant.ofEpochMilli((Long)o);

		return fromObject(target);
	}

	static final Conversion<Object, Object> fromFloat(final Class<?> target) {
		 
		if (target == Byte.class) return o -> ((Float)o).byteValue();
		if (target == Short.class) return o -> ((Float)o).shortValue();
		if (target == Integer.class) return o -> ((Float)o).intValue();
		if (target == Long.class) return o -> ((Float)o).longValue();
		if (target == Double.class) return o -> ((Float)o).doubleValue();
		if (target == Boolean.class) return o -> ((Float)o) == 0 ? Boolean.FALSE : Boolean.TRUE;
		
		return fromObject(target);
	}

	static final Conversion<Object, Object> fromDouble(final Class<?> target) {
		 
		if (target == Byte.class) return o -> ((Double)o).byteValue();
		if (target == Short.class) return o -> ((Double)o).shortValue();
		if (target == Integer.class) return o -> ((Double)o).intValue();
		if (target == Long.class) return o -> ((Double)o).longValue();
		if (target == Float.class) return o -> ((Double)o).floatValue();
		
		return fromObject(target);
	}

	static final Conversion<Object, Object> fromInstant(final Class<?> target) {
  
		if (target == Long.class) return o -> ((Instant)o).toEpochMilli();
		if (target == Date.class) return o -> Date.from((Instant)o);

		return fromObject(target);
	}

	static final Conversion<Object, Object> fromDate(final Class<?> target) {
		  
		if (target == Instant.class) return o -> ((Date)o).toInstant();

		return fromObject(target);
	}

	static final Conversion<Object, Object> fromBoolean(final Class<?> target) {
		 
		if (target == Byte.class) return o -> Boolean.TRUE.equals(o) ? 1 : 0;
		if (target == Short.class) return o -> Boolean.TRUE.equals(o) ? 1 : 0;
		if (target == Integer.class) return o -> Boolean.TRUE.equals(o) ? 1 : 0;
		if (target == Long.class) return o -> Boolean.TRUE.equals(o) ? 1l : 0l;
		if (target == Float.class) return o -> Boolean.TRUE.equals(o) ? 1f : 0f;
		if (target == Double.class) return o -> Boolean.TRUE.equals(o) ? 1d : 0d;
		
		return fromObject(target);
	}
	
	static final Conversion<Object, Object> fromObject(final Class<?> target) {
		if (target == String.class) return Object::toString;

		return null;
	}
}
