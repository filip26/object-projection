package com.apicatalog.projection.objects;

import java.net.URI;

public class Env {

	final URI baseUri;
	
	public Env(final URI basePath) {
		this.baseUri = basePath;
	}
	
	public URI getBaseUri() {
		return baseUri;
	}
	
}
