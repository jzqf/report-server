package com.qfree.obo.report.scheduling.jobs;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.eclipse.birt.report.engine.api.IParameterDefn;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.JobRepository;
import com.qfree.obo.report.db.JobStatusRepository;
import com.qfree.obo.report.db.RoleRepository;
import com.qfree.obo.report.db.SubscriptionRepository;
import com.qfree.obo.report.domain.Job;
import com.qfree.obo.report.domain.JobParameter;
import com.qfree.obo.report.domain.JobParameterValue;
import com.qfree.obo.report.domain.JobStatus;
import com.qfree.obo.report.domain.Role;
import com.qfree.obo.report.domain.Subscription;
import com.qfree.obo.report.domain.SubscriptionParameter;
import com.qfree.obo.report.domain.SubscriptionParameterValue;
import com.qfree.obo.report.exceptions.JobProcessorNotScheduledCannotTrigger;
import com.qfree.obo.report.exceptions.JobProcessorSchedulerNotRunningCannotTrigger;
import com.qfree.obo.report.exceptions.UntreatedCaseException;
import com.qfree.obo.report.scheduling.schedulers.SubscriptionJobProcessorScheduler;
import com.qfree.obo.report.scheduling.schedulers.SubscriptionScheduler;
import com.qfree.obo.report.util.DateUtils;
import com.qfree.obo.report.util.ReportUtils;

/*
 * This class is instantiated by Quartz and therefore Spring-based dependency
 * injection will not work. There are three work-arounds to this:
 * 
 * 1. Use JobDetailFactoryBean to create a JobDetail: The job bean class, i.e.,
 *    this class, SubscriptionScheduledJob, is passed to the 
 *    JobDetailFactoryBean via a call to setJobClass(...). In order for an 
 *    instance of SubscriptionScheduledJob to have access to Spring-managed 
 *    beans, they can be passed to it in the "job data map" that is passed to 
 *    the JobDetailFactoryBean when setting up the scheduled job. They can be 
 *    beans of any type: Spring Data-generated repositories, custom service 
 *    classes, etc. I have tested this and it appears to work just fine.
 * 
 *    Since for this case this class is not managed by Spring, it makes no sense 
 *    to annotate it with @Component. If it _were_ a Spring-managed class, it 
 *    would probably need to be given a scope of "prototype" because we need 
 *    separate instances to maintain the state, in particular the field 
 *    "subscriptionId". But since for this case this class is not managed by 
 *    Spring, it makes no sense to annotate it with @Scope, either.
 * 
 * 2. Use JobDetailFactoryBean to create a JobDetail: Create a 
 *    SpringBeanJobFactory that supports Spring's @Autowired dependency 
 *    injection. The Quartz scheduler can be configured to be aware of this 
 *    factory. Then @Autowired DI will just work in any Quartz-instantiated job 
 *    beans created from a JobDetailFactoryBean. I have tested this and it works
 *    fine, but I have chosen the next approach in order to be able to work with
 *    a fully Spring-managed job bean. Therefore, I have commented out these 
 *    configuration details in SchedulingConfig.
 * 
 * 3. Use a MethodInvokingJobDetailFactoryBean to create a scheduled job: For
 *    this to work this SubscriptionScheduledJob class must be a prototype-
 *    scoped  Spring bean and the instance of this class that is passed to the
 *    MethodInvokingJobDetailFactoryBean must be created from an ObjectFactory 
 *    (so we get a new instance for each subscription job). This approach 
 *    produces a Quartz scheduled job bean that is fully Spring-managed (I 
 *    think).
 *    
 *    This is the approach I have chose, since the scheduled job bean is a fully
 *    Spring-managed bean.
 *    
 *    However, I encountered a confusing exception trying to autowire a bean of 
 *    type SubscriptionService in this class. The exception message listed a 
 *    long chain of beans injected to each other and then ended with:
 *    
 *         Requested bean is currently in creation: Is there an unresolvable 
 *         circular reference?
 *    
 *    I never figured that one out. It may have been related to how this is 
 *    a prototype-scoped bean and *not* due to the autowiring itself. This is
 *    because I also tried the following:
 *    
 *      1. Autowire the SubscriptionService bean in the singleton-scoped
 *         SubscriptionScheduler.
 *         
 *      2. Create the SubscriptionScheduledJob prototype bean in 
 *         SubscriptionScheduler using the object factory
 *         ObjectFactory<SubscriptionScheduledJob> (as I do here in treatment
 *         #3).
 *      
 *      3. Set the SubscriptionService service bean on the 
 *         SubscriptionScheduledJob object in SubscriptionScheduledJob using: 
 *         subscriptionScheduledJob.setSubscriptionService(subscriptionService).
 *    
 *    Everything worked fine up to this point, but the same exception was STILL 
 *    thrown from the SubscriptionScheduledJob. I "solved" this, eventually, by
 *    autowiring Repository classes in the SubscriptionScheduledJob instead of 
 *    autowiring a single SubscriptionService service class. Somehow, the 
 *    Repository classes did not trigger this problem in the same was as the 
 *    service bean did.
 *    
 *  I use @Transactional so that entities will not be persisted to the database
 *  if an exception is thrown before a complete collection of Job, 
 *  JobParameter and JobParameterValue entities are saved.
 */
@Transactional
@Component
@Scope(value = "prototype")
//public class SubscriptionScheduledJob extends QuartzJobBean {   <- for approaches 1 & 2 above
public class SubscriptionScheduledJob {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionScheduledJob.class);

	//@Autowired
	//private SubscriptionService subscriptionService;

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private JobStatusRepository jobStatusRepository;

	@Autowired
	private SubscriptionJobProcessorScheduler subscriptionJobProcessorScheduler;

	@Autowired
	private RoleRepository roleRepository;

	/**
	 * This is set in {@link SubscriptionScheduler#scheduleJob(Subscription)}
	 * when a {@link Subscription} is scheduled as a job with the Quartz
	 * Scheduler.
	 * 
	 * <p>
	 * This is how this {@link SubscriptionScheduledJob} knows which
	 * {@link Subscription} it is for.
	 */
	private UUID subscriptionId;

	/*
	 * This is used to avoid the unfortunate, although probably unlikely, 
	 * possibility that this job is triggered at a very high frequency (due
	 * to an error in the cron schedule or whatever. If that should happen, then
	 * a very large number of Job entities could be created quickly, resulting 
	 * in a huge number of reports being run and sent out by email. 
	 * 
	 * To avoid this possibility, the "run()" method (which performs all of the
	 * work to process a Subscription and create a Job), will do nothing until
	 * a certain minimum time has elapsed. This minimum time is specified by
	 * MIN_TIME_BETWEEN_RUNS_MS.
	 */
	private long lastRunMs = 0;

	/**
	 * This is the minimum time in milliseconds between runs.
	 * 
	 * <p>
	 * The "run()" method will do nothing until at least this much time has
	 * elapsed since the last time it processed the subscription.
	 */
	private final long MIN_TIME_BETWEEN_RUNS_MS = 60L * 1000L;

	/*
	 * QUESTION: Is it possible for more than one instance of this class to be
	 * created for the same subscription? Could this happen if a ridiculously
	 * frequent schedule was set and the code run from this class ran for longer
	 * than the repeat time? If so, we might need to be extra careful about making
	 * everything thread-safe. If so, should I declare this method as 
	 * "synchronized"?
	 */
	/* 
	 * This is for approaches 1 & 2 above where we use a JobDetailFactoryBean 
	 * instead of a MethodInvokingJobDetailFactoryBean to create a scheduled 
	 * job. For this case the method signature must be different because this 
	 * class extends QuartzJobBean:
	 */
	//	@Override
	//	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
	/**
	 * Runs according to the subscription schedule. On each run a new Job is
	 * created.
	 * 
	 * Creating a new Job and its related entities can be done very quickly.
	 * Another scheduled Quartz job, SubscriptionJobProcessorScheduledJob will
	 * discover these Job entities and run them.
	 * 
	 * @throws UntreatedCaseException
	 */
	public void run() throws UntreatedCaseException {

		/*
		 * To avoid the potential of a messy situation where an unreasonable 
		 * large number of Job entities are created per second, this method does
		 * nothing until a certain minimum time has elapsed since the last time
		 * it processed its Subscription.
		 */
		if (System.currentTimeMillis() - lastRunMs > MIN_TIME_BETWEEN_RUNS_MS) {
			lastRunMs = System.currentTimeMillis(); // Update "last run" time

			logger.debug("");
			logger.debug("");
			logger.info("Creating Job for subscriptionId = {}...", subscriptionId);

			Subscription subscription = subscriptionRepository.findOne(subscriptionId);
			if (subscription != null) {

				/*
				 * The new Job will have status=QUEUED.
				 */
				JobStatus jobStatusQueued = jobStatusRepository.findOne(JobStatus.QUEUED_ID);

				Job job = new Job(
						subscription,
						jobStatusQueued,
						null,
						subscription.getReportVersion(),
						subscription.getRole(),
						subscription.getDocumentFormat(),
						subscription.getEmailAddress());

				List<JobParameter> jobParameters = new ArrayList<>(0);
				job.setJobParameters(jobParameters);

				/*
				 * Create one JobParameter for each SubscriptionParameter:
				 */
				for (SubscriptionParameter subscriptionParameter : subscription.getSubscriptionParameters()) {

					logger.info("Creating JobParameter for ReportRarameter \"{}\"",
							subscriptionParameter.getReportParameter().getName());

					JobParameter jobParameter = new JobParameter(job, subscriptionParameter.getReportParameter());
					jobParameters.add(jobParameter);

					List<JobParameterValue> jobParameterValues = new ArrayList<>(0);
					jobParameter.setJobParameterValues(jobParameterValues);

					/*
					 * Create one JobParameterValue for each 
					 * SubscriptionParameterValue that is linked to the 
					 * SubscriptionParameter entity:
					 */
					List<SubscriptionParameterValue> subscriptionParameterValues = subscriptionParameter
							.getSubscriptionParameterValues();
					for (SubscriptionParameterValue subscriptionParameterValue : subscriptionParameterValues) {

						Integer parameterDataType = subscriptionParameter.getReportParameter().getDataType();

						/*
						 * SubscriptionParameterValue entities can represent either:
						 * 
						 *   1. A static value for a report parameter, or
						 *   
						 *   2. Details for how to compute a report parameter value.
						 *      This applies only to report parameters:
						 *        a. that have a single SubscriptionParameterValue
						 *        b. of type "date" or "datetime"
						 *        c. at least one "dynamic" attribute of the
						 *           SubscriptionParameterValue is set. We do not
						 *           test below if any of the "duration" attributes
						 *           is not null because they can only be used 
						 *           together with the other "dynamic" attributes,
						 *           which we *do* test for not null.
						 */
						if (subscriptionParameterValues.size() == 1

								&& (parameterDataType.equals(IParameterDefn.TYPE_DATE_TIME) ||
										parameterDataType.equals(IParameterDefn.TYPE_DATE))

								&& (subscriptionParameterValue.getYearNumber() != null ||
										subscriptionParameterValue.getYearsAgo() != null ||
										subscriptionParameterValue.getMonthNumber() != null ||
										subscriptionParameterValue.getMonthsAgo() != null ||
										subscriptionParameterValue.getWeeksAgo() != null ||
										subscriptionParameterValue.getDaysAgo() != null ||
										subscriptionParameterValue.getDayOfWeekNumber() != null ||
										subscriptionParameterValue.getDayOfMonthNumber() != null ||
										/*
										 * These two parameters must *both* be not
										 * null in order to be used.
										 */
										(subscriptionParameterValue.getDayOfWeekInMonthOrdinal() != null &&
												subscriptionParameterValue.getDayOfWeekInMonthNumber() != null)
						//||subscriptionParameterValue.getDurationToAddYears() != null ||
						//subscriptionParameterValue.getDurationToAddMonths() != null ||
						//subscriptionParameterValue.getDurationToAddWeeks() != null ||
						//subscriptionParameterValue.getDurationToAddDays() != null ||
						//subscriptionParameterValue.getDurationToAddHours() != null ||
						//subscriptionParameterValue.getDurationToAddMinutes() != null ||
						//subscriptionParameterValue.getDurationToAddSeconds() != null

						)) {

							logger.debug("Generating dynamic date or datetime value...");

							/*
							 * Assume for the time being that the report expects date 
							 * or datetime parameters with no time zone information.
							 * This means that we can use classes LocalDate and
							 * LocalDatetime. If this is not the case, we must add
							 * support for time zones in the future.
							 */
							LocalDateTime localDateTime = LocalDateTime.now();
							logger.debug("localDateTime = LocalDateTime.now() = {}", localDateTime);

							if (subscriptionParameterValue.getYearNumber() != null) {
								/*
								 * Set year number of localDateTime to specified year number.
								 */
								try {
									localDateTime = localDateTime.withYear(subscriptionParameterValue.getYearNumber());
									logger.debug("After setting year number to {}. localDateTime = {}",
											subscriptionParameterValue.getYearNumber(),
											localDateTime);
								} catch (DateTimeException e) {
									logger.warn(
											"Illegal value for subscriptionParameterValue.getYearNumber(): {}. localDateTime = {}. Exception: {}",
											subscriptionParameterValue.getYearNumber(), localDateTime, e);
								}
							} else if (subscriptionParameterValue.getYearsAgo() != null) {
								/*
								 * Move localDateTime backwards specified number of years.
								 */
								try {
									localDateTime = localDateTime
											.plusYears(-subscriptionParameterValue.getYearsAgo().longValue());
									logger.debug("After moving localDateTime back {} years. localDateTime = {}",
											subscriptionParameterValue.getYearsAgo(), localDateTime);
								} catch (DateTimeException e) {
									logger.warn(
											"Illegal value for subscriptionParameterValue.getYearsAgo(): {}. localDateTime = {}. Exception: {}",
											subscriptionParameterValue.getYearsAgo(), localDateTime, e);
								}
							}

							if (subscriptionParameterValue.getMonthNumber() != null) {
								/*
								 * Set month number of localDateTime to specified month number.
								 */
								try {
									localDateTime = localDateTime
											.withMonth(subscriptionParameterValue.getMonthNumber());
									logger.debug("After setting month number to {}. localDateTime = {}",
											subscriptionParameterValue.getMonthNumber(),
											localDateTime);
								} catch (DateTimeException e) {
									logger.warn(
											"Illegal value for subscriptionParameterValue.getMonthNumber(): {}. localDateTime = {}. Exception: {}",
											subscriptionParameterValue.getMonthNumber(), localDateTime, e);
								}
							} else if (subscriptionParameterValue.getMonthsAgo() != null) {
								/*
								 * Move localDateTime backwards specified number of months.
								 */
								try {
									localDateTime = localDateTime
											.plusMonths(-subscriptionParameterValue.getMonthsAgo().longValue());
									logger.debug("After moving localDateTime back {} months. localDateTime = {}",
											subscriptionParameterValue.getMonthsAgo(), localDateTime);
								} catch (DateTimeException e) {
									logger.warn(
											"Illegal value for subscriptionParameterValue.getMonthsAgo(): {}. localDateTime = {}. Exception: {}",
											subscriptionParameterValue.getMonthsAgo(), localDateTime, e);
								}
							}

							if (subscriptionParameterValue.getWeeksAgo() != null) {
								/*
								 * Move localDateTime backwards specified number of weeks.
								 */
								try {
									localDateTime = localDateTime
											.plusWeeks(-subscriptionParameterValue.getWeeksAgo().longValue());
									logger.debug("After moving localDateTime back {} weeks. localDateTime = {}",
											subscriptionParameterValue.getWeeksAgo(), localDateTime);
								} catch (DateTimeException e) {
									logger.warn(
											"Illegal value for subscriptionParameterValue.getWeeksAgo(): {}. localDateTime = {}. Exception: {}",
											subscriptionParameterValue.getWeeksAgo(), localDateTime, e);
								}
							}

							if (subscriptionParameterValue.getDaysAgo() != null) {
								/*
								 * Move localDateTime backwards specified number of days.
								 */
								try {
									localDateTime = localDateTime
											.plusDays(-subscriptionParameterValue.getDaysAgo().longValue());
									logger.debug("After moving localDateTime back {} days. localDateTime = {}",
											subscriptionParameterValue.getDaysAgo(), localDateTime);
								} catch (DateTimeException e) {
									logger.warn(
											"Illegal value for subscriptionParameterValue.getDaysAgo(): {}. localDateTime = {}. Exception: {}",
											subscriptionParameterValue.getDaysAgo(), localDateTime, e);
								}
							}

							if (subscriptionParameterValue.getDayOfWeekNumber() != null) {
								/*
								 * Move the day-of-week number (1-7) of localDateTime, within the 
								 * current Monday-to-Sunday week, even if it causes the month to 
								 * change.
								 */
								try {
									localDateTime = localDateTime.with(ChronoField.DAY_OF_WEEK,
											subscriptionParameterValue.getDayOfWeekNumber().longValue());
									logger.debug(
											"After setting day-of-week of localDateTime to {}, even if it causes the month to change. localDateTime = {}",
											subscriptionParameterValue.getDayOfWeekNumber(), localDateTime);
								} catch (DateTimeException | ArithmeticException e) {
									logger.warn(
											"Illegal value for subscriptionParameterValue.getDayOfWeekNumber(): {}. localDateTime = {}. Exception: {}",
											subscriptionParameterValue.getDayOfWeekNumber(), localDateTime, e);
								}
							}

							if (subscriptionParameterValue.getDayOfMonthNumber() != null) {
								try {
									if (subscriptionParameterValue.getDayOfMonthNumber() > 0
											&& subscriptionParameterValue.getDayOfMonthNumber() <= 31) {
										/*
										 * Move the day-of-month number (1-31) of localDateTime.
										 */
										localDateTime = localDateTime.with(ChronoField.DAY_OF_MONTH,
												subscriptionParameterValue.getDayOfMonthNumber().longValue());
										logger.debug(
												"After setting day-of-month of localDateTime to {}. localDateTime = {}",
												subscriptionParameterValue.getDayOfMonthNumber(), localDateTime);
									} else if (subscriptionParameterValue.getDayOfMonthNumber() < 0
											&& subscriptionParameterValue.getDayOfMonthNumber() >= -31) {
										/*
										 * Move the day-of-month number (1-31) of localDateTime relative
										 * to the last day of the month.
										 * 
										 *   -1: Last day of the month
										 *   -2: 2nd to last day of the month
										 *   -3: 3rd to last day of the month
										 *     ...
										 */
										long lastDayOfMonth = localDateTime.range(ChronoField.DAY_OF_MONTH)
												.getMaximum();
										logger.debug("lastDayOfMonth = {}", lastDayOfMonth);
										localDateTime = localDateTime.with(ChronoField.DAY_OF_MONTH,
												lastDayOfMonth
														+ subscriptionParameterValue.getDayOfMonthNumber().longValue()
														+ 1);
										logger.debug(
												"After setting day-of-month of localDateTime to {}. localDateTime = {}",
												lastDayOfMonth
														+ subscriptionParameterValue.getDayOfMonthNumber().longValue()
														+ 1,
												localDateTime);
									} else {
										/*
										 * Zero has no meaning.
										 */
										logger.warn(
												"Illegal value for subscriptionParameterValue.getDayOfMonthNumber(): {}",
												subscriptionParameterValue.getDayOfMonthNumber());
									}
								} catch (DateTimeException | ArithmeticException e) {
									logger.warn(
											"Illegal value for subscriptionParameterValue.getDayOfMonthNumber(): {}. localDateTime = {}. Exception: {}",
											subscriptionParameterValue.getDayOfMonthNumber(), localDateTime, e);
								}
							}

							if (subscriptionParameterValue.getDayOfWeekInMonthOrdinal() != null
									&& subscriptionParameterValue.getDayOfWeekInMonthNumber() != null) {
								/*
								 * Select the Nth day-of-week in the same month (if possible) as
								 * localDateTime. Here:
								 * 
								 *   N           = subscriptionParameterValue.getDayOfWeekInMonthOrdinal()
								 *   day-of-week = subscriptionParameterValue.getDayOfWeekInMonthNumber()
								 *                 (1:Monday, ...7:Sunday)
								 */
								try {
									TemporalAdjuster dayOfWeekInMonthAdjuster = TemporalAdjusters.dayOfWeekInMonth(
											subscriptionParameterValue.getDayOfWeekInMonthOrdinal(),
											DayOfWeek.of(subscriptionParameterValue.getDayOfWeekInMonthNumber()));
									localDateTime = localDateTime.with(dayOfWeekInMonthAdjuster);
									logger.debug(
											"After setting Nth day-of-week with subscriptionParameterValue.getDayOfWeekInMonthOrdinal()={}, subscriptionParameterValue.getDayOfWeekInMonthNumber()={}. localDateTime = {}",
											subscriptionParameterValue.getDayOfWeekInMonthOrdinal(),
											subscriptionParameterValue.getDayOfWeekInMonthNumber(), localDateTime);
								} catch (DateTimeException | ArithmeticException e) {
									logger.warn(
											"Adjustment cannot be made for subscriptionParameterValue.getDayOfWeekInMonthOrdinal()={}, subscriptionParameterValue.getDayOfWeekInMonthNumber()={}. localDateTime = {}. Exception: {}",
											subscriptionParameterValue.getDayOfWeekInMonthOrdinal(),
											subscriptionParameterValue.getDayOfWeekInMonthNumber(), localDateTime, e);
								}
							}

							if (subscriptionParameterValue.getDurationToAddYears() != null) {
								try {
									localDateTime = localDateTime.plus(
											subscriptionParameterValue.getDurationToAddYears().longValue(),
											ChronoUnit.YEARS);
									logger.debug("After adding {} years. localDateTime = {}",
											subscriptionParameterValue.getDurationToAddYears(),
											localDateTime);
								} catch (DateTimeException | ArithmeticException e) {
									logger.warn(
											"Adjustment cannot be made for subscriptionParameterValue.getDurationToAddYears()={}. localDateTime = {}. Exception: {}",
											subscriptionParameterValue.getDurationToAddYears(), localDateTime, e);
								}
							}
							if (subscriptionParameterValue.getDurationToAddMonths() != null) {
								try {
									localDateTime = localDateTime.plus(
											subscriptionParameterValue.getDurationToAddMonths().longValue(),
											ChronoUnit.MONTHS);
									logger.debug("After adding {} months. localDateTime = {}",
											subscriptionParameterValue.getDurationToAddMonths(),
											localDateTime);
								} catch (DateTimeException | ArithmeticException e) {
									logger.warn(
											"Adjustment cannot be made for subscriptionParameterValue.getDurationToAddMonths()={}. localDateTime = {}. Exception: {}",
											subscriptionParameterValue.getDurationToAddMonths(), localDateTime, e);
								}
							}
							if (subscriptionParameterValue.getDurationToAddWeeks() != null) {
								try {
									localDateTime = localDateTime.plus(
											subscriptionParameterValue.getDurationToAddWeeks().longValue(),
											ChronoUnit.WEEKS);
									logger.debug("After adding {} weeks. localDateTime = {}",
											subscriptionParameterValue.getDurationToAddWeeks(),
											localDateTime);
								} catch (DateTimeException | ArithmeticException e) {
									logger.warn(
											"Adjustment cannot be made for subscriptionParameterValue.getDurationToAddWeeks()={}. localDateTime = {}. Exception: {}",
											subscriptionParameterValue.getDurationToAddWeeks(), localDateTime, e);
								}
							}
							if (subscriptionParameterValue.getDurationToAddDays() != null) {
								try {
									localDateTime = localDateTime.plus(
											subscriptionParameterValue.getDurationToAddDays().longValue(),
											ChronoUnit.DAYS);
									logger.debug("After adding {} days. localDateTime = {}",
											subscriptionParameterValue.getDurationToAddDays(),
											localDateTime);
								} catch (DateTimeException | ArithmeticException e) {
									logger.warn(
											"Adjustment cannot be made for subscriptionParameterValue.getDurationToAddDays()={}. localDateTime = {}. Exception: {}",
											subscriptionParameterValue.getDurationToAddDays(), localDateTime, e);
								}
							}
							if (subscriptionParameterValue.getDurationToAddHours() != null) {
								try {
									localDateTime = localDateTime.plus(
											subscriptionParameterValue.getDurationToAddHours().longValue(),
											ChronoUnit.HOURS);
									logger.debug("After adding {} hours. localDateTime = {}",
											subscriptionParameterValue.getDurationToAddHours(),
											localDateTime);
								} catch (DateTimeException | ArithmeticException e) {
									logger.warn(
											"Adjustment cannot be made for subscriptionParameterValue.getDurationToAddHours()={}. localDateTime = {}. Exception: {}",
											subscriptionParameterValue.getDurationToAddHours(), localDateTime, e);
								}
							}
							if (subscriptionParameterValue.getDurationToAddMinutes() != null) {
								try {
									localDateTime = localDateTime.plus(
											subscriptionParameterValue.getDurationToAddMinutes().longValue(),
											ChronoUnit.MINUTES);
									logger.debug("After adding {} minutes. localDateTime = {}",
											subscriptionParameterValue.getDurationToAddMinutes(),
											localDateTime);
								} catch (DateTimeException | ArithmeticException e) {
									logger.warn(
											"Adjustment cannot be made for subscriptionParameterValue.getDurationToAddMinutes()={}. localDateTime = {}. Exception: {}",
											subscriptionParameterValue.getDurationToAddMinutes(), localDateTime, e);
								}
							}
							if (subscriptionParameterValue.getDurationToAddSeconds() != null) {
								try {
									localDateTime = localDateTime.plus(
											subscriptionParameterValue.getDurationToAddSeconds().longValue(),
											ChronoUnit.SECONDS);
									logger.debug("After adding {} seconds. localDateTime = {}",
											subscriptionParameterValue.getDurationToAddSeconds(),
											localDateTime);
								} catch (DateTimeException | ArithmeticException e) {
									logger.warn(
											"Adjustment cannot be made for subscriptionParameterValue.getDurationToAddSeconds()={}. localDateTime = {}. Exception: {}",
											subscriptionParameterValue.getDurationToAddSeconds(), localDateTime, e);
								}
							}

							/*
							 * If we are dealing with a date report parameter and the
							 * attribute "durationSubtractOneDayForDates" is set to
							 * true, then it is necessary to subtract one day from 
							 * localDateTime.
							 */
							if (parameterDataType.equals(IParameterDefn.TYPE_DATE)
									&& subscriptionParameterValue.getDurationSubtractOneDayForDates()) {
								/*
								 * But we only subtract one day *if* a "duration" 
								 * attribute has been set that shifts the date in
								 * units of at least one day, i.e., year, month,
								 * week or day.
								 */
								if (subscriptionParameterValue.getDurationToAddYears() != null
										|| subscriptionParameterValue.getDurationToAddMonths() != null
										|| subscriptionParameterValue.getDurationToAddWeeks() != null
										|| subscriptionParameterValue.getDurationToAddDays() != null) {
									logger.debug(
											"Subtracting one day from localDateTime because durationToAddYears=true");
									try {
										localDateTime = localDateTime.plus(-1L, ChronoUnit.DAYS);
										logger.debug("After subtracting 1 day . localDateTime = {}", localDateTime);
									} catch (DateTimeException | ArithmeticException e) {
										logger.warn(
												"Exception thrown subtracting one day from localDateTime. localDateTime = {}. Exception: {}",
												localDateTime, e);
									}
								}
							}

							/*
							 * If we are computing a report parameter of type "Date", not 
							 * "Datetime", the time part of localDateTime must be discarded.
							 * 
							 * This LocalDate may also be used for a report parameter of type 
							 * "Datetime", provided a value was specified for the "timeValue"
							 * attribute of the SubscriptionParameterValue. 
							 */
							LocalDate localDate = localDateTime.toLocalDate();
							logger.debug("localDate = {}", localDate);

							if (parameterDataType.equals(IParameterDefn.TYPE_DATE)) {

								Date dateValue = Date
										.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
								logger.debug("Dynamically generated dateValue = {}", dateValue);

								JobParameterValue jobParameterValue = new JobParameterValue(jobParameter,
										null, dateValue, null, null, null, null, null);
								jobParameterValues.add(jobParameterValue);

							} else if (parameterDataType.equals(IParameterDefn.TYPE_DATE_TIME)) {

								if (subscriptionParameterValue.getTimeValue() != null) {
									/*
									 * If a value exists for subscriptionParameterValue.getTimeValue(), 
									 * we combine the LocalDate with this specified time value to 
									 * create a LocalDateTime.
									 */
									Date entityTimeDate = subscriptionParameterValue.getTimeValue();
									logger.debug("entityTimeDate = {}", entityTimeDate);
									LocalTime localTime = DateUtils.localTimeFromEntityTimeDate(entityTimeDate);
									logger.debug("localTime = {}", localTime);
									localDateTime = LocalDateTime.of(localDate, localTime);
									logger.debug("localDateTime = {}", localDateTime);
								} else {
									/*
									 * If a value not *not* exist for 
									 * subscriptionParameterValue.getTimeValue(), then there is 
									 * nothing to do here. We just convert the value of localDateTime
									 * that was computed above to the final Date that we will use for
									 * the "datetimeValue" attribute of a new JobParameterValue entity.
									 */
								}

								Date datetimeValue = Date
										.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
								logger.debug("Dynamically generated datetimeValue = {}", datetimeValue);

								JobParameterValue jobParameterValue = new JobParameterValue(jobParameter,
										null, null, datetimeValue, null, null, null, null);
								jobParameterValues.add(jobParameterValue);

							} else {
								/*
								 * This exception should never really be thrown, because
								 * I check for this case above, but it will catch coding
								 * errors if I do something stupid.
								 */
								String errorMessage = String.format(
										"subscriptionParameter.getReportParameter().getDataType() = %s, subscriptionParameterValue = %s",
										subscriptionParameter.getReportParameter().getDataType(),
										subscriptionParameterValue);
								throw new UntreatedCaseException(errorMessage);
							}

						} else {
							/*
							 * The SubscriptionParameterValue entity represents a static value.
							 */
							JobParameterValue jobParameterValue = new JobParameterValue(jobParameter,
									subscriptionParameterValue);
							jobParameterValues.add(jobParameterValue);

							/*
							 * Treat the special report parameter 
							 * RP_REPORT_REQUESTED_BY. For this parameter, we 
							 * override the "string_value" with the username of 
							 * the Role associated with the Job.
							 */
							if (ReportUtils.RP_REPORT_REQUESTED_BY
									.equals(jobParameter.getReportParameter().getName())) {
								logger.info("{}: jobParameterValue.getStringValue() (before) = {}",
										ReportUtils.RP_REPORT_REQUESTED_BY, jobParameterValue.getStringValue());
								Role role = job.getRole();
								if (role != null) {
									jobParameterValue.setStringValue(role.getUsername());
								}
								logger.info("{}: jobParameterValue.getStringValue() (after) = {}",
										ReportUtils.RP_REPORT_REQUESTED_BY, jobParameterValue.getStringValue());
							}
						}
					}
				}

				/*
				 * This should save all entities created.
				 */
				job = jobRepository.save(job);

				logger.info("Finished creating Job for subscriptionId = {}", subscriptionId);
				logger.info("jobRepository.count() = {}", jobRepository.count());

				/*
				 * Force the processor to run so it will process the Job just 
				 * created. This assumes that the transaction in which this job
				 * here is running within will finish before the job processor
				 * tries to process this Job; otherwise, the Job created here
				 * will not be visible to it.
				*/
				try {
					subscriptionJobProcessorScheduler.triggerJob();
				} catch (SchedulerException | JobProcessorSchedulerNotRunningCannotTrigger
						| JobProcessorNotScheduledCannotTrigger e) {
					logger.error("Exception thrown when triggering the job processor.", e);
				}

			} else {
				logger.error("No Subscription exists for subscriptionId = {}", subscriptionId);
			}
		}
	}

	public UUID getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(UUID subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	//	public SubscriptionService getSubscriptionService() {
	//		return subscriptionService;
	//	}
	//
	//	public void setSubscriptionService(SubscriptionService subscriptionService) {
	//		this.subscriptionService = subscriptionService;
	//	}

	public SubscriptionRepository getSubscriptionRepository() {
		return subscriptionRepository;
	}

	public void setSubscriptionRepository(SubscriptionRepository subscriptionRepository) {
		this.subscriptionRepository = subscriptionRepository;
	}

	public JobRepository getJobRepository() {
		return jobRepository;
	}

	public void setJobRepository(JobRepository jobRepository) {
		this.jobRepository = jobRepository;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SubscriptionScheduledJob [subscriptionId=");
		builder.append(subscriptionId);
		builder.append("]");
		return builder.toString();
	}
}
