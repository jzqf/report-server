package com.qfree.obo.report.security;

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
	 * fail).
	 */
	private final Boolean active;

	public ReportServerUser(
			UUID roleId,
			String username, String password,
			boolean enabled, boolean active,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, true, true, true, authorities);
		this.roleId = roleId;
		this.active = active;
	}

	public UUID getRoleId() {
		return roleId;
	}

	public Boolean isActive() {
		return active;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReportServerUser [roleId=");
		builder.append(roleId);
		builder.append(", active=");
		builder.append(active);
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
