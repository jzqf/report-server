insert into reporting.report_category (description, abbreviation, active) values ('Accounting', 'ACCT', false);
insert into reporting.report_category (description, abbreviation, active) values ('Q-Free internal', 'QFREE', true);
insert into reporting.report_category (description, abbreviation, active) values ('Manual validation', 'MIR', false);
insert into reporting.report_category (description, abbreviation, active) values ('Traffic', 'TRA', true);

insert into reporting.report_parameter (description, abbreviation, active) values ('Accounting', 'ACCT', false);
insert into reporting.report_parameter (description, abbreviation, active) values ('Q-Free internal', 'QFREE', true);
insert into reporting.report_parameter (description, abbreviation, active) values ('Manual validation', 'MIR', true);
insert into reporting.report_parameter (description, abbreviation, active) values ('Traffic', 'TRA', true);

insert into reporting.report (report_category_id, name, created_on) values (1, 'Report name #01', '2012-06-09 22:00:00Z');
insert into reporting.report (report_category_id, name, created_on) values (1, 'Report name #02', '2012-06-09 22:10:00Z');
insert into reporting.report (report_category_id, name, created_on) values (1, 'Report name #03', '2012-07-04 23:30:00Z');
insert into reporting.report (report_category_id, name, created_on) values (2, 'Report name #04', '2012-03-25 12:15:00Z');
insert into reporting.report (report_category_id, name, created_on) values (4, 'Report name #05', '2012-03-25 12:15:00Z');
insert into reporting.report (report_category_id, name, created_on) values (4, 'Report name #06', '2012-03-25 12:25:00Z');
insert into reporting.report (report_category_id, name, created_on) values (4, 'Report name #07', '2012-03-25 12:35:00Z');
insert into reporting.report (report_category_id, name, created_on) values (4, 'Report name #08', '2012-03-25 12:45:00Z');
insert into reporting.report (report_category_id, name, created_on) values (4, 'Report name #09', '2012-03-25 12:55:00Z');
insert into reporting.report (report_category_id, name, created_on) values (4, 'Report name #10', '2012-03-25 13:05:00Z');
insert into reporting.report (report_category_id, name, created_on) values (4, 'Report name #11', '2012-03-25 13:15:00Z');
insert into reporting.report (report_category_id, name, created_on) values (4, 'Report name #12', '2012-03-25 13:25:00Z');
insert into reporting.report (report_category_id, name, created_on) values (4, 'Report name #13', '2012-03-25 13:35:00Z');
insert into reporting.report (report_category_id, name, created_on) values (4, 'Report name #14', '2012-03-25 13:45:00Z');
insert into reporting.report (report_category_id, name, created_on) values (4, 'Report name #15', '2012-03-25 13:55:00Z');
