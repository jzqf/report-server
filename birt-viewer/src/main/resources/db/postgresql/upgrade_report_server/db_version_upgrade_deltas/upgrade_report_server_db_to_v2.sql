BEGIN TRANSACTION;

-- Update the DB schema from v1 to v2:

-- Database diff generated with pgModeler (PostgreSQL Database Modeler).
-- pgModeler  version: 0.8.1
-- PostgreSQL version: 9.4

-- [ Diff summary ]
-- Dropped objects: 6
-- Created objects: 19
-- Changed objects: 3
-- Truncated tables: 0

SET search_path=public,pg_catalog,reporting;
-- ddl-end --


-- [ Dropped objects ] --
ALTER TABLE reporting.report_parameter DROP CONSTRAINT IF EXISTS fk_reportparameter_widget CASCADE;
-- ddl-end --
ALTER TABLE reporting.report_parameter DROP CONSTRAINT IF EXISTS fk_reportparameter_parametertype CASCADE;
-- ddl-end --
DROP TABLE IF EXISTS reporting.widget CASCADE;
-- ddl-end --
DROP TABLE IF EXISTS reporting.parameter_type CASCADE;
-- ddl-end --
DROP SEQUENCE IF EXISTS reporting.job_job_id_seq1 CASCADE;
-- ddl-end --
DROP SEQUENCE IF EXISTS reporting.hibernate_sequence CASCADE;
-- ddl-end --
ALTER TABLE reporting.report_parameter DROP COLUMN IF EXISTS description CASCADE;
-- ddl-end --
ALTER TABLE reporting.report_parameter DROP COLUMN IF EXISTS parameter_type_id CASCADE;
-- ddl-end --
ALTER TABLE reporting.report_parameter DROP COLUMN IF EXISTS widget_id CASCADE;
-- ddl-end --


-- [ Created objects ] --
-- object: reporting.parameter_group | type: TABLE --
-- DROP TABLE IF EXISTS reporting.parameter_group CASCADE;
CREATE TABLE reporting.parameter_group(
	parameter_group_id uuid NOT NULL DEFAULT uuid_generate_v4(),
	created_on timestamp NOT NULL,
	group_type integer NOT NULL,
	name character varying(80) NOT NULL,
	prompt_text character varying(132) NOT NULL,
	CONSTRAINT parameter_group_pkey PRIMARY KEY (parameter_group_id)

);
-- ddl-end --
ALTER TABLE reporting.parameter_group OWNER TO report_server_app;
-- ddl-end --

-- object: alignment | type: COLUMN --
-- ALTER TABLE reporting.report_parameter DROP COLUMN IF EXISTS alignment CASCADE;
ALTER TABLE reporting.report_parameter ADD COLUMN alignment integer NOT NULL;
-- ddl-end --


-- object: allow_new_values | type: COLUMN --
-- ALTER TABLE reporting.report_parameter DROP COLUMN IF EXISTS allow_new_values CASCADE;
ALTER TABLE reporting.report_parameter ADD COLUMN allow_new_values boolean NOT NULL;
-- ddl-end --


-- object: auto_suggest_threshold | type: COLUMN --
-- ALTER TABLE reporting.report_parameter DROP COLUMN IF EXISTS auto_suggest_threshold CASCADE;
ALTER TABLE reporting.report_parameter ADD COLUMN auto_suggest_threshold integer NOT NULL;
-- ddl-end --


-- object: control_type | type: COLUMN --
-- ALTER TABLE reporting.report_parameter DROP COLUMN IF EXISTS control_type CASCADE;
ALTER TABLE reporting.report_parameter ADD COLUMN control_type integer NOT NULL;
-- ddl-end --


-- object: data_type | type: COLUMN --
-- ALTER TABLE reporting.report_parameter DROP COLUMN IF EXISTS data_type CASCADE;
ALTER TABLE reporting.report_parameter ADD COLUMN data_type integer NOT NULL;
-- ddl-end --


-- object: default_value | type: COLUMN --
-- ALTER TABLE reporting.report_parameter DROP COLUMN IF EXISTS default_value CASCADE;
ALTER TABLE reporting.report_parameter ADD COLUMN default_value character varying(80);
-- ddl-end --


-- object: display_format | type: COLUMN --
-- ALTER TABLE reporting.report_parameter DROP COLUMN IF EXISTS display_format CASCADE;
ALTER TABLE reporting.report_parameter ADD COLUMN display_format character varying(132);
-- ddl-end --


-- object: display_in_fixed_order | type: COLUMN --
-- ALTER TABLE reporting.report_parameter DROP COLUMN IF EXISTS display_in_fixed_order CASCADE;
ALTER TABLE reporting.report_parameter ADD COLUMN display_in_fixed_order boolean NOT NULL;
-- ddl-end --


-- object: display_name | type: COLUMN --
-- ALTER TABLE reporting.report_parameter DROP COLUMN IF EXISTS display_name CASCADE;
ALTER TABLE reporting.report_parameter ADD COLUMN display_name character varying(80);
-- ddl-end --


-- object: help_text | type: COLUMN --
-- ALTER TABLE reporting.report_parameter DROP COLUMN IF EXISTS help_text CASCADE;
ALTER TABLE reporting.report_parameter ADD COLUMN help_text character varying(1024);
-- ddl-end --


-- object: hidden | type: COLUMN --
-- ALTER TABLE reporting.report_parameter DROP COLUMN IF EXISTS hidden CASCADE;
ALTER TABLE reporting.report_parameter ADD COLUMN hidden boolean NOT NULL;
-- ddl-end --


-- object: parameter_type | type: COLUMN --
-- ALTER TABLE reporting.report_parameter DROP COLUMN IF EXISTS parameter_type CASCADE;
ALTER TABLE reporting.report_parameter ADD COLUMN parameter_type integer NOT NULL;
-- ddl-end --


-- object: prompt_text | type: COLUMN --
-- ALTER TABLE reporting.report_parameter DROP COLUMN IF EXISTS prompt_text CASCADE;
ALTER TABLE reporting.report_parameter ADD COLUMN prompt_text character varying(132) NOT NULL;
-- ddl-end --


-- object: selection_list_type | type: COLUMN --
-- ALTER TABLE reporting.report_parameter DROP COLUMN IF EXISTS selection_list_type CASCADE;
ALTER TABLE reporting.report_parameter ADD COLUMN selection_list_type integer NOT NULL;
-- ddl-end --


-- object: value_concealed | type: COLUMN --
-- ALTER TABLE reporting.report_parameter DROP COLUMN IF EXISTS value_concealed CASCADE;
ALTER TABLE reporting.report_parameter ADD COLUMN value_concealed boolean NOT NULL;
-- ddl-end --


-- object: value_expr | type: COLUMN --
-- ALTER TABLE reporting.report_parameter DROP COLUMN IF EXISTS value_expr CASCADE;
ALTER TABLE reporting.report_parameter ADD COLUMN value_expr character varying(132);
-- ddl-end --


-- object: parameter_group_id | type: COLUMN --
-- ALTER TABLE reporting.report_parameter DROP COLUMN IF EXISTS parameter_group_id CASCADE;
ALTER TABLE reporting.report_parameter ADD COLUMN parameter_group_id uuid;
-- ddl-end --


-- object: reporting.selection_list_value | type: TABLE --
-- DROP TABLE IF EXISTS reporting.selection_list_value CASCADE;
CREATE TABLE reporting.selection_list_value(
	selection_list_value_id uuid NOT NULL DEFAULT uuid_generate_v4(),
	created_on timestamp NOT NULL,
	order_index integer NOT NULL,
	value_assigned character varying(132) NOT NULL,
	value_displayed character varying(132) NOT NULL,
	report_parameter_id uuid NOT NULL,
	CONSTRAINT selection_list_value_pkey PRIMARY KEY (selection_list_value_id)

);
-- ddl-end --
ALTER TABLE reporting.selection_list_value OWNER TO report_server_app;
-- ddl-end --

-- object: fk_reportparameter_parametergroup | type: CONSTRAINT --
-- ALTER TABLE reporting.report_parameter DROP CONSTRAINT IF EXISTS fk_reportparameter_parametergroup CASCADE;
ALTER TABLE reporting.report_parameter ADD CONSTRAINT fk_reportparameter_parametergroup FOREIGN KEY (parameter_group_id)
REFERENCES reporting.parameter_group (parameter_group_id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_selectionlistvalue_reportparameter | type: CONSTRAINT --
-- ALTER TABLE reporting.selection_list_value DROP CONSTRAINT IF EXISTS fk_selectionlistvalue_reportparameter CASCADE;
ALTER TABLE reporting.selection_list_value ADD CONSTRAINT fk_selectionlistvalue_reportparameter FOREIGN KEY (report_parameter_id)
REFERENCES reporting.report_parameter (report_parameter_id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --



-- [ Changed objects ] --
-- ALTER DATABASE "report_server_db-01-v0.6" RENAME TO report_server_db;
-- ddl-end --
ALTER TABLE reporting.job ALTER COLUMN job_id SET DEFAULT nextval('job_job_id_seq'::regclass);
-- ddl-end --
ALTER TABLE reporting.report_parameter ALTER COLUMN name TYPE character varying(80);
-- ddl-end --


-- Update the DB content from v1 to v2:


-- Update global configuration record for "DB_VERSION" to reflect the new version.
UPDATE configuration SET (integer_value, string_value) = (2, '2') WHERE param_name='DB_VERSION' AND role_id IS NULL;

COMMIT;