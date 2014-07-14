package com.ibamo.kawalpemilu.service.region;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.cache.CacheLoader;
import com.ibamo.kawalpemilu.framework.exceptions.RegionNotFoundException;
import com.ibamo.kawalpemilu.framework.exceptions.ResponseClassMismatchException;
import com.ibamo.kawalpemilu.model.kpu.PersistedRegion;
import com.ibamo.kawalpemilu.model.kpu.Region;
import com.ibamo.kawalpemilu.service.NetworkService;

public class RegionCacheLoader extends CacheLoader<Region, PersistedRegion> {

	private static Logger LOG = Logger.getLogger(RegionCacheLoader.class
			.getSimpleName());

	// private String computeRegionCacheKey(final String regionId) {
	// return regionId;
	// }

	private List<Region> fetchSubregionsFromKPU(final String regionId) {
		final URL regionFetchUrl = RegionAccessService.getInstance()
				.constructRegionFetchUrl(regionId);

		try {
			final long startTime = System.currentTimeMillis();
			final List<Region> subregions = NetworkService.getInstance()
					.fetchUrlForList(regionFetchUrl,
							new TypeReference<List<Region>>() {
							});

			LOG.info("Fetching subregions for region " + regionId
					+ " from KPU took "
					+ (System.currentTimeMillis() - startTime) + " ms.");

			return subregions;

		} catch (ResponseClassMismatchException ex) {
			LOG.severe("Unable to parse response as Region, declaring region to be not found.");
			throw new RegionNotFoundException(regionId);
		}

	}

	// @SuppressWarnings("unchecked")
	// private List<Region> getSubregionsFromMemcache(final String regionId) {
	// Object memcachedValue = MemcacheServiceFactory.getMemcacheService()
	// .get(computeRegionCacheKey(regionId));
	//
	// if (!(memcachedValue instanceof List)) {
	// return null;
	// }
	//
	// List<?> memcachedCollection = (List<?>) memcachedValue;
	// if (memcachedCollection.isEmpty()) {
	// return Collections.emptyList();
	// }
	//
	// if (!(memcachedCollection.iterator().next() instanceof Region)) {
	// return null;
	// }
	//
	// return (List<Region>) memcachedCollection;
	// }

	@Override
	public PersistedRegion load(final Region region) throws Exception {
		// first, access the memcache-backed datastore
		final long startTime = System.currentTimeMillis();
		PersistedRegion persistedRegion = RegionDatastoreAccessService
				.getInstance().load(region.getId());
		LOG.info("Fetching persisted region for region " + region
				+ " from datastore took "
				+ (System.currentTimeMillis() - startTime) + " ms.");

		if (persistedRegion == null || persistedRegion.hasMissingSubregions()) {
			// if null, convert the Region into PersistedRegion. left parent to
			// null since we assume this is a province level region.
			if (persistedRegion == null) {
				persistedRegion = RegionDatastoreAccessService.getInstance()
						.toPersistedRegion(region, null);
			}

			// fetch the subregions
			final List<Region> kpuSubregions = fetchSubregionsFromKPU(region
					.getId());

			LOG.finer("Fetched subregion of region " + region + " are "
					+ kpuSubregions);

			// convert subregions to PersistedRegion with the persistedRegion
			// constructed above as its parent entity
			final Collection<PersistedRegion> persistedSubregions = RegionDatastoreAccessService
					.getInstance().toPersistedRegions(kpuSubregions,
							persistedRegion);

			LOG.finer("Converted persisted subregions of region " + region
					+ " are " + persistedSubregions);

			// set the subregions as subregions of the parent region
			for (PersistedRegion persistedSubregion : persistedSubregions) {
				persistedRegion.addSubregion(persistedSubregion);
			}

			// store the persistedRegion first since this is the parent entity
			RegionDatastoreAccessService.getInstance().save(persistedRegion);

			// store the persistedSubregions
			RegionDatastoreAccessService.getInstance()
					.save(persistedSubregions);

			// // reload the persistedRegion so that the subregions will be
			// // populated with the correct refs
			// persistedRegion =
			// RegionDatastoreAccessService.getInstance().load(
			// region.getId());
		}

		LOG.finer("Persisted subregion of persisted region " + persistedRegion
				+ " that is about to be converted to regions are "
				+ persistedRegion.getSubregions());

		return persistedRegion;

		// else if (persistedRegion.getSubregions().isEmpty()
		// && persistedRegion.getLevel() != RegionLevel.VILLAGE) {
		// // region should have subregions:
		// // 1. load the subregions from KPU
		// // 2. convert region to persistedregion
		// // 2. add subregions to persistedregion
		// // 3. convert the subregions to persistedregions
		// // 3. persist the persistedregion
		// // 4. persist the persistedsubregions
		// }

		// final Set<String> storedSubregionIds = RegionDatastoreAccessService
		// .getInstance().loadSubregionIds(regionId);
		//
		// // if we cannot find any subregions from the datastore..
		// if (storedSubregionIds.isEmpty()) {
		// // ..fetch them from KPU..
		// final List<Region> kpuSubregions = fetchSubregionsFromKPU(regionId);
		//
		// // ..convert them to PersistedRegions..
		// final Collection<PersistedRegion> persistedRegions =
		// RegionDatastoreAccessService
		// .getInstance().toPersistedRegions(kpuSubregions);
		//
		// // ..persist them in the datastore..
		// RegionDatastoreAccessService.getInstance().save(persistedRegions);
		//
		// // ..and finally, convert them to the subregion ids.
		// return Lists.newArrayList(toRegionIds(kpuSubregions));
		// } else {
		// return Lists.newArrayList(storedSubregionIds);
		// }

		// List<Region> memcachedRegion = RegionMemcacheService.getInstance()
		// .getSubregionsFromMemcache(regionId);
		//
		// if (memcachedRegion != null) {
		// return memcachedRegion;
		// }
		//
		// // memcache miss, fetch from KPU
		// List<Region> kpuSubRegions = fetchSubregionsFromKPU(regionId);
		// if (kpuSubRegions != null) {
		// RegionMemcacheService.getInstance().putSubregionsToMemcache(
		// regionId, kpuSubRegions);
		// return kpuSubRegions;
		// }
		//
		// throw new RegionNotFoundException(regionId);
	}

	// private Collection<PersistedRegion> toPersistedRegions(
	// final Collection<Region> regions) {
	// final Collection<PersistedRegion> persistedRegions = new ArrayList<>();
	// for (Region region : regions) {
	// persistedRegions.add(toPersistedRegion(region));
	// }
	//
	// return persistedRegions;
	// }
	//
	// private PersistedRegion toPersistedRegion(final Region region) {
	// final PersistedRegion persistedRegion = new PersistedRegion(
	// region.getId(), region.getParent(), region.getName(),
	// region.getLevel(), region.getNumberOfVotingStations());
	//
	// return persistedRegion;
	// }

	// private Set<String> toRegionIds(final List<Region> regions) {
	// final Set<String> regionIds = new HashSet<>();
	// for (Region region : regions) {
	// regionIds.add(region.getId());
	// }
	// return regionIds;
	// }
}
