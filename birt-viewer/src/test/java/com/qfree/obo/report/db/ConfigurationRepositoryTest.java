package com.qfree.obo.report.db;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.configuration.Config;
import com.qfree.obo.report.configuration.Config.ParamName;
import com.qfree.obo.report.domain.Configuration;
import com.qfree.obo.report.domain.Role;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PersistenceConfigTestEnv.class)
public class ConfigurationRepositoryTest {

	private static final Logger logger = LoggerFactory.getLogger(ConfigurationRepositoryTest.class);

	@Autowired
	ConfigurationRepository configurationRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	Config config;

	@Test
	@Transactional
	public void countTestRecords() {
		assertThat(configurationRepository.count(), is(18L));
		assertThat(configurationRepository.count(), is(equalTo(18L)));
	}

	@Test
	@Transactional
	public void integerValueExistsDefault() {
		UUID uuidOfDefaultIntegerValueConfiguration = UUID.fromString("b4fd2271-26fb-4db7-bfe7-07211d849364");
		Configuration defaultIntegerValueConfiguration = configurationRepository
				.findOne(uuidOfDefaultIntegerValueConfiguration);
		assertThat(defaultIntegerValueConfiguration, is(not(nullValue())));
		assertThat(defaultIntegerValueConfiguration.getIntegerValue(), is(42));
	}

	@Test
	@Transactional
	public void integerValueFetchDefault() {
		//		Configuration configuration = configurationRepository.findByParamName(ParamName.TEST_INTEGER.toString());
		Configuration configuration = configurationRepository.findByParamName(ParamName.TEST_INTEGER);
		assertThat(configuration, is(not(nullValue())));
		Integer integerValue = configuration.getIntegerValue();
		assertThat(integerValue, is(not(nullValue())));
		assertThat(integerValue, is(42));
	}

	@Test
	@Transactional
	public void integerValueFetchDefaultFromConfig() {
		Object integerValueObject = config.get(ParamName.TEST_INTEGER);
		assertThat(integerValueObject, is(instanceOf(Integer.class)));
		assertThat((Integer) integerValueObject, is(42));
	}

	@Test
	@Transactional
	public void integerValueFetchNonexistentRoleSpecificFromConfig() {
		UUID uuidOfRole_bcbc = UUID.fromString("e918c8aa-c6d1-462c-9e91-f1db0fb9f346");
		Role role_bcbc = roleRepository.findOne(uuidOfRole_bcbc);
		assertThat(role_bcbc, is(notNullValue()));
		/*
		 * There is a default value for ParamName.TEST_INTEGER, but no
		 * role-specific value for Role "bcbc".
		 */
		Object integerValueObject = config.get(ParamName.TEST_INTEGER, role_bcbc);
		assertThat(integerValueObject, is(instanceOf(Integer.class)));
		assertThat((Integer) integerValueObject, is(42));
	}

	@Test
	@Transactional
	public void stringValueExistsDefault() {
		UUID uuidOfDefaultStringValueConfiguration = UUID.fromString("62f9ca07-79ce-48dd-8357-873191ea8b9d");
		Configuration defaultStringValueConfiguration = configurationRepository
				.findOne(uuidOfDefaultStringValueConfiguration);
		assertThat(defaultStringValueConfiguration, is(not(nullValue())));
		assertThat(defaultStringValueConfiguration.getStringValue(), is("Meaning of life"));
	}

	@Test
	@Transactional
	public void stringValueFetchDefault() {
		//		Configuration configuration = configurationRepository.findByParamName(ParamName.TEST_STRING.toString());
		Configuration configuration = configurationRepository.findByParamName(ParamName.TEST_STRING);
		assertThat(configuration, is(not(nullValue())));
		String stringValue = configuration.getStringValue();
		assertThat(stringValue, is(not(nullValue())));
		assertThat(stringValue, is("Meaning of life"));
	}

	//	@Test
	//	@Transactional
	//	public void stringValueFetchDefaultFromConfig() {
	//		Object stringValueObject = Config.get(ParamName.TEST_STRING);
	//		assertThat(stringValueObject, is(instanceOf(String.class)));
	//		assertThat((String) stringValueObject, is("Meaning of life"));
	//	}

	@Test
	@Transactional
	public void stringValueFetchDefaultFromConfig() {
		Object stringValueObject = config.get(ParamName.TEST_STRING);
		assertThat(stringValueObject, is(instanceOf(String.class)));
		assertThat((String) stringValueObject, is("Meaning of life"));
	}

	/*
	 * Attempt to fetch a role-specific value value that does not exist in the
	 * [configuration] table. However, there *is* a default value for the 
	 * parameter, which should be returned instead.
	 */
	@Test
	@Transactional
	public void stringValueFetchNonexistentRoleSpecificFromConfig() {
		UUID uuidOfRole_bcbc = UUID.fromString("e918c8aa-c6d1-462c-9e91-f1db0fb9f346");
		Role role_bcbc = roleRepository.findOne(uuidOfRole_bcbc);
		assertThat(role_bcbc, is(notNullValue()));
		/*
		 * There is a default value for ParamName.TEST_STRING, but no
		 * role-specific value for Role "bcbc".
		 */
		Object stringValueObject = config.get(ParamName.TEST_STRING, role_bcbc);
		assertThat(stringValueObject, is(instanceOf(String.class)));
		assertThat((String) stringValueObject, is("Meaning of life"));
	}

	/*
	 * Attempt to fetch a default (Role==null) value that does not exist in the
	 * [configuration] table. This should return null for the parameter value.
	 * An error should also be logged, but we do not check that here.
	 */
	@Test
	@Transactional
	public void stringValueFetchNonexistentDefaultFromConfig() {
		/*
		 * Parameter ParamName.TEST_NOTSET does not exist in the test data for
		 * the [configuration] table.
		 */
		Object stringValueObject = config.get(ParamName.TEST_NOTSET);
		assertThat(stringValueObject, is(nullValue()));
	}

	//	/*
	//	 * Set default integer value for a parameter that already has a default 
	//	 * (Role==null) Configuration. This should update the existing Configuration
	//	 * entity to store this new value.
	//	 */
	//	@Test
	//	@Transactional
	//	public void integerValueSetDefaultWithConfig() {
	//		long countConfiguration = 18;
	//		assertThat(configurationRepository.count(), is(countConfiguration));
	//		/*
	//		 * Retrieve the existing default value.
	//		 */
	//		Object integerValueDefaultObject = config.get(ParamName.TEST_INTEGER);
	//		assertThat(integerValueDefaultObject, is(instanceOf(Integer.class)));
	//		assertThat((Integer) integerValueDefaultObject, is(42));
	//		/*
	//		 * Update default value.
	//		 */
	//		Integer newIntgerValue = 123456;
	//				config.set(ParamName.TEST_INTEGER, newIntgerValue);
	//		/*
	//		 * The existing Configuration should have been updated, so there should
	//		 * be the same number of entities in the database
	//		 */
	//		assertThat(configurationRepository.count(), is(countConfiguration));
	//		/* 
	//		 * Retrieve value just set.
	//		 */
	//		Object integerValueNewDefaultObject = config.get(ParamName.TEST_INTEGER);
	//		assertThat(integerValueNewDefaultObject, is(instanceOf(Integer.class)));
	//		assertThat((Integer) integerValueNewDefaultObject, is(newIntgerValue));
	//	}
	//
	//	/*
	//	 * Set role-specific integer value for a parameter that has a default 
	//	 * (Role==null) Configuration, but no role-specific value. This should
	//	 * create a new Configuration entity to store this value.
	//	 */
	//	@Test
	//	@Transactional
	//	public void integerValueSetRoleSpecificWithConfig() {
	//		long countConfiguration = 18;
	//		assertThat(configurationRepository.count(), is(countConfiguration));
	//
	//		UUID uuidOfRole_bcbc = UUID.fromString("e918c8aa-c6d1-462c-9e91-f1db0fb9f346");
	//		Role role_bcbc = roleRepository.findOne(uuidOfRole_bcbc);
	//		assertThat(role_bcbc, is(notNullValue()));
	//		/*
	//		 * There is a default value for ParamName.TEST_INTEGER, but no
	//		 * role-specific value for Role "bcbc".
	//		 */
	//		Integer defaultIntegerValue = 42;
	//		Object integerValueObject = config.get(ParamName.TEST_INTEGER, role_bcbc);
	//		assertThat(integerValueObject, is(instanceOf(Integer.class)));
	//		assertThat((Integer) integerValueObject, is(defaultIntegerValue));
	//		/*
	//		 * Set role-specific value, which should override the default value for
	//		 * the specified role.
	//		 */
	//		Integer newIntgerValue = 1000001;
	//		config.set(ParamName.TEST_INTEGER, newIntgerValue, role_bcbc);
	//		/*
	//		 * This should have created a new Configuration.
	//		 */
	//		assertThat(configurationRepository.count(), is(countConfiguration + 1));
	//		/*
	//		 * Retrieve value just set. The new value should override the default.
	//		 */
	//		Object integerValueRoleSpecificObject = config.get(ParamName.TEST_INTEGER, role_bcbc);
	//		assertThat(integerValueRoleSpecificObject, is(instanceOf(Integer.class)));
	//		assertThat((Integer) integerValueRoleSpecificObject, is(newIntgerValue));
	//		/*
	//		 * The default value for ParamName.TEST_INTEGER should still be the
	//		 * same.
	//		 */
	//		Object integerValueDefaultObject = config.get(ParamName.TEST_INTEGER);
	//		assertThat(integerValueDefaultObject, is(instanceOf(Integer.class)));
	//		assertThat((Integer) integerValueDefaultObject, is(defaultIntegerValue));
	//	}

	/*
	 * Set default string value for a parameter that already has a default 
	 * (Role==null) Configuration. This should update the existing Configuration
	 * entity to store this new value.
	 */
	@Test
	@Transactional
	public void stringValueSetDefaultWithConfig() {
		long countConfiguration = 18;
		assertThat(configurationRepository.count(), is(countConfiguration));
		/*
		 * Retrieve the existing default value.
		 */
		Object stringValueDefaultObject = config.get(ParamName.TEST_STRING);
		assertThat(stringValueDefaultObject, is(instanceOf(String.class)));
		assertThat((String) stringValueDefaultObject, is("Meaning of life"));
		/*
		 * Update default value.
		 */
		String newStringValue = "New default value";
		config.set(ParamName.TEST_STRING, newStringValue);
		/*
		 * The existing Configuration should have been updated, so there should
		 * be the same number of entities in the database
		 */
		assertThat(configurationRepository.count(), is(countConfiguration));
		/* 
		 * Retrieve value just set.
		 */
		Object stringValueNewDefaultObject = config.get(ParamName.TEST_STRING);
		assertThat(stringValueNewDefaultObject, is(instanceOf(String.class)));
		assertThat((String) stringValueNewDefaultObject, is(newStringValue));
	}

	/*
	 * Set role-specific string value for a parameter that has a default 
	 * (Role==null) Configuration, but no role-specific value. This should
	 * create a new Configuration entity to store this value.
	 */
	@Test
	@Transactional
	public void stringValueSetRoleSpecificWithConfig() {
		long countConfiguration = 18;
		assertThat(configurationRepository.count(), is(countConfiguration));

		UUID uuidOfRole_bcbc = UUID.fromString("e918c8aa-c6d1-462c-9e91-f1db0fb9f346");
		Role role_bcbc = roleRepository.findOne(uuidOfRole_bcbc);
		assertThat(role_bcbc, is(notNullValue()));
		/*
		 * There is a default value for ParamName.TEST_STRING, but no
		 * role-specific value for Role "bcbc".
		 */
		String defaultStringValue = "Meaning of life";
		Object stringValueObject = config.get(ParamName.TEST_STRING, role_bcbc);
		assertThat(stringValueObject, is(instanceOf(String.class)));
		assertThat((String) stringValueObject, is(defaultStringValue));
		/*
		 * Set role-specific value, which should override the default value for
		 * the specified role.
		 */
		String newStringValue = "String value for role 'bcbc'";
		config.set(ParamName.TEST_STRING, newStringValue, role_bcbc);
		/*
		 * This should have created a new Configuration.
		 */
		assertThat(configurationRepository.count(), is(countConfiguration + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		Object stringValueRoleSpecificObject = config.get(ParamName.TEST_STRING, role_bcbc);
		assertThat(stringValueRoleSpecificObject, is(instanceOf(String.class)));
		assertThat((String) stringValueRoleSpecificObject, is(newStringValue));
		/*
		 * The default value for ParamName.TEST_STRING should still be the
		 * same.
		 */
		Object stringValueDefaultObject = config.get(ParamName.TEST_STRING);
		assertThat(stringValueDefaultObject, is(instanceOf(String.class)));
		assertThat((String) stringValueDefaultObject, is(defaultStringValue));
	}

}
