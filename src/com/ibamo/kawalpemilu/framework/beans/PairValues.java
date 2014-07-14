package com.ibamo.kawalpemilu.framework.beans;

public class PairValues<A, B> {

	private A first;
	private B second;

	public PairValues(final A first, final B second) {
		this.first = first;
		this.second = second;
	}

	public A getFirst() {
		return first;
	}

	public B getSecond() {
		return second;
	}
}
