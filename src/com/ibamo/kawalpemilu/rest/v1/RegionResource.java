package com.ibamo.kawalpemilu.rest.v1;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.appengine.api.users.UserServiceFactory;
import com.ibamo.kawalpemilu.model.kpu.PersistedRegion;
import com.ibamo.kawalpemilu.service.region.RegionDatastoreAccessService;

@Path("/regions")
public class RegionResource {

	private static Logger LOG = Logger
			.getLogger(RegionResource.class.getName());

	@GET
	@Path("/{regionId}")
	@Produces(MediaType.APPLICATION_JSON)
	public PersistedRegion getRegion(
			@PathParam("regionId") final String regionId)
			throws IllegalAccessException {

		if (!hasAccessRights()) {
			throw new IllegalAccessException(
					"Only admins can access this resource.");
		}

		return RegionDatastoreAccessService.getInstance().load(regionId);
	}

	private boolean hasAccessRights() {
		return UserServiceFactory.getUserService().isUserLoggedIn()
				&& UserServiceFactory.getUserService().isUserAdmin();
	}
}
