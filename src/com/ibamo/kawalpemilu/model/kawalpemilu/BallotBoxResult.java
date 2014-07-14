package com.ibamo.kawalpemilu.model.kawalpemilu;

import com.ibamo.kawalpemilu.model.kpu.Region;

public class BallotBoxResult {
	// http://scanc1.kpu.go.id/viewp.php?f=008954400104.jpg
	private static final String imageUrlTemplate = "http://scanc1.kpu.go.id/viewp.php?f=";
	private static final char idSeparator = '-';

	private String id;
	private String regionId;
	private int votingStationNumber;
	private String nonce;

	public BallotBoxResult() {
	}

	public BallotBoxResult(final Region region, final int votingStationNumber) {
		this.regionId = region.getId();
		this.votingStationNumber = votingStationNumber;
		setId(new StringBuilder(regionId).append(idSeparator)
				.append(votingStationNumber).toString());
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getImageUrl() {
		final StringBuilder imageUrlBuilder = new StringBuilder(
				imageUrlTemplate);

		final StringBuilder fileNameBuilder = new StringBuilder(getRegionId());
		while (fileNameBuilder.length() < 7) {
			fileNameBuilder.insert(0, '0');
		}

		fileNameBuilder.append(getVotingStationNumber());
		while (fileNameBuilder.length() < 10) {
			fileNameBuilder.insert(7, '0');
		}

		fileNameBuilder.append("04.jpg");
		imageUrlBuilder.append(fileNameBuilder);

		return imageUrlBuilder.toString();
	}

	public String getNonce() {
		return nonce;
	}

	public String getRegionId() {
		return regionId;
	}

	public int getVotingStationNumber() {
		return votingStationNumber;
	}

	public void setNonce(final String nonce) {
		this.nonce = nonce;
	}
}
