package com.qfree.obo.report.rest.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.api.IParameterDefn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.ReportParameterRepository;
import com.qfree.obo.report.domain.ReportParameter;
import com.qfree.obo.report.dto.ReportParameterResource;
import com.qfree.obo.report.dto.ReportParameterResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.dto.SelectionListValueCollectionResource;
import com.qfree.obo.report.exceptions.DynamicSelectionListKeyException;
import com.qfree.obo.report.exceptions.RptdesignOpenFromStreamException;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;
import com.qfree.obo.report.service.ReportParameterService;

@Component
@Path(ResourcePath.REPORTPARAMETERS_PATH)
public class ReportParameterController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(ReportParameterController.class);

	private final ReportParameterRepository reportParameterRepository;
	private final ReportParameterService reportParameterService;

	@Autowired
	public ReportParameterController(
			ReportParameterRepository reportParameterRepository,
			ReportParameterService reportParameterService) {
		this.reportParameterRepository = reportParameterRepository;
		this.reportParameterService = reportParameterService;
	}

	///*
	// * This endpoint is commented out because there should be no need to 
	// * retrieve *all* ReportParameter's via the ReST API. Instead, the 
	// * ReportVersionController can be used to retrieve all report parameters
	// * assiciated with a single ReportVersion entity. Or, the endpoint below
	// * associated with the getById(...) method can be used to retrieve a single
	// * specified ReportParameter.
	// * 
	// * This endpoint can be tested with:
	// * 
	// *   $ mvn clean spring-boot:run
	// *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	// *   http://localhost:8080/rest/reportParameters?expand=reportParameters
	// * 
	// * @Transactional is used to avoid org.hibernate.LazyInitializationException
	// * being thrown when evaluating reportParameter.getSelectionListValues().
	// */
	//@Transactional
	//@GET
	//@Produces(MediaType.APPLICATION_JSON)
	////	public List<ReportParameterResource> getList(
	//public ReportParameterCollectionResource getList(
	//		@HeaderParam("Accept") final String acceptHeader,
	//		@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
	//		@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
	//		@Context final UriInfo uriInfo) {
	//	Map<String, List<String>> queryParams = new HashMap<>();
	//	queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
	//	queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
	//	RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
	//
	//	List<ReportParameter> reportParameters = null;
	//	//if (RestUtils.FILTER_INACTIVE_RECORDS && !ResourcePath.showAll(ReportParameter.class, showAll)) {
	//	//	reportParameters = reportParameterRepository.findByActiveTrue();
	//	//} else {
	//	reportParameters = reportParameterRepository.findAll();
	//	//}
	//	List<ReportParameterResource> reportParameterResources = new ArrayList<>(reportParameters.size());
	//	for (ReportParameter reportParameter : reportParameters) {
	//		reportParameterResources
	//				.add(new ReportParameterResource(reportParameter, uriInfo, queryParams, apiVersion));
	//	}
	//	//		return reportParameterResources;
	//	return new ReportParameterCollectionResource(reportParameterResources, ReportParameter.class, uriInfo,
	//			queryParams,
	//			apiVersion);
	//}

	/*
	 * This endpoint is commented out because there should be no need to create
	 * a ReportParameter via the ReST API. Instead, the ReportParameter's will
	 * only be created when a ReportVersion is uploaded/POSTed, at which time
	 * the rptdesign file will be parsed to extract the report parameters. See
	 * ReportVersionController where this is done.
	 * 
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X POST -d \
	 *   '{...}' \
	 *   http://localhost:8080/rest/reportParameters
	 */
	//	@POST
	//	@Consumes(MediaType.APPLICATION_JSON)
	//	@Produces(MediaType.APPLICATION_JSON)
	//	public Response create(
	//			ReportParameterResource reportParameterResource,
	//			@HeaderParam("Accept") final String acceptHeader,
	//			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
	//			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
	//			@Context final UriInfo uriInfo) {
	//		Map<String, List<String>> queryParams = new HashMap<>();
	//		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
	//		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
	//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
	//
	//		ReportParameter reportParameter = reportParameterService.saveNewFromResource(reportParameterResource);
	//		if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
	//			addToExpandList(expand, ReportParameter.class);
	//		}
	//		ReportParameterResource resource = new ReportParameterResource(reportParameter, uriInfo, queryParams,
	//				apiVersion);
	//		return created(resource);
	//	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	 *   http://localhost:8080/rest/reportParameters/c7f1d394-9814-4ede-bb01-2700187d79ca
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown when evaluating reportParameter.getSelectionListValues().
	 */
	@Transactional
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ReportParameterResource getById(
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
			addToExpandList(expand, ReportParameter.class);
		}
		ReportParameter reportParameter = reportParameterRepository.findOne(id);
		RestUtils.ifNullThen404(reportParameter, ReportParameter.class, "reportId", id.toString());
		ReportParameterResource reportParameterResource = new ReportParameterResource(reportParameter, uriInfo,
				queryParams, apiVersion);
		return reportParameterResource;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X PUT -d \
	 *   '{"reportVersion":{"reportVersionId":"7ff1da11-a229-45c5-90be-79417a628125"},\
	 *   "orderIndex":999,\
	 *   "controlType":0,\
	 *   "promptText":"New prompt text",\
	 *   "required":false,\
	 *   "defaultValue":"new default value",\
	 *   "displayName":"new display name",\
	 *   "helpText":"new help text",\
	 *   "displayFormat":"new display format",\
	 *   "alignment":3,\
	 *   "allowNewValues":true,\
	 *   "parameterGroup":{"parameterGroupId":"abc81f46-3940-44bb-a401-d937c55a10bc"}}' \
	 *   http://localhost:8080/rest/reportParameters/fecd8f20-5ff0-4b0f-ae0c-0829cea1c938
	 * 
	 * All UUID values must be replaced here with actual resource id's from the
	 * report server database, of course.
	 * 
	 * If the ReportParameter to be updated is not linked to a ParameterGroup,
	 * then the "parameterGroup" attribute in the PUT data should be omitted
	 * entirely.
	 */
	@Path("/{id}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateById(
			ReportParameterResource reportParameterResource,
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final ServletContext servletContext,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		/*
		 * Retrieve ReportParameter entity to be updated.
		 */
		ReportParameter reportParameter = reportParameterRepository.findOne(id);
		RestUtils.ifNullThen404(reportParameter, ReportParameter.class, "reportId", id.toString());

		/*
		 * Ensure that some of the entity's attributes are not changed. These 
		 * attributes should not appear in the PUT data, but if they do, their
		 * value will not be used because it will be overridden here.
		 */
		reportParameterResource.setReportParameterId(reportParameter.getReportParameterId());
		reportParameterResource.setName(reportParameter.getName());
		reportParameterResource.setDataType(reportParameter.getDataType());
		reportParameterResource.setMultivalued(reportParameter.getMultivalued());
		reportParameterResource.setHidden(reportParameter.getHidden());
		reportParameterResource.setValueConcealed(reportParameter.getValueConcealed());
		reportParameterResource.setDisplayInFixedOrder(reportParameter.getDisplayInFixedOrder());
		reportParameterResource.setParameterType(reportParameter.getParameterType());
		reportParameterResource.setAutoSuggestThreshold(reportParameter.getAutoSuggestThreshold());
		reportParameterResource.setSelectionListType(reportParameter.getSelectionListType());
		reportParameterResource.setValueExpr(reportParameter.getValueExpr());
		reportParameterResource.setCreatedOn(reportParameter.getCreatedOn());

		/*
		 * Save updated entity.
		 */
		reportParameter = reportParameterService.saveExistingFromResource(reportParameterResource);

		return Response.status(Response.Status.OK).build();
	}

	/*
	 * Return the SelectionListValue's associated with a single ReportParameter 
	 * that is specified by its id. This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	 *   http://localhost:8080/rest/reportParameters/0955ab6e-6ce6-4b90-a60c-aa0548e4d358/selectionListValues\
	 *   ?expand=selectionListValues
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown when evaluating reportParameter.getSelectionListValues().
	 */
	@Transactional
	@Path("/{id}" + ResourcePath.SELECTIONLISTVALUES_PATH)
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SelectionListValueCollectionResource getSelectionListValuesByReportParameterId(
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@QueryParam(ResourcePath.PARENTPARAMVALUE_QP_NAME) final List<String> parentParamValues,
			@Context final UriInfo uriInfo)
					throws RptdesignOpenFromStreamException, BirtException, DynamicSelectionListKeyException {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		ReportParameter reportParameter = reportParameterRepository.findOne(id);
		RestUtils.ifNullThen404(reportParameter, ReportParameter.class, "reportParameterId", id.toString());

		/*
		 * Create a SelectionListValueCollectionResource to represent the 
		 * selection list values. If the selection list is dynamic, i.e., it
		 * must be generated by running a query that is represented by a data 
		 * source specified in the rptdesign file, then it is necessary to use
		 * the BIRT API to request these values.
		 * 
		 * If, on the other hand, the selection list is static or there is no
		 * selection list at all, then it is a simple matter of creating a
		 * SelectionListValueCollectionResource from SelectionListValue entities
		 * stored in the report server database. 
		 */
		SelectionListValueCollectionResource selectionListValueCollectionResource = null;
		if (reportParameter.getSelectionListType().equals(IParameterDefn.SELECTION_LIST_DYNAMIC)) {
			/*
			 * The selection list is dynamic. 
			 */
			logger.info("dynamicListKeys = {}", parentParamValues);
			String rptdesign = reportParameter.getReportVersion().getRptdesign();
			selectionListValueCollectionResource = reportParameterService.getDynamicSelectionList(
					reportParameter, parentParamValues, rptdesign, uriInfo, queryParams, apiVersion);
		} else {
			/*
			 * Either the selection list is static or there is no selection list
			 * at all. It is a simple matter of returning a 
			 * SelectionListValueCollectionResource based on SelectionListValue 
			 * entities stored in the report server database that are linked to
			 * the specified report parameter.
			 */
			selectionListValueCollectionResource = new SelectionListValueCollectionResource(
					reportParameter, uriInfo, queryParams, apiVersion);
		}
		return selectionListValueCollectionResource;
	}
}
