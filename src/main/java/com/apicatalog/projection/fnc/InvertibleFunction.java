package com.apicatalog.projection.fnc;

import com.apicatalog.projection.Value;

public interface InvertibleFunction {

	Object compute(ContextValue ctx, Value...values) throws InvertibleFunctionError;
	
	Object[] inverse(ContextValue ctx, Value value) throws InvertibleFunctionError;
	
}
