-- H2:

-- These commands are not necessary because we create a new embedded datbase on
-- each run.
--DROP TABLE IF EXISTS reporting.report;
--DROP TABLE IF EXISTS reporting.report_category;
--DROP SCHEMA IF EXISTS reporting;

CREATE SCHEMA reporting;

CREATE TABLE reporting.report_category (
  report_category_id  identity     NOT NULL,
  description         varchar(25)  NOT NULL,
  abbreviation        varchar(20)  NOT NULL,
  active              boolean      NOT NULL,
  PRIMARY KEY (report_category_id)
);

CREATE TABLE reporting.report (
  report_id           identity     NOT NULL,
  report_category_id  integer      NOT NULL,
  name                varchar(80)  NOT NULL,
  created_on          timestamp    NOT NULL,
  PRIMARY KEY (report_id),
  FOREIGN KEY (report_category_id) REFERENCES report_category(report_category_id)
);

CREATE TABLE reporting.report_parameter (
  report_parameter_id  uuid         NOT NULL DEFAULT RANDOM_UUID(),
  description          varchar(25)  NOT NULL,
  abbreviation         varchar(20)  NOT NULL,
  active               boolean      NOT NULL,
  PRIMARY KEY (report_parameter_id)
);
