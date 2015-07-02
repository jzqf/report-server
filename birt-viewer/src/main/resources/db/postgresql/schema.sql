-- PostgreSQL:

DROP TABLE IF EXISTS configuration;
DROP TABLE IF EXISTS job;
DROP TABLE IF EXISTS job_parameter;
DROP TABLE IF EXISTS role_report;
DROP TABLE IF EXISTS role_parameter_value;
DROP TABLE IF EXISTS report;
DROP TABLE IF EXISTS report_category;
DROP TABLE IF EXISTS report_parameter;
DROP TABLE IF EXISTS report_version;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS role_role;
DROP TABLE IF EXISTS subscription_parameter_value;
DROP TABLE IF EXISTS subscription;
DROP TABLE IF EXISTS document_format;
DROP TABLE IF EXISTS parameter_type;
DROP TABLE IF EXISTS widget;

CREATE SCHEMA IF NOT EXISTS reporting;
--------------------------------------------------------------------------------

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
-- Name: document_format; Type: TABLE; Schema: reporting; Owner: dbtest; Tablespace: 
--

CREATE TABLE document_format (
    document_format_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    active boolean NOT NULL,
    binary_data boolean NOT NULL,
    birt_format character varying(12) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    file_extension character varying(12) NOT NULL,
    media_type character varying(100) NOT NULL,
    name character varying(32) NOT NULL
);

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: reporting; Owner: dbtest
--

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: job; Type: TABLE; Schema: reporting; Owner: dbtest; Tablespace: 
--

CREATE TABLE job (
    job_id bigint NOT NULL,
    created_on timestamp without time zone NOT NULL,
    document text,
    encoded boolean,
    file_name character varying(128),
    url character varying(1024),
    document_format_id uuid NOT NULL,
    report_version_id uuid NOT NULL,
    role_id uuid NOT NULL
);

--
-- Name: job_job_id_seq; Type: SEQUENCE; Schema: reporting; Owner: dbtest
--

CREATE SEQUENCE job_job_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: job_job_id_seq1; Type: SEQUENCE; Schema: reporting; Owner: dbtest
--

CREATE SEQUENCE job_job_id_seq1
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: job_job_id_seq1; Type: SEQUENCE OWNED BY; Schema: reporting; Owner: dbtest
--

ALTER SEQUENCE job_job_id_seq1 OWNED BY job.job_id;


--
-- Name: job_parameter_value; Type: TABLE; Schema: reporting; Owner: dbtest; Tablespace: 
--

CREATE TABLE job_parameter_value (
    job_parameter_value_id bigint NOT NULL,
    created_on timestamp without time zone NOT NULL,
    string_value character varying(80) NOT NULL,
    job_id bigint NOT NULL,
    report_parameter_id uuid DEFAULT uuid_generate_v4() NOT NULL
);

--
-- Name: job_parameter_value_job_parameter_value_id_seq; Type: SEQUENCE; Schema: reporting; Owner: dbtest
--

CREATE SEQUENCE job_parameter_value_job_parameter_value_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: job_parameter_value_job_parameter_value_id_seq; Type: SEQUENCE OWNED BY; Schema: reporting; Owner: dbtest
--

ALTER SEQUENCE job_parameter_value_job_parameter_value_id_seq OWNED BY job_parameter_value.job_parameter_value_id;


--
-- Name: parameter_type; Type: TABLE; Schema: reporting; Owner: dbtest; Tablespace: 
--

CREATE TABLE parameter_type (
    parameter_type_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    abbreviation character varying(32) NOT NULL,
    active boolean NOT NULL,
    created_on timestamp without time zone NOT NULL,
    description character varying(32) NOT NULL
);

--
-- Name: report; Type: TABLE; Schema: reporting; Owner: dbtest; Tablespace: 
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
-- Name: report_category; Type: TABLE; Schema: reporting; Owner: dbtest; Tablespace: 
--

CREATE TABLE report_category (
    report_category_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    abbreviation character varying(32) NOT NULL,
    active boolean NOT NULL,
    created_on timestamp without time zone NOT NULL,
    description character varying(32) NOT NULL
);

--
-- Name: report_parameter; Type: TABLE; Schema: reporting; Owner: dbtest; Tablespace: 
--

CREATE TABLE report_parameter (
    report_parameter_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    description character varying(80) NOT NULL,
    multivalued boolean NOT NULL,
    name character varying(32) NOT NULL,
    order_index integer NOT NULL,
    required boolean NOT NULL,
    parameter_type_id uuid NOT NULL,
    report_version_id uuid NOT NULL,
    widget_id uuid NOT NULL
);

--
-- Name: report_version; Type: TABLE; Schema: reporting; Owner: dbtest; Tablespace: 
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
-- Name: role; Type: TABLE; Schema: reporting; Owner: dbtest; Tablespace: 
--

CREATE TABLE role (
    role_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    encoded_password character varying(32) NOT NULL,
    full_name character varying(32),
    login_role boolean NOT NULL,
    username character varying(32) NOT NULL
);

--
-- Name: role_parameter_value; Type: TABLE; Schema: reporting; Owner: dbtest; Tablespace: 
--

CREATE TABLE role_parameter_value (
    role_parameter_value_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    string_value character varying(80) NOT NULL,
    report_parameter_id uuid NOT NULL,
    role_id uuid NOT NULL
);

--
-- Name: role_report; Type: TABLE; Schema: reporting; Owner: dbtest; Tablespace: 
--

CREATE TABLE role_report (
    role_report_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    report_id uuid NOT NULL,
    role_id uuid NOT NULL
);

--
-- Name: role_role; Type: TABLE; Schema: reporting; Owner: dbtest; Tablespace: 
--

CREATE TABLE role_role (
    role_role_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    child_role_id uuid NOT NULL,
    parent_role_id uuid NOT NULL
);

--
-- Name: subscription; Type: TABLE; Schema: reporting; Owner: dbtest; Tablespace: 
--

CREATE TABLE subscription (
    subscription_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    cron_schedule character varying(80),
    description character varying(80),
    email character varying(80) NOT NULL,
    run_once_at timestamp without time zone,
    document_format_id uuid NOT NULL,
    report_version_id uuid NOT NULL,
    role_id uuid NOT NULL
);

--
-- Name: subscription_parameter_value; Type: TABLE; Schema: reporting; Owner: dbtest; Tablespace: 
--

CREATE TABLE subscription_parameter_value (
    subscription_parameter_value_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    day_of_month_number integer,
    day_of_week_number integer,
    days_relative integer,
    month_number integer,
    months_relative integer,
    string_value character varying(80),
    time_value time without time zone,
    week_of_month_number integer,
    week_of_year_number integer,
    weeks_relative integer,
    year_number integer,
    years_relative integer,
    report_parameter_id uuid NOT NULL,
    subscription_id uuid NOT NULL
);

--
-- Name: widget; Type: TABLE; Schema: reporting; Owner: dbtest; Tablespace: 
--

CREATE TABLE widget (
    widget_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    active boolean NOT NULL,
    created_on timestamp without time zone NOT NULL,
    description character varying(80) NOT NULL,
    multiple_select boolean NOT NULL,
    name character varying(32) NOT NULL
);

--
-- Name: job_id; Type: DEFAULT; Schema: reporting; Owner: dbtest
--

ALTER TABLE ONLY job ALTER COLUMN job_id SET DEFAULT nextval('job_job_id_seq1'::regclass);


--
-- Name: job_parameter_value_id; Type: DEFAULT; Schema: reporting; Owner: dbtest
--

ALTER TABLE ONLY job_parameter_value ALTER COLUMN job_parameter_value_id SET DEFAULT nextval('job_parameter_value_job_parameter_value_id_seq'::regclass);


--
-- Name: configuration_pkey; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY configuration
    ADD CONSTRAINT configuration_pkey PRIMARY KEY (configuration_id);


--
-- Name: document_format_pkey; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY document_format
    ADD CONSTRAINT document_format_pkey PRIMARY KEY (document_format_id);


--
-- Name: job_parameter_value_pkey; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY job_parameter_value
    ADD CONSTRAINT job_parameter_value_pkey PRIMARY KEY (job_parameter_value_id);


--
-- Name: job_pkey; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY job
    ADD CONSTRAINT job_pkey PRIMARY KEY (job_id);


--
-- Name: parameter_type_pkey; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY parameter_type
    ADD CONSTRAINT parameter_type_pkey PRIMARY KEY (parameter_type_id);


--
-- Name: report_category_pkey; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY report_category
    ADD CONSTRAINT report_category_pkey PRIMARY KEY (report_category_id);


--
-- Name: report_parameter_pkey; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY report_parameter
    ADD CONSTRAINT report_parameter_pkey PRIMARY KEY (report_parameter_id);


--
-- Name: report_pkey; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY report
    ADD CONSTRAINT report_pkey PRIMARY KEY (report_id);


--
-- Name: report_version_pkey; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY report_version
    ADD CONSTRAINT report_version_pkey PRIMARY KEY (report_version_id);


--
-- Name: role_parameter_value_pkey; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY role_parameter_value
    ADD CONSTRAINT role_parameter_value_pkey PRIMARY KEY (role_parameter_value_id);


--
-- Name: role_pkey; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY role
    ADD CONSTRAINT role_pkey PRIMARY KEY (role_id);


--
-- Name: role_report_pkey; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY role_report
    ADD CONSTRAINT role_report_pkey PRIMARY KEY (role_report_id);


--
-- Name: role_role_pkey; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY role_role
    ADD CONSTRAINT role_role_pkey PRIMARY KEY (role_role_id);


--
-- Name: subscription_parameter_value_pkey; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY subscription_parameter_value
    ADD CONSTRAINT subscription_parameter_value_pkey PRIMARY KEY (subscription_parameter_value_id);


--
-- Name: subscription_pkey; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY subscription
    ADD CONSTRAINT subscription_pkey PRIMARY KEY (subscription_id);


--
-- Name: uc_configuration_paramname_role; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY configuration
    ADD CONSTRAINT uc_configuration_paramname_role UNIQUE (param_name, role_id);


--
-- Name: uc_jobparametervalue_job_parameter_value; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY job_parameter_value
    ADD CONSTRAINT uc_jobparametervalue_job_parameter_value UNIQUE (job_id, report_parameter_id, string_value);


--
-- Name: uc_reportparameter_reportversion_orderindex; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY report_parameter
    ADD CONSTRAINT uc_reportparameter_reportversion_orderindex UNIQUE (report_version_id, order_index);


--
-- Name: uc_reportversion_report_versioncode; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY report_version
    ADD CONSTRAINT uc_reportversion_report_versioncode UNIQUE (report_id, version_code);


--
-- Name: uc_reportversion_report_versionname; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY report_version
    ADD CONSTRAINT uc_reportversion_report_versionname UNIQUE (report_id, version_name);


--
-- Name: uc_role_username; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY role
    ADD CONSTRAINT uc_role_username UNIQUE (username);


--
-- Name: uc_roleparametervalue_role_parameter_value; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY role_parameter_value
    ADD CONSTRAINT uc_roleparametervalue_role_parameter_value UNIQUE (role_id, report_parameter_id, string_value);


--
-- Name: uc_rolereport_role_report; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY role_report
    ADD CONSTRAINT uc_rolereport_role_report UNIQUE (role_id, report_id);


--
-- Name: uc_rolerole_parent_child; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY role_role
    ADD CONSTRAINT uc_rolerole_parent_child UNIQUE (parent_role_id, child_role_id);


--
-- Name: widget_pkey; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY widget
    ADD CONSTRAINT widget_pkey PRIMARY KEY (widget_id);


--
-- Name: fk_configuration_role; Type: FK CONSTRAINT; Schema: reporting; Owner: dbtest
--

ALTER TABLE ONLY configuration
    ADD CONSTRAINT fk_configuration_role FOREIGN KEY (role_id) REFERENCES role(role_id);


--
-- Name: fk_job_documentformat; Type: FK CONSTRAINT; Schema: reporting; Owner: dbtest
--

ALTER TABLE ONLY job
    ADD CONSTRAINT fk_job_documentformat FOREIGN KEY (document_format_id) REFERENCES document_format(document_format_id);


--
-- Name: fk_job_report; Type: FK CONSTRAINT; Schema: reporting; Owner: dbtest
--

ALTER TABLE ONLY job
    ADD CONSTRAINT fk_job_report FOREIGN KEY (report_version_id) REFERENCES report_version(report_version_id);


--
-- Name: fk_job_role; Type: FK CONSTRAINT; Schema: reporting; Owner: dbtest
--

ALTER TABLE ONLY job
    ADD CONSTRAINT fk_job_role FOREIGN KEY (role_id) REFERENCES role(role_id);


--
-- Name: fk_jobparametervalue_job; Type: FK CONSTRAINT; Schema: reporting; Owner: dbtest
--

ALTER TABLE ONLY job_parameter_value
    ADD CONSTRAINT fk_jobparametervalue_job FOREIGN KEY (job_id) REFERENCES job(job_id);


--
-- Name: fk_jobparametervalue_reportparameter; Type: FK CONSTRAINT; Schema: reporting; Owner: dbtest
--

ALTER TABLE ONLY job_parameter_value
    ADD CONSTRAINT fk_jobparametervalue_reportparameter FOREIGN KEY (report_parameter_id) REFERENCES report_parameter(report_parameter_id);


--
-- Name: fk_report_reportcategory; Type: FK CONSTRAINT; Schema: reporting; Owner: dbtest
--

ALTER TABLE ONLY report
    ADD CONSTRAINT fk_report_reportcategory FOREIGN KEY (report_category_id) REFERENCES report_category(report_category_id);


--
-- Name: fk_reportparameter_parametertype; Type: FK CONSTRAINT; Schema: reporting; Owner: dbtest
--

ALTER TABLE ONLY report_parameter
    ADD CONSTRAINT fk_reportparameter_parametertype FOREIGN KEY (parameter_type_id) REFERENCES parameter_type(parameter_type_id);


--
-- Name: fk_reportparameter_report; Type: FK CONSTRAINT; Schema: reporting; Owner: dbtest
--

ALTER TABLE ONLY report_parameter
    ADD CONSTRAINT fk_reportparameter_report FOREIGN KEY (report_version_id) REFERENCES report_version(report_version_id);


--
-- Name: fk_reportparameter_widget; Type: FK CONSTRAINT; Schema: reporting; Owner: dbtest
--

ALTER TABLE ONLY report_parameter
    ADD CONSTRAINT fk_reportparameter_widget FOREIGN KEY (widget_id) REFERENCES widget(widget_id);


--
-- Name: fk_reportversion_report; Type: FK CONSTRAINT; Schema: reporting; Owner: dbtest
--

ALTER TABLE ONLY report_version
    ADD CONSTRAINT fk_reportversion_report FOREIGN KEY (report_id) REFERENCES report(report_id);


--
-- Name: fk_roleparametervalue_reportparameter; Type: FK CONSTRAINT; Schema: reporting; Owner: dbtest
--

ALTER TABLE ONLY role_parameter_value
    ADD CONSTRAINT fk_roleparametervalue_reportparameter FOREIGN KEY (report_parameter_id) REFERENCES report_parameter(report_parameter_id);


--
-- Name: fk_roleparametervalue_role; Type: FK CONSTRAINT; Schema: reporting; Owner: dbtest
--

ALTER TABLE ONLY role_parameter_value
    ADD CONSTRAINT fk_roleparametervalue_role FOREIGN KEY (role_id) REFERENCES role(role_id);


--
-- Name: fk_rolereport_report; Type: FK CONSTRAINT; Schema: reporting; Owner: dbtest
--

ALTER TABLE ONLY role_report
    ADD CONSTRAINT fk_rolereport_report FOREIGN KEY (report_id) REFERENCES report(report_id);


--
-- Name: fk_rolereport_role; Type: FK CONSTRAINT; Schema: reporting; Owner: dbtest
--

ALTER TABLE ONLY role_report
    ADD CONSTRAINT fk_rolereport_role FOREIGN KEY (role_id) REFERENCES role(role_id);


--
-- Name: fk_rolerole_childrole; Type: FK CONSTRAINT; Schema: reporting; Owner: dbtest
--

ALTER TABLE ONLY role_role
    ADD CONSTRAINT fk_rolerole_childrole FOREIGN KEY (child_role_id) REFERENCES role(role_id);


--
-- Name: fk_rolerole_parentrole; Type: FK CONSTRAINT; Schema: reporting; Owner: dbtest
--

ALTER TABLE ONLY role_role
    ADD CONSTRAINT fk_rolerole_parentrole FOREIGN KEY (parent_role_id) REFERENCES role(role_id);


--
-- Name: fk_subscription_documentformat; Type: FK CONSTRAINT; Schema: reporting; Owner: dbtest
--

ALTER TABLE ONLY subscription
    ADD CONSTRAINT fk_subscription_documentformat FOREIGN KEY (document_format_id) REFERENCES document_format(document_format_id);


--
-- Name: fk_subscription_reportversion; Type: FK CONSTRAINT; Schema: reporting; Owner: dbtest
--

ALTER TABLE ONLY subscription
    ADD CONSTRAINT fk_subscription_reportversion FOREIGN KEY (report_version_id) REFERENCES report_version(report_version_id);


--
-- Name: fk_subscription_role; Type: FK CONSTRAINT; Schema: reporting; Owner: dbtest
--

ALTER TABLE ONLY subscription
    ADD CONSTRAINT fk_subscription_role FOREIGN KEY (role_id) REFERENCES role(role_id);


--
-- Name: fk_subscriptionparametervalue_reportparameter; Type: FK CONSTRAINT; Schema: reporting; Owner: dbtest
--

ALTER TABLE ONLY subscription_parameter_value
    ADD CONSTRAINT fk_subscriptionparametervalue_reportparameter FOREIGN KEY (report_parameter_id) REFERENCES report_parameter(report_parameter_id);


--
-- Name: fk_subscriptionparametervalue_subscription; Type: FK CONSTRAINT; Schema: reporting; Owner: dbtest
--

ALTER TABLE ONLY subscription_parameter_value
    ADD CONSTRAINT fk_subscriptionparametervalue_subscription FOREIGN KEY (subscription_id) REFERENCES subscription(subscription_id);
