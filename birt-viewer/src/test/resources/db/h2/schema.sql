-- H2:

-- This commands are not necessary because we create a new embedded datbase on
-- each run.
--DROP TABLE IF EXISTS reporting.report;
--DROP TABLE IF EXISTS reporting.report_category;
--DROP SCHEMA IF EXISTS reporting;

CREATE SCHEMA reporting;

CREATE TABLE reporting.report_category (
  report_category_id  identity,
  description         varchar(25)  not null,
  abbreviation        varchar(20)  not null,
  active              boolean      not null
);

CREATE TABLE reporting.report (
  report_id           integer      identity primary key,
  report_category_id  integer      not null,
  name                varchar(80)  not null,
  created_on          timestamp    not null,
  FOREIGN KEY (report_category_id) REFERENCES report_category(report_category_id)
);
