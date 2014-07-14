package com.ibamo.rameramec1.kpu.services;


public class RegionAccessService {
//	private static class SingletonHolder {
//		private static final RegionAccessService INSTANCE = new RegionAccessService();
//	}
//	
//	private static Logger LOG = Logger.getLogger(RegionAccessService.class.getSimpleName());
//
//	private static final String urlTemplate = "http://dapil.kpu.go.id/api.php?cmd=browse_wilayah&wilayah_id=";
//
//	public static RegionAccessService getInstance() {
//		return SingletonHolder.INSTANCE;
//	}
//
//	private final LoadingCache<String, Collection<Region>> regionCacheAccessor;
//	
//	private RegionAccessService() {
//		regionCacheAccessor = buildCache();
//	}
//	
//	private LoadingCache<String, Collection<Region>> buildCache() {
//		return CacheBuilder.newBuilder().maximumSize(80000).build(new RegionCacheLoader());
//	}
//	
//	private String buildRegionUrlString(final String regionId) {
//		final StringBuilder urlStringBuilder = new StringBuilder(urlTemplate);
//		urlStringBuilder.append(regionId);
//		
//		return urlStringBuilder.toString();
//	}
//	public URL constructRegionFetchUrl(final String regionId) {
//		final String regionUrlString = buildRegionUrlString(regionId);
//		try {
//			return new URL(regionUrlString);
//		} catch (MalformedURLException ex) {
//			LOG.severe("Malformed URL when trying to fetch regions: " + regionUrlString);
//			throw new InvalidServerConfiguration(ConfigType.BROWSE_REGION_URL);
//		}
//	}
//
//	private LoadingCache<String, Collection<Region>> getRegionCacheAccessor() {
//		return regionCacheAccessor;
//	}
//
//	public Collection<Region> readRegion(final String regionId) {
//		try {
//			return getRegionCacheAccessor().get(regionId);
//		} catch (ExecutionException ex) {
//			throw new RegionNotFoundException(regionId);
//		}
//	}
}
