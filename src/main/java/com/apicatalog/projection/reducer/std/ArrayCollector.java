package com.apicatalog.projection.reducer.std;

import com.apicatalog.projection.reducer.Reducer;
import com.apicatalog.projection.reducer.ReducerConfig;

public class ArrayCollector implements Reducer<Object, Object[]> {

	@Override
	public void initReducer(ReducerConfig ctx) {
		// nothing to initialize
	}

	@Override
	public Object[] reduce(Object[] objects) {
		return objects;
	}

	@Override
	public Object[] expand(Object[] object) {
		return object;
	}

	
	
}
