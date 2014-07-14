package com.ibamo.kawalpemilu.service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import com.google.common.io.BaseEncoding;
import com.ibamo.kawalpemilu.model.kawalpemilu.BallotBoxResult;
import com.ibamo.kawalpemilu.model.kawalpemilu.BallotBoxUserInput;

public class SecurityService {

	private static class SingletonHolder {
		private static final SecurityService INSTANCE = new SecurityService();
	}

	private static Logger LOG = Logger.getLogger(SecurityService.class
			.getName());
	private static final int NONCE_BYTE_LENGTH = 32;
	private static final int USER_ID_BYTE_LENGTH = 32;

	private static final char NONCE_PERSISTENCE_KEY_SEPARATOR = '-';

	public static SecurityService getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private String composeKey(final String ballotId, final String userId) {
		return new StringBuilder(userId)
				.append(NONCE_PERSISTENCE_KEY_SEPARATOR).append(ballotId)
				.toString();
	}

	public String generateNonceForResult(final BallotBoxResult result,
			final String userId) {
		final String nonceString = generateRandomToken(NONCE_BYTE_LENGTH);
		final String resultPersistenceKey = composeKey(result.getId(), userId);
		PersistenceService.getInstance().put(resultPersistenceKey, nonceString);

		return nonceString;
	}

	private String generateRandomToken(final int tokenLength) {
		final byte[] nonceBytes = new byte[tokenLength];
		ThreadLocalRandom.current().nextBytes(nonceBytes);

		return BaseEncoding.base64Url().encode(nonceBytes);
	}

	public String generateUserId() {
		return generateRandomToken(USER_ID_BYTE_LENGTH);
	}

	public boolean validateAdviceFromUser(final BallotBoxUserInput advice,
			final String userId) {

		final String persistenceKey = composeKey(advice.getId(), userId);
		final String storedNonce = PersistenceService.getInstance().getString(
				persistenceKey);

		if (storedNonce == null) {
			LOG.warning("Cannot find nonce for user " + userId
					+ " and ballot id " + advice.getId() + ".");
			return false;
		}

		return storedNonce.equals(advice.getNonce());
	}
}
