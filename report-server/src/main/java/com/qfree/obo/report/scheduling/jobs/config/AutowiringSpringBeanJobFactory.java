package com.qfree.obo.report.scheduling.jobs.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import com.qfree.obo.report.scheduling.jobs.SubscriptionScheduledJob;

/**
 * This JobFactory provides support for Spring @Autowired dependency injection
 * in Quartz-instantiated job beans.
 * 
 * <p>
 * In particular, it provides support for @Autowired in:
 * 
 * <p>
 * {@link SubscriptionScheduledJob}
 * 
 * @author jeffreyz
 * 
 * @see <a href="http://blog.btmatthews.com/?p=40">http://blog.btmatthews.com/?p
 *      =4</a>
 * @see <a href="https://gist.github.com/jelies/5085593">https://gist.github.com
 *      /jelies/5085593</a>
 * 
 */
public final class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements
		ApplicationContextAware {

	private transient AutowireCapableBeanFactory beanFactory;

	@Override
	public void setApplicationContext(final ApplicationContext context) {
		beanFactory = context.getAutowireCapableBeanFactory();
	}

	@Override
	protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
		final Object job = super.createJobInstance(bundle);
		beanFactory.autowireBean(job);
		return job;
	}
}