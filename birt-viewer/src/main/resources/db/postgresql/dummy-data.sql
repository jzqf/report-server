BEGIN TRANSACTION;


insert into reporting.parameter_type (parameter_type_id, description, abbreviation, active, created_on) values ('bfa09b13-ad55-481e-8c29-b047dc5d7f3e', 'Boolean (True/False)'   , 'boolean'  , true, '2015-05-01T12:00:01');
insert into reporting.parameter_type (parameter_type_id, description, abbreviation, active, created_on) values ('12d3f4f8-468d-4faf-be3a-5c15eaba4eb6', 'Date (no time)'         , 'date'     , true, '2015-05-01T12:00:01');
insert into reporting.parameter_type (parameter_type_id, description, abbreviation, active, created_on) values ('abce5a38-b1e9-42a3-9962-19227d51dd4a', 'Datetime (date + time)' , 'datetime' , true, '2015-05-01T12:00:01');
insert into reporting.parameter_type (parameter_type_id, description, abbreviation, active, created_on) values ('f2bfa3f9-f446-49dd-ad0e-6a02b3af1023', 'Decimal'                , 'decimal'  , true, '2015-05-01T12:00:01');
insert into reporting.parameter_type (parameter_type_id, description, abbreviation, active, created_on) values ('8b0bfc37-5fb4-4dea-87fc-3e2c3313af17', 'Float'                  , 'float'    , true, '2015-05-01T12:00:01');
insert into reporting.parameter_type (parameter_type_id, description, abbreviation, active, created_on) values ('807c64b1-a59b-465c-998b-a399984b5ef4', 'Integer'                , 'integer'  , true, '2015-05-01T12:00:01');
insert into reporting.parameter_type (parameter_type_id, description, abbreviation, active, created_on) values ('9b0af697-8bc9-49e2-b8b6-136ced83dbd8', 'String'                 , 'string'   , true, '2015-05-01T12:00:01');
insert into reporting.parameter_type (parameter_type_id, description, abbreviation, active, created_on) values ('da575eee-e5a3-4149-8ea3-1fd86015bbb9', 'Time'                   , 'time'     , true, '2015-05-01T12:00:01');
insert into reporting.parameter_type (parameter_type_id, description, abbreviation, active, created_on) values ('2bc62461-6ddb-4e86-b46d-080cd5e9cf83', 'Any'                    , 'any'      , true, '2015-05-01T12:00:01');


---- [role_parameter] records:
----
---- Role "aabb", report parameter "Report01Param01" (integer - multi-valued):
--insert into reporting.role_parameter (role_parameter_id, role_id, report_parameter_id, created_on) values ('81e23dae-9fb9-4862-9b66-de8d0c5a91f0', 'ee56f34d-dbb4-41c1-9d30-ce29cf973820', '206723d6-50e7-4f4a-85c0-cb679e92ad6b', '2015-05-31T13:00:00');
---- Role "aabb", report parameter "Report02Param01" (datetime - single-valued):
--insert into reporting.role_parameter (role_parameter_id, role_id, report_parameter_id, created_on) values ('21c59fa8-0e5b-429c-b78e-b2f0e0f0940d', 'ee56f34d-dbb4-41c1-9d30-ce29cf973820', '36fc0de4-cc4c-4efa-8c47-73e0e254e449', '2015-05-31T13:00:00');
---- Role "aabb", report parameter "Report03Param01" (integer - multi-valued):
--insert into reporting.role_parameter (role_parameter_id, role_id, report_parameter_id, created_on) values ('65d6219d-d115-4715-8e9a-206e3b576135', 'ee56f34d-dbb4-41c1-9d30-ce29cf973820', '5a201251-b04f-406e-b07c-c6d55dc3dc85', '2015-05-31T13:00:00');
---- Role "aabb", report parameter "Report04Param01" (boolean - single-valued):
--insert into reporting.role_parameter (role_parameter_id, role_id, report_parameter_id, created_on) values ('9c42ad18-4d21-4161-b3d2-80f0ad0982eb', 'ee56f34d-dbb4-41c1-9d30-ce29cf973820', '4c2bd07e-c7d7-451a-8c07-c8f589959382', '2015-05-31T13:00:00');
---- Role "acca", report parameter "Report05Param01" (datetime - multi-valued):
--insert into reporting.role_parameter (role_parameter_id, role_id, report_parameter_id, created_on) values ('2445d793-909f-467d-83de-257375ef3155', '39bc8737-b9eb-4cb5-9765-1c33dd5ee40c', '73792710-8d69-477a-8d44-fe646507eaf8', '2015-05-31T13:00:00');
---- Role "acca", report parameter "Report04Param01" (integer - single-valued):
--insert into reporting.role_parameter (role_parameter_id, role_id, report_parameter_id, created_on) values ('bab4fe25-3f7f-463b-8615-2752010d769d', '39bc8737-b9eb-4cb5-9765-1c33dd5ee40c', '86e93f08-86fd-4aed-99c2-1f4af29382d3', '2015-05-31T13:00:00');
--
--
---- [role_parameter_value] records:
----
---- Role "aabb", report parameter "Report01Param01" (integer - multi-valued):
--insert into reporting.role_parameter_value (role_parameter_value_id, role_parameter_id, boolean_value, date_value, datetime_value, float_value, integer_value, string_value, time_value, created_on) values ('7e7f2284-efcd-447b-b0fd-07b841400666', '81e23dae-9fb9-4862-9b66-de8d0c5a91f0', null, null, null                     , null, 666 , null, null, '2015-05-31T13:00:00');
---- Role "aabb", report parameter "Report02Param01" (datetime - single-valued):
--insert into reporting.role_parameter_value (role_parameter_value_id, role_parameter_id, boolean_value, date_value, datetime_value, float_value, integer_value, string_value, time_value, created_on) values ('c0b449cc-4b87-4733-8648-a1b0168a1925', '21c59fa8-0e5b-429c-b78e-b2f0e0f0940d', null, null, '2015-07-14T11:11:11.011', null, null, null, null, '2015-05-31T13:00:00');
---- Role "aabb", report parameter "Report03Param01" (integer - multi-valued):
--insert into reporting.role_parameter_value (role_parameter_value_id, role_parameter_id, boolean_value, date_value, datetime_value, float_value, integer_value, string_value, time_value, created_on) values ('967147d0-24e2-46a5-8423-e18a87bf13c1', '65d6219d-d115-4715-8e9a-206e3b576135', null, null, null                     , null, 100 , null, null, '2015-05-31T13:00:00');
--insert into reporting.role_parameter_value (role_parameter_value_id, role_parameter_id, boolean_value, date_value, datetime_value, float_value, integer_value, string_value, time_value, created_on) values ('7fe51a01-c311-46e3-9a8b-70346a71b47f', '65d6219d-d115-4715-8e9a-206e3b576135', null, null, null                     , null, 400 , null, null, '2015-05-31T13:00:00');
--insert into reporting.role_parameter_value (role_parameter_value_id, role_parameter_id, boolean_value, date_value, datetime_value, float_value, integer_value, string_value, time_value, created_on) values ('58d7d22d-620d-474e-aae5-a57620229110', '65d6219d-d115-4715-8e9a-206e3b576135', null, null, null                     , null, 200 , null, null, '2015-05-31T13:00:00');
---- Role "aabb", report parameter "Report04Param01" (boolean - single-valued):
--insert into reporting.role_parameter_value (role_parameter_value_id, role_parameter_id, boolean_value, date_value, datetime_value, float_value, integer_value, string_value, time_value, created_on) values ('80546b03-0e32-46c2-abe8-4b7f3e34d90b', '9c42ad18-4d21-4161-b3d2-80f0ad0982eb', true, null, null                     , null, null, null, null, '2015-05-31T13:00:00');
---- Role "acca", report parameter "Report05Param01" (datetime - multi-valued):
--insert into reporting.role_parameter_value (role_parameter_value_id, role_parameter_id, boolean_value, date_value, datetime_value, float_value, integer_value, string_value, time_value, created_on) values ('213fac69-8424-4d88-a55d-90ac43e18832', '2445d793-909f-467d-83de-257375ef3155', null, null, '1961-11-04T00:00:00'    , null, null, null, null, '2015-05-31T13:00:00');
---- Role "acca", report parameter "Report04Param01" (integer - single-valued):
--insert into reporting.role_parameter_value (role_parameter_value_id, role_parameter_id, boolean_value, date_value, datetime_value, float_value, integer_value, string_value, time_value, created_on) values ('558f3ecb-15dc-4eec-951a-eedc6ebf3fb0', 'bab4fe25-3f7f-463b-8615-2752010d769d', null, null, null                     , null, -99 , null, null, '2015-05-31T13:00:00');


insert into reporting.report_category (report_category_id, description, abbreviation, active, created_on) values ('7a482694-51d2-42d0-b0e2-19dd13bbbc64', 'Accounting'       , 'ACCT' , true, '2015-04-30T12:00:00');
insert into reporting.report_category (report_category_id, description, abbreviation, active, created_on) values ('bb2bc482-c19a-4c19-a087-e68ffc62b5a0', 'Q-Free internal'  , 'QFREE', true, '2015-05-30T22:00:00');
insert into reporting.report_category (report_category_id, description, abbreviation, active, created_on) values ('5c3cc664-b685-4f6e-8d9a-2927c6bcffdc', 'Manual validation', 'MIR'  , true, '2015-03-01T02:00:00');
insert into reporting.report_category (report_category_id, description, abbreviation, active, created_on) values ('72d7cb27-1770-4cc7-b301-44d39ccf1e76', 'Traffic'          , 'TRA'  , true, '2015-03-01T02:00:10');


insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('08de9764-735f-4c82-bbe9-3981b29cc133', 'Queued'    , 'QUEUED'    , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('a613aae2-836a-4b03-a75d-cfb8303eaad5', 'Running'   , 'RUNNING'   , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('24274a10-6d83-4f0f-a807-2c96d86ce5d6', 'Delivering', 'DELIVERING', true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('f378fc09-35e4-4096-b1d1-2db14756b098', 'Completed' , 'COMPLETED' , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('2a9cd697-af00-45bc-aa6a-053284b9d9e4', 'Failed'    , 'FAILED'    , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('5125c537-e178-42de-b4dd-e538fa3da802', 'Canceled'  , 'CANCELED'  , true, current_timestamp AT TIME ZONE 'UTC');


insert into reporting.report (report_id, report_category_id, name, number, sort_order, active, created_on) values ('d65f3d9c-f67d-4beb-9936-9dfa19aa1407', '7a482694-51d2-42d0-b0e2-19dd13bbbc64', 'Test Report #01', 100, 100, true , '2014-06-09T22:00:00');
insert into reporting.report (report_id, report_category_id, name, number, sort_order, active, created_on) values ('c7f1d394-9814-4ede-bb01-2700187d79ca', '7a482694-51d2-42d0-b0e2-19dd13bbbc64', 'Test Report #02', 200, 200, true , '2014-06-09T22:10:00');
insert into reporting.report (report_id, report_category_id, name, number, sort_order, active, created_on) values ('fe718314-5b39-40e7-aed2-279354c04a9d', '7a482694-51d2-42d0-b0e2-19dd13bbbc64', 'Test Report #03', 300, 300, false, '2014-07-04T23:30:00');
insert into reporting.report (report_id, report_category_id, name, number, sort_order, active, created_on) values ('702d5daa-e23d-4f00-b32b-67b44c06d8f6', 'bb2bc482-c19a-4c19-a087-e68ffc62b5a0', 'Test Report #04', 400, 400, true , '2014-03-25T12:15:00');
insert into reporting.report (report_id, report_category_id, name, number, sort_order, active, created_on) values ('f1f06b15-c0b6-488d-9eed-74e867a47d5a', '72d7cb27-1770-4cc7-b301-44d39ccf1e76', 'Test Report #05', 500, 500, false, '2014-03-25T12:15:00');
insert into reporting.report (report_id, report_category_id, name, number, sort_order, active, created_on) values ('adc50b28-cb84-4ede-9759-43f467ac22ec', 'bb2bc482-c19a-4c19-a087-e68ffc62b5a0', 'Test Report #06', 600, 600, true , '2015-05-06T15:00:00');


CREATE TEMPORARY TABLE tmp_rptdesign (rptdesign text) ON COMMIT DROP;

-- Report 100: 'Test Report #01', version 1:
INSERT INTO tmp_rptdesign (rptdesign) VALUES ('<?xml version="1.0" encoding="UTF-8"?>
<report xmlns="http://www.eclipse.org/birt/2005/design" version="3.2.23" id="1">
    <property name="createdBy">Eclipse BIRT Designer Version 4.4.2.v201410272105 Build &lt;4.4.2.v20150217-1805></property>
    <property name="units">in</property>
    <property name="iconFile">/templates/blank_report.gif</property>
    <property name="bidiLayoutOrientation">ltr</property>
    <property name="imageDPI">96</property>
    <page-setup>
        <simple-master-page name="Simple MasterPage" id="2"/>
    </page-setup>
    <body>
        <label id="3">
            <text-property name="text">Report 100:  Test Report #01 - v1</text-property>
        </label>
    </body>
</report>');
insert into reporting.report_version (report_version_id, report_id, file_name, rptdesign, version_name, version_code, active, created_on) VALUES ('dbc0883b-afe3-4147-87b4-0ed35869cd35', 'd65f3d9c-f67d-4beb-9936-9dfa19aa1407', '100-TestReport01_v0.5.rptdesign', (SELECT rptdesign FROM tmp_rptdesign), '0.5', 1, true, '2015-05-06T15:10:00');
delete from tmp_rptdesign;

-- Report 200. 'Test Report #02', version 1:
INSERT INTO tmp_rptdesign (rptdesign) VALUES ('<?xml version="1.0" encoding="UTF-8"?>
<report xmlns="http://www.eclipse.org/birt/2005/design" version="3.2.23" id="1">
    <property name="createdBy">Eclipse BIRT Designer Version 4.4.2.v201410272105 Build &lt;4.4.2.v20150217-1805></property>
    <property name="units">in</property>
    <property name="iconFile">/templates/blank_report.gif</property>
    <property name="bidiLayoutOrientation">ltr</property>
    <property name="imageDPI">96</property>
    <page-setup>
        <simple-master-page name="Simple MasterPage" id="2"/>
    </page-setup>
    <body>
        <label id="3">
            <text-property name="text">Report 200:  Test Report #02 - v1</text-property>
        </label>
    </body>
</report>');
insert into reporting.report_version (report_version_id, report_id, file_name, rptdesign, version_name, version_code, active, created_on) VALUES ('fb8a4896-609b-438a-81b0-c70587f8f637', 'c7f1d394-9814-4ede-bb01-2700187d79ca', '200-TestReport02_v0.9.rptdesign', (SELECT rptdesign FROM tmp_rptdesign), '0.9', 1, false, '2015-05-06T15:10:00');
delete from tmp_rptdesign;

-- Report 200. 'Test Report #02', version 2:
INSERT INTO tmp_rptdesign (rptdesign) VALUES ('<?xml version="1.0" encoding="UTF-8"?>
<report xmlns="http://www.eclipse.org/birt/2005/design" version="3.2.23" id="1">
    <property name="createdBy">Eclipse BIRT Designer Version 4.4.2.v201410272105 Build &lt;4.4.2.v20150217-1805></property>
    <property name="units">in</property>
    <property name="iconFile">/templates/blank_report.gif</property>
    <property name="bidiLayoutOrientation">ltr</property>
    <property name="imageDPI">96</property>
    <page-setup>
        <simple-master-page name="Simple MasterPage" id="2"/>
    </page-setup>
    <body>
        <label id="3">
            <text-property name="text">Report 200:  Test Report #02 - v2</text-property>
        </label>
    </body>
</report>');
insert into reporting.report_version (report_version_id, report_id, file_name, rptdesign, version_name, version_code, active, created_on) VALUES ('2f88d7ff-8b74-457a-bce6-89d3e74d0bb9', 'c7f1d394-9814-4ede-bb01-2700187d79ca', '200-TestReport02_v1.0.rptdesign', (SELECT rptdesign FROM tmp_rptdesign), '1.0', 2, true, '2015-05-06T09:00:00');
delete from tmp_rptdesign;

-- Report 200. 'Test Report #02', version 3:
INSERT INTO tmp_rptdesign (rptdesign) VALUES ('<?xml version="1.0" encoding="UTF-8"?>
<report xmlns="http://www.eclipse.org/birt/2005/design" version="3.2.23" id="1">
    <property name="createdBy">Eclipse BIRT Designer Version 4.4.2.v201410272105 Build &lt;4.4.2.v20150217-1805></property>
    <property name="units">in</property>
    <property name="iconFile">/templates/blank_report.gif</property>
    <property name="bidiLayoutOrientation">ltr</property>
    <property name="imageDPI">96</property>
    <page-setup>
        <simple-master-page name="Simple MasterPage" id="2"/>
    </page-setup>
    <body>
        <label id="3">
            <text-property name="text">Report 200:  Test Report #02 - v3</text-property>
        </label>
    </body>
</report>');
insert into reporting.report_version (report_version_id, report_id, file_name, rptdesign, version_name, version_code, active, created_on) VALUES ('293abf69-1516-4e9b-84ae-241d25c13e8d', 'c7f1d394-9814-4ede-bb01-2700187d79ca', '200-TestReport02_v2.0.rptdesign', (SELECT rptdesign FROM tmp_rptdesign), '2.0', 3, true, '2015-05-08T15:00:00');
delete from tmp_rptdesign;

-- Report 300. 'Test Report #03', version 1:
INSERT INTO tmp_rptdesign (rptdesign) VALUES ('<?xml version="1.0" encoding="UTF-8"?>
<report xmlns="http://www.eclipse.org/birt/2005/design" version="3.2.23" id="1">
    <property name="createdBy">Eclipse BIRT Designer Version 4.4.2.v201410272105 Build &lt;4.4.2.v20150217-1805></property>
    <property name="units">in</property>
    <property name="iconFile">/templates/blank_report.gif</property>
    <property name="bidiLayoutOrientation">ltr</property>
    <property name="imageDPI">96</property>
    <page-setup>
        <simple-master-page name="Simple MasterPage" id="2"/>
    </page-setup>
    <body>
        <label id="3">
            <text-property name="text">Report 300:  Test Report #03 - v1</text-property>
        </label>
    </body>
</report>');
insert into reporting.report_version (report_version_id, report_id, file_name, rptdesign, version_name, version_code, active, created_on) VALUES ('8dd9bb5a-1565-4d38-8a8b-857803088626', 'fe718314-5b39-40e7-aed2-279354c04a9d', '300-TestReport03_v0.5.rptdesign', (SELECT rptdesign FROM tmp_rptdesign), '0.5', 1, true, '2015-05-06T15:10:00');
delete from tmp_rptdesign;

-- Report 400. 'Test Report #04', version 1:
INSERT INTO tmp_rptdesign (rptdesign) VALUES ('<?xml version="1.0" encoding="UTF-8"?>
<report xmlns="http://www.eclipse.org/birt/2005/design" version="3.2.23" id="1">
    <property name="createdBy">Eclipse BIRT Designer Version 4.4.2.v201410272105 Build &lt;4.4.2.v20150217-1805></property>
    <property name="units">in</property>
    <property name="iconFile">/templates/blank_report.gif</property>
    <property name="bidiLayoutOrientation">ltr</property>
    <property name="imageDPI">96</property>
    <page-setup>
        <simple-master-page name="Simple MasterPage" id="2"/>
    </page-setup>
    <body>
        <label id="3">
            <text-property name="text">Report 400:  Test Report #04 - v1</text-property>
        </label>
    </body>
</report>');
insert into reporting.report_version (report_version_id, report_id, file_name, rptdesign, version_name, version_code, active, created_on) VALUES ('d481c452-990c-4ea3-9afa-3ea60cef04ab', '702d5daa-e23d-4f00-b32b-67b44c06d8f6', '400-TestReport04_v1.0.rptdesign', (SELECT rptdesign FROM tmp_rptdesign), '1.0', 1, true, '2015-05-06T15:10:00');
delete from tmp_rptdesign;
--
-- Report 400. 'Test Report #04', version 2:
INSERT INTO tmp_rptdesign (rptdesign) VALUES ('<?xml version="1.0" encoding="UTF-8"?>
<report xmlns="http://www.eclipse.org/birt/2005/design" version="3.2.23" id="1">
    <property name="createdBy">Eclipse BIRT Designer Version 4.4.2.v201410272105 Build &lt;4.4.2.v20150217-1805></property>
    <property name="units">in</property>
    <property name="iconFile">/templates/blank_report.gif</property>
    <property name="bidiLayoutOrientation">ltr</property>
    <property name="imageDPI">96</property>
    <parameters>
        <scalar-parameter name="StringParameterTextBoxReqd" id="5">
            <text-property name="helpText">This is some help text for "StringParameterTextBoxReqd"</text-property>
            <text-property name="promptText">String: TextBox (required)</text-property>
            <property name="valueType">static</property>
            <property name="dataType">string</property>
            <property name="distinct">true</property>
            <simple-property-list name="defaultValue">
                <value type="constant">some default value</value>
            </simple-property-list>
            <list-property name="selectionList"/>
            <property name="paramType">simple</property>
            <property name="controlType">text-box</property>
            <structure name="format">
                <property name="category">Unformatted</property>
            </structure>
        </scalar-parameter>
        <scalar-parameter name="StringParameterTextBoxNotReqd" id="236">
            <text-property name="helpText">This is some help text for "StringParameterTextBoxNotReqd"</text-property>
            <text-property name="promptText">String: TextBox (not required)</text-property>
            <property name="valueType">static</property>
            <property name="isRequired">false</property>
            <property name="dataType">string</property>
            <property name="distinct">true</property>
            <simple-property-list name="defaultValue">
                <value type="constant">some default value</value>
            </simple-property-list>
            <list-property name="selectionList"/>
            <property name="paramType">simple</property>
            <property name="controlType">text-box</property>
            <structure name="format">
                <property name="category">Unformatted</property>
            </structure>
        </scalar-parameter>
        <scalar-parameter name="StringParameterComboBoxStaticReqd" id="57">
            <text-property name="helpText">This is some help text for "StringParameterComboBoxStaticReqd"</text-property>
            <text-property name="promptText">String: ComboBox (static list, required)</text-property>
            <property name="valueType">static</property>
            <property name="dataType">string</property>
            <property name="distinct">true</property>
            <simple-property-list name="defaultValue">
                <value type="constant">value2</value>
            </simple-property-list>
            <list-property name="selectionList">
                <structure>
                    <property name="value">value1</property>
                    <property name="label">Display value 1</property>
                </structure>
                <structure>
                    <property name="value">value2</property>
                    <property name="label">Display value 2</property>
                </structure>
                <structure>
                    <property name="value">value3</property>
                    <property name="label">Display value 3</property>
                </structure>
                <structure>
                    <property name="value">value4</property>
                    <property name="label">Display value 4</property>
                </structure>
            </list-property>
            <property name="paramType">simple</property>
            <property name="controlType">list-box</property>
            <property name="mustMatch">false</property>
            <property name="fixedOrder">true</property>
            <structure name="format">
                <property name="category">Unformatted</property>
            </structure>
        </scalar-parameter>
        <scalar-parameter name="StringParameterComboBoxStaticNotReqd" id="240">
            <text-property name="helpText">This is some help text for "StringParameterComboBoxStaticNotReqd"</text-property>
            <text-property name="promptText">String: ComboBox (static list, not required)</text-property>
            <property name="valueType">static</property>
            <property name="isRequired">false</property>
            <property name="dataType">string</property>
            <property name="distinct">true</property>
            <simple-property-list name="defaultValue">
                <value type="constant">value2</value>
            </simple-property-list>
            <list-property name="selectionList">
                <structure>
                    <property name="value">value1</property>
                    <property name="label">Display value 1</property>
                </structure>
                <structure>
                    <property name="value">value2</property>
                    <property name="label">Display value 2</property>
                </structure>
                <structure>
                    <property name="value">value3</property>
                    <property name="label">Display value 3</property>
                </structure>
                <structure>
                    <property name="value">value4</property>
                    <property name="label">Display value 4</property>
                </structure>
            </list-property>
            <property name="paramType">simple</property>
            <property name="controlType">list-box</property>
            <property name="mustMatch">false</property>
            <property name="fixedOrder">true</property>
            <structure name="format">
                <property name="category">Unformatted</property>
            </structure>
        </scalar-parameter>
        <scalar-parameter name="StringParameterListBoxStaticSingleReqd" id="63">
            <text-property name="helpText">This is some help text for "StringParameterListBoxStaticSingleReqd"</text-property>
            <text-property name="promptText">String: ListBox (static list, single select, required)</text-property>
            <property name="valueType">static</property>
            <property name="dataType">string</property>
            <property name="distinct">true</property>
            <simple-property-list name="defaultValue">
                <value type="constant">value2</value>
            </simple-property-list>
            <list-property name="selectionList">
                <structure>
                    <property name="value">value1</property>
                    <property name="label">Display value 1</property>
                </structure>
                <structure>
                    <property name="value">value2</property>
                    <property name="label">Display value 2</property>
                </structure>
                <structure>
                    <property name="value">value3</property>
                    <property name="label">Display value 3</property>
                </structure>
                <structure>
                    <property name="value">value4</property>
                    <property name="label">Display value 4</property>
                </structure>
            </list-property>
            <property name="paramType">simple</property>
            <property name="controlType">list-box</property>
            <property name="mustMatch">true</property>
            <property name="fixedOrder">true</property>
            <structure name="format">
                <property name="category">Unformatted</property>
            </structure>
        </scalar-parameter>
        <scalar-parameter name="StringParameterListBoxStaticSingleNotReqd" id="253">
            <text-property name="helpText">This is some help text for "StringParameterListBoxStaticSingleNotReqd"</text-property>
            <text-property name="promptText">String: ListBox (static list, single select, not required)</text-property>
            <property name="valueType">static</property>
            <property name="isRequired">false</property>
            <property name="dataType">string</property>
            <property name="distinct">true</property>
            <simple-property-list name="defaultValue">
                <value type="constant">value2</value>
            </simple-property-list>
            <list-property name="selectionList">
                <structure>
                    <property name="value">value1</property>
                    <property name="label">Display value 1</property>
                </structure>
                <structure>
                    <property name="value">value2</property>
                    <property name="label">Display value 2</property>
                </structure>
                <structure>
                    <property name="value">value3</property>
                    <property name="label">Display value 3</property>
                </structure>
                <structure>
                    <property name="value">value4</property>
                    <property name="label">Display value 4</property>
                </structure>
            </list-property>
            <property name="paramType">simple</property>
            <property name="controlType">list-box</property>
            <property name="mustMatch">true</property>
            <property name="fixedOrder">true</property>
            <structure name="format">
                <property name="category">Unformatted</property>
            </structure>
        </scalar-parameter>
        <scalar-parameter name="StringParameterListBoxStaticMultipleReqd" id="67">
            <text-property name="helpText">This is some help text for "StringParameterListBoxStaticMultipleReqd"</text-property>
            <text-property name="promptText">String: ListBox (static list, multiple select, required)</text-property>
            <property name="valueType">static</property>
            <property name="dataType">string</property>
            <property name="distinct">true</property>
            <simple-property-list name="defaultValue">
                <value type="constant">value2</value>
            </simple-property-list>
            <list-property name="selectionList">
                <structure>
                    <property name="value">value1</property>
                    <property name="label">Display value 1</property>
                </structure>
                <structure>
                    <property name="value">value2</property>
                    <property name="label">Display value 2</property>
                </structure>
                <structure>
                    <property name="value">value3</property>
                    <property name="label">Display value 3</property>
                </structure>
                <structure>
                    <property name="value">value4</property>
                    <property name="label">Display value 4</property>
                </structure>
            </list-property>
            <property name="paramType">multi-value</property>
            <property name="controlType">list-box</property>
            <property name="mustMatch">true</property>
            <property name="fixedOrder">true</property>
            <structure name="format">
                <property name="category">Unformatted</property>
            </structure>
        </scalar-parameter>
        <scalar-parameter name="StringParameterListBoxStaticMultipleNotReqd" id="266">
            <text-property name="helpText">This is some help text for "StringParameterListBoxStaticMultipleNotReqd"</text-property>
            <text-property name="promptText">String: ListBox (static list, multiple select, not required)</text-property>
            <property name="valueType">static</property>
            <property name="isRequired">false</property>
            <property name="dataType">string</property>
            <property name="distinct">true</property>
            <simple-property-list name="defaultValue">
                <value type="constant">value2</value>
            </simple-property-list>
            <list-property name="selectionList">
                <structure>
                    <property name="value">value1</property>
                    <property name="label">Display value 1</property>
                </structure>
                <structure>
                    <property name="value">value2</property>
                    <property name="label">Display value 2</property>
                </structure>
                <structure>
                    <property name="value">value3</property>
                    <property name="label">Display value 3</property>
                </structure>
                <structure>
                    <property name="value">value4</property>
                    <property name="label">Display value 4</property>
                </structure>
            </list-property>
            <property name="paramType">multi-value</property>
            <property name="controlType">list-box</property>
            <property name="mustMatch">true</property>
            <property name="fixedOrder">true</property>
            <structure name="format">
                <property name="category">Unformatted</property>
            </structure>
        </scalar-parameter>
        <scalar-parameter name="BooleanParameterCheckBox" id="184">
            <text-property name="helpText">This is some help text for "BooleanParameterCheckBox"</text-property>
            <text-property name="promptText">Boolean: Check Box</text-property>
            <property name="valueType">static</property>
            <property name="isRequired">true</property>
            <property name="dataType">boolean</property>
            <property name="distinct">true</property>
            <simple-property-list name="defaultValue">
                <value type="constant">True</value>
            </simple-property-list>
            <list-property name="selectionList"/>
            <property name="paramType">simple</property>
            <property name="controlType">check-box</property>
            <structure name="format"/>
        </scalar-parameter>
        <scalar-parameter name="BooleanParameterRadioButtonReqd" id="196">
            <text-property name="helpText">This is some help text for "BooleanParameterRadioButtonReqd"</text-property>
            <text-property name="promptText">Boolean: Radio Button (required)</text-property>
            <property name="valueType">static</property>
            <property name="isRequired">true</property>
            <property name="dataType">boolean</property>
            <property name="distinct">true</property>
            <simple-property-list name="defaultValue">
                <value type="constant">true</value>
            </simple-property-list>
            <list-property name="selectionList">
                <structure>
                    <property name="value">false</property>
                    <property name="label">Click this for no/false</property>
                </structure>
                <structure>
                    <property name="value">true</property>
                    <property name="label">Click this for yes/true</property>
                </structure>
            </list-property>
            <property name="paramType">simple</property>
            <property name="controlType">radio-button</property>
            <property name="fixedOrder">true</property>
            <structure name="format"/>
        </scalar-parameter>
        <scalar-parameter name="BooleanParameterRadioButtonNotReqd" id="283">
            <text-property name="helpText">This is some help text for "BooleanParameterRadioButtonNotReqd"</text-property>
            <text-property name="promptText">Boolean: Radio Button (not required)</text-property>
            <property name="valueType">static</property>
            <property name="isRequired">false</property>
            <property name="dataType">boolean</property>
            <property name="distinct">true</property>
            <simple-property-list name="defaultValue">
                <value type="constant">true</value>
            </simple-property-list>
            <list-property name="selectionList">
                <structure>
                    <property name="value">false</property>
                    <property name="label">Click this for no/false</property>
                </structure>
                <structure>
                    <property name="value">true</property>
                    <property name="label">Click this for yes/true</property>
                </structure>
            </list-property>
            <property name="paramType">simple</property>
            <property name="controlType">radio-button</property>
            <property name="fixedOrder">true</property>
            <structure name="format"/>
        </scalar-parameter>
        <scalar-parameter name="DateParameterTextBoxReqd" id="78">
            <text-property name="helpText">This is some help text for "DateParameterTextBoxReqd"</text-property>
            <text-property name="promptText">Date: TextBox (required)</text-property>
            <property name="valueType">static</property>
            <property name="dataType">date</property>
            <property name="distinct">true</property>
            <simple-property-list name="defaultValue">
                <value type="constant">2015-06-30</value>
            </simple-property-list>
            <list-property name="selectionList"/>
            <property name="paramType">simple</property>
            <property name="controlType">text-box</property>
            <structure name="format">
                <property name="category">Custom</property>
                <property name="pattern">yyyy.MM.dd</property>
            </structure>
        </scalar-parameter>
        <scalar-parameter name="DateParameterTextBoxNotReqd" id="292">
            <text-property name="helpText">This is some help text for "DateParameterTextBoxNotReqd"</text-property>
            <text-property name="promptText">Date: TextBox (not required)</text-property>
            <property name="valueType">static</property>
            <property name="isRequired">false</property>
            <property name="dataType">date</property>
            <property name="distinct">true</property>
            <simple-property-list name="defaultValue">
                <value type="constant">2015-06-30</value>
            </simple-property-list>
            <list-property name="selectionList"/>
            <property name="paramType">simple</property>
            <property name="controlType">text-box</property>
            <structure name="format">
                <property name="category">Custom</property>
                <property name="pattern">yyyy.MM.dd</property>
            </structure>
        </scalar-parameter>
        <scalar-parameter name="DatetimeParameterTextboxReqd" id="176">
            <text-property name="helpText">This is some help text for "DatetimeParameterTextboxReqd"</text-property>
            <text-property name="promptText">Datetime:: TextBox (required)</text-property>
            <property name="valueType">static</property>
            <property name="dataType">dateTime</property>
            <property name="distinct">true</property>
            <simple-property-list name="defaultValue">
                <value type="constant">2015-06-30 12:00:00</value>
            </simple-property-list>
            <list-property name="selectionList"/>
            <property name="paramType">simple</property>
            <property name="controlType">text-box</property>
            <structure name="format">
                <property name="category">Custom</property>
                <property name="pattern">yyyy.MM.dd hh:mm:ss</property>
            </structure>
        </scalar-parameter>
        <scalar-parameter name="DatetimeParameterTextboxNotReqd" id="299">
            <text-property name="helpText">This is some help text for "DatetimeParameterTextboxNotReqd"</text-property>
            <text-property name="promptText">Datetime:: TextBox (not required)</text-property>
            <property name="valueType">static</property>
            <property name="isRequired">false</property>
            <property name="dataType">dateTime</property>
            <property name="distinct">true</property>
            <simple-property-list name="defaultValue">
                <value type="constant">2015-06-30 12:00:00</value>
            </simple-property-list>
            <list-property name="selectionList"/>
            <property name="paramType">simple</property>
            <property name="controlType">text-box</property>
            <structure name="format">
                <property name="category">Custom</property>
                <property name="pattern">yyyy.MM.dd hh:mm:ss</property>
            </structure>
        </scalar-parameter>
        <scalar-parameter name="TimeParameterTextboxReqd" id="180">
            <text-property name="helpText">This is some help text for "TimeParameterTextboxReqd"</text-property>
            <text-property name="promptText">Time: Textbox (required)</text-property>
            <property name="valueType">static</property>
            <property name="dataType">time</property>
            <property name="distinct">true</property>
            <simple-property-list name="defaultValue">
                <value type="constant">08:00:00</value>
            </simple-property-list>
            <list-property name="selectionList"/>
            <property name="paramType">simple</property>
            <property name="controlType">text-box</property>
            <structure name="format">
                <property name="category">Custom</property>
                <property name="pattern">hh:mm:ss</property>
            </structure>
        </scalar-parameter>
        <scalar-parameter name="TimeParameterTextboxNotReqd" id="308">
            <text-property name="helpText">This is some help text for "TimeParameterTextboxNotReqd"</text-property>
            <text-property name="promptText">Time: Textbox (not required)</text-property>
            <property name="valueType">static</property>
            <property name="isRequired">false</property>
            <property name="dataType">time</property>
            <property name="distinct">true</property>
            <simple-property-list name="defaultValue">
                <value type="constant">08:00:00</value>
            </simple-property-list>
            <list-property name="selectionList"/>
            <property name="paramType">simple</property>
            <property name="controlType">text-box</property>
            <structure name="format">
                <property name="category">Custom</property>
                <property name="pattern">hh:mm:ss</property>
            </structure>
        </scalar-parameter>
        <scalar-parameter name="IntegerParameterTextBoxReqd" id="203">
            <text-property name="helpText">This is some help text for "IntegerParameterTextBoxReqd"</text-property>
            <text-property name="promptText">Integer: TextBox (required)</text-property>
            <property name="valueType">static</property>
            <property name="dataType">integer</property>
            <property name="distinct">true</property>
            <simple-property-list name="defaultValue">
                <value type="constant">42</value>
            </simple-property-list>
            <list-property name="selectionList"/>
            <property name="paramType">simple</property>
            <property name="controlType">text-box</property>
            <structure name="format">
                <property name="category">Unformatted</property>
            </structure>
        </scalar-parameter>
        <scalar-parameter name="IntegerParameterTextBoxNotReqd" id="316">
            <text-property name="helpText">This is some help text for "IntegerParameterTextBoxNotReqd"</text-property>
            <text-property name="promptText">Integer: TextBox (not required)</text-property>
            <property name="valueType">static</property>
            <property name="isRequired">false</property>
            <property name="dataType">integer</property>
            <property name="distinct">true</property>
            <simple-property-list name="defaultValue">
                <value type="constant">42</value>
            </simple-property-list>
            <list-property name="selectionList"/>
            <property name="paramType">simple</property>
            <property name="controlType">text-box</property>
            <structure name="format">
                <property name="category">Unformatted</property>
            </structure>
        </scalar-parameter>
        <scalar-parameter name="IntegerParameterComboBoxStaticReqd" id="207">
            <text-property name="helpText">This is some help text for "IntegerParameterComboBoxStaticReqd"</text-property>
            <text-property name="promptText">Integer: Combo Box (static list, required)</text-property>
            <property name="valueType">static</property>
            <property name="dataType">integer</property>
            <property name="distinct">true</property>
            <simple-property-list name="defaultValue">
                <value type="constant">42</value>
            </simple-property-list>
            <list-property name="selectionList">
                <structure>
                    <property name="value">0</property>
                    <property name="label">Display test for 0</property>
                </structure>
                <structure>
                    <property name="value">42</property>
                    <property name="label">Display test for 42</property>
                </structure>
                <structure>
                    <property name="value">666</property>
                    <property name="label">Display test for 666</property>
                </structure>
                <structure>
                    <property name="value">12345678</property>
                    <property name="label">Display test for 12345678</property>
                </structure>
            </list-property>
            <property name="paramType">simple</property>
            <property name="controlType">list-box</property>
            <property name="mustMatch">false</property>
            <property name="fixedOrder">true</property>
            <structure name="format">
                <property name="category">Unformatted</property>
            </structure>
        </scalar-parameter>
        <scalar-parameter name="IntegerParameterComboBoxStaticNotReqd" id="319">
            <text-property name="helpText">This is some help text for "IntegerParameterComboBoxStaticNotReqd"</text-property>
            <text-property name="promptText">Integer: Combo Box (static list, not required)</text-property>
            <property name="valueType">static</property>
            <property name="isRequired">false</property>
            <property name="dataType">integer</property>
            <property name="distinct">true</property>
            <simple-property-list name="defaultValue">
                <value type="constant">42</value>
            </simple-property-list>
            <list-property name="selectionList">
                <structure>
                    <property name="value">0</property>
                    <property name="label">Display test for 0</property>
                </structure>
                <structure>
                    <property name="value">42</property>
                    <property name="label">Display test for 42</property>
                </structure>
                <structure>
                    <property name="value">666</property>
                    <property name="label">Display test for 666</property>
                </structure>
                <structure>
                    <property name="value">12345678</property>
                    <property name="label">Display test for 12345678</property>
                </structure>
            </list-property>
            <property name="paramType">simple</property>
            <property name="controlType">list-box</property>
            <property name="mustMatch">false</property>
            <property name="fixedOrder">true</property>
            <structure name="format">
                <property name="category">Unformatted</property>
            </structure>
        </scalar-parameter>
        <scalar-parameter name="IntegerParameterListBoxStaticSingleReqd" id="218">
            <text-property name="helpText">This is some help text for "IntegerParameterListBoxStaticSingleReqd"</text-property>
            <text-property name="promptText">Integer: List Box (static list, single select, required)</text-property>
            <property name="valueType">static</property>
            <property name="dataType">integer</property>
            <property name="distinct">true</property>
            <simple-property-list name="defaultValue">
                <value type="constant">42</value>
            </simple-property-list>
            <list-property name="selectionList">
                <structure>
                    <property name="value">0</property>
                    <property name="label">Display test for 0</property>
                </structure>
                <structure>
                    <property name="value">42</property>
                    <property name="label">Display test for 42</property>
                </structure>
                <structure>
                    <property name="value">666</property>
                    <property name="label">Display test for 666</property>
                </structure>
                <structure>
                    <property name="value">12345678</property>
                    <property name="label">Display test for 12345678</property>
                </structure>
            </list-property>
            <property name="paramType">simple</property>
            <property name="controlType">list-box</property>
            <property name="mustMatch">true</property>
            <property name="fixedOrder">true</property>
            <structure name="format">
                <property name="category">Unformatted</property>
            </structure>
        </scalar-parameter>
        <scalar-parameter name="IntegerParameterListBoxStaticSingleNotReqd" id="337">
            <text-property name="helpText">This is some help text for "IntegerParameterListBoxStaticSingleNotReqd"</text-property>
            <text-property name="promptText">Integer: List Box (static list, single select, not required)</text-property>
            <property name="valueType">static</property>
            <property name="isRequired">false</property>
            <property name="dataType">integer</property>
            <property name="distinct">true</property>
            <simple-property-list name="defaultValue">
                <value type="constant">42</value>
            </simple-property-list>
            <list-property name="selectionList">
                <structure>
                    <property name="value">0</property>
                    <property name="label">Display test for 0</property>
                </structure>
                <structure>
                    <property name="value">42</property>
                    <property name="label">Display test for 42</property>
                </structure>
                <structure>
                    <property name="value">666</property>
                    <property name="label">Display test for 666</property>
                </structure>
                <structure>
                    <property name="value">12345678</property>
                    <property name="label">Display test for 12345678</property>
                </structure>
            </list-property>
            <property name="paramType">simple</property>
            <property name="controlType">list-box</property>
            <property name="mustMatch">true</property>
            <property name="fixedOrder">true</property>
            <structure name="format">
                <property name="category">Unformatted</property>
            </structure>
        </scalar-parameter>
        <scalar-parameter name="IntegerParameterListBoxStaticMultipleReqd" id="220">
            <text-property name="helpText">This is some help text for "IntegerParameterListBoxStaticMultipleReqd"</text-property>
            <text-property name="promptText">Integer: List Box (static list, multiple select, required)</text-property>
            <property name="valueType">static</property>
            <property name="dataType">integer</property>
            <property name="distinct">true</property>
            <simple-property-list name="defaultValue">
                <value type="constant">42</value>
            </simple-property-list>
            <list-property name="selectionList">
                <structure>
                    <property name="value">0</property>
                    <property name="label">Display test for 0</property>
                </structure>
                <structure>
                    <property name="value">42</property>
                    <property name="label">Display test for 42</property>
                </structure>
                <structure>
                    <property name="value">666</property>
                    <property name="label">Display test for 666</property>
                </structure>
                <structure>
                    <property name="value">12345678</property>
                    <property name="label">Display test for 12345678</property>
                </structure>
            </list-property>
            <property name="paramType">multi-value</property>
            <property name="controlType">list-box</property>
            <property name="mustMatch">true</property>
            <property name="fixedOrder">true</property>
            <structure name="format">
                <property name="category">Unformatted</property>
            </structure>
        </scalar-parameter>
        <scalar-parameter name="IntegerParameterListBoxStaticMultipleNotReqd" id="340">
            <text-property name="helpText">This is some help text for "IntegerParameterListBoxStaticMultipleNotReqd"</text-property>
            <text-property name="promptText">Integer: List Box (static list, multiple select, not required)</text-property>
            <property name="valueType">static</property>
            <property name="isRequired">false</property>
            <property name="dataType">integer</property>
            <property name="distinct">true</property>
            <simple-property-list name="defaultValue">
                <value type="constant">42</value>
            </simple-property-list>
            <list-property name="selectionList">
                <structure>
                    <property name="value">0</property>
                    <property name="label">Display test for 0</property>
                </structure>
                <structure>
                    <property name="value">42</property>
                    <property name="label">Display test for 42</property>
                </structure>
                <structure>
                    <property name="value">666</property>
                    <property name="label">Display test for 666</property>
                </structure>
                <structure>
                    <property name="value">12345678</property>
                    <property name="label">Display test for 12345678</property>
                </structure>
            </list-property>
            <property name="paramType">multi-value</property>
            <property name="controlType">list-box</property>
            <property name="mustMatch">true</property>
            <property name="fixedOrder">true</property>
            <structure name="format">
                <property name="category">Unformatted</property>
            </structure>
        </scalar-parameter>
        <scalar-parameter name="FloatParameterTextBoxReqd" id="199">
            <text-property name="helpText">This is some help text for "FloatParameterTextBoxReqd"</text-property>
            <text-property name="promptText">Float: TextBox - required</text-property>
            <property name="valueType">static</property>
            <property name="dataType">float</property>
            <property name="distinct">true</property>
            <simple-property-list name="defaultValue">
                <value type="constant">3.14159</value>
            </simple-property-list>
            <list-property name="selectionList"/>
            <property name="paramType">simple</property>
            <property name="controlType">text-box</property>
            <structure name="format">
                <property name="category">Unformatted</property>
            </structure>
        </scalar-parameter>
        <scalar-parameter name="FloatParameterTextBoxNotReqd" id="349">
            <text-property name="helpText">This is some help text for "FloatParameterTextBoxNotReqd"</text-property>
            <text-property name="promptText">Float: TextBox - not required</text-property>
            <property name="valueType">static</property>
            <property name="isRequired">false</property>
            <property name="dataType">float</property>
            <property name="distinct">true</property>
            <simple-property-list name="defaultValue">
                <value type="constant">3.14159</value>
            </simple-property-list>
            <list-property name="selectionList"/>
            <property name="paramType">simple</property>
            <property name="controlType">text-box</property>
            <structure name="format">
                <property name="category">Unformatted</property>
            </structure>
        </scalar-parameter>
    </parameters>
    <page-setup>
        <simple-master-page name="Simple MasterPage" id="2"/>
    </page-setup>
    <body>
        <label id="3">
            <text-property name="text">400-TestReport04_v1.1</text-property>
        </label>
        <label id="227">
            <property name="fontSize">12pt</property>
            <property name="fontWeight">bold</property>
            <property name="paddingTop">10pt</property>
            <property name="paddingBottom">2pt</property>
            <property name="textAlign">center</property>
            <text-property name="text">Report Parameters</text-property>
        </label>
        <grid id="6">
            <property name="borderBottomStyle">solid</property>
            <property name="borderBottomWidth">thin</property>
            <property name="borderLeftStyle">solid</property>
            <property name="borderLeftWidth">thin</property>
            <property name="borderRightStyle">solid</property>
            <property name="borderRightWidth">thin</property>
            <property name="borderTopStyle">solid</property>
            <property name="borderTopWidth">thin</property>
            <property name="marginTop">0pt</property>
            <property name="marginLeft">4pt</property>
            <property name="marginRight">4pt</property>
            <property name="width">7.84375in</property>
            <column id="7">
                <property name="verticalAlign">top</property>
                <property name="width">1.6041666666666667in</property>
            </column>
            <column id="49">
                <property name="width">3.6666666666666665in</property>
            </column>
            <column id="8">
                <property name="width">2.5729166666666665in</property>
            </column>
            <row id="9">
                <property name="borderBottomStyle">solid</property>
                <property name="borderBottomWidth">thin</property>
                <property name="borderLeftStyle">solid</property>
                <property name="borderLeftWidth">thin</property>
                <property name="borderRightStyle">solid</property>
                <property name="borderRightWidth">thin</property>
                <property name="borderTopStyle">solid</property>
                <property name="borderTopWidth">thin</property>
                <cell id="10">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">2pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingBottom">2pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="50">
                        <property name="fontWeight">bold</property>
                        <text-property name="text">Data type</text-property>
                    </label>
                </cell>
                <cell id="39">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">2pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingBottom">2pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="51">
                        <property name="fontWeight">bold</property>
                        <text-property name="text">Widget</text-property>
                    </label>
                </cell>
                <cell id="11">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">2pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingBottom">2pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="52">
                        <property name="fontWeight">bold</property>
                        <text-property name="text">Parameter value entered</text-property>
                    </label>
                </cell>
            </row>
            <row id="12">
                <cell id="13">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">5pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="54">
                        <text-property name="text">String</text-property>
                    </label>
                </cell>
                <cell id="40">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">5pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="56">
                        <text-property name="text">Textbox - required</text-property>
                    </label>
                </cell>
                <cell id="14">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">5pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <data id="255">
                        <list-property name="boundDataColumns">
                            <structure>
                                <property name="name">StringParameterTextBoxReqd</property>
                                <expression name="expression" type="javascript">params["StringParameterTextBoxReqd"]</expression>
                                <property name="dataType">string</property>
                            </structure>
                        </list-property>
                        <property name="resultSetColumn">StringParameterTextBoxReqd</property>
                    </data>
                </cell>
            </row>
            <row id="231">
                <cell id="232">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                </cell>
                <cell id="233">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="235">
                        <text-property name="text">Textbox - not required</text-property>
                    </label>
                </cell>
                <cell id="234">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <data id="256">
                        <list-property name="boundDataColumns">
                            <structure>
                                <property name="name">StringParameterTextBoxNotReqd</property>
                                <expression name="expression" type="javascript">params["StringParameterTextBoxNotReqd"]</expression>
                                <property name="dataType">string</property>
                            </structure>
                        </list-property>
                        <property name="resultSetColumn">StringParameterTextBoxNotReqd</property>
                    </data>
                </cell>
            </row>
            <row id="15">
                <cell id="16">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                </cell>
                <cell id="41">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="59">
                        <text-property name="text">Combo Box - static list, required</text-property>
                    </label>
                </cell>
                <cell id="17">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <data id="257">
                        <list-property name="boundDataColumns">
                            <structure>
                                <property name="name">StringParameterComboBoxStaticReqd</property>
                                <expression name="expression" type="javascript">params["StringParameterComboBoxStaticReqd"]</expression>
                                <property name="dataType">string</property>
                            </structure>
                        </list-property>
                        <property name="resultSetColumn">StringParameterComboBoxStaticReqd</property>
                    </data>
                </cell>
            </row>
            <row id="241">
                <cell id="242">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                </cell>
                <cell id="243">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="245">
                        <text-property name="text">Combo Box - static list, not required</text-property>
                    </label>
                </cell>
                <cell id="244">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <data id="258">
                        <list-property name="boundDataColumns">
                            <structure>
                                <property name="name">StringParameterComboBoxStaticNotReqd</property>
                                <expression name="expression" type="javascript">params["StringParameterComboBoxStaticNotReqd"]</expression>
                                <property name="dataType">string</property>
                            </structure>
                        </list-property>
                        <property name="resultSetColumn">StringParameterComboBoxStaticNotReqd</property>
                    </data>
                </cell>
            </row>
            <row id="18">
                <cell id="19">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                </cell>
                <cell id="42">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="61">
                        <text-property name="text">Combo Box - dynamic list</text-property>
                    </label>
                </cell>
                <cell id="20">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="62">
                        <property name="fontStyle">italic</property>
                        <property name="color">gray</property>
                        <text-property name="text">not implemented</text-property>
                    </label>
                </cell>
            </row>
            <row id="21">
                <cell id="22">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                </cell>
                <cell id="43">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="65">
                        <text-property name="text">List Box - static list, single select, required</text-property>
                    </label>
                </cell>
                <cell id="23">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <data id="259">
                        <list-property name="boundDataColumns">
                            <structure>
                                <property name="name">StringParameterListBoxStaticSingleReqd</property>
                                <expression name="expression" type="javascript">params["StringParameterListBoxStaticSingleReqd"]</expression>
                                <property name="dataType">string</property>
                            </structure>
                        </list-property>
                        <property name="resultSetColumn">StringParameterListBoxStaticSingleReqd</property>
                    </data>
                </cell>
            </row>
            <row id="248">
                <cell id="249">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                </cell>
                <cell id="250">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="252">
                        <text-property name="text">List Box - static list, single select, not required</text-property>
                    </label>
                </cell>
                <cell id="251">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <data id="260">
                        <list-property name="boundDataColumns">
                            <structure>
                                <property name="name">StringParameterListBoxStaticSingleNotReqd</property>
                                <expression name="expression" type="javascript">params["StringParameterListBoxStaticSingleNotReqd"]</expression>
                                <property name="dataType">string</property>
                            </structure>
                        </list-property>
                        <property name="resultSetColumn">StringParameterListBoxStaticSingleNotReqd</property>
                    </data>
                </cell>
            </row>
            <row id="24">
                <cell id="25">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                </cell>
                <cell id="44">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="66">
                        <text-property name="text">List Box - static list, multiple select, required</text-property>
                    </label>
                </cell>
                <cell id="26">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <data id="267">
                        <list-property name="boundDataColumns">
                            <structure>
                                <property name="name">StringParameterListBoxStaticMultipleReqd</property>
                                <expression name="expression" type="javascript">params["StringParameterListBoxStaticMultipleReqd"]</expression>
                                <property name="dataType">javaObject</property>
                            </structure>
                        </list-property>
                        <property name="resultSetColumn">StringParameterListBoxStaticMultipleReqd</property>
                    </data>
                </cell>
            </row>
            <row id="261">
                <cell id="262">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                </cell>
                <cell id="263">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="265">
                        <text-property name="text">List Box - static list, multiple select, not required</text-property>
                    </label>
                </cell>
                <cell id="264">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <data id="268">
                        <list-property name="boundDataColumns">
                            <structure>
                                <property name="name">StringParameterListBoxStaticMultipleNotReqd</property>
                                <expression name="expression" type="javascript">params["StringParameterListBoxStaticMultipleNotReqd"]</expression>
                                <property name="dataType">javaObject</property>
                            </structure>
                        </list-property>
                        <property name="resultSetColumn">StringParameterListBoxStaticMultipleNotReqd</property>
                    </data>
                </cell>
            </row>
            <row id="27">
                <cell id="28">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                </cell>
                <cell id="45">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="69">
                        <text-property name="text">List Box - dynamic list, single select</text-property>
                    </label>
                </cell>
                <cell id="29">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="71">
                        <property name="fontStyle">italic</property>
                        <property name="color">gray</property>
                        <text-property name="text">not implemented</text-property>
                    </label>
                </cell>
            </row>
            <row id="30">
                <cell id="31">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                </cell>
                <cell id="46">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="70">
                        <text-property name="text">List Box - dynamic list, multiple select</text-property>
                    </label>
                </cell>
                <cell id="32">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="72">
                        <property name="fontStyle">italic</property>
                        <property name="color">gray</property>
                        <text-property name="text">not implemented</text-property>
                    </label>
                </cell>
            </row>
            <row id="189">
                <property name="borderTopColor">#C0C0C0</property>
                <property name="borderTopStyle">solid</property>
                <property name="borderTopWidth">thin</property>
                <cell id="190">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">5pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="193">
                        <text-property name="text">Boolean</text-property>
                    </label>
                </cell>
                <cell id="191">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">5pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="194">
                        <text-property name="text">Check Box</text-property>
                    </label>
                </cell>
                <cell id="192">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">5pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <data id="278">
                        <list-property name="boundDataColumns">
                            <structure>
                                <property name="name">BooleanParameterCheckBox</property>
                                <expression name="expression" type="javascript">params["BooleanParameterCheckBox"]</expression>
                                <property name="dataType">boolean</property>
                            </structure>
                        </list-property>
                        <property name="resultSetColumn">BooleanParameterCheckBox</property>
                    </data>
                </cell>
            </row>
            <row id="185">
                <cell id="186">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">1pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                </cell>
                <cell id="187">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">1pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="197">
                        <text-property name="text">Radio Button - required</text-property>
                    </label>
                </cell>
                <cell id="188">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">1pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <data id="284">
                        <list-property name="boundDataColumns">
                            <structure>
                                <property name="name">BooleanParameterRadioButtonReqd</property>
                                <expression name="expression" type="javascript">params["BooleanParameterRadioButtonReqd"]</expression>
                                <property name="dataType">boolean</property>
                            </structure>
                        </list-property>
                        <property name="resultSetColumn">BooleanParameterRadioButtonReqd</property>
                    </data>
                </cell>
            </row>
            <row id="279">
                <cell id="280">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">1pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                </cell>
                <cell id="281">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">1pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="286">
                        <text-property name="text">Radio Button - not required</text-property>
                    </label>
                </cell>
                <cell id="282">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">1pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <data id="285">
                        <list-property name="boundDataColumns">
                            <structure>
                                <property name="name">BooleanParameterRadioButtonNotReqd</property>
                                <expression name="expression" type="javascript">params["BooleanParameterRadioButtonNotReqd"]</expression>
                                <property name="dataType">boolean</property>
                            </structure>
                        </list-property>
                        <property name="resultSetColumn">BooleanParameterRadioButtonNotReqd</property>
                    </data>
                </cell>
            </row>
            <row id="74">
                <property name="borderTopColor">#C0C0C0</property>
                <property name="borderTopStyle">solid</property>
                <property name="borderTopWidth">thin</property>
                <cell id="75">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">5pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="79">
                        <text-property name="text">Date</text-property>
                    </label>
                </cell>
                <cell id="76">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">5pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="81">
                        <text-property name="text">Textbox - Input format: yyyy.MM.dd, required</text-property>
                    </label>
                </cell>
                <cell id="77">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">5pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <data id="291">
                        <structure name="dateTimeFormat">
                            <property name="category">Custom</property>
                            <property name="pattern">MMM dd, yyyy</property>
                        </structure>
                        <list-property name="boundDataColumns">
                            <structure>
                                <property name="name">DateParameterTextBoxReqd</property>
                                <expression name="expression" type="javascript">params["DateParameterTextBoxReqd"]</expression>
                                <property name="dataType">date</property>
                            </structure>
                        </list-property>
                        <property name="resultSetColumn">DateParameterTextBoxReqd</property>
                    </data>
                </cell>
            </row>
            <row id="287">
                <cell id="288">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">1pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                </cell>
                <cell id="289">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">1pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="294">
                        <text-property name="text">Textbox - Input format: yyyy.MM.dd, not required</text-property>
                    </label>
                </cell>
                <cell id="290">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">1pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <data id="293">
                        <structure name="dateTimeFormat">
                            <property name="category">Custom</property>
                            <property name="pattern">MMM dd, yyyy</property>
                        </structure>
                        <list-property name="boundDataColumns">
                            <structure>
                                <property name="name">DateParameterTextBoxNotReqd</property>
                                <expression name="expression" type="javascript">params["DateParameterTextBoxNotReqd"]</expression>
                                <property name="dataType">date</property>
                            </structure>
                        </list-property>
                        <property name="resultSetColumn">DateParameterTextBoxNotReqd</property>
                    </data>
                </cell>
            </row>
            <row id="171">
                <property name="borderTopColor">#C0C0C0</property>
                <property name="borderTopStyle">solid</property>
                <property name="borderTopWidth">thin</property>
                <cell id="172">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">5pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="178">
                        <text-property name="text">Datetime</text-property>
                    </label>
                </cell>
                <cell id="173">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">5pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="179">
                        <text-property name="text">Textbox - Input format: yyyy.MM.dd hh:mm:ss, required</text-property>
                    </label>
                </cell>
                <cell id="174">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">5pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <data id="300">
                        <structure name="dateTimeFormat">
                            <property name="category">Custom</property>
                            <property name="pattern">MMM dd, yyyy ''at'' kk:mm:ss</property>
                        </structure>
                        <list-property name="boundDataColumns">
                            <structure>
                                <property name="name">DatetimeParameterTextboxReqd</property>
                                <expression name="expression" type="javascript">params["DatetimeParameterTextboxReqd"]</expression>
                                <property name="dataType">date-time</property>
                            </structure>
                        </list-property>
                        <property name="resultSetColumn">DatetimeParameterTextboxReqd</property>
                    </data>
                </cell>
            </row>
            <row id="295">
                <cell id="296">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">1pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                </cell>
                <cell id="297">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">1pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="302">
                        <text-property name="text">Textbox - Input format: yyyy.MM.dd hh:mm:ss, not required</text-property>
                    </label>
                </cell>
                <cell id="298">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">1pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <data id="301">
                        <structure name="dateTimeFormat">
                            <property name="category">Custom</property>
                            <property name="pattern">MMM dd, yyyy ''at'' kk:mm:ss</property>
                        </structure>
                        <list-property name="boundDataColumns">
                            <structure>
                                <property name="name">DatetimeParameterTextboxNotReqd</property>
                                <expression name="expression" type="javascript">params["DatetimeParameterTextboxNotReqd"]</expression>
                                <property name="dataType">date-time</property>
                            </structure>
                        </list-property>
                        <property name="resultSetColumn">DatetimeParameterTextboxNotReqd</property>
                    </data>
                </cell>
            </row>
            <row id="167">
                <property name="borderTopColor">#C0C0C0</property>
                <property name="borderTopStyle">solid</property>
                <property name="borderTopWidth">thin</property>
                <cell id="168">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">5pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="181">
                        <text-property name="text">Time</text-property>
                    </label>
                </cell>
                <cell id="169">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">5pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="182">
                        <text-property name="text">Textbox - Input format: hh:mm:ss, required</text-property>
                    </label>
                </cell>
                <cell id="170">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">5pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <data id="309">
                        <structure name="dateTimeFormat">
                            <property name="category">Custom</property>
                            <property name="pattern">kk:mm:ss</property>
                        </structure>
                        <list-property name="boundDataColumns">
                            <structure>
                                <property name="name">TimeParameterTextboxReqd</property>
                                <expression name="expression" type="javascript">params["TimeParameterTextboxReqd"]</expression>
                                <property name="dataType">time</property>
                            </structure>
                        </list-property>
                        <property name="resultSetColumn">TimeParameterTextboxReqd</property>
                    </data>
                </cell>
            </row>
            <row id="303">
                <cell id="304">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">1pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                </cell>
                <cell id="305">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">1pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="307">
                        <text-property name="text">Textbox - Input format: hh:mm:ss, not required</text-property>
                    </label>
                </cell>
                <cell id="306">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">1pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <data id="310">
                        <structure name="dateTimeFormat">
                            <property name="category">Custom</property>
                            <property name="pattern">kk:mm:ss</property>
                        </structure>
                        <list-property name="boundDataColumns">
                            <structure>
                                <property name="name">TimeParameterTextboxNotReqd</property>
                                <expression name="expression" type="javascript">params["TimeParameterTextboxNotReqd"]</expression>
                                <property name="dataType">time</property>
                            </structure>
                        </list-property>
                        <property name="resultSetColumn">TimeParameterTextboxNotReqd</property>
                    </data>
                </cell>
            </row>
            <row id="159">
                <property name="borderTopColor">#C0C0C0</property>
                <property name="borderTopStyle">solid</property>
                <property name="borderTopWidth">thin</property>
                <cell id="160">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="borderTopColor">#C0C0C0</property>
                    <property name="borderTopStyle">solid</property>
                    <property name="borderTopWidth">thin</property>
                    <property name="paddingTop">5pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="204">
                        <text-property name="text">Integer</text-property>
                    </label>
                </cell>
                <cell id="161">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">5pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="205">
                        <text-property name="text">Textbox - required</text-property>
                    </label>
                </cell>
                <cell id="162">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">5pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <data id="317">
                        <list-property name="boundDataColumns">
                            <structure>
                                <property name="name">IntegerParameterTextBoxReqd</property>
                                <expression name="expression" type="javascript">params["IntegerParameterTextBoxReqd"]</expression>
                                <property name="dataType">integer</property>
                            </structure>
                        </list-property>
                        <property name="resultSetColumn">IntegerParameterTextBoxReqd</property>
                    </data>
                </cell>
            </row>
            <row id="311">
                <cell id="312">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                </cell>
                <cell id="313">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="315">
                        <text-property name="text">Textbox - not required</text-property>
                    </label>
                </cell>
                <cell id="314">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <data id="318">
                        <list-property name="boundDataColumns">
                            <structure>
                                <property name="name">IntegerParameterTextBoxNotReqd</property>
                                <expression name="expression" type="javascript">params["IntegerParameterTextBoxNotReqd"]</expression>
                                <property name="dataType">integer</property>
                            </structure>
                        </list-property>
                        <property name="resultSetColumn">IntegerParameterTextBoxNotReqd</property>
                    </data>
                </cell>
            </row>
            <row id="155">
                <cell id="156">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                </cell>
                <cell id="157">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="208">
                        <text-property name="text">Combo Box - static list, required</text-property>
                    </label>
                </cell>
                <cell id="158">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <data id="325">
                        <list-property name="boundDataColumns">
                            <structure>
                                <property name="name">IntegerParameterComboBoxStaticReqd</property>
                                <expression name="expression" type="javascript">params["IntegerParameterComboBoxStaticReqd"]</expression>
                                <property name="dataType">integer</property>
                            </structure>
                        </list-property>
                        <property name="resultSetColumn">IntegerParameterComboBoxStaticReqd</property>
                    </data>
                </cell>
            </row>
            <row id="320">
                <cell id="321">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                </cell>
                <cell id="322">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="324">
                        <text-property name="text">Combo Box - static list, not required</text-property>
                    </label>
                </cell>
                <cell id="323">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <data id="326">
                        <list-property name="boundDataColumns">
                            <structure>
                                <property name="name">IntegerParameterComboBoxStaticNotReqd</property>
                                <expression name="expression" type="javascript">params["IntegerParameterComboBoxStaticNotReqd"]</expression>
                                <property name="dataType">integer</property>
                            </structure>
                        </list-property>
                        <property name="resultSetColumn">IntegerParameterComboBoxStaticNotReqd</property>
                    </data>
                </cell>
            </row>
            <row id="151">
                <cell id="152">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                </cell>
                <cell id="153">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="209">
                        <text-property name="text">Combo Box - dynamic list</text-property>
                    </label>
                </cell>
                <cell id="154">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="210">
                        <property name="fontStyle">italic</property>
                        <property name="color">gray</property>
                        <text-property name="text">not implemented</text-property>
                    </label>
                </cell>
            </row>
            <row id="147">
                <cell id="148">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                </cell>
                <cell id="149">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="211">
                        <text-property name="text">List Box - static list, single select, required</text-property>
                    </label>
                </cell>
                <cell id="150">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <data id="338">
                        <list-property name="boundDataColumns">
                            <structure>
                                <property name="name">IntegerParameterListBoxStaticSingleReqd</property>
                                <expression name="expression" type="javascript">params["IntegerParameterListBoxStaticSingleReqd"]</expression>
                                <property name="dataType">integer</property>
                            </structure>
                        </list-property>
                        <property name="resultSetColumn">IntegerParameterListBoxStaticSingleReqd</property>
                    </data>
                </cell>
            </row>
            <row id="327">
                <cell id="328">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                </cell>
                <cell id="329">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="335">
                        <text-property name="text">List Box - static list, single select, not required</text-property>
                    </label>
                </cell>
                <cell id="330">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <data id="339">
                        <list-property name="boundDataColumns">
                            <structure>
                                <property name="name">IntegerParameterListBoxStaticSingleNotReqd</property>
                                <expression name="expression" type="javascript">params["IntegerParameterListBoxStaticSingleNotReqd"]</expression>
                                <property name="dataType">integer</property>
                            </structure>
                        </list-property>
                        <property name="resultSetColumn">IntegerParameterListBoxStaticSingleNotReqd</property>
                    </data>
                </cell>
            </row>
            <row id="143">
                <cell id="144">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                </cell>
                <cell id="145">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="212">
                        <text-property name="text">List Box - static list, multiple select, required</text-property>
                    </label>
                </cell>
                <cell id="146">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <data id="341">
                        <list-property name="boundDataColumns">
                            <structure>
                                <property name="name">IntegerParameterListBoxStaticMultipleReqd</property>
                                <expression name="expression" type="javascript">params["IntegerParameterListBoxStaticMultipleReqd"]</expression>
                                <property name="dataType">javaObject</property>
                            </structure>
                        </list-property>
                        <property name="resultSetColumn">IntegerParameterListBoxStaticMultipleReqd</property>
                    </data>
                </cell>
            </row>
            <row id="331">
                <cell id="332">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                </cell>
                <cell id="333">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="336">
                        <text-property name="text">List Box - static list, multiple select, not required</text-property>
                    </label>
                </cell>
                <cell id="334">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <data id="342">
                        <list-property name="boundDataColumns">
                            <structure>
                                <property name="name">IntegerParameterListBoxStaticMultipleNotReqd</property>
                                <expression name="expression" type="javascript">params["IntegerParameterListBoxStaticMultipleNotReqd"]</expression>
                                <property name="dataType">javaObject</property>
                            </structure>
                        </list-property>
                        <property name="resultSetColumn">IntegerParameterListBoxStaticMultipleNotReqd</property>
                    </data>
                </cell>
            </row>
            <row id="139">
                <cell id="140">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                </cell>
                <cell id="141">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="213">
                        <text-property name="text">List Box - dynamic list, single select</text-property>
                    </label>
                </cell>
                <cell id="142">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="215">
                        <property name="fontStyle">italic</property>
                        <property name="color">gray</property>
                        <text-property name="text">not implemented</text-property>
                    </label>
                </cell>
            </row>
            <row id="135">
                <cell id="136">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                </cell>
                <cell id="137">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="214">
                        <text-property name="text">List Box - dynamic list, multiple select</text-property>
                    </label>
                </cell>
                <cell id="138">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="216">
                        <property name="fontStyle">italic</property>
                        <property name="color">gray</property>
                        <text-property name="text">not implemented</text-property>
                    </label>
                </cell>
            </row>
            <row id="131">
                <property name="borderTopColor">#C0C0C0</property>
                <property name="borderTopStyle">solid</property>
                <property name="borderTopWidth">thin</property>
                <cell id="132">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">5pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="228">
                        <text-property name="text">Float</text-property>
                    </label>
                </cell>
                <cell id="133">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">5pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="229">
                        <text-property name="text">Textbox - required</text-property>
                    </label>
                </cell>
                <cell id="134">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">5pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <data id="350">
                        <list-property name="boundDataColumns">
                            <structure>
                                <property name="name">FloatParameterTextBoxReqd</property>
                                <expression name="expression" type="javascript">params["FloatParameterTextBoxReqd"]</expression>
                                <property name="dataType">float</property>
                            </structure>
                        </list-property>
                        <property name="resultSetColumn">FloatParameterTextBoxReqd</property>
                    </data>
                </cell>
            </row>
            <row id="344">
                <cell id="345">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">1pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                </cell>
                <cell id="346">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">1pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <label id="348">
                        <text-property name="text">Textbox - not required</text-property>
                    </label>
                </cell>
                <cell id="347">
                    <property name="borderLeftStyle">solid</property>
                    <property name="borderLeftWidth">thin</property>
                    <property name="borderRightStyle">solid</property>
                    <property name="borderRightWidth">thin</property>
                    <property name="paddingTop">1pt</property>
                    <property name="paddingLeft">3pt</property>
                    <property name="paddingRight">3pt</property>
                    <data id="351">
                        <list-property name="boundDataColumns">
                            <structure>
                                <property name="name">FloatParameterTextBoxNotReqd</property>
                                <expression name="expression" type="javascript">params["FloatParameterTextBoxNotReqd"]</expression>
                                <property name="dataType">float</property>
                            </structure>
                        </list-property>
                        <property name="resultSetColumn">FloatParameterTextBoxNotReqd</property>
                    </data>
                </cell>
            </row>
        </grid>
    </body>
</report>');
insert into reporting.report_version (report_version_id, report_id, file_name, rptdesign, version_name, version_code, active, created_on) VALUES ('bbd23109-e1e9-404e-913d-32150d8fd92f', '702d5daa-e23d-4f00-b32b-67b44c06d8f6', '400-TestReport04_v1.1.rptdesign', (SELECT rptdesign FROM tmp_rptdesign), '1.1', 2, true, '2015-05-10T19:00:00');
delete from tmp_rptdesign;


--insert into reporting.report_parameter (report_parameter_id, report_version_id, name, prompt_text, order_index, created_on) values ('206723d6-50e7-4f4a-85c0-cb679e92ad6b', 'dbc0883b-afe3-4147-87b4-0ed35869cd35', 'Report01Param01', 'Prompt text for parameter #1 for Report #1', 1, '2015-05-06T15:00:00');
--insert into reporting.report_parameter (report_parameter_id, report_version_id, name, prompt_text, order_index, created_on) values ('36fc0de4-cc4c-4efa-8c47-73e0e254e449', 'fb8a4896-609b-438a-81b0-c70587f8f637', 'Report02Param01', 'Prompt text for parameter #1 for Report #2', 1, '2015-05-06T15:00:01');
--insert into reporting.report_parameter (report_parameter_id, report_version_id, name, prompt_text, order_index, created_on) values ('5a201251-b04f-406e-b07c-c6d55dc3dc85', '2f88d7ff-8b74-457a-bce6-89d3e74d0bb9', 'Report03Param01', 'Prompt text for parameter #1 for Report #3', 1, '2015-05-06T15:00:02');
--insert into reporting.report_parameter (report_parameter_id, report_version_id, name, prompt_text, order_index, created_on) values ('4c2bd07e-c7d7-451a-8c07-c8f589959382', '293abf69-1516-4e9b-84ae-241d25c13e8d', 'Report04Param01', 'Prompt text for parameter #1 for Report #4', 1, '2015-05-06T15:00:03');
--insert into reporting.report_parameter (report_parameter_id, report_version_id, name, prompt_text, order_index, created_on) values ('73792710-8d69-477a-8d44-fe646507eaf8', '8dd9bb5a-1565-4d38-8a8b-857803088626', 'Report05Param01', 'Prompt text for parameter #1 for Report #5', 1, '2015-05-06T15:00:04');
--insert into reporting.report_parameter (report_parameter_id, report_version_id, name, prompt_text, order_index, created_on) values ('86e93f08-86fd-4aed-99c2-1f4af29382d3', 'd481c452-990c-4ea3-9afa-3ea60cef04ab', 'Report06Param01', 'Prompt text for parameter #1 for Report #6', 1, '2015-05-06T15:00:05');


-- Create "authority" rows:
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('1e4f29b9-3183-4f54-a4ee-96c2347d7e06', 'MANAGE_AUTHORITIES'  , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('dae0f68f-11c6-438c-8312-aca4d95731fc', 'MANAGE_CATEGORIES'   , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('cd2c5d93-9b57-4a8b-b789-84dd567e0fa2', 'MANAGE_FILEFORMATS'  , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('7e1ee8bb-3d5d-481a-b0f9-5c3fae3f4e38', 'MANAGE_FILESYNCING'  , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('e5b569e6-bb2a-4f00-a9ca-f85ef0a3a339', 'MANAGE_JOBPROCESSOR' , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('12b1cb22-7686-4c9b-b73b-a25d4cb31663', 'MANAGE_JOBS'         , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('ace1edd3-6a5b-4b40-a802-79616472b893', 'DELETE_JOBS'         , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('bb8f7964-504a-4544-8638-11a62cc9a2ca', 'MANAGE_JOBSTATUSES'  , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('1bbc9e73-1095-4471-bdb2-726b10e47936', 'MANAGE_PREFERENCES'  , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('7b758de7-cd54-43fa-baa0-dfbe59e66000', 'MANAGE_REPORTS'      , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('2efd4eca-bcb2-4cec-b804-3142c8297d65', 'UPLOAD_REPORTS'      , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('2dac7af0-ba7d-4009-a313-e9a288272e90', 'MANAGE_ROLES'        , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('608d6156-b155-487d-bdd3-4e00260b7443', 'MANAGE_SUBSCRIPTIONS', true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('94db0a84-e366-4ab8-aeba-171482979f3d', 'DELETE_SUBSCRIPTIONS', true, current_timestamp AT TIME ZONE 'UTC');


-- Insert  tree of [role] records:
--
-- Admin role with login privileges. encoded_password is BCrypt.encode('reportadmin')
INSERT INTO reporting.role (role_id, username, login_role, encoded_password, full_name, email_address, time_zone_id, enabled, active, created_on) VALUES ('54aa1d35-f67d-47e6-8bea-cadd6085796e', 'reportadmin', true , '$2a$08$PjY.jVSaf0x2HCnETH6aj.0Gb3RWX9VMunMcpIfrJGBGjsYA2S6vC', 'Report Administrator', null                  , 'CET'           , true, true, current_timestamp AT TIME ZONE 'UTC');
--
-- encoded_password is BCrypt.encode('password1')
INSERT INTO reporting.role (role_id, username, login_role, encoded_password, full_name, email_address, time_zone_id, enabled, active, created_on) VALUES ('29fe8a1f-7826-4df0-8bfd-151b54198655', 'user1'      , true , '$2a$08$SIVVleb01XxPHvn72ThPo.NAgKrbMuq5r/JvcWj5g6LIPDqnuVeT2', 'User Number 1'       , 'user1@somedomain.com', 'CET'           , true, true, current_timestamp AT TIME ZONE 'UTC');
-- encoded_password is BCrypt.encode('password2')
INSERT INTO reporting.role (role_id, username, login_role, encoded_password, full_name, email_address, time_zone_id, enabled, active, created_on) VALUES ('fa06393a-d341-4bf6-b047-1a8c6a383483', 'user2'      , false, '$2a$08$SjqsEsbb86MU/eggjgjfuuvQ0WnDhKm1oJ9IJn/uc04J51ksXNbN6', 'User Number 2'       , 'user2@somedomain.com', 'CET'           , true, true, current_timestamp AT TIME ZONE 'UTC');
-- encoded_password is BCrypt.encode('password3')
INSERT INTO reporting.role (role_id, username, login_role, encoded_password, full_name, email_address, time_zone_id, enabled, active, created_on) VALUES ('b85fd129-17d9-40e7-ac11-7541040f8627', 'user3'      , true , '$2a$08$qmN5yWFDL9MT8kjftuVeguljomDvAMkSDaFnw9oSwKdV0RHmNg5TG', 'User Number 3'       , 'user3@somedomain.com', 'Canada/Pacific', true, true, current_timestamp AT TIME ZONE 'UTC');
-- encoded_password is BCrypt.encode('password4')
INSERT INTO reporting.role (role_id, username, login_role, encoded_password, full_name, email_address, time_zone_id, enabled, active, created_on) VALUES ('46e477dc-085f-4714-a24f-742428579fcc', 'user4'      , true , '$2a$08$Co4iHF1kcy1j3r7nd6hBgOAJUgDKaIitNx.BTW8gbVukqwRZr6MLu', 'User Number 4'       , 'user4@somedomain.com', 'GMT'           , true, true, current_timestamp AT TIME ZONE 'UTC');
--
-- Make user "user4" a child of "reportadmin", effectively making "user4" an administrator:
INSERT INTO reporting.role_role (role_role_id, parent_role_id, child_role_id, created_on) VALUES ('3b2c7b99-cf00-43c6-85a7-f4e17bbea386', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '46e477dc-085f-4714-a24f-742428579fcc', current_timestamp AT TIME ZONE 'UTC');


-- Give the "Administrator" role all authorities by creating "role_authority" rows:
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('f812aab5-2390-43f3-add3-a3d673939a73', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '1e4f29b9-3183-4f54-a4ee-96c2347d7e06', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('62c78032-5ec7-440c-8107-386b745b9ab9', '54aa1d35-f67d-47e6-8bea-cadd6085796e', 'dae0f68f-11c6-438c-8312-aca4d95731fc', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('eb8e56c2-7a8e-4dbe-97ca-a0e6166b258a', '54aa1d35-f67d-47e6-8bea-cadd6085796e', 'cd2c5d93-9b57-4a8b-b789-84dd567e0fa2', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('7560ef1c-4acf-43bd-af27-b5ad9f992f57', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '7e1ee8bb-3d5d-481a-b0f9-5c3fae3f4e38', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('44dd6566-a02c-47eb-b889-31016fe1d8ed', '54aa1d35-f67d-47e6-8bea-cadd6085796e', 'e5b569e6-bb2a-4f00-a9ca-f85ef0a3a339', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('222e6e22-f3e3-4631-847f-31896139987a', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '12b1cb22-7686-4c9b-b73b-a25d4cb31663', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('d3a80355-b464-4763-97ab-cd76af70b687', '54aa1d35-f67d-47e6-8bea-cadd6085796e', 'ace1edd3-6a5b-4b40-a802-79616472b893', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('69ee95d1-f6b0-4805-b653-e33ad3955609', '54aa1d35-f67d-47e6-8bea-cadd6085796e', 'bb8f7964-504a-4544-8638-11a62cc9a2ca', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('bbaddcf3-ef9c-49d1-9e5a-d9197a106b9e', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '1bbc9e73-1095-4471-bdb2-726b10e47936', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('8e90d278-a555-4a3a-981a-8e329eac63d4', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '7b758de7-cd54-43fa-baa0-dfbe59e66000', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('659e6aa3-14bf-4ba2-8630-538da94f6ffc', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '2efd4eca-bcb2-4cec-b804-3142c8297d65', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('2ef4aaf6-9ea4-48d0-a5a4-79670a264b79', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '2dac7af0-ba7d-4009-a313-e9a288272e90', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('fb5685ae-2470-449f-bfac-a2a291b863b4', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '608d6156-b155-487d-bdd3-4e00260b7443', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('13e14dbc-d30d-4006-95ed-b8de417a2315', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '94db0a84-e366-4ab8-aeba-171482979f3d', current_timestamp AT TIME ZONE 'UTC');


-- Create a "Q-Free administrator" role that can be used to bypass external
-- authentication by an authentication provider.
-- encoded_password is BCrypt.encode('qfreereportserveradmin_Af5Dj%4$'):
INSERT INTO reporting.role (role_id, username, login_role, encoded_password, full_name, email_address, time_zone_id, enabled, active, created_on) VALUES ('10ab3537-0b12-44fa-a27b-6cf1aac14282', 'qfreereportserveradmin', true , '$2a$08$53v/RUtexw7Ovdx4i2F44O4XcyOLLZklf39XZW1C4jT3JjBJQ8fi6', 'Q-Free Administrator', null                  , 'CET'           , true, true, current_timestamp AT TIME ZONE 'UTC');
--
-- Give the "Q-Free administrator" role all authorities by creating "role_authority" rows:
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('4cb38206-bd6c-4db1-a596-0944b386c093', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '1e4f29b9-3183-4f54-a4ee-96c2347d7e06', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('e2ef968a-7fd6-4e9e-b74a-58fc6f208a50', '10ab3537-0b12-44fa-a27b-6cf1aac14282', 'dae0f68f-11c6-438c-8312-aca4d95731fc', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('143e65a8-3051-4512-bf02-940455f25744', '10ab3537-0b12-44fa-a27b-6cf1aac14282', 'cd2c5d93-9b57-4a8b-b789-84dd567e0fa2', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('e40a6bc3-3888-49ec-a9f5-6c17df4907a2', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '7e1ee8bb-3d5d-481a-b0f9-5c3fae3f4e38', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('6603e5e2-0b71-4e0c-8983-6c1c50efb0d6', '10ab3537-0b12-44fa-a27b-6cf1aac14282', 'e5b569e6-bb2a-4f00-a9ca-f85ef0a3a339', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('89bc2070-aedf-4959-9492-8f1a051f2c47', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '12b1cb22-7686-4c9b-b73b-a25d4cb31663', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('d806bc73-1744-471c-8f24-14af227f780b', '10ab3537-0b12-44fa-a27b-6cf1aac14282', 'ace1edd3-6a5b-4b40-a802-79616472b893', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('43c97218-f645-47be-ac64-7004e34ee31c', '10ab3537-0b12-44fa-a27b-6cf1aac14282', 'bb8f7964-504a-4544-8638-11a62cc9a2ca', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('bdb0077e-ddd1-4651-8584-7d67f6a5d7e6', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '1bbc9e73-1095-4471-bdb2-726b10e47936', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('f3f5546e-d819-4a4a-92be-c6ee150e02d0', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '7b758de7-cd54-43fa-baa0-dfbe59e66000', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('3d1392ab-b5b8-45b8-96e1-cf7060836924', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '2efd4eca-bcb2-4cec-b804-3142c8297d65', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('ce4ffcfd-7802-4e1b-bdf1-6d2b70062cef', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '2dac7af0-ba7d-4009-a313-e9a288272e90', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('54246034-83ed-488c-b447-05fd0b1e57b4', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '608d6156-b155-487d-bdd3-4e00260b7443', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('0fff6274-a9dc-4262-aa29-5feff9414810', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '94db0a84-e366-4ab8-aeba-171482979f3d', current_timestamp AT TIME ZONE 'UTC');


--(role_id                               , username, login_role)
--('29fe8a1f-7826-4df0-8bfd-151b54198655', 'user1' , true      );
--('fa06393a-d341-4bf6-b047-1a8c6a383483', 'user2' , false     ); XXX
--('b85fd129-17d9-40e7-ac11-7541040f8627', 'user3' , true      );
--('46e477dc-085f-4714-a24f-742428579fcc', 'user4' , true      );

--(report_id                             , report_category_id                    , name             , number, active)
--('d65f3d9c-f67d-4beb-9936-9dfa19aa1407', '7a482694-51d2-42d0-b0e2-19dd13bbbc64', 'Test Report #01', 100   , true  );
--('c7f1d394-9814-4ede-bb01-2700187d79ca', '7a482694-51d2-42d0-b0e2-19dd13bbbc64', 'Test Report #02', 200   , true  );
--('fe718314-5b39-40e7-aed2-279354c04a9d', '7a482694-51d2-42d0-b0e2-19dd13bbbc64', 'Test Report #03', 300   , false ); XXX
--('702d5daa-e23d-4f00-b32b-67b44c06d8f6', 'bb2bc482-c19a-4c19-a087-e68ffc62b5a0', 'Test Report #04', 400   , true  );
--('f1f06b15-c0b6-488d-9eed-74e867a47d5a', '72d7cb27-1770-4cc7-b301-44d39ccf1e76', 'Test Report #05', 500   , false ); XXX
--('adc50b28-cb84-4ede-9759-43f467ac22ec', 'bb2bc482-c19a-4c19-a087-e68ffc62b5a0', 'Test Report #06', 600   , true  );

-- Role "user1" has access to report "Test Report #01", "Test Report #02", "Test Report #03":
insert into reporting.role_report (role_report_id, role_id, report_id, created_on) values ('50213453-0b2f-426d-b4f1-e70030b1592d', '29fe8a1f-7826-4df0-8bfd-151b54198655', 'd65f3d9c-f67d-4beb-9936-9dfa19aa1407', '2015-05-06T15:30:00');
insert into reporting.role_report (role_report_id, role_id, report_id, created_on) values ('4280bdae-26a3-4dd8-8f0c-aa6c73a5e89c', '29fe8a1f-7826-4df0-8bfd-151b54198655', 'c7f1d394-9814-4ede-bb01-2700187d79ca', '2015-05-06T15:30:00');
insert into reporting.role_report (role_report_id, role_id, report_id, created_on) values ('2e08e5aa-c35c-48ba-a3a9-be0cfe475db4', '29fe8a1f-7826-4df0-8bfd-151b54198655', 'fe718314-5b39-40e7-aed2-279354c04a9d', '2015-05-06T15:30:00');
-- Role "user2" has access to report "Test Report #02", "Test Report #03", "Test Report #04", "Test Report #05":
insert into reporting.role_report (role_report_id, role_id, report_id, created_on) values ('6c99bd7d-0fc0-494c-8a1f-bfcfcc7e34f8', 'fa06393a-d341-4bf6-b047-1a8c6a383483', 'c7f1d394-9814-4ede-bb01-2700187d79ca', '2015-05-06T15:30:00');
insert into reporting.role_report (role_report_id, role_id, report_id, created_on) values ('2a3af647-0518-4dfb-8466-f94da3d755c0', 'fa06393a-d341-4bf6-b047-1a8c6a383483', 'fe718314-5b39-40e7-aed2-279354c04a9d', '2015-05-06T15:30:00');
insert into reporting.role_report (role_report_id, role_id, report_id, created_on) values ('461716c1-f0c7-437c-a3a2-6c63938f9128', 'fa06393a-d341-4bf6-b047-1a8c6a383483', '702d5daa-e23d-4f00-b32b-67b44c06d8f6', '2015-05-06T15:30:00');
insert into reporting.role_report (role_report_id, role_id, report_id, created_on) values ('fc9edf17-c8af-433e-9469-11b56c9d53ca', 'fa06393a-d341-4bf6-b047-1a8c6a383483', 'f1f06b15-c0b6-488d-9eed-74e867a47d5a', '2015-05-06T15:30:00');
-- Role "user3" has access to reports "Test Report #01", "Test Report #02", "Test Report #03", "Test Report #04", "Test Report #05", "Test Report #06":
insert into reporting.role_report (role_report_id, role_id, report_id, created_on) values ('3ad583f6-8116-4208-a803-e65f2b877c60', 'b85fd129-17d9-40e7-ac11-7541040f8627', 'd65f3d9c-f67d-4beb-9936-9dfa19aa1407', '2015-05-06T15:30:00');
insert into reporting.role_report (role_report_id, role_id, report_id, created_on) values ('f72562bc-62d1-410e-a18b-94f15443bd5a', 'b85fd129-17d9-40e7-ac11-7541040f8627', 'c7f1d394-9814-4ede-bb01-2700187d79ca', '2015-05-06T15:30:00');
insert into reporting.role_report (role_report_id, role_id, report_id, created_on) values ('580084a1-c77b-4bac-8a93-395532ba27ae', 'b85fd129-17d9-40e7-ac11-7541040f8627', 'fe718314-5b39-40e7-aed2-279354c04a9d', '2015-05-06T15:30:00');
insert into reporting.role_report (role_report_id, role_id, report_id, created_on) values ('2bae387e-1f79-4456-ada9-e6a2510eab23', 'b85fd129-17d9-40e7-ac11-7541040f8627', '702d5daa-e23d-4f00-b32b-67b44c06d8f6', '2015-05-06T15:30:00');
insert into reporting.role_report (role_report_id, role_id, report_id, created_on) values ('6dc23516-1dab-439d-9609-e1172ac51adf', 'b85fd129-17d9-40e7-ac11-7541040f8627', 'f1f06b15-c0b6-488d-9eed-74e867a47d5a', '2015-05-06T15:30:00');
insert into reporting.role_report (role_report_id, role_id, report_id, created_on) values ('31d1b180-c6d6-4bed-a614-61359b54c9d9', 'b85fd129-17d9-40e7-ac11-7541040f8627', 'adc50b28-cb84-4ede-9759-43f467ac22ec', '2015-05-06T15:30:00');
-- Role "user4" has no direct access to any reports:
--insert into reporting.role_report (role_report_id, role_id, report_id, created_on) values ('0186bd70-2555-4603-8ed9-c389fd60d4f8', '46e477dc-085f-4714-a24f-742428579fcc', '', '2015-05-06T15:30:00');


---- Role "aabb", report parameter "Report01Param01" (multi-valued):
--insert into reporting.role_parameter_value (role_parameter_value_id, role_id, report_parameter_id, string_value, created_on) values ('7e7f2284-efcd-447b-b0fd-07b841400666', 'ee56f34d-dbb4-41c1-9d30-ce29cf973820', '206723d6-50e7-4f4a-85c0-cb679e92ad6b', 'Role aabb''s integer value for Report01Param01'   , '2015-05-31T13:00:00');
---- Role "aabb", report parameter "Report02Param01" (single-valued):
--insert into reporting.role_parameter_value (role_parameter_value_id, role_id, report_parameter_id, string_value, created_on) values ('c0b449cc-4b87-4733-8648-a1b0168a1925', 'ee56f34d-dbb4-41c1-9d30-ce29cf973820', '36fc0de4-cc4c-4efa-8c47-73e0e254e449', 'Role aabb''s value for Report02Param01'   , '2015-05-31T13:00:00');
---- Role "aabb", report parameter "Report03Param01" (multi-valued):
--insert into reporting.role_parameter_value (role_parameter_value_id, role_id, report_parameter_id, string_value, created_on) values ('967147d0-24e2-46a5-8423-e18a87bf13c1', 'ee56f34d-dbb4-41c1-9d30-ce29cf973820', '5a201251-b04f-406e-b07c-c6d55dc3dc85', 'Role aabb''s value #1 for Report03Param01', '2015-05-31T13:00:00');
--insert into reporting.role_parameter_value (role_parameter_value_id, role_id, report_parameter_id, string_value, created_on) values ('7fe51a01-c311-46e3-9a8b-70346a71b47f', 'ee56f34d-dbb4-41c1-9d30-ce29cf973820', '5a201251-b04f-406e-b07c-c6d55dc3dc85', 'Role aabb''s value #2 for Report03Param01', '2015-05-31T13:00:00');
--insert into reporting.role_parameter_value (role_parameter_value_id, role_id, report_parameter_id, string_value, created_on) values ('58d7d22d-620d-474e-aae5-a57620229110', 'ee56f34d-dbb4-41c1-9d30-ce29cf973820', '5a201251-b04f-406e-b07c-c6d55dc3dc85', 'Role aabb''s value #3 for Report03Param01', '2015-05-31T13:00:00');
---- Role "aabb", report parameter "Report04Param01" (single-valued):
--insert into reporting.role_parameter_value (role_parameter_value_id, role_id, report_parameter_id, string_value, created_on) values ('80546b03-0e32-46c2-abe8-4b7f3e34d90b', 'ee56f34d-dbb4-41c1-9d30-ce29cf973820', '4c2bd07e-c7d7-451a-8c07-c8f589959382', 'Role aabb''s value for Report04Param01'   , '2015-05-31T13:00:00');
---- Role "acca", report parameter "Report05Param01" (multi-valued):
--insert into reporting.role_parameter_value (role_parameter_value_id, role_id, report_parameter_id, string_value, created_on) values ('213fac69-8424-4d88-a55d-90ac43e18832', '39bc8737-b9eb-4cb5-9765-1c33dd5ee40c', '73792710-8d69-477a-8d44-fe646507eaf8', 'Role acca''s value for Report05Param01'   , '2015-05-31T13:00:00');
---- Role "acca", report parameter "Report04Param01" (single-valued):
--insert into reporting.role_parameter_value (role_parameter_value_id, role_id, report_parameter_id, string_value, created_on) values ('558f3ecb-15dc-4eec-951a-eedc6ebf3fb0', '39bc8737-b9eb-4cb5-9765-1c33dd5ee40c', '86e93f08-86fd-4aed-99c2-1f4af29382d3', 'Role acca''s value for Report06Param01'   , '2015-05-31T13:00:00');
--
--
---- "Default" (not role-specific) values for some test configuration parameters:
--insert into reporting.configuration (configuration_id, param_name, role_id, param_type, boolean_value , created_on) VALUES ('1f4f4814-577f-426d-bb98-4cc47446d578', 'TEST_BOOLEAN'  , null, 'BOOLEAN'  , true                      , '2015-07-14T00:00:00');
--insert into reporting.configuration (configuration_id, param_name, role_id, param_type, bytea_value   , created_on) VALUES ('e61167ae-2331-408c-9b5c-b2880adbea5a', 'TEST_BYTEARRAY', null, 'BYTEARRAY', null                      , '2015-07-14T00:00:00');
--insert into reporting.configuration (configuration_id, param_name, role_id, param_type, date_value    , created_on) VALUES ('7d325b3c-d307-4fd0-bdae-c349ec2d4835', 'TEST_DATE'     , null, 'DATE'     , '1958-05-06'              , '2015-07-14T00:00:00');
--insert into reporting.configuration (configuration_id, param_name, role_id, param_type, datetime_value, created_on) VALUES ('5e7d5a1e-5d42-4a9c-b790-e45e545463f7', 'TEST_DATETIME' , null, 'DATETIME' , '2008-11-29T01:00:00'     , '2015-07-14T00:00:00');
--insert into reporting.configuration (configuration_id, param_name, role_id, param_type, double_value  , created_on) VALUES ('4bedeeae-6436-4c64-862b-4e87ffaa4527', 'TEST_DOUBLE'   , null, 'DOUBLE'   , 299792458.467             , '2015-07-14T00:00:00');
--insert into reporting.configuration (configuration_id, param_name, role_id, param_type, float_value   , created_on) VALUES ('d34f8309-56e3-4328-9227-0d8c1e57a941', 'TEST_FLOAT'    , null, 'FLOAT'    , 3.14159                   , '2015-07-14T00:00:00');
--insert into reporting.configuration (configuration_id, param_name, role_id, param_type, integer_value , created_on) VALUES ('b4fd2271-26fb-4db7-bfe7-07211d849364', 'TEST_INTEGER'  , null, 'INTEGER'  , 42                        , '2015-07-14T00:00:00');
--insert into reporting.configuration (configuration_id, param_name, role_id, param_type, long_value    , created_on) VALUES ('334051cf-bea6-47d9-8bac-1b7c314ae985', 'TEST_LONG'     , null, 'LONG'     , 1234567890                , '2015-07-14T00:00:00');
--insert into reporting.configuration (configuration_id, param_name, role_id, param_type, string_value  , created_on) VALUES ('62f9ca07-79ce-48dd-8357-873191ea8b9d', 'TEST_STRING'   , null, 'STRING'   , 'Meaning of life'         , '2015-07-14T00:00:00');
--insert into reporting.configuration (configuration_id, param_name, role_id, param_type, text_value    , created_on) VALUES ('b0aa23ef-2be5-4041-a95f-b7615af639b5', 'TEST_TEXT'     , null, 'TEXT'     , 'Meaning of life - really', '2015-07-14T00:00:00');
--insert into reporting.configuration (configuration_id, param_name, role_id, param_type, time_value    , created_on) VALUES ('e0537e8a-62f3-4240-ba19-da0d5005092e', 'TEST_TIME'     , null, 'TIME'     , '16:17:18'                , '2015-07-14T00:00:00');
---- Role-specific values (Role "aabb") for some test configuration parameters:
--insert into reporting.configuration (configuration_id, param_name, role_id, param_type, boolean_value , created_on) VALUES ('345585af-9351-4f3b-87d0-dacf3c8d7580', 'TEST_BOOLEAN'  , 'ee56f34d-dbb4-41c1-9d30-ce29cf973820', 'BOOLEAN'  , false                     , '2015-07-14T00:00:00');
--insert into reporting.configuration (configuration_id, param_name, role_id, param_type, bytea_value   , created_on) VALUES ('b4d1e45b-137e-4507-b785-bc396cad16e2', 'TEST_BYTEARRAY', 'ee56f34d-dbb4-41c1-9d30-ce29cf973820', 'BYTEARRAY', null                      , '2015-07-14T00:00:00');
--insert into reporting.configuration (configuration_id, param_name, role_id, param_type, date_value    , created_on) VALUES ('3093f887-874c-4d1f-ab08-ab64bac3938c', 'TEST_DATE'     , 'ee56f34d-dbb4-41c1-9d30-ce29cf973820', 'DATE'     , '1971-09-08'              , '2015-07-14T00:00:00');
--insert into reporting.configuration (configuration_id, param_name, role_id, param_type, datetime_value, created_on) VALUES ('f139e7d6-4afd-4ec1-b812-e824780fd4b2', 'TEST_DATETIME' , 'ee56f34d-dbb4-41c1-9d30-ce29cf973820', 'DATETIME' , '2008-11-30T02:00:01'     , '2015-07-14T00:00:00');
--insert into reporting.configuration (configuration_id, param_name, role_id, param_type, double_value  , created_on) VALUES ('25ec072f-a6f1-4b15-9301-9f5fbb3084e8', 'TEST_DOUBLE'   , 'ee56f34d-dbb4-41c1-9d30-ce29cf973820', 'DOUBLE'   , 1.618033988749            , '2015-07-14T00:00:00');
--insert into reporting.configuration (configuration_id, param_name, role_id, param_type, float_value   , created_on) VALUES ('f912dce9-5d10-4552-94ee-dca3e22d7e9b', 'TEST_FLOAT'    , 'ee56f34d-dbb4-41c1-9d30-ce29cf973820', 'FLOAT'    , 2.71828                   , '2015-07-14T00:00:00');
--insert into reporting.configuration (configuration_id, param_name, role_id, param_type, integer_value , created_on) VALUES ('5bb07d17-247c-41e5-97b5-2e5d3de8493b', 'TEST_INTEGER'  , 'ee56f34d-dbb4-41c1-9d30-ce29cf973820', 'INTEGER'  , 666                       , '2015-07-14T00:00:00');
--insert into reporting.configuration (configuration_id, param_name, role_id, param_type, long_value    , created_on) VALUES ('5564308d-23d1-4712-bcd7-d31224e6d49b', 'TEST_LONG'     , 'ee56f34d-dbb4-41c1-9d30-ce29cf973820', 'LONG'     , 9876543210                , '2015-07-14T00:00:00');
--insert into reporting.configuration (configuration_id, param_name, role_id, param_type, string_value  , created_on) VALUES ('cea34ae3-e9ec-450a-a4da-122554755b3f', 'TEST_STRING'   , 'ee56f34d-dbb4-41c1-9d30-ce29cf973820', 'STRING'   , 'Yada yada'               , '2015-07-14T00:00:00');
--insert into reporting.configuration (configuration_id, param_name, role_id, param_type, text_value    , created_on) VALUES ('b32e1203-6884-4ed6-a265-b4c87fbbfd35', 'TEST_TEXT'     , 'ee56f34d-dbb4-41c1-9d30-ce29cf973820', 'TEXT'     , 'Yada yada yada yada'     , '2015-07-14T00:00:00');
--insert into reporting.configuration (configuration_id, param_name, role_id, param_type, time_value    , created_on) VALUES ('96a86f9a-da4e-4173-9267-94a68176bff0', 'TEST_TIME'     , 'ee56f34d-dbb4-41c1-9d30-ce29cf973820', 'TIME'     , '00:00:01'                , '2015-07-14T00:00:00');
--
--
--insert into reporting.subscription (subscription_id, role_id, report_version_id, document_format_id, delivery_datetime_run_at, delivery_cron_schedule, delivery_time_zone_id, email_address, description, enabled, active, created_on) values ('7f68e31c-2884-4638-b3e5-c64697a28bd1', 'ee56f34d-dbb4-41c1-9d30-ce29cf973820', 'dbc0883b-afe3-4147-87b4-0ed35869cd35', '30800d77-5fdd-44bc-94a3-1502bd307c1d', '2016-01-01T03:00:00', '00 6 * * 1', 'CET', 'jeffreyz@q-free.com', 'Description for Subscription #1', false, true, '2015-06-06T15:45:30');
--
--
---- [subscription_parameter] rows for [report_parameter]'s associated with 
---- [report] "Report name #01" for [subscription] "Description for Subscription #1":
--insert into reporting.subscription_parameter (subscription_parameter_id, subscription_id, report_parameter_id, created_on) VALUES ('4e490877-0803-47e2-a74e-61865aadf9a9', '7f68e31c-2884-4638-b3e5-c64697a28bd1', '206723d6-50e7-4f4a-85c0-cb679e92ad6b', '2015-03-15T11:00:00');
--
---- [subscription_parameter_value] rows for [subscription_parameter]'s associated with 
---- [subscription] "Description for Subscription #1":
--insert into reporting.subscription_parameter_value (subscription_parameter_value_id, subscription_parameter_id, boolean_value, date_value, datetime_value, float_value, integer_value, string_value, time_value, year_number, years_ago, month_number, months_ago, weeks_ago, day_of_week_in_month_ordinal, day_of_week_in_month_number, day_of_week_number, day_of_month_number, days_ago, duration_to_add_years, duration_to_add_months, duration_to_add_weeks, duration_to_add_days, duration_to_add_hours, duration_to_add_minutes, duration_to_add_seconds, duration_subtract_one_day_for_dates, created_on) VALUES ('a5159f31-1c2b-496f-af04-bcb02cc04cfb', '4e490877-0803-47e2-a74e-61865aadf9a9', null, null, null, null, 12321, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, true, '2015-03-15T11:00:01');
--
--
---- Create [job] rows. We cannot specifiy job_id because its data type is bigint;
---- hence, it may not be unique. Therefore, we let PostgreSQL create the job_id
---- values for us (the DDL for this column includes: "default nextval('job_job_id_seq'::regclass)"
----
---- job for [report_version] "version 1" of report "Report name #01" run by role "aabb". [document_format] = PDF, [job_status] = "Queued":
--insert into reporting.job (report_version_id, subscription_id, job_status_id, job_status_remarks, role_id, document_format_id, url, file_name, document, encoded, job_status_set_at, report_ran_at, email_address, report_emailed_at, created_on) VALUES ('dbc0883b-afe3-4147-87b4-0ed35869cd35', null, '08de9764-735f-4c82-bbe9-3981b29cc133', null, 'ee56f34d-dbb4-41c1-9d30-ce29cf973820', '30800d77-5fdd-44bc-94a3-1502bd307c1d', null, null, null, null, '2015-06-06T00:00:15', null, null, null, '2015-06-06T00:00:15');
---- job for [report_version] "version 1" of report "Report name #01" run by role "aabb". [document_format] = OpenDocument Spreadsheet, [job_status] = "Queued":
--insert into reporting.job (report_version_id, subscription_id, job_status_id, job_status_remarks, role_id, document_format_id, url, file_name, document, encoded, job_status_set_at, report_ran_at, email_address, report_emailed_at, created_on) VALUES ('dbc0883b-afe3-4147-87b4-0ed35869cd35', null, '08de9764-735f-4c82-bbe9-3981b29cc133', null, 'ee56f34d-dbb4-41c1-9d30-ce29cf973820', '05a4ad8d-6f30-4d6d-83d5-995345a8dc58', null, null, null, null, '2015-06-06T00:00:15', null, null, null, '2015-06-07T00:00:15');
--------------------------------------------------------------------------------


COMMIT;
