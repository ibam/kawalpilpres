package com.ibamo.kawalpemilu.model.kawalpemilu;

import java.util.HashSet;
import java.util.Set;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
@Cache
public class BallotBoxPersistedTally {

	@Id
	private String ballotId;

	private Set<BallotBoxPersistedAdvice> advices = new HashSet<>();

	@Index
	private int numberOfAdvices;

	@Index
	private int numberOfPositiveAdvices;

	@Index
	private int numberOfNegativeAdvices;

	@Index
	private int adviceKarmaBalance;

	@SuppressWarnings("unused")
	private BallotBoxPersistedTally() {
	}

	public BallotBoxPersistedTally(final String ballotId) {
		this.ballotId = ballotId;
	}

	public String getId() {
		return ballotId;
	}

	public int getNumberOfAdvices() {
		return numberOfAdvices;
	}

	public int getNumberOfPositiveAdvices() {
		return numberOfPositiveAdvices;
	}

	public int getNumberOfNegativeAdvices() {
		return numberOfNegativeAdvices;
	}

	public int getAdviceKarmaBalance() {
		return adviceKarmaBalance;
	}

	public Set<BallotBoxPersistedAdvice> getAdvices() {
		return advices;
	}

	public boolean hasAdvices() {
		return !getAdvices().isEmpty();
	}

	public boolean containsUserAdvice(final String userId,
			final AdviceType adviceType) {

		for (BallotBoxPersistedAdvice advice : getAdvices()) {
			if (advice.getAdvisorUserId().equals(userId)
					&& advice.getAdviceTypeCode() == adviceType
							.getPersistenceCode()) {
				return true;
			}
		}

		return false;
	}

	public void addAdvice(
			final BallotBoxPersistedAdvice ballotBoxPersistedAdvice) {
		getAdvices().add(ballotBoxPersistedAdvice);
		syncAdviceNumbers();
	}

	private void syncAdviceNumbers() {
		numberOfAdvices = getAdvices().size();
		numberOfPositiveAdvices = 0;
		numberOfNegativeAdvices = 0;

		for (BallotBoxPersistedAdvice advice : getAdvices()) {
			if (advice.getAdviceTypeCode() == AdviceType.LOOKS_GOOD
					.getPersistenceCode()) {
				numberOfPositiveAdvices++;
			} else {
				numberOfNegativeAdvices++;
			}
		}

		adviceKarmaBalance = numberOfPositiveAdvices - numberOfNegativeAdvices;
	}
}
