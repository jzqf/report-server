-- H2:

-- These commands are not necessary for an embedded database that is used for
-- testing because we create a new datbase on each run.
--DROP TABLE IF EXISTS reporting.report;
--DROP TABLE IF EXISTS reporting.report_category;
--DROP SCHEMA IF EXISTS reporting;

CREATE SCHEMA reporting;

CREATE TABLE reporting.report_category (
  report_category_id  uuid         NOT NULL DEFAULT RANDOM_UUID(),
  description         varchar(25)  NOT NULL,
  abbreviation        varchar(20)  NOT NULL,
  active              boolean      NOT NULL,
  PRIMARY KEY (report_category_id)
);

CREATE TABLE reporting.report (
  report_id           uuid         NOT NULL DEFAULT RANDOM_UUID(),
  report_category_id  uuid         NOT NULL,
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
