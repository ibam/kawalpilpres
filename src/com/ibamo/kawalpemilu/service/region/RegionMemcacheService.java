package com.ibamo.kawalpemilu.service.region;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.MemcacheService.IdentifiableValue;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.ibamo.kawalpemilu.framework.beans.PairValues;
import com.ibamo.kawalpemilu.model.kpu.Region;

@Deprecated
public class RegionMemcacheService {
	private static Logger LOG = Logger.getLogger(RegionMemcacheService.class
			.getName());

	private static class SingletonHolder {
		private static final RegionMemcacheService INSTANCE = new RegionMemcacheService();
	}

	public static RegionMemcacheService getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private String computeRegionCacheKey(final String regionId) {
		return regionId;
	}

	public void putSubregionsToMemcache(final String regionId,
			final Collection<Region> region) {
		MemcacheServiceFactory.getMemcacheService().put(
				computeRegionCacheKey(regionId), region);
	}

	public List<Region> getSubregionsFromMemcache(final String regionId) {
		final PairValues<IdentifiableValue, List<Region>> result = getSubregionsWithIdentifiable(regionId);
		
		if (result == null) {
			return null;
		} else {
			return result.getSecond();
		}
	}

	@SuppressWarnings("unchecked")
	private PairValues<IdentifiableValue, List<Region>> getSubregionsWithIdentifiable(
			final String regionId) {
		final IdentifiableValue identifiable = getIdentifiableForSubregions(regionId);
		if (identifiable == null) {
			return null;
		}
		
		Object memcachedValue = identifiable.getValue();

		if (!(memcachedValue instanceof List)) {
			return null;
		}

		List<?> memcachedCollection = (List<?>) memcachedValue;
		if (memcachedCollection.isEmpty()) {
			return new PairValues<IdentifiableValue, List<Region>>(
					identifiable, Collections.<Region> emptyList());
		}

		if (!(memcachedCollection.iterator().next() instanceof Region)) {
			return null;
		}

		return new PairValues<IdentifiableValue, List<Region>>(identifiable,
				(List<Region>) memcachedCollection);
	}

	private IdentifiableValue getIdentifiableForSubregions(final String regionId) {
		return MemcacheServiceFactory.getMemcacheService().getIdentifiable(
				computeRegionCacheKey(regionId));
	}

	public void updateRegion(final Region region) {
		final String regionParentId = region.getParent();
		final PairValues<IdentifiableValue, List<Region>> foundSubregions = getSubregionsWithIdentifiable(regionParentId);
		
		if (foundSubregions == null) {
			LOG.warning("Trying to update region, but subregions cannot be found in memcache.");
			return;
		}
		
		final List<Region> memcachedSubregions = foundSubregions.getSecond();

		boolean isSubregionChanged = false;

		for (int i = 0; i < memcachedSubregions.size(); i++) {
			if (memcachedSubregions.get(i).equals(region)) {
				memcachedSubregions.set(i, region);
				isSubregionChanged = true;
				break;
			}
		}

		if (!isSubregionChanged) {
			LOG.warning("Attempting to update subregion "
					+ region.getId()
					+ ", but subregions on the list is not modified. There are "
					+ memcachedSubregions.size()
					+ " subregions for region's parent.");
			return;
		}
		
		putSubregionsToMemcache(regionParentId, memcachedSubregions);
	}
}
