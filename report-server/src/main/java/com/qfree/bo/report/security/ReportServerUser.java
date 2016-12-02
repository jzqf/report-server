package com.qfree.bo.report.security;

import java.util.Collection;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * Holds the "principal" associated with an authenticated connection.
 * <p>
 * This class stores additional information about the authenticated user beyond
 * that supported by {@link User}.
 * <p>
 * All fields of this class are available to the @PreAuthorize annotation that
 * is used for method-level security. An object of this class is accessed using
 * the identifier "principal", e.g., the "roleId" field is exposed with
 * "principal.roleId".
 * 
 * @author Jeffrey Zelt
 *
 */
public class ReportServerUser extends User {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(ReportServerUser.class);

	/**
	 * The value of Role.roleId for the authenticated user.
	 * 
	 * This is used in @PreAuthorize statements to authorize, e.g., that the
	 * authenticated user updating his/her own Role record, even if the user
	 * does not have the "MANAGE_ROLES" authority.
	 */
	private final UUID roleId;

	/**
	 * The value of Role.active for the authenticated user.
	 * 
	 * If this is false, the request should be refused (authentication should
	 * fail). This means that this field should ideally <b>never</b> be false,
	 * which in turn means that there is no real need to introduce this field at
	 * all. The only reason that this field has been included is in case there
	 * is one day introduced a mechanism to bypass the requirement that the Role
	 * is "active"; in that case, it could be useful to know if a user has been
	 * authenticated via an inactive Role. It could also be the case that the
	 * value of this field needs to be checked <i>before</i> the connection is
	 * authenticated, but currently this is not the case.
	 * 
	 * The same observation also applies to the "enabled" field, but it is
	 * inherited from {@link User} and, therefore is not documented in this
	 * class.
	 */
	private final Boolean active;

	/**
	 * The value of Role.loginRole for the authenticated user.
	 * 
	 * If this is false, the request should be refused (authentication should
	 * fail). This means that this field should ideally <b>never</b> be false,
	 * which in turn means that there is no real need to introduce this field at
	 * all. The only reason that this field has been included is in case there
	 * is one day introduced a mechanism to bypass the requirement that the Role
	 * is a "login" Role; in that case, it could be useful to know if a user has
	 * been authenticated via a non-login Role. It could also be the case that
	 * the value of this field needs to be checked <i>before</i> the connection
	 * is authenticated, but currently this is not the case.
	 */
	private final Boolean loginRole;

	public ReportServerUser(
			UUID roleId,
			String username, String password,
			boolean enabled, boolean active, boolean loginRole,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, true, true, true, authorities);
		this.roleId = roleId;
		this.active = active;
		this.loginRole = loginRole;
	}

	public UUID getRoleId() {
		return roleId;
	}

	public Boolean isActive() {
		return active;
	}

	public Boolean isLoginRole() {
		return loginRole;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReportServerUser [roleId=");
		builder.append(roleId);
		builder.append(", active=");
		builder.append(active);
		builder.append(", loginRole=");
		builder.append(loginRole);
		builder.append(", getAuthorities()=");
		builder.append(getAuthorities());
		builder.append(", getUsername()=");
		builder.append(getUsername());
		builder.append(", isEnabled()=");
		builder.append(isEnabled());
		builder.append("]");
		return builder.toString();
	}
}
