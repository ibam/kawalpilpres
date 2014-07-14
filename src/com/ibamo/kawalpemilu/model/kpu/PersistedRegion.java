package com.ibamo.kawalpemilu.model.kpu;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

@Entity
@Cache
public class PersistedRegion {
	@Id
	private String id;

	@Load
	private Ref<PersistedRegion> parent;

	@Ignore
	private PersistedRegion cachedParent;

	@Index
	private String name;

	@Index
	private RegionLevel level;

	private Integer numberOfVotingStations;

	@Load
	private List<Ref<PersistedRegion>> subregions = new ArrayList<>();

	@Ignore
	private List<PersistedRegion> cachedSubregions = new ArrayList<>();

	@SuppressWarnings("unused")
	private PersistedRegion() {
	}

	public PersistedRegion(final String regionId, final PersistedRegion parent,
			final String name, final RegionLevel level,
			final Integer numberOfVotingStations) {

		this.id = regionId;
		if (parent != null) {
			this.parent = Ref.create(parent);
			this.cachedParent = parent;
		}
		this.name = name;
		this.level = level;
		this.numberOfVotingStations = numberOfVotingStations;
	}

	public void addSubregion(final PersistedRegion subregion) {
		subregions.add(Ref.create(subregion));
		cachedSubregions.add(subregion);
	}

	public String getId() {
		return id;
	}

	public RegionLevel getLevel() {
		return level;
	}

	public String getName() {
		return name;
	}

	public Integer getNumberOfVotingStations() {
		return numberOfVotingStations;
	}

	public PersistedRegion getParent() {
		// refresh cache if cache is stale
		if (cachedParent == null && parent != null) {
			cachedParent = parent.getValue();
		}
		return cachedParent;
	}

	public List<PersistedRegion> getSubregions() {
		// refresh cache if cache is stale
		if (cachedSubregions.size() != subregions.size()) {
			cachedSubregions.clear();
			for (Ref<PersistedRegion> subregionRef : subregions) {
				final PersistedRegion loadedSubregion = subregionRef.get();
				cachedSubregions.add(loadedSubregion);
			}
		}

		return cachedSubregions;
	}

	/**
	 * Return true if the region does not have persisted subregions, but the
	 * level indicates that the subregions should actually be there.
	 * 
	 * @return
	 */
	public boolean hasMissingSubregions() {
		return getSubregions().isEmpty() && getLevel() != RegionLevel.VILLAGE;
	}

	public void setNumberOfVotingStations(Integer numberOfVotingStations) {
		this.numberOfVotingStations = numberOfVotingStations;
	}

	public String toString() {
		return "[Persisted Region #" + getId() + ", " + getName() + "]";
	}
}
