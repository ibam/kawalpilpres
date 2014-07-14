package com.ibamo.kawalpemilu.model.kpu;

import com.google.common.primitives.Ints;

public enum RegionLevel {
	PROVINCE(1), 	// Propinsi
	REGENCY(2), 	// Kabupaten
	DISTRICT(3),	// Kecamatan
	VILLAGE(4),		// Desa/kecamatan
	UNKNOWN(-1); 	// Unknown
	
	private int regionCode;
	
	private RegionLevel(final int regionCode) {
		this.regionCode = regionCode;
	}

	public static RegionLevel fromCode(final String levelCode) {
		if (Ints.tryParse(levelCode) == null) {
			return RegionLevel.UNKNOWN;
		} else {
			return fromCode(Integer.parseInt(levelCode));
		}
	}
	
	public static RegionLevel fromCode(final int levelCode) {
		for (RegionLevel level : values()) {
			if (level.regionCode == levelCode) {
				return level;
			}
		}
		
		return RegionLevel.UNKNOWN;
	}
}
