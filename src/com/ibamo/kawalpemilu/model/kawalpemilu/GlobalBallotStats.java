package com.ibamo.kawalpemilu.model.kawalpemilu;

public class GlobalBallotStats {
	private int totalProcessedCount;
	private int totalSuspectedNegativeCount;
	private int totalVerifiedNegativeCount;

	private long lastUpdateTimestamp;

	public GlobalBallotStats() {
		lastUpdateTimestamp = System.currentTimeMillis();
	}
	
	public long getLastUpdateTimestamp() {
		return lastUpdateTimestamp;
	}

	public int getTotalProcessedCount() {
		return totalProcessedCount;
	}

	public int getTotalSuspectedNegativeCount() {
		return totalSuspectedNegativeCount;
	}

	public int getTotalVerifiedNegativeCount() {
		return totalVerifiedNegativeCount;
	}

	public void setTotalProcessedCount(final int totalProcessedCount) {
		this.totalProcessedCount = totalProcessedCount;
	}

	public void setTotalSuspectedNegativeCount(
			final int totalSuspectedNegativeCount) {
		this.totalSuspectedNegativeCount = totalSuspectedNegativeCount;
	}

	public void setTotalVerifiedNegativeCount(
			final int totalVerifiedNegativeCount) {
		this.totalVerifiedNegativeCount = totalVerifiedNegativeCount;
	}
}
