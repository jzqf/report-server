package com.qfree.bo.report.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qfree.bo.report.db.JobRepository;
import com.qfree.bo.report.db.RoleRepository;
import com.qfree.bo.report.db.SubscriptionRepository;
import com.qfree.bo.report.domain.Job;
import com.qfree.bo.report.domain.Role;
import com.qfree.bo.report.domain.Subscription;

@Component(value = "dos")
public class DomainObjectSecurityService {

	private static final Logger logger = LoggerFactory.getLogger(DomainObjectSecurityService.class);

	private final RoleRepository roleRepository;
	//	private final ReportRepository reportRepository;
	//	private final ReportVersionRepository reportVersionRepository;
	private final SubscriptionRepository subscriptionRepository;
	private final JobRepository jobRepository;

	@Autowired
	public DomainObjectSecurityService(
			RoleRepository roleRepository,
			//	ReportRepository reportRepository,
			//	ReportVersionRepository reportVersionRepository,
			SubscriptionRepository subscriptionRepository,
			JobRepository jobRepository) {
		this.roleRepository = roleRepository;
		//	this.reportRepository = reportRepository;
		//	this.reportVersionRepository = reportVersionRepository;
		this.subscriptionRepository = subscriptionRepository;
		this.jobRepository = jobRepository;
	}

	public boolean isQfreeAdmin(UUID principalRoleId) {
		return Role.QFREE_ADMIN_ROLE_ID.equals(principalRoleId);
	}

	public Role role(UUID roleId) {
		return roleRepository.findOne(roleId);
	}

	/**
	 * Returns true if the Role entity specified by its id is "owned" by the
	 * Role with id=principalRoleId
	 * 
	 * In practice, this means that these two Roles are the same. "Ownership"
	 * makes more sense for the other entity types treated by this class.
	 * 
	 * This method is used in @PreAuthorize SpEL expressions. It guarantees that
	 * there will not be a nullPointerExpression if no Role exists for roleId.
	 * 
	 * @param roleId
	 * @param principalRoleId
	 * @return
	 */
	public boolean ownsRole(UUID roleId, UUID principalRoleId) {
		Role role = roleRepository.findOne(roleId);
		if (role != null) {
			return role.getRoleId().equals(principalRoleId);
		} else {
			return true;
		}
	}

	//	public Report report(UUID reportId) {
	//		return reportRepository.findOne(reportId);
	//	}
	//
	//	public ReportVersion reportVersion(UUID reportVersionId) {
	//		return reportVersionRepository.findOne(reportVersionId);
	//	}

	public Subscription subscription(UUID subscriptionId) {
		return subscriptionRepository.findOne(subscriptionId);
	}

	/**
	 * Returns true if the Subscription entity specified by its id is "owned" by
	 * the Role with id=principalRoleId
	 * 
	 * This method is used in @PreAuthorize SpEL expressions. It guarantees that
	 * there will not be a nullPointerExpression if no Subscription exists for
	 * subscriptionId.
	 * 
	 * @param subscriptionId
	 * @param principalRoleId
	 * @return
	 */
	public boolean ownsSubscription(UUID subscriptionId, UUID principalRoleId) {
		Subscription subscription = subscriptionRepository.findOne(subscriptionId);
		if (subscription != null) {
			return subscription.getRole().getRoleId().equals(principalRoleId);
		} else {
			return true;
		}
	}

	public Job job(Long jobId) {
		return jobRepository.findOne(jobId);
	}

	/**
	 * Returns true if the Job entity specified by its id is "owned" by the Role
	 * with id=principalRoleId
	 * 
	 * This method is used in @PreAuthorize SpEL expressions. It guarantees that
	 * there will not be a nullPointerExpression if no Job exists for jobId.
	 * 
	 * @param jobId
	 * @param principalRoleId
	 * @return
	 */
	public boolean ownsJob(Long jobId, UUID principalRoleId) {
		Job job = jobRepository.findOne(jobId);
		if (job != null) {
			return job.getRole().getRoleId().equals(principalRoleId);
		} else {
			return true;
		}
	}
}
