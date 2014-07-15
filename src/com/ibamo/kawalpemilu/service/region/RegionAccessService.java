package com.ibamo.kawalpemilu.service.region;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import com.google.common.base.Splitter;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.ibamo.kawalpemilu.framework.exceptions.InvalidServerConfiguration;
import com.ibamo.kawalpemilu.framework.exceptions.InvalidServerConfiguration.ConfigType;
import com.ibamo.kawalpemilu.framework.exceptions.RegionNotFoundException;
import com.ibamo.kawalpemilu.model.kpu.PersistedRegion;
import com.ibamo.kawalpemilu.model.kpu.Region;
import com.ibamo.kawalpemilu.model.kpu.RegionLevel;
import com.ibamo.kawalpemilu.service.NetworkService;

public class RegionAccessService {
	private static class SingletonHolder {
		private static final RegionAccessService INSTANCE = new RegionAccessService();
	}

	private static Logger LOG = Logger.getLogger(RegionAccessService.class
			.getName());

	private static final String GET_REGION_URL_TEMPLATE = "http://dapil.kpu.go.id/api.php?cmd=browse_wilayah&wilayah_id=";

	private static final String TPS_COUNT_SCRAPING_URL_TEMPLATE = "http://pilpres2014.kpu.go.id/c1.php?cmd=select&parent=";

	private static final String TPS_SIGNAL_STRING = "class=\"image1_";

	public static RegionAccessService getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private final LoadingCache<Region, PersistedRegion> regionCacheAccessor;

	private RegionAccessService() {
		regionCacheAccessor = buildCache();
	}

	private LoadingCache<Region, PersistedRegion> buildCache() {
		return CacheBuilder.newBuilder().maximumSize(80000)
				.build(new RegionCacheLoader());
	}

	private String buildRegionUrlString(final String regionId) {
		final StringBuilder urlStringBuilder = new StringBuilder(
				GET_REGION_URL_TEMPLATE);
		urlStringBuilder.append(regionId);

		return urlStringBuilder.toString();
	}

	private String buildVotingStationNumbersUrlString(final String regionId) {
		final StringBuilder urlStringBuilder = new StringBuilder(
				TPS_COUNT_SCRAPING_URL_TEMPLATE);
		urlStringBuilder.append(regionId);

		return urlStringBuilder.toString();
	}

	public URL constructRegionFetchUrl(final String regionId) {
		final String regionUrlString = buildRegionUrlString(regionId);
		try {
			return new URL(regionUrlString);
		} catch (MalformedURLException ex) {
			throw new InvalidServerConfiguration(ConfigType.GET_REGION_URL);
		}
	}

	public URL constructVotingStationNumbersFetchUrl(final String regionId) {
		final String regionUrlString = buildVotingStationNumbersUrlString(regionId);
		try {
			return new URL(regionUrlString);
		} catch (MalformedURLException ex) {
			throw new InvalidServerConfiguration(
					ConfigType.GET_VOTING_NUMBER_URL);
		}
	}

	public int countOccurences(final String matchedString,
			final String targetString) {

		return (Splitter.on(matchedString).trimResults().omitEmptyStrings()
				.splitToList(targetString).size() - 1) / 4;
	}

	private int fetchNumberOfVotingStations(final Region region) {
		if (region.getLevel() != RegionLevel.VILLAGE) {
			throw new IllegalArgumentException(
					"Can only get the number of voting station from a village-level region.");
		}

		final URL url = constructVotingStationNumbersFetchUrl(region.getId());
		final String rawScrapedPage = NetworkService.getInstance().fetchRawUrl(
				url);

		final int occurences = countOccurences(TPS_SIGNAL_STRING,
				rawScrapedPage);
		if (occurences == 0) {
			LOG.warning("Cannot count signal \"" + TPS_SIGNAL_STRING
					+ "\" on scraped page " + rawScrapedPage);
			return -1; // no photo has been uploaded yet
		} else {
			LOG.finer("Found " + occurences + " TPS from raw scraped page "
					+ rawScrapedPage);
		}

		return occurences;
	}

	private LoadingCache<Region, PersistedRegion> getCacheBackedRegionAccessor() {
		return regionCacheAccessor;
	}

	/**
	 * Will return -1 if no TPS data is ready yet for the region.
	 * 
	 * @param region
	 * @return
	 */
	public int getNumberOfVotingStations(final Region region) {
		if (region.getLevel() != RegionLevel.VILLAGE) {
			throw new IllegalArgumentException(
					"Can only get the number of voting station from a village-level region.");
		}

		if (region.hasNumberOfVotingStations()) {
			return region.getNumberOfVotingStations();
		}

		updateNumberOfVotingStations(region);

		if (!region.hasNumberOfVotingStations()) {
			return -1; // No TPS data yet.
		}

		return region.getNumberOfVotingStations();
	}

	public Region getRegion(final String regionId) {
		final Region region = new Region();
		region.setId(regionId);
		return getRegion(region);
	}
	
	public Region getRegion(final Region region) {
		try {
			final PersistedRegion persistedRegion = getCacheBackedRegionAccessor()
					.get(region);
			
			if (persistedRegion == null) {
				return null;
			}

			return RegionDatastoreAccessService.getInstance().toRegion(
					persistedRegion);
		} catch (ExecutionException ex) {
			throw new RegionNotFoundException(region.getId());
		}
	}

	public List<Region> getSubregions(final Region region) {
		try {
			final PersistedRegion persistedRegion = getCacheBackedRegionAccessor()
					.get(region);

			final List<PersistedRegion> persistedSubregions = persistedRegion
					.getSubregions();

			LOG.finer("Persisted subregion of persisted region "
					+ persistedRegion
					+ " that is about to be converted to regions are "
					+ persistedSubregions);

			return RegionDatastoreAccessService.getInstance().toRegions(
					persistedSubregions);
		} catch (ExecutionException ex) {
			throw new RegionNotFoundException(region.getId());
		}
	}

	private void updateNumberOfVotingStations(final Region region) {
		long startTime = System.currentTimeMillis();
		final int kpuNumberOfVotingStations = fetchNumberOfVotingStations(region);
		LOG.info("Fetching number of voting stations from KPU took "
				+ (System.currentTimeMillis() - startTime) + " ms.");

		if (kpuNumberOfVotingStations > 0) {
			region.setNumberOfVotingStation(kpuNumberOfVotingStations);

			// update the persisted region
			final PersistedRegion persistedRegion = RegionDatastoreAccessService
					.getInstance().load(region.getId());

			persistedRegion
					.setNumberOfVotingStations(kpuNumberOfVotingStations);

			RegionDatastoreAccessService.getInstance().save(persistedRegion);
		}
	}
}
