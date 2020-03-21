package com.apicatalog.projection.fnc;

import com.apicatalog.projection.Value;

public interface InvertibleFunction {

	void init(ContextValue ctx);
	
	Object compute(Value...values) throws InvertibleFunctionError;
	
	Object[] inverse(Value value) throws InvertibleFunctionError;
	
}
