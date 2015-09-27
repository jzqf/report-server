package com.qfree.obo.report.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.DocumentFormatRepository;
import com.qfree.obo.report.db.ReportVersionRepository;
import com.qfree.obo.report.db.RoleRepository;
import com.qfree.obo.report.db.SubscriptionRepository;
import com.qfree.obo.report.domain.DocumentFormat;
import com.qfree.obo.report.domain.ReportVersion;
import com.qfree.obo.report.domain.Role;
import com.qfree.obo.report.domain.Subscription;
import com.qfree.obo.report.domain.SubscriptionParameter;
import com.qfree.obo.report.domain.SubscriptionParameterValue;
import com.qfree.obo.report.dto.DocumentFormatResource;
import com.qfree.obo.report.dto.ReportVersionResource;
import com.qfree.obo.report.dto.RestErrorResource.RestError;
import com.qfree.obo.report.dto.RoleResource;
import com.qfree.obo.report.dto.SubscriptionResource;
import com.qfree.obo.report.exceptions.RestApiException;
import com.qfree.obo.report.rest.server.RestUtils;

@Component
@Transactional
public class SubscriptionService {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionService.class);

	private final SubscriptionRepository subscriptionRepository;
	private final DocumentFormatRepository documentFormatRepository;
	private final ReportVersionRepository reportVersionRepository;
	private final RoleRepository roleRepository;

	@Autowired
	public SubscriptionService(
			SubscriptionRepository subscriptionRepository,
			DocumentFormatRepository documentFormatRepository,
			ReportVersionRepository reportVersionRepository,
			RoleRepository roleRepository) {
		this.subscriptionRepository = subscriptionRepository;
		this.documentFormatRepository = documentFormatRepository;
		this.reportVersionRepository = reportVersionRepository;
		this.roleRepository = roleRepository;
	}

	@Transactional
	public Subscription saveNewFromResource(SubscriptionResource subscriptionResource) {

		RestUtils.ifNewResourceIdNotNullThen403(subscriptionResource.getSubscriptionId(),
				Subscription.class, "subscriptionId", subscriptionResource.getSubscriptionId());

		/*
		 * Do not allow Active=true for a new Subscription. Normally, additional 
		 * information must be specified before a subscription can be enabled. 
		 * If there is not sufficient information to allow the subscription to 
		 * be enabled and we did allow active=true to be specified, there is no 
		 * easy way to both create the Subscription entity AND return an error 
		 * message describing why the subscription could not be activated. 
		 */
		if (subscriptionResource.getActive() == null) {
			subscriptionResource.setActive(Boolean.FALSE);
		} else if (subscriptionResource.getActive()) {
			throw new RestApiException(RestError.FORBIDDEN_NEW_SUBSCRIPTION_ACTIVE, Subscription.class);
		}

		// /*
		// * Enforce NOT NULL constraints.
		// */
		// RestUtils.ifAttrNullThen403(subscriptionResource.getEmail(),
		// Subscription.class, "email");

		return saveOrUpdateFromResource(subscriptionResource);
	}

	@Transactional
	public Subscription saveExistingFromResource(SubscriptionResource subscriptionResource) {
		return saveOrUpdateFromResource(subscriptionResource);
	}

	@Transactional
	public Subscription saveOrUpdateFromResource(SubscriptionResource subscriptionResource) {
		logger.debug("subscriptionResource = {}", subscriptionResource);

		/*
		 * IMPORTANT:
		 * 
		 * Retrieve the DocumentFormatResource from subscriptionResource.
		 * We assume here that the documentFormatId attribute of this object is
		 * set to the id of the DocumentFormat that will we associated with the
		 * Subscription entity that is be saved/created below. It is not 
		 * necessary for any on the other DocumentFormatResource attributes to 
		 * have non-null values.
		 * 
		 * If documentFormatId is not provided here, we throw a custom 
		 * exception. 
		 */
		DocumentFormatResource documentFormatResource = subscriptionResource.getDocumentFormatResource();
		logger.debug("documentFormatResource = {}", documentFormatResource);
		UUID documentFormatId = documentFormatResource.getDocumentFormatId();
		logger.debug("documentFormatId = {}", documentFormatId);
		DocumentFormat documentFormat = null;
		if (documentFormatId != null) {
			documentFormat = documentFormatRepository.findOne(documentFormatId);
			RestUtils.ifNullThen404(documentFormat, DocumentFormat.class, "documentFormatId",
					documentFormatId.toString());
		} else {
			throw new RestApiException(RestError.FORBIDDEN_SUBSCRIPTION_DOCUMENTFORMAT_NULL,
					Subscription.class, "documentFormatId");
		}

		/*
		 * IMPORTANT:
		 * 
		 * Retrieve the ReportVersionResource from subscriptionResource.
		 * We assume here that the reportVersionId attribute of this object is
		 * set to the id of the ReportVersion that will we associated with the
		 * Subscription entity that is be saved/created below. It is not 
		 * necessary for any on the other ReportVersionResource attributes to 
		 * have non-null values.
		 * 
		 * If reportVersionId is not provided here, we throw a custom 
		 * exception. 
		 */
		ReportVersionResource reportVersionResource = subscriptionResource.getReportVersionResource();
		logger.debug("reportVersionResource = {}", reportVersionResource);
		UUID reportVersionId = reportVersionResource.getReportVersionId();
		logger.debug("reportVersionId = {}", reportVersionId);
		ReportVersion reportVersion = null;
		if (reportVersionId != null) {
			reportVersion = reportVersionRepository.findOne(reportVersionId);
			RestUtils.ifNullThen404(reportVersion, ReportVersion.class, "reportVersionId",
					reportVersionId.toString());
		} else {
			throw new RestApiException(RestError.FORBIDDEN_SUBSCRIPTION_REPORTVERSION_NULL,
					Subscription.class, "reportVersionId");
		}

		/*
		 * IMPORTANT:
		 * 
		 * Retrieve the RoleResource from subscriptionResource.
		 * We assume here that the roleId attribute of this object is
		 * set to the id of the Role that will we associated with the
		 * Subscription entity that is be saved/created below. It is not 
		 * necessary for any on the other RoleResource attributes to 
		 * have non-null values.
		 * 
		 * If roleId is not provided here, we throw a custom 
		 * exception. 
		 */
		RoleResource roleResource = subscriptionResource.getRoleResource();
		logger.debug("roleResource = {}", roleResource);
		UUID roleId = roleResource.getRoleId();
		logger.debug("roleId = {}", roleId);
		Role role = null;
		if (roleId != null) {
			role = roleRepository.findOne(roleId);
			RestUtils.ifNullThen404(role, Role.class, "roleId",
					roleId.toString());
		} else {
			throw new RestApiException(RestError.FORBIDDEN_SUBSCRIPTION_ROLE_NULL,
					Subscription.class, "roleId");
		}

		Subscription subscription = new Subscription(subscriptionResource, documentFormat, reportVersion, role);
		logger.debug("subscription = {}", subscription);

		/*
		 * This "save" method will persist or merge the given entity using the
		 * underlying JPA EntityManager. If the entity has not been persisted 
		 * yet, Spring Data JPA will save the entity via a call to the 
		 * entityManager.persist(...) method; otherwise, the 
		 * entityManager.merge(...) method will be called. But since the id of
		 * this entity is not set above, currently this will always save a new
		 * Subscription.
		 */
		subscription = subscriptionRepository.save(subscription);
		logger.debug("subscription (created/updated) = {}", subscription);

		return subscription;
	}

	/**
	 * Performs whatever actions are appropriate when a subscription is
	 * deactivated.
	 * 
	 * <p>
	 * The most important action is to disable any scheduling that is active for
	 * the subscription.
	 * 
	 * @param subscription
	 */
	public void deactivate(Subscription subscription) {

		// TODO Write me!

	}

	/**
	 * Performs whatever actions are appropriate when a subscription is
	 * activated.
	 * 
	 * <p>
	 * The most important action is to enable scheduling for the subscription,
	 * but this can only be done if certain details have been provided:
	 * 
	 * <ul>
	 * <li>There must be at least one {@link SubscriptionParameterValue} per
	 * {@link SubscriptionParameter}, and each
	 * {@link SubscriptionParameterValue} needs to have non-null values
	 * assigned.
	 * <li>There can be more than one {@link SubscriptionParameterValue} per
	 * {@link SubscriptionParameter}, but only if the associated
	 * {@link ReportParameter} has <code>multivalued=true</code>.
	 * <li>The {@link Subscription} must have a usable value for either
	 * <code>cronSchedule</code> or <code>runOnceAt</code>.
	 * <li>The {@link Subscription} must have a value for <code>email</code>.
	 * </ul>
	 * 
	 * @param subscription
	 */
	public void activate(Subscription subscription) {

		// TODO Write me!

	}
}
