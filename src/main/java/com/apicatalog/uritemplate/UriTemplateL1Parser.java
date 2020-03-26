package com.apicatalog.uritemplate;

import java.util.ArrayList;

/**
 * 'Level 1' URI Template parser
 *
 * @author filip
 *
 */
public class UriTemplateL1Parser {
	
	enum State { VAR_BEGIN, VAR_END, STOP_CHAR };
	
	protected UriTemplateL1Parser() {
	}

	public static UriTemplateL1 of(final String uriTemplate) throws MalformedUriTemplate {

		if (uriTemplate == null) {
			throw new IllegalArgumentException("Invalid null as uriTemplate parameter.");
		}
		
		final ArrayList<String> elements = new ArrayList<>();
		final ArrayList<Integer> variables = new ArrayList<>();
		final ArrayList<Integer> stops = new ArrayList<>();

		final char[] input = uriTemplate.toCharArray();
		int inputIndex = 0;

		State state = State.VAR_BEGIN;
		
		// strip leading spaces		
		for (; inputIndex < input.length; inputIndex++) {
			if (!Character.isWhitespace(input[inputIndex])) {
				break;
			}
		}

		int tmpIndex = inputIndex;
		
		// process URI template
		for (; inputIndex < input.length; inputIndex++) {
			
			final char ch = input[inputIndex];
			
			switch (state) {
			case VAR_BEGIN:
				// found first variable
				if (ch == '{') {
					if (tmpIndex < inputIndex) {
						elements.add(String.copyValueOf(input, tmpIndex, inputIndex - tmpIndex));
					}
					state = State.VAR_END;
					tmpIndex = inputIndex + 1;
				}
				break;
				
			case VAR_END:
				if (ch == '}') {
					variables.add(elements.size());
					elements.add(String.copyValueOf(input, tmpIndex, inputIndex - tmpIndex));
					state = State.STOP_CHAR;
					tmpIndex = inputIndex + 1;
				}
				break;
				
			case STOP_CHAR:
				if (ch == '/' || ch == '&' || ch == '?' || ch == '=' || ch == '#') {
					stops.add(inputIndex);
					elements.add(String.copyValueOf(input, tmpIndex, inputIndex - tmpIndex + 1));
					state = State.VAR_BEGIN;
					tmpIndex = inputIndex + 1;
				}
				break;
			}			
		}
		
		switch (state) {
		case STOP_CHAR:			
		case VAR_BEGIN:
			if (tmpIndex != -1 && tmpIndex < input.length) {

				int endIndex = input.length;
				
				// strip trailing spaces
				for (; endIndex > tmpIndex; endIndex--) {
					if (!Character.isWhitespace(input[endIndex-1])) {
						break;
					}
				}
				// add remaining input
				if (tmpIndex < endIndex) {
					elements.add(String.copyValueOf(input, tmpIndex, endIndex - tmpIndex));
				}
			}
			break;
			
		case VAR_END:
			throw new MalformedUriTemplate("Unexpected end of input, expected '}' template=" + uriTemplate);
		}
		
		if (elements.isEmpty()) {
			throw new MalformedUriTemplate("Invalid URI template=" + uriTemplate + ". Only 'Level 1' templates are supported.");
		}		
		
		// extract stop chars
		final char[] stopChars = new char[variables.size()];

		inputIndex = 0;
		for (Integer stopIndex : stops) {
			stopChars[inputIndex++] = input[stopIndex];
		}
		if (stops.size() < variables.size()) {
			stopChars[inputIndex] = UriTemplateL1.END_OF_INPUT;
		}
		
		// return URI template
		return new UriTemplateL1(
						elements.toArray(new String[0]),
						variables.stream().mapToInt(v -> v).toArray(),
						stopChars
				);
	}
}
