package com.apicatalog.projection.objects;

import java.util.ArrayDeque;
import java.util.Deque;

public final class ProjectionQueue {

	final Deque<ProjectionQueueKey> components;
	
	protected ProjectionQueue() {
		this.components = new ArrayDeque<>(10);
	}
	
	public static ProjectionQueue create() {

		return new ProjectionQueue();
	}
	
	public ProjectionQueue push(Object projection) {
		components.addFirst(ProjectionQueueKey.of(projection));
		return this;
	}
	
	public Object peek() {
		return components.peekFirst().getProjection();
	}
	
	public Object pop() {
		return components.removeFirst().getProjection();
	}
	
	public boolean contains(Class<?> projectionClass) {
		return components.contains(ProjectionQueueKey.of(projectionClass));
	}

	public int length() {
		return components.size();
	}
}
