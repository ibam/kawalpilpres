package com.ibamo.kawalpemilu.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.ibamo.kawalpemilu.framework.exceptions.RemoteServiceException;
import com.ibamo.kawalpemilu.framework.exceptions.ResponseClassMismatchException;

public class NetworkService {

	private static class SingletonHolder {
		private static final NetworkService INSTANCE = new NetworkService();
	}

	// private static Logger LOG = Logger.getLogger(NetworkService.class
	// .getSimpleName());

	public static NetworkService getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private ObjectMapper networkObjectMapper = null;

	private NetworkService() {
		networkObjectMapper = buildObjectMapper();
	}

	private ObjectMapper getNetworkObjectMapper() {
		return networkObjectMapper;
	}

	private ObjectMapper buildObjectMapper() {
		ObjectMapper constructedMapper = new ObjectMapper();
		constructedMapper.configure(
				DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		return constructedMapper;
	}

	@SuppressWarnings("unchecked")
	public <T> T fetchUrlFor(final URL url, Class<T> beanClass) {
		return (T) fetchUrlForPOJOBean(url, beanClass);
		// try {
		// return getNetworkObjectMapper().readValue(fetchUrl.openStream(),
		// beanClass);
		// } catch (JsonParseException | JsonMappingException ex) {
		// throw new ResponseClassMismatchException(ex, beanClass);
		// } catch (IOException ex) {
		// throw new RemoteServiceException(ex);
		// }
	}

	private Object fetchUrlForPOJOBean(final URL url,
			final Object beanDefinition) {

		InputStream urlStream = null;

		try {
			urlStream = url.openStream();

			if (beanDefinition instanceof Class<?>) {
				return getNetworkObjectMapper().readValue(urlStream,
						(Class<?>) beanDefinition);
			} else if (beanDefinition instanceof TypeReference<?>) {
				return getNetworkObjectMapper().readValue(urlStream,
						(TypeReference<?>) beanDefinition);
			} else {
				throw new IllegalStateException(
						"Bean definition is neither class nor type reference.");
			}
		} catch (JsonParseException | JsonMappingException ex) {
			throw new ResponseClassMismatchException(ex,
					beanDefinition.toString());
		} catch (IOException ex) {
			throw new RemoteServiceException(ex);
		} finally {
			if (urlStream != null) {
				try {
					urlStream.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> fetchUrlForList(final URL url,
			TypeReference<List<T>> beanTypeReference) {
		return (List<T>) fetchUrlForPOJOBean(url, beanTypeReference);
		// try {
		// return getNetworkObjectMapper().readValue(fetchUrl.openStream(),
		// beanTypeReference);
		// } catch (JsonParseException | JsonMappingException ex) {
		// ex.printStackTrace();
		// throw new ResponseClassMismatchException(
		// beanTypeReference.getClass());
		// } catch (IOException ex) {
		// ex.printStackTrace();
		// throw new RemoteServiceException();
		// }
	}

	public String fetchRawUrl(final URL url) {
		try (InputStreamReader reader = new InputStreamReader(url.openStream(),
				Charsets.UTF_8)) {
			return CharStreams.toString(reader);
		} catch (IOException ex) {
			throw new RemoteServiceException(ex);
		}
	}
}
