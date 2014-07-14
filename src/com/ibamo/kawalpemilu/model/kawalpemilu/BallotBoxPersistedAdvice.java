package com.ibamo.kawalpemilu.model.kawalpemilu;

import java.util.Objects;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.googlecode.objectify.annotation.Container;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.condition.IfNotZero;

public class BallotBoxPersistedAdvice {
	@Index
	private String advisorUserId;

	@Index({ IfNotZero.class })
	private int adviceTypeCode = -1;

	private String customAdvice;

	@Container
	private BallotBoxPersistedTally containerTally;

	@SuppressWarnings("unused")
	private BallotBoxPersistedAdvice() {
	}

	public BallotBoxPersistedAdvice(final String userId,
			final AdviceType adviceType) {
		this.advisorUserId = userId;
		this.adviceTypeCode = adviceType.getPersistenceCode();
	}

	@Override
	public boolean equals(final Object compared) {
		if (!(compared instanceof BallotBoxPersistedAdvice)) {
			return false;
		}

		final BallotBoxPersistedAdvice comparedAdvice = (BallotBoxPersistedAdvice) compared;
		return Objects.equals(advisorUserId, comparedAdvice.advisorUserId)
				&& Objects
						.equals(adviceTypeCode, comparedAdvice.adviceTypeCode)
				&& Objects.equals(customAdvice, comparedAdvice.customAdvice);
	}

	public int getAdviceTypeCode() {
		return adviceTypeCode;
	}

	public String getAdvisorUserId() {
		return advisorUserId;
	}

	public String getCustomAdvice() {
		return customAdvice;
	}

	@JsonIgnore
	public BallotBoxPersistedTally getParentTally() {
		return containerTally;
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		if (advisorUserId != null) {
			hashCode *= advisorUserId.hashCode();
		}

		if (customAdvice != null) {
			hashCode *= customAdvice.hashCode();
		}

		return hashCode * adviceTypeCode;
	}
}
