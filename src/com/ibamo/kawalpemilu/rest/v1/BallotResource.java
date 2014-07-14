package com.ibamo.kawalpemilu.rest.v1;

import java.util.Collection;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.appengine.api.users.UserServiceFactory;
import com.ibamo.kawalpemilu.model.kawalpemilu.BallotAccessor;
import com.ibamo.kawalpemilu.model.kawalpemilu.BallotBoxPersistedTally;
import com.ibamo.kawalpemilu.model.kawalpemilu.BallotBoxUserInput;
import com.ibamo.kawalpemilu.model.kawalpemilu.BallotBoxResult;
import com.ibamo.kawalpemilu.model.kawalpemilu.GlobalBallotStats;
import com.ibamo.kawalpemilu.service.SecurityService;
import com.ibamo.kawalpemilu.service.ballot.BallotService;

@Path("/ballots")
public class BallotResource implements BallotAccessor {

	private static Logger LOG = Logger
			.getLogger(BallotResource.class.getName());

	public static final String USER_ID_COOKIE_NAME = "uid";

	@Context
	private HttpServletRequest contextRequest;

	@Context
	private HttpServletResponse contextResponse;

	private String ensureUserIdExist(final HttpServletRequest request,
			final HttpServletResponse response) {
		String cookieUserId = extractUserId(request);
		if (cookieUserId == null) {
			cookieUserId = SecurityService.getInstance().generateUserId();
			setUserIdToCookieResponse(cookieUserId, response);
		}

		return cookieUserId;
	}
	
	private String extractUserId(final HttpServletRequest httpRequest) {
		Cookie[] requestCookies = httpRequest.getCookies();
		if (requestCookies == null) {
			return null;
		}

		for (Cookie cookie : httpRequest.getCookies()) {
			if (cookie.getName().equals(USER_ID_COOKIE_NAME)) {
				return cookie.getValue();
			}
		}

		return null;
	}

	@GET
	@Path("/tallies")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<BallotBoxPersistedTally> getAllBallotTallies()
			throws IllegalAccessException {

		if (!hasAccessRights()) {
			throw new IllegalAccessException(
					"Only admins can access this resource.");
		}

		return BallotService.getInstance().getAllTallies();
	}

	@GET
	@Path("/{ballotId}/tally")
	@Produces(MediaType.APPLICATION_JSON)
	public BallotBoxPersistedTally getBallotTally(
			@PathParam("ballotId") final String ballotId)
			throws IllegalAccessException {

		if (!hasAccessRights()) {
			throw new IllegalAccessException(
					"Only admins can access this resource.");
		}

		return BallotService.getInstance().getTally(ballotId);
	}

	@GET
	@Path("/stats")
	@Produces(MediaType.APPLICATION_JSON)
	public GlobalBallotStats getGlobalBallotStats() {
		return BallotService.getInstance().getGlobalBallotStats();
	}

	@GET
	@Path("/random")
	@Produces(MediaType.APPLICATION_JSON)
	public BallotBoxResult getRandomResult() {
		final String userId = ensureUserIdExist(contextRequest, contextResponse);
		final BallotBoxResult result = BallotService.getInstance()
				.getRandomResult();

		final String nonce = SecurityService.getInstance()
				.generateNonceForResult(result, userId);

		result.setNonce(nonce);

		BallotService.getInstance().encryptBallotId(result);

		return result;
	}

	private boolean hasAccessRights() {
		return UserServiceFactory.getUserService().isUserLoggedIn()
				&& UserServiceFactory.getUserService().isUserAdmin();
	}

	private void setUserIdToCookieResponse(final String cookieUserId,
			final HttpServletResponse response) {
		response.addCookie(new Cookie(USER_ID_COOKIE_NAME, cookieUserId));
	}

	@POST
	@Path("/submit/ids/{ballotId}")
	public Response submitBallotAdvice(
			@PathParam("ballotId") final String ballotId,
			final BallotBoxUserInput advice) {

		final String userId = extractUserId(contextRequest);
		if (userId == null) {
			// silently reject
			LOG.warning("Advice " + advice + " received for ballot id "
					+ ballotId + ", but user id is missing. Rejecting advice.");

			return Response.noContent().build();
		}

		// decrypt the ballot id
		final String decryptedId = BallotService.getInstance().decryptBallotId(
				ballotId);
		advice.setId(decryptedId);

		final boolean isRequestValid = SecurityService.getInstance()
				.validateAdviceFromUser(advice, userId);

		if (!isRequestValid) {
			// silently reject
			LOG.warning("Advice "
					+ advice
					+ " received for ballot id "
					+ ballotId
					+ ", but nonce indicates that the advice is not valid. Rejecting advice.");

			return Response.noContent().build();
		}

		BallotService.getInstance().processAdviceFromUser(advice, userId);

		return Response.noContent().build();
	}
}
