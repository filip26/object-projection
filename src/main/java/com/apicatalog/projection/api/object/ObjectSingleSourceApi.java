package com.apicatalog.projection.api.object;

import com.apicatalog.projection.api.AccessModeApi;
import com.apicatalog.projection.api.ConversionApi;
import com.apicatalog.projection.api.OptionalApi;

public interface ObjectSingleSourceApi<P> extends 
											ObjectProjectionApi<P>, 
											ConversionApi<ObjectSingleSourceApi<P>>, 
											OptionalApi<ObjectSingleSourceApi<P>>,
											AccessModeApi<ObjectSingleSourceApi<P>>
											{
}
