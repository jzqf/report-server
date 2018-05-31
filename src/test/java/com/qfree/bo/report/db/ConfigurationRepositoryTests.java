package com.qfree.bo.report.db;

import static org.hamcrest.Matchers.equalTo;
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

import com.qfree.bo.report.ApplicationConfig;
import com.qfree.bo.report.domain.Configuration;
import com.qfree.bo.report.domain.Configuration.ParamName;
import com.qfree.bo.report.domain.Role;
import com.qfree.bo.report.util.DateUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class)
/*
 * It is not clear that this test class dirties the Spring application context,
 * but I have experienced that if this annotation is not used on some test 
 * classes, then tests in *other* test classes can experience errors, i.e.,
 * exceptions (not test failures). For example if this annotation was not used 
 * in test class ReportCategoryRepositoryTests, then exceptions where thrown 
 * from the tests in ConfigurationServiceTests. In this case the log file 
 * contained messages that included things like:
 * 
 *    SQL Error: 90079, SQLState: 90079
 *    Schema "REPORTING" not found; SQL statement:
 *    org.hibernate.exception.GenericJDBCException: could not prepare statement
 * 
 * This problem seems to be related in some way to how the H2 RDBMS is used for
 * testing. Although this problem has been shown to be repeatable as long as 
 * code and dependencies are not changed, it is *not* repeatable if things do
 * change. Therefore, I have decided to annotate *all* test classes with the
 * @DirtiesContext annotation that access the H2 DB in any way.
 */
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
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

	/*
	 * Get role-specific boolean value for a parameter that has both a default 
	 * (Role==null) Configuration, and a role-specific value. This should
	 * fetch the role-specific value.
	 */
	@Test
	@Transactional
	public void booleanValueGetRoleSpecific() {
		UUID uuidOfRole_aabb = UUID.fromString("ee56f34d-dbb4-41c1-9d30-ce29cf973820");
		Role role_aabb = roleRepository.findOne(uuidOfRole_aabb);
		assertThat(role_aabb, is(notNullValue()));
		/*
		 * Role "aabb" has a role-specific value for the TEST_BOOLEAN parameter.
		 */
		Object booleanValueObject = configurationRepository.findByParamName(ParamName.TEST_BOOLEAN, role_aabb)
				.getBooleanValue();
		assertThat(booleanValueObject, is(instanceOf(Boolean.class)));
		assertThat((Boolean) booleanValueObject, is(TEST_BOOLEAN_ROLE_aabb_VALUE));
	}

	@Test
	@Transactional
	public void booleanValueFetchNonexistentRoleSpecific() {
		UUID uuidOfRole_bcbc = UUID.fromString("e918c8aa-c6d1-462c-9e91-f1db0fb9f346");
		Role role_bcbc = roleRepository.findOne(uuidOfRole_bcbc);
		assertThat(role_bcbc, is(notNullValue()));
		/*
		 * There is a default value for ParamName.TEST_BOOLEAN, but no
		 * role-specific value for Role "bcbc".
		 */
		Object booleanValueObject = configurationRepository.findByParamName(ParamName.TEST_BOOLEAN, role_bcbc);
		assertThat(booleanValueObject, is(nullValue()));
	}

	// ======================== Date parameter tests ===========================

	@Test
	@Transactional
	public void dateValueExistsDefault() {
		UUID uuidOfDefaultDateValueConfiguration = UUID.fromString("7d325b3c-d307-4fd0-bdae-c349ec2d4835");
		Configuration defaultDateValueConfiguration = configurationRepository
				.findOne(uuidOfDefaultDateValueConfiguration);
		assertThat(defaultDateValueConfiguration, is(not(nullValue())));
		assertThat(DateUtils.entityDateToNormalDate(defaultDateValueConfiguration.getDateValue()),
				is(TEST_DATE_DEFAULT_VALUE));
	}

	@Test
	@Transactional
	public void dateValueFetchDefault() {
		Configuration configuration = configurationRepository.findByParamName(ParamName.TEST_DATE);
		assertThat(configuration, is(not(nullValue())));
		Date dateFromConfig = configuration.getDateValue();
		assertThat(dateFromConfig, is(not(nullValue())));
		assertThat(DateUtils.entityDateToNormalDate(dateFromConfig), is(TEST_DATE_DEFAULT_VALUE));
	}

	// ====================== Datetime parameter tests =========================

	@Test
	@Transactional
	public void datetimeValueExistsDefault() {
		UUID uuidOfDefaultDatetimeValueConfiguration = UUID.fromString("5e7d5a1e-5d42-4a9c-b790-e45e545463f7");
		Configuration defaultDatetimeValueConfiguration = configurationRepository
				.findOne(uuidOfDefaultDatetimeValueConfiguration);
		assertThat(defaultDatetimeValueConfiguration, is(not(nullValue())));
		assertThat(DateUtils.entityTimestampToNormalDate(defaultDatetimeValueConfiguration.getDatetimeValue()),
				is(TEST_DATETIME_DEFAULT_VALUE));
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

	/*
	 * Get role-specific string value for a parameter that has both a default 
	 * (Role==null) Configuration, and a role-specific value. This should
	 * fetch the role-specific value.
	 */
	@Test
	@Transactional
	public void stringValueGetRoleSpecific() {
		UUID uuidOfRole_aabb = UUID.fromString("ee56f34d-dbb4-41c1-9d30-ce29cf973820");
		Role role_aabb = roleRepository.findOne(uuidOfRole_aabb);
		assertThat(role_aabb, is(notNullValue()));
		/*
		 * Role "aabb" has a role-specific value for the TEST_STRING parameter.
		 */
		Object stringValueObject = configurationRepository.findByParamName(ParamName.TEST_STRING, role_aabb)
				.getStringValue();
		assertThat(stringValueObject, is(instanceOf(String.class)));
		assertThat((String) stringValueObject, is(TEST_STRING_ROLE_aabb_VALUE));
	}

	@Test
	@Transactional
	public void stringValueFetchNonexistentRoleSpecific() {
		UUID uuidOfRole_bcbc = UUID.fromString("e918c8aa-c6d1-462c-9e91-f1db0fb9f346");
		Role role_bcbc = roleRepository.findOne(uuidOfRole_bcbc);
		assertThat(role_bcbc, is(notNullValue()));
		/*
		 * There is a default value for ParamName.TEST_STRING, but no
		 * role-specific value for Role "bcbc".
		 */
		Object stringValueObject = configurationRepository.findByParamName(ParamName.TEST_STRING, role_bcbc);
		assertThat(stringValueObject, is(nullValue()));
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

	// ======================== Time parameter tests ===========================

	@Test
	@Transactional
	public void timeValueExistsDefault() {
		UUID uuidOfDefaultTimeValueConfiguration = UUID.fromString("e0537e8a-62f3-4240-ba19-da0d5005092e");
		Configuration defaultTimeValueConfiguration = configurationRepository
				.findOne(uuidOfDefaultTimeValueConfiguration);
		assertThat(defaultTimeValueConfiguration, is(not(nullValue())));
		assertThat(DateUtils.timePartsEqual(defaultTimeValueConfiguration.getTimeValue(), TEST_TIME_DEFAULT_VALUE),
				is(true));
	}

}
