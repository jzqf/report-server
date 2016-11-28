package com.qfree.obo.report.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.ReportParameterRepository;
import com.qfree.obo.report.db.SelectionListValueRepository;
import com.qfree.obo.report.domain.ReportParameter;
import com.qfree.obo.report.domain.SelectionListValue;
import com.qfree.obo.report.dto.ReportParameterResource;
import com.qfree.obo.report.dto.RestErrorResource.RestError;
import com.qfree.obo.report.dto.SelectionListValueResource;
import com.qfree.obo.report.exceptions.RestApiException;
import com.qfree.obo.report.util.RestUtils;

@Component
@Transactional
public class SelectionListValueService {

	private static final Logger logger = LoggerFactory.getLogger(SelectionListValueService.class);

	private final SelectionListValueRepository selectionListValueRepository;
	private final ReportParameterRepository reportParameterRepository;

	@Autowired
	public SelectionListValueService(
			SelectionListValueRepository selectionListValueRepository,
			ReportParameterRepository reportParameterRepository) {
		this.selectionListValueRepository = selectionListValueRepository;
		this.reportParameterRepository = reportParameterRepository;
	}

	@Transactional
	public SelectionListValue saveNewFromResource(SelectionListValueResource selectionListValueResource) {

		RestUtils.ifNewResourceIdNotNullThen403(selectionListValueResource.getSelectionListValueId(),
				SelectionListValue.class,
				"selectionListValueId", selectionListValueResource.getSelectionListValueId());

		RestUtils.ifAttrNullThen403(selectionListValueResource.getValueAssigned(), SelectionListValue.class,
				"valueAssigned");
		RestUtils.ifAttrNullThen403(selectionListValueResource.getValueDisplayed(), SelectionListValue.class,
				"valueDisplayed");
		RestUtils.ifAttrNullThen403(selectionListValueResource.getOrderIndex(), SelectionListValue.class, "orderIndex");

		/*
		 * Not only must selectionListValueResource.getReportParameterResource()
		 * be not null, this ReportParameterResource must have an id so that the
		 * new SelectionListValue entity will be linked to a ReportParameter
		 * entity.
		 */
		RestUtils.ifAttrNullThen403(selectionListValueResource.getReportParameterResource(), SelectionListValue.class,
				"reportParameter");
		RestUtils.ifAttrNullThen403(selectionListValueResource.getReportParameterResource().getReportParameterId(),
				ReportParameter.class, "reportParameterId");

		return saveOrUpdateFromResource(selectionListValueResource);
	}

	@Transactional
	public SelectionListValue saveExistingFromResource(SelectionListValueResource selectionListValueResource) {
		return saveOrUpdateFromResource(selectionListValueResource);
	}

	@Transactional
	public SelectionListValue saveOrUpdateFromResource(SelectionListValueResource selectionListValueResource) {
		logger.debug("selectionListValueResource = {}", selectionListValueResource);

		/*
		 * IMPORTANT:
		 * 
		 * Retrieve the ReportParameterResource from selectionListValueResource.
		 * We assume here that the reportParameterId attribute of this object is
		 * set to the id of the ReportParameter that will we associated with the
		 * SelectionListValue entity that is be saved/created below. It is not 
		 * necessary for any on the other ReportParameterResource attributes to 
		 * have non-null values.
		 * 
		 * If reportParameterId is not provided here, we throw a custom 
		 * exception. 
		 */
		ReportParameterResource reportParameterResource = selectionListValueResource.getReportParameterResource();
		logger.debug("reportParameterResource = {}", reportParameterResource);
		UUID reportParameterId = null;
		if (reportParameterResource != null) {
			reportParameterId = reportParameterResource.getReportParameterId();
		}
		logger.debug("reportParameterId = {}", reportParameterId);
		ReportParameter reportParameter = null;
		if (reportParameterId != null) {
			reportParameter = reportParameterRepository.findOne(reportParameterId);
			RestUtils.ifNullThen404(reportParameter, ReportParameter.class, "reportParameterId",
					reportParameterId.toString());
		} else {
			throw new RestApiException(RestError.FORBIDDEN_SELECTIONLISTVALUE_REPORTPARAMETER_NULL,
					SelectionListValue.class, "reportParameterId");
		}

		SelectionListValue selectionListValue = new SelectionListValue(selectionListValueResource, reportParameter);
		logger.debug("selectionListValue = {}", selectionListValue);
		/*
		 * This "save" method will persist or merge the given entity using the
		 * underlying JPA EntityManager. If the entity has not been persisted 
		 * yet, Spring Data JPA will save the entity via a call to the 
		 * entityManager.persist(...) method; otherwise, the 
		 * entityManager.merge(...) method will be called. But since the id of
		 * this entity is not set above, currently this will always save a new
		 * SelectionListValue.
		 */
		selectionListValue = selectionListValueRepository.save(selectionListValue);
		logger.debug("selectionListValue (created/updated) = {}", selectionListValue);

		return selectionListValue;
	}
}
