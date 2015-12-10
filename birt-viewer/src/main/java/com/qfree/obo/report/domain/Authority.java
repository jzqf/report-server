package com.qfree.obo.report.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.validator.constraints.NotBlank;

import com.qfree.obo.report.dto.AuthorityResource;
import com.qfree.obo.report.util.DateUtils;

/**
 * The persistent class for the "authority" database table.
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "authority", schema = "reporting",
		uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }, name = "uc_authority_name") })
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class Authority implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//	@Column(name = "authority_id", unique = true, nullable = false)
	//	private Long authorityId;
	@NotNull
	@Type(type = "uuid-custom")
	//	@Type(type = "pg-uuid")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "authority_id", unique = true, nullable = false,
			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	private UUID authorityId;

	@NotBlank
	@Column(name = "name", nullable = false, length = 50)
	private String name;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting an Authority will delete all of its RoleAuthority's.
	 */
	@OneToMany(mappedBy = "authority", cascade = CascadeType.ALL)
	private List<RoleAuthority> roleAuthorities;

	@NotNull
	@Column(name = "active", nullable = false)
	private Boolean active;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	private Authority() {
	}

	public Authority(String name, Boolean active) {
		this(null, name, active, DateUtils.nowUtc());
	}

	public Authority(String name, Boolean active, Date createdOn) {
		this(null, name, active, createdOn);
	}

	public Authority(AuthorityResource authorityResource) {
		this(
				authorityResource.getAuthorityId(),
				authorityResource.getName(),
				authorityResource.isActive(),
				authorityResource.getCreatedOn());
	}

	public Authority(UUID authorityId, String name, Boolean active, Date createdOn) {
		super();
		this.authorityId = authorityId;
		this.name = name;
		this.active = (active != null) ? active : true;
		this.createdOn = (createdOn != null) ? createdOn : DateUtils.nowUtc();
	}

	public UUID getAuthorityId() {
		return this.authorityId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Date getCreatedOn() {
		return this.createdOn;
	}

	public List<RoleAuthority> getRoleAuthorities() {
		return roleAuthorities;
	}

	public void setRoleAuthorities(List<RoleAuthority> roleAuthorities) {
		this.roleAuthorities = roleAuthorities;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Authority [authorityId=");
		builder.append(authorityId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", active=");
		builder.append(active);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}
}
