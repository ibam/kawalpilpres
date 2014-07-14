package com.ibamo.rameramec1.kpu.services;


public class NetworkService {
//
//	private static Logger LOG = Logger.getLogger(NetworkService.class
//			.getSimpleName());
//
//	private ObjectMapper networkMapper = null;
//
//	private NetworkService() {
//		networkMapper = buildObjectMapper();
//	}
//
//	private ObjectMapper buildObjectMapper() {
//		ObjectMapper constructedMapper = new ObjectMapper();
//		constructedMapper.configure(
//				DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//		return constructedMapper;
//	}
//
//	private static class SingletonHolder {
//		private static final NetworkService INSTANCE = new NetworkService();
//	}
//
//	public static NetworkService getInstance() {
//		return SingletonHolder.INSTANCE;
//	}
//
//	public <T> Collection<T> fetchUrlForCollection(final URL fetchUrl,
//			TypeReference<List<T>> beanTypeReference) {
//		try {
//			return networkMapper.readValue(fetchUrl.openStream(),
//					beanTypeReference);
//		} catch (JsonParseException | JsonMappingException ex) {
//			ex.printStackTrace();
//			throw new ResponseClassMismatchException(
//					beanTypeReference.getClass());
//		} catch (IOException ex) {
//			ex.printStackTrace();
//			throw new RemoteServiceException();
//		}
//	}
//
//	public <T> T fetchUrlFor(final URL fetchUrl, Class<T> beanClass) {
//		try {
//			return networkMapper.readValue(fetchUrl.openStream(), beanClass);
//		} catch (JsonParseException | JsonMappingException ex) {
//			throw new ResponseClassMismatchException(beanClass);
//		} catch (IOException ex) {
//			ex.printStackTrace();
//			throw new RemoteServiceException();
//		}
//	}
}
