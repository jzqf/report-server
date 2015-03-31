BEGIN TRANSACTION;

insert into reporting.report_category (report_category_id, description, abbreviation, active, created_on) values ('7a482694-51d2-42d0-b0e2-19dd13bbbc64', 'Accounting'       , 'ACCT' , false, '2015-04-30T12:00:00');
insert into reporting.report_category (report_category_id, description, abbreviation, active, created_on) values ('bb2bc482-c19a-4c19-a087-e68ffc62b5a0', 'Q-Free internal'  , 'QFREE', true , '2015-05-30T22:00:00');
insert into reporting.report_category (report_category_id, description, abbreviation, active, created_on) values ('5c3cc664-b685-4f6e-8d9a-2927c6bcffdc', 'Manual validation', 'MIR'  , false, '2015-03-01T02:00:00');
insert into reporting.report_category (report_category_id, description, abbreviation, active, created_on) values ('72d7cb27-1770-4cc7-b301-44d39ccf1e76', 'Traffic'          , 'TRA'  , true , '2015-03-01T02:00:10');

CREATE TEMPORARY TABLE tmp_rptdesign (rptdesign text) ON COMMIT DROP;
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
</report>');

insert into reporting.report (report_id, report_category_id, name, created_on, rptdesign) values ('d65f3d9c-f67d-4beb-9936-9dfa19aa1407', '7a482694-51d2-42d0-b0e2-19dd13bbbc64', 'Report name #01', '2014-06-09T22:00:00', (SELECT rptdesign FROM tmp_rptdesign));
insert into reporting.report (report_id, report_category_id, name, created_on, rptdesign) values ('c7f1d394-9814-4ede-bb01-2700187d79ca', '7a482694-51d2-42d0-b0e2-19dd13bbbc64', 'Report name #02', '2014-06-09T22:10:00', (SELECT rptdesign FROM tmp_rptdesign));
insert into reporting.report (report_id, report_category_id, name, created_on, rptdesign) values ('fe718314-5b39-40e7-aed2-279354c04a9d', '7a482694-51d2-42d0-b0e2-19dd13bbbc64', 'Report name #03', '2014-07-04T23:30:00', (SELECT rptdesign FROM tmp_rptdesign));
insert into reporting.report (report_id, report_category_id, name, created_on, rptdesign) values ('702d5daa-e23d-4f00-b32b-67b44c06d8f6', 'bb2bc482-c19a-4c19-a087-e68ffc62b5a0', 'Report name #04', '2014-03-25T12:15:00', (SELECT rptdesign FROM tmp_rptdesign));
insert into reporting.report (report_id, report_category_id, name, created_on, rptdesign) values ('f1f06b15-c0b6-488d-9eed-74e867a47d5a', '72d7cb27-1770-4cc7-b301-44d39ccf1e76', 'Report name #05', '2014-03-25T12:15:00', (SELECT rptdesign FROM tmp_rptdesign));
insert into reporting.report (report_id, report_category_id, name, created_on, rptdesign) values ('adc50b28-cb84-4ede-9759-43f467ac22ec', 'bb2bc482-c19a-4c19-a087-e68ffc62b5a0', 'Report name #06', '2015-05-06T15:00:00', (SELECT rptdesign FROM tmp_rptdesign));

insert into reporting.widget (widget_id, name, description, multiple_select ,active, created_on ) values ('b8e91527-8b0e-4ed2-8cba-8cb8989ba8e2', 'widget #1', 'widget #1 description', false, true, '2015-03-31T02:00:00');

insert into reporting.report_parameter (report_parameter_id, report_id, name, description, widget_id, required, created_on) values ('206723d6-50e7-4f4a-85c0-cb679e92ad6b', 'd65f3d9c-f67d-4beb-9936-9dfa19aa1407', 'Report01Param01', 'Parameter #1 for Report #1', 'b8e91527-8b0e-4ed2-8cba-8cb8989ba8e2', true,  '2015-05-06T15:00:00');
insert into reporting.report_parameter (report_parameter_id, report_id, name, description, widget_id, required, created_on) values ('36fc0de4-cc4c-4efa-8c47-73e0e254e449', 'c7f1d394-9814-4ede-bb01-2700187d79ca', 'Report02Param01', 'Parameter #1 for Report #2', 'b8e91527-8b0e-4ed2-8cba-8cb8989ba8e2', false, '2015-05-06T15:00:01');
insert into reporting.report_parameter (report_parameter_id, report_id, name, description, widget_id, required, created_on) values ('5a201251-b04f-406e-b07c-c6d55dc3dc85', 'fe718314-5b39-40e7-aed2-279354c04a9d', 'Report03Param01', 'Parameter #1 for Report #3', 'b8e91527-8b0e-4ed2-8cba-8cb8989ba8e2', true,  '2015-05-06T15:00:02');
insert into reporting.report_parameter (report_parameter_id, report_id, name, description, widget_id, required, created_on) values ('4c2bd07e-c7d7-451a-8c07-c8f589959382', '702d5daa-e23d-4f00-b32b-67b44c06d8f6', 'Report04Param01', 'Parameter #1 for Report #4', 'b8e91527-8b0e-4ed2-8cba-8cb8989ba8e2', true,  '2015-05-06T15:00:03');
insert into reporting.report_parameter (report_parameter_id, report_id, name, description, widget_id, required, created_on) values ('73792710-8d69-477a-8d44-fe646507eaf8', 'f1f06b15-c0b6-488d-9eed-74e867a47d5a', 'Report05Param01', 'Parameter #1 for Report #5', 'b8e91527-8b0e-4ed2-8cba-8cb8989ba8e2', true,  '2015-05-06T15:00:04');
insert into reporting.report_parameter (report_parameter_id, report_id, name, description, widget_id, required, created_on) values ('86e93f08-86fd-4aed-99c2-1f4af29382d3', 'adc50b28-cb84-4ede-9759-43f467ac22ec', 'Report06Param01', 'Parameter #1 for Report #6', 'b8e91527-8b0e-4ed2-8cba-8cb8989ba8e2', false, '2015-05-06T15:00:05');

COMMIT;
