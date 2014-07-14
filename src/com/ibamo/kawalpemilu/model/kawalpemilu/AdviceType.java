package com.ibamo.kawalpemilu.model.kawalpemilu;

public enum AdviceType {
	LOOKS_GOOD("LooksGood", 0),
	INCORRECT_TOTAL_VOTES("IncorrectTotalVotes", 1),
	MISSING_WITNESS_SIGNATURE("MissingWitnessSignature", 2),
	INCORRECTLY_SCANNED_PAGE("IncorrectlyScannedPage", 3),
	
	CUSTOM("CustomReason", 99),
	
	UNKNOWN("Unknown-ShouldNeverShowUp", -1),
	;
	
	public static AdviceType getAdviceType(final String adviceString) {
		for (AdviceType adviceType : values()) {
			if (adviceType.adviceString.equals(adviceString)) {
				return adviceType;
			}
		}
		
		return UNKNOWN;
	}
	private final String adviceString;
	
	private final int persistenceCode;
	
	private AdviceType(final String adviceString, final int persistenceCode) {
		this.adviceString = adviceString;
		this.persistenceCode = persistenceCode;
	}
	
	public int getPersistenceCode() {
		return persistenceCode;
	}
}