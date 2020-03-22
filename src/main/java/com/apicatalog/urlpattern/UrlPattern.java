package com.apicatalog.urlpattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class UrlPattern {
	
	final String[] elements;
	final int[] variables;
	
	protected UrlPattern(String[] elements, int[] variables) {
		this.elements = elements;
		this.variables = variables;
	}

	public static UrlPattern valueOf(final String urlPattern) throws MalformedUrlPattern {
	
		final ArrayList<String> elements = new ArrayList<>();
		final ArrayList<Integer> variables = new ArrayList<>();

		// find first variable
		int startIndex = 0;
		int varBeginIndex = urlPattern.indexOf('{', startIndex);
		
		while (varBeginIndex != -1) {
			
			// find end index
			int varEndIndex = urlPattern.indexOf('}', varBeginIndex);
			if (varEndIndex == -1) {
				throw new MalformedUrlPattern("Unexpected end, expected '}' pattern=" + urlPattern);
			}
			
			// add constant
			if (startIndex < varBeginIndex) {
				elements.add(urlPattern.substring(startIndex, varBeginIndex));
			}
			
			// add variable index
			variables.add(elements.size());
			
			// add variable
			elements.add(urlPattern.substring(varBeginIndex + 1, varEndIndex));
			
			// find next variable
			startIndex = varEndIndex + 1;
			varBeginIndex = urlPattern.indexOf('{', startIndex);
			
		}

		// add remaining input as constant
		if (startIndex < urlPattern.length()) {
			elements.add(urlPattern.substring(startIndex));
		}
		
		return new UrlPattern(elements.toArray(new String[0]), variables.stream().mapToInt(i -> i).toArray());
	}
	
	public Collection<String> variables() {
		return Arrays
				.stream(variables)
				.mapToObj(i -> elements[i])
				.collect(Collectors.toList());
	}
	
	public String populate(String...values) {
		
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
	
}
