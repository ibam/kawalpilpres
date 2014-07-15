package com.ibamo.kawalpemilu.service.region;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import com.ibamo.kawalpemilu.model.kpu.PersistedRegion;
import com.ibamo.kawalpemilu.model.kpu.Region;

public class RegionDatastoreAccessService {
	private static class SingletonHolder {
		private static final RegionDatastoreAccessService INSTANCE = new RegionDatastoreAccessService();
	}

	private static Logger LOG = Logger
			.getLogger(RegionDatastoreAccessService.class.getName());

	public static RegionDatastoreAccessService getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private RegionDatastoreAccessService() {
	}

	public PersistedRegion load(final String regionId) {
		return ofy().load().type(PersistedRegion.class).id(regionId).now();
	}

	public void save(final Collection<PersistedRegion> persistedRegions) {
		ofy().save().entities(persistedRegions);
	}

	public void save(final PersistedRegion region) {
		ofy().save().entity(region);
	}

	public void saveNow(final Collection<PersistedRegion> persistedRegions) {
		ofy().save().entities(persistedRegions).now();
	}

	public void saveNow(final PersistedRegion region) {
		ofy().save().entity(region).now();
	}

	public PersistedRegion toPersistedRegion(final Region region,
			final PersistedRegion parent) {
		final PersistedRegion persistedRegion = new PersistedRegion(
				region.getId(), parent, region.getName(), region.getLevel(),
				region.getNumberOfVotingStations());

		return persistedRegion;
	}

	public Collection<PersistedRegion> toPersistedRegions(
			final Collection<Region> regions,
			final PersistedRegion persistedRegion) {
		final Collection<PersistedRegion> persistedRegions = new ArrayList<>();
		for (Region region : regions) {
			persistedRegions.add(toPersistedRegion(region, persistedRegion));
		}

		return persistedRegions;
	}

	public Region toRegion(final PersistedRegion persistedRegion) {
		final Region region = new Region();
		region.setId(persistedRegion.getId());
		region.setLevel(persistedRegion.getLevel());
		region.setName(persistedRegion.getName());
		region.setNumberOfVotingStation(persistedRegion
				.getNumberOfVotingStations());

		if (persistedRegion.getParent() == null) {
			region.setParent(null);
		} else {
			region.setParent(persistedRegion.getParent().getId());
		}

		return region;
	}

	public List<Region> toRegions(final List<PersistedRegion> persistedRegions) {
		final List<Region> regions = new ArrayList<>();
		boolean isNullInList = false;

		for (PersistedRegion persistedRegion : persistedRegions) {
			if (persistedRegion == null) {
				isNullInList = true;
				continue;
			}
			regions.add(toRegion(persistedRegion));
		}

		if (isNullInList) {
			LOG.warning("Null element encountered when converting persisted regions to regions. Persisted regions are "
					+ persistedRegions);
		}

		return regions;
	}
}
