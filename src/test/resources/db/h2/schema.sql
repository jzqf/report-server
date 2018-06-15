-- H2:

-- These commands are needed when using an embedded H2 database that is used for
-- testing, even if a new database is created for each run. It seems that this
-- script is run by Spring before individual test methods *without* first
-- deleting the H2 database. Hence, this script must be able to execute for an
-- EXISTING database that already has these objects created. This behaviour only
-- started after I added Spring Boot to this application. Therefore, it is 
-- necessary to drop all tables before creating them again.

CREATE SCHEMA IF NOT EXISTS reporting;

DROP TABLE IF EXISTS reporting.configuration CASCADE;
DROP TABLE IF EXISTS reporting.role_report CASCADE;
DROP TABLE IF EXISTS reporting.role_role CASCADE;
DROP TABLE IF EXISTS reporting.role_authority CASCADE;
--
DROP TABLE IF EXISTS reporting.authority CASCADE;
---
DROP TABLE IF EXISTS reporting.asset CASCADE;
DROP TABLE IF EXISTS reporting.asset_tree CASCADE;
DROP TABLE IF EXISTS reporting.asset_type CASCADE;
--
DROP TABLE IF EXISTS reporting.role_parameter_value CASCADE;
DROP TABLE IF EXISTS reporting.role_parameter CASCADE;
--
DROP TABLE IF EXISTS reporting.job_parameter_value CASCADE;
DROP TABLE IF EXISTS reporting.job_parameter CASCADE;
DROP TABLE IF EXISTS reporting.job CASCADE;
--
DROP TABLE IF EXISTS reporting.subscription_parameter_value CASCADE;
DROP TABLE IF EXISTS reporting.subscription_parameter CASCADE;
DROP TABLE IF EXISTS reporting.subscription CASCADE;
--
DROP TABLE IF EXISTS reporting.selection_list_value CASCADE;
DROP TABLE IF EXISTS reporting.report_parameter CASCADE;
--
DROP TABLE IF EXISTS reporting.report_version CASCADE;
DROP TABLE IF EXISTS reporting.report CASCADE;
--
DROP TABLE IF EXISTS reporting.role CASCADE;
--
DROP TABLE IF EXISTS reporting.document_format CASCADE;
DROP TABLE IF EXISTS reporting.report_category CASCADE;
DROP TABLE IF EXISTS reporting.parameter_group CASCADE;
DROP TABLE IF EXISTS reporting.job_status CASCADE;
--
DROP TABLE IF EXISTS reporting.document CASCADE;
--------------------------------------------------------------------------------

--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.5
-- Dumped by pg_dump version 9.5.5

--
-- Name: asset; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE reporting.asset (
    asset_id uuid NOT NULL,
    active boolean NOT NULL,
    created_on timestamp NOT NULL,
    filename character varying(256) NOT NULL,
    asset_tree_id uuid NOT NULL,
    asset_type_id uuid NOT NULL,
    document_id uuid NOT NULL
);




--
-- Name: asset_tree; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE reporting.asset_tree (
    asset_tree_id uuid NOT NULL,
    abbreviation character varying(32) NOT NULL,
    active boolean NOT NULL,
    created_on timestamp NOT NULL,
    directory character varying(256) NOT NULL,
    name character varying(32) NOT NULL
);




--
-- Name: asset_type; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE reporting.asset_type (
    asset_type_id uuid NOT NULL,
    abbreviation character varying(32) NOT NULL,
    active boolean NOT NULL,
    created_on timestamp NOT NULL,
    directory character varying(256) NOT NULL,
    name character varying(32) NOT NULL
);




--
-- Name: authority; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE reporting.authority (
    authority_id uuid NOT NULL,
    active boolean NOT NULL,
    created_on timestamp NOT NULL,
    name character varying(50) NOT NULL
);




--
-- Name: configuration; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE reporting.configuration (
    configuration_id uuid NOT NULL,
    boolean_value boolean,
    bytea_value bytea,
    created_on timestamp NOT NULL,
    date_value date,
    datetime_value timestamp,
    double_value double precision,
    float_value real,
    integer_value integer,
    long_value bigint,
    param_name character varying(64) NOT NULL,
    param_type character varying(16) NOT NULL,
    string_value character varying(1000),
    text_value text,
    time_value time,
    role_id uuid
);




--
-- Name: document; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE reporting.document (
    document_id uuid NOT NULL,
    content bytea,
    created_on timestamp NOT NULL
);




--
-- Name: document_format; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE reporting.document_format (
    document_format_id uuid NOT NULL,
    active boolean NOT NULL,
    binary_data boolean NOT NULL,
    birt_format character varying(12) NOT NULL,
    created_on timestamp NOT NULL,
    file_extension character varying(12) NOT NULL,
    internet_media_type character varying(100) NOT NULL,
    name character varying(32) NOT NULL
);




--
-- Name: job; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE reporting.job (
    job_id identity NOT NULL,
    created_on timestamp NOT NULL,
    document text,
    email_address character varying(160),
    encoded boolean,
    file_name character varying(128),
    job_status_remarks text,
    job_status_set_at timestamp NOT NULL,
    report_emailed_at timestamp,
    report_ran_at timestamp,
    url character varying(1024),
    document_format_id uuid NOT NULL,
    job_status_id uuid NOT NULL,
    report_version_id uuid NOT NULL,
    role_id uuid NOT NULL,
    subscription_id uuid
);


--
-- Name: job_parameter; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE reporting.job_parameter (
    job_parameter_id identity NOT NULL,
    created_on timestamp NOT NULL,
    job_id bigint NOT NULL,
    report_parameter_id uuid NOT NULL
);


--
-- Name: job_parameter_value; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE reporting.job_parameter_value (
    job_parameter_value_id identity NOT NULL,
    boolean_value boolean,
    created_on timestamp NOT NULL,
    date_value date,
    datetime_value timestamp,
    float_value double precision,
    integer_value integer,
    string_value character varying(80),
    time_value time,
    job_parameter_id bigint NOT NULL
);


--
-- Name: job_status; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE reporting.job_status (
    job_status_id uuid NOT NULL,
    abbreviation character varying(32) NOT NULL,
    active boolean NOT NULL,
    created_on timestamp NOT NULL,
    description character varying(32) NOT NULL
);




--
-- Name: parameter_group; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE reporting.parameter_group (
    parameter_group_id uuid NOT NULL,
    created_on timestamp NOT NULL,
    group_type integer NOT NULL,
    name character varying(80) NOT NULL,
    prompt_text character varying(132) NOT NULL
);




--
-- Name: report; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE reporting.report (
    report_id uuid NOT NULL,
    active boolean NOT NULL,
    created_on timestamp NOT NULL,
    name character varying(80) NOT NULL,
    number integer NOT NULL,
    sort_order integer NOT NULL,
    report_category_id uuid NOT NULL
);




--
-- Name: report_category; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE reporting.report_category (
    report_category_id uuid NOT NULL,
    abbreviation character varying(32) NOT NULL,
    active boolean NOT NULL,
    created_on timestamp NOT NULL,
    description character varying(32) NOT NULL
);




--
-- Name: report_parameter; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE reporting.report_parameter (
    report_parameter_id uuid NOT NULL,
    alignment integer NOT NULL,
    allow_new_values boolean NOT NULL,
    auto_suggest_threshold integer NOT NULL,
    control_type integer NOT NULL,
    created_on timestamp NOT NULL,
    data_type integer NOT NULL,
    default_value character varying(80),
    display_format character varying(132),
    display_in_fixed_order boolean NOT NULL,
    display_name character varying(80),
    help_text character varying(1024),
    hidden boolean NOT NULL,
    multivalued boolean NOT NULL,
    name character varying(80) NOT NULL,
    order_index integer NOT NULL,
    parameter_type integer NOT NULL,
    prompt_text character varying(132) NOT NULL,
    required boolean NOT NULL,
    selection_list_type integer NOT NULL,
    value_concealed boolean NOT NULL,
    value_expr character varying(132),
    parameter_group_id uuid,
    report_version_id uuid NOT NULL
);




--
-- Name: report_version; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE reporting.report_version (
    report_version_id uuid NOT NULL,
    active boolean NOT NULL,
    created_on timestamp NOT NULL,
    file_name character varying(80) NOT NULL,
    rptdesign text NOT NULL,
    version_code integer NOT NULL,
    version_name character varying(16) NOT NULL,
    report_id uuid NOT NULL
);




--
-- Name: role; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE reporting.role (
    role_id uuid NOT NULL,
    active boolean NOT NULL,
    created_on timestamp NOT NULL,
    email_address character varying(160),
    enabled boolean NOT NULL,
    encoded_password character varying(64),
    full_name character varying(32),
    login_role boolean NOT NULL,
    time_zone_id character varying(80),
    username character varying(32) NOT NULL
);




--
-- Name: role_authority; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE reporting.role_authority (
    role_authority_id uuid NOT NULL,
    created_on timestamp NOT NULL,
    authority_id uuid NOT NULL,
    role_id uuid NOT NULL
);




--
-- Name: role_parameter; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE reporting.role_parameter (
    role_parameter_id uuid NOT NULL,
    created_on timestamp NOT NULL,
    report_parameter_id uuid NOT NULL,
    role_id uuid NOT NULL
);




--
-- Name: role_parameter_value; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE reporting.role_parameter_value (
    role_parameter_value_id uuid NOT NULL,
    boolean_value boolean,
    created_on timestamp NOT NULL,
    date_value date,
    datetime_value timestamp,
    float_value double precision,
    integer_value integer,
    string_value character varying(80),
    time_value time,
    role_parameter_id uuid NOT NULL
);




--
-- Name: role_report; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE reporting.role_report (
    role_report_id uuid NOT NULL,
    created_on timestamp NOT NULL,
    report_id uuid NOT NULL,
    role_id uuid NOT NULL
);




--
-- Name: role_role; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE reporting.role_role (
    role_role_id uuid NOT NULL,
    created_on timestamp NOT NULL,
    child_role_id uuid NOT NULL,
    parent_role_id uuid NOT NULL
);




--
-- Name: selection_list_value; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE reporting.selection_list_value (
    selection_list_value_id uuid NOT NULL,
    created_on timestamp NOT NULL,
    order_index integer NOT NULL,
    value_assigned character varying(132) NOT NULL,
    value_displayed character varying(132) NOT NULL,
    report_parameter_id uuid NOT NULL
);




--
-- Name: subscription; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE reporting.subscription (
    subscription_id uuid NOT NULL,
    active boolean NOT NULL,
    created_on timestamp NOT NULL,
    custom_report_parameter_1_name character varying(64),
    custom_report_parameter_1_value character varying(64),
    custom_report_parameter_2_name character varying(64),
    custom_report_parameter_2_value character varying(64),
    custom_report_parameter_3_name character varying(64),
    custom_report_parameter_3_value character varying(64),
    custom_report_parameter_4_name character varying(64),
    custom_report_parameter_4_value character varying(64),
    custom_report_parameter_5_name character varying(64),
    custom_report_parameter_5_value character varying(64),
    custom_report_parameter_6_name character varying(64),
    custom_report_parameter_6_value character varying(64),
    custom_report_parameter_7_name character varying(64),
    custom_report_parameter_7_value character varying(64),
    custom_report_parameter_8_name character varying(64),
    custom_report_parameter_8_value character varying(64),
    delivery_cron_schedule character varying(80),
    delivery_datetime_run_at timestamp,
    delivery_time_zone_id character varying(80),
    description character varying(1024),
    email_address character varying(160),
    enabled boolean NOT NULL,
    document_format_id uuid NOT NULL,
    report_version_id uuid NOT NULL,
    role_id uuid NOT NULL
);




--
-- Name: subscription_parameter; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE reporting.subscription_parameter (
    subscription_parameter_id uuid NOT NULL,
    created_on timestamp NOT NULL,
    report_parameter_id uuid NOT NULL,
    subscription_id uuid NOT NULL
);




--
-- Name: subscription_parameter_value; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE reporting.subscription_parameter_value (
    subscription_parameter_value_id uuid NOT NULL,
    boolean_value boolean,
    created_on timestamp NOT NULL,
    date_value date,
    datetime_value timestamp,
    day_of_month_number integer,
    day_of_week_in_month_number integer,
    day_of_week_in_month_ordinal integer,
    day_of_week_number integer,
    days_ago integer,
    duration_subtract_one_day_for_dates boolean NOT NULL,
    duration_to_add_days integer,
    duration_to_add_hours integer,
    duration_to_add_minutes integer,
    duration_to_add_months integer,
    duration_to_add_seconds integer,
    duration_to_add_weeks integer,
    duration_to_add_years integer,
    float_value double precision,
    integer_value integer,
    month_number integer,
    months_ago integer,
    string_value character varying(80),
    time_value time,
    weeks_ago integer,
    year_number integer,
    years_ago integer,
    subscription_parameter_id uuid NOT NULL
);


--
-- Name: asset_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.asset
    ADD CONSTRAINT asset_pkey PRIMARY KEY (asset_id);


--
-- Name: asset_tree_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.asset_tree
    ADD CONSTRAINT asset_tree_pkey PRIMARY KEY (asset_tree_id);


--
-- Name: asset_type_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.asset_type
    ADD CONSTRAINT asset_type_pkey PRIMARY KEY (asset_type_id);


--
-- Name: authority_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.authority
    ADD CONSTRAINT authority_pkey PRIMARY KEY (authority_id);


--
-- Name: configuration_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.configuration
    ADD CONSTRAINT configuration_pkey PRIMARY KEY (configuration_id);


--
-- Name: document_format_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.document_format
    ADD CONSTRAINT document_format_pkey PRIMARY KEY (document_format_id);


--
-- Name: document_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.document
    ADD CONSTRAINT document_pkey PRIMARY KEY (document_id);


--
-- Name: job_status_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.job_status
    ADD CONSTRAINT job_status_pkey PRIMARY KEY (job_status_id);


--
-- Name: parameter_group_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.parameter_group
    ADD CONSTRAINT parameter_group_pkey PRIMARY KEY (parameter_group_id);


--
-- Name: report_category_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.report_category
    ADD CONSTRAINT report_category_pkey PRIMARY KEY (report_category_id);


--
-- Name: report_parameter_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.report_parameter
    ADD CONSTRAINT report_parameter_pkey PRIMARY KEY (report_parameter_id);


--
-- Name: report_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.report
    ADD CONSTRAINT report_pkey PRIMARY KEY (report_id);


--
-- Name: report_version_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.report_version
    ADD CONSTRAINT report_version_pkey PRIMARY KEY (report_version_id);


--
-- Name: role_authority_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.role_authority
    ADD CONSTRAINT role_authority_pkey PRIMARY KEY (role_authority_id);


--
-- Name: role_parameter_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.role_parameter
    ADD CONSTRAINT role_parameter_pkey PRIMARY KEY (role_parameter_id);


--
-- Name: role_parameter_value_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.role_parameter_value
    ADD CONSTRAINT role_parameter_value_pkey PRIMARY KEY (role_parameter_value_id);


--
-- Name: role_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.role
    ADD CONSTRAINT role_pkey PRIMARY KEY (role_id);


--
-- Name: role_report_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.role_report
    ADD CONSTRAINT role_report_pkey PRIMARY KEY (role_report_id);


--
-- Name: role_role_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.role_role
    ADD CONSTRAINT role_role_pkey PRIMARY KEY (role_role_id);


--
-- Name: selection_list_value_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.selection_list_value
    ADD CONSTRAINT selection_list_value_pkey PRIMARY KEY (selection_list_value_id);


--
-- Name: subscription_parameter_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.subscription_parameter
    ADD CONSTRAINT subscription_parameter_pkey PRIMARY KEY (subscription_parameter_id);


--
-- Name: subscription_parameter_value_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.subscription_parameter_value
    ADD CONSTRAINT subscription_parameter_value_pkey PRIMARY KEY (subscription_parameter_value_id);


--
-- Name: subscription_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.subscription
    ADD CONSTRAINT subscription_pkey PRIMARY KEY (subscription_id);


--
-- Name: uc_authority_name; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.authority
    ADD CONSTRAINT uc_authority_name UNIQUE (name);


--
-- Name: uc_configuration_paramname_role; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.configuration
    ADD CONSTRAINT uc_configuration_paramname_role UNIQUE (param_name, role_id);


--
-- Name: uc_jobparameter_job_parameter; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.job_parameter
    ADD CONSTRAINT uc_jobparameter_job_parameter UNIQUE (job_id, report_parameter_id);


--
-- Name: uc_reportparameter_reportversion_orderindex; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.report_parameter
    ADD CONSTRAINT uc_reportparameter_reportversion_orderindex UNIQUE (report_version_id, order_index);


--
-- Name: uc_reportversion_filename; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.report_version
    ADD CONSTRAINT uc_reportversion_filename UNIQUE (file_name);


--
-- Name: uc_reportversion_report_versioncode; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.report_version
    ADD CONSTRAINT uc_reportversion_report_versioncode UNIQUE (report_id, version_code);


--
-- Name: uc_reportversion_report_versionname; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.report_version
    ADD CONSTRAINT uc_reportversion_report_versionname UNIQUE (report_id, version_name);


--
-- Name: uc_role_username; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.role
    ADD CONSTRAINT uc_role_username UNIQUE (username);


--
-- Name: uc_roleauthority_role_authority; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.role_authority
    ADD CONSTRAINT uc_roleauthority_role_authority UNIQUE (role_id, authority_id);


--
-- Name: uc_roleparameter_role_parameter; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.role_parameter
    ADD CONSTRAINT uc_roleparameter_role_parameter UNIQUE (role_id, report_parameter_id);


--
-- Name: uc_rolereport_role_report; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.role_report
    ADD CONSTRAINT uc_rolereport_role_report UNIQUE (role_id, report_id);


--
-- Name: uc_rolerole_parent_child; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.role_role
    ADD CONSTRAINT uc_rolerole_parent_child UNIQUE (parent_role_id, child_role_id);


--
-- Name: uc_subscriptionparameter_subscription_parameter; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.subscription_parameter
    ADD CONSTRAINT uc_subscriptionparameter_subscription_parameter UNIQUE (subscription_id, report_parameter_id);


--
-- Name: uq_asset_name_tree_type; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.asset
    ADD CONSTRAINT uq_asset_name_tree_type UNIQUE (filename, asset_tree_id, asset_type_id);


--
-- Name: uq_assettree_abbreviation; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.asset_tree
    ADD CONSTRAINT uq_assettree_abbreviation UNIQUE (abbreviation);


--
-- Name: uq_assettree_directory; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.asset_tree
    ADD CONSTRAINT uq_assettree_directory UNIQUE (directory);


--
-- Name: uq_assettree_name; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.asset_tree
    ADD CONSTRAINT uq_assettree_name UNIQUE (name);


--
-- Name: uq_assettype_abbreviation; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.asset_type
    ADD CONSTRAINT uq_assettype_abbreviation UNIQUE (abbreviation);


--
-- Name: uq_assettype_directory; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.asset_type
    ADD CONSTRAINT uq_assettype_directory UNIQUE (directory);


--
-- Name: uq_assettype_name; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.asset_type
    ADD CONSTRAINT uq_assettype_name UNIQUE (name);


--
-- Name: fk_asset_assettree; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.asset
    ADD CONSTRAINT fk_asset_assettree FOREIGN KEY (asset_tree_id) REFERENCES asset_tree(asset_tree_id);


--
-- Name: fk_asset_assettype; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.asset
    ADD CONSTRAINT fk_asset_assettype FOREIGN KEY (asset_type_id) REFERENCES asset_type(asset_type_id);


--
-- Name: fk_asset_document; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.asset
    ADD CONSTRAINT fk_asset_document FOREIGN KEY (document_id) REFERENCES document(document_id);


--
-- Name: fk_configuration_role; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.configuration
    ADD CONSTRAINT fk_configuration_role FOREIGN KEY (role_id) REFERENCES role(role_id);


--
-- Name: fk_job_documentformat; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.job
    ADD CONSTRAINT fk_job_documentformat FOREIGN KEY (document_format_id) REFERENCES document_format(document_format_id);


--
-- Name: fk_job_jobstatus; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.job
    ADD CONSTRAINT fk_job_jobstatus FOREIGN KEY (job_status_id) REFERENCES job_status(job_status_id);


--
-- Name: fk_job_reportversion; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.job
    ADD CONSTRAINT fk_job_reportversion FOREIGN KEY (report_version_id) REFERENCES report_version(report_version_id);


--
-- Name: fk_job_role; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.job
    ADD CONSTRAINT fk_job_role FOREIGN KEY (role_id) REFERENCES role(role_id);


--
-- Name: fk_job_subscription; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.job
    ADD CONSTRAINT fk_job_subscription FOREIGN KEY (subscription_id) REFERENCES subscription(subscription_id);


--
-- Name: fk_jobparameter_job; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.job_parameter
    ADD CONSTRAINT fk_jobparameter_job FOREIGN KEY (job_id) REFERENCES job(job_id);


--
-- Name: fk_jobparameter_reportparameter; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.job_parameter
    ADD CONSTRAINT fk_jobparameter_reportparameter FOREIGN KEY (report_parameter_id) REFERENCES report_parameter(report_parameter_id);


--
-- Name: fk_jobparametervalue_jobparameter; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.job_parameter_value
    ADD CONSTRAINT fk_jobparametervalue_jobparameter FOREIGN KEY (job_parameter_id) REFERENCES job_parameter(job_parameter_id);


--
-- Name: fk_report_reportcategory; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.report
    ADD CONSTRAINT fk_report_reportcategory FOREIGN KEY (report_category_id) REFERENCES report_category(report_category_id);


--
-- Name: fk_reportparameter_parametergroup; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.report_parameter
    ADD CONSTRAINT fk_reportparameter_parametergroup FOREIGN KEY (parameter_group_id) REFERENCES parameter_group(parameter_group_id);


--
-- Name: fk_reportparameter_reportversion; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.report_parameter
    ADD CONSTRAINT fk_reportparameter_reportversion FOREIGN KEY (report_version_id) REFERENCES report_version(report_version_id);


--
-- Name: fk_reportversion_report; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.report_version
    ADD CONSTRAINT fk_reportversion_report FOREIGN KEY (report_id) REFERENCES report(report_id);


--
-- Name: fk_roleauthority_authority; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.role_authority
    ADD CONSTRAINT fk_roleauthority_authority FOREIGN KEY (authority_id) REFERENCES authority(authority_id);


--
-- Name: fk_roleauthority_role; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.role_authority
    ADD CONSTRAINT fk_roleauthority_role FOREIGN KEY (role_id) REFERENCES role(role_id);


--
-- Name: fk_roleparameter_reportparameter; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.role_parameter
    ADD CONSTRAINT fk_roleparameter_reportparameter FOREIGN KEY (report_parameter_id) REFERENCES report_parameter(report_parameter_id);


--
-- Name: fk_roleparameter_role; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.role_parameter
    ADD CONSTRAINT fk_roleparameter_role FOREIGN KEY (role_id) REFERENCES role(role_id);


--
-- Name: fk_roleparametervalue_roleparameter; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.role_parameter_value
    ADD CONSTRAINT fk_roleparametervalue_roleparameter FOREIGN KEY (role_parameter_id) REFERENCES role_parameter(role_parameter_id);


--
-- Name: fk_rolereport_report; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.role_report
    ADD CONSTRAINT fk_rolereport_report FOREIGN KEY (report_id) REFERENCES report(report_id);


--
-- Name: fk_rolereport_role; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.role_report
    ADD CONSTRAINT fk_rolereport_role FOREIGN KEY (role_id) REFERENCES role(role_id);


--
-- Name: fk_rolerole_childrole; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.role_role
    ADD CONSTRAINT fk_rolerole_childrole FOREIGN KEY (child_role_id) REFERENCES role(role_id);


--
-- Name: fk_rolerole_parentrole; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.role_role
    ADD CONSTRAINT fk_rolerole_parentrole FOREIGN KEY (parent_role_id) REFERENCES role(role_id);


--
-- Name: fk_selectionlistvalue_reportparameter; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.selection_list_value
    ADD CONSTRAINT fk_selectionlistvalue_reportparameter FOREIGN KEY (report_parameter_id) REFERENCES report_parameter(report_parameter_id);


--
-- Name: fk_subscription_documentformat; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.subscription
    ADD CONSTRAINT fk_subscription_documentformat FOREIGN KEY (document_format_id) REFERENCES document_format(document_format_id);


--
-- Name: fk_subscription_reportversion; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.subscription
    ADD CONSTRAINT fk_subscription_reportversion FOREIGN KEY (report_version_id) REFERENCES report_version(report_version_id);


--
-- Name: fk_subscription_role; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.subscription
    ADD CONSTRAINT fk_subscription_role FOREIGN KEY (role_id) REFERENCES role(role_id);


--
-- Name: fk_subscriptionparameter_reportparameter; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.subscription_parameter
    ADD CONSTRAINT fk_subscriptionparameter_reportparameter FOREIGN KEY (report_parameter_id) REFERENCES report_parameter(report_parameter_id);


--
-- Name: fk_subscriptionparameter_subscription; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.subscription_parameter
    ADD CONSTRAINT fk_subscriptionparameter_subscription FOREIGN KEY (subscription_id) REFERENCES subscription(subscription_id);


--
-- Name: fk_subscriptionparametervalue_subscriptionparameter; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE reporting.subscription_parameter_value
    ADD CONSTRAINT fk_subscriptionparametervalue_subscriptionparameter FOREIGN KEY (subscription_parameter_id) REFERENCES subscription_parameter(subscription_parameter_id);
