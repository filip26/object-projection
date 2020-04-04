package com.apicatalog.uritemplate;

import java.util.ArrayList;

/**
 * 'Level 1' URI Template parser
 *
 * @author filip
 *
 */
public class UriTemplateL1Parser {
	
	enum State { VAR_BEGIN, VAR_END, STOP_CHAR }
	
	final ArrayList<String> elements;
	final ArrayList<Integer> variables;
	final ArrayList<Integer> stops;

	char[] input;
	
	int inputIndex;
	int tmpIndex;

	State state;

	UriTemplateL1Parser() {
		this.elements = new ArrayList<>(10);
		this.variables = new ArrayList<>(10);
		this.stops = new ArrayList<>(10);
	}
	
	public static final UriTemplateL1Parser newInstance() {
		return new UriTemplateL1Parser();
	}

	public static final UriTemplateL1 of(final String uriTemplate) throws MalformedUriTemplate {
		return newInstance().parse(uriTemplate);
	}
	
	public final UriTemplateL1 parse(final String uriTemplate) throws MalformedUriTemplate {

		if (uriTemplate == null) {
			throw new IllegalArgumentException("Invalid null as uriTemplate parameter.");
		}
		
		elements.clear();
		variables.clear();
		stops.clear();
		
		state = State.VAR_BEGIN;
		input = uriTemplate.toCharArray();
		inputIndex = 0;
				 
		stripspaces();

		tmpIndex = inputIndex;
		
		// process URI template
		for (; inputIndex < input.length; inputIndex++) {
			
			final char ch = input[inputIndex];
			
			switch (state) {
			case VAR_BEGIN:
				// found first variable
				if (ch == '{') {
					beginVar();
				}
				break;
				
			case VAR_END:
				if (ch == '}') {
					endVar();
				}
				break;
				
			case STOP_CHAR:
				if (ch == '/' || ch == '&' || ch == '?' || ch == '=' || ch == '#') {
					stopChar();
				}
				break;
			}			
		}

		complete(uriTemplate);
		
		// return URI template
		return new UriTemplateL1(
						elements.toArray(new String[0]),
						variables.stream().mapToInt(v -> v).toArray(),
						getStopchars()
				);
	}
	
	void beginVar() {
		if (tmpIndex < inputIndex) {
			elements.add(String.copyValueOf(input, tmpIndex, inputIndex - tmpIndex));
		}
		state = State.VAR_END;
		tmpIndex = inputIndex + 1;

	}
	
	void endVar() {
		variables.add(elements.size());
		elements.add(String.copyValueOf(input, tmpIndex, inputIndex - tmpIndex));
		state = State.STOP_CHAR;
		tmpIndex = inputIndex + 1;
	}
	
	void stopChar() {
		stops.add(inputIndex);
		elements.add(String.copyValueOf(input, tmpIndex, inputIndex - tmpIndex + 1));
		state = State.VAR_BEGIN;
		tmpIndex = inputIndex + 1;
	}
	
	void complete(final String uriTemplate) throws MalformedUriTemplate {
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
	}

	// extract stop chars
	char[] getStopchars() {
		final char[] stopChars = new char[variables.size()];
	
		inputIndex = 0;
		for (Integer stopIndex : stops) {
			stopChars[inputIndex++] = input[stopIndex];
		}
		if (stops.size() < variables.size()) {
			stopChars[inputIndex] = UriTemplateL1.END_OF_INPUT;
		}
		
		return stopChars;
	}

	// strip leading spaces
	void stripspaces() {
		for (; inputIndex < input.length; inputIndex++) {
			if (!Character.isWhitespace(input[inputIndex])) {
				break;
			}
		}
	}
	
}
