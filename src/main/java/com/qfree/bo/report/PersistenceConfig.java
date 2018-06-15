package com.qfree.bo.report;

import java.util.Properties;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.qfree.bo.report.db")
@PropertySource("classpath:config.properties")
//This is for *multiple* properties files. The @PropertySource elements must be
//comma-separated:
//@PropertySources({
//		@PropertySource("classpath:config.properties")
//})
public class PersistenceConfig {

	private static final Logger logger = LoggerFactory.getLogger(PersistenceConfig.class);

	/*
	 * The injected "env" object here will contain key/value pairs for each 
	 * property in the properties files specified above in the @PropertySource
	 * or @PropertySources annotation above.
	 */
	@Autowired
	private Environment env;

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

	@Bean
	public DataSource dataSource() {
		DataSource dataSource = null;
		if (env.getProperty("spring.database.vendor").equals(Database.H2.toString())) {

			EmbeddedDatabaseBuilder edb = new EmbeddedDatabaseBuilder();
			edb.setType(EmbeddedDatabaseType.H2);
			edb.addScript("classpath:db/h2/schema.sql");
			edb.addScript("classpath:db/h2/test-data.sql");
			EmbeddedDatabase embeddedDatabase = edb.build();

			dataSource = embeddedDatabase;

		} else if (env.getProperty("spring.database.vendor").equals(Database.POSTGRESQL.toString())) {

			//TODO Review all parameters for this class and update this setup as appropriate
			BasicDataSource basicDataSource = new BasicDataSource();
			basicDataSource.setDriverClassName(env.getProperty("db.jdbc.driverclass"));
			basicDataSource.setUrl(env.getProperty("db.jdbc.url"));
			basicDataSource.setUsername(env.getProperty("db.username"));
			basicDataSource.setPassword(env.getProperty("db.password"));
			//basicDataSource.setDefaultCatalog("ServerCommon");
			basicDataSource.setInitialSize(0);
			//basicDataSource.setMaxActive(this.dbConcurrentCallsMaxCalls + 8);  // This is for DBCB v1.4
			basicDataSource.setMaxTotal(10);                                     // This is for DBCB v2.0 (API change)
			//		basicDataSource.setMaxIdle(this.dbConcurrentCallsMaxCalls / 2 + 8);
			basicDataSource.setMinIdle(0);
			//basicDataSource.setRemoveAbandoned(true);		// Can help to reduce chance of memory leaks // This is for DBCB v1.4
			basicDataSource.setRemoveAbandonedTimeout(300);	// this is the default (5 minutes)

			dataSource = basicDataSource;

		} else {
			logger.error("Unsupported database: {}", env.getProperty("spring.database.vendor"));
			throw new UnsupportedOperationException("Unsupported database: "
					+ env.getProperty("spring.database.vendor"));
		}
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

	@Bean
	public LocalContainerEntityManagerFactoryBean
			entityManagerFactory(DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setDataSource(dataSource);
		emf.setPersistenceUnitName("reportServer");
		emf.setJpaVendorAdapter(jpaVendorAdapter);
		emf.setJpaProperties(additionalProperties());
		emf.setPackagesToScan("com.qfree.bo.report.domain");
		return emf;
	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabase(Database.valueOf(env.getProperty("spring.database.vendor")));
		//		adapter.setDatabase(Database.H2);
		//		adapter.setShowSql(true);
		//		adapter.setGenerateDdl(false);
		//		adapter.setDatabasePlatform("org.hibernate.dialect.H2Dialect");
		return adapter;
	}

	Properties additionalProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
		properties.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
		String hbm2ddlAuto = env.getProperty("hibernate.hbm2ddl.auto");
		if (hbm2ddlAuto != null && !hbm2ddlAuto.isEmpty()) {
			properties.setProperty("hibernate.hbm2ddl.auto", hbm2ddlAuto);
		}
		String import_files = env.getProperty("hibernate.hbm2ddl.import_files");
		if (import_files != null && !import_files.isEmpty()) {
			/* The "import_files" scripts are only executed if the schema is created, 
			 * i.e., if hibernate.hbm2ddl.auto is set to "create" or "create-drop".
			 */
			properties.setProperty("hibernate.hbm2ddl.import_files", import_files);
		}
		properties.setProperty("hibernate.hbm2ddl.import_files_sql_extractor",
				"org.hibernate.tool.hbm2ddl.MultipleLinesSqlCommandExtractor");
		//properties.setProperty("hibernate.default_schema", "reporting");

		return properties;
	}

	@Configuration
	@EnableTransactionManagement
	public static class TransactionConfig {

		@Inject
		private EntityManagerFactory emf;

		@Bean
		public PlatformTransactionManager transactionManager() {
			JpaTransactionManager transactionManager = new JpaTransactionManager();
			transactionManager.setEntityManagerFactory(emf);
			return transactionManager;
		}
	}

	//	@Bean
	//	public EmbeddedServletContainerFactory servletContainer() {
	//		TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
	//		//		factory.setPort(9000);
	//		//		factory.setSessionTimeout(10, TimeUnit.MINUTES);
	//		//		factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/notfound.html"));
	//		return factory;
	//	}

}
