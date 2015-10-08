package com.qfree.obo.report;

//import javax.naming.Context;
//import javax.naming.InitialContext;
//import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.qfree.obo.report.service.StartupService;

/**
 * The main/root class for Java-based configuration for the root Spring
 * container shared by all servlets and filters.
 */
@Configuration
/*
 * Auto-configuration note 1:
 * 
 * The "org.eclipse.birt.runtime:viewservlets" Maven artifact used in this 
 * project includes MongoDB dependencies (probably in order to support MongoDB 
 * data sources). This seems to trigger Spring Boot's auto-configuration 
 * mechanism to try to set up MongoDB support which fails because there are no 
 * appropriate MongoDB Maven dependencies. In order to fix this problem, MongoDB
 * auto-configuration is explicitly excluded here.
 * 
 * Auto-configuration note 2:
 * 
 * The "org.springframework.data:spring-data-jpa" Maven artifact used in this 
 * project includes a dependency for the "org.aspectj:aspectjrt" Maven artifact.
 * This seems to trigger Spring Boot's auto-configuration mechanism to try to 
 * set up AOP support which fails because there are missing Maven dependencies. 
 * There are three fixes for this problem (only one needs to be implemented):
 * 
 *   1. Explicitly exclude AOP auto-configuration. This is the approach used 
 *      here.
 *      
 *   2. Add an extra Maven dependency to this project. The following seems to
 *      work:
 *   
 *		<dependency>
 *			<groupId>org.aspectj</groupId>
 *			<artifactId>aspectjweaver</artifactId>
 *			<version>1.8.5</version>
 *		</dependency>
 *
 *      It is unsatisfying to solve the problem this way because this may cease
 *      to solve the problem in the future. Therefore, this solution was 
 *      rejected.
 *   
 *   3. Add an exclusion for the existing 
 *      "org.springframework.data:spring-data-jpa" Maven artifact used in this 
 *      project:
 *   
 *		<dependency>
 *			<groupId>org.springframework.data</groupId>
 *			<artifactId>spring-data-jpa</artifactId>
 *			<exclusions>
 *				<exclusion>
 *					<groupId>org.aspectj</groupId>
 *					<artifactId>aspectjrt</artifactId>
 *				</exclusion>
 *			</exclusions>
 *		</dependency>
 *
 *      As with solution #2, it is unsatisfying to solve the problem this way 
 *      because this may cease to solve the problem in the future. Therefore, 
 *      this solution was also rejected.
 */
@EnableAutoConfiguration(exclude = { MongoAutoConfiguration.class, AopAutoConfiguration.class,
		// Do not exclude:
		//	//EmbeddedServletContainerAutoConfiguration.class, REQUIRED
		//	//JerseyAutoConfiguration.class,                   REQUIRED
		//	//PropertyPlaceholderAutoConfiguration.class,      REQUIRED for "${local.server.port}", ...
		})
@ComponentScan(basePackageClasses = {
		com.qfree.obo.report.rest.server.ComponentScanPackageMarker.class,
		com.qfree.obo.report.service.ComponentScanPackageMarker.class,
})
@Import({
		PersistenceConfig.class,
		SchedulingConfig.class })
@ImportResource("classpath:spring/root-context.xml")
//@ImportResource("/WEB-INF/spring/root-context.xml")
@PropertySource("classpath:config.properties")
//This is for *multiple* properties files. The @PropertySource elements must be
//comma-separated:
//@PropertySources({
//		@PropertySource("classpath:config.properties")
//})
public class ApplicationConfig {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

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
	//  /*
	//	 * This bean must be declared if any beans associated with this Spring
	//	 * container use {@literal @}Value notation to inject property values stored
	//	 * in a property file specified with a {@literal @}PropertySource annotation
	//	 * above. The {@literal @}PropertySource annotation may appear in this
	//	 * configuration class or in another class processed by this container.
	//	 */
	//	@Bean
	//	public static PropertySourcesPlaceholderConfigurer
	//			propertySourcesPlaceholderConfigurer() {
	//		return new PropertySourcesPlaceholderConfigurer();
	//	}
	//
	//    /*
	//     * This is an alternate way to set up a PropertySourcesPlaceholderConfigurer
	//     * bean, but here we specify the property files in the bean definition and
	//     * not in one or more @PropertySource entries above.
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

	/*
	 * To get this to work, I may need to also register:
	 * 
	 *     filters
	 *     listeners
	 *     additional servlet mappings
	 *     context parameters		e.g., specify "reports" directory, ...
	 *     ...?
	 * 
	 * In fact, I may need to effectively specify here most of what appears in 
	 * web.xml. See:
	 * 
	 * http://docs.spring.io/autorepo/docs/spring-boot/current/api/org/springframework/boot/context/embedded/ServletRegistrationBean.html
	 * http://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/context/embedded/FilterRegistrationBean.html
	 * http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-web-applications.html#boot-features-embedded-container
	 * http://www.leveluplunch.com/blog/2014/04/01/spring-boot-configure-servlet-mapping-filters/
	 * http://stackoverflow.com/questions/20915528/how-can-i-register-a-secondary-servlet-with-spring-boot
	 * http://stackoverflow.com/questions/19825946/how-to-add-a-filter-class-in-spring-boot
	 * http://stackoverflow.com/questions/26639475/how-to-set-context-param-in-spring-boot
	 * 
	 * Should these beans only be declared from within the "main" method 
	 * somehow (to avoid creating these beans unless I am running this 
	 * application with an embedded Tomcat server via SpringApplication?
	 */
	//	@Bean
	//	public ViewerServlet viewerServlet() {
	//		return new ViewerServlet();
	//	}
	//
	//	@Bean
	//	public ServletRegistrationBean viewerServletRegistrationBean(ViewerServlet viewerServlet) {
	//		//return new ServletRegistrationBean(viewerServlet, "/report-server/frameset");
	//		ServletRegistrationBean registration = new ServletRegistrationBean(viewerServlet);
	//		registration.addUrlMappings("/report-server/frameset/*", "/report-server/run/*");
	//		return registration;
	//	}
	//	//	public ServletRegistrationBean servletRegistrationBean() {
	//	//		return new ServletRegistrationBean(new org.eclipse.birt.report.servlet.ViewerServlet(),
	//	//				"/report-server/frameset");
	//	//	}

	@Bean
	public StartupService startupService() {
		return new StartupService();
	}

	public static void main(String[] args) {
		SpringApplication.run(ApplicationConfig.class, args);
	}
}
