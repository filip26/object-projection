package com.apicatalog.projection.api.object;

import com.apicatalog.projection.api.ConversionApi;
import com.apicatalog.projection.api.SourceApi;

public interface ObjectArraySourceApi<P> extends SourceApi<ObjectArraySourceItemApi<P>>, ConversionApi<ObjectArraySourceApi<P>> {

}
