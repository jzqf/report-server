package com.qfree.obo.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
@EnableScheduling
@ComponentScan(basePackageClasses = {
		com.qfree.obo.report.scheduling.ComponentScanPackageMarker.class,
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

	/*
	 * TODO If I specify this bean here, then I probably don't need:
	 * 1. The @Component annotation on class MyBean.
	 * 2. The @ComponentScan(basePackageClasses = 
	 *    {com.qfree.obo.report.scheduling.ComponentScanPackageMarker.class})
	 *    above.
	 */
	//	@Bean
	//	public MyBean myBean() {
	//		return new MyBean();
	//	}
	//
	//	/*
	//	 * MethodInvokingJobDetailFactoryBean: For times when you just need to 
	//	 * invoke a method on a specific object.
	//	 */
	//	@Bean
	//	public MethodInvokingJobDetailFactoryBean simpleJobDetail() {
	//		MethodInvokingJobDetailFactoryBean simpleJobDetail = new MethodInvokingJobDetailFactoryBean();
	//		simpleJobDetail.setTargetObject(myBean());
	//		simpleJobDetail.setTargetMethod("printMessage");
	//		return simpleJobDetail;
	//	}
	//
	//	@Bean
	//	public SimpleTriggerFactoryBean simpleTriggerFactoryBean() {
	//		SimpleTriggerFactoryBean simpleTrigger = new SimpleTriggerFactoryBean();
	//		simpleTrigger.setJobDetail(simpleJobDetail().getObject());
	//		simpleTrigger.setStartDelay(1000);
	//		simpleTrigger.setRepeatInterval(2000);
	//		simpleTrigger.setRepeatCount(50);
	//		return simpleTrigger;
	//	}

	@Bean
	public SchedulerFactoryBean schedulerFactory() {
		SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
		//		schedulerFactory.setTriggers(simpleTriggerFactoryBean().getObject());
		return schedulerFactory;
	}

}
