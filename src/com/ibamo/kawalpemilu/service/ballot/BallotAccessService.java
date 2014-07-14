package com.ibamo.kawalpemilu.service.ballot;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.Collection;
import java.util.logging.Logger;

import com.ibamo.kawalpemilu.model.kawalpemilu.BallotBoxPersistedTally;
import com.ibamo.kawalpemilu.model.kawalpemilu.BallotBoxUserInput;

public class BallotAccessService {
	private static Logger LOG = Logger.getLogger(BallotAccessService.class
			.getName());

	private static class SingletonHolder {
		private static final BallotAccessService INSTANCE = new BallotAccessService();
	}

	public static BallotAccessService getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private BallotAccessService() {
	}

	public BallotBoxPersistedTally getTallyForInput(
			final BallotBoxUserInput input) {
		final String tallyId = extractTallyId(input);
		return getTally(tallyId);
	}

	public BallotBoxPersistedTally getTally(final String id) {
		return ofy().load().type(BallotBoxPersistedTally.class).id(id).now();
	}

	private String extractTallyId(final BallotBoxUserInput input) {
		return input.getId();
	}

	public void storeTally(final BallotBoxPersistedTally tally) {
		ofy().save().entity(tally);
	}

	public Collection<BallotBoxPersistedTally> getAllTallies() {
		return ofy().load().type(BallotBoxPersistedTally.class).list();
	}

}
