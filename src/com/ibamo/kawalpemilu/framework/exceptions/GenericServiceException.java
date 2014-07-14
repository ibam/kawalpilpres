package com.ibamo.kawalpemilu.framework.exceptions;

@SuppressWarnings("serial")
public class GenericServiceException extends RuntimeException {
	public GenericServiceException(final String reason) {
		super(reason);
	}
	
	public GenericServiceException(final String reason, final Throwable cause) {
		super(reason, cause);
	}
}
