package com.ibamo.kawalpemilu.model.kpu;

import java.util.concurrent.ThreadLocalRandom;

public enum Province {
	ACEH ("1"),
	SUMATERA_UTARA ("6728"),
	SUMATERA_BARAT ("12920"),
	RIAU ("14086"),
	JAMBI ("15885"),
	SUMATERA_SELATAN ("17404"),
	BENGKULU ("20802"),
	LAMPUNG ("22328"),
	KEPULAUAN_BANGKA_BELITUNG ("24993"),
	KEPULAUAN_RIAU ("25405"),
	DKI_JAKARTA ("25823"),
	JAWA_BARAT ("26141"),
	JAWA_TENGAH ("32676"),
	DAERAH_ISTIMEWA_YOGYAKARTA ("41863"),
	JAWA_TIMUR ("42385"),
	BANTEN ("51578"),
	BALI ("53241"),
	NUSA_TENGGARA_BARAT ("54020"),
	NUSA_TENGGARA_TIMUR ("55065"),
	KALIMANTAN_BARAT ("58285"),
	KALIMANTAN_TENGAH ("60371"),
	KALIMANTAN_SELATAN ("61965"),
	KALIMANTAN_TIMUR ("64111"),
	SULAWESI_UTARA ("65702"),
	SULAWESI_TENGAH ("67393"),
	SULAWESI_SELATAN ("69268"),
	SULAWESI_TENGGARA ("72551"),
	GORONTALO ("74716"),
	SULAWESI_BARAT ("75425"),
	MALUKU ("76096"),
	MALUKU_UTARA ("77085"),
	PAPUA ("78203"),
	PAPUA_BARAT ("81877"), ;

	//private String provincialCode;
	private Region region;

	private Province(final String provincialCode) {
		//this.provincialCode = provincialCode;
		region = new Region();
		region.setId(provincialCode);
		region.setLevel(RegionLevel.PROVINCE);
		region.setName(this.name().replace('_', ' '));
		region.setNumberOfVotingStation(null);
		region.setParent(null);
	}

	public Region getRegion() {
		return region;
	}

	private static int getRandomProvinceIndex() {
		return ThreadLocalRandom.current().nextInt(Province.values().length);
	}

	public static Province getRandomProvince() {
		return Province.values()[getRandomProvinceIndex()];
	}
}