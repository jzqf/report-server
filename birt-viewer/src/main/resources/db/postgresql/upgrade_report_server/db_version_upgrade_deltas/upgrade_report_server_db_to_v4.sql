BEGIN TRANSACTION;

-- Update the DB schema from v3 to v4:

-- Database diff generated with pgModeler (PostgreSQL Database Modeler).
-- pgModeler  version: 0.8.1
-- PostgreSQL version: 9.4

-- [ Diff summary ]
-- Dropped objects: 0
-- Created objects: 6
-- Changed objects: 1
-- Truncated tables: 0

SET search_path=public,pg_catalog,reporting;
-- ddl-end --

-- Rename column "media_type" to "internet_media_type" instead of deleting 
-- column "media_type" and then creating column "internet_media_type". This is
-- so that we preserve the data in this column.
ALTER TABLE reporting.document_format RENAME COLUMN media_type TO internet_media_type;
--
---- [ Dropped objects ] --
--ALTER TABLE reporting.document_format DROP COLUMN IF EXISTS media_type CASCADE;
---- ddl-end --
--
---- [ Created objects ] --
---- object: internet_media_type | type: COLUMN --
---- ALTER TABLE reporting.document_format DROP COLUMN IF EXISTS internet_media_type CASCADE;
--ALTER TABLE reporting.document_format ADD COLUMN internet_media_type character varying(100) NOT NULL;
---- ddl-end --


-- object: email | type: COLUMN --
-- ALTER TABLE reporting.job DROP COLUMN IF EXISTS email CASCADE;
ALTER TABLE reporting.job ADD COLUMN email character varying(160);
-- ddl-end --


-- For PostgreSQL, "timestamp" is equivalent to "timestamp without time zone".

-- object: job_status_set_at | type: COLUMN --
-- ALTER TABLE reporting.job DROP COLUMN IF EXISTS job_status_set_at CASCADE;
ALTER TABLE reporting.job ADD COLUMN job_status_set_at timestamp NOT NULL;
-- ddl-end --


-- object: report_emailed_at | type: COLUMN --
-- ALTER TABLE reporting.job DROP COLUMN IF EXISTS report_emailed_at CASCADE;
ALTER TABLE reporting.job ADD COLUMN report_emailed_at timestamp;
-- ddl-end --


-- object: report_ran_at | type: COLUMN --
-- ALTER TABLE reporting.job DROP COLUMN IF EXISTS report_ran_at CASCADE;
ALTER TABLE reporting.job ADD COLUMN report_ran_at timestamp;
-- ddl-end --


-- object: duration_subtract_one_day_for_dates | type: COLUMN --
-- ALTER TABLE reporting.subscription_parameter_value DROP COLUMN IF EXISTS duration_subtract_one_day_for_dates CASCADE;
ALTER TABLE reporting.subscription_parameter_value ADD COLUMN duration_subtract_one_day_for_dates boolean NOT NULL;
-- ddl-end --



-- Update the DB content from v3 to v4:

insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('5125c537-e178-42de-b4dd-e538fa3da802', 'Canceled' , 'CANCELED' , true, current_timestamp AT TIME ZONE 'UTC');


-- Update global configuration record for "DB_VERSION" to reflect the new version.
UPDATE reporting.configuration SET (integer_value, string_value) = (4, '4') WHERE param_name='DB_VERSION' AND role_id IS NULL;

COMMIT;