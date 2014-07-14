package com.ibamo.kawalpemilu.framework.exceptions;

@SuppressWarnings("serial")
public class InvalidServerConfiguration extends GenericServiceException {
	public enum ConfigType {
		GET_REGION_URL,
		GET_VOTING_NUMBER_URL,
		SECURITY_PROPERTIES_FILE_NAME,
		BALLOT_ID_KEY_ENCRYPTION,
		BALLOT_ID_KEY_DECRYPTION;
	}

	public InvalidServerConfiguration(final ConfigType invalidConfig) {
		this(invalidConfig, null);
	}

	public InvalidServerConfiguration(final ConfigType invalidConfig,
			final Throwable cause) {
		super("Invalid configuration: " + invalidConfig.name(), cause);
	}
}
