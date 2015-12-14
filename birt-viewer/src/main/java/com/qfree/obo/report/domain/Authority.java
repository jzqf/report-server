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

	/*
	 * Primary keys of the built-in "authorities".
	 */
	public static final UUID AUTHORITY_UUID_MANAGE_AUTHORITIES = UUID
			.fromString("1e4f29b9-3183-4f54-a4ee-96c2347d7e06");
	public static final UUID AUTHORITY_UUID_MANAGE_CATEGORIES = UUID.fromString("dae0f68f-11c6-438c-8312-aca4d95731fc");
	public static final UUID AUTHORITY_UUID_MANAGE_FILEFORMATS = UUID
			.fromString("cd2c5d93-9b57-4a8b-b789-84dd567e0fa2");
	public static final UUID AUTHORITY_UUID_MANAGE_FILESYNCING = UUID
			.fromString("7e1ee8bb-3d5d-481a-b0f9-5c3fae3f4e38");
	public static final UUID AUTHORITY_UUID_MANAGE_JOBPROCESSOR = UUID
			.fromString("e5b569e6-bb2a-4f00-a9ca-f85ef0a3a339");
	public static final UUID AUTHORITY_UUID_MANAGE_JOBS = UUID.fromString("12b1cb22-7686-4c9b-b73b-a25d4cb31663");
	public static final UUID AUTHORITY_UUID_DELETE_JOBS = UUID.fromString("ace1edd3-6a5b-4b40-a802-79616472b89");
	public static final UUID AUTHORITY_UUID_MANAGE_JOBSTATUSES = UUID
			.fromString("bb8f7964-504a-4544-8638-11a62cc9a2ca");
	public static final UUID AUTHORITY_UUID_MANAGE_PREFERENCES = UUID
			.fromString("1bbc9e73-1095-4471-bdb2-726b10e47936");
	public static final UUID AUTHORITY_UUID_MANAGE_REPORTS = UUID.fromString("7b758de7-cd54-43fa-baa0-dfbe59e66000");
	public static final UUID AUTHORITY_UUID_UPLOAD_REPORTS = UUID.fromString("2efd4eca-bcb2-4cec-b804-3142c8297d65");
	public static final UUID AUTHORITY_UUID_MANAGE_ROLES = UUID.fromString("2dac7af0-ba7d-4009-a313-e9a288272e90");
	public static final UUID AUTHORITY_UUID_MANAGE_SUBSCRIPTIONS = UUID
			.fromString("608d6156-b155-487d-bdd3-4e00260b7443");
	public static final UUID AUTHORITY_UUID_DELETE_SUBSCRIPTIONS = UUID
			.fromString("94db0a84-e366-4ab8-aeba-171482979f3d");

	public static final UUID AUTHORITY_UUID_RUN_DIAGNOSTICS = null;

	/*
	 * Names of the built-in "authorities".
	 * 
	 * These names are used in @Secured annotations.
	 */
	public static final String AUTHORITY_NAME_MANAGE_AUTHORITIES = "MANAGE_AUTHORITIES";
	public static final String AUTHORITY_NAME_MANAGE_FILEFORMATS = "MANAGE_FILEFORMATS";
	public static final String AUTHORITY_NAME_MANAGE_FILESYNCING = "MANAGE_FILESYNCING";
	public static final String AUTHORITY_NAME_MANAGE_CATEGORIES = "MANAGE_CATEGORIES";
	public static final String AUTHORITY_NAME_MANAGE_JOBPROCESSOR = "MANAGE_JOBPROCESSOR";
	public static final String AUTHORITY_NAME_MANAGE_JOBS = "MANAGE_JOBS";
	public static final String AUTHORITY_NAME_DELETE_JOBS = "DELETE_JOBS";
	public static final String AUTHORITY_NAME_MANAGE_JOBSTATUSES = "MANAGE_JOBSTATUSES";
	public static final String AUTHORITY_NAME_MANAGE_PREFERENCES = "MANAGE_PREFERENCES";
	public static final String AUTHORITY_NAME_MANAGE_REPORTS = "MANAGE_REPORTS";
	public static final String AUTHORITY_NAME_UPLOAD_REPORTS = "UPLOAD_REPORTS";
	public static final String AUTHORITY_NAME_MANAGE_ROLES = "MANAGE_ROLES";
	public static final String AUTHORITY_NAME_MANAGE_SUBSCRIPTIONS = "MANAGE_SUBSCRIPTIONS";
	public static final String AUTHORITY_NAME_DELETE_SUBSCRIPTIONS = "DELETE_SUBSCRIPTIONS";

	public static final String AUTHORITY_NAME_RUN_DIAGNOSTICS = "RUN_DIAGNOSTICS";

	//	/**
	//	 * Enum to hold details associated with "authorities" so that they are not
	//	 * hardwired into the code.
	//	 * 
	//	 * @author jeffreyz
	//	 *
	//	 */
	//	public enum SecurityAuthority {
	//
	//		MANAGE_CATEGORIES(
	//				Authority.AUTHORITY_UUID_MANAGE_CATEGORIES,
	//				Authority.AUTHORITY_NAME_MANAGE_CATEGORIES),
	//		MANAGE_JOBS(
	//				Authority.AUTHORITY_UUID_MANAGE_JOBS,
	//				Authority.AUTHORITY_NAME_MANAGE_JOBS),
	//		DELETE_JOBS(
	//				Authority.AUTHORITY_UUID_DELETE_JOBS,
	//				Authority.AUTHORITY_NAME_DELETE_JOBS),
	//		MANAGE_REPORTS(
	//				Authority.AUTHORITY_UUID_MANAGE_REPORTS,
	//				Authority.AUTHORITY_NAME_MANAGE_REPORTS),
	//		UPLOAD_REPORTS(
	//				Authority.AUTHORITY_UUID_UPLOAD_REPORTS,
	//				Authority.AUTHORITY_NAME_UPLOAD_REPORTS),
	//		MANAGE_ROLES(
	//				Authority.AUTHORITY_UUID_MANAGE_ROLES,
	//				Authority.AUTHORITY_NAME_MANAGE_ROLES),
	//		MANAGE_SUBSCRIPTIONS(
	//				Authority.AUTHORITY_UUID_MANAGE_SUBSCRIPTIONS,
	//				Authority.AUTHORITY_NAME_MANAGE_SUBSCRIPTIONS),
	//		DELETE_SUBSCRIPTIONS(
	//				Authority.AUTHORITY_UUID_DELETE_SUBSCRIPTIONS,
	//				Authority.AUTHORITY_NAME_DELETE_SUBSCRIPTIONS);
	//
	//		private final UUID uuid;
	//		private final String name;
	//
	//		private SecurityAuthority(UUID path, String expandParam) {
	//			this.uuid = path;
	//			this.name = expandParam;
	//		}
	//
	//		public UUID getUuid() {
	//			return uuid;
	//		}
	//
	//		public String getName() {
	//			return name;
	//		}
	//	}

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
