package com.apicatalog.projection.fnc;

import com.apicatalog.projection.Value;

public interface InvertibleFunction {

	Object compute(Value...values);
	
	Object[] inverse(Value value);
	
}
