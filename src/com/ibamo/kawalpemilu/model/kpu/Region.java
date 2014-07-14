package com.ibamo.kawalpemilu.model.kpu;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Region implements Serializable {
	/**
	 * Generated.
	 */
	private static final long serialVersionUID = -8469986581581986682L;

	private String regionId;
	private String parent;

	private String name;
	private RegionLevel level;
	private Integer numberOfVotingStation;

	@Override
	public boolean equals(Object compared) {
		if (!(compared instanceof Region)) {
			return false;
		}

		final Region comparedRegion = (Region) compared;
		return getId().equals(comparedRegion.getId());
	}

	@JsonProperty("wilayah_id")
	public String getId() {
		return regionId;
	}

	public RegionLevel getLevel() {
		return level;
	}

	@JsonProperty("nama")
	public String getName() {
		return name;
	}

	@JsonIgnore
	public Integer getNumberOfVotingStations() {
		return numberOfVotingStation;
	}

	@JsonProperty("parent")
	public String getParent() {
		return parent;
	}

	@JsonIgnore
	public boolean hasNumberOfVotingStations() {
		return getNumberOfVotingStations() != null
				&& getNumberOfVotingStations() > 0;
	}

	@JsonProperty("wilayah_id")
	public void setId(final String wilayahId) {
		this.regionId = wilayahId;
	}

	public void setLevel(final RegionLevel level) {
		this.level = level;
	}

	@JsonProperty("tingkat")
	private void setLevelCode(final String levelCode) {
		setLevel(RegionLevel.fromCode(levelCode));
	}

	@JsonProperty("nama")
	public void setName(final String name) {
		this.name = name;
	}

	@JsonIgnore
	public void setNumberOfVotingStation(final Integer numberOfVotingStation) {
		this.numberOfVotingStation = numberOfVotingStation;
	}

	@JsonProperty("parent")
	public void setParent(final String parent) {
		this.parent = parent;
	}

	@Override
	public String toString() {
		return "[Region #" + getId() + ", " + getName() + "]";
	}
}
