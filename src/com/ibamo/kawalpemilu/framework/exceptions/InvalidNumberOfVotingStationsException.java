package com.ibamo.kawalpemilu.framework.exceptions;

@SuppressWarnings("serial")
public class InvalidNumberOfVotingStationsException extends
		GenericServiceException {

	public InvalidNumberOfVotingStationsException(final String regionId) {
		super("Cannot fetch number of voting stations for region with ID " + regionId);
	}

}
