package com.qfree.obo.report;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import com.qfree.obo.report.scheduling.jobs.config.AutowiringSpringBeanJobFactory;

@Configuration
@EnableScheduling
@ComponentScan(basePackageClasses = {
		com.qfree.obo.report.scheduling.jobs.ComponentScanPackageMarker.class,
		com.qfree.obo.report.scheduling.schedulers.ComponentScanPackageMarker.class,
})
@PropertySource("classpath:config.properties")
//This is for *multiple* properties files. The @PropertySource elements must be
//comma-separated:
//@PropertySources({
//		@PropertySource("classpath:config.properties")
//})
public class SchedulingConfig {

	private static final Logger logger = LoggerFactory.getLogger(SchedulingConfig.class);

	/*
	 * The injected "env" object here will contain key/value pairs for each 
	 * property in the properties files specified above in the @PropertySource
	 * or @PropertySources annotation above.
	 */
	@Autowired
	private Environment env;

	//	@Bean
	//	public SchedulerFactoryBean schedulerFactory() {
	//		SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
	//		return schedulerFactory;
	//	}

	//	@Autowired
	//	private DataSource dataSource;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private ApplicationContext applicationContext;

	@Bean
	public SchedulerFactoryBean quartzScheduler() {
		SchedulerFactoryBean quartzScheduler = new SchedulerFactoryBean();

		//	quartzScheduler.setDataSource(dataSource);
		quartzScheduler.setTransactionManager(transactionManager);
		quartzScheduler.setOverwriteExistingJobs(true);
		quartzScheduler.setSchedulerName("qfree-report-server-quartz-scheduler");

		/*
		 *  Custom Spring job factory with DI support for @Autowired.
		 */
		AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
		jobFactory.setApplicationContext(applicationContext);
		quartzScheduler.setJobFactory(jobFactory);

		quartzScheduler.setQuartzProperties(quartzProperties());

		//	Trigger[] triggers = { procesoMQTrigger().getObject() };
		//	quartzScheduler.setTriggers(triggers);

		return quartzScheduler;
	}

	//	@Bean
	//	public JobDetailFactoryBean procesoMQJob() {
	//		JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
	//		jobDetailFactory.setJobClass(ExampleService.class);
	//		jobDetailFactory.setGroup("spring3-quartz");
	//		return jobDetailFactory;
	//	}

	//	@Bean
	//	public CronTriggerFactoryBean procesoMQTrigger() {
	//		CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
	//		cronTriggerFactoryBean.setJobDetail(procesoMQJob().getObject());
	//		cronTriggerFactoryBean.setCronExpression(0 * * * * ?);
	//		cronTriggerFactoryBean.setGroup("spring3-quartz");
	//		return cronTriggerFactoryBean;
	//	}

	@Bean
	public Properties quartzProperties() {
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
		Properties properties = null;
		try {
			propertiesFactoryBean.afterPropertiesSet();
			properties = propertiesFactoryBean.getObject();
		} catch (IOException e) {
			logger.warn("Cannot load quartz.properties.");
		}
		return properties;
	}

	@PostConstruct
	public void init() {
		logger.info("QuartzConfig initialized.");
	}
}
