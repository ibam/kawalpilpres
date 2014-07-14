package com.ibamo.kawalpemilu.framework.exceptions;

@SuppressWarnings("serial")
public class RemoteServiceException extends GenericServiceException {
	public RemoteServiceException(final Throwable cause) {
		super("Remote service threw an exception.", cause);
	}
}
