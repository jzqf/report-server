package com.qfree.bo.report.rest.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.bo.report.db.AuthorityRepository;
import com.qfree.bo.report.domain.Authority;
import com.qfree.bo.report.dto.AuthorityCollectionResource;
import com.qfree.bo.report.dto.AuthorityResource;
import com.qfree.bo.report.dto.ResourcePath;
import com.qfree.bo.report.util.RestUtils;
import com.qfree.bo.report.util.RestUtils.RestApiVersion;

@Component
@Path(ResourcePath.AUTHORITIES_PATH)
public class AuthorityController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(AuthorityController.class);

	private final AuthorityRepository authorityRepository;

	@Autowired
	public AuthorityController(
			AuthorityRepository authorityRepository) {
		this.authorityRepository = authorityRepository;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X GET -iH "Accept: application/json;v=1" http://localhost:8080/rest/authorities?expand=authorities
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown.
	 */
	@GET
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_AUTHORITIES + "')")
	public AuthorityCollectionResource getList(
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			//@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			//@QueryParam(ResourcePath.PAGE_OFFSET_QP_NAME) final List<String> pageOffset,
			//@QueryParam(ResourcePath.PAGE_LIMIT_QP_NAME) final List<String> pageLimit,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		//queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		//RestUtils.checkPaginationQueryParams(pageOffset, pageLimit, queryParams);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		List<Authority> authorities = null;
		/*
		 * Only active Authority entities are returned in the 
		 * AuthorityCollectionResource. We don't let inactive ones be returned,
		 * even if the "showAll" query parameter is used.
		 */
		//if (RestUtils.FILTER_INACTIVE_RECORDS && !ResourcePath.showAll(Authority.class, showAll)) {
		authorities = authorityRepository.findByActiveTrue();
		//} else {
		//	authorities = authorityRepository.findAll();
		//}
		return new AuthorityCollectionResource(authorities, Authority.class, uriInfo, queryParams, apiVersion);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X GET -iH "Accept: application/json;v=1" \
	 *   http://localhost:8080/rest/authorities/c7f1d394-9814-4ede-bb01-2700187d79ca
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown.
	 */
	@Path("/{id}")
	@GET
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_AUTHORITIES + "')")
	public AuthorityResource getById(
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
			addToExpandList(expand, Authority.class);
		}
		Authority authority = authorityRepository.findOne(id);
		RestUtils.ifNullThen404(authority, Authority.class, "authorityId", id.toString());
		AuthorityResource authorityResource = new AuthorityResource(authority, uriInfo, queryParams, apiVersion);
		return authorityResource;
	}
}
