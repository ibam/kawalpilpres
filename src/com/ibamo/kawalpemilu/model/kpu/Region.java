package com.ibamo.kawalpemilu.model.kpu;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Region implements Serializable {
	// [{"wilayah_id":"6729","parent":"6728","nama":"LABUHANBATU SELATAN","tingkat":"2"

	/**
	 * Generated.
	 */
	private static final long serialVersionUID = -8469986581581986682L;

	private String regionId;
	private String parent;

	private String name;
	private RegionLevel level;
	private Integer numberOfVotingStation;

	@JsonIgnore
	public void setNumberOfVotingStation(final Integer numberOfVotingStation) {
		this.numberOfVotingStation = numberOfVotingStation;
	}

	@JsonIgnore
	public Integer getNumberOfVotingStations() {
		return numberOfVotingStation;
	}

	@JsonIgnore
	public boolean hasNumberOfVotingStations() {
		return getNumberOfVotingStations() != null
				&& getNumberOfVotingStations() > 0;
	}

	@JsonProperty("parent")
	public String getParent() {
		return parent;
	}

	@JsonProperty("parent")
	public void setParent(final String parent) {
		this.parent = parent;
	}

	@JsonProperty("wilayah_id")
	public String getId() {
		return regionId;
	}

	@JsonProperty("wilayah_id")
	public void setId(final String wilayahId) {
		this.regionId = wilayahId;
	}

	@JsonProperty("nama")
	public String getName() {
		return name;
	}

	@JsonProperty("nama")
	public void setName(final String name) {
		this.name = name;
	}

	public RegionLevel getLevel() {
		return level;
	}

	public void setLevel(final RegionLevel level) {
		this.level = level;
	}

	@JsonProperty("tingkat")
	private void setLevelCode(final String levelCode) {
		setLevel(RegionLevel.fromCode(levelCode));
	}

	@Override
	public boolean equals(Object compared) {
		if (!(compared instanceof Region)) {
			return false;
		}

		final Region comparedRegion = (Region) compared;
		return getId().equals(comparedRegion.getId());
	}

	@Override
	public String toString() {
		return "[Region #" + getId() + ", " + getName() + "]";
	}
}
