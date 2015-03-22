insert into reporting.report_category (report_category_id, description, abbreviation, active) values ('7a482694-51d2-42d0-b0e2-19dd13bbbc64', 'Accounting', 'ACCT', false);
insert into reporting.report_category (report_category_id, description, abbreviation, active) values ('bb2bc482-c19a-4c19-a087-e68ffc62b5a0', 'Q-Free internal', 'QFREE', true);
insert into reporting.report_category (report_category_id, description, abbreviation, active) values ('5c3cc664-b685-4f6e-8d9a-2927c6bcffdc', 'Manual validation', 'MIR', false);
insert into reporting.report_category (report_category_id, description, abbreviation, active) values ('72d7cb27-1770-4cc7-b301-44d39ccf1e76', 'Traffic', 'TRA', true);

insert into reporting.report_parameter (report_parameter_id, description, abbreviation, active) values ('206723d6-50e7-4f4a-85c0-cb679e92ad6b', 'Accounting', 'ACCT', false);
insert into reporting.report_parameter (report_parameter_id, description, abbreviation, active) values ('36fc0de4-cc4c-4efa-8c47-73e0e254e449', 'Q-Free internal', 'QFREE', true);
insert into reporting.report_parameter (report_parameter_id, description, abbreviation, active) values ('5a201251-b04f-406e-b07c-c6d55dc3dc85', 'Manual validation', 'MIR', true);
insert into reporting.report_parameter (report_parameter_id, description, abbreviation, active) values ('4c2bd07e-c7d7-451a-8c07-c8f589959382', 'Traffic', 'TRA', true);

insert into reporting.report (report_id, report_category_id, name, created_on) values ('d65f3d9c-f67d-4beb-9936-9dfa19aa1407', '7a482694-51d2-42d0-b0e2-19dd13bbbc64', 'Report name #01', '2012-06-09T22:00:00');
insert into reporting.report (report_id, report_category_id, name, created_on) values ('c7f1d394-9814-4ede-bb01-2700187d79ca', '7a482694-51d2-42d0-b0e2-19dd13bbbc64', 'Report name #02', '2012-06-09T22:10:00');
insert into reporting.report (report_id, report_category_id, name, created_on) values ('fe718314-5b39-40e7-aed2-279354c04a9d', '7a482694-51d2-42d0-b0e2-19dd13bbbc64', 'Report name #03', '2012-07-04T23:30:00');
insert into reporting.report (report_id, report_category_id, name, created_on) values ('702d5daa-e23d-4f00-b32b-67b44c06d8f6', 'bb2bc482-c19a-4c19-a087-e68ffc62b5a0', 'Report name #04', '2012-03-25T12:15:00');
insert into reporting.report (report_id, report_category_id, name, created_on) values ('f1f06b15-c0b6-488d-9eed-74e867a47d5a', '72d7cb27-1770-4cc7-b301-44d39ccf1e76', 'Report name #05', '2012-03-25T12:15:00');
insert into reporting.report (report_id, report_category_id, name, created_on) values ('fe4d399b-3f2e-4bac-86a5-9aaaca4c35de', '72d7cb27-1770-4cc7-b301-44d39ccf1e76', 'Report name #06', '2012-03-25T12:25:00');
insert into reporting.report (report_id, report_category_id, name, created_on) values ('ef665a19-019b-421f-960b-9b65206b5edd', '72d7cb27-1770-4cc7-b301-44d39ccf1e76', 'Report name #07', '2012-03-25T12:35:00');
insert into reporting.report (report_id, report_category_id, name, created_on) values ('629177eb-1c0b-46d9-93f3-4421c56c2f4a', '72d7cb27-1770-4cc7-b301-44d39ccf1e76', 'Report name #08', '2012-03-25T12:45:00');
insert into reporting.report (report_id, report_category_id, name, created_on) values ('8d9a517b-4628-4a17-af26-21252fafcb5f', '72d7cb27-1770-4cc7-b301-44d39ccf1e76', 'Report name #09', '2012-03-25T12:55:00');
insert into reporting.report (report_id, report_category_id, name, created_on) values ('2c2b279b-8d8d-4b15-a795-6de0a0796a03', '72d7cb27-1770-4cc7-b301-44d39ccf1e76', 'Report name #10', '2012-03-25T13:05:00');
insert into reporting.report (report_id, report_category_id, name, created_on) values ('8b42eeef-928a-49cb-b827-4177b5ad6f6d', '72d7cb27-1770-4cc7-b301-44d39ccf1e76', 'Report name #11', '2012-03-25T13:15:00');
insert into reporting.report (report_id, report_category_id, name, created_on) values ('c606d7f3-9c62-4db3-bba7-52fa58e31327', '72d7cb27-1770-4cc7-b301-44d39ccf1e76', 'Report name #12', '2012-03-25T13:25:00');
insert into reporting.report (report_id, report_category_id, name, created_on) values ('75162a48-3031-4e46-a1b5-5fb09248e01e', '72d7cb27-1770-4cc7-b301-44d39ccf1e76', 'Report name #13', '2012-03-25T13:35:00');
insert into reporting.report (report_id, report_category_id, name, created_on) values ('0308594d-081f-4adf-8ead-33f8e1e02217', '72d7cb27-1770-4cc7-b301-44d39ccf1e76', 'Report name #14', '2012-03-25T13:45:00');
insert into reporting.report (report_id, report_category_id, name, created_on) values ('f8b6f20e-626a-4039-b385-7106c15d0204', '72d7cb27-1770-4cc7-b301-44d39ccf1e76', 'Report name #15', '2012-03-25T13:55:00');
