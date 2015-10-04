package com.qfree.obo.report.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.validator.constraints.NotBlank;

import com.qfree.obo.report.dto.JobStatusResource;
import com.qfree.obo.report.util.DateUtils;

/**
 * The persistent class for the "job_status" database table.
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "job_status", schema = "reporting")
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class JobStatus implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * These UUID values are stored in the report server database as JobStatus
	 * primary and foreign key values. Therefore, they should never be changed
	 * or deleted.
	 */
	public static final UUID QUEUED_ID = UUID.fromString("08de9764-735f-4c82-bbe9-3981b29cc133");
	public static final UUID RUNNING_ID = UUID.fromString("a613aae2-836a-4b03-a75d-cfb8303eaad5");
	public static final UUID COMPLETED_ID = UUID.fromString("f378fc09-35e4-4096-b1d1-2db14756b098");
	public static final UUID FAILED_ID = UUID.fromString("2a9cd697-af00-45bc-aa6a-053284b9d9e4");

	@Id
	//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//	@Column(name = "job_status_id", unique = true, nullable = false)
	//	private Long jobStatusId;
	@NotNull
	@Type(type = "uuid-custom")
	//	@Type(type = "pg-uuid")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "job_status_id", unique = true, nullable = false,
			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	private UUID jobStatusId;

	@NotBlank
	@Column(name = "abbreviation", nullable = false, length = 32)
	private String abbreviation;

	@NotBlank
	@Column(name = "description", nullable = false, length = 32)
	private String description;

	@NotNull
	@Column(name = "active", nullable = false)
	private Boolean active;

	//	@OneToMany(targetEntity = Job.class, mappedBy = "jobStatus")
	//	private List<Job> jobs;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	private JobStatus() {
	}

	public JobStatus(String description, String abbreviation) {
		this(null, description, abbreviation, true, DateUtils.nowUtc());
	}

	public JobStatus(String description, String abbreviation, Boolean active) {
		this(null, description, abbreviation, active, DateUtils.nowUtc());
	}

	public JobStatus(String description, String abbreviation, Boolean active, Date createdOn) {
		this(null, description, abbreviation, active, createdOn);
	}

	public JobStatus(JobStatusResource jobStatusResource) {
		this(
				jobStatusResource.getJobStatusId(),
				jobStatusResource.getDescription(),
				jobStatusResource.getAbbreviation(),
				jobStatusResource.isActive(),
				//				(jobStatusResource.isActive() != null) ? jobStatusResource.isActive() : true,
				jobStatusResource.getCreatedOn());
	}

	public JobStatus(UUID jobStatusId, String description, String abbreviation, Boolean active, Date createdOn) {
		this.jobStatusId = jobStatusId;
		this.description = description;
		this.abbreviation = abbreviation;
		this.active = (active != null) ? active : true;
		this.createdOn = (createdOn != null) ? createdOn : DateUtils.nowUtc();
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	//	public List<Job> getJobs() {
	//		return jobs;
	//	}
	//
	//	public void setJobs(List<Job> jobs) {
	//		this.jobs = jobs;
	//	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public UUID getJobStatusId() {
		return jobStatusId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JobStatus [jobStatusId=");
		builder.append(jobStatusId);
		builder.append(", abbreviation=");
		builder.append(abbreviation);
		builder.append(", description=");
		builder.append(description);
		builder.append(", active=");
		builder.append(active);
		//		builder.append(", jobs=");
		//		builder.append(jobs);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}

}
