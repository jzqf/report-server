BEGIN TRANSACTION;

-- Update the DB schema from v2 to v3:

-- Database diff generated with pgModeler (PostgreSQL Database Modeler).
-- pgModeler  version: 0.8.1
-- PostgreSQL version: 9.4

-- [ Diff summary ]
-- Dropped objects: 10
-- Created objects: 48
-- Changed objects: 5
-- Truncated tables: 0

SET search_path=public,pg_catalog,reporting;
-- ddl-end --


-- [ Dropped objects ] --
ALTER TABLE reporting.subscription_parameter_value DROP CONSTRAINT IF EXISTS fk_subscriptionparametervalue_subscription CASCADE;
-- ddl-end --
ALTER TABLE reporting.subscription_parameter_value DROP CONSTRAINT IF EXISTS fk_subscriptionparametervalue_reportparameter CASCADE;
-- ddl-end --
ALTER TABLE reporting.role_parameter_value DROP CONSTRAINT IF EXISTS fk_roleparametervalue_role CASCADE;
-- ddl-end --
ALTER TABLE reporting.role_parameter_value DROP CONSTRAINT IF EXISTS fk_roleparametervalue_reportparameter CASCADE;
-- ddl-end --
ALTER TABLE reporting.report_parameter DROP CONSTRAINT IF EXISTS fk_reportparameter_report CASCADE;
-- ddl-end --
ALTER TABLE reporting.job_parameter_value DROP CONSTRAINT IF EXISTS fk_jobparametervalue_reportparameter CASCADE;
-- ddl-end --
ALTER TABLE reporting.job_parameter_value DROP CONSTRAINT IF EXISTS fk_jobparametervalue_job CASCADE;
-- ddl-end --
ALTER TABLE reporting.job DROP CONSTRAINT IF EXISTS fk_job_report CASCADE;
-- ddl-end --
ALTER TABLE reporting.role_parameter_value DROP CONSTRAINT IF EXISTS uc_roleparametervalue_role_parameter_value CASCADE;
-- ddl-end --
ALTER TABLE reporting.job_parameter_value DROP CONSTRAINT IF EXISTS uc_jobparametervalue_job_parameter_value CASCADE;
-- ddl-end --
ALTER TABLE reporting.job_parameter_value DROP COLUMN IF EXISTS job_id CASCADE;
-- ddl-end --
ALTER TABLE reporting.job_parameter_value DROP COLUMN IF EXISTS report_parameter_id CASCADE;
-- ddl-end --
ALTER TABLE reporting.role_parameter_value DROP COLUMN IF EXISTS report_parameter_id CASCADE;
-- ddl-end --
ALTER TABLE reporting.role_parameter_value DROP COLUMN IF EXISTS role_id CASCADE;
-- ddl-end --
ALTER TABLE reporting.subscription DROP COLUMN IF EXISTS cron_schedule CASCADE;
-- ddl-end --
ALTER TABLE reporting.subscription DROP COLUMN IF EXISTS run_once_at CASCADE;
-- ddl-end --
ALTER TABLE reporting.subscription_parameter_value DROP COLUMN IF EXISTS days_relative CASCADE;
-- ddl-end --
ALTER TABLE reporting.subscription_parameter_value DROP COLUMN IF EXISTS months_relative CASCADE;
-- ddl-end --
ALTER TABLE reporting.subscription_parameter_value DROP COLUMN IF EXISTS week_of_month_number CASCADE;
-- ddl-end --
ALTER TABLE reporting.subscription_parameter_value DROP COLUMN IF EXISTS week_of_year_number CASCADE;
-- ddl-end --
ALTER TABLE reporting.subscription_parameter_value DROP COLUMN IF EXISTS weeks_relative CASCADE;
-- ddl-end --
ALTER TABLE reporting.subscription_parameter_value DROP COLUMN IF EXISTS years_relative CASCADE;
-- ddl-end --
ALTER TABLE reporting.subscription_parameter_value DROP COLUMN IF EXISTS report_parameter_id CASCADE;
-- ddl-end --
ALTER TABLE reporting.subscription_parameter_value DROP COLUMN IF EXISTS subscription_id CASCADE;
-- ddl-end --


-- [ Created objects ] --
-- object: job_status_remarks | type: COLUMN --
-- ALTER TABLE reporting.job DROP COLUMN IF EXISTS job_status_remarks CASCADE;
ALTER TABLE reporting.job ADD COLUMN job_status_remarks text;
-- ddl-end --


-- object: job_status_id | type: COLUMN --
-- ALTER TABLE reporting.job DROP COLUMN IF EXISTS job_status_id CASCADE;
ALTER TABLE reporting.job ADD COLUMN job_status_id uuid NOT NULL;
-- ddl-end --


-- object: subscription_id | type: COLUMN --
-- ALTER TABLE reporting.job DROP COLUMN IF EXISTS subscription_id CASCADE;
ALTER TABLE reporting.job ADD COLUMN subscription_id uuid;
-- ddl-end --


-- object: reporting.job_parameter_job_parameter_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS reporting.job_parameter_job_parameter_id_seq CASCADE;
CREATE SEQUENCE reporting.job_parameter_job_parameter_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --
ALTER SEQUENCE reporting.job_parameter_job_parameter_id_seq OWNER TO report_server_app;
-- ddl-end --

-- object: reporting.job_parameter | type: TABLE --
-- DROP TABLE IF EXISTS reporting.job_parameter CASCADE;
CREATE TABLE reporting.job_parameter(
	job_parameter_id bigint NOT NULL DEFAULT nextval('job_parameter_job_parameter_id_seq'::regclass),
	created_on timestamp NOT NULL,
	job_id bigint NOT NULL,
	report_parameter_id uuid NOT NULL,
	CONSTRAINT job_parameter_pkey PRIMARY KEY (job_parameter_id),
	CONSTRAINT uc_jobparameter_job_parameter UNIQUE (job_id,report_parameter_id)

);
-- ddl-end --
ALTER TABLE reporting.job_parameter OWNER TO report_server_app;
-- ddl-end --

-- object: boolean_value | type: COLUMN --
-- ALTER TABLE reporting.job_parameter_value DROP COLUMN IF EXISTS boolean_value CASCADE;
ALTER TABLE reporting.job_parameter_value ADD COLUMN boolean_value boolean;
-- ddl-end --


-- object: date_value | type: COLUMN --
-- ALTER TABLE reporting.job_parameter_value DROP COLUMN IF EXISTS date_value CASCADE;
ALTER TABLE reporting.job_parameter_value ADD COLUMN date_value date;
-- ddl-end --


-- object: datetime_value | type: COLUMN --
-- ALTER TABLE reporting.job_parameter_value DROP COLUMN IF EXISTS datetime_value CASCADE;
ALTER TABLE reporting.job_parameter_value ADD COLUMN datetime_value timestamp;
-- ddl-end --


-- object: float_value | type: COLUMN --
-- ALTER TABLE reporting.job_parameter_value DROP COLUMN IF EXISTS float_value CASCADE;
ALTER TABLE reporting.job_parameter_value ADD COLUMN float_value double precision;
-- ddl-end --


-- object: integer_value | type: COLUMN --
-- ALTER TABLE reporting.job_parameter_value DROP COLUMN IF EXISTS integer_value CASCADE;
ALTER TABLE reporting.job_parameter_value ADD COLUMN integer_value integer;
-- ddl-end --


-- object: time_value | type: COLUMN --
-- ALTER TABLE reporting.job_parameter_value DROP COLUMN IF EXISTS time_value CASCADE;
ALTER TABLE reporting.job_parameter_value ADD COLUMN time_value time;
-- ddl-end --


-- object: job_parameter_id | type: COLUMN --
-- ALTER TABLE reporting.job_parameter_value DROP COLUMN IF EXISTS job_parameter_id CASCADE;
ALTER TABLE reporting.job_parameter_value ADD COLUMN job_parameter_id bigint NOT NULL;
-- ddl-end --


-- object: reporting.job_status | type: TABLE --
-- DROP TABLE IF EXISTS reporting.job_status CASCADE;
CREATE TABLE reporting.job_status(
	job_status_id uuid NOT NULL DEFAULT uuid_generate_v4(),
	abbreviation character varying(32) NOT NULL,
	active boolean NOT NULL,
	created_on timestamp NOT NULL,
	description character varying(32) NOT NULL,
	CONSTRAINT job_status_pkey PRIMARY KEY (job_status_id)

);
-- ddl-end --
ALTER TABLE reporting.job_status OWNER TO report_server_app;
-- ddl-end --

-- object: email | type: COLUMN --
-- ALTER TABLE reporting.role DROP COLUMN IF EXISTS email CASCADE;
ALTER TABLE reporting.role ADD COLUMN email character varying(160);
-- ddl-end --


-- object: time_zone_id | type: COLUMN --
-- ALTER TABLE reporting.role DROP COLUMN IF EXISTS time_zone_id CASCADE;
ALTER TABLE reporting.role ADD COLUMN time_zone_id character varying(80);
-- ddl-end --


-- object: reporting.role_parameter | type: TABLE --
-- DROP TABLE IF EXISTS reporting.role_parameter CASCADE;
CREATE TABLE reporting.role_parameter(
	role_parameter_id uuid NOT NULL DEFAULT uuid_generate_v4(),
	created_on timestamp NOT NULL,
	report_parameter_id uuid NOT NULL,
	role_id uuid NOT NULL,
	CONSTRAINT role_parameter_pkey PRIMARY KEY (role_parameter_id),
	CONSTRAINT uc_roleparameter_role_parameter UNIQUE (role_id,report_parameter_id)

);
-- ddl-end --
ALTER TABLE reporting.role_parameter OWNER TO report_server_app;
-- ddl-end --

-- object: boolean_value | type: COLUMN --
-- ALTER TABLE reporting.role_parameter_value DROP COLUMN IF EXISTS boolean_value CASCADE;
ALTER TABLE reporting.role_parameter_value ADD COLUMN boolean_value boolean;
-- ddl-end --


-- object: date_value | type: COLUMN --
-- ALTER TABLE reporting.role_parameter_value DROP COLUMN IF EXISTS date_value CASCADE;
ALTER TABLE reporting.role_parameter_value ADD COLUMN date_value date;
-- ddl-end --


-- object: datetime_value | type: COLUMN --
-- ALTER TABLE reporting.role_parameter_value DROP COLUMN IF EXISTS datetime_value CASCADE;
ALTER TABLE reporting.role_parameter_value ADD COLUMN datetime_value timestamp;
-- ddl-end --


-- object: float_value | type: COLUMN --
-- ALTER TABLE reporting.role_parameter_value DROP COLUMN IF EXISTS float_value CASCADE;
ALTER TABLE reporting.role_parameter_value ADD COLUMN float_value double precision;
-- ddl-end --


-- object: integer_value | type: COLUMN --
-- ALTER TABLE reporting.role_parameter_value DROP COLUMN IF EXISTS integer_value CASCADE;
ALTER TABLE reporting.role_parameter_value ADD COLUMN integer_value integer;
-- ddl-end --


-- object: time_value | type: COLUMN --
-- ALTER TABLE reporting.role_parameter_value DROP COLUMN IF EXISTS time_value CASCADE;
ALTER TABLE reporting.role_parameter_value ADD COLUMN time_value time;
-- ddl-end --


-- object: role_parameter_id | type: COLUMN --
-- ALTER TABLE reporting.role_parameter_value DROP COLUMN IF EXISTS role_parameter_id CASCADE;
ALTER TABLE reporting.role_parameter_value ADD COLUMN role_parameter_id uuid NOT NULL;
-- ddl-end --


-- object: active | type: COLUMN --
-- ALTER TABLE reporting.subscription DROP COLUMN IF EXISTS active CASCADE;
ALTER TABLE reporting.subscription ADD COLUMN active boolean NOT NULL;
-- ddl-end --


-- object: delivery_cron_schedule | type: COLUMN --
-- ALTER TABLE reporting.subscription DROP COLUMN IF EXISTS delivery_cron_schedule CASCADE;
ALTER TABLE reporting.subscription ADD COLUMN delivery_cron_schedule character varying(80);
-- ddl-end --


-- object: delivery_datetime_run_at | type: COLUMN --
-- ALTER TABLE reporting.subscription DROP COLUMN IF EXISTS delivery_datetime_run_at CASCADE;
ALTER TABLE reporting.subscription ADD COLUMN delivery_datetime_run_at timestamp;
-- ddl-end --


-- object: delivery_time_zone_id | type: COLUMN --
-- ALTER TABLE reporting.subscription DROP COLUMN IF EXISTS delivery_time_zone_id CASCADE;
ALTER TABLE reporting.subscription ADD COLUMN delivery_time_zone_id character varying(80);
-- ddl-end --


-- object: enabled | type: COLUMN --
-- ALTER TABLE reporting.subscription DROP COLUMN IF EXISTS enabled CASCADE;
ALTER TABLE reporting.subscription ADD COLUMN enabled boolean NOT NULL;
-- ddl-end --


-- object: reporting.subscription_parameter | type: TABLE --
-- DROP TABLE IF EXISTS reporting.subscription_parameter CASCADE;
CREATE TABLE reporting.subscription_parameter(
	subscription_parameter_id uuid NOT NULL DEFAULT uuid_generate_v4(),
	created_on timestamp NOT NULL,
	report_parameter_id uuid NOT NULL,
	subscription_id uuid NOT NULL,
	CONSTRAINT subscription_parameter_pkey PRIMARY KEY (subscription_parameter_id),
	CONSTRAINT uc_subscriptionparameter_subscription_parameter UNIQUE (subscription_id,report_parameter_id)

);
-- ddl-end --
ALTER TABLE reporting.subscription_parameter OWNER TO report_server_app;
-- ddl-end --

-- object: boolean_value | type: COLUMN --
-- ALTER TABLE reporting.subscription_parameter_value DROP COLUMN IF EXISTS boolean_value CASCADE;
ALTER TABLE reporting.subscription_parameter_value ADD COLUMN boolean_value boolean;
-- ddl-end --


-- object: date_value | type: COLUMN --
-- ALTER TABLE reporting.subscription_parameter_value DROP COLUMN IF EXISTS date_value CASCADE;
ALTER TABLE reporting.subscription_parameter_value ADD COLUMN date_value date;
-- ddl-end --


-- object: datetime_value | type: COLUMN --
-- ALTER TABLE reporting.subscription_parameter_value DROP COLUMN IF EXISTS datetime_value CASCADE;
ALTER TABLE reporting.subscription_parameter_value ADD COLUMN datetime_value timestamp;
-- ddl-end --


-- object: day_of_week_in_month_number | type: COLUMN --
-- ALTER TABLE reporting.subscription_parameter_value DROP COLUMN IF EXISTS day_of_week_in_month_number CASCADE;
ALTER TABLE reporting.subscription_parameter_value ADD COLUMN day_of_week_in_month_number integer;
-- ddl-end --


-- object: day_of_week_in_month_ordinal | type: COLUMN --
-- ALTER TABLE reporting.subscription_parameter_value DROP COLUMN IF EXISTS day_of_week_in_month_ordinal CASCADE;
ALTER TABLE reporting.subscription_parameter_value ADD COLUMN day_of_week_in_month_ordinal integer;
-- ddl-end --


-- object: days_ago | type: COLUMN --
-- ALTER TABLE reporting.subscription_parameter_value DROP COLUMN IF EXISTS days_ago CASCADE;
ALTER TABLE reporting.subscription_parameter_value ADD COLUMN days_ago integer;
-- ddl-end --


-- object: duration_to_add_days | type: COLUMN --
-- ALTER TABLE reporting.subscription_parameter_value DROP COLUMN IF EXISTS duration_to_add_days CASCADE;
ALTER TABLE reporting.subscription_parameter_value ADD COLUMN duration_to_add_days integer;
-- ddl-end --


-- object: duration_to_add_hours | type: COLUMN --
-- ALTER TABLE reporting.subscription_parameter_value DROP COLUMN IF EXISTS duration_to_add_hours CASCADE;
ALTER TABLE reporting.subscription_parameter_value ADD COLUMN duration_to_add_hours integer;
-- ddl-end --


-- object: duration_to_add_minutes | type: COLUMN --
-- ALTER TABLE reporting.subscription_parameter_value DROP COLUMN IF EXISTS duration_to_add_minutes CASCADE;
ALTER TABLE reporting.subscription_parameter_value ADD COLUMN duration_to_add_minutes integer;
-- ddl-end --


-- object: duration_to_add_months | type: COLUMN --
-- ALTER TABLE reporting.subscription_parameter_value DROP COLUMN IF EXISTS duration_to_add_months CASCADE;
ALTER TABLE reporting.subscription_parameter_value ADD COLUMN duration_to_add_months integer;
-- ddl-end --


-- object: duration_to_add_seconds | type: COLUMN --
-- ALTER TABLE reporting.subscription_parameter_value DROP COLUMN IF EXISTS duration_to_add_seconds CASCADE;
ALTER TABLE reporting.subscription_parameter_value ADD COLUMN duration_to_add_seconds integer;
-- ddl-end --


-- object: duration_to_add_weeks | type: COLUMN --
-- ALTER TABLE reporting.subscription_parameter_value DROP COLUMN IF EXISTS duration_to_add_weeks CASCADE;
ALTER TABLE reporting.subscription_parameter_value ADD COLUMN duration_to_add_weeks integer;
-- ddl-end --


-- object: duration_to_add_years | type: COLUMN --
-- ALTER TABLE reporting.subscription_parameter_value DROP COLUMN IF EXISTS duration_to_add_years CASCADE;
ALTER TABLE reporting.subscription_parameter_value ADD COLUMN duration_to_add_years integer;
-- ddl-end --


-- object: float_value | type: COLUMN --
-- ALTER TABLE reporting.subscription_parameter_value DROP COLUMN IF EXISTS float_value CASCADE;
ALTER TABLE reporting.subscription_parameter_value ADD COLUMN float_value double precision;
-- ddl-end --


-- object: integer_value | type: COLUMN --
-- ALTER TABLE reporting.subscription_parameter_value DROP COLUMN IF EXISTS integer_value CASCADE;
ALTER TABLE reporting.subscription_parameter_value ADD COLUMN integer_value integer;
-- ddl-end --


-- object: months_ago | type: COLUMN --
-- ALTER TABLE reporting.subscription_parameter_value DROP COLUMN IF EXISTS months_ago CASCADE;
ALTER TABLE reporting.subscription_parameter_value ADD COLUMN months_ago integer;
-- ddl-end --


-- object: weeks_ago | type: COLUMN --
-- ALTER TABLE reporting.subscription_parameter_value DROP COLUMN IF EXISTS weeks_ago CASCADE;
ALTER TABLE reporting.subscription_parameter_value ADD COLUMN weeks_ago integer;
-- ddl-end --


-- object: years_ago | type: COLUMN --
-- ALTER TABLE reporting.subscription_parameter_value DROP COLUMN IF EXISTS years_ago CASCADE;
ALTER TABLE reporting.subscription_parameter_value ADD COLUMN years_ago integer;
-- ddl-end --


-- object: subscription_parameter_id | type: COLUMN --
-- ALTER TABLE reporting.subscription_parameter_value DROP COLUMN IF EXISTS subscription_parameter_id CASCADE;
ALTER TABLE reporting.subscription_parameter_value ADD COLUMN subscription_parameter_id uuid NOT NULL;
-- ddl-end --


-- object: fk_job_jobstatus | type: CONSTRAINT --
-- ALTER TABLE reporting.job DROP CONSTRAINT IF EXISTS fk_job_jobstatus CASCADE;
ALTER TABLE reporting.job ADD CONSTRAINT fk_job_jobstatus FOREIGN KEY (job_status_id)
REFERENCES reporting.job_status (job_status_id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_job_reportversion | type: CONSTRAINT --
-- ALTER TABLE reporting.job DROP CONSTRAINT IF EXISTS fk_job_reportversion CASCADE;
ALTER TABLE reporting.job ADD CONSTRAINT fk_job_reportversion FOREIGN KEY (report_version_id)
REFERENCES reporting.report_version (report_version_id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_job_subscription | type: CONSTRAINT --
-- ALTER TABLE reporting.job DROP CONSTRAINT IF EXISTS fk_job_subscription CASCADE;
ALTER TABLE reporting.job ADD CONSTRAINT fk_job_subscription FOREIGN KEY (subscription_id)
REFERENCES reporting.subscription (subscription_id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_jobparametervalue_jobparameter | type: CONSTRAINT --
-- ALTER TABLE reporting.job_parameter_value DROP CONSTRAINT IF EXISTS fk_jobparametervalue_jobparameter CASCADE;
ALTER TABLE reporting.job_parameter_value ADD CONSTRAINT fk_jobparametervalue_jobparameter FOREIGN KEY (job_parameter_id)
REFERENCES reporting.job_parameter (job_parameter_id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_reportparameter_reportversion | type: CONSTRAINT --
-- ALTER TABLE reporting.report_parameter DROP CONSTRAINT IF EXISTS fk_reportparameter_reportversion CASCADE;
ALTER TABLE reporting.report_parameter ADD CONSTRAINT fk_reportparameter_reportversion FOREIGN KEY (report_version_id)
REFERENCES reporting.report_version (report_version_id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_roleparametervalue_roleparameter | type: CONSTRAINT --
-- ALTER TABLE reporting.role_parameter_value DROP CONSTRAINT IF EXISTS fk_roleparametervalue_roleparameter CASCADE;
ALTER TABLE reporting.role_parameter_value ADD CONSTRAINT fk_roleparametervalue_roleparameter FOREIGN KEY (role_parameter_id)
REFERENCES reporting.role_parameter (role_parameter_id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_subscriptionparametervalue_subscriptionparameter | type: CONSTRAINT --
-- ALTER TABLE reporting.subscription_parameter_value DROP CONSTRAINT IF EXISTS fk_subscriptionparametervalue_subscriptionparameter CASCADE;
ALTER TABLE reporting.subscription_parameter_value ADD CONSTRAINT fk_subscriptionparametervalue_subscriptionparameter FOREIGN KEY (subscription_parameter_id)
REFERENCES reporting.subscription_parameter (subscription_parameter_id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_jobparameter_job | type: CONSTRAINT --
-- ALTER TABLE reporting.job_parameter DROP CONSTRAINT IF EXISTS fk_jobparameter_job CASCADE;
ALTER TABLE reporting.job_parameter ADD CONSTRAINT fk_jobparameter_job FOREIGN KEY (job_id)
REFERENCES reporting.job (job_id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_jobparameter_reportparameter | type: CONSTRAINT --
-- ALTER TABLE reporting.job_parameter DROP CONSTRAINT IF EXISTS fk_jobparameter_reportparameter CASCADE;
ALTER TABLE reporting.job_parameter ADD CONSTRAINT fk_jobparameter_reportparameter FOREIGN KEY (report_parameter_id)
REFERENCES reporting.report_parameter (report_parameter_id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_roleparameter_reportparameter | type: CONSTRAINT --
-- ALTER TABLE reporting.role_parameter DROP CONSTRAINT IF EXISTS fk_roleparameter_reportparameter CASCADE;
ALTER TABLE reporting.role_parameter ADD CONSTRAINT fk_roleparameter_reportparameter FOREIGN KEY (report_parameter_id)
REFERENCES reporting.report_parameter (report_parameter_id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_roleparameter_role | type: CONSTRAINT --
-- ALTER TABLE reporting.role_parameter DROP CONSTRAINT IF EXISTS fk_roleparameter_role CASCADE;
ALTER TABLE reporting.role_parameter ADD CONSTRAINT fk_roleparameter_role FOREIGN KEY (role_id)
REFERENCES reporting.role (role_id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_subscriptionparameter_reportparameter | type: CONSTRAINT --
-- ALTER TABLE reporting.subscription_parameter DROP CONSTRAINT IF EXISTS fk_subscriptionparameter_reportparameter CASCADE;
ALTER TABLE reporting.subscription_parameter ADD CONSTRAINT fk_subscriptionparameter_reportparameter FOREIGN KEY (report_parameter_id)
REFERENCES reporting.report_parameter (report_parameter_id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_subscriptionparameter_subscription | type: CONSTRAINT --
-- ALTER TABLE reporting.subscription_parameter DROP CONSTRAINT IF EXISTS fk_subscriptionparameter_subscription CASCADE;
ALTER TABLE reporting.subscription_parameter ADD CONSTRAINT fk_subscriptionparameter_subscription FOREIGN KEY (subscription_id)
REFERENCES reporting.subscription (subscription_id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --



-- [ Changed objects ] --
-- ALTER DATABASE "report_server_db-02-v0.7" RENAME TO report_server_db;
-- ddl-end --
ALTER TABLE reporting.job_parameter_value ALTER COLUMN string_value DROP NOT NULL;
-- ddl-end --
ALTER TABLE reporting.role_parameter_value ALTER COLUMN string_value DROP NOT NULL;
-- ddl-end --
ALTER TABLE reporting.subscription ALTER COLUMN description TYPE character varying(1024);
-- ddl-end --
ALTER TABLE reporting.subscription ALTER COLUMN email TYPE character varying(160);
-- ddl-end --
ALTER TABLE reporting.subscription ALTER COLUMN email DROP NOT NULL;
-- ddl-end --


-- Update the DB content from v2 to v3:

insert into reporting.document_format (document_format_id, name, file_extension, media_type, binary_data, birt_format, active, created_on) values ('bc5169e0-3d36-483c-a7b5-a76766587991', 'Microsoft Word'               , 'doc' , 'application/msword'                                                       , true , 'doc' , true , current_timestamp AT TIME ZONE 'UTC');
insert into reporting.document_format (document_format_id, name, file_extension, media_type, binary_data, birt_format, active, created_on) values ('d0225349-1642-46e3-a949-4ce39795907f', 'Office Open XML Document'     , 'docx', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'  , true , 'docx', true , current_timestamp AT TIME ZONE 'UTC');
insert into reporting.document_format (document_format_id, name, file_extension, media_type, binary_data, birt_format, active, created_on) values ('e1d0b3f2-f639-4521-a055-d5465dce29a2', 'HTML'                         , 'html', 'text/html'                                                                , false, 'html', false, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.document_format (document_format_id, name, file_extension, media_type, binary_data, birt_format, active, created_on) values ('38b73b21-cb66-42cf-932b-1cdf7937525c', 'OpenDocument Presentation'    , 'odp' , 'application/vnd.oasis.opendocument.presentation'                          , true , 'odp' , true , current_timestamp AT TIME ZONE 'UTC');
insert into reporting.document_format (document_format_id, name, file_extension, media_type, binary_data, birt_format, active, created_on) values ('05a4ad8d-6f30-4d6d-83d5-995345a8dc58', 'OpenDocument Spreadsheet'     , 'ods' , 'application/vnd.oasis.opendocument.spreadsheet'                           , true , 'ods' , true , current_timestamp AT TIME ZONE 'UTC');
insert into reporting.document_format (document_format_id, name, file_extension, media_type, binary_data, birt_format, active, created_on) values ('b4f2249d-f52e-47e2-871c-daf35f4ba78e', 'OpenDocument Text'            , 'odt' , 'application/vnd.oasis.opendocument.text'                                  , true , 'odt' , true , current_timestamp AT TIME ZONE 'UTC');
insert into reporting.document_format (document_format_id, name, file_extension, media_type, binary_data, birt_format, active, created_on) values ('30800d77-5fdd-44bc-94a3-1502bd307c1d', 'PDF'                          , 'pdf' , 'application/pdf'                                                          , true , 'pdf' , true , current_timestamp AT TIME ZONE 'UTC');
insert into reporting.document_format (document_format_id, name, file_extension, media_type, binary_data, birt_format, active, created_on) values ('597f34fb-10d8-4408-971a-1b67472ac588', 'PowerPoint'                   , 'ppt' , 'application/vnd.ms-powerpoint'                                            , true , 'ppt' , true , current_timestamp AT TIME ZONE 'UTC');
insert into reporting.document_format (document_format_id, name, file_extension, media_type, binary_data, birt_format, active, created_on) values ('d7ccb194-91c6-4dce-bbfe-6424f079dc07', 'Office Open XML Presentation' , 'pptx', 'application/vnd.openxmlformats-officedocument.presentationml.presentation', true , 'pptx', true , current_timestamp AT TIME ZONE 'UTC');
insert into reporting.document_format (document_format_id, name, file_extension, media_type, binary_data, birt_format, active, created_on) values ('25762ba8-1688-4100-b323-b9e74eba396c', 'Microsoft Excel'              , 'xls' , 'application/vnd.ms-excel'                                                 , true , 'xls' , true , current_timestamp AT TIME ZONE 'UTC');
insert into reporting.document_format (document_format_id, name, file_extension, media_type, binary_data, birt_format, active, created_on) values ('c78ac922-2f37-4855-83ae-b708d453b005', 'Office Open XML Workbook'     , 'xlsx', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'        , true , 'xlsx', true , current_timestamp AT TIME ZONE 'UTC');

insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('08de9764-735f-4c82-bbe9-3981b29cc133', 'Queued'   , 'QUEUED'   , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('a613aae2-836a-4b03-a75d-cfb8303eaad5', 'Running'  , 'RUNNING'  , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('f378fc09-35e4-4096-b1d1-2db14756b098', 'Completed', 'COMPLETED', true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('2a9cd697-af00-45bc-aa6a-053284b9d9e4', 'Failed'   , 'FAILED'   , true, current_timestamp AT TIME ZONE 'UTC');

UPDATE reporting.role SET (email, time_zone_id) = ('user1@somedomain.com', 'CET'           ) WHERE role_id='29fe8a1f-7826-4df0-8bfd-151b54198655';
UPDATE reporting.role SET (email, time_zone_id) = ('user2@somedomain.com', 'CET'           ) WHERE role_id='fa06393a-d341-4bf6-b047-1a8c6a383483';
UPDATE reporting.role SET (email, time_zone_id) = ('user3@somedomain.com', 'Canada/Pacific') WHERE role_id='b85fd129-17d9-40e7-ac11-7541040f8627';
UPDATE reporting.role SET (email, time_zone_id) = ('user4@somedomain.com', 'GMT'           ) WHERE role_id='46e477dc-085f-4714-a24f-742428579fcc';


-- Update global configuration record for "DB_VERSION" to reflect the new version.
UPDATE reporting.configuration SET (integer_value, string_value) = (3, '3') WHERE param_name='DB_VERSION' AND role_id IS NULL;

--ROLLBACK;
COMMIT;