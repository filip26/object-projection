package com.apicatalog.projection.api.map;

import com.apicatalog.projection.api.AccessModeApi;
import com.apicatalog.projection.api.ConversionApi;
import com.apicatalog.projection.api.OptionalApi;

public interface MapSingleSourceApi extends 			
										MapProjectionApi, 
										ConversionApi<MapSingleSourceApi>, 
										OptionalApi<MapSingleSourceApi>,
										AccessModeApi<MapSingleSourceApi>
										{

}
