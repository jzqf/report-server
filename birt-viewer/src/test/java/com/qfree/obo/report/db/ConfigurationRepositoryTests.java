package com.qfree.obo.report.db;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import org.joda.time.LocalTime;
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
public class ConfigurationRepositoryTests {

	private static final Logger logger = LoggerFactory.getLogger(ConfigurationRepositoryTests.class);

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
	private static final LocalTime TEST_TIME_DEFAULT_VALUE_JODA = new LocalTime(TEST_TIME_DEFAULT_VALUE);
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
	private static final LocalTime TEST_TIME_ROLE_aabb_VALUE_JODA = new LocalTime(TEST_TIME_ROLE_aabb_VALUE);

	@Autowired
	ConfigurationRepository configurationRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	Config config;

	@Test
	@Transactional
	public void countTestRecords() {
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		assertThat(configurationRepository.count(), is(equalTo(NUM_TEST_CONFIGURATIONS)));
	}

	// ======================== Boolean parameter tests ========================

	@Test
	@Transactional
	public void booleanValueExistsDefault() {
		UUID uuidOfDefaultBooleanValueConfiguration = UUID.fromString("1f4f4814-577f-426d-bb98-4cc47446d578");
		Configuration defaultBooleanValueConfiguration = configurationRepository
				.findOne(uuidOfDefaultBooleanValueConfiguration);
		assertThat(defaultBooleanValueConfiguration, is(not(nullValue())));
		assertThat(defaultBooleanValueConfiguration.getBooleanValue(), is(TEST_BOOLEAN_DEFAULT_VALUE));
	}

	@Test
	@Transactional
	public void booleanValueFetchDefault() {
		Configuration configuration = configurationRepository.findByParamName(ParamName.TEST_BOOLEAN);
		assertThat(configuration, is(not(nullValue())));
		Boolean booleanValue = configuration.getBooleanValue();
		assertThat(booleanValue, is(not(nullValue())));
		assertThat(booleanValue, is(TEST_BOOLEAN_DEFAULT_VALUE));
	}

	@Test
	@Transactional
	public void booleanValueFetchDefaultFromConfig() {
		Object booleanValueObject = config.get(ParamName.TEST_BOOLEAN);
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
		Object booleanValueObject = config.get(ParamName.TEST_BOOLEAN, role_aabb);
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
		Object booleanValueObject = config.get(ParamName.TEST_BOOLEAN, role_bcbc);
		assertThat(booleanValueObject, is(instanceOf(Boolean.class)));
		assertThat((Boolean) booleanValueObject, is(TEST_BOOLEAN_DEFAULT_VALUE));
	}

	/*
	 * Set default boolean value for a parameter that already has a default 
	 * (Role==null) Configuration. This should update the existing Configuration
	 * entity to store this new value.
	 */
	@Test
	@Transactional
	public void booleanValueSetDefaultWithConfig() {
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/*
		 * Retrieve the existing default value.
		 */
		Object booleanValueDefaultObject = config.get(ParamName.TEST_BOOLEAN);
		assertThat(booleanValueDefaultObject, is(instanceOf(Boolean.class)));
		assertThat((Boolean) booleanValueDefaultObject, is(TEST_BOOLEAN_DEFAULT_VALUE));
		/*
		 * Update default value.
		 */
		Boolean newBooleanValue = false;
		config.set(ParamName.TEST_BOOLEAN, newBooleanValue);
		/*
		 * The existing Configuration should have been updated, so there should
		 * be the same number of entities in the database
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/* 
		 * Retrieve value just set.
		 */
		Object booleanValueNewDefaultObject = config.get(ParamName.TEST_BOOLEAN);
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
		Object booleanValueObject = config.get(ParamName.TEST_BOOLEAN, role_bcbc);
		assertThat(booleanValueObject, is(instanceOf(Boolean.class)));
		assertThat((Boolean) booleanValueObject, is(TEST_BOOLEAN_DEFAULT_VALUE));

		/*
		 * Set role-specific value, which should override the default value for
		 * the specified role.
		 */
		Boolean newBooleanValue = false;
		config.set(ParamName.TEST_BOOLEAN, newBooleanValue, role_bcbc);
		/*
		 * This should have created a new Configuration.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		Object booleanValueRoleSpecificObject = config.get(ParamName.TEST_BOOLEAN, role_bcbc);
		assertThat(booleanValueRoleSpecificObject, is(instanceOf(Boolean.class)));
		assertThat((Boolean) booleanValueRoleSpecificObject, is(newBooleanValue));

		/*
		 * Update the role-specific value. This should override the default 
		 * value for the specified role and RE-USE the new role-specific
		 * Configuration just created, i.e., not create a new Configuration
		 */
		newBooleanValue = true;
		config.set(ParamName.TEST_BOOLEAN, newBooleanValue, role_bcbc);
		/*
		 * This should have re-used the Configuration just created.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		booleanValueRoleSpecificObject = config.get(ParamName.TEST_BOOLEAN, role_bcbc);
		assertThat(booleanValueRoleSpecificObject, is(instanceOf(Boolean.class)));
		assertThat((Boolean) booleanValueRoleSpecificObject, is(newBooleanValue));

		/*
		 * The default value for ParamName.TEST_BOOLEAN should still be the
		 * same.
		 */
		Object booleanValueDefaultObject = config.get(ParamName.TEST_BOOLEAN);
		assertThat(booleanValueDefaultObject, is(instanceOf(Boolean.class)));
		assertThat((Boolean) booleanValueDefaultObject, is(TEST_BOOLEAN_DEFAULT_VALUE));
	}

	// ======================== Date parameter tests ===========================

	@Test
	@Transactional
	public void dateValueExistsDefault() {
		UUID uuidOfDefaultDateValueConfiguration = UUID.fromString("7d325b3c-d307-4fd0-bdae-c349ec2d4835");
		Configuration defaultDateValueConfiguration = configurationRepository
				.findOne(uuidOfDefaultDateValueConfiguration);
		assertThat(defaultDateValueConfiguration, is(not(nullValue())));
		/*
		 * TEST_DATE_DEFAULT_VALUE has time zone information, but the 
		 * datetime from the Configuration does not. In order to compare the 
		 * datetime from the Configuration (for this unit test), we create a 
		 * new date from the date retrieved from the Configuration and then
		 * compared *that* Date WITH TEST_DATE_DEFAULT_VALUE.
		 */
		Date dateFromConfig = defaultDateValueConfiguration.getDateValue();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(dateFromConfig);
		Date dateWithTimeZone = calendar.getTime();
		assertThat(dateWithTimeZone, is(equalTo(TEST_DATE_DEFAULT_VALUE)));
	}

	@Test
	@Transactional
	public void dateValueFetchDefault() {
		Configuration configuration = configurationRepository.findByParamName(ParamName.TEST_DATE);
		assertThat(configuration, is(not(nullValue())));
		Date dateFromConfig = configuration.getDateValue();
		assertThat(dateFromConfig, is(not(nullValue())));
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(dateFromConfig);
		Date dateWithTimeZone = calendar.getTime();
		assertThat(dateWithTimeZone, is(equalTo(TEST_DATE_DEFAULT_VALUE)));
	}

	@Test
	@Transactional
	public void dateValueFetchDefaultFromConfig() {
		Object dateValueObject = config.get(ParamName.TEST_DATE);
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

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(dateFromConfig);
		Date dateWithTimeZone = calendar.getTime();
		assertThat(dateWithTimeZone, is(equalTo(TEST_DATE_DEFAULT_VALUE)));
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
		Object dateValueObject = config.get(ParamName.TEST_DATE, role_aabb);
		assertThat(dateValueObject, is(instanceOf(Date.class)));

		Date dateFromConfig = (Date) dateValueObject;
		assertThat(dateFromConfig, is(not(nullValue())));
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(dateFromConfig);
		Date dateWithTimeZone = calendar.getTime();
		assertThat(dateWithTimeZone, is(equalTo(TEST_DATE_ROLE_aabb_VALUE)));
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
		Object dateValueObject = config.get(ParamName.TEST_DATE, role_bcbc);
		assertThat(dateValueObject, is(instanceOf(Date.class)));

		Date dateFromConfig = (Date) dateValueObject;
		assertThat(dateFromConfig, is(not(nullValue())));
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(dateFromConfig);
		Date dateWithTimeZone = calendar.getTime();
		assertThat(dateWithTimeZone, is(equalTo(TEST_DATE_DEFAULT_VALUE)));
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
		Object dateValueObject = config.get(ParamName.TEST_DATE);
		assertThat(dateValueObject, is(instanceOf(Date.class)));
		Date dateFromConfig = (Date) dateValueObject;
		assertThat(dateFromConfig, is(not(nullValue())));
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(dateFromConfig);
		Date dateWithTimeZone = calendar.getTime();
		assertThat(dateWithTimeZone, is(equalTo(TEST_DATE_DEFAULT_VALUE)));
		/*
		 * Update default value.
		 */
		Date newDateValue = new GregorianCalendar(1927, 1, 15).getTime();
		config.set(ParamName.TEST_DATE, newDateValue);
		/*
		 * The existing Configuration should have been updated, so there should
		 * be the same number of entities in the database
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/* 
		 * Retrieve value just set.
		 */
		Object dateValueObjectUpdated = config.get(ParamName.TEST_DATE);
		assertThat(dateValueObjectUpdated, is(instanceOf(Date.class)));
		Date dateFromConfigUpdated = (Date) dateValueObjectUpdated;
		assertThat(dateFromConfigUpdated, is(not(nullValue())));
		Calendar calendarUpdated = new GregorianCalendar();
		calendarUpdated.setTime(dateFromConfigUpdated);
		Date dateWithTimeZoneUpdated = calendarUpdated.getTime();
		assertThat(dateWithTimeZoneUpdated, is(equalTo(newDateValue)));
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
		Object dateValueObject = config.get(ParamName.TEST_DATE, role_bcbc);
		assertThat(dateValueObject, is(instanceOf(Date.class)));
		Date dateFromConfig = (Date) dateValueObject;
		assertThat(dateFromConfig, is(not(nullValue())));
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(dateFromConfig);
		Date dateWithTimeZone = calendar.getTime();
		assertThat(dateWithTimeZone, is(equalTo(TEST_DATE_DEFAULT_VALUE)));

		/*
		 * Set role-specific value, which should override the default value for
		 * the specified role.
		 */
		Date newDateValue = new GregorianCalendar(1932, 3, 17).getTime();
		config.set(ParamName.TEST_DATE, newDateValue, role_bcbc);
		/*
		 * This should have created a new Configuration.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		Object dateValueRoleSpecificObject = config.get(ParamName.TEST_DATE, role_bcbc);
		assertThat(dateValueRoleSpecificObject, is(instanceOf(Date.class)));
		Date dateFromRoleSpecific = (Date) dateValueRoleSpecificObject;
		assertThat(dateFromRoleSpecific, is(not(nullValue())));
		Calendar calendarUpdated = new GregorianCalendar();
		calendarUpdated.setTime(dateFromRoleSpecific);
		Date dateWithTimeZoneUpdated = calendarUpdated.getTime();
		assertThat(dateWithTimeZoneUpdated, is(equalTo(newDateValue)));

		/*
		 * Update the role-specific value. This should override the default 
		 * value for the specified role and RE-USE the new role-specific
		 * Configuration just created, i.e., not create a new Configuration
		 */
		newDateValue = new GregorianCalendar(1955, 2, 24).getTime();
		config.set(ParamName.TEST_DATE, newDateValue, role_bcbc);
		/*
		 * This should have created a new Configuration.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		dateValueRoleSpecificObject = config.get(ParamName.TEST_DATE, role_bcbc);
		assertThat(dateValueRoleSpecificObject, is(instanceOf(Date.class)));
		dateFromRoleSpecific = (Date) dateValueRoleSpecificObject;
		assertThat(dateFromRoleSpecific, is(not(nullValue())));
		calendarUpdated = new GregorianCalendar();
		calendarUpdated.setTime(dateFromRoleSpecific);
		dateWithTimeZoneUpdated = calendarUpdated.getTime();
		assertThat(dateWithTimeZoneUpdated, is(equalTo(newDateValue)));

		/*
		 * The default value for ParamName.TEST_DATE should still be the
		 * same.
		 */
		Object dateValueDefaultObject = config.get(ParamName.TEST_DATE);
		assertThat(dateValueDefaultObject, is(instanceOf(Date.class)));
		dateFromConfig = (Date) dateValueDefaultObject;
		assertThat(dateFromConfig, is(not(nullValue())));
		calendar = new GregorianCalendar();
		calendar.setTime(dateFromConfig);
		dateWithTimeZone = calendar.getTime();
		assertThat(dateWithTimeZone, is(equalTo(TEST_DATE_DEFAULT_VALUE)));
	}

	// ====================== Datetime parameter tests =========================

	@Test
	@Transactional
	public void datetimeValueExistsDefault() {
		UUID uuidOfDefaultDatetimeValueConfiguration = UUID.fromString("5e7d5a1e-5d42-4a9c-b790-e45e545463f7");
		Configuration defaultDatetimeValueConfiguration = configurationRepository
				.findOne(uuidOfDefaultDatetimeValueConfiguration);
		assertThat(defaultDatetimeValueConfiguration, is(not(nullValue())));
		/*
		 * TEST_DATETIME_DEFAULT_VALUE has time zone information, but the 
		 * datetime from the Configuration does not. In order to compare the 
		 * datetime from the Configuration (for this unit test), we create a 
		 * new date from the date retrieved from the Configuration and then
		 * compared *that* Date WITH TEST_DATETIME_DEFAULT_VALUE.
		 */
		Date datetimeFromConfig = defaultDatetimeValueConfiguration.getDatetimeValue();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(datetimeFromConfig);
		Date dateWithTimeZone = calendar.getTime();
		assertThat(dateWithTimeZone, is(equalTo(TEST_DATETIME_DEFAULT_VALUE)));
	}

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
		Object datetimeValueObject = config.get(ParamName.TEST_DATETIME, role_aabb);
		assertThat(datetimeValueObject, is(instanceOf(Date.class)));

		Date datetimeFromConfig = (Date) datetimeValueObject;
		assertThat(datetimeFromConfig, is(not(nullValue())));
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(datetimeFromConfig);
		Date datetimeWithTimeZone = calendar.getTime();
		assertThat(datetimeWithTimeZone, is(equalTo(TEST_DATETIME_ROLE_aabb_VALUE)));
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
		Object datetimeValueObject = config.get(ParamName.TEST_DATETIME);
		assertThat(datetimeValueObject, is(instanceOf(Date.class)));
		Date datetimeFromConfig = (Date) datetimeValueObject;
		assertThat(datetimeFromConfig, is(not(nullValue())));
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(datetimeFromConfig);
		Date datetimeWithTimeZone = calendar.getTime();
		assertThat(datetimeWithTimeZone, is(equalTo(TEST_DATETIME_DEFAULT_VALUE)));
		/*
		 * Update default value.
		 */
		Date newDatetimeValue = new GregorianCalendar(1960, 4, 14, 1, 2, 3).getTime();
		config.set(ParamName.TEST_DATETIME, newDatetimeValue);
		/*
		 * The existing Configuration should have been updated, so there should
		 * be the same number of entities in the database
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/* 
		 * Retrieve value just set.
		 */
		Object datetimeValueObjectUpdated = config.get(ParamName.TEST_DATETIME);
		assertThat(datetimeValueObjectUpdated, is(instanceOf(Date.class)));
		Date datetimeFromConfigUpdated = (Date) datetimeValueObjectUpdated;
		assertThat(datetimeFromConfigUpdated, is(not(nullValue())));
		Calendar calendarUpdated = new GregorianCalendar();
		calendarUpdated.setTime(datetimeFromConfigUpdated);
		Date datetimeWithTimeZoneUpdated = calendarUpdated.getTime();
		assertThat(datetimeWithTimeZoneUpdated, is(equalTo(newDatetimeValue)));
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
		Object datetimeValueObject = config.get(ParamName.TEST_DATETIME, role_bcbc);
		assertThat(datetimeValueObject, is(instanceOf(Date.class)));
		Date datetimeFromConfig = (Date) datetimeValueObject;
		assertThat(datetimeFromConfig, is(not(nullValue())));
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(datetimeFromConfig);
		Date datetimeWithTimeZone = calendar.getTime();
		assertThat(datetimeWithTimeZone, is(equalTo(TEST_DATETIME_DEFAULT_VALUE)));

		/*
		 * Set role-specific value, which should override the default value for
		 * the specified role.
		 */
		Date newDatetimeValue = new GregorianCalendar(1960, 4, 14, 1, 2, 3).getTime();
		config.set(ParamName.TEST_DATETIME, newDatetimeValue, role_bcbc);
		/*
		 * This should have created a new Configuration.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		Object datetimeValueRoleSpecificObject = config.get(ParamName.TEST_DATETIME, role_bcbc);
		assertThat(datetimeValueRoleSpecificObject, is(instanceOf(Date.class)));
		Date datetimeFromRoleSpecific = (Date) datetimeValueRoleSpecificObject;
		assertThat(datetimeFromRoleSpecific, is(not(nullValue())));
		Calendar calendarUpdated = new GregorianCalendar();
		calendarUpdated.setTime(datetimeFromRoleSpecific);
		Date datetimeWithTimeZoneUpdated = calendarUpdated.getTime();
		assertThat(datetimeWithTimeZoneUpdated, is(equalTo(newDatetimeValue)));

		/*
		 * Update the role-specific value. This should override the default 
		 * value for the specified role and RE-USE the new role-specific
		 * Configuration just created, i.e., not create a new Configuration
		 */
		newDatetimeValue = new GregorianCalendar(1961, 10, 4, 18, 30, 15).getTime();
		config.set(ParamName.TEST_DATETIME, newDatetimeValue, role_bcbc);
		/*
		 * This should have created a new Configuration.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		datetimeValueRoleSpecificObject = config.get(ParamName.TEST_DATETIME, role_bcbc);
		assertThat(datetimeValueRoleSpecificObject, is(instanceOf(Date.class)));
		datetimeFromRoleSpecific = (Date) datetimeValueRoleSpecificObject;
		assertThat(datetimeFromRoleSpecific, is(not(nullValue())));
		calendarUpdated = new GregorianCalendar();
		calendarUpdated.setTime(datetimeFromRoleSpecific);
		datetimeWithTimeZoneUpdated = calendarUpdated.getTime();
		assertThat(datetimeWithTimeZoneUpdated, is(equalTo(newDatetimeValue)));

		/*
		 * The default value for ParamName.TEST_DATETIME should still be the
		 * same.
		 */
		Object datetimeValueDefaultObject = config.get(ParamName.TEST_DATETIME);
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
		calendar = new GregorianCalendar();
		calendar.setTime(datetimeFromConfig);
		datetimeWithTimeZone = calendar.getTime();
		assertThat(datetimeWithTimeZone, is(equalTo(TEST_DATETIME_DEFAULT_VALUE)));
	}

	// ======================== Double parameter tests ========================

	@Test
	@Transactional
	public void doubleValueExistsDefault() {
		UUID uuidOfDefaultDoubleValueConfiguration = UUID.fromString("4bedeeae-6436-4c64-862b-4e87ffaa4527");
		Configuration defaultDoubleValueConfiguration = configurationRepository
				.findOne(uuidOfDefaultDoubleValueConfiguration);
		assertThat(defaultDoubleValueConfiguration, is(not(nullValue())));
		assertThat(defaultDoubleValueConfiguration.getDoubleValue(), is(TEST_DOUBLE_DEFAULT_VALUE));
	}

	@Test
	@Transactional
	public void doubleValueFetchDefault() {
		Configuration configuration = configurationRepository.findByParamName(ParamName.TEST_DOUBLE);
		assertThat(configuration, is(not(nullValue())));
		Double doubleValue = configuration.getDoubleValue();
		assertThat(doubleValue, is(not(nullValue())));
		assertThat(doubleValue, is(TEST_DOUBLE_DEFAULT_VALUE));
	}

	@Test
	@Transactional
	public void doubleValueFetchDefaultFromConfig() {
		Object doubleValueObject = config.get(ParamName.TEST_DOUBLE);
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
		Object doubleValueObject = config.get(ParamName.TEST_DOUBLE, role_aabb);
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
		Object doubleValueObject = config.get(ParamName.TEST_DOUBLE, role_bcbc);
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
		Object doubleValueDefaultObject = config.get(ParamName.TEST_DOUBLE);
		assertThat(doubleValueDefaultObject, is(instanceOf(Double.class)));
		assertThat((Double) doubleValueDefaultObject, is(TEST_DOUBLE_DEFAULT_VALUE));
		/*
		 * Update default value.
		 */
		Double newDoubleValue = 111222333.444;
		config.set(ParamName.TEST_DOUBLE, newDoubleValue);
		/*
		 * The existing Configuration should have been updated, so there should
		 * be the same number of entities in the database
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/* 
		 * Retrieve value just set.
		 */
		Object doubleValueNewDefaultObject = config.get(ParamName.TEST_DOUBLE);
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
		Object doubleValueObject = config.get(ParamName.TEST_DOUBLE, role_bcbc);
		assertThat(doubleValueObject, is(instanceOf(Double.class)));
		assertThat((Double) doubleValueObject, is(TEST_DOUBLE_DEFAULT_VALUE));

		/*
		 * Set role-specific value, which should override the default value for
		 * the specified role.
		 */
		Double newDoubleValue = 483.128444;
		config.set(ParamName.TEST_DOUBLE, newDoubleValue, role_bcbc);
		/*
		 * This should have created a new Configuration.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		Object doubleValueRoleSpecificObject = config.get(ParamName.TEST_DOUBLE, role_bcbc);
		assertThat(doubleValueRoleSpecificObject, is(instanceOf(Double.class)));
		assertThat((Double) doubleValueRoleSpecificObject, is(newDoubleValue));

		/*
		 * Update the role-specific value. This should override the default 
		 * value for the specified role and RE-USE the new role-specific
		 * Configuration just created, i.e., not create a new Configuration
		 */
		newDoubleValue = 0.000001;
		config.set(ParamName.TEST_DOUBLE, newDoubleValue, role_bcbc);
		/*
		 * This should have re-used the Configuration just created.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		doubleValueRoleSpecificObject = config.get(ParamName.TEST_DOUBLE, role_bcbc);
		assertThat(doubleValueRoleSpecificObject, is(instanceOf(Double.class)));
		assertThat((Double) doubleValueRoleSpecificObject, is(newDoubleValue));

		/*
		 * The default value for ParamName.TEST_DOUBLE should still be the
		 * same.
		 */
		Object doubleValueDefaultObject = config.get(ParamName.TEST_DOUBLE);
		assertThat(doubleValueDefaultObject, is(instanceOf(Double.class)));
		assertThat((Double) doubleValueDefaultObject, is(TEST_DOUBLE_DEFAULT_VALUE));
	}

	// ======================== Float parameter tests ========================

	@Test
	@Transactional
	public void floatValueExistsDefault() {
		UUID uuidOfDefaultFloatValueConfiguration = UUID.fromString("d34f8309-56e3-4328-9227-0d8c1e57a941");
		Configuration defaultFloatValueConfiguration = configurationRepository
				.findOne(uuidOfDefaultFloatValueConfiguration);
		assertThat(defaultFloatValueConfiguration, is(not(nullValue())));
		assertThat(defaultFloatValueConfiguration.getFloatValue(), is(TEST_FLOAT_DEFAULT_VALUE));
	}

	@Test
	@Transactional
	public void floatValueFetchDefault() {
		Configuration configuration = configurationRepository.findByParamName(ParamName.TEST_FLOAT);
		assertThat(configuration, is(not(nullValue())));
		Float floatValue = configuration.getFloatValue();
		assertThat(floatValue, is(not(nullValue())));
		assertThat(floatValue, is(TEST_FLOAT_DEFAULT_VALUE));
	}

	@Test
	@Transactional
	public void floatValueFetchDefaultFromConfig() {
		Object floatValueObject = config.get(ParamName.TEST_FLOAT);
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
		Object floatValueObject = config.get(ParamName.TEST_FLOAT, role_aabb);
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
		Object floatValueObject = config.get(ParamName.TEST_FLOAT, role_bcbc);
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
		Object floatValueDefaultObject = config.get(ParamName.TEST_FLOAT);
		assertThat(floatValueDefaultObject, is(instanceOf(Float.class)));
		assertThat((Float) floatValueDefaultObject, is(TEST_FLOAT_DEFAULT_VALUE));
		/*
		 * Update default value.
		 */
		Float newFloatValue = 1234.56F;
		config.set(ParamName.TEST_FLOAT, newFloatValue);
		/*
		 * The existing Configuration should have been updated, so there should
		 * be the same number of entities in the database
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/* 
		 * Retrieve value just set.
		 */
		Object floatValueNewDefaultObject = config.get(ParamName.TEST_FLOAT);
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
		Object floatValueObject = config.get(ParamName.TEST_FLOAT, role_bcbc);
		assertThat(floatValueObject, is(instanceOf(Float.class)));
		assertThat((Float) floatValueObject, is(TEST_FLOAT_DEFAULT_VALUE));

		/*
		 * Set role-specific value, which should override the default value for
		 * the specified role.
		 */
		Float newFloatValue = 82.583F;
		config.set(ParamName.TEST_FLOAT, newFloatValue, role_bcbc);
		/*
		 * This should have created a new Configuration.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		Object floatValueRoleSpecificObject = config.get(ParamName.TEST_FLOAT, role_bcbc);
		assertThat(floatValueRoleSpecificObject, is(instanceOf(Float.class)));
		assertThat((Float) floatValueRoleSpecificObject, is(newFloatValue));

		/*
		 * Update the role-specific value. This should override the default 
		 * value for the specified role and RE-USE the new role-specific
		 * Configuration just created, i.e., not create a new Configuration
		 */
		newFloatValue = 0.001F;
		config.set(ParamName.TEST_FLOAT, newFloatValue, role_bcbc);
		/*
		 * This should have re-used the Configuration just created.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		floatValueRoleSpecificObject = config.get(ParamName.TEST_FLOAT, role_bcbc);
		assertThat(floatValueRoleSpecificObject, is(instanceOf(Float.class)));
		assertThat((Float) floatValueRoleSpecificObject, is(newFloatValue));

		/*
		 * The default value for ParamName.TEST_FLOAT should still be the
		 * same.
		 */
		Object floatValueDefaultObject = config.get(ParamName.TEST_FLOAT);
		assertThat(floatValueDefaultObject, is(instanceOf(Float.class)));
		assertThat((Float) floatValueDefaultObject, is(TEST_FLOAT_DEFAULT_VALUE));
	}

	// ======================== Integer parameter tests ========================

	@Test
	@Transactional
	public void integerValueExistsDefault() {
		UUID uuidOfDefaultIntegerValueConfiguration = UUID.fromString("b4fd2271-26fb-4db7-bfe7-07211d849364");
		Configuration defaultIntegerValueConfiguration = configurationRepository
				.findOne(uuidOfDefaultIntegerValueConfiguration);
		assertThat(defaultIntegerValueConfiguration, is(not(nullValue())));
		assertThat(defaultIntegerValueConfiguration.getIntegerValue(), is(TEST_INTEGER_DEFAULT_VALUE));
	}

	@Test
	@Transactional
	public void integerValueFetchDefault() {
		Configuration configuration = configurationRepository.findByParamName(ParamName.TEST_INTEGER);
		assertThat(configuration, is(not(nullValue())));
		Integer integerValue = configuration.getIntegerValue();
		assertThat(integerValue, is(not(nullValue())));
		assertThat(integerValue, is(TEST_INTEGER_DEFAULT_VALUE));
	}

	@Test
	@Transactional
	public void integerValueFetchDefaultFromConfig() {
		Object integerValueObject = config.get(ParamName.TEST_INTEGER);
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
		Object integerValueObject = config.get(ParamName.TEST_INTEGER, role_aabb);
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
		Object integerValueObject = config.get(ParamName.TEST_INTEGER, role_bcbc);
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
		Object integerValueDefaultObject = config.get(ParamName.TEST_INTEGER);
		assertThat(integerValueDefaultObject, is(instanceOf(Integer.class)));
		assertThat((Integer) integerValueDefaultObject, is(TEST_INTEGER_DEFAULT_VALUE));
		/*
		 * Update default value.
		 */
		Integer newIntegerValue = 123456;
		config.set(ParamName.TEST_INTEGER, newIntegerValue);
		/*
		 * The existing Configuration should have been updated, so there should
		 * be the same number of entities in the database
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/* 
		 * Retrieve value just set.
		 */
		Object integerValueNewDefaultObject = config.get(ParamName.TEST_INTEGER);
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
		Object integerValueObject = config.get(ParamName.TEST_INTEGER, role_bcbc);
		assertThat(integerValueObject, is(instanceOf(Integer.class)));
		assertThat((Integer) integerValueObject, is(TEST_INTEGER_DEFAULT_VALUE));

		/*
		 * Set role-specific value, which should override the default value for
		 * the specified role.
		 */
		Integer newIntegerValue = 1000001;
		config.set(ParamName.TEST_INTEGER, newIntegerValue, role_bcbc);
		/*
		 * This should have created a new Configuration.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		Object integerValueRoleSpecificObject = config.get(ParamName.TEST_INTEGER, role_bcbc);
		assertThat(integerValueRoleSpecificObject, is(instanceOf(Integer.class)));
		assertThat((Integer) integerValueRoleSpecificObject, is(newIntegerValue));

		/*
		 * Update the role-specific value. This should override the default 
		 * value for the specified role and RE-USE the new role-specific
		 * Configuration just created, i.e., not create a new Configuration
		 */
		newIntegerValue = 666666;
		config.set(ParamName.TEST_INTEGER, newIntegerValue, role_bcbc);
		/*
		 * This should have re-used the Configuration just created.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		integerValueRoleSpecificObject = config.get(ParamName.TEST_INTEGER, role_bcbc);
		assertThat(integerValueRoleSpecificObject, is(instanceOf(Integer.class)));
		assertThat((Integer) integerValueRoleSpecificObject, is(newIntegerValue));

		/*
		 * The default value for ParamName.TEST_INTEGER should still be the
		 * same.
		 */
		Object integerValueDefaultObject = config.get(ParamName.TEST_INTEGER);
		assertThat(integerValueDefaultObject, is(instanceOf(Integer.class)));
		assertThat((Integer) integerValueDefaultObject, is(TEST_INTEGER_DEFAULT_VALUE));
	}

	// ======================== Long parameter tests ========================

	@Test
	@Transactional
	public void longValueExistsDefault() {
		UUID uuidOfDefaultLongValueConfiguration = UUID.fromString("334051cf-bea6-47d9-8bac-1b7c314ae985");
		Configuration defaultLongValueConfiguration = configurationRepository
				.findOne(uuidOfDefaultLongValueConfiguration);
		assertThat(defaultLongValueConfiguration, is(not(nullValue())));
		assertThat(defaultLongValueConfiguration.getLongValue(), is(TEST_LONG_DEFAULT_VALUE));
	}

	@Test
	@Transactional
	public void longValueFetchDefault() {
		Configuration configuration = configurationRepository.findByParamName(ParamName.TEST_LONG);
		assertThat(configuration, is(not(nullValue())));
		Long longValue = configuration.getLongValue();
		assertThat(longValue, is(not(nullValue())));
		assertThat(longValue, is(TEST_LONG_DEFAULT_VALUE));
	}

	@Test
	@Transactional
	public void longValueFetchDefaultFromConfig() {
		Object longValueObject = config.get(ParamName.TEST_LONG);
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
		Object longValueObject = config.get(ParamName.TEST_LONG, role_aabb);
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
		Object longValueObject = config.get(ParamName.TEST_LONG, role_bcbc);
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
		Object longValueDefaultObject = config.get(ParamName.TEST_LONG);
		assertThat(longValueDefaultObject, is(instanceOf(Long.class)));
		assertThat((Long) longValueDefaultObject, is(TEST_LONG_DEFAULT_VALUE));
		/*
		 * Update default value.
		 */
		Long newLongValue = 246801357901234L;
		config.set(ParamName.TEST_LONG, newLongValue);
		/*
		 * The existing Configuration should have been updated, so there should
		 * be the same number of entities in the database
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/* 
		 * Retrieve value just set.
		 */
		Object longValueNewDefaultObject = config.get(ParamName.TEST_LONG);
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
		Object longValueObject = config.get(ParamName.TEST_LONG, role_bcbc);
		assertThat(longValueObject, is(instanceOf(Long.class)));
		assertThat((Long) longValueObject, is(TEST_LONG_DEFAULT_VALUE));

		/*
		 * Set role-specific value, which should override the default value for
		 * the specified role.
		 */
		Long newLongValue = 246801357901234L;
		config.set(ParamName.TEST_LONG, newLongValue, role_bcbc);
		/*
		 * This should have created a new Configuration.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		Object longValueRoleSpecificObject = config.get(ParamName.TEST_LONG, role_bcbc);
		assertThat(longValueRoleSpecificObject, is(instanceOf(Long.class)));
		assertThat((Long) longValueRoleSpecificObject, is(newLongValue));

		/*
		 * Update the role-specific value. This should override the default 
		 * value for the specified role and RE-USE the new role-specific
		 * Configuration just created, i.e., not create a new Configuration
		 */
		newLongValue = 666666666666L;
		config.set(ParamName.TEST_LONG, newLongValue, role_bcbc);
		/*
		 * This should have re-used the Configuration just created.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		longValueRoleSpecificObject = config.get(ParamName.TEST_LONG, role_bcbc);
		assertThat(longValueRoleSpecificObject, is(instanceOf(Long.class)));
		assertThat((Long) longValueRoleSpecificObject, is(newLongValue));

		/*
		 * The default value for ParamName.TEST_LONG should still be the
		 * same.
		 */
		Object longValueDefaultObject = config.get(ParamName.TEST_LONG);
		assertThat(longValueDefaultObject, is(instanceOf(Long.class)));
		assertThat((Long) longValueDefaultObject, is(TEST_LONG_DEFAULT_VALUE));
	}

	// ======================== String parameter tests =========================

	@Test
	@Transactional
	public void stringValueExistsDefault() {
		UUID uuidOfDefaultStringValueConfiguration = UUID.fromString("62f9ca07-79ce-48dd-8357-873191ea8b9d");
		Configuration defaultStringValueConfiguration = configurationRepository
				.findOne(uuidOfDefaultStringValueConfiguration);
		assertThat(defaultStringValueConfiguration, is(not(nullValue())));
		assertThat(defaultStringValueConfiguration.getStringValue(), is(TEST_STRING_DEFAULT_VALUE));
	}

	@Test
	@Transactional
	public void stringValueFetchDefault() {
		Configuration configuration = configurationRepository.findByParamName(ParamName.TEST_STRING);
		assertThat(configuration, is(not(nullValue())));
		String stringValue = configuration.getStringValue();
		assertThat(stringValue, is(not(nullValue())));
		assertThat(stringValue, is(TEST_STRING_DEFAULT_VALUE));
	}

	@Test
	@Transactional
	public void stringValueFetchDefaultFromConfig() {
		Object stringValueObject = config.get(ParamName.TEST_STRING);
		assertThat(stringValueObject, is(instanceOf(String.class)));
		assertThat((String) stringValueObject, is(TEST_STRING_DEFAULT_VALUE));
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
		Object stringValueObject = config.get(ParamName.TEST_STRING, role_aabb);
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
		Object stringValueObject = config.get(ParamName.TEST_STRING, role_bcbc);
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
		Object stringValueObject = config.get(ParamName.TEST_NOTSET);
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
		Object stringValueDefaultObject = config.get(ParamName.TEST_STRING);
		assertThat(stringValueDefaultObject, is(instanceOf(String.class)));
		assertThat((String) stringValueDefaultObject, is(TEST_STRING_DEFAULT_VALUE));
		/*
		 * Update default value.
		 */
		String newStringValue = "New default value";
		config.set(ParamName.TEST_STRING, newStringValue);
		/*
		 * The existing Configuration should have been updated, so there should
		 * be the same number of entities in the database
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
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
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));

		UUID uuidOfRole_bcbc = UUID.fromString("e918c8aa-c6d1-462c-9e91-f1db0fb9f346");
		Role role_bcbc = roleRepository.findOne(uuidOfRole_bcbc);
		assertThat(role_bcbc, is(notNullValue()));
		/*
		 * There is a default value for ParamName.TEST_STRING, but no
		 * role-specific value for Role "bcbc".
		 */
		Object stringValueObject = config.get(ParamName.TEST_STRING, role_bcbc);
		assertThat(stringValueObject, is(instanceOf(String.class)));
		assertThat((String) stringValueObject, is(TEST_STRING_DEFAULT_VALUE));

		/*
		 * Set role-specific value, which should override the default value for
		 * the specified role.
		 */
		String newStringValue = "String value for role 'bcbc'";
		config.set(ParamName.TEST_STRING, newStringValue, role_bcbc);
		/*
		 * This should have created a new Configuration.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		Object stringValueRoleSpecificObject = config.get(ParamName.TEST_STRING, role_bcbc);
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
		config.set(ParamName.TEST_STRING, newStringValue, role_bcbc);
		/*
		 * This should have re-used the Configuration just created.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		stringValueRoleSpecificObject = config.get(ParamName.TEST_STRING, role_bcbc);
		assertThat(stringValueRoleSpecificObject, is(instanceOf(String.class)));
		assertThat((String) stringValueRoleSpecificObject, is(newStringValue));

		/*
		 * The default value for ParamName.TEST_STRING should still be the
		 * same.
		 */
		Object stringValueDefaultObject = config.get(ParamName.TEST_STRING);
		assertThat(stringValueDefaultObject, is(instanceOf(String.class)));
		assertThat((String) stringValueDefaultObject, is(TEST_STRING_DEFAULT_VALUE));
	}

	// ======================== Text parameter tests =========================

	@Test
	@Transactional
	public void textValueExistsDefault() {
		UUID uuidOfDefaultTextValueConfiguration = UUID.fromString("b0aa23ef-2be5-4041-a95f-b7615af639b5");
		Configuration defaultTextValueConfiguration = configurationRepository
				.findOne(uuidOfDefaultTextValueConfiguration);
		assertThat(defaultTextValueConfiguration, is(not(nullValue())));
		assertThat(defaultTextValueConfiguration.getTextValue(), is(TEST_TEXT_DEFAULT_VALUE));
	}

	@Test
	@Transactional
	public void textValueFetchDefault() {
		Configuration configuration = configurationRepository.findByParamName(ParamName.TEST_TEXT);
		assertThat(configuration, is(not(nullValue())));
		String textValue = configuration.getTextValue();
		assertThat(textValue, is(not(nullValue())));
		assertThat(textValue, is(TEST_TEXT_DEFAULT_VALUE));
	}

	@Test
	@Transactional
	public void textValueFetchDefaultFromConfig() {
		Object textValueObject = config.get(ParamName.TEST_TEXT);
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
		Object textValueObject = config.get(ParamName.TEST_TEXT, role_aabb);
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
		Object textValueObject = config.get(ParamName.TEST_TEXT, role_bcbc);
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
		Object textValueDefaultObject = config.get(ParamName.TEST_TEXT);
		assertThat(textValueDefaultObject, is(instanceOf(String.class)));
		assertThat((String) textValueDefaultObject, is(TEST_TEXT_DEFAULT_VALUE));
		/*
		 * Update default value.
		 */
		String newTextValue = "New default text value, which could be very long";
		config.set(ParamName.TEST_TEXT, newTextValue);
		/*
		 * The existing Configuration should have been updated, so there should
		 * be the same number of entities in the database
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/* 
		 * Retrieve value just set.
		 */
		Object textValueNewDefaultObject = config.get(ParamName.TEST_TEXT);
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
		Object textValueObject = config.get(ParamName.TEST_TEXT, role_bcbc);
		assertThat(textValueObject, is(instanceOf(String.class)));
		assertThat((String) textValueObject, is(TEST_TEXT_DEFAULT_VALUE));

		/*
		 * Set role-specific value, which should override the default value for
		 * the specified role.
		 */
		String newTextValue = "A potentially very long string.";
		config.set(ParamName.TEST_TEXT, newTextValue, role_bcbc);
		/*
		 * This should have created a new Configuration.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		Object textValueRoleSpecificObject = config.get(ParamName.TEST_TEXT, role_bcbc);
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
		config.set(ParamName.TEST_TEXT, newTextValue, role_bcbc);
		/*
		 * This should have re-used the Configuration just created.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		textValueRoleSpecificObject = config.get(ParamName.TEST_TEXT, role_bcbc);
		assertThat(textValueRoleSpecificObject, is(instanceOf(String.class)));
		assertThat((String) textValueRoleSpecificObject, is(newTextValue));

		/*
		 * The default value for ParamName.TEST_TEXT should still be the
		 * same.
		 */
		Object textValueDefaultObject = config.get(ParamName.TEST_TEXT);
		assertThat(textValueDefaultObject, is(instanceOf(String.class)));
		assertThat((String) textValueDefaultObject, is(TEST_TEXT_DEFAULT_VALUE));
	}

	// ======================== Time parameter tests ===========================

	@Test
	@Transactional
	public void timeValueExistsDefault() {
		UUID uuidOfDefaultTimeValueConfiguration = UUID.fromString("e0537e8a-62f3-4240-ba19-da0d5005092e");
		Configuration defaultTimeValueConfiguration = configurationRepository
				.findOne(uuidOfDefaultTimeValueConfiguration);
		assertThat(defaultTimeValueConfiguration, is(not(nullValue())));
		/*
		 * Convert the time returned from the Configuration to Joda time so
		 * that it can be compared to TEST_TIME_DEFAULT_VALUE_JODA. Joda time
		 * is used here because Java 7 does not have convenient methods for 
		 * dealing with times that are not associated with an instant in time) 
		 * or with dates that do not have a time portion. I could have managed 
		 * this with standard Java 7 methods, but it is easier to just use Joda 
		 * time.
		 */
		LocalTime defaultTime_Joda = new LocalTime(defaultTimeValueConfiguration.getTimeValue());
		assertThat(defaultTime_Joda, is(equalTo(TEST_TIME_DEFAULT_VALUE_JODA)));
	}

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
		Object timeValueObject = config.get(ParamName.TEST_TIME, role_aabb);
		assertThat(timeValueObject, is(instanceOf(Date.class)));

		Date timeFromConfig = (Date) timeValueObject;
		assertThat(timeFromConfig, is(not(nullValue())));

		LocalTime defaultTime_Joda = new LocalTime(timeFromConfig);
		assertThat(defaultTime_Joda, is(equalTo(TEST_TIME_ROLE_aabb_VALUE_JODA)));
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
		Object timeValueObject = config.get(ParamName.TEST_TIME);
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

		/*
		 * Convert Date object to Joda time LocalTime object to make it easy
		 * to compare with TEST_TIME_DEFAULT_VALUE_JODA.
		 */
		LocalTime timeFromConfig_Joda = new LocalTime(timeFromConfig);
		assertThat(timeFromConfig_Joda, is(equalTo(TEST_TIME_DEFAULT_VALUE_JODA)));

		/*
		 * Update default value.
		 * 
		 * The year, month & day here (1800, 6, 15) are arbitrary and are not
		 * used because it is only the time portion that we work with.
		 */
		Date newTimeValue = new GregorianCalendar(1800, 6, 15, 13, 45, 59).getTime();
		config.set(ParamName.TEST_TIME, newTimeValue);
		/*
		* The existing Configuration should have been updated, so there should
		* be the same number of entities in the database
		*/
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS));
		/* 
		 * Retrieve value just set.
		 */
		Object timeValueObjectUpdated = config.get(ParamName.TEST_TIME);
		assertThat(timeValueObjectUpdated, is(instanceOf(Date.class)));
		Date timeFromConfigUpdated = (Date) timeValueObjectUpdated;
		assertThat(timeFromConfigUpdated, is(not(nullValue())));

		LocalTime timeFromConfigUpdated_Joda = new LocalTime(timeFromConfigUpdated);
		LocalTime newTimeValue_Joda = new LocalTime(newTimeValue);
		assertThat(timeFromConfigUpdated_Joda, is(equalTo(newTimeValue_Joda)));
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
		Object timeValueObject = config.get(ParamName.TEST_TIME, role_bcbc);
		assertThat(timeValueObject, is(instanceOf(Date.class)));
		Date timeFromConfig = (Date) timeValueObject;
		assertThat(timeFromConfig, is(not(nullValue())));

		/*
		 * Convert Date object to Joda time LocalTime object to make it easy
		 * to compare with TEST_TIME_DEFAULT_VALUE_JODA.
		 */
		LocalTime timeFromConfig_Joda = new LocalTime(timeFromConfig);
		assertThat(timeFromConfig_Joda, is(equalTo(TEST_TIME_DEFAULT_VALUE_JODA)));

		/*
		 * Set role-specific value, which should override the default value for
		 * the specified role.
		 * 
		 * The year, month & day here (1960, 4, 14) are arbitrary and are not
		 * used because it is only the time portion that we work with.
		 */
		Date newTimeValue = new GregorianCalendar(1960, 4, 14, 23, 30, 59).getTime();
		config.set(ParamName.TEST_TIME, newTimeValue, role_bcbc);
		/*
		 * This should have created a new Configuration.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		Object timeValueRoleSpecificObject = config.get(ParamName.TEST_TIME, role_bcbc);
		assertThat(timeValueRoleSpecificObject, is(instanceOf(Date.class)));
		Date timeFromRoleSpecific = (Date) timeValueRoleSpecificObject;
		assertThat(timeFromRoleSpecific, is(not(nullValue())));

		LocalTime timeFromRoleSpecific_Joda = new LocalTime(timeFromRoleSpecific);
		LocalTime newTimeValue_Joda = new LocalTime(newTimeValue);
		assertThat(timeFromRoleSpecific_Joda, is(equalTo(newTimeValue_Joda)));

		/*
		 * Update the role-specific value. This should override the default 
		 * value for the specified role and RE-USE the new role-specific
		 * Configuration just created, i.e., not create a new Configuration
		 * 
		 * The year, month & day here (1960, 4, 14) are arbitrary and are not
		 * used because it is only the time portion that we work with.
		 */
		newTimeValue = new GregorianCalendar(1961, 10, 4, 00, 00, 00).getTime();
		config.set(ParamName.TEST_TIME, newTimeValue, role_bcbc);
		/*
		 * This should have created a new Configuration.
		 */
		assertThat(configurationRepository.count(), is(NUM_TEST_CONFIGURATIONS + 1));
		/*
		 * Retrieve value just set. The new value should override the default.
		 */
		timeValueRoleSpecificObject = config.get(ParamName.TEST_TIME, role_bcbc);
		assertThat(timeValueRoleSpecificObject, is(instanceOf(Date.class)));
		timeFromRoleSpecific = (Date) timeValueRoleSpecificObject;
		assertThat(timeFromRoleSpecific, is(not(nullValue())));

		timeFromRoleSpecific_Joda = new LocalTime(timeFromRoleSpecific);
		newTimeValue_Joda = new LocalTime(newTimeValue);
		assertThat(timeFromRoleSpecific_Joda, is(equalTo(newTimeValue_Joda)));

		/*
		 * The default value for ParamName.TEST_TIME should still be the
		 * same.
		 */
		Object timeValueDefaultObject = config.get(ParamName.TEST_TIME);
		assertThat(timeValueDefaultObject, is(instanceOf(Date.class)));
		timeFromConfig = (Date) timeValueDefaultObject;
		assertThat(timeFromConfig, is(not(nullValue())));

		/*
		 * Convert Date object to Joda time LocalTime object to make it easy
		 * to compare with TEST_TIME_DEFAULT_VALUE_JODA.
		 */
		timeFromConfig_Joda = new LocalTime(timeFromConfig);
		assertThat(timeFromConfig_Joda, is(equalTo(TEST_TIME_DEFAULT_VALUE_JODA)));
	}

}
