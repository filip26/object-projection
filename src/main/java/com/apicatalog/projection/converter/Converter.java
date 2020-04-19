package com.apicatalog.projection.converter;

/**
 * Interface for invertible conversion from any Java object to any Java object.
 * 
 * @author filip
 *
 * @param <A> Java type 
 * @param <B> Java type
 */
public interface Converter<A, B> {

	void initConverter(ConverterConfig ctx) throws ConverterError;
	
	/**
	 * Convert an object of type <A> to object of type <B>
	 * 
	 * @param object of type <B>
	 * @return object of type <A>
	 * 
	 * @throws ConverterError if the object of type <A> cannot be converted to the type <B>
	 */
	B forward(A object) throws ConverterError;
	
	/**
	 * Convert an object of type <B> to object of type <A>
	 * 
	 * @param object of type <B>
	 * @return object of type <A>
	 * 
	 * @throws ConverterError if the object of type <B> cannot be converted to the type <A>
	 */	
	A backward(B object) throws ConverterError;
	
}
