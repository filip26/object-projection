package com.apicatalog.projection.context;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

final class ContextNamespace {

	public static final int MAX_DEPTH = 10;
	
	final String[] names;
	
	int index;
	
	public ContextNamespace() {
		this.names = new String[MAX_DEPTH];
		this.index = 0;
	}
	
	public ContextNamespace(ContextNamespace ref) {
		this.names = new String[MAX_DEPTH];
		this.index = ref.index;
		
		System.arraycopy(ref.names, 0, names, 0, ref.index);
	}

	public void push(String name) {
		if (index >= names.length) {
			throw new IllegalStateException();
		}
		names[index++] = name;
	}
	
	public String pop() {
		return names[--index];
	}
	
	public String last() {
		return names[index];
	}

	public String getQName(String name) {
		if (index == 0) {
			return name;
		}
		return IntStream.range(0, index).mapToObj(i -> names[i]).collect(Collectors.joining(".")).concat(".").concat(name);
	}
	
}
