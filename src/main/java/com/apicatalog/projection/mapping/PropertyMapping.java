package com.apicatalog.projection.mapping;

import com.apicatalog.projection.annotation.IFunction;

public class PropertyMapping {

	final String name;

	SourceMapping[] sources;
	
	TargetMapping target;
	
	IFunction[] functions;
	
	public PropertyMapping(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public SourceMapping[] getSources() {
		return sources;
	}

	public void setSources(SourceMapping[] mapping) {
		this.sources = mapping;
	}

	public IFunction[] getFunctions() {
		return functions;
	}

	public void setFunctions(IFunction[] functions) {
		this.functions = functions;
	}

	public TargetMapping getTarget() {
		return target;
	}

	public void setTarget(TargetMapping target) {
		this.target = target;
	}
}
