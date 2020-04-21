package com.apicatalog.projection.api.map;

import java.util.Map;

import com.apicatalog.projection.api.AccessModeApi;
import com.apicatalog.projection.api.BuilderApi;
import com.apicatalog.projection.api.ConversionApi;
import com.apicatalog.projection.api.OptionalApi;

public interface MapSingleSourceApi extends 			
										MapProjectionApi, 
										BuilderApi<Map<String, Object>>,
										ConversionApi<MapSingleSourceApi>, 
										OptionalApi<MapSingleSourceApi>,
										AccessModeApi<MapSingleSourceApi>
										{

}
