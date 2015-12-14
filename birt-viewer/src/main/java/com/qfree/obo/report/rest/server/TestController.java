package com.qfree.obo.report.rest.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import com.qfree.obo.report.domain.Authority;
import com.qfree.obo.report.domain.Configuration.ParamName;
import com.qfree.obo.report.scheduling.jobs.SubscriptionScheduledJob;
import com.qfree.obo.report.scheduling.schedulers.SubscriptionJobProcessorScheduler;
import com.qfree.obo.report.scheduling.schedulers.SubscriptionScheduler;
import com.qfree.obo.report.service.BirtService;
import com.qfree.obo.report.service.ConfigurationService;
import com.qfree.obo.report.util.RestUtils;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@Component
@Path("/test")
public class TestController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(TestController.class);

	private final ConfigurationService configurationService;
	private final BirtService birtService;

	private final SchedulerFactoryBean schedulerFactoryBean;
	//	private final SubscriptionJobProcessorScheduledJob subscriptionJobProcessorScheduledJob;
	private final SubscriptionJobProcessorScheduler subscriptionJobProcessorScheduler;
	private final SubscriptionScheduler subscriptionScheduler;

	@Autowired
	public TestController(
			ConfigurationService configurationService,
			BirtService birtService,
			SchedulerFactoryBean schedulerFactoryBean,
			//			SubscriptionJobProcessorScheduledJob subscriptionJobProcessorScheduledJob,
			SubscriptionJobProcessorScheduler subscriptionJobProcessorScheduler,
			SubscriptionScheduler subscriptionScheduler) {
		this.configurationService = configurationService;
		this.birtService = birtService;
		this.schedulerFactoryBean = schedulerFactoryBean;
		//		this.subscriptionJobProcessorScheduledJob = subscriptionJobProcessorScheduledJob;
		this.subscriptionJobProcessorScheduler = subscriptionJobProcessorScheduler;
		this.subscriptionScheduler = subscriptionScheduler;
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_RUN_DIAGNOSTICS + "')")
	public String test(@HeaderParam("Accept") String acceptHeader) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v2);
		switch (apiVersion) {
		case v1:
			/*
			 * Code for API v1:
			 */
			break;
		default:
			/*
			 * Code for default API version as well as unrecognized version from 
			 * "Accept" header:
			 */
		}
		return "/test endpoint: API version " + apiVersion.getVersion();
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: text/plain;v=1" -X GET http://localhost:8080/rest/test/api_version
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown when evaluating ...
	 */
	/**
	 * ReST endpoint that can be used to confirm that the API version is being
	 * specified correctly by a client.
	 * 
	 * The response entity is the version number specified in the request.
	 *  
	 * @param acceptHeader
	 * @return
	 */
	@GET
	@Path("/api_version")
	@Produces(MediaType.TEXT_PLAIN)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_RUN_DIAGNOSTICS + "')")
	public String acceptHeaderApiVersionGet(@HeaderParam("Accept") String acceptHeader) {
		//		logger.info("acceptHeader = {}", acceptHeader);
		//		System.out.println("acceptHeaderApiVersionGet: acceptHeader = " + acceptHeader);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v2);
		//		logger.info("apiVersion, apiVersion.getVersion() = {}, {}", apiVersion, apiVersion.getVersion());
		//		System.out.println("acceptHeaderApiVersionGet: apiVersion, apiVersion.getVersion() = "
		//				+ apiVersion + ", " + apiVersion.getVersion());
		return apiVersion.getVersion();
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: text/plain;v=1" -X POST http://localhost:8080/rest/test/api_version
	 */
	@POST
	@Path("/api_version")
	@Produces(MediaType.TEXT_PLAIN)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_RUN_DIAGNOSTICS + "')")
	public String acceptHeaderApiVersionPost(@HeaderParam("Accept") String acceptHeader) {
		//		logger.info("acceptHeader = {}", acceptHeader);
		//		System.out.println("acceptHeaderApiVersionPost: acceptHeader = " + acceptHeader);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v3);
		//		logger.info("apiVersion, apiVersion.getVersion() = {}, {}", apiVersion, apiVersion.getVersion());
		//		System.out.println("acceptHeaderApiVersionPost: apiVersion, apiVersion.getVersion() = "
		//				+ apiVersion + ", " + apiVersion.getVersion());
		return apiVersion.getVersion();
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: text/plain;v=1" -X POST http://localhost:8080/rest/test/api_version
	 */
	@PUT
	@Path("/api_version")
	@Produces(MediaType.TEXT_PLAIN)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_RUN_DIAGNOSTICS + "')")
	public String acceptHeaderApiVersionPut(@HeaderParam("Accept") String acceptHeader) {
		//		logger.info("acceptHeader = {}", acceptHeader);
		//		System.out.println("acceptHeaderApiVersionPost: acceptHeader = " + acceptHeader);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v4);
		//		logger.info("apiVersion, apiVersion.getVersion() = {}, {}", apiVersion, apiVersion.getVersion());
		//		System.out.println("acceptHeaderApiVersionPost: apiVersion, apiVersion.getVersion() = "
		//				+ apiVersion + ", " + apiVersion.getVersion());
		return apiVersion.getVersion();
	}

	@POST
	@Path("/form")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_RUN_DIAGNOSTICS + "')")
	public String formPostProduceText(
			@HeaderParam("Accept") String acceptHeader,
			@FormParam("param1") String param1,
			@FormParam("param2") String param2) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v2);
		return "(" + param1 + ", " + param2 + "): " + apiVersion;
	}

	@GET
	@Path("/string_param_default")
	@Produces(MediaType.TEXT_PLAIN)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_RUN_DIAGNOSTICS + "')")
	public String getTestStringParamDefault(@HeaderParam("Accept") String acceptHeader) {
		//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v2);
		//		Object stringValueDefaultObject = configurationService.get(ParamName.TEST_STRING);
		//		String stringParam = null;
		//		if (stringValueDefaultObject instanceof String) {
		//			stringParam = (String) stringValueDefaultObject;
		//		}
		//		return stringParam;
		return configurationService.get(ParamName.TEST_STRING, null, String.class);
	}

	@GET
	@Path("/string_param_default")
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_RUN_DIAGNOSTICS + "')")
	public String getTestStringParamDefaultAsJson(@HeaderParam("Accept") String acceptHeader) {
		//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v2);
		String stringValue = configurationService.get(ParamName.TEST_STRING, null, String.class);


		//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
		//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
		//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX


		return configurationService.get(ParamName.TEST_STRING, null, String.class);
	}

	@POST
	@Path("/string_param_default")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_RUN_DIAGNOSTICS + "')")
	public String postTestStringParamDefault(
			@HeaderParam("Accept") String acceptHeader,
			@FormParam("paramValue") String newParamValue) {
		//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v2);
		/*
		 * Update parameter's default value.
		 */
		configurationService.set(ParamName.TEST_STRING, newParamValue);
		/*
		 * Return updated value.
		 */
		return configurationService.get(ParamName.TEST_STRING, null, String.class);
	}

	@PUT
	@Path("/string_param_default")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_RUN_DIAGNOSTICS + "')")
	public String putTestStringParamDefault(
			@HeaderParam("Accept") String acceptHeader,
			String newParamValue) {
		//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v2);
		/*
		 * Update parameter's default value.
		 */
		configurationService.set(ParamName.TEST_STRING, newParamValue);
		/*
		 * Return updated value.
		 */
		return configurationService.get(ParamName.TEST_STRING, null, String.class);
	}

	//TODO USE @PUT TO RETURN A JSON object?????????????????

	//TODO USE @PUT TO accept a JSON object, e.g., a new Configuration and then later a new Role?
	//		Insert into DB and then RETURN A JSON object?????????????????

	@GET
	@Path("/parse_report_params")
	@Produces(MediaType.TEXT_PLAIN)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_RUN_DIAGNOSTICS + "')")
	public String parseReportParamsTest(
			@HeaderParam("Accept") final String acceptHeader,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		try {

			/*
			 * Load rptdesign file into a String.
			 */
			//java.nio.file.Path rptdesignPath = Paths
			//		.get("/home/jeffreyz/git/obo-birt-reports/birt-reports/tests/400-TestReport04_v1.1.rptdesign");
			java.nio.file.Path rptdesignPath = Paths.get("/home/jeffreyz/Desktop/cascade_v3.2.23.rptdesign");
			//java.nio.file.Path rptdesignPath = Paths.get("/home/jeffreyz/Desktop/cascade_v3.2.6.rptdesign");
			List<String> rptdesignLines = null;
			try {
				rptdesignLines = Files.readAllLines(rptdesignPath);// assumes UTF-8 encoding
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String rptdesignXml = String.join("\n", rptdesignLines);
			//logger.info("rptdesignXml = \n{}", rptdesignXml);

			//ReportUtils.parseReportParams(rptdesignXml);
			birtService.parseReportParams(rptdesignXml);

		} catch (Exception e) {
			logger.error("Parsing the report parameters failed with the following exception:", e);
		}

		return "Please work!!!";
	}

	@GET
	@Path("/scheduleTask")
	@Produces(MediaType.TEXT_PLAIN)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_RUN_DIAGNOSTICS + "')")
	public String scheduleTask(
			@HeaderParam("Accept") final String acceptHeader,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		/*
		 * Get the underlying Quartz Scheduler. According to the Javadoc for
		 * org.springframework.scheduling.quartz.SchedulerFactoryBean :
		 * 
		 *   For dynamic registration of jobs at runtime, use a bean reference 
		 *   to this SchedulerFactoryBean to get direct access to the Quartz 
		 *   Scheduler (org.quartz.Scheduler). This allows you to create new 
		 *   jobs and triggers, and also to control and monitor the entire 
		 *   Scheduler.
		 * 
		 * So it seems that in order to schedule jobs dynamically (which is what
		 * we are doing here), one *must* use the Quartz Scheduler object
		 * obtained here.
		 */
		Scheduler scheduler = schedulerFactoryBean.getScheduler();

		String complexJobGroupName = "ReportSubscriptions";
		String complexJobName = "complexSubscriptionUUID";

		UUID subscriptionId = UUID.randomUUID();

		Map<String, Object> jobDataMap = new HashMap<>();
		jobDataMap.put("subscriptionId", subscriptionId);

		/*
		 * Also set a field to be a singleton bean that is injected into the
		 * current context. This bean should be keep track of which subscriptions
		 * have been scheduled, which are active, paused, etc. Try this approach
		 * instead of using static maps/lists. This bean will still need to be
		 * thread-safe - therefore, check the Javadoc for data structures (maps,
		 * lists, sets,...) that are inherently thread-safe? Or...
		 * 
		 * Name this class SubscriptionScheduleManager / SubscriptionScheduleService?
		 */

		JobDetailFactoryBean complexJobDetail = new JobDetailFactoryBean();
		complexJobDetail.setJobClass(SubscriptionScheduledJob.class);
		complexJobDetail.setJobDataAsMap(jobDataMap);
		complexJobDetail.setDurability(true); // ?????????????????????????????????????
		complexJobDetail.setGroup(complexJobGroupName);
		complexJobDetail.setName(complexJobName);
		complexJobDetail.afterPropertiesSet();

		/*
		 * Create trigger for complexJobDetail.
		 */
		CronTriggerFactoryBean cronTrigger = new CronTriggerFactoryBean();
		cronTrigger.setJobDetail(complexJobDetail.getObject());
		cronTrigger.setName("cronTriggerCreatedByJeff");
		cronTrigger.setStartDelay(1000L);
		cronTrigger.setCronExpression("0/5 * * ? * MON-FRI"); // Run the job every 5 seconds only on weekdays
		try {
			cronTrigger.afterPropertiesSet();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		try {
			scheduler.scheduleJob(complexJobDetail.getObject(), cronTrigger.getObject());
		} catch (SchedulerException e) {
			e.printStackTrace();
		}

		try {
			logger.info("scheduler.getTriggerGroupNames() = {}", scheduler.getTriggerGroupNames());
		} catch (SchedulerException e) {
			e.printStackTrace();
		}

		try {
			Set<JobKey> jobKeySet = scheduler.getJobKeys(GroupMatcher.anyGroup());
			logger.debug("scheduler.getJobKeys(GroupMatcher.anyGroup()) = {}", jobKeySet);
			JobKey[] jobKeys = jobKeySet.toArray(new JobKey[] {});
			logger.info("{} JobKeys:", jobKeys.length);
			for (JobKey jobKey : jobKeys) {
				logger.info("    {}", jobKey);
			}
		} catch (SchedulerException e1) {
			e1.printStackTrace();
		}

		try {
			logger.info("scheduler.isStarted() = {}", scheduler.isStarted());
		} catch (SchedulerException e) {
			e.printStackTrace();
		}

		return "Return something here after scheduling the task?";
	}

	//	@GET
	//	@Path("/scheduleJobProcessor")
	//	@Produces(MediaType.TEXT_PLAIN)
	//	public String scheduleJobProcessor(
	//			@HeaderParam("Accept") final String acceptHeader,
	//			@Context final UriInfo uriInfo) throws SchedulerException, ClassNotFoundException, NoSuchMethodException, JobProcessorAlreadyScheduledException {
	//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
	//
	//		logger.info("Scheduling job processor");
	//		subscriptionJobProcessorScheduler.scheduleJob();
	//
	//		return "Job processor scheduled";
	//	}

	//	@GET
	//	@Path("/triggerJobProcessor")
	//	@Produces(MediaType.TEXT_PLAIN)
	//	public String triggerJobProcessor(
	//			@HeaderParam("Accept") final String acceptHeader,
	//			@Context final UriInfo uriInfo) throws SchedulerException, JobProcessorSchedulerNotRunningCannotTrigger, JobProcessorNotScheduledCannotTrigger {
	//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
	//
	//		logger.info("Triggering job processor...");
	//		subscriptionJobProcessorScheduler.triggerJob();
	//
	//		return "Triggered job processor";
	//	}

	//	@GET
	//	@Path("/pauseJobProcessor")
	//	@Produces(MediaType.TEXT_PLAIN)
	//	public String pauseJobProcessor(
	//			@HeaderParam("Accept") final String acceptHeader,
	//			@Context final UriInfo uriInfo) throws SchedulerException, JobProcessorNotScheduledCannotPause, JobProcessorSchedulerNotRunningCannotPause {
	//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
	//
	//		logger.info("Pausing job processor");
	//		subscriptionJobProcessorScheduler.pauseJob();
	//
	//		return "Paused job processor";
	//	}

	//	@GET
	//	@Path("/resumeJobProcessor")
	//	@Produces(MediaType.TEXT_PLAIN)
	//	public String resumeJobProcessor(
	//			@HeaderParam("Accept") final String acceptHeader,
	//			@Context final UriInfo uriInfo) throws SchedulerException, JobProcessorNotScheduledCannotResume, JobProcessorSchedulerNotRunningCannotResume {
	//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
	//
	//		logger.info("Resuming job processor...");
	//		subscriptionJobProcessorScheduler.resumeJob();
	//
	//		return "Resumed job processor";
	//	}

	//	@GET
	//	@Path("/unscheduleJobProcessor")
	//	@Produces(MediaType.TEXT_PLAIN)
	//	public String unscheduleJobProcessor(
	//			@HeaderParam("Accept") final String acceptHeader,
	//			@Context final UriInfo uriInfo) throws SchedulerException, JobProcessorNotScheduledCannotStop {
	//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
	//
	//		//		logger.info("schedulerFactoryBean.stop()");
	//		//		schedulerFactoryBean.stop();
	//		//
	//		//		logger.info("schedulerFactoryBean.isRunning() = {}", schedulerFactoryBean.isRunning());
	//		//
	//		//		Scheduler scheduler = schedulerFactoryBean.getScheduler();
	//		//		try {
	//		//			logger.info("scheduler.isShutdown() = {}", scheduler.isShutdown());
	//		//		} catch (SchedulerException e) {
	//		//			e.printStackTrace();
	//		//		}
	//
	//		/*
	//		 * This will start the scheduler again, so "stop()" is really like "pause()".
	//		 */
	//		//		logger.info("schedulerFactoryBean.start()");
	//		//		schedulerFactoryBean.start();
	//
	//		subscriptionJobProcessorScheduler.unscheduleJob();
	//
	//		return "Job processor unscheduled";
	//	}

	@GET
	@Path("/scheduleAllJobs")
	@Produces(MediaType.TEXT_PLAIN)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_RUN_DIAGNOSTICS + "')")
	public String scheduleAllJobs(
			@HeaderParam("Accept") final String acceptHeader,
			@Context final UriInfo uriInfo) throws SchedulerException {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		logger.info("Executing subscriptionScheduler.scheduleAllJobs()");
		subscriptionScheduler.scheduleAllJobs();

		return "Executed subscriptionScheduler.scheduleAllJobs()";
	}

	/**
	 * This endpoints does absolutely nothing (No OPeration).
	 * 
	 * <p>
	 * It can be used for timing tests to determine the overhead of
	 * authentication, etc.
	 * 
	 * @return
	 */
	@GET
	@Path("/nop")
	@Produces(MediaType.TEXT_PLAIN)
	//	public String requestTimerTest() {
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_RUN_DIAGNOSTICS + "')")
	public String requestTimerTest(@HeaderParam("Accept") String acceptHeader) {
		//	RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
		//	return apiVersion.getVersion();
		return "nop";
	}

}
