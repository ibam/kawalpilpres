package com.ibamo.kawalpemilu.service;

import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class PersistenceService {
	private static class SingletonHolder {
		private static final PersistenceService INSTANCE = new PersistenceService();
	}

	public static PersistenceService getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public void put(final String persistenceKey, final Object persistedValue) {
		putToMemcache(persistenceKey, persistedValue);
	}

	private void putToMemcache(final String persistenceKey,
			final Object persistedValue) {
		MemcacheServiceFactory.getAsyncMemcacheService().put(persistenceKey,
				persistedValue);
	}

	public String getString(final String persistenceKey) {
		final Object memcacheValue = MemcacheServiceFactory
				.getMemcacheService().get(persistenceKey);

		if (memcacheValue instanceof String) {
			return (String) memcacheValue;
		}

		return null;
	}
}
