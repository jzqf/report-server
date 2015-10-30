BEGIN TRANSACTION;

CREATE SCHEMA IF NOT EXISTS reporting;

DROP TABLE IF EXISTS configuration CASCADE;
DROP TABLE IF EXISTS role_report CASCADE;
DROP TABLE IF EXISTS role_role CASCADE;
--
DROP TABLE IF EXISTS role_parameter_value CASCADE;
DROP TABLE IF EXISTS role_parameter CASCADE;
--
DROP TABLE IF EXISTS job_parameter_value CASCADE;
DROP TABLE IF EXISTS job_parameter CASCADE;
DROP TABLE IF EXISTS job CASCADE;
--
DROP TABLE IF EXISTS subscription_parameter_value CASCADE;
DROP TABLE IF EXISTS subscription_parameter CASCADE;
DROP TABLE IF EXISTS subscription CASCADE;
--
DROP TABLE IF EXISTS selection_list_value CASCADE;
DROP TABLE IF EXISTS report_parameter CASCADE;
--
DROP TABLE IF EXISTS report_version CASCADE;
DROP TABLE IF EXISTS report CASCADE;
--
DROP TABLE IF EXISTS role CASCADE;
--
DROP TABLE IF EXISTS document_format CASCADE;
DROP TABLE IF EXISTS report_category CASCADE;
DROP TABLE IF EXISTS parameter_group CASCADE;
DROP TABLE IF EXISTS job_status CASCADE;
--------------------------------------------------------------------------------

--
-- PostgreSQL database dump
--

--
-- Name: configuration; Type: TABLE; Schema: reporting; Owner: report_server_app; Tablespace: 
--

CREATE TABLE configuration (
    configuration_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    boolean_value boolean,
    bytea_value bytea,
    created_on timestamp without time zone NOT NULL,
    date_value date,
    datetime_value timestamp without time zone,
    double_value double precision,
    float_value real,
    integer_value integer,
    long_value bigint,
    param_name character varying(64) NOT NULL,
    param_type character varying(16) NOT NULL,
    string_value character varying(1000),
    text_value text,
    time_value time without time zone,
    role_id uuid
);




--
-- Name: document_format; Type: TABLE; Schema: reporting; Owner: report_server_app; Tablespace: 
--

CREATE TABLE document_format (
    document_format_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    active boolean NOT NULL,
    binary_data boolean NOT NULL,
    birt_format character varying(12) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    file_extension character varying(12) NOT NULL,
    internet_media_type character varying(100) NOT NULL,
    name character varying(32) NOT NULL
);




--
-- Name: job; Type: TABLE; Schema: reporting; Owner: report_server_app; Tablespace: 
--

CREATE TABLE job (
    job_id bigint NOT NULL,
    created_on timestamp without time zone NOT NULL,
    document text,
    encoded boolean,
    file_name character varying(128),
    job_status_remarks text,
    url character varying(1024),
    document_format_id uuid NOT NULL,
    job_status_id uuid NOT NULL,
    report_version_id uuid NOT NULL,
    role_id uuid NOT NULL,
    subscription_id uuid
);




--
-- Name: job_job_id_seq; Type: SEQUENCE; Schema: reporting; Owner: report_server_app
--

CREATE SEQUENCE job_job_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;




--
-- Name: job_job_id_seq; Type: SEQUENCE OWNED BY; Schema: reporting; Owner: report_server_app
--

ALTER SEQUENCE job_job_id_seq OWNED BY job.job_id;


--
-- Name: job_parameter; Type: TABLE; Schema: reporting; Owner: report_server_app; Tablespace: 
--

CREATE TABLE job_parameter (
    job_parameter_id bigint NOT NULL,
    created_on timestamp without time zone NOT NULL,
    job_id bigint NOT NULL,
    report_parameter_id uuid NOT NULL
);




--
-- Name: job_parameter_job_parameter_id_seq; Type: SEQUENCE; Schema: reporting; Owner: report_server_app
--

CREATE SEQUENCE job_parameter_job_parameter_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;




--
-- Name: job_parameter_job_parameter_id_seq; Type: SEQUENCE OWNED BY; Schema: reporting; Owner: report_server_app
--

ALTER SEQUENCE job_parameter_job_parameter_id_seq OWNED BY job_parameter.job_parameter_id;


--
-- Name: job_parameter_value; Type: TABLE; Schema: reporting; Owner: report_server_app; Tablespace: 
--

CREATE TABLE job_parameter_value (
    job_parameter_value_id bigint NOT NULL,
    boolean_value boolean,
    created_on timestamp without time zone NOT NULL,
    date_value date,
    datetime_value timestamp without time zone,
    float_value double precision,
    integer_value integer,
    string_value character varying(80),
    time_value time without time zone,
    job_parameter_id bigint NOT NULL
);




--
-- Name: job_parameter_value_job_parameter_value_id_seq; Type: SEQUENCE; Schema: reporting; Owner: report_server_app
--

CREATE SEQUENCE job_parameter_value_job_parameter_value_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;




--
-- Name: job_parameter_value_job_parameter_value_id_seq; Type: SEQUENCE OWNED BY; Schema: reporting; Owner: report_server_app
--

ALTER SEQUENCE job_parameter_value_job_parameter_value_id_seq OWNED BY job_parameter_value.job_parameter_value_id;


--
-- Name: job_status; Type: TABLE; Schema: reporting; Owner: report_server_app; Tablespace: 
--

CREATE TABLE job_status (
    job_status_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    abbreviation character varying(32) NOT NULL,
    active boolean NOT NULL,
    created_on timestamp without time zone NOT NULL,
    description character varying(32) NOT NULL
);




--
-- Name: parameter_group; Type: TABLE; Schema: reporting; Owner: report_server_app; Tablespace: 
--

CREATE TABLE parameter_group (
    parameter_group_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    group_type integer NOT NULL,
    name character varying(80) NOT NULL,
    prompt_text character varying(132) NOT NULL
);




--
-- Name: report; Type: TABLE; Schema: reporting; Owner: report_server_app; Tablespace: 
--

CREATE TABLE report (
    report_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    active boolean NOT NULL,
    created_on timestamp without time zone NOT NULL,
    name character varying(80) NOT NULL,
    number integer NOT NULL,
    sort_order integer NOT NULL,
    report_category_id uuid NOT NULL
);




--
-- Name: report_category; Type: TABLE; Schema: reporting; Owner: report_server_app; Tablespace: 
--

CREATE TABLE report_category (
    report_category_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    abbreviation character varying(32) NOT NULL,
    active boolean NOT NULL,
    created_on timestamp without time zone NOT NULL,
    description character varying(32) NOT NULL
);




--
-- Name: report_parameter; Type: TABLE; Schema: reporting; Owner: report_server_app; Tablespace: 
--

CREATE TABLE report_parameter (
    report_parameter_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    alignment integer NOT NULL,
    allow_new_values boolean NOT NULL,
    auto_suggest_threshold integer NOT NULL,
    control_type integer NOT NULL,
    created_on timestamp without time zone NOT NULL,
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
-- Name: report_version; Type: TABLE; Schema: reporting; Owner: report_server_app; Tablespace: 
--

CREATE TABLE report_version (
    report_version_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    active boolean NOT NULL,
    created_on timestamp without time zone NOT NULL,
    file_name character varying(80) NOT NULL,
    rptdesign text NOT NULL,
    version_code integer NOT NULL,
    version_name character varying(16) NOT NULL,
    report_id uuid NOT NULL
);




--
-- Name: role; Type: TABLE; Schema: reporting; Owner: report_server_app; Tablespace: 
--

CREATE TABLE role (
    role_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    email character varying(160),
    encoded_password character varying(32) NOT NULL,
    full_name character varying(32),
    login_role boolean NOT NULL,
    time_zone_id character varying(80),
    username character varying(32) NOT NULL
);




--
-- Name: role_parameter; Type: TABLE; Schema: reporting; Owner: report_server_app; Tablespace: 
--

CREATE TABLE role_parameter (
    role_parameter_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    report_parameter_id uuid NOT NULL,
    role_id uuid NOT NULL
);




--
-- Name: role_parameter_value; Type: TABLE; Schema: reporting; Owner: report_server_app; Tablespace: 
--

CREATE TABLE role_parameter_value (
    role_parameter_value_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    boolean_value boolean,
    created_on timestamp without time zone NOT NULL,
    date_value date,
    datetime_value timestamp without time zone,
    float_value double precision,
    integer_value integer,
    string_value character varying(80),
    time_value time without time zone,
    role_parameter_id uuid NOT NULL
);




--
-- Name: role_report; Type: TABLE; Schema: reporting; Owner: report_server_app; Tablespace: 
--

CREATE TABLE role_report (
    role_report_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    report_id uuid NOT NULL,
    role_id uuid NOT NULL
);




--
-- Name: role_role; Type: TABLE; Schema: reporting; Owner: report_server_app; Tablespace: 
--

CREATE TABLE role_role (
    role_role_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    child_role_id uuid NOT NULL,
    parent_role_id uuid NOT NULL
);




--
-- Name: selection_list_value; Type: TABLE; Schema: reporting; Owner: report_server_app; Tablespace: 
--

CREATE TABLE selection_list_value (
    selection_list_value_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    order_index integer NOT NULL,
    value_assigned character varying(132) NOT NULL,
    value_displayed character varying(132) NOT NULL,
    report_parameter_id uuid NOT NULL
);




--
-- Name: subscription; Type: TABLE; Schema: reporting; Owner: report_server_app; Tablespace: 
--

CREATE TABLE subscription (
    subscription_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    active boolean NOT NULL,
    created_on timestamp without time zone NOT NULL,
    delivery_cron_schedule character varying(80),
    delivery_datetime_run_at timestamp without time zone,
    delivery_time_zone_id character varying(80),
    description character varying(1024),
    email character varying(160),
    enabled boolean NOT NULL,
    document_format_id uuid NOT NULL,
    report_version_id uuid NOT NULL,
    role_id uuid NOT NULL
);




--
-- Name: subscription_parameter; Type: TABLE; Schema: reporting; Owner: report_server_app; Tablespace: 
--

CREATE TABLE subscription_parameter (
    subscription_parameter_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    report_parameter_id uuid NOT NULL,
    subscription_id uuid NOT NULL
);




--
-- Name: subscription_parameter_value; Type: TABLE; Schema: reporting; Owner: report_server_app; Tablespace: 
--

CREATE TABLE subscription_parameter_value (
    subscription_parameter_value_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    boolean_value boolean,
    created_on timestamp without time zone NOT NULL,
    date_value date,
    datetime_value timestamp without time zone,
    day_of_month_number integer,
    day_of_week_in_month_number integer,
    day_of_week_in_month_ordinal integer,
    day_of_week_number integer,
    days_ago integer,
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
    time_value time without time zone,
    weeks_ago integer,
    year_number integer,
    years_ago integer,
    subscription_parameter_id uuid NOT NULL
);




--
-- Name: job_id; Type: DEFAULT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY job ALTER COLUMN job_id SET DEFAULT nextval('job_job_id_seq'::regclass);


--
-- Name: job_parameter_id; Type: DEFAULT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY job_parameter ALTER COLUMN job_parameter_id SET DEFAULT nextval('job_parameter_job_parameter_id_seq'::regclass);


--
-- Name: job_parameter_value_id; Type: DEFAULT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY job_parameter_value ALTER COLUMN job_parameter_value_id SET DEFAULT nextval('job_parameter_value_job_parameter_value_id_seq'::regclass);



--
-- Name: job_job_id_seq; Type: SEQUENCE SET; Schema: reporting; Owner: report_server_app
--

SELECT pg_catalog.setval('job_job_id_seq', 1, false);


--
-- Name: job_parameter_job_parameter_id_seq; Type: SEQUENCE SET; Schema: reporting; Owner: report_server_app
--

SELECT pg_catalog.setval('job_parameter_job_parameter_id_seq', 1, false);


--
-- Name: job_parameter_value_job_parameter_value_id_seq; Type: SEQUENCE SET; Schema: reporting; Owner: report_server_app
--

SELECT pg_catalog.setval('job_parameter_value_job_parameter_value_id_seq', 1, false);


--
-- Name: configuration_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY configuration
    ADD CONSTRAINT configuration_pkey PRIMARY KEY (configuration_id);


--
-- Name: document_format_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY document_format
    ADD CONSTRAINT document_format_pkey PRIMARY KEY (document_format_id);


--
-- Name: job_parameter_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY job_parameter
    ADD CONSTRAINT job_parameter_pkey PRIMARY KEY (job_parameter_id);


--
-- Name: job_parameter_value_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY job_parameter_value
    ADD CONSTRAINT job_parameter_value_pkey PRIMARY KEY (job_parameter_value_id);


--
-- Name: job_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY job
    ADD CONSTRAINT job_pkey PRIMARY KEY (job_id);


--
-- Name: job_status_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY job_status
    ADD CONSTRAINT job_status_pkey PRIMARY KEY (job_status_id);


--
-- Name: parameter_group_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY parameter_group
    ADD CONSTRAINT parameter_group_pkey PRIMARY KEY (parameter_group_id);


--
-- Name: report_category_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY report_category
    ADD CONSTRAINT report_category_pkey PRIMARY KEY (report_category_id);


--
-- Name: report_parameter_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY report_parameter
    ADD CONSTRAINT report_parameter_pkey PRIMARY KEY (report_parameter_id);


--
-- Name: report_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY report
    ADD CONSTRAINT report_pkey PRIMARY KEY (report_id);


--
-- Name: report_version_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY report_version
    ADD CONSTRAINT report_version_pkey PRIMARY KEY (report_version_id);


--
-- Name: role_parameter_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY role_parameter
    ADD CONSTRAINT role_parameter_pkey PRIMARY KEY (role_parameter_id);


--
-- Name: role_parameter_value_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY role_parameter_value
    ADD CONSTRAINT role_parameter_value_pkey PRIMARY KEY (role_parameter_value_id);


--
-- Name: role_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY role
    ADD CONSTRAINT role_pkey PRIMARY KEY (role_id);


--
-- Name: role_report_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY role_report
    ADD CONSTRAINT role_report_pkey PRIMARY KEY (role_report_id);


--
-- Name: role_role_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY role_role
    ADD CONSTRAINT role_role_pkey PRIMARY KEY (role_role_id);


--
-- Name: selection_list_value_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY selection_list_value
    ADD CONSTRAINT selection_list_value_pkey PRIMARY KEY (selection_list_value_id);


--
-- Name: subscription_parameter_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY subscription_parameter
    ADD CONSTRAINT subscription_parameter_pkey PRIMARY KEY (subscription_parameter_id);


--
-- Name: subscription_parameter_value_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY subscription_parameter_value
    ADD CONSTRAINT subscription_parameter_value_pkey PRIMARY KEY (subscription_parameter_value_id);


--
-- Name: subscription_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY subscription
    ADD CONSTRAINT subscription_pkey PRIMARY KEY (subscription_id);


--
-- Name: uc_configuration_paramname_role; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY configuration
    ADD CONSTRAINT uc_configuration_paramname_role UNIQUE (param_name, role_id);


--
-- Name: uc_jobparameter_job_parameter; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY job_parameter
    ADD CONSTRAINT uc_jobparameter_job_parameter UNIQUE (job_id, report_parameter_id);


--
-- Name: uc_reportparameter_reportversion_orderindex; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY report_parameter
    ADD CONSTRAINT uc_reportparameter_reportversion_orderindex UNIQUE (report_version_id, order_index);


--
-- Name: uc_reportversion_report_versioncode; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY report_version
    ADD CONSTRAINT uc_reportversion_report_versioncode UNIQUE (report_id, version_code);


--
-- Name: uc_reportversion_report_versionname; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY report_version
    ADD CONSTRAINT uc_reportversion_report_versionname UNIQUE (report_id, version_name);


--
-- Name: uc_role_username; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY role
    ADD CONSTRAINT uc_role_username UNIQUE (username);


--
-- Name: uc_roleparameter_role_parameter; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY role_parameter
    ADD CONSTRAINT uc_roleparameter_role_parameter UNIQUE (role_id, report_parameter_id);


--
-- Name: uc_rolereport_role_report; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY role_report
    ADD CONSTRAINT uc_rolereport_role_report UNIQUE (role_id, report_id);


--
-- Name: uc_rolerole_parent_child; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY role_role
    ADD CONSTRAINT uc_rolerole_parent_child UNIQUE (parent_role_id, child_role_id);


--
-- Name: uc_subscriptionparameter_subscription_parameter; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY subscription_parameter
    ADD CONSTRAINT uc_subscriptionparameter_subscription_parameter UNIQUE (subscription_id, report_parameter_id);


--
-- Name: fk_configuration_role; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY configuration
    ADD CONSTRAINT fk_configuration_role FOREIGN KEY (role_id) REFERENCES role(role_id);


--
-- Name: fk_job_documentformat; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY job
    ADD CONSTRAINT fk_job_documentformat FOREIGN KEY (document_format_id) REFERENCES document_format(document_format_id);


--
-- Name: fk_job_jobstatus; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY job
    ADD CONSTRAINT fk_job_jobstatus FOREIGN KEY (job_status_id) REFERENCES job_status(job_status_id);


--
-- Name: fk_job_reportversion; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY job
    ADD CONSTRAINT fk_job_reportversion FOREIGN KEY (report_version_id) REFERENCES report_version(report_version_id);


--
-- Name: fk_job_role; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY job
    ADD CONSTRAINT fk_job_role FOREIGN KEY (role_id) REFERENCES role(role_id);


--
-- Name: fk_job_subscription; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY job
    ADD CONSTRAINT fk_job_subscription FOREIGN KEY (subscription_id) REFERENCES subscription(subscription_id);


--
-- Name: fk_jobparameter_job; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY job_parameter
    ADD CONSTRAINT fk_jobparameter_job FOREIGN KEY (job_id) REFERENCES job(job_id);


--
-- Name: fk_jobparameter_reportparameter; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY job_parameter
    ADD CONSTRAINT fk_jobparameter_reportparameter FOREIGN KEY (report_parameter_id) REFERENCES report_parameter(report_parameter_id);


--
-- Name: fk_jobparametervalue_jobparameter; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY job_parameter_value
    ADD CONSTRAINT fk_jobparametervalue_jobparameter FOREIGN KEY (job_parameter_id) REFERENCES job_parameter(job_parameter_id);


--
-- Name: fk_report_reportcategory; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY report
    ADD CONSTRAINT fk_report_reportcategory FOREIGN KEY (report_category_id) REFERENCES report_category(report_category_id);


--
-- Name: fk_reportparameter_parametergroup; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY report_parameter
    ADD CONSTRAINT fk_reportparameter_parametergroup FOREIGN KEY (parameter_group_id) REFERENCES parameter_group(parameter_group_id);


--
-- Name: fk_reportparameter_reportversion; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY report_parameter
    ADD CONSTRAINT fk_reportparameter_reportversion FOREIGN KEY (report_version_id) REFERENCES report_version(report_version_id);


--
-- Name: fk_reportversion_report; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY report_version
    ADD CONSTRAINT fk_reportversion_report FOREIGN KEY (report_id) REFERENCES report(report_id);


--
-- Name: fk_roleparameter_reportparameter; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY role_parameter
    ADD CONSTRAINT fk_roleparameter_reportparameter FOREIGN KEY (report_parameter_id) REFERENCES report_parameter(report_parameter_id);


--
-- Name: fk_roleparameter_role; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY role_parameter
    ADD CONSTRAINT fk_roleparameter_role FOREIGN KEY (role_id) REFERENCES role(role_id);


--
-- Name: fk_roleparametervalue_roleparameter; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY role_parameter_value
    ADD CONSTRAINT fk_roleparametervalue_roleparameter FOREIGN KEY (role_parameter_id) REFERENCES role_parameter(role_parameter_id);


--
-- Name: fk_rolereport_report; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY role_report
    ADD CONSTRAINT fk_rolereport_report FOREIGN KEY (report_id) REFERENCES report(report_id);


--
-- Name: fk_rolereport_role; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY role_report
    ADD CONSTRAINT fk_rolereport_role FOREIGN KEY (role_id) REFERENCES role(role_id);


--
-- Name: fk_rolerole_childrole; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY role_role
    ADD CONSTRAINT fk_rolerole_childrole FOREIGN KEY (child_role_id) REFERENCES role(role_id);


--
-- Name: fk_rolerole_parentrole; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY role_role
    ADD CONSTRAINT fk_rolerole_parentrole FOREIGN KEY (parent_role_id) REFERENCES role(role_id);


--
-- Name: fk_selectionlistvalue_reportparameter; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY selection_list_value
    ADD CONSTRAINT fk_selectionlistvalue_reportparameter FOREIGN KEY (report_parameter_id) REFERENCES report_parameter(report_parameter_id);


--
-- Name: fk_subscription_documentformat; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY subscription
    ADD CONSTRAINT fk_subscription_documentformat FOREIGN KEY (document_format_id) REFERENCES document_format(document_format_id);


--
-- Name: fk_subscription_reportversion; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY subscription
    ADD CONSTRAINT fk_subscription_reportversion FOREIGN KEY (report_version_id) REFERENCES report_version(report_version_id);


--
-- Name: fk_subscription_role; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY subscription
    ADD CONSTRAINT fk_subscription_role FOREIGN KEY (role_id) REFERENCES role(role_id);


--
-- Name: fk_subscriptionparameter_reportparameter; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY subscription_parameter
    ADD CONSTRAINT fk_subscriptionparameter_reportparameter FOREIGN KEY (report_parameter_id) REFERENCES report_parameter(report_parameter_id);


--
-- Name: fk_subscriptionparameter_subscription; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY subscription_parameter
    ADD CONSTRAINT fk_subscriptionparameter_subscription FOREIGN KEY (subscription_id) REFERENCES subscription(subscription_id);


--
-- Name: fk_subscriptionparametervalue_subscriptionparameter; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY subscription_parameter_value
    ADD CONSTRAINT fk_subscriptionparametervalue_subscriptionparameter FOREIGN KEY (subscription_parameter_id) REFERENCES subscription_parameter(subscription_parameter_id);


insert into reporting.document_format (document_format_id, name, file_extension, internet_media_type, binary_data, birt_format, active, created_on) values ('bc5169e0-3d36-483c-a7b5-a76766587991', 'Microsoft Word'               , 'doc' , 'application/msword'                                                       , true , 'doc' , true , current_timestamp AT TIME ZONE 'UTC');
insert into reporting.document_format (document_format_id, name, file_extension, internet_media_type, binary_data, birt_format, active, created_on) values ('d0225349-1642-46e3-a949-4ce39795907f', 'Office Open XML Document'     , 'docx', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'  , true , 'docx', true , current_timestamp AT TIME ZONE 'UTC');
insert into reporting.document_format (document_format_id, name, file_extension, internet_media_type, binary_data, birt_format, active, created_on) values ('e1d0b3f2-f639-4521-a055-d5465dce29a2', 'HTML'                         , 'html', 'text/html'                                                                , false, 'html', false, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.document_format (document_format_id, name, file_extension, internet_media_type, binary_data, birt_format, active, created_on) values ('38b73b21-cb66-42cf-932b-1cdf7937525c', 'OpenDocument Presentation'    , 'odp' , 'application/vnd.oasis.opendocument.presentation'                          , true , 'odp' , true , current_timestamp AT TIME ZONE 'UTC');
insert into reporting.document_format (document_format_id, name, file_extension, internet_media_type, binary_data, birt_format, active, created_on) values ('05a4ad8d-6f30-4d6d-83d5-995345a8dc58', 'OpenDocument Spreadsheet'     , 'ods' , 'application/vnd.oasis.opendocument.spreadsheet'                           , true , 'ods' , true , current_timestamp AT TIME ZONE 'UTC');
insert into reporting.document_format (document_format_id, name, file_extension, internet_media_type, binary_data, birt_format, active, created_on) values ('b4f2249d-f52e-47e2-871c-daf35f4ba78e', 'OpenDocument Text'            , 'odt' , 'application/vnd.oasis.opendocument.text'                                  , true , 'odt' , true , current_timestamp AT TIME ZONE 'UTC');
insert into reporting.document_format (document_format_id, name, file_extension, internet_media_type, binary_data, birt_format, active, created_on) values ('30800d77-5fdd-44bc-94a3-1502bd307c1d', 'PDF'                          , 'pdf' , 'application/pdf'                                                          , true , 'pdf' , true , current_timestamp AT TIME ZONE 'UTC');
insert into reporting.document_format (document_format_id, name, file_extension, internet_media_type, binary_data, birt_format, active, created_on) values ('597f34fb-10d8-4408-971a-1b67472ac588', 'PowerPoint'                   , 'ppt' , 'application/vnd.ms-powerpoint'                                            , true , 'ppt' , true , current_timestamp AT TIME ZONE 'UTC');
insert into reporting.document_format (document_format_id, name, file_extension, internet_media_type, binary_data, birt_format, active, created_on) values ('d7ccb194-91c6-4dce-bbfe-6424f079dc07', 'Office Open XML Presentation' , 'pptx', 'application/vnd.openxmlformats-officedocument.presentationml.presentation', true , 'pptx', true , current_timestamp AT TIME ZONE 'UTC');
insert into reporting.document_format (document_format_id, name, file_extension, internet_media_type, binary_data, birt_format, active, created_on) values ('25762ba8-1688-4100-b323-b9e74eba396c', 'Microsoft Excel'              , 'xls' , 'application/vnd.ms-excel'                                                 , true , 'xls' , true , current_timestamp AT TIME ZONE 'UTC');
insert into reporting.document_format (document_format_id, name, file_extension, internet_media_type, binary_data, birt_format, active, created_on) values ('c78ac922-2f37-4855-83ae-b708d453b005', 'Office Open XML Workbook'     , 'xlsx', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'        , true , 'xlsx', true , current_timestamp AT TIME ZONE 'UTC');


insert into reporting.report_category (report_category_id, description, abbreviation, active, created_on) values ('7a482694-51d2-42d0-b0e2-19dd13bbbc64', 'Accounting'       , 'ACCT' , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.report_category (report_category_id, description, abbreviation, active, created_on) values ('bb2bc482-c19a-4c19-a087-e68ffc62b5a0', 'Q-Free internal'  , 'QFREE', true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.report_category (report_category_id, description, abbreviation, active, created_on) values ('5c3cc664-b685-4f6e-8d9a-2927c6bcffdc', 'Manual validation', 'MIR'  , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.report_category (report_category_id, description, abbreviation, active, created_on) values ('72d7cb27-1770-4cc7-b301-44d39ccf1e76', 'Traffic'          , 'TRA'  , true, current_timestamp AT TIME ZONE 'UTC');


insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('08de9764-735f-4c82-bbe9-3981b29cc133', 'Queued'   , 'QUEUED'   , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('a613aae2-836a-4b03-a75d-cfb8303eaad5', 'Running'  , 'RUNNING'  , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('f378fc09-35e4-4096-b1d1-2db14756b098', 'Completed', 'COMPLETED', true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('2a9cd697-af00-45bc-aa6a-053284b9d9e4', 'Failed'   , 'FAILED'   , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('5125c537-e178-42de-b4dd-e538fa3da802', 'Canceled' , 'CANCELED' , true, current_timestamp AT TIME ZONE 'UTC');


-- Insert  tree of [role] records:
--
-- encoded_password '44rSFJQ9qtHWTBAvrsKd5K/p2j0=' is Base64(SHA-1('password1'))
INSERT INTO reporting.role (role_id, username, login_role, encoded_password, full_name, email, time_zone_id, created_on) VALUES ('29fe8a1f-7826-4df0-8bfd-151b54198655', 'user1', true , '44rSFJQ9qtHWTBAvrsKd5K/p2j0=', 'User Number 1', 'user1@somedomain.com', 'CET'           , '2015-05-07T09:10:00');
-- encoded_password 'KqYKj/f81HPTIeAUav2eJt85UUc=' is Base64(SHA-1('password2'))
INSERT INTO reporting.role (role_id, username, login_role, encoded_password, full_name, email, time_zone_id, created_on) VALUES ('fa06393a-d341-4bf6-b047-1a8c6a383483', 'user2', false, 'KqYKj/f81HPTIeAUav2eJt85UUc=', 'User Number 2', 'user2@somedomain.com', 'CET'           , '2015-05-07T09:12:01');
-- encoded_password 'ERnP037iRzV+A0oI2ETuol9v0g8=' is Base64(SHA-1('password3'))
INSERT INTO reporting.role (role_id, username, login_role, encoded_password, full_name, email, time_zone_id, created_on) VALUES ('b85fd129-17d9-40e7-ac11-7541040f8627', 'user3', true , 'ERnP037iRzV+A0oI2ETuol9v0g8=', 'User Number 3', 'user3@somedomain.com', 'Canada/Pacific', '2015-05-07T09:13:22');
-- encoded_password 'oddYTarKRzjUma1wgohrARFyddg=' is Base64(SHA-1('password4'))
INSERT INTO reporting.role (role_id, username, login_role, encoded_password, full_name, email, time_zone_id, created_on) VALUES ('46e477dc-085f-4714-a24f-742428579fcc', 'user4', true , 'oddYTarKRzjUma1wgohrARFyddg=', 'User Number 4', 'user4@somedomain.com', 'GMT'           , '2015-05-07T09:14:09');


-- Create a global configuration record that contains the current database 
-- version. This will get updated at the database is upgraded over time. This
-- version number will be updated whenever the data model changes *or* Q-Free
-- supplied content changes (records are created, updated or deleted).
INSERT INTO reporting.configuration (param_name, role_id, param_type, integer_value, string_value , created_on) VALUES ('DB_VERSION', null, 'INTEGER', 3, '3', current_timestamp AT TIME ZONE 'UTC');

--ROLLBACK;
COMMIT;
