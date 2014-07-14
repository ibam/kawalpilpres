package com.ibamo.kawalpemilu.framework.exceptions;


@SuppressWarnings("serial")
public class ResponseClassMismatchException extends GenericServiceException {
	public ResponseClassMismatchException(final Throwable cause,
			final String classDefinition) {
		super("Cannot parse response as class " + classDefinition + ".", cause);
	}
}
