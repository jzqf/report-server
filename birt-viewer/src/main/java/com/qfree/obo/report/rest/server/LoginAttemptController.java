package com.qfree.obo.report.rest.server;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qfree.obo.report.db.RoleRepository;
import com.qfree.obo.report.domain.Role;
import com.qfree.obo.report.dto.RestErrorResource.RestError;
import com.qfree.obo.report.dto.RoleResource;
import com.qfree.obo.report.exceptions.RestApiException;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;
import com.qfree.obo.report.service.RoleService;

@Component
@Path("loginAttempts")
//@Path(ResourcePath.ROLES_PATH)
public class LoginAttemptController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(LoginAttemptController.class);

	private final RoleRepository roleRepository;
	private final RoleService roleService;

	@Autowired
	public LoginAttemptController(
			RoleRepository roleRepository,
			RoleService roleService) {
		this.roleRepository = roleRepository;
		this.roleService = roleService;
	}

	//	/*
	//	 * This endpoint can be tested with:
	//	 * 
	//	 *   $ mvn clean spring-boot:run
	//	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	//	 *   http://localhost:8080/rest/roles
	//	 */
	//	@GET
	//	@Produces(MediaType.APPLICATION_JSON)
	//	public List<RoleResource> getList(
	//			@HeaderParam("Accept") final String acceptHeader,
	//			@QueryParam("expand") final List<String> expand,
	//			@Context final UriInfo uriInfo) {
	//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
	//
	//		// List<Role> roles = roleRepository.findByActiveTrue();
	//		List<Role> roles = roleRepository.findAll();
	//		List<RoleResource> roleResources = new ArrayList<>(roles.size());
	//		for (Role role : roles) {
	//			roleResources.add(new RoleResource(role, uriInfo, expand, apiVersion));
	//		}
	//		return roleResources;
	//	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" \
	 *     -X POST -d '{"username":"user1","encodedPassword":"44rSFJQ9qtHWTBAvrsKd5K/p2j0="}' \
	 *     http://localhost:8080/rest/loginAttempts
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RoleResource authenticate(
			RoleResource roleResource,
			@HeaderParam("Accept") final String acceptHeader,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
		logger.info("apiVersion = {}", apiVersion);
		/*
		 * Ensure that both the user name and encoded password have been 
		 * supplied.
		 */
		if (roleResource.getUsername() != null && !roleResource.getUsername().isEmpty() &&
				roleResource.getEncodedPassword() != null && !roleResource.getEncodedPassword().isEmpty()) {
			logger.debug("roleResource.getUsername() = {}, roleResource.getEncodedPassword() = {}",
					roleResource.getUsername(), roleResource.getEncodedPassword());
			Role role = roleRepository.findByUsername(roleResource.getUsername());
			RestUtils.ifNullThen404(role, Role.class, "username", roleResource.getUsername());
			if (role.isLoginRole()) {
				logger.debug("role = {}", role);
				if (role.getEncodedPassword().equals(roleResource.getEncodedPassword())) {

					List<String> expand = newExpandList(Role.class);  // Force primary resource to be "expanded"
					RoleResource resource = new RoleResource(role, uriInfo, expand, apiVersion);
					return resource;

				} else {
					throw new RestApiException(RestError.FORBIDDEN_BAD_ROLE_PASSWORD,
							Role.class, "encodedPassword", roleResource.getEncodedPassword());
				}
			} else {
				String message = String.format("Role for username '%s' does not have 'login' privilege",
						roleResource.getUsername());
				throw new RestApiException(RestError.FORBIDDEN_NOT_LOGIN_ROLE, message,
						Role.class, "loginRole", new Boolean(role.isLoginRole()).toString());
			}
		} else {
			throw new RestApiException(RestError.NOT_FOUND_ROLE_TO_AUTHENTICATE, Role.class);
		}
	}

	//	/*
	//	 * This endpoint can be tested with:
	//	 * 
	//	 *   $ mvn clean spring-boot:run
	//	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	//	 *   http://localhost:8080/rest/roles/0db97c2a-fb78-464a-a0e7-8d25f6003c14
	//	 */
	//	@Path("/{id}")
	//	@GET
	//	@Produces(MediaType.APPLICATION_JSON)
	//	public RoleResource getById(
	//			@PathParam("id") final UUID id,
	//			@HeaderParam("Accept") final String acceptHeader,
	//			@QueryParam("expand") final List<String> expand,
	//			@Context final UriInfo uriInfo) {
	//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
	//
	//	if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
	//		addToExpandList(expand, Role.class);
	//	}
	//		Role role = roleRepository.findOne(id);
	//		RestUtils.ifNullThen404(role, Role.class, "roleId", id.toString());
	//		RoleResource roleResource =
	//				new RoleResource(role, uriInfo, expand, apiVersion);
	//		return roleResource;
	//	}

	//	/*
	//	 * This endpoint can be tested with:
	//	 * 
	//	 *   $ mvn clean spring-boot:run
	//	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X PUT -d \
	//	 *   '{"username":"baaa (modified)","fullName":"Mr. baaa","encodedPassword":"qwerty=","loginRole":true}' \
	//	 *   http://localhost:8080/rest/roles/0db97c2a-fb78-464a-a0e7-8d25f6003c14
	//	 */
	//	@Path("/{id}")
	//	@PUT
	//	@Consumes(MediaType.APPLICATION_JSON)
	//	@Produces(MediaType.APPLICATION_JSON)
	//	public Response updateById(
	//			RoleResource roleResource,
	//			@PathParam("id") final UUID id,
	//			@HeaderParam("Accept") final String acceptHeader,
	//			@Context final UriInfo uriInfo) {
	//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
	//		logger.debug("apiVersion = {}", apiVersion);
	//		logger.debug("roleResource = {}", roleResource);
	//
	//		/*
	//		 * Retrieve Role entity to be updated.
	//		 */
	//		Role role = roleRepository.findOne(id);
	//		RestUtils.ifNullThen404(role, Role.class, "roleId", id.toString());
	//		logger.debug("role (to be updated) = {}", role);
	//		/*
	//		 * Ensure that the entity's "id" and "CreatedOn" are not changed.
	//		 */
	//		roleResource.setRoleId(role.getRoleId());
	//		roleResource.setCreatedOn(role.getCreatedOn());
	//		logger.debug("roleResource (adjusted) = {}", roleResource);
	//		/*
	//		 * Save updated entity.
	//		 */
	//		role = roleService.saveOrUpdateFromResource(roleResource);
	//		logger.debug("role (after saveOrUpdateFromResource) = {}", role);
	//		return Response.status(Response.Status.OK).build();
	//	}

}
