package com.ibamo.kawalpemilu.model.kawalpemilu;

import org.codehaus.jackson.annotate.JsonIgnore;


public class BallotBoxUserInput {
	private String nonce;
	private String advice;
	private String id;
	
	public String getAdvice() {
		return advice;
	}

	public AdviceType getAdviceType() {
		return AdviceType.getAdviceType(getAdvice());
	}
	
	@JsonIgnore
	public String getId() {
		return id;
	}

	public String getNonce() {
		return nonce;
	}
	
	public void setAdvice(final String advice) {
		this.advice = advice;
	}

	@JsonIgnore
	public void setId(final String id) {
		this.id = id;
	}

	public void setNonce(final String nonce) {
		this.nonce = nonce;
	}

	public String toString() {
		return nonce + "|" + advice;
	}
}
