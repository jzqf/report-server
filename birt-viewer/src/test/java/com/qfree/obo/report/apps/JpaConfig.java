package com.qfree.obo.report.apps;

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
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.qfree.obo.report.db")
@PropertySource("classpath:config.properties")
//This is for *multiple* properties files (Spring 4+). The @PropertySource 
//elements must be comma-separated:
//@PropertySources({
//		@PropertySource("classpath:config.properties")
//})
public class JpaConfig {

	private static final Logger logger = LoggerFactory.getLogger(JpaConfig.class);

	/*
	 * The injected "env" object here will contain key/value pairs for each 
	 * property in the properties files specified above in the @PropertySource
	 * or @PropertySources annotation above.
	 */
	@Autowired
	private Environment env;

	//	@Bean
	//	public DataSource dataSource() {
	//		EmbeddedDatabaseBuilder edb = new EmbeddedDatabaseBuilder();
	//		edb.setType(EmbeddedDatabaseType.H2);
	//		edb.addScript("classpath:db/schema.sql");
	//		edb.addScript("classpath:db/test-data.sql");
	//		EmbeddedDatabase embeddedDatabase = edb.build();
	//		return embeddedDatabase;
	//	}
	//
	//	@Bean
	//	public JpaVendorAdapter jpaVendorAdapter() {
	//		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
	//		adapter.setDatabase(Database.H2);
	//		adapter.setShowSql(true);
	//		adapter.setGenerateDdl(false);
	//		adapter.setDatabasePlatform("org.hibernate.dialect.H2Dialect");
	//		return adapter;
	//	}
	//
	//	@Bean
	//	public LocalContainerEntityManagerFactoryBean
	//			entityManagerFactory(DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
	//		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
	//		emf.setDataSource(dataSource);
	//		emf.setPersistenceUnitName("reportServer");
	//		emf.setJpaVendorAdapter(jpaVendorAdapter);
	//		emf.setPackagesToScan("com.qfree.obo.report.domain");
	//		return emf;
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

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		//		adapter.setDatabase(Database.H2);
		//		adapter.setShowSql(true);
		//		adapter.setGenerateDdl(false);
		//		adapter.setDatabasePlatform("org.hibernate.dialect.H2Dialect");
		return adapter;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean
			entityManagerFactory(DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setDataSource(dataSource);
		emf.setPersistenceUnitName("reportServer");
		emf.setJpaVendorAdapter(jpaVendorAdapter);
		emf.setPackagesToScan("com.qfree.obo.report.domain");
		emf.setJpaProperties(additionalProperties());
		return emf;
	}

	Properties additionalProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
		properties.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
		properties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));

		properties.setProperty("hibernate.hbm2ddl.import_files", "/db/test-data.sql");	// HARDWIRED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

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
}
