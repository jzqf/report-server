package com.qfree.obo.report.security;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.qfree.obo.report.db.RoleRepository;
import com.qfree.obo.report.domain.Configuration.ParamName;
import com.qfree.obo.report.service.ConfigurationService;

public class ReportServerAuthenticationProvider implements AuthenticationProvider {

	private static final Logger logger = LoggerFactory.getLogger(ReportServerAuthenticationProvider.class);

	//	@Autowired
	//	private HttpServletRequest httpServletRequest;

	//	@Autowired
	//	private ServletRequest servletRequest;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private ConfigurationService configurationService;

	public ReportServerAuthenticationProvider() {
		super();
	}

	@Override
	public Authentication authenticate(final Authentication authentication) throws AuthenticationException {

		//	System.out.println("\nIn CustomAuthenticationProvider.authenticate(...)!");
		//	System.out.println("authentication.getDetails() = " + authentication.getDetails());
		//	System.out.println("servletRequest = " + servletRequest);
		//	System.out.println("httpServletRequest = " + httpServletRequest);
		//	System.out.println("httpServletRequest.getPathInfo() = " + httpServletRequest.getPathInfo());
		//	System.out.println("httpServletRequest.getParameterMap() = " + httpServletRequest.getParameterMap());

		final String name = authentication.getName();
		final String password = authentication.getCredentials().toString();
		logger.info("name = {}, password = {}", name, password);

		/*
		 * I may want to check for local Role entities first? But
		 */

		if (name != null && !name.trim().isEmpty() && password != null && !password.trim().isEmpty()) {

			String authenticationProviderUrl = configurationService.get(ParamName.AUTHENTICATION_PROVIDER_URL,
					null, String.class);
			//			authenticationProviderUrl = "http://www.apple.com";

			if (authenticationProviderUrl != null && !authenticationProviderUrl.isEmpty()) {

				HttpAuthenticationFeature basicAuthenticationFeature = HttpAuthenticationFeature.basic(name, password);

				Client client = ClientBuilder.newBuilder()
						//					.register(basicAuthenticationFeature)
						.build();

				/*
				 * 8081: My local Tomcat server
				 */
				//	String port = "8081";
				//	WebTarget webTarget = client.target("http://localhost:" + port + "/report-server/rest");

				/*
				 * 8080: Embedded Tomcat server started with:
				 * 
				 *     $ mvn clean spring-boot:run
				 */
				WebTarget webTarget = client.target(authenticationProviderUrl);

				//	Response response = webTarget
				//			.path("reportCategories/7a482694-51d2-42d0-b0e2-19dd13bbbc64")
				//			.queryParam(ResourcePath.EXPAND_QP_NAME, ResourcePath.REPORTCATEGORY_EXPAND_PARAM)
				//			.request()
				//			.header("Accept", MediaType.APPLICATION_JSON + ";v=1")
				//			.get();
				//	logger.info("response.getStatus() = {}", response.getStatus());
				//	ReportCategoryResource resource = response.readEntity(ReportCategoryResource.class);
				//	//		String resource = response.readEntity(String.class);
				//	logger.info("resource = {}", resource);
				//	System.out.println("resource = " + resource);

				Invocation.Builder invocationBuilder = webTarget
						//.path("test/nop")
						.request();
				//.header("Accept", MediaType.TEXT_PLAIN);
				//.header("Accept", MediaType.TEXT_HTML);

				final long startTime = System.currentTimeMillis();
				Response response = invocationBuilder.head();
				//Response response = invocationBuilder.get();
				//	System.out.println("response.getStatus() = " + response.getStatus());
				//	String resource = response.readEntity(String.class);
				//	System.out.println("resource = " + resource);
				final long endTime = System.currentTimeMillis();
				logger.info("External authentication request used {} ms", endTime - startTime);

				logger.info("response.getStatus() = {}", response.getStatus());
				if (response.getStatus() == Response.Status.OK.getStatusCode()) {
					logger.info("Request PASSED authentication by: {}", authenticationProviderUrl);
				} else {
					logger.info("Request FAILED authentication by: {}", authenticationProviderUrl);
				}
			} else {
				logger.info("External authentication provider not configured.");
			}

			if (name.equals("ui") && password.equals("ui")) {
				//if (name.equals("admin") && password.equals("system")) {
				final List<GrantedAuthority> grantedAuths = new ArrayList<>();
				grantedAuths.add(new SimpleGrantedAuthority("ROLE_RESTAPI"));
				final UserDetails principal = new User(name, password, grantedAuths);
				final Authentication auth = new UsernamePasswordAuthenticationToken(principal, password, grantedAuths);
				return auth;
			}
		} else {
			logger.warn("The name and/or password is not valid. The connection cannot be authenticated."
					+ " name = {}, password = {}", name, password);
		}
		return null;
	}

	@Override
	public boolean supports(final Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
