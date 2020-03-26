package com.apicatalog.uritemplate;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * 'Level 1' URI Template
 * 
 * see https://tools.ietf.org/html/rfc6570
 * 
 * @author filip
 *
 */
public class UriTemplateL1 {
	
	protected static final char END_OF_INPUT = '\n';
	
	final String[] elements;
	final int[] variables;
	final char[] stopCharacters;
	
	protected UriTemplateL1(String[] elements, int[] variables, char[] stopCharacters) {
		this.elements = elements;
		this.variables = variables;
		this.stopCharacters = stopCharacters;
	}

	public static UriTemplateL1 of(final String urlPattern) throws MalformedUriTemplate {
		return UriTemplateL1Parser.of(urlPattern);
	}
	
	public Collection<String> variables() {
		return Arrays
				.stream(variables)
				.mapToObj(i -> elements[i])
				.collect(Collectors.toList());
	}
	
	public String expand(String...values) {
		
		if ((values == null && variables.length > 0)
			|| (values != null && values.length != variables.length)){
			throw new IllegalArgumentException();
		}
		
		final StringBuilder builder = new StringBuilder();
		
		int vi = variables.length > 0 ? 0 : -1;
		
		for (int ei = 0; ei < elements.length; ei++) {
			
			// variable?
			if (vi != -1 && values != null && variables[vi] == ei) {
				builder.append(values[vi]);
				vi = (vi + 1 < variables.length ? vi + 1 : -1);
				continue;
			}
			
			builder.append(elements[ei]);
		}
		
		return builder.toString();
	}
	
	public String[] extract(final String url) {
		
		if (StringUtils.isBlank(url)) {
			throw new IllegalArgumentException();
		}
		
		if (variables.length == 0) {
			return new String[0];
		}
		
		char[] input = url.toCharArray();
		int inputIndex = 0;
	
		final String[] vars = new String[variables.length];
		int variableIndex = 0;

		for (int elementIndex = 0; elementIndex < elements.length; elementIndex++) {

			// processing variable
			if (variableIndex < variables.length && elementIndex == variables[variableIndex]) {
			
				// extract a variable from the remaining input
				if (stopCharacters[variableIndex] == END_OF_INPUT) {
					
					int length = input.length - inputIndex;
					
					if ((variables[variableIndex] + 1) < elements.length) {
						

						String constant = elements[variables[variableIndex] + 1];
						
						length -= constant.length();
					}
					if (length < 0) {
						return new String[0];
					}

					vars[variableIndex++] = String.copyValueOf(input, inputIndex,  length);
					inputIndex += length;
					
				} else {
					// looking for stop character
					for (int i=inputIndex; i < input.length; i++) {
						if (stopCharacters[variableIndex] == input[i]) {

							String constant = elements[variables[variableIndex] + 1]; 
						
							vars[variableIndex++] = String.copyValueOf(input, inputIndex, 1 + i - inputIndex - constant.length());
							inputIndex = 1 + i - constant.length();
							break;
						}
					}
				}
				

			} else {
				// processing constant
				final String constant = elements[elementIndex];
				
				if (inputIndex + constant.length() > input.length) {
					return new String[0];
				}

				// does it match to expected constant
				if (!Arrays.equals(input, inputIndex, inputIndex + constant.length(), constant.toCharArray(), 0, constant.length())) {
					return new String[0];
				}
				inputIndex += constant.length();								
			}
			
		}		
		return vars;
	}
	
}
