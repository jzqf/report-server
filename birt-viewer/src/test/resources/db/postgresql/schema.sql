-- PostgreSQL:

DROP TABLE IF EXISTS reporting.report;
DROP TABLE IF EXISTS reporting.report_category;
DROP TABLE IF EXISTS reporting.report_parameter;

CREATE TABLE reporting.report_category (
  report_category_id  uuid         NOT NULL DEFAULT uuid_generate_v4(),
  description         varchar(25)  NOT NULL,
  abbreviation        varchar(20)  NOT NULL,
  active              boolean      NOT NULL,
  PRIMARY KEY (report_category_id)
);

CREATE TABLE reporting.report (
  report_id           uuid         NOT NULL DEFAULT uuid_generate_v4(),
  report_category_id  uuid         NOT NULL,
  name                varchar(80)  NOT NULL,
  created_on          timestamp    NOT NULL,
  PRIMARY KEY (report_id),
  FOREIGN KEY (report_category_id) REFERENCES report_category(report_category_id)
);

CREATE TABLE reporting.report_parameter (
  report_parameter_id  uuid        NOT NULL DEFAULT uuid_generate_v4(),
  description         varchar(25)  NOT NULL,
  abbreviation        varchar(20)  NOT NULL,
  active              boolean      NOT NULL,
  PRIMARY KEY (report_parameter_id)
);