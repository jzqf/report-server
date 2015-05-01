package com.qfree.obo.report.service;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.ApplicationConfig;
import com.qfree.obo.report.db.ConfigurationRepository;
import com.qfree.obo.report.db.RoleRepository;
import com.qfree.obo.report.domain.Configuration.ParamName;
import com.qfree.obo.report.domain.Role;
import com.qfree.obo.report.util.DateUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class)
/*
 * It is not clear that this test class dirties the Spring application context,
 * but some of the subsequent tests fail in other JUnit test classes UNLESS this
 * @DirtiesContext annotation is placed here. Therefore, it should be left here
 * unless/until this behaviour is investigated thoroughly.
 */
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class ConfigurationServiceTests {

	private static final Logger logger = LoggerFactory.getLogger(ConfigurationServiceTests.class);

	private static final long NUM_TEST_CONFIGURATIONS = 22L;  // number of test [configuration] records

	/*
	 * Default values (role_id = null) in the test [configuration] records.
	 */
	private static final Boolean TEST_BOOLEAN_DEFAULT_VALUE = true;
	private static final byte[] TEST_BYTEARRAY_DEFAULT_VALUE = null;
	private static final Date TEST_DATE_DEFAULT_VALUE = new GregorianCalendar(1958, 4, 6).getTime();
	private static final Date TEST_DATETIME_DEFAULT_VALUE = new GregorianCalendar(2008, 10, 29, 01, 0, 0).getTime();
	private static final Double TEST_DOUBLE_DEFAULT_VALUE = 299792458.467;
	private static final Float TEST_FLOAT_DEFAULT_VALUE = 3.14159F;
	private static final Integer TEST_INTEGER_DEFAULT_VALUE = 42;
	private static final Long TEST_LONG_DEFAULT_VALUE = 1234567890L;
	private static final String TEST_STRING_DEFAULT_VALUE = "Meaning of life";
	private static final String TEST_TEXT_DEFAULT_VALUE = "Meaning of life - really";
	// The year, month and day here are arbitrary and used only to construct a Date.
	private static final Date TEST_TIME_DEFAULT_VALUE = new GregorianCalendar(2000, 0, 1, 16, 17, 18).getTime();
	/*
	 * Role-specific values for Role "aabb" in the test [configuration] records.
	 */
	private static final Boolean TEST_BOOLEAN_ROLE_aabb_VALUE = false;
	private static final byte[] TEST_BYTEARRAY_ROLE_aabb_VALUE = null;
	private static final Date TEST_DATE_ROLE_aabb_VALUE = new GregorianCalendar(1971, 8, 8).getTime();
	private static final Date TEST_DATETIME_ROLE_aabb_VALUE = new GregorianCalendar(2008, 10, 30, 02, 0, 1).getTime();
	private static final Double TEST_DOUBLE_ROLE_aabb_VALUE = 1.618033988749;
	private static final Float TEST_FLOAT_ROLE_aabb_VALUE = 2.71828F;
	private static final Integer TEST_INTEGER_ROLE_aabb_VALUE = 666;
	private static final Long TEST_LONG_ROLE_aabb_VALUE = 9876543210L;
	private static final String TEST_STRING_ROLE_aabb_VALUE = "Yada yada";
	private static final String TEST_TEXT_ROLE_aabb_VALUE = "Yada yada yada yada";
	// The year, month and day here are arbitrary and used only to construct a Date.
	private static final Date TEST_TIME_ROLE_aabb_VALUE = new GregorianCalendar(2000, 0, 1, 0, 0, 1).getTime();

	@Autowired
	ConfigurationRepository configurationRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	ConfigurationService configurationService;

	// ======================== Boolean parameter tests ========================

	@Test
	@Transactional
	public void booleanValueFetchDefaultFromConfig() {
		Object booleanValueObject = configurationService.get(ParamName.TEST_BOOLEAN);
		assertThat(booleanValueObject, is(instanceOf(Boolean.class)));
		assertThat((Boolean) booleanValueObject, is(TEST_BOOLEAN_DEFAULT_VALUE));
	}

	/*
	 * Get role-specific boolean value for a parameter that has both a default 
	 * (Role==null) Configuration, and a role-specific value. This should
	 * fetch the role-specific value.
	 */
	@Test
	@Transactional
	public void booleanValueGetRoleSpecificWithConfig() {
		/*
		 * Role "aabb" has a role-specific value for the TEST_BOOLEAN parameter.
		 */
		UUID uuidOfRole_aabb = UUID.fromString("ee56f34d-dbb4-41c1-9d30-ce29cf973820");
		Role role_aabb = roleRepository.findOne(uuidOfRole_aabb);
		assertThat(role_aabb, is(notNullValue()));
		/*
		 * Fetch the role-specific value for the TEST_BOOLEAN parameter for Role 
		 * "aabb".
		 */
		Object booleanValueObject = configurationService.get(ParamName.TEST_BOOLEAN, role_aabb);
		assertThat(booleanValueObject, is(instanceOf(Boolean.class)));
		assertThat((Boolean) booleanValueObject, is(TEST_BOOLEAN_ROLE_aabb_VALUE));
	}

	@Test
	@Transactional
	public void booleanValueFetchNonexistentRoleSpecificFromConfig() {
		UUID uuidOfRole_bcbc = UUID.fromString("e918c8aa-c6d1-462c-9e91-f1db0fb9f346");
		Role role_bcbc = roleRepository.findOne(uuidOfRole_bcbc);
		assertThat(role_bcbc, is(notNullValue()));
		/*
		 * There is a default value for ParamName.TEST_BOOLEAN, but no
		 * role-specific value for Role "bcbc".
		 */
		Object booleanValueObject = configurationService.get(ParamName.TEST_BOOLEAN, role_bcbc);
		assertThat(booleanValueObject, is(instanceOf(Boolean.class)));
		assertThat((Boolean) booleanValueObject, is(TEST_BOOLEAN_DEFAULT_VALUE));
	}

	/*
	 * Set default boolean value for a parameter that already has a default 
	 *Configuration. This should update the existing Configuration
	 * entity to store this new value.
	 */
	@Test
	@Transactional
	public void booleanValueSetDefaultWithConfig() {
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/*
		 * Retrieve the existing default value.
		 */
		Object booleanValueDefaultObject = configurationService.get(ParamName.TEST_BOOLEAN);
		assertThat(booleanValueDefaultObject, is(instanceOf(Boolean.class)));
		assertThat((Boolean) booleanValueDefaultObject, is(TEST_BOOLEAN_DEFAULT_VALUE));
		/*
		 * Update default value.
		 */
		Boolean newBooleanValue = false;
		configurationService.set(ParamName.TEST_BOOLEAN, newBooleanValue);
		/*
		 * The existing Configuration should have been updated, so there should
		 * be the same number of entities in the database
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/* 
		 * Retrieve value just set.
		 */
		Object booleanValueNewDefaultObject = configurationService.get(ParamName.TEST_BOOLEAN);
		assertThat(booleanValueNewDefaultObject, is(instanceOf(Boolean.class)));
		assertThat((Boolean) booleanValueNewDefaultObject, is(newBooleanValue));
	}

	/*
	 * Set role-specific boolean value for a parameter that has a default 
	 * (Role==null) Configuration, but no role-specific value. This should
	 * create a new Configuration entity to store this value.
	 */
	@Test
	@Transactional
	public void booleanValueSetRoleSpecificWithConfig() {
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));

		UUID uuidOfRole_bcbc = UUID.fromString("e918c8aa-c6d1-462c-9e91-f1db0fb9f346");
		Role role_bcbc = roleRepository.findOne(uuidOfRole_bcbc);
		assertThat(role_bcbc, is(notNullValue()));
		/*
		 * There is a default value for ParamName.TEST_BOOLEAN, but no
		 * role-specific value for Role "bcbc".
		 */
		Object booleanValueObject = configurationService.get(ParamName.TEST_BOOLEAN, role_bcbc);
		assertThat(booleanValueObject, is(instanceOf(Boolean.class)));
		assertThat((Boolean) booleanValueObject, is(TEST_BOOLEAN_DEFAULT_VALUE));

		/*
		 * Set role-specific value, which should override the default value for
		 * the specified role.
		 */
		Boolean newBooleanValue = false;
		configurationService.set(ParamName.TEST_BOOLEAN, newBooleanValue, role_bcbc);
		/*
		 * This should have created a new Configuration.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		Object booleanValueRoleSpecificObject = configurationService.get(ParamName.TEST_BOOLEAN, role_bcbc);
		assertThat(booleanValueRoleSpecificObject, is(instanceOf(Boolean.class)));
		assertThat((Boolean) booleanValueRoleSpecificObject, is(newBooleanValue));

		/*
		 * Update the role-specific value. This should override the default 
		 * value for the specified role and RE-USE the new role-specific
		 * Configuration just created, i.e., not create a new Configuration
		 */
		newBooleanValue = true;
		configurationService.set(ParamName.TEST_BOOLEAN, newBooleanValue, role_bcbc);
		/*
		 * This should have re-used the Configuration just created.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		booleanValueRoleSpecificObject = configurationService.get(ParamName.TEST_BOOLEAN, role_bcbc);
		assertThat(booleanValueRoleSpecificObject, is(instanceOf(Boolean.class)));
		assertThat((Boolean) booleanValueRoleSpecificObject, is(newBooleanValue));

		/*
		 * The default value for ParamName.TEST_BOOLEAN should still be the
		 * same.
		 */
		Object booleanValueDefaultObject = configurationService.get(ParamName.TEST_BOOLEAN);
		assertThat(booleanValueDefaultObject, is(instanceOf(Boolean.class)));
		assertThat((Boolean) booleanValueDefaultObject, is(TEST_BOOLEAN_DEFAULT_VALUE));
	}

	// ======================== Date parameter tests ===========================

	@Test
	@Transactional
	public void dateValueFetchDefaultFromConfig() {
		Object dateValueObject = configurationService.get(ParamName.TEST_DATE);
		assertThat(dateValueObject, is(instanceOf(Date.class)));
		Date dateFromConfig = (Date) dateValueObject;
		assertThat(dateFromConfig, is(not(nullValue())));

		/*
		 * [configuration]date_value contains '1958-05-06' in the database.
		 * The string value logged here is:
		 * 
		 *     1958-05-06
		 * 
		 * Note that it does not contain a time portion and nor does it 
		 * contain a time zone.
		 */
		//		logger.info("dateFromConfig = {}", dateFromConfig);
		/*
		 * If this changes, it means that dates are being treated differently
		 * by the database or the ORM:
		 */
		assertThat(dateFromConfig.toString(), is("1958-05-06"));
		assertThat(DateUtils.entityDateToNormalDate(dateFromConfig), is(TEST_DATE_DEFAULT_VALUE));
	}

	/*
	 * Get role-specific date value for a parameter that has both a default 
	 * (Role==null) Configuration, and a role-specific value. This should
	 * fetch the role-specific value.
	 */
	@Test
	@Transactional
	public void dateValueGetRoleSpecificWithConfig() {
		/*
		 * Role "aabb" has a role-specific value for the TEST_DATE parameter.
		 */
		UUID uuidOfRole_aabb = UUID.fromString("ee56f34d-dbb4-41c1-9d30-ce29cf973820");
		Role role_aabb = roleRepository.findOne(uuidOfRole_aabb);
		assertThat(role_aabb, is(notNullValue()));
		/*
		 * Fetch the role-specific value for the TEST_DATE parameter for Role 
		 * "aabb".
		 */
		Object dateValueObject = configurationService.get(ParamName.TEST_DATE, role_aabb);
		assertThat(dateValueObject, is(instanceOf(Date.class)));

		Date dateFromConfig = (Date) dateValueObject;
		assertThat(dateFromConfig, is(not(nullValue())));
		assertThat(DateUtils.entityDateToNormalDate(dateFromConfig), is(TEST_DATE_ROLE_aabb_VALUE));
	}

	@Test
	@Transactional
	public void dateValueFetchNonexistentRoleSpecificFromConfig() {
		UUID uuidOfRole_bcbc = UUID.fromString("e918c8aa-c6d1-462c-9e91-f1db0fb9f346");
		Role role_bcbc = roleRepository.findOne(uuidOfRole_bcbc);
		assertThat(role_bcbc, is(notNullValue()));
		/*
		 * There is a default value for ParamName.TEST_DATE, but no
		 * role-specific value for Role "bcbc".
		 */
		Object dateValueObject = configurationService.get(ParamName.TEST_DATE, role_bcbc);
		assertThat(dateValueObject, is(instanceOf(Date.class)));

		Date dateFromConfig = (Date) dateValueObject;
		assertThat(dateFromConfig, is(not(nullValue())));
		assertThat(DateUtils.entityDateToNormalDate(dateFromConfig), is(TEST_DATE_DEFAULT_VALUE));
	}

	/*
	 * Set default date value for a parameter that already has a default 
	 * (Role==null) Configuration. This should update the existing Configuration
	 * entity to store this new value.
	 */
	@Test
	@Transactional
	public void dateValueSetDefaultWithConfig() {
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/*
		 * Retrieve the existing default value.
		 */
		Object dateValueObject = configurationService.get(ParamName.TEST_DATE);
		assertThat(dateValueObject, is(instanceOf(Date.class)));
		Date dateFromConfig = (Date) dateValueObject;
		assertThat(dateFromConfig, is(not(nullValue())));
		assertThat(DateUtils.entityDateToNormalDate(dateFromConfig), is(TEST_DATE_DEFAULT_VALUE));
		/*
		 * Update default value.
		 */
		Date newDateValue = new GregorianCalendar(1927, 1, 15).getTime();
		configurationService.set(ParamName.TEST_DATE, newDateValue);
		/*
		 * The existing Configuration should have been updated, so there should
		 * be the same number of entities in the database
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/* 
		 * Retrieve value just set.
		 */
		Object dateValueObjectUpdated = configurationService.get(ParamName.TEST_DATE);
		assertThat(dateValueObjectUpdated, is(instanceOf(Date.class)));
		Date dateFromConfigUpdated = (Date) dateValueObjectUpdated;
		assertThat(dateFromConfigUpdated, is(not(nullValue())));
		assertThat(DateUtils.entityDateToNormalDate(dateFromConfigUpdated), is(newDateValue));
	}

	/*
	 * Set role-specific date value for a parameter that has a default 
	 * (Role==null) Configuration, but no role-specific value. This should
	 * create a new Configuration entity to store this value.
	 */
	@Test
	@Transactional
	public void dateValueSetRoleSpecificWithConfig() {
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));

		UUID uuidOfRole_bcbc = UUID.fromString("e918c8aa-c6d1-462c-9e91-f1db0fb9f346");
		Role role_bcbc = roleRepository.findOne(uuidOfRole_bcbc);
		assertThat(role_bcbc, is(notNullValue()));
		/*
		 * There is a default value for ParamName.TEST_DATE, but no
		 * role-specific value for Role "bcbc".
		 */
		Object dateValueObject = configurationService.get(ParamName.TEST_DATE, role_bcbc);
		assertThat(dateValueObject, is(instanceOf(Date.class)));
		Date dateFromConfig = (Date) dateValueObject;
		assertThat(dateFromConfig, is(not(nullValue())));
		assertThat(DateUtils.entityDateToNormalDate(dateFromConfig), is(TEST_DATE_DEFAULT_VALUE));

		/*
		 * Set role-specific value, which should override the default value for
		 * the specified role.
		 */
		Date newDateValue = new GregorianCalendar(1932, 3, 17).getTime();
		configurationService.set(ParamName.TEST_DATE, newDateValue, role_bcbc);
		/*
		 * This should have created a new Configuration.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		Object dateValueRoleSpecificObject = configurationService.get(ParamName.TEST_DATE, role_bcbc);
		assertThat(dateValueRoleSpecificObject, is(instanceOf(Date.class)));
		Date dateFromRoleSpecific = (Date) dateValueRoleSpecificObject;
		assertThat(dateFromRoleSpecific, is(not(nullValue())));
		assertThat(DateUtils.entityDateToNormalDate(dateFromRoleSpecific), is(newDateValue));

		/*
		 * Update the role-specific value. This should override the default 
		 * value for the specified role and RE-USE the new role-specific
		 * Configuration just created, i.e., not create a new Configuration
		 */
		newDateValue = new GregorianCalendar(1955, 2, 24).getTime();
		configurationService.set(ParamName.TEST_DATE, newDateValue, role_bcbc);
		/*
		 * This should have created a new Configuration.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		dateValueRoleSpecificObject = configurationService.get(ParamName.TEST_DATE, role_bcbc);
		assertThat(dateValueRoleSpecificObject, is(instanceOf(Date.class)));
		dateFromRoleSpecific = (Date) dateValueRoleSpecificObject;
		assertThat(dateFromRoleSpecific, is(not(nullValue())));
		assertThat(DateUtils.entityDateToNormalDate(dateFromRoleSpecific), is(newDateValue));

		/*
		 * The default value for ParamName.TEST_DATE should still be the
		 * same.
		 */
		Object dateValueDefaultObject = configurationService.get(ParamName.TEST_DATE);
		assertThat(dateValueDefaultObject, is(instanceOf(Date.class)));
		dateFromConfig = (Date) dateValueDefaultObject;
		assertThat(dateFromConfig, is(not(nullValue())));
		assertThat(DateUtils.entityDateToNormalDate(dateFromConfig), is(TEST_DATE_DEFAULT_VALUE));
	}

	// ====================== Datetime parameter tests =========================

	/*
	 * Get role-specific datetime value for a parameter that has both a default 
	 * (Role==null) Configuration, and a role-specific value. This should
	 * fetch the role-specific value.
	 */
	@Test
	@Transactional
	public void datetimeValueGetRoleSpecificWithConfig() {
		/*
		 * Role "aabb" has a role-specific value for the TEST_DATETIME parameter.
		 */
		UUID uuidOfRole_aabb = UUID.fromString("ee56f34d-dbb4-41c1-9d30-ce29cf973820");
		Role role_aabb = roleRepository.findOne(uuidOfRole_aabb);
		assertThat(role_aabb, is(notNullValue()));
		/*
		 * Fetch the role-specific value for the TEST_DATETIME parameter for Role 
		 * "aabb".
		 */
		Object datetimeValueObject = configurationService.get(ParamName.TEST_DATETIME, role_aabb);
		assertThat(datetimeValueObject, is(instanceOf(Date.class)));

		Date datetimeFromConfig = (Date) datetimeValueObject;
		assertThat(datetimeFromConfig, is(not(nullValue())));
		assertThat(DateUtils.entityTimestampToNormalDate(datetimeFromConfig), is(TEST_DATETIME_ROLE_aabb_VALUE));
	}

	/*
	 * Set default datetime value for a parameter that already has a default 
	 * (Role==null) Configuration. This should update the existing Configuration
	 * entity to store this new value.
	 */
	@Test
	@Transactional
	public void datetimeValueSetDefaultWithConfig() {
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/*
		 * Retrieve the existing default value.
		 */
		Object datetimeValueObject = configurationService.get(ParamName.TEST_DATETIME);
		assertThat(datetimeValueObject, is(instanceOf(Date.class)));
		Date datetimeFromConfig = (Date) datetimeValueObject;
		assertThat(datetimeFromConfig, is(not(nullValue())));
		assertThat(DateUtils.entityTimestampToNormalDate(datetimeFromConfig), is(TEST_DATETIME_DEFAULT_VALUE));
		/*
		 * Update default value.
		 */
		Date newDatetimeValue = new GregorianCalendar(1960, 4, 14, 1, 2, 3).getTime();
		configurationService.set(ParamName.TEST_DATETIME, newDatetimeValue);
		/*
		 * The existing Configuration should have been updated, so there should
		 * be the same number of entities in the database
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/* 
		 * Retrieve value just set.
		 */
		Object datetimeValueObjectUpdated = configurationService.get(ParamName.TEST_DATETIME);
		assertThat(datetimeValueObjectUpdated, is(instanceOf(Date.class)));
		Date datetimeFromConfigUpdated = (Date) datetimeValueObjectUpdated;
		assertThat(datetimeFromConfigUpdated, is(not(nullValue())));
		assertThat(DateUtils.entityTimestampToNormalDate(datetimeFromConfigUpdated), is(newDatetimeValue));
	}

	/*
	 * Set role-specific datetime value for a parameter that has a default 
	 * (Role==null) Configuration, but no role-specific value. This should
	 * create a new Configuration entity to store this value.
	 */
	@Test
	@Transactional
	public void datetimeValueSetRoleSpecificWithConfig() {
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));

		UUID uuidOfRole_bcbc = UUID.fromString("e918c8aa-c6d1-462c-9e91-f1db0fb9f346");
		Role role_bcbc = roleRepository.findOne(uuidOfRole_bcbc);
		assertThat(role_bcbc, is(notNullValue()));
		/*
		 * There is a default value for ParamName.TEST_DATETIME, but no
		 * role-specific value for Role "bcbc".
		 */
		Object datetimeValueObject = configurationService.get(ParamName.TEST_DATETIME, role_bcbc);
		assertThat(datetimeValueObject, is(instanceOf(Date.class)));
		Date datetimeFromConfig = (Date) datetimeValueObject;
		assertThat(datetimeFromConfig, is(not(nullValue())));
		assertThat(DateUtils.entityTimestampToNormalDate(datetimeFromConfig), is(TEST_DATETIME_DEFAULT_VALUE));

		/*
		 * Set role-specific value, which should override the default value for
		 * the specified role.
		 */
		Date newDatetimeValue = new GregorianCalendar(1960, 4, 14, 1, 2, 3).getTime();
		configurationService.set(ParamName.TEST_DATETIME, newDatetimeValue, role_bcbc);
		/*
		 * This should have created a new Configuration.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		Object datetimeValueRoleSpecificObject = configurationService.get(ParamName.TEST_DATETIME, role_bcbc);
		assertThat(datetimeValueRoleSpecificObject, is(instanceOf(Date.class)));
		Date datetimeFromRoleSpecific = (Date) datetimeValueRoleSpecificObject;
		assertThat(datetimeFromRoleSpecific, is(not(nullValue())));
		assertThat(DateUtils.entityTimestampToNormalDate(datetimeFromRoleSpecific), is(newDatetimeValue));

		/*
		 * Update the role-specific value. This should override the default 
		 * value for the specified role and RE-USE the new role-specific
		 * Configuration just created, i.e., not create a new Configuration
		 */
		newDatetimeValue = new GregorianCalendar(1961, 10, 4, 18, 30, 15).getTime();
		configurationService.set(ParamName.TEST_DATETIME, newDatetimeValue, role_bcbc);
		/*
		 * This should have created a new Configuration.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		datetimeValueRoleSpecificObject = configurationService.get(ParamName.TEST_DATETIME, role_bcbc);
		assertThat(datetimeValueRoleSpecificObject, is(instanceOf(Date.class)));
		datetimeFromRoleSpecific = (Date) datetimeValueRoleSpecificObject;
		assertThat(datetimeFromRoleSpecific, is(not(nullValue())));
		assertThat(DateUtils.entityTimestampToNormalDate(datetimeFromRoleSpecific), is(newDatetimeValue));

		/*
		 * The default value for ParamName.TEST_DATETIME should still be the
		 * same.
		 */
		Object datetimeValueDefaultObject = configurationService.get(ParamName.TEST_DATETIME);
		assertThat(datetimeValueDefaultObject, is(instanceOf(Date.class)));
		datetimeFromConfig = (Date) datetimeValueDefaultObject;

		/*
		 * [configuration]datetime_value contains '2008-11-29T01:00:00' in the 
		 * database. The string value logged here is:
		 * 
		 *     2008-11-29 01:00:00.0
		 */
		//		logger.info("datetimeFromConfig = {}", datetimeFromConfig);
		/*
		 * If this changes, it means that dates are being treated differently
		 * by the database or the ORM:
		 */
		assertThat(datetimeFromConfig.toString(), is("2008-11-29 01:00:00.0"));

		assertThat(datetimeFromConfig, is(not(nullValue())));
		assertThat(DateUtils.entityTimestampToNormalDate(datetimeFromConfig), is(TEST_DATETIME_DEFAULT_VALUE));
	}

	// ======================== Double parameter tests ========================

	@Test
	@Transactional
	public void doubleValueFetchDefaultFromConfig() {
		Object doubleValueObject = configurationService.get(ParamName.TEST_DOUBLE);
		assertThat(doubleValueObject, is(instanceOf(Double.class)));
		assertThat((Double) doubleValueObject, is(TEST_DOUBLE_DEFAULT_VALUE));
	}

	/*
	 * Get role-specific double value for a parameter that has both a default 
	 * (Role==null) Configuration, and a role-specific value. This should
	 * fetch the role-specific value.
	 */
	@Test
	@Transactional
	public void doubleValueGetRoleSpecificWithConfig() {
		/*
		 * Role "aabb" has a role-specific value for the TEST_DOUBLE parameter.
		 */
		UUID uuidOfRole_aabb = UUID.fromString("ee56f34d-dbb4-41c1-9d30-ce29cf973820");
		Role role_aabb = roleRepository.findOne(uuidOfRole_aabb);
		assertThat(role_aabb, is(notNullValue()));
		/*
		 * Fetch the role-specific value for the TEST_DOUBLE parameter for Role 
		 * "aabb".
		 */
		Object doubleValueObject = configurationService.get(ParamName.TEST_DOUBLE, role_aabb);
		assertThat(doubleValueObject, is(instanceOf(Double.class)));
		assertThat((Double) doubleValueObject, is(TEST_DOUBLE_ROLE_aabb_VALUE));
	}

	@Test
	@Transactional
	public void doubleValueFetchNonexistentRoleSpecificFromConfig() {
		UUID uuidOfRole_bcbc = UUID.fromString("e918c8aa-c6d1-462c-9e91-f1db0fb9f346");
		Role role_bcbc = roleRepository.findOne(uuidOfRole_bcbc);
		assertThat(role_bcbc, is(notNullValue()));
		/*
		 * There is a default value for ParamName.TEST_DOUBLE, but no
		 * role-specific value for Role "bcbc".
		 */
		Object doubleValueObject = configurationService.get(ParamName.TEST_DOUBLE, role_bcbc);
		assertThat(doubleValueObject, is(instanceOf(Double.class)));
		assertThat((Double) doubleValueObject, is(TEST_DOUBLE_DEFAULT_VALUE));
	}

	/*
	 * Set default double value for a parameter that already has a default 
	 * (Role==null) Configuration. This should update the existing Configuration
	 * entity to store this new value.
	 */
	@Test
	@Transactional
	public void doubleValueSetDefaultWithConfig() {
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/*
		 * Retrieve the existing default value.
		 */
		Object doubleValueDefaultObject = configurationService.get(ParamName.TEST_DOUBLE);
		assertThat(doubleValueDefaultObject, is(instanceOf(Double.class)));
		assertThat((Double) doubleValueDefaultObject, is(TEST_DOUBLE_DEFAULT_VALUE));
		/*
		 * Update default value.
		 */
		Double newDoubleValue = 111222333.444;
		configurationService.set(ParamName.TEST_DOUBLE, newDoubleValue);
		/*
		 * The existing Configuration should have been updated, so there should
		 * be the same number of entities in the database
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/* 
		 * Retrieve value just set.
		 */
		Object doubleValueNewDefaultObject = configurationService.get(ParamName.TEST_DOUBLE);
		assertThat(doubleValueNewDefaultObject, is(instanceOf(Double.class)));
		assertThat((Double) doubleValueNewDefaultObject, is(newDoubleValue));
	}

	/*
	 * Set role-specific double value for a parameter that has a default 
	 * (Role==null) Configuration, but no role-specific value. This should
	 * create a new Configuration entity to store this value.
	 */
	@Test
	@Transactional
	public void doubleValueSetRoleSpecificWithConfig() {
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));

		UUID uuidOfRole_bcbc = UUID.fromString("e918c8aa-c6d1-462c-9e91-f1db0fb9f346");
		Role role_bcbc = roleRepository.findOne(uuidOfRole_bcbc);
		assertThat(role_bcbc, is(notNullValue()));
		/*
		 * There is a default value for ParamName.TEST_DOUBLE, but no
		 * role-specific value for Role "bcbc".
		 */
		Object doubleValueObject = configurationService.get(ParamName.TEST_DOUBLE, role_bcbc);
		assertThat(doubleValueObject, is(instanceOf(Double.class)));
		assertThat((Double) doubleValueObject, is(TEST_DOUBLE_DEFAULT_VALUE));

		/*
		 * Set role-specific value, which should override the default value for
		 * the specified role.
		 */
		Double newDoubleValue = 483.128444;
		configurationService.set(ParamName.TEST_DOUBLE, newDoubleValue, role_bcbc);
		/*
		 * This should have created a new Configuration.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		Object doubleValueRoleSpecificObject = configurationService.get(ParamName.TEST_DOUBLE, role_bcbc);
		assertThat(doubleValueRoleSpecificObject, is(instanceOf(Double.class)));
		assertThat((Double) doubleValueRoleSpecificObject, is(newDoubleValue));

		/*
		 * Update the role-specific value. This should override the default 
		 * value for the specified role and RE-USE the new role-specific
		 * Configuration just created, i.e., not create a new Configuration
		 */
		newDoubleValue = 0.000001;
		configurationService.set(ParamName.TEST_DOUBLE, newDoubleValue, role_bcbc);
		/*
		 * This should have re-used the Configuration just created.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		doubleValueRoleSpecificObject = configurationService.get(ParamName.TEST_DOUBLE, role_bcbc);
		assertThat(doubleValueRoleSpecificObject, is(instanceOf(Double.class)));
		assertThat((Double) doubleValueRoleSpecificObject, is(newDoubleValue));

		/*
		 * The default value for ParamName.TEST_DOUBLE should still be the
		 * same.
		 */
		Object doubleValueDefaultObject = configurationService.get(ParamName.TEST_DOUBLE);
		assertThat(doubleValueDefaultObject, is(instanceOf(Double.class)));
		assertThat((Double) doubleValueDefaultObject, is(TEST_DOUBLE_DEFAULT_VALUE));
	}

	// ======================== Float parameter tests ========================

	@Test
	@Transactional
	public void floatValueFetchDefaultFromConfig() {
		Object floatValueObject = configurationService.get(ParamName.TEST_FLOAT);
		assertThat(floatValueObject, is(instanceOf(Float.class)));
		assertThat((Float) floatValueObject, is(TEST_FLOAT_DEFAULT_VALUE));
	}

	/*
	 * Get role-specific float value for a parameter that has both a default 
	 * (Role==null) Configuration, and a role-specific value. This should
	 * fetch the role-specific value.
	 */
	@Test
	@Transactional
	public void floatValueGetRoleSpecificWithConfig() {
		/*
		 * Role "aabb" has a role-specific value for the TEST_FLOAT parameter.
		 */
		UUID uuidOfRole_aabb = UUID.fromString("ee56f34d-dbb4-41c1-9d30-ce29cf973820");
		Role role_aabb = roleRepository.findOne(uuidOfRole_aabb);
		assertThat(role_aabb, is(notNullValue()));
		/*
		 * Fetch the role-specific value for the TEST_FLOAT parameter for Role 
		 * "aabb".
		 */
		Object floatValueObject = configurationService.get(ParamName.TEST_FLOAT, role_aabb);
		assertThat(floatValueObject, is(instanceOf(Float.class)));
		assertThat((Float) floatValueObject, is(TEST_FLOAT_ROLE_aabb_VALUE));
	}

	@Test
	@Transactional
	public void floatValueFetchNonexistentRoleSpecificFromConfig() {
		UUID uuidOfRole_bcbc = UUID.fromString("e918c8aa-c6d1-462c-9e91-f1db0fb9f346");
		Role role_bcbc = roleRepository.findOne(uuidOfRole_bcbc);
		assertThat(role_bcbc, is(notNullValue()));
		/*
		 * There is a default value for ParamName.TEST_FLOAT, but no
		 * role-specific value for Role "bcbc".
		 */
		Object floatValueObject = configurationService.get(ParamName.TEST_FLOAT, role_bcbc);
		assertThat(floatValueObject, is(instanceOf(Float.class)));
		assertThat((Float) floatValueObject, is(TEST_FLOAT_DEFAULT_VALUE));
	}

	/*
	 * Set default float value for a parameter that already has a default 
	 * (Role==null) Configuration. This should update the existing Configuration
	 * entity to store this new value.
	 */
	@Test
	@Transactional
	public void floatValueSetDefaultWithConfig() {
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/*
		 * Retrieve the existing default value.
		 */
		Object floatValueDefaultObject = configurationService.get(ParamName.TEST_FLOAT);
		assertThat(floatValueDefaultObject, is(instanceOf(Float.class)));
		assertThat((Float) floatValueDefaultObject, is(TEST_FLOAT_DEFAULT_VALUE));
		/*
		 * Update default value.
		 */
		Float newFloatValue = 1234.56F;
		configurationService.set(ParamName.TEST_FLOAT, newFloatValue);
		/*
		 * The existing Configuration should have been updated, so there should
		 * be the same number of entities in the database
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/* 
		 * Retrieve value just set.
		 */
		Object floatValueNewDefaultObject = configurationService.get(ParamName.TEST_FLOAT);
		assertThat(floatValueNewDefaultObject, is(instanceOf(Float.class)));
		assertThat((Float) floatValueNewDefaultObject, is(newFloatValue));
	}

	/*
	 * Set role-specific float value for a parameter that has a default 
	 * (Role==null) Configuration, but no role-specific value. This should
	 * create a new Configuration entity to store this value.
	 */
	@Test
	@Transactional
	public void floatValueSetRoleSpecificWithConfig() {
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));

		UUID uuidOfRole_bcbc = UUID.fromString("e918c8aa-c6d1-462c-9e91-f1db0fb9f346");
		Role role_bcbc = roleRepository.findOne(uuidOfRole_bcbc);
		assertThat(role_bcbc, is(notNullValue()));
		/*
		 * There is a default value for ParamName.TEST_FLOAT, but no
		 * role-specific value for Role "bcbc".
		 */
		Object floatValueObject = configurationService.get(ParamName.TEST_FLOAT, role_bcbc);
		assertThat(floatValueObject, is(instanceOf(Float.class)));
		assertThat((Float) floatValueObject, is(TEST_FLOAT_DEFAULT_VALUE));

		/*
		 * Set role-specific value, which should override the default value for
		 * the specified role.
		 */
		Float newFloatValue = 82.583F;
		configurationService.set(ParamName.TEST_FLOAT, newFloatValue, role_bcbc);
		/*
		 * This should have created a new Configuration.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		Object floatValueRoleSpecificObject = configurationService.get(ParamName.TEST_FLOAT, role_bcbc);
		assertThat(floatValueRoleSpecificObject, is(instanceOf(Float.class)));
		assertThat((Float) floatValueRoleSpecificObject, is(newFloatValue));

		/*
		 * Update the role-specific value. This should override the default 
		 * value for the specified role and RE-USE the new role-specific
		 * Configuration just created, i.e., not create a new Configuration
		 */
		newFloatValue = 0.001F;
		configurationService.set(ParamName.TEST_FLOAT, newFloatValue, role_bcbc);
		/*
		 * This should have re-used the Configuration just created.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		floatValueRoleSpecificObject = configurationService.get(ParamName.TEST_FLOAT, role_bcbc);
		assertThat(floatValueRoleSpecificObject, is(instanceOf(Float.class)));
		assertThat((Float) floatValueRoleSpecificObject, is(newFloatValue));

		/*
		 * The default value for ParamName.TEST_FLOAT should still be the
		 * same.
		 */
		Object floatValueDefaultObject = configurationService.get(ParamName.TEST_FLOAT);
		assertThat(floatValueDefaultObject, is(instanceOf(Float.class)));
		assertThat((Float) floatValueDefaultObject, is(TEST_FLOAT_DEFAULT_VALUE));
	}

	// ======================== Integer parameter tests ========================

	@Test
	@Transactional
	public void integerValueFetchDefaultFromConfig() {
		Object integerValueObject = configurationService.get(ParamName.TEST_INTEGER);
		assertThat(integerValueObject, is(instanceOf(Integer.class)));
		assertThat((Integer) integerValueObject, is(TEST_INTEGER_DEFAULT_VALUE));
	}

	/*
	 * Get role-specific integer value for a parameter that has both a default 
	 * (Role==null) Configuration, and a role-specific value. This should
	 * fetch the role-specific value.
	 */
	@Test
	@Transactional
	public void integerValueGetRoleSpecificWithConfig() {
		/*
		 * Role "aabb" has a role-specific value for the TEST_INTEGER parameter.
		 */
		UUID uuidOfRole_aabb = UUID.fromString("ee56f34d-dbb4-41c1-9d30-ce29cf973820");
		Role role_aabb = roleRepository.findOne(uuidOfRole_aabb);
		assertThat(role_aabb, is(notNullValue()));
		/*
		 * Fetch the role-specific value for the TEST_INTEGER parameter for Role 
		 * "aabb".
		 */
		Object integerValueObject = configurationService.get(ParamName.TEST_INTEGER, role_aabb);
		assertThat(integerValueObject, is(instanceOf(Integer.class)));
		assertThat((Integer) integerValueObject, is(TEST_INTEGER_ROLE_aabb_VALUE));
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
		Object integerValueObject = configurationService.get(ParamName.TEST_INTEGER, role_bcbc);
		assertThat(integerValueObject, is(instanceOf(Integer.class)));
		assertThat((Integer) integerValueObject, is(TEST_INTEGER_DEFAULT_VALUE));
	}

	/*
	 * Set default integer value for a parameter that already has a default 
	 * (Role==null) Configuration. This should update the existing Configuration
	 * entity to store this new value.
	 */
	@Test
	@Transactional
	public void integerValueSetDefaultWithConfig() {
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/*
		 * Retrieve the existing default value.
		 */
		Object integerValueDefaultObject = configurationService.get(ParamName.TEST_INTEGER);
		assertThat(integerValueDefaultObject, is(instanceOf(Integer.class)));
		assertThat((Integer) integerValueDefaultObject, is(TEST_INTEGER_DEFAULT_VALUE));
		/*
		 * Update default value.
		 */
		Integer newIntegerValue = 123456;
		configurationService.set(ParamName.TEST_INTEGER, newIntegerValue);
		/*
		 * The existing Configuration should have been updated, so there should
		 * be the same number of entities in the database
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/* 
		 * Retrieve value just set.
		 */
		Object integerValueNewDefaultObject = configurationService.get(ParamName.TEST_INTEGER);
		assertThat(integerValueNewDefaultObject, is(instanceOf(Integer.class)));
		assertThat((Integer) integerValueNewDefaultObject, is(newIntegerValue));
	}

	/*
	 * Set role-specific integer value for a parameter that has a default 
	 * (Role==null) Configuration, but no role-specific value. This should
	 * create a new Configuration entity to store this value.
	 */
	@Test
	@Transactional
	public void integerValueSetRoleSpecificWithConfig() {
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));

		UUID uuidOfRole_bcbc = UUID.fromString("e918c8aa-c6d1-462c-9e91-f1db0fb9f346");
		Role role_bcbc = roleRepository.findOne(uuidOfRole_bcbc);
		assertThat(role_bcbc, is(notNullValue()));
		/*
		 * There is a default value for ParamName.TEST_INTEGER, but no
		 * role-specific value for Role "bcbc".
		 */
		Object integerValueObject = configurationService.get(ParamName.TEST_INTEGER, role_bcbc);
		assertThat(integerValueObject, is(instanceOf(Integer.class)));
		assertThat((Integer) integerValueObject, is(TEST_INTEGER_DEFAULT_VALUE));

		/*
		 * Set role-specific value, which should override the default value for
		 * the specified role.
		 */
		Integer newIntegerValue = 1000001;
		configurationService.set(ParamName.TEST_INTEGER, newIntegerValue, role_bcbc);
		/*
		 * This should have created a new Configuration.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		Object integerValueRoleSpecificObject = configurationService.get(ParamName.TEST_INTEGER, role_bcbc);
		assertThat(integerValueRoleSpecificObject, is(instanceOf(Integer.class)));
		assertThat((Integer) integerValueRoleSpecificObject, is(newIntegerValue));

		/*
		 * Update the role-specific value. This should override the default 
		 * value for the specified role and RE-USE the new role-specific
		 * Configuration just created, i.e., not create a new Configuration
		 */
		newIntegerValue = 666666;
		configurationService.set(ParamName.TEST_INTEGER, newIntegerValue, role_bcbc);
		/*
		 * This should have re-used the Configuration just created.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		integerValueRoleSpecificObject = configurationService.get(ParamName.TEST_INTEGER, role_bcbc);
		assertThat(integerValueRoleSpecificObject, is(instanceOf(Integer.class)));
		assertThat((Integer) integerValueRoleSpecificObject, is(newIntegerValue));

		/*
		 * The default value for ParamName.TEST_INTEGER should still be the
		 * same.
		 */
		Object integerValueDefaultObject = configurationService.get(ParamName.TEST_INTEGER);
		assertThat(integerValueDefaultObject, is(instanceOf(Integer.class)));
		assertThat((Integer) integerValueDefaultObject, is(TEST_INTEGER_DEFAULT_VALUE));
	}

	// ======================== Long parameter tests ========================

	@Test
	@Transactional
	public void longValueFetchDefaultFromConfig() {
		Object longValueObject = configurationService.get(ParamName.TEST_LONG);
		assertThat(longValueObject, is(instanceOf(Long.class)));
		assertThat((Long) longValueObject, is(TEST_LONG_DEFAULT_VALUE));
	}

	/*
	 * Get role-specific long value for a parameter that has both a default 
	 * (Role==null) Configuration, and a role-specific value. This should
	 * fetch the role-specific value.
	 */
	@Test
	@Transactional
	public void longValueGetRoleSpecificWithConfig() {
		/*
		 * Role "aabb" has a role-specific value for the TEST_LONG parameter.
		 */
		UUID uuidOfRole_aabb = UUID.fromString("ee56f34d-dbb4-41c1-9d30-ce29cf973820");
		Role role_aabb = roleRepository.findOne(uuidOfRole_aabb);
		assertThat(role_aabb, is(notNullValue()));
		/*
		 * Fetch the role-specific value for the TEST_LONG parameter for Role 
		 * "aabb".
		 */
		Object longValueObject = configurationService.get(ParamName.TEST_LONG, role_aabb);
		assertThat(longValueObject, is(instanceOf(Long.class)));
		assertThat((Long) longValueObject, is(TEST_LONG_ROLE_aabb_VALUE));
	}

	@Test
	@Transactional
	public void longValueFetchNonexistentRoleSpecificFromConfig() {
		UUID uuidOfRole_bcbc = UUID.fromString("e918c8aa-c6d1-462c-9e91-f1db0fb9f346");
		Role role_bcbc = roleRepository.findOne(uuidOfRole_bcbc);
		assertThat(role_bcbc, is(notNullValue()));
		/*
		 * There is a default value for ParamName.TEST_LONG, but no
		 * role-specific value for Role "bcbc".
		 */
		Object longValueObject = configurationService.get(ParamName.TEST_LONG, role_bcbc);
		assertThat(longValueObject, is(instanceOf(Long.class)));
		assertThat((Long) longValueObject, is(TEST_LONG_DEFAULT_VALUE));
	}

	/*
	 * Set default long value for a parameter that already has a default 
	 * (Role==null) Configuration. This should update the existing Configuration
	 * entity to store this new value.
	 */
	@Test
	@Transactional
	public void longValueSetDefaultWithConfig() {
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/*
		 * Retrieve the existing default value.
		 */
		Object longValueDefaultObject = configurationService.get(ParamName.TEST_LONG);
		assertThat(longValueDefaultObject, is(instanceOf(Long.class)));
		assertThat((Long) longValueDefaultObject, is(TEST_LONG_DEFAULT_VALUE));
		/*
		 * Update default value.
		 */
		Long newLongValue = 246801357901234L;
		configurationService.set(ParamName.TEST_LONG, newLongValue);
		/*
		 * The existing Configuration should have been updated, so there should
		 * be the same number of entities in the database
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/* 
		 * Retrieve value just set.
		 */
		Object longValueNewDefaultObject = configurationService.get(ParamName.TEST_LONG);
		assertThat(longValueNewDefaultObject, is(instanceOf(Long.class)));
		assertThat((Long) longValueNewDefaultObject, is(newLongValue));
	}

	/*
	 * Set role-specific long value for a parameter that has a default 
	 * (Role==null) Configuration, but no role-specific value. This should
	 * create a new Configuration entity to store this value.
	 */
	@Test
	@Transactional
	public void longValueSetRoleSpecificWithConfig() {
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));

		UUID uuidOfRole_bcbc = UUID.fromString("e918c8aa-c6d1-462c-9e91-f1db0fb9f346");
		Role role_bcbc = roleRepository.findOne(uuidOfRole_bcbc);
		assertThat(role_bcbc, is(notNullValue()));
		/*
		 * There is a default value for ParamName.TEST_LONG, but no
		 * role-specific value for Role "bcbc".
		 */
		Object longValueObject = configurationService.get(ParamName.TEST_LONG, role_bcbc);
		assertThat(longValueObject, is(instanceOf(Long.class)));
		assertThat((Long) longValueObject, is(TEST_LONG_DEFAULT_VALUE));

		/*
		 * Set role-specific value, which should override the default value for
		 * the specified role.
		 */
		Long newLongValue = 246801357901234L;
		configurationService.set(ParamName.TEST_LONG, newLongValue, role_bcbc);
		/*
		 * This should have created a new Configuration.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		Object longValueRoleSpecificObject = configurationService.get(ParamName.TEST_LONG, role_bcbc);
		assertThat(longValueRoleSpecificObject, is(instanceOf(Long.class)));
		assertThat((Long) longValueRoleSpecificObject, is(newLongValue));

		/*
		 * Update the role-specific value. This should override the default 
		 * value for the specified role and RE-USE the new role-specific
		 * Configuration just created, i.e., not create a new Configuration
		 */
		newLongValue = 666666666666L;
		configurationService.set(ParamName.TEST_LONG, newLongValue, role_bcbc);
		/*
		 * This should have re-used the Configuration just created.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		longValueRoleSpecificObject = configurationService.get(ParamName.TEST_LONG, role_bcbc);
		assertThat(longValueRoleSpecificObject, is(instanceOf(Long.class)));
		assertThat((Long) longValueRoleSpecificObject, is(newLongValue));

		/*
		 * The default value for ParamName.TEST_LONG should still be the
		 * same.
		 */
		Object longValueDefaultObject = configurationService.get(ParamName.TEST_LONG);
		assertThat(longValueDefaultObject, is(instanceOf(Long.class)));
		assertThat((Long) longValueDefaultObject, is(TEST_LONG_DEFAULT_VALUE));
	}

	// ======================== String parameter tests =========================

	@Test
	@Transactional
	public void stringValueFetchDefaultFromConfig() {
		Object stringValueObject = configurationService.get(ParamName.TEST_STRING);
		assertThat(stringValueObject, is(instanceOf(String.class)));
		assertThat((String) stringValueObject, is(TEST_STRING_DEFAULT_VALUE));
	}

	@Test
	@Transactional
	public void stringValueFetchDefaultFromConfigNullRole() {
		Object stringValueObject = configurationService.get(ParamName.TEST_STRING, null);
		assertThat(stringValueObject, is(instanceOf(String.class)));
		assertThat((String) stringValueObject, is(TEST_STRING_DEFAULT_VALUE));
	}

	/*
	 * Same test, but a typed value is returned, not an Object.
	 */
	@Test
	@Transactional
	public void stringValueFetchDefaultFromConfigNullRoleGeneric() {
		String stringValue = configurationService.get(ParamName.TEST_STRING, null, String.class);
		assertThat(stringValue, is(TEST_STRING_DEFAULT_VALUE));
	}

	/*
	 * Get role-specific string value for a parameter that has both a default 
	 * (Role==null) Configuration, and a role-specific value. This should
	 * fetch the role-specific value.
	 */
	@Test
	@Transactional
	public void stringValueGetRoleSpecificWithConfig() {
		/*
		 * Role "aabb" has a role-specific value for the TEST_STRING parameter.
		 */
		UUID uuidOfRole_aabb = UUID.fromString("ee56f34d-dbb4-41c1-9d30-ce29cf973820");
		Role role_aabb = roleRepository.findOne(uuidOfRole_aabb);
		assertThat(role_aabb, is(notNullValue()));
		/*
		 * Fetch the role-specific value for the TEST_STRING parameter for Role 
		 * "aabb".
		 */
		Object stringValueObject = configurationService.get(ParamName.TEST_STRING, role_aabb);
		assertThat(stringValueObject, is(instanceOf(String.class)));
		assertThat((String) stringValueObject, is(TEST_STRING_ROLE_aabb_VALUE));
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
		Object stringValueObject = configurationService.get(ParamName.TEST_STRING, role_bcbc);
		assertThat(stringValueObject, is(instanceOf(String.class)));
		assertThat((String) stringValueObject, is(TEST_STRING_DEFAULT_VALUE));
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
		Object stringValueObject = configurationService.get(ParamName.TEST_NOTSET);
		assertThat(stringValueObject, is(nullValue()));
	}

	/*
	 * Set default string value for a parameter that already has a default 
	 * (Role==null) Configuration. This should update the existing Configuration
	 * entity to store this new value.
	 */
	@Test
	@Transactional
	public void stringValueSetDefaultWithConfig() {
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/*
		 * Retrieve the existing default value.
		 */
		Object stringValueDefaultObject = configurationService.get(ParamName.TEST_STRING);
		assertThat(stringValueDefaultObject, is(instanceOf(String.class)));
		assertThat((String) stringValueDefaultObject, is(TEST_STRING_DEFAULT_VALUE));
		/*
		 * Update default value.
		 */
		String newStringValue = "New default value";
		configurationService.set(ParamName.TEST_STRING, newStringValue);
		/*
		 * The existing Configuration should have been updated, so there should
		 * be the same number of entities in the database
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/* 
		 * Retrieve value just set.
		 */
		Object stringValueNewDefaultObject = configurationService.get(ParamName.TEST_STRING);
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
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));

		UUID uuidOfRole_bcbc = UUID.fromString("e918c8aa-c6d1-462c-9e91-f1db0fb9f346");
		Role role_bcbc = roleRepository.findOne(uuidOfRole_bcbc);
		assertThat(role_bcbc, is(notNullValue()));
		/*
		 * There is a default value for ParamName.TEST_STRING, but no
		 * role-specific value for Role "bcbc".
		 */
		Object stringValueObject = configurationService.get(ParamName.TEST_STRING, role_bcbc);
		assertThat(stringValueObject, is(instanceOf(String.class)));
		assertThat((String) stringValueObject, is(TEST_STRING_DEFAULT_VALUE));

		/*
		 * Set role-specific value, which should override the default value for
		 * the specified role.
		 */
		String newStringValue = "String value for role 'bcbc'";
		configurationService.set(ParamName.TEST_STRING, newStringValue, role_bcbc);
		/*
		 * This should have created a new Configuration.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		Object stringValueRoleSpecificObject = configurationService.get(ParamName.TEST_STRING, role_bcbc);
		assertThat(stringValueRoleSpecificObject, is(instanceOf(String.class)));
		assertThat((String) stringValueRoleSpecificObject, is(newStringValue));

		/*
		 * Update the role-specific value. This should override the default 
		 * value for the specified role and RE-USE the new role-specific
		 * Configuration just created, i.e., not create a new Configuration
		 */
		/*
		 * Set role-specific value, which should override the default value for
		 * the specified role.
		 */
		newStringValue = "Updated role-specific value";
		configurationService.set(ParamName.TEST_STRING, newStringValue, role_bcbc);
		/*
		 * This should have re-used the Configuration just created.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		stringValueRoleSpecificObject = configurationService.get(ParamName.TEST_STRING, role_bcbc);
		assertThat(stringValueRoleSpecificObject, is(instanceOf(String.class)));
		assertThat((String) stringValueRoleSpecificObject, is(newStringValue));

		/*
		 * The default value for ParamName.TEST_STRING should still be the
		 * same.
		 */
		Object stringValueDefaultObject = configurationService.get(ParamName.TEST_STRING);
		assertThat(stringValueDefaultObject, is(instanceOf(String.class)));
		assertThat((String) stringValueDefaultObject, is(TEST_STRING_DEFAULT_VALUE));
	}

	// ======================== Text parameter tests =========================

	@Test
	@Transactional
	public void textValueFetchDefaultFromConfig() {
		Object textValueObject = configurationService.get(ParamName.TEST_TEXT);
		assertThat(textValueObject, is(instanceOf(String.class)));
		assertThat((String) textValueObject, is(TEST_TEXT_DEFAULT_VALUE));
	}

	/*
	 * Get role-specific text value for a parameter that has both a default 
	 * (Role==null) Configuration, and a role-specific value. This should
	 * fetch the role-specific value.
	 */
	@Test
	@Transactional
	public void textValueGetRoleSpecificWithConfig() {
		/*
		 * Role "aabb" has a role-specific value for the TEST_TEXT parameter.
		 */
		UUID uuidOfRole_aabb = UUID.fromString("ee56f34d-dbb4-41c1-9d30-ce29cf973820");
		Role role_aabb = roleRepository.findOne(uuidOfRole_aabb);
		assertThat(role_aabb, is(notNullValue()));
		/*
		 * Fetch the role-specific value for the TEST_TEXT parameter for Role 
		 * "aabb".
		 */
		Object textValueObject = configurationService.get(ParamName.TEST_TEXT, role_aabb);
		assertThat(textValueObject, is(instanceOf(String.class)));
		assertThat((String) textValueObject, is(TEST_TEXT_ROLE_aabb_VALUE));
	}

	/*
	 * Attempt to fetch a role-specific value value that does not exist in the
	 * [configuration] table. However, there *is* a default value for the 
	 * parameter, which should be returned instead.
	 */
	@Test
	@Transactional
	public void textValueFetchNonexistentRoleSpecificFromConfig() {
		UUID uuidOfRole_bcbc = UUID.fromString("e918c8aa-c6d1-462c-9e91-f1db0fb9f346");
		Role role_bcbc = roleRepository.findOne(uuidOfRole_bcbc);
		assertThat(role_bcbc, is(notNullValue()));
		/*
		 * There is a default value for ParamName.TEST_TEXT, but no
		 * role-specific value for Role "bcbc".
		 */
		Object textValueObject = configurationService.get(ParamName.TEST_TEXT, role_bcbc);
		assertThat(textValueObject, is(instanceOf(String.class)));
		assertThat((String) textValueObject, is(TEST_TEXT_DEFAULT_VALUE));
	}

	/*
	 * Set default text value for a parameter that already has a default 
	 * (Role==null) Configuration. This should update the existing Configuration
	 * entity to store this new value.
	 */
	@Test
	@Transactional
	public void textValueSetDefaultWithConfig() {
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/*
		 * Retrieve the existing default value.
		 */
		Object textValueDefaultObject = configurationService.get(ParamName.TEST_TEXT);
		assertThat(textValueDefaultObject, is(instanceOf(String.class)));
		assertThat((String) textValueDefaultObject, is(TEST_TEXT_DEFAULT_VALUE));
		/*
		 * Update default value.
		 */
		String newTextValue = "New default text value, which could be very long";
		configurationService.set(ParamName.TEST_TEXT, newTextValue);
		/*
		 * The existing Configuration should have been updated, so there should
		 * be the same number of entities in the database
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/* 
		 * Retrieve value just set.
		 */
		Object textValueNewDefaultObject = configurationService.get(ParamName.TEST_TEXT);
		assertThat(textValueNewDefaultObject, is(instanceOf(String.class)));
		assertThat((String) textValueNewDefaultObject, is(newTextValue));
	}

	/*
	 * Set role-specific text value for a parameter that has a default 
	 * (Role==null) Configuration, but no role-specific value. This should
	 * create a new Configuration entity to store this value.
	 */
	@Test
	@Transactional
	public void textValueSetRoleSpecificWithConfig() {
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));

		UUID uuidOfRole_bcbc = UUID.fromString("e918c8aa-c6d1-462c-9e91-f1db0fb9f346");
		Role role_bcbc = roleRepository.findOne(uuidOfRole_bcbc);
		assertThat(role_bcbc, is(notNullValue()));
		/*
		 * There is a default value for ParamName.TEST_TEXT, but no
		 * role-specific value for Role "bcbc".
		 */
		Object textValueObject = configurationService.get(ParamName.TEST_TEXT, role_bcbc);
		assertThat(textValueObject, is(instanceOf(String.class)));
		assertThat((String) textValueObject, is(TEST_TEXT_DEFAULT_VALUE));

		/*
		 * Set role-specific value, which should override the default value for
		 * the specified role.
		 */
		String newTextValue = "A potentially very long string.";
		configurationService.set(ParamName.TEST_TEXT, newTextValue, role_bcbc);
		/*
		 * This should have created a new Configuration.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		Object textValueRoleSpecificObject = configurationService.get(ParamName.TEST_TEXT, role_bcbc);
		assertThat(textValueRoleSpecificObject, is(instanceOf(String.class)));
		assertThat((String) textValueRoleSpecificObject, is(newTextValue));

		/*
		 * Update the role-specific value. This should override the default 
		 * value for the specified role and RE-USE the new role-specific
		 * Configuration just created, i.e., not create a new Configuration
		 */
		/*
		 * Set role-specific value, which should override the default value for
		 * the specified role.
		 */
		newTextValue = "Updated role-specific text value";
		configurationService.set(ParamName.TEST_TEXT, newTextValue, role_bcbc);
		/*
		 * This should have re-used the Configuration just created.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		textValueRoleSpecificObject = configurationService.get(ParamName.TEST_TEXT, role_bcbc);
		assertThat(textValueRoleSpecificObject, is(instanceOf(String.class)));
		assertThat((String) textValueRoleSpecificObject, is(newTextValue));

		/*
		 * The default value for ParamName.TEST_TEXT should still be the
		 * same.
		 */
		Object textValueDefaultObject = configurationService.get(ParamName.TEST_TEXT);
		assertThat(textValueDefaultObject, is(instanceOf(String.class)));
		assertThat((String) textValueDefaultObject, is(TEST_TEXT_DEFAULT_VALUE));
	}

	// ======================== Time parameter tests ===========================

	/*
	 * Get role-specific time value for a parameter that has both a default 
	 * (Role==null) Configuration, and a role-specific value. This should
	 * fetch the role-specific value.
	 */
	@Test
	@Transactional
	public void timeValueGetRoleSpecificWithConfig() {
		/*
		 * Role "aabb" has a role-specific value for the TEST_TIME parameter.
		 */
		UUID uuidOfRole_aabb = UUID.fromString("ee56f34d-dbb4-41c1-9d30-ce29cf973820");
		Role role_aabb = roleRepository.findOne(uuidOfRole_aabb);
		assertThat(role_aabb, is(notNullValue()));
		/*
		 * Fetch the role-specific value for the TEST_TIME parameter for Role 
		 * "aabb".
		 */
		Object timeValueObject = configurationService.get(ParamName.TEST_TIME, role_aabb);
		assertThat(timeValueObject, is(instanceOf(Date.class)));

		Date timeFromConfig = (Date) timeValueObject;
		assertThat(timeFromConfig, is(not(nullValue())));

		assertThat(DateUtils.timePartsEqual(timeFromConfig, TEST_TIME_ROLE_aabb_VALUE), is(true));
	}

	/*
	 * Set default time value for a parameter that already has a default 
	 * (Role==null) Configuration. This should update the existing Configuration
	 * entity to store this new value.
	 */
	@Test
	@Transactional
	public void timeValueSetDefaultWithConfig() {
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/*
		 * Retrieve the existing default value.
		 */
		Object timeValueObject = configurationService.get(ParamName.TEST_TIME);
		assertThat(timeValueObject, is(instanceOf(Date.class)));
		Date timeFromConfig = (Date) timeValueObject;

		/*
		 * [configuration]time_value contains '16:17:18' in the database. The 
		 * string value logged here is:
		 * 
		 *     16:17:18
		 */
		//		logger.info("timeFromConfig = {}", timeFromConfig);
		/*
		 * If this changes, it means that dates are being treated differently
		 * by the database or the ORM:
		 */
		assertThat(timeFromConfig.toString(), is("16:17:18"));
		assertThat(timeFromConfig, is(not(nullValue())));
		assertThat(DateUtils.timePartsEqual(timeFromConfig, TEST_TIME_DEFAULT_VALUE), is(true));

		/*
		 * Update default value.
		 * 
		 * The year, month & day here (1800, 6, 15) are arbitrary and are not
		 * used because it is only the time portion that we work with.
		 */
		Date newTimeValue = new GregorianCalendar(1800, 6, 15, 13, 45, 59).getTime();
		configurationService.set(ParamName.TEST_TIME, newTimeValue);
		/*
		* The existing Configuration should have been updated, so there should
		* be the same number of entities in the database
		*/
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/* 
		 * Retrieve value just set.
		 */
		Object timeValueObjectUpdated = configurationService.get(ParamName.TEST_TIME);
		assertThat(timeValueObjectUpdated, is(instanceOf(Date.class)));
		Date timeFromConfigUpdated = (Date) timeValueObjectUpdated;
		assertThat(timeFromConfigUpdated, is(not(nullValue())));
		assertThat(DateUtils.timePartsEqual(timeFromConfigUpdated, newTimeValue), is(true));
	}

	/*
	 * Set role-specific time value for a parameter that has a default 
	 * (Role==null) Configuration, but no role-specific value. This should
	 * create a new Configuration entity to store this value.
	 */
	@Test
	@Transactional
	public void timeValueSetRoleSpecificWithConfig() {
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));

		UUID uuidOfRole_bcbc = UUID.fromString("e918c8aa-c6d1-462c-9e91-f1db0fb9f346");
		Role role_bcbc = roleRepository.findOne(uuidOfRole_bcbc);
		assertThat(role_bcbc, is(notNullValue()));
		/*
		 * There is a default value for ParamName.TEST_TIME, but no
		 * role-specific value for Role "bcbc".
		 */
		Object timeValueObject = configurationService.get(ParamName.TEST_TIME, role_bcbc);
		assertThat(timeValueObject, is(instanceOf(Date.class)));
		Date timeFromConfig = (Date) timeValueObject;
		assertThat(timeFromConfig, is(not(nullValue())));
		assertThat(DateUtils.timePartsEqual(timeFromConfig, TEST_TIME_DEFAULT_VALUE), is(true));

		/*
		 * Set role-specific value, which should override the default value for
		 * the specified role.
		 * 
		 * The year, month & day here (1960, 4, 14) are arbitrary and are not
		 * used because it is only the time portion that we work with.
		 */
		Date newTimeValue = new GregorianCalendar(1960, 4, 14, 23, 30, 59).getTime();
		configurationService.set(ParamName.TEST_TIME, newTimeValue, role_bcbc);
		/*
		 * This should have created a new Configuration.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		Object timeValueRoleSpecificObject = configurationService.get(ParamName.TEST_TIME, role_bcbc);
		assertThat(timeValueRoleSpecificObject, is(instanceOf(Date.class)));
		Date timeFromRoleSpecific = (Date) timeValueRoleSpecificObject;
		assertThat(timeFromRoleSpecific, is(not(nullValue())));
		assertThat(DateUtils.timePartsEqual(timeFromRoleSpecific, newTimeValue), is(true));

		/*
		 * Update the role-specific value. This should override the default 
		 * value for the specified role and RE-USE the new role-specific
		 * Configuration just created, i.e., not create a new Configuration
		 * 
		 * The year, month & day here (1960, 4, 14) are arbitrary and are not
		 * used because it is only the time portion that we work with.
		 */
		newTimeValue = new GregorianCalendar(1961, 10, 4, 00, 00, 00).getTime();
		configurationService.set(ParamName.TEST_TIME, newTimeValue, role_bcbc);
		/*
		 * This should have created a new Configuration.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		timeValueRoleSpecificObject = configurationService.get(ParamName.TEST_TIME, role_bcbc);
		assertThat(timeValueRoleSpecificObject, is(instanceOf(Date.class)));
		timeFromRoleSpecific = (Date) timeValueRoleSpecificObject;
		assertThat(timeFromRoleSpecific, is(not(nullValue())));
		assertThat(DateUtils.timePartsEqual(timeFromRoleSpecific, newTimeValue), is(true));

		/*
		 * The default value for ParamName.TEST_TIME should still be the
		 * same.
		 */
		Object timeValueDefaultObject = configurationService.get(ParamName.TEST_TIME);
		assertThat(timeValueDefaultObject, is(instanceOf(Date.class)));
		timeFromConfig = (Date) timeValueDefaultObject;
		assertThat(timeFromConfig, is(not(nullValue())));
		assertThat(DateUtils.timePartsEqual(timeFromConfig, TEST_TIME_DEFAULT_VALUE), is(true));
	}

}
