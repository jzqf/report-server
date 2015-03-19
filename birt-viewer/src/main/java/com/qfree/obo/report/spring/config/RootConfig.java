package com.qfree.obo.report.spring.config;

//import javax.naming.Context;
//import javax.naming.InitialContext;
//import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

//import com.borgsoftware.springmvc.spring.web.PropertyTest;

/**
 * The main/root class for Java-based configuration for the root Spring
 * container shared by all servlets and filters.
 */
@Configuration
@ImportResource("classpath:spring/root-context.xml")
//@ImportResource("/WEB-INF/spring/root-context.xml")
// This is for a *single* properties file:
@PropertySource("classpath:config.properties")
// This is for *multiple* properties files (Spring 4+). The @PropertySource 
// elements must be comma-separated:
//@PropertySources({
//		@PropertySource("classpath:config.properties")
//})
public class RootConfig {

	//TODO Remove all content and only include annotations. See page 60 of Spring in Action.

	private static final Logger logger = LoggerFactory.getLogger(RootConfig.class);

	/*
	 * The injected "env" object here will contain key/value pairs for each 
	 * property in the properties files specified above in the @PropertySource
	 * or @PropertySources annotation above.
	 */
	@Autowired
	private Environment env;

	//	/*
	//	 * Configuration parameters can be injected one by one like this, and then
	//	 * they can be used in beans below where necessary. But I have commented out
	//	 * this code because it is a little cleaner to inject a single Environment
	//	 * object instead, as I do above.
	//	 */
	//
	//	@Value("${db.username}")
	//	private String dbUsername;
	//
	//	@Value("${db.password}")
	//	private String dbPassword;
	//
	//	@Value("${db.jdbc.driverclass}")
	//	private String jdbcDriverClass;
	//
	//	@Value("${db.jdbc.url}")
	//	private String jdbcUrl;
	//
	//    /**
	//	 * This bean must be declared if any beans associated with this Spring
	//	 * container use {@literal @}Value notation to inject property values stored
	//	 * in a property file specified with a {@literal @}PropertySource annotation
	//	 * above. The {@literal @}PropertySource annotation may appear in this
	//	 * configuration class or in another class processed by this container.
	//	 * 
	//	 * Note that each Spring container needs its own
	//	 * PropertySourcesPlaceholderConfigurer bean if {@literal @}Value is used
	//	 * with beans/classes defined in that container. For example, we need this
	//	 * PropertySourcesPlaceholderConfigurerbean if beans created by this
	//	 * container use {@literal @}Value, but Config.java *also* needs its own
	//	 * PropertySourcesPlaceholderConfigurer bean for beans/classes that use
	//	 * {@literal @}Value that are defined in that container, e.g., MVC
	//	 * controller classes/beans.
	//	 * 
	//	 * @return
	//	 */
	//	@Bean
	//	public static PropertySourcesPlaceholderConfigurer
	//			propertySourcesPlaceholderConfigurer() {
	//		return new PropertySourcesPlaceholderConfigurer();
	//	}
	//
	//    /**
	//     * This is an alternate way to set up a PropertySourcesPlaceholderConfigurer
	//     * bean, but here we specify the property files in the bean definition and
	//     * not in one or more @PropertySource entries above.
	//     * 
	//     * @return
	//     */
	//	//    @Bean
	//	//    public static PropertySourcesPlaceholderConfigurer myPropertySourcesPlaceholderConfigurer() {
	//	//        PropertySourcesPlaceholderConfigurer p = new PropertySourcesPlaceholderConfigurer();
	//	//        Resource[] resourceLocations = new Resource[] {
	//	//                new ClassPathResource("propertyfile1.properties"),
	//	//                new ClassPathResource("propertyfile2.properties")
	//	//        };
	//	//        p.setLocations(resourceLocations);
	//	//        return p;
	//	//    }

	//	// Only used with ContractServiceJdbcRaw:
	//	@Bean
	//	public AppConfigParams appConfigParams() {
	//		final AppConfigParams object = new AppConfigParams();
	//		object.setJdbcDriverClass(env.getProperty("db.jdbc.driverclass"));
	//		object.setJdbcUrl(env.getProperty("db.jdbc.url"));
	//		object.setDbUsername(env.getProperty("db.username"));
	//		object.setDbPassword(env.getProperty("db.password"));
	//		object.setConcurrentCalls_permits(this.dbConcurrentCallsMaxCalls);
	//		object.setConcurrentCalls_timeoutsecs(this.dbConcurrentCallsWaitSecs);
	//		return object;
	//	}

	/* This is a simple DataSource provided by Spring. Not suitable for 
	 * production, but can be used for testing. Returns a new connection each
	 * time a connection is requested.
	 */
	//	@Bean
	//	public DataSource dataSource() {
	//		DriverManagerDataSource dataSource = new DriverManagerDataSource();
	//		dataSource.setDriverClassName(env.getProperty("db.jdbc.driverclass"));
	//		dataSource.setUrl(env.getProperty("db.jdbc.url"));
	//		dataSource.setUsername(env.getProperty("db.username"));
	//		dataSource.setPassword(env.getProperty("db.password"));
	//		return dataSource;
	//	}

	/*
	 * Apache Commons DBCP 2.x pooled DataSource
	 */
	@Bean
	public DataSource dataSource() {
		//TODO Review all parameters for this class and update this setup as appropriate
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(env.getProperty("db.jdbc.driverclass"));
		dataSource.setUrl(env.getProperty("db.jdbc.url"));
		dataSource.setUsername(env.getProperty("db.username"));
		dataSource.setPassword(env.getProperty("db.password"));
		//dataSource.setDefaultCatalog("ServerCommon");
		dataSource.setInitialSize(0);
		//dataSource.setMaxActive(this.dbConcurrentCallsMaxCalls + 8);  // This is for DBCB v1.4
		dataSource.setMaxTotal(10);                                     // This is for DBCB v2.0 (API change)
		//		dataSource.setMaxIdle(this.dbConcurrentCallsMaxCalls / 2 + 8);
		dataSource.setMinIdle(0);
		//dataSource.setRemoveAbandoned(true);		// Can help to reduce chance of memory leaks // This is for DBCB v1.4
		dataSource.setRemoveAbandonedTimeout(300);	// this is the default (5 minutes)
		return dataSource;
	}

	/* JNDI DataSource. 
	 * 
	 * This may be a Apache Commons DBCP 2.x pooled DataSource, but we don't 
	 * really know or care here.
	 *
	 * The required JDBC driver must be present in the local Maven
	 * repository as well as in the application server, e.g., the Tomcat 
	 * $CATALINA_HOME/lib directory.  The DataSource object is created by the 
	 * container, e.g., Tomcat.  Tomcat has, by default, the Apache Commons 
	 * "dbcp" & "pool" libraries installed in 
	 * $CATALINA_HOME/lib/tomcat-dbcp.jar.
	 * 
	 *	TODO See page 289 of Spring in Action for Spring-specific code!!!!!!!!!!!!!!!!!!!
	 */
	//	@Bean
	//	public DataSource dataSource() {
	//		DataSource dataSource = null;
	//		//			JndiTemplate jndi = new JndiTemplate();
	//		try {
	//			//			dataSource = (DataSource) jndi.lookup("java:comp/env/jdbc/autopass");
	//			Context initContext = new InitialContext();
	//			Context envContext = (Context) initContext.lookup("java:comp/env");
	//			dataSource = (DataSource) envContext.lookup("jdbc/autopass");
	//		} catch (NamingException e) {
	//			logger.error("NamingException for java:comp/env/jdbc/autopass", e);
	//		}
	//		return dataSource;
	//	}

	//	@Bean
	//	public JdbcTemplate jdbcTemplate() {
	//		//		return new JdbcTemplate(this.ds);
	//		return new JdbcTemplate(this.dataSource());
	//	}

}
