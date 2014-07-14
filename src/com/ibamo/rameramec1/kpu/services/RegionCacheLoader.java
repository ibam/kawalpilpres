package com.ibamo.rameramec1.kpu.services;

import java.util.Collection;

import com.google.common.cache.CacheLoader;
import com.ibamo.kawalpemilu.model.kpu.Region;

public class RegionCacheLoader extends CacheLoader<String, Collection<Region>> {

	@Override
	public Collection<Region> load(String arg0) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

//	private static Logger LOG = Logger.getLogger(RegionCacheLoader.class
//			.getSimpleName());
//
//	private String computeRegionCacheKey(final String regionId) {
//		return regionId;
//	}
//
//	private Collection<Region> fetchSubregionsFromKPU(final String regionId) {
//		final URL regionFetchUrl = RegionAccessService.getInstance()
//				.constructRegionFetchUrl(regionId);
//
//		try {
//			return NetworkService.getInstance().fetchUrlForList(regionFetchUrl, new TypeReference<List<Region>>(){});
//		} catch (ResponseClassMismatchException ex) {
//			LOG.severe("Unable to parse response as Region, declaring region to be not found.");
//			throw new RegionNotFoundException(regionId);
//		}
//
//	}
//
//	@SuppressWarnings("unchecked")
//	private Collection<Region> getSubregionsFromMemcache(final String regionId) {
//		Object memcachedValue = MemcacheServiceFactory.getMemcacheService()
//				.get(computeRegionCacheKey(regionId));
//		if (memcachedValue instanceof Collection) {
//			Collection<?> memcachedCollection = (Collection<?>) memcachedValue;
//			if (memcachedCollection.isEmpty()) {
//				return Collections.emptyList();
//			} else if (memcachedCollection.iterator().next() instanceof Region) {
//				return (Collection<Region>) memcachedCollection;
//			}
//			return null;
//		} else {
//			return null;
//		}
//	}
//
//	@Override
//	public Collection<Region> load(final String regionId) throws Exception {
//		// first, access the memcache
//		Collection<Region> memcachedRegion = getSubregionsFromMemcache(regionId);
//		if (memcachedRegion != null) {
//			return memcachedRegion;
//		}
//
//		// memcache miss, fetch from KPU
//		Collection<Region> kpuSubRegions = fetchSubregionsFromKPU(regionId);
//		if (kpuSubRegions != null) {
//			putSubregionsToMemcache(regionId, kpuSubRegions);
//			return kpuSubRegions;
//		}
//
//		throw new RegionNotFoundException(regionId);
//	}
//
//	private void putSubregionsToMemcache(final String regionId, final Collection<Region> region) {
//		MemcacheServiceFactory.getMemcacheService().put(
//				computeRegionCacheKey(regionId), region);
//	}

}
