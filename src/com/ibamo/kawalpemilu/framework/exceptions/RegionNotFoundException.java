package com.ibamo.kawalpemilu.framework.exceptions;

@SuppressWarnings("serial")
public class RegionNotFoundException extends GenericServiceException {
	public RegionNotFoundException(final String regionId) {
		this("Region with ID " + regionId + " cannot be found.", null);
	}
	
	public RegionNotFoundException(final String regionId, final Throwable cause) {
		super("Region with ID " + regionId + " cannot be found.", cause);
	}
}
