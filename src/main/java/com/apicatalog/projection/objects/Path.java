package com.apicatalog.projection.objects;

import java.util.ArrayDeque;
import java.util.Deque;

public class Path {

	final Deque<Class<?>> components;
	
	protected Path() {
		this.components = new ArrayDeque<>(10);
	}
	
	public static Path create() {

		return new Path();
	}
	
	public Path push(Class<?> projectionClass) {
		components.addFirst(projectionClass);
		return this;
	}
	
	public Class<?> pop() {
		return components.removeFirst();
	}
	
	public boolean contains(Class<?> projectionClass) {
		return components.contains(projectionClass);
	}

	public int length() {
		return components.size();
	}
	
	
}
