package com.ibamo.kawalpemilu.service.ballot;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Ints;
import com.ibamo.kawalpemilu.framework.exceptions.InvalidServerConfiguration;
import com.ibamo.kawalpemilu.framework.exceptions.InvalidServerConfiguration.ConfigType;
import com.ibamo.kawalpemilu.model.kawalpemilu.BallotAccessor;
import com.ibamo.kawalpemilu.model.kawalpemilu.BallotBoxPersistedAdvice;
import com.ibamo.kawalpemilu.model.kawalpemilu.BallotBoxPersistedTally;
import com.ibamo.kawalpemilu.model.kawalpemilu.BallotBoxResult;
import com.ibamo.kawalpemilu.model.kawalpemilu.BallotBoxUserInput;
import com.ibamo.kawalpemilu.model.kawalpemilu.GlobalBallotStats;
import com.ibamo.kawalpemilu.model.kpu.Province;
import com.ibamo.kawalpemilu.model.kpu.Region;
import com.ibamo.kawalpemilu.model.kpu.RegionLevel;
import com.ibamo.kawalpemilu.service.region.RegionAccessService;

public class BallotService {
	private static class SingletonHolder {
		private static final BallotService INSTANCE = new BallotService();
	}

	private static final String BALLOT_BOX_ID_ENCRYPTION_ALGORITHM = "AES";

	private static final String BALLOT_BOX_ID_ENCRYPTION_KEY_NAME = "ballot.id.encryption.key";

	private static Logger LOG = Logger.getLogger(BallotService.class.getName());

	private static final int MAX_REROLL = 3;

	public static BallotService getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private final byte[] ballotBoxIdEncryptionKey;

	private static final double REVERIFY_CHANCE_PERCENTAGE = 20;

	private BallotService() {
		final String keyValue = System
				.getProperty(BALLOT_BOX_ID_ENCRYPTION_KEY_NAME);
		ballotBoxIdEncryptionKey = BaseEncoding.base32Hex().decode(keyValue);
	}

	private Cipher constructCipher(final int cipherMode,
			final byte[] encryptionKey) throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance(BALLOT_BOX_ID_ENCRYPTION_ALGORITHM);
		cipher.init(cipherMode, new SecretKeySpec(encryptionKey,
				BALLOT_BOX_ID_ENCRYPTION_ALGORITHM));

		return cipher;
	}

	public void convertTalliesToKarma() {
		Collection<BallotBoxPersistedTally> allTallies = BallotAccessService
				.getInstance().getAllTallies();

		for (BallotBoxPersistedTally tally : allTallies) {
			if (tally.getAdviceKarmaBalance() == 0) {
				tally.syncAdviceNumbers();
				BallotAccessService.getInstance().storeTally(tally);

				LOG.info("Fixing karma for tally #" + tally.getId());
			}
		}
	}

	public String decryptBallotId(final String ballotBoxId) {
		try {
			byte[] encryptionKey = getBallotBoxIdEncryptionKey();
			Cipher cipher = constructCipher(Cipher.DECRYPT_MODE, encryptionKey);

			byte[] encryptedId = BaseEncoding.base64Url().decode(ballotBoxId);

			final String decryptedId = new String(cipher.doFinal(encryptedId),
					Charsets.UTF_8);
			return decryptedId;

		} catch (GeneralSecurityException ex) {
			throw new InvalidServerConfiguration(
					ConfigType.BALLOT_ID_KEY_DECRYPTION, ex);
		}
	}

	public void encryptBallotId(final BallotBoxResult ballot) {
		try {
			byte[] encryptionKey = getBallotBoxIdEncryptionKey();
			Cipher cipher = constructCipher(Cipher.ENCRYPT_MODE, encryptionKey);

			byte[] encryptedIdBytes = cipher.doFinal(ballot.getId().getBytes(
					Charsets.UTF_8));

			final String encryptedId = BaseEncoding.base64Url().encode(
					encryptedIdBytes);

			ballot.setId(encryptedId);
		} catch (GeneralSecurityException ex) {
			throw new InvalidServerConfiguration(
					ConfigType.BALLOT_ID_KEY_ENCRYPTION, ex);
		}
	}

	public Collection<BallotBoxPersistedTally> getAllTallies() {
		return BallotAccessService.getInstance().getAllTallies();
	}

	private byte[] getBallotBoxIdEncryptionKey() {
		return ballotBoxIdEncryptionKey;
	}

	private BallotBoxResult getBallotBoxResult(final Region region,
			final int votingStationNumber) {
		if (region.getLevel() != RegionLevel.VILLAGE) {
			throw new IllegalArgumentException(
					"Can only fetch ballot box result from a village-level region. Region is "
							+ region + ".");
		}

		final BallotBoxResult result = new BallotBoxResult(region,
				votingStationNumber);

		return result;
	}

	public GlobalBallotStats getGlobalBallotStats() {
		final GlobalBallotStats ballotStats = new GlobalBallotStats();
		ballotStats.setTotalProcessedCount(getTotalProcessedCount());

		ballotStats
				.setTotalSuspectedNegativeCount(getTotalSuspectedNegativeCount());

		ballotStats
				.setTotalVerifiedNegativeCount(getTotalVerifiedNegativeCount());

		return ballotStats;

	}

	private int getNumberOfVotingStation(final Region region) {
		return RegionAccessService.getInstance().getNumberOfVotingStations(
				region);
	}
	private Region getRandomBallotRegion() {
		Region randomVillage = null;

		do {
			final Province randomProvince = Province.getRandomProvince();
			randomVillage = getRandomProvincialRegion(randomProvince,
					RegionLevel.VILLAGE);

		} while (getNumberOfVotingStation(randomVillage) < 0);

		return randomVillage;
	}

	private Region getRandomProvincialRegion(final Province province,
			final RegionLevel regionLevel) {
		final Region region = province.getRegion();
		return getRandomSubregionAtLevel(region, regionLevel);
	}

	public BallotBoxResult getRandomResultForUser(final String userId) {

		Region randomBallotRegion = null;
		int randomVotingStationNumber = 1;

		final double diceRoll = ThreadLocalRandom.current().nextDouble(100);
		BallotBoxPersistedTally randomTally = null;

		if (diceRoll < REVERIFY_CHANCE_PERCENTAGE) {
			int attempt = 0;
			do {
				randomTally = BallotAccessService.getInstance()
						.getRandomTallyForReverify();
				attempt++;
			} while (randomTally.containsUser(userId) && attempt < MAX_REROLL);

			// if random tally still contains user, dump it
			if (randomTally.containsUser(userId)) {
				randomTally = null;
			}
		}

		if (randomTally != null) {
			LOG.info("Picking up tally #" + randomTally.getId()
					+ " with karma " + randomTally.getAdviceKarmaBalance()
					+ " for reverification.");

			final String tallyId = randomTally.getId();
			final List<String> splittedId = Splitter.on(
					BallotBoxResult.idSeparator).splitToList(tallyId);

			if (splittedId.size() == 2
					&& Ints.tryParse(splittedId.get(1)) != null) {

				randomBallotRegion = new Region();
				randomBallotRegion.setId(splittedId.get(0));
				randomBallotRegion.setLevel(RegionLevel.VILLAGE);
				randomVotingStationNumber = Integer.parseInt(splittedId.get(1));
			} else {
				LOG.warning("Tally ID ["
						+ tallyId
						+ "] cannot be split into proper ballot and voting number. Falling back to random picking.");
			}
		}

		// fallback
		if (randomBallotRegion == null) {
			LOG.info("Picking up random region for verification.");

			randomBallotRegion = getRandomBallotRegion();
			randomVotingStationNumber = getRandomVotingStationNumber(randomBallotRegion);
		}

		return getBallotBoxResult(randomBallotRegion, randomVotingStationNumber);
	}

	private Region getRandomSubregionAtLevel(final Region region,
			final RegionLevel regionLevel) {
		final List<Region> subregions = getSubregions(region);

		LOG.finer("Found subregions for region " + region + " are "
				+ subregions);

		final int sampledSubregionIndex = ThreadLocalRandom.current().nextInt(
				subregions.size());
		final Region sampledSubregion = subregions.get(sampledSubregionIndex);

		if (sampledSubregion.getLevel() == regionLevel) {
			return sampledSubregion;
		} else {
			return getRandomSubregionAtLevel(sampledSubregion, regionLevel);
		}
	}

	private int getRandomVotingStationNumber(final Region region) {
		int numberOfVotingStation = getNumberOfVotingStation(region);
		return ThreadLocalRandom.current().nextInt(numberOfVotingStation) + 1;
	}

	private List<Region> getSubregions(final Region region) {
		return RegionAccessService.getInstance().getSubregions(region);
	}

	public BallotBoxPersistedTally getTally(final String ballotId) {
		return BallotAccessService.getInstance().getTally(ballotId);
	}

	private int getTotalProcessedCount() {
		final int singleAdviceTallyCount = ofy().load()
				.type(BallotBoxPersistedTally.class)
				.filter("numberOfAdvices", 1).count();

		final int doubleAdviceTallyCount = ofy().load()
				.type(BallotBoxPersistedTally.class)
				.filter("numberOfAdvices", 2).count();

		final int tripleAdviceTallyCount = ofy().load()
				.type(BallotBoxPersistedTally.class)
				.filter("numberOfAdvices", 3).count();

		final int quadrupleAdviceTallyCount = ofy().load()
				.type(BallotBoxPersistedTally.class)
				.filter("numberOfAdvices", 4).count();

		final int manyAdviceTallyCount = ofy().load()
				.type(BallotBoxPersistedTally.class)
				.filter("numberOfAdvices >", 4).count();

		int totalCount = singleAdviceTallyCount;
		totalCount += doubleAdviceTallyCount * 2;
		totalCount += tripleAdviceTallyCount * 3;
		totalCount += quadrupleAdviceTallyCount * 4;
		totalCount += manyAdviceTallyCount * 4;

		return totalCount;
	}

	private int getTotalSuspectedNegativeCount() {
		return ofy().load().type(BallotBoxPersistedTally.class)
				.filter("adviceKarmaBalance <", 0).count();
	}

	private int getTotalVerifiedNegativeCount() {
		return ofy().load().type(BallotBoxPersistedTally.class)
				.filter("adviceKarmaBalance <", -1).count(); // TODO: Set to -2
																// once we have
																// more data
	}

	private boolean mergeUserInputToTally(final String userId,
			final BallotBoxUserInput input, final BallotBoxPersistedTally tally) {
		if (tally.containsUserAdvice(userId, input.getAdviceType())) {
			return false;
		}

		tally.addAdvice(new BallotBoxPersistedAdvice(userId, input
				.getAdviceType()));

		return true;
	}

	/**
	 * Advice already contains decrypted and matching ID with the ballot result.
	 * 
	 * @param advice
	 */
	public void processAdviceFromUser(final BallotBoxUserInput input,
			final String userId) {

		BallotBoxPersistedTally tally = BallotAccessService.getInstance()
				.getTallyForInput(input);

		if (tally == null) {
			tally = new BallotBoxPersistedTally(input.getId());
		}

		boolean isChanged = mergeUserInputToTally(userId, input, tally);

		if (isChanged) {
			BallotAccessService.getInstance().storeTally(tally);
		}
	}
}
