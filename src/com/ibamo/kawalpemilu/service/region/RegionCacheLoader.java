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
		}

		LOG.finer("Persisted subregion of persisted region " + persistedRegion
				+ " that is about to be converted to regions are "
				+ persistedRegion.getSubregions());

		return persistedRegion;
	}
}
