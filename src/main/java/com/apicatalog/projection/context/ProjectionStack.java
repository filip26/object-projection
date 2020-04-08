package com.apicatalog.projection.context;

import java.util.ArrayDeque;
import java.util.Deque;

public final class ProjectionStack {

	final Deque<ProjectionStackKey> stack;
	
	protected ProjectionStack() {
		this.stack = new ArrayDeque<>(10);
	}
	
	public static ProjectionStack create() {
		return new ProjectionStack();
	}
	
	public ProjectionStack push(Object projection) {
		stack.addFirst(ProjectionStackKey.of(projection));
		return this;
	}
	
	public Object peek() {
		return stack.peekFirst().getProjection();
	}
	
	public Object pop() {
		return stack.removeFirst().getProjection();
	}
	
	public boolean contains(Class<?> projectionClass) {
		return stack.contains(ProjectionStackKey.of(projectionClass));
	}

	public int length() {
		return stack.size();
	}
}
