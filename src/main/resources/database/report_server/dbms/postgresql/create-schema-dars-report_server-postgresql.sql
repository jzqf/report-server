--
-- Name: asset; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE asset (
    asset_id uuid DEFAULT reporting.uuid_generate_v4() NOT NULL,
    active boolean NOT NULL,
    created_on timestamp without time zone NOT NULL,
    filename character varying(256) NOT NULL,
    asset_tree_id uuid NOT NULL,
    asset_type_id uuid NOT NULL,
    document_id uuid NOT NULL
);




--
-- Name: asset_tree; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE asset_tree (
    asset_tree_id uuid DEFAULT reporting.uuid_generate_v4() NOT NULL,
    abbreviation character varying(32) NOT NULL,
    active boolean NOT NULL,
    created_on timestamp without time zone NOT NULL,
    directory character varying(256) NOT NULL,
    name character varying(32) NOT NULL
);




--
-- Name: asset_type; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE asset_type (
    asset_type_id uuid DEFAULT reporting.uuid_generate_v4() NOT NULL,
    abbreviation character varying(32) NOT NULL,
    active boolean NOT NULL,
    created_on timestamp without time zone NOT NULL,
    directory character varying(256) NOT NULL,
    name character varying(32) NOT NULL
);




--
-- Name: authority; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE authority (
    authority_id uuid DEFAULT reporting.uuid_generate_v4() NOT NULL,
    active boolean NOT NULL,
    created_on timestamp without time zone NOT NULL,
    name character varying(50) NOT NULL
);




--
-- Name: configuration; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE configuration (
    configuration_id uuid DEFAULT reporting.uuid_generate_v4() NOT NULL,
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
-- Name: document; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE document (
    document_id uuid DEFAULT reporting.uuid_generate_v4() NOT NULL,
    content bytea,
    created_on timestamp without time zone NOT NULL
);




--
-- Name: document_format; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE document_format (
    document_format_id uuid DEFAULT reporting.uuid_generate_v4() NOT NULL,
    active boolean NOT NULL,
    binary_data boolean NOT NULL,
    birt_format character varying(12) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    file_extension character varying(12) NOT NULL,
    internet_media_type character varying(100) NOT NULL,
    name character varying(32) NOT NULL
);




--
-- Name: job; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE job (
    job_id bigint NOT NULL,
    created_on timestamp without time zone NOT NULL,
    document text,
    email_address character varying(160),
    encoded boolean,
    file_name character varying(128),
    job_status_remarks text,
    job_status_set_at timestamp without time zone NOT NULL,
    report_emailed_at timestamp without time zone,
    report_ran_at timestamp without time zone,
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
-- Name: job_parameter; Type: TABLE; Schema: reporting; Owner: report_server_app
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
-- Name: job_parameter_value; Type: TABLE; Schema: reporting; Owner: report_server_app
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
-- Name: job_status; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE job_status (
    job_status_id uuid DEFAULT reporting.uuid_generate_v4() NOT NULL,
    abbreviation character varying(32) NOT NULL,
    active boolean NOT NULL,
    created_on timestamp without time zone NOT NULL,
    description character varying(32) NOT NULL
);




--
-- Name: parameter_group; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE parameter_group (
    parameter_group_id uuid DEFAULT reporting.uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    group_type integer NOT NULL,
    name character varying(80) NOT NULL,
    prompt_text character varying(132) NOT NULL
);




--
-- Name: report; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE report (
    report_id uuid DEFAULT reporting.uuid_generate_v4() NOT NULL,
    active boolean NOT NULL,
    created_on timestamp without time zone NOT NULL,
    name character varying(80) NOT NULL,
    number integer NOT NULL,
    sort_order integer NOT NULL,
    report_category_id uuid NOT NULL
);




--
-- Name: report_category; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE report_category (
    report_category_id uuid DEFAULT reporting.uuid_generate_v4() NOT NULL,
    abbreviation character varying(32) NOT NULL,
    active boolean NOT NULL,
    created_on timestamp without time zone NOT NULL,
    description character varying(32) NOT NULL
);




--
-- Name: report_parameter; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE report_parameter (
    report_parameter_id uuid DEFAULT reporting.uuid_generate_v4() NOT NULL,
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
-- Name: report_version; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE report_version (
    report_version_id uuid DEFAULT reporting.uuid_generate_v4() NOT NULL,
    active boolean NOT NULL,
    created_on timestamp without time zone NOT NULL,
    file_name character varying(80) NOT NULL,
    rptdesign text NOT NULL,
    version_code integer NOT NULL,
    version_name character varying(16) NOT NULL,
    report_id uuid NOT NULL
);




--
-- Name: role; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE role (
    role_id uuid DEFAULT reporting.uuid_generate_v4() NOT NULL,
    active boolean NOT NULL,
    created_on timestamp without time zone NOT NULL,
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

CREATE TABLE role_authority (
    role_authority_id uuid DEFAULT reporting.uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    authority_id uuid NOT NULL,
    role_id uuid NOT NULL
);




--
-- Name: role_parameter; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE role_parameter (
    role_parameter_id uuid DEFAULT reporting.uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    report_parameter_id uuid NOT NULL,
    role_id uuid NOT NULL
);




--
-- Name: role_parameter_value; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE role_parameter_value (
    role_parameter_value_id uuid DEFAULT reporting.uuid_generate_v4() NOT NULL,
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
-- Name: role_report; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE role_report (
    role_report_id uuid DEFAULT reporting.uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    report_id uuid NOT NULL,
    role_id uuid NOT NULL
);




--
-- Name: role_role; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE role_role (
    role_role_id uuid DEFAULT reporting.uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    child_role_id uuid NOT NULL,
    parent_role_id uuid NOT NULL
);




--
-- Name: selection_list_value; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE selection_list_value (
    selection_list_value_id uuid DEFAULT reporting.uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    order_index integer NOT NULL,
    value_assigned character varying(132) NOT NULL,
    value_displayed character varying(132) NOT NULL,
    report_parameter_id uuid NOT NULL
);




--
-- Name: subscription; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE subscription (
    subscription_id uuid DEFAULT reporting.uuid_generate_v4() NOT NULL,
    active boolean NOT NULL,
    created_on timestamp without time zone NOT NULL,
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
    delivery_datetime_run_at timestamp without time zone,
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

CREATE TABLE subscription_parameter (
    subscription_parameter_id uuid DEFAULT reporting.uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    report_parameter_id uuid NOT NULL,
    subscription_id uuid NOT NULL
);




--
-- Name: subscription_parameter_value; Type: TABLE; Schema: reporting; Owner: report_server_app
--

CREATE TABLE subscription_parameter_value (
    subscription_parameter_value_id uuid DEFAULT reporting.uuid_generate_v4() NOT NULL,
    boolean_value boolean,
    created_on timestamp without time zone NOT NULL,
    date_value date,
    datetime_value timestamp without time zone,
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
-- Name: asset_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY asset
    ADD CONSTRAINT asset_pkey PRIMARY KEY (asset_id);


--
-- Name: asset_tree_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY asset_tree
    ADD CONSTRAINT asset_tree_pkey PRIMARY KEY (asset_tree_id);


--
-- Name: asset_type_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY asset_type
    ADD CONSTRAINT asset_type_pkey PRIMARY KEY (asset_type_id);


--
-- Name: authority_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY authority
    ADD CONSTRAINT authority_pkey PRIMARY KEY (authority_id);


--
-- Name: configuration_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY configuration
    ADD CONSTRAINT configuration_pkey PRIMARY KEY (configuration_id);


--
-- Name: document_format_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY document_format
    ADD CONSTRAINT document_format_pkey PRIMARY KEY (document_format_id);


--
-- Name: document_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY document
    ADD CONSTRAINT document_pkey PRIMARY KEY (document_id);


--
-- Name: job_parameter_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY job_parameter
    ADD CONSTRAINT job_parameter_pkey PRIMARY KEY (job_parameter_id);


--
-- Name: job_parameter_value_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY job_parameter_value
    ADD CONSTRAINT job_parameter_value_pkey PRIMARY KEY (job_parameter_value_id);


--
-- Name: job_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY job
    ADD CONSTRAINT job_pkey PRIMARY KEY (job_id);


--
-- Name: job_status_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY job_status
    ADD CONSTRAINT job_status_pkey PRIMARY KEY (job_status_id);


--
-- Name: parameter_group_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY parameter_group
    ADD CONSTRAINT parameter_group_pkey PRIMARY KEY (parameter_group_id);


--
-- Name: report_category_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY report_category
    ADD CONSTRAINT report_category_pkey PRIMARY KEY (report_category_id);


--
-- Name: report_parameter_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY report_parameter
    ADD CONSTRAINT report_parameter_pkey PRIMARY KEY (report_parameter_id);


--
-- Name: report_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY report
    ADD CONSTRAINT report_pkey PRIMARY KEY (report_id);


--
-- Name: report_version_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY report_version
    ADD CONSTRAINT report_version_pkey PRIMARY KEY (report_version_id);


--
-- Name: role_authority_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY role_authority
    ADD CONSTRAINT role_authority_pkey PRIMARY KEY (role_authority_id);


--
-- Name: role_parameter_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY role_parameter
    ADD CONSTRAINT role_parameter_pkey PRIMARY KEY (role_parameter_id);


--
-- Name: role_parameter_value_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY role_parameter_value
    ADD CONSTRAINT role_parameter_value_pkey PRIMARY KEY (role_parameter_value_id);


--
-- Name: role_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY role
    ADD CONSTRAINT role_pkey PRIMARY KEY (role_id);


--
-- Name: role_report_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY role_report
    ADD CONSTRAINT role_report_pkey PRIMARY KEY (role_report_id);


--
-- Name: role_role_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY role_role
    ADD CONSTRAINT role_role_pkey PRIMARY KEY (role_role_id);


--
-- Name: selection_list_value_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY selection_list_value
    ADD CONSTRAINT selection_list_value_pkey PRIMARY KEY (selection_list_value_id);


--
-- Name: subscription_parameter_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY subscription_parameter
    ADD CONSTRAINT subscription_parameter_pkey PRIMARY KEY (subscription_parameter_id);


--
-- Name: subscription_parameter_value_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY subscription_parameter_value
    ADD CONSTRAINT subscription_parameter_value_pkey PRIMARY KEY (subscription_parameter_value_id);


--
-- Name: subscription_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY subscription
    ADD CONSTRAINT subscription_pkey PRIMARY KEY (subscription_id);


--
-- Name: uc_authority_name; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY authority
    ADD CONSTRAINT uc_authority_name UNIQUE (name);


--
-- Name: uc_configuration_paramname_role; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY configuration
    ADD CONSTRAINT uc_configuration_paramname_role UNIQUE (param_name, role_id);


--
-- Name: uc_jobparameter_job_parameter; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY job_parameter
    ADD CONSTRAINT uc_jobparameter_job_parameter UNIQUE (job_id, report_parameter_id);


--
-- Name: uc_reportparameter_reportversion_orderindex; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY report_parameter
    ADD CONSTRAINT uc_reportparameter_reportversion_orderindex UNIQUE (report_version_id, order_index);


--
-- Name: uc_reportversion_filename; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY report_version
    ADD CONSTRAINT uc_reportversion_filename UNIQUE (file_name);


--
-- Name: uc_reportversion_report_versioncode; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY report_version
    ADD CONSTRAINT uc_reportversion_report_versioncode UNIQUE (report_id, version_code);


--
-- Name: uc_reportversion_report_versionname; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY report_version
    ADD CONSTRAINT uc_reportversion_report_versionname UNIQUE (report_id, version_name);


--
-- Name: uc_role_username; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY role
    ADD CONSTRAINT uc_role_username UNIQUE (username);


--
-- Name: uc_roleauthority_role_authority; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY role_authority
    ADD CONSTRAINT uc_roleauthority_role_authority UNIQUE (role_id, authority_id);


--
-- Name: uc_roleparameter_role_parameter; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY role_parameter
    ADD CONSTRAINT uc_roleparameter_role_parameter UNIQUE (role_id, report_parameter_id);


--
-- Name: uc_rolereport_role_report; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY role_report
    ADD CONSTRAINT uc_rolereport_role_report UNIQUE (role_id, report_id);


--
-- Name: uc_rolerole_parent_child; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY role_role
    ADD CONSTRAINT uc_rolerole_parent_child UNIQUE (parent_role_id, child_role_id);


--
-- Name: uc_subscriptionparameter_subscription_parameter; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY subscription_parameter
    ADD CONSTRAINT uc_subscriptionparameter_subscription_parameter UNIQUE (subscription_id, report_parameter_id);


--
-- Name: uq_asset_name_tree_type; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY asset
    ADD CONSTRAINT uq_asset_name_tree_type UNIQUE (filename, asset_tree_id, asset_type_id);


--
-- Name: uq_assettree_abbreviation; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY asset_tree
    ADD CONSTRAINT uq_assettree_abbreviation UNIQUE (abbreviation);


--
-- Name: uq_assettree_directory; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY asset_tree
    ADD CONSTRAINT uq_assettree_directory UNIQUE (directory);


--
-- Name: uq_assettree_name; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY asset_tree
    ADD CONSTRAINT uq_assettree_name UNIQUE (name);


--
-- Name: uq_assettype_abbreviation; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY asset_type
    ADD CONSTRAINT uq_assettype_abbreviation UNIQUE (abbreviation);


--
-- Name: uq_assettype_directory; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY asset_type
    ADD CONSTRAINT uq_assettype_directory UNIQUE (directory);


--
-- Name: uq_assettype_name; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY asset_type
    ADD CONSTRAINT uq_assettype_name UNIQUE (name);


--
-- Name: fk_asset_assettree; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY asset
    ADD CONSTRAINT fk_asset_assettree FOREIGN KEY (asset_tree_id) REFERENCES asset_tree(asset_tree_id);


--
-- Name: fk_asset_assettype; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY asset
    ADD CONSTRAINT fk_asset_assettype FOREIGN KEY (asset_type_id) REFERENCES asset_type(asset_type_id);


--
-- Name: fk_asset_document; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY asset
    ADD CONSTRAINT fk_asset_document FOREIGN KEY (document_id) REFERENCES document(document_id);


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
-- Name: fk_roleauthority_authority; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY role_authority
    ADD CONSTRAINT fk_roleauthority_authority FOREIGN KEY (authority_id) REFERENCES authority(authority_id);


--
-- Name: fk_roleauthority_role; Type: FK CONSTRAINT; Schema: reporting; Owner: report_server_app
--

ALTER TABLE ONLY role_authority
    ADD CONSTRAINT fk_roleauthority_role FOREIGN KEY (role_id) REFERENCES role(role_id);


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


insert into reporting.report_category (report_category_id, description, abbreviation, active, created_on) values ('7a482694-51d2-42d0-b0e2-19dd13bbbc64', 'Accounting'             , 'ACCT' , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.report_category (report_category_id, description, abbreviation, active, created_on) values ('bb2bc482-c19a-4c19-a087-e68ffc62b5a0', 'Q-Free internal'        , 'QFREE', true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.report_category (report_category_id, description, abbreviation, active, created_on) values ('5c3cc664-b685-4f6e-8d9a-2927c6bcffdc', 'Manual validation'      , 'MIR'  , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.report_category (report_category_id, description, abbreviation, active, created_on) values ('0a8f19bd-b710-4cd5-b7aa-440f3fe644cf', 'Service Level Agreement', 'SLA'  , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.report_category (report_category_id, description, abbreviation, active, created_on) values ('72d7cb27-1770-4cc7-b301-44d39ccf1e76', 'Traffic'                , 'TRA'  , true, current_timestamp AT TIME ZONE 'UTC');


insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('08de9764-735f-4c82-bbe9-3981b29cc133', 'Queued'    , 'QUEUED'    , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('a613aae2-836a-4b03-a75d-cfb8303eaad5', 'Running'   , 'RUNNING'   , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('24274a10-6d83-4f0f-a807-2c96d86ce5d6', 'Delivering', 'DELIVERING', true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('f378fc09-35e4-4096-b1d1-2db14756b098', 'Completed' , 'COMPLETED' , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('2a9cd697-af00-45bc-aa6a-053284b9d9e4', 'Failed'    , 'FAILED'    , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('5125c537-e178-42de-b4dd-e538fa3da802', 'Canceled'  , 'CANCELED'  , true, current_timestamp AT TIME ZONE 'UTC');


-- Create [asset_tree] rows:
--
-- "qfree" directory for assets used by Q-Free-written reports.
insert into reporting.asset_tree (asset_tree_id, name, abbreviation, directory, active, created_on) values ('7f9d0216-48d7-49ba-b043-ec48db03c938', 'Q-Free'  , 'QFREE', 'qfree' , true, current_timestamp AT TIME ZONE 'UTC');
-- "assets" directory for assets used by customer-written reports.
insert into reporting.asset_tree (asset_tree_id, name, abbreviation, directory, active, created_on) values ('272199f9-d407-492f-a147-41a2b7d0cd02', 'Customer', 'CUST' , 'assets', true, current_timestamp AT TIME ZONE 'UTC');
 

-- Create [asset_type] rows:
insert into reporting.asset_type (asset_type_id, name, abbreviation, directory, active, created_on) values ('f2f4b13f-9c45-4515-bb4b-62e8ccc3d95c', 'CSS file'        , 'CSS'       , 'css'        , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.asset_type (asset_type_id, name, abbreviation, directory, active, created_on) values ('2603ed74-94b5-4c39-ad36-a7e16a374237', 'HTML page'       , 'HTML'      , 'html'       , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.asset_type (asset_type_id, name, abbreviation, directory, active, created_on) values ('1e7ddbbc-8b40-4373-bfc5-6e6d3d5964d8', 'Image file'      , 'IMAGE'     , 'images'     , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.asset_type (asset_type_id, name, abbreviation, directory, active, created_on) values ('bbeaf653-2c06-4128-a49d-1d42f13cc443', 'JAR file'        , 'JAR'       , 'jars'       , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.asset_type (asset_type_id, name, abbreviation, directory, active, created_on) values ('1c488bcf-15ab-4fcb-8ab8-23cda9342a77', 'JavaScript file' , 'JS'        , 'js'         , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.asset_type (asset_type_id, name, abbreviation, directory, active, created_on) values ('26835616-00e2-475d-89ce-11c12e659605', 'BIRT Library'    , 'BIRT LIB'  , 'libraries'  , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.asset_type (asset_type_id, name, abbreviation, directory, active, created_on) values ('4b811357-d664-4e44-bc75-197e6946abcd', 'Properties file' , 'PROPERTIES', 'properties' , true, current_timestamp AT TIME ZONE 'UTC');


-- Create "authority" rows:
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('e2883c0e-5972-4225-a805-27410a2866f4', 'USE_RESTAPI'         , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('1e4f29b9-3183-4f54-a4ee-96c2347d7e06', 'MANAGE_AUTHORITIES'  , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('dae0f68f-11c6-438c-8312-aca4d95731fc', 'MANAGE_CATEGORIES'   , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('cd2c5d93-9b57-4a8b-b789-84dd567e0fa2', 'MANAGE_FILEFORMATS'  , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('7e1ee8bb-3d5d-481a-b0f9-5c3fae3f4e38', 'MANAGE_FILESYNCING'  , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('e5b569e6-bb2a-4f00-a9ca-f85ef0a3a339', 'MANAGE_JOBPROCESSOR' , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('12b1cb22-7686-4c9b-b73b-a25d4cb31663', 'MANAGE_JOBS'         , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('ace1edd3-6a5b-4b40-a802-79616472b893', 'DELETE_JOBS'         , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('bb8f7964-504a-4544-8638-11a62cc9a2ca', 'MANAGE_JOBSTATUSES'  , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('1bbc9e73-1095-4471-bdb2-726b10e47936', 'MANAGE_PREFERENCES'  , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('7b758de7-cd54-43fa-baa0-dfbe59e66000', 'MANAGE_REPORTS'      , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('2efd4eca-bcb2-4cec-b804-3142c8297d65', 'UPLOAD_REPORTS'      , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('2dac7af0-ba7d-4009-a313-e9a288272e90', 'MANAGE_ROLES'        , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('608d6156-b155-487d-bdd3-4e00260b7443', 'MANAGE_SUBSCRIPTIONS', true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('94db0a84-e366-4ab8-aeba-171482979f3d', 'DELETE_SUBSCRIPTIONS', true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('b33f1d5c-170a-4737-ae20-eb9e7aa42d04', 'MANAGE_ASSETS'       , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('e5d86ab9-c660-4bb6-8b96-4b75bd59a5d8', 'MANAGE_ASSETTREES'   , false, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('2c68aeeb-af3b-4f7a-8ecf-6ed11252ec23', 'MANAGE_ASSETTYPES'   , false, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('d2717c9b-b7ab-4085-a169-b95ede048c4c', 'MANAGE_DOCUMENTS'    , false, current_timestamp AT TIME ZONE 'UTC');


-- Insert  tree of [role] records:
--
-- Admin role with login privileges. encoded_password is BCrypt.encode('reportadmin')
INSERT INTO reporting.role (role_id, username, login_role, encoded_password, full_name, email_address, time_zone_id, enabled, active, created_on) VALUES ('54aa1d35-f67d-47e6-8bea-cadd6085796e', 'reportadmin', true , '$2a$08$PjY.jVSaf0x2HCnETH6aj.0Gb3RWX9VMunMcpIfrJGBGjsYA2S6vC', 'Report Administrator', null                  , 'CET'           , true, true, current_timestamp AT TIME ZONE 'UTC');
--
--
-- encoded_password is BCrypt.encode('password1')
INSERT INTO reporting.role (role_id, username, login_role, encoded_password, full_name, email_address, time_zone_id, enabled, active, created_on) VALUES ('29fe8a1f-7826-4df0-8bfd-151b54198655', 'user1'      , true , '$2a$08$SIVVleb01XxPHvn72ThPo.NAgKrbMuq5r/JvcWj5g6LIPDqnuVeT2', 'User Number 1'       , 'user1@somedomain.com', 'CET'           , true, true, current_timestamp AT TIME ZONE 'UTC');
-- encoded_password is BCrypt.encode('password2')
INSERT INTO reporting.role (role_id, username, login_role, encoded_password, full_name, email_address, time_zone_id, enabled, active, created_on) VALUES ('fa06393a-d341-4bf6-b047-1a8c6a383483', 'user2'      , false, '$2a$08$SjqsEsbb86MU/eggjgjfuuvQ0WnDhKm1oJ9IJn/uc04J51ksXNbN6', 'User Number 2'       , 'user2@somedomain.com', 'CET'           , true, true, current_timestamp AT TIME ZONE 'UTC');
-- encoded_password is BCrypt.encode('password3')
INSERT INTO reporting.role (role_id, username, login_role, encoded_password, full_name, email_address, time_zone_id, enabled, active, created_on) VALUES ('b85fd129-17d9-40e7-ac11-7541040f8627', 'user3'      , true , '$2a$08$qmN5yWFDL9MT8kjftuVeguljomDvAMkSDaFnw9oSwKdV0RHmNg5TG', 'User Number 3'       , 'user3@somedomain.com', 'Canada/Pacific', true, true, current_timestamp AT TIME ZONE 'UTC');
-- encoded_password is BCrypt.encode('password4')
INSERT INTO reporting.role (role_id, username, login_role, encoded_password, full_name, email_address, time_zone_id, enabled, active, created_on) VALUES ('46e477dc-085f-4714-a24f-742428579fcc', 'user4'      , true , '$2a$08$Co4iHF1kcy1j3r7nd6hBgOAJUgDKaIitNx.BTW8gbVukqwRZr6MLu', 'User Number 4'       , 'user4@somedomain.com', 'GMT'           , true, true, current_timestamp AT TIME ZONE 'UTC');
--
-- Make user "user4" a child of "reportadmin", effectively making "user4" an administrator:
INSERT INTO reporting.role_role (role_role_id, parent_role_id, child_role_id, created_on) VALUES ('3b2c7b99-cf00-43c6-85a7-f4e17bbea386', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '46e477dc-085f-4714-a24f-742428579fcc', current_timestamp AT TIME ZONE 'UTC');
--
-- Give the "Administrator" role most authorities by creating "role_authority" rows:
--INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('80f98056-85c0-4ed0-989c-7e5354ab8151', '54aa1d35-f67d-47e6-8bea-cadd6085796e', 'e2883c0e-5972-4225-a805-27410a2866f4', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('f812aab5-2390-43f3-add3-a3d673939a73', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '1e4f29b9-3183-4f54-a4ee-96c2347d7e06', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('62c78032-5ec7-440c-8107-386b745b9ab9', '54aa1d35-f67d-47e6-8bea-cadd6085796e', 'dae0f68f-11c6-438c-8312-aca4d95731fc', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('eb8e56c2-7a8e-4dbe-97ca-a0e6166b258a', '54aa1d35-f67d-47e6-8bea-cadd6085796e', 'cd2c5d93-9b57-4a8b-b789-84dd567e0fa2', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('7560ef1c-4acf-43bd-af27-b5ad9f992f57', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '7e1ee8bb-3d5d-481a-b0f9-5c3fae3f4e38', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('44dd6566-a02c-47eb-b889-31016fe1d8ed', '54aa1d35-f67d-47e6-8bea-cadd6085796e', 'e5b569e6-bb2a-4f00-a9ca-f85ef0a3a339', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('222e6e22-f3e3-4631-847f-31896139987a', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '12b1cb22-7686-4c9b-b73b-a25d4cb31663', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('d3a80355-b464-4763-97ab-cd76af70b687', '54aa1d35-f67d-47e6-8bea-cadd6085796e', 'ace1edd3-6a5b-4b40-a802-79616472b893', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('69ee95d1-f6b0-4805-b653-e33ad3955609', '54aa1d35-f67d-47e6-8bea-cadd6085796e', 'bb8f7964-504a-4544-8638-11a62cc9a2ca', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('bbaddcf3-ef9c-49d1-9e5a-d9197a106b9e', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '1bbc9e73-1095-4471-bdb2-726b10e47936', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('8e90d278-a555-4a3a-981a-8e329eac63d4', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '7b758de7-cd54-43fa-baa0-dfbe59e66000', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('659e6aa3-14bf-4ba2-8630-538da94f6ffc', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '2efd4eca-bcb2-4cec-b804-3142c8297d65', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('2ef4aaf6-9ea4-48d0-a5a4-79670a264b79', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '2dac7af0-ba7d-4009-a313-e9a288272e90', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('fb5685ae-2470-449f-bfac-a2a291b863b4', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '608d6156-b155-487d-bdd3-4e00260b7443', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('13e14dbc-d30d-4006-95ed-b8de417a2315', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '94db0a84-e366-4ab8-aeba-171482979f3d', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('71ead393-41d2-4dec-b983-aef26ef62827', '54aa1d35-f67d-47e6-8bea-cadd6085796e', 'b33f1d5c-170a-4737-ae20-eb9e7aa42d04', current_timestamp AT TIME ZONE 'UTC');
--INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('5c3f8ac9-0dd1-4ffd-b840-63146e469b78', '54aa1d35-f67d-47e6-8bea-cadd6085796e', 'e5d86ab9-c660-4bb6-8b96-4b75bd59a5d8', current_timestamp AT TIME ZONE 'UTC');
--INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('13680bc1-19c3-4dff-9a9e-720f0ab65ff9', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '2c68aeeb-af3b-4f7a-8ecf-6ed11252ec23', current_timestamp AT TIME ZONE 'UTC');
--INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('5099855c-4ea5-4203-b9c7-972995b72c66', '54aa1d35-f67d-47e6-8bea-cadd6085796e', 'd2717c9b-b7ab-4085-a169-b95ede048c4c', current_timestamp AT TIME ZONE 'UTC');
--
--
-- Create a "Q-Free administrator" role that can be used to bypass external
-- authentication by an authentication provider.
-- encoded_password is BCrypt.encode('qfreereportserveradmin_Af5Dj%4$'):
INSERT INTO reporting.role (role_id, username, login_role, encoded_password, full_name, email_address, time_zone_id, enabled, active, created_on) VALUES ('10ab3537-0b12-44fa-a27b-6cf1aac14282', 'qfree-reportserver-admin', true , '$2a$08$53v/RUtexw7Ovdx4i2F44O4XcyOLLZklf39XZW1C4jT3JjBJQ8fi6', 'Q-Free Administrator', null                  , 'CET'           , true, true, current_timestamp AT TIME ZONE 'UTC');
--
-- Give the "Q-Free administrator" role all authorities by creating "role_authority" rows:
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('be6a207f-10f7-4e86-a174-392b4934d2a0', '10ab3537-0b12-44fa-a27b-6cf1aac14282', 'e2883c0e-5972-4225-a805-27410a2866f4', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('4cb38206-bd6c-4db1-a596-0944b386c093', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '1e4f29b9-3183-4f54-a4ee-96c2347d7e06', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('e2ef968a-7fd6-4e9e-b74a-58fc6f208a50', '10ab3537-0b12-44fa-a27b-6cf1aac14282', 'dae0f68f-11c6-438c-8312-aca4d95731fc', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('143e65a8-3051-4512-bf02-940455f25744', '10ab3537-0b12-44fa-a27b-6cf1aac14282', 'cd2c5d93-9b57-4a8b-b789-84dd567e0fa2', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('e40a6bc3-3888-49ec-a9f5-6c17df4907a2', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '7e1ee8bb-3d5d-481a-b0f9-5c3fae3f4e38', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('6603e5e2-0b71-4e0c-8983-6c1c50efb0d6', '10ab3537-0b12-44fa-a27b-6cf1aac14282', 'e5b569e6-bb2a-4f00-a9ca-f85ef0a3a339', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('89bc2070-aedf-4959-9492-8f1a051f2c47', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '12b1cb22-7686-4c9b-b73b-a25d4cb31663', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('d806bc73-1744-471c-8f24-14af227f780b', '10ab3537-0b12-44fa-a27b-6cf1aac14282', 'ace1edd3-6a5b-4b40-a802-79616472b893', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('43c97218-f645-47be-ac64-7004e34ee31c', '10ab3537-0b12-44fa-a27b-6cf1aac14282', 'bb8f7964-504a-4544-8638-11a62cc9a2ca', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('bdb0077e-ddd1-4651-8584-7d67f6a5d7e6', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '1bbc9e73-1095-4471-bdb2-726b10e47936', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('f3f5546e-d819-4a4a-92be-c6ee150e02d0', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '7b758de7-cd54-43fa-baa0-dfbe59e66000', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('3d1392ab-b5b8-45b8-96e1-cf7060836924', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '2efd4eca-bcb2-4cec-b804-3142c8297d65', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('ce4ffcfd-7802-4e1b-bdf1-6d2b70062cef', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '2dac7af0-ba7d-4009-a313-e9a288272e90', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('54246034-83ed-488c-b447-05fd0b1e57b4', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '608d6156-b155-487d-bdd3-4e00260b7443', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('0fff6274-a9dc-4262-aa29-5feff9414810', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '94db0a84-e366-4ab8-aeba-171482979f3d', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('ef7b2b61-8bfc-4a14-8b41-718a12b115c6', '10ab3537-0b12-44fa-a27b-6cf1aac14282', 'b33f1d5c-170a-4737-ae20-eb9e7aa42d04', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('faac077d-af19-4e00-97da-7c0bf71f07e8', '10ab3537-0b12-44fa-a27b-6cf1aac14282', 'e5d86ab9-c660-4bb6-8b96-4b75bd59a5d8', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('37aaf7d3-1a9f-4e1b-8707-1aaf3ac5bd87', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '2c68aeeb-af3b-4f7a-8ecf-6ed11252ec23', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('7bcdf522-dac7-4e2a-bc42-6d6872a8391d', '10ab3537-0b12-44fa-a27b-6cf1aac14282', 'd2717c9b-b7ab-4085-a169-b95ede048c4c', current_timestamp AT TIME ZONE 'UTC');
--
--
-- Create a role has full access to a ReST API endpoints. It will bypass 
-- external authentication by an authentication provider.
-- encoded_password is BCrypt.encode('ReportServer*RESTADMIN'):
INSERT INTO reporting.role (role_id, username, login_role, encoded_password, full_name, email_address, time_zone_id, enabled, active, created_on) VALUES ('689833f9-e55c-4eaf-aba6-79f8b1d1a058', 'reportserver-restadmin', true , '$2a$08$4sGkB9oqsz0ws5liaPYofem6WwiQzmZ6DzDP7cuvwWN24ycZZefbq', 'ReST Administrator', null                  , 'CET'           , true, true, current_timestamp AT TIME ZONE 'UTC');
--
-- Give the "ReST Administrator" role all authorities by creating "role_authority" rows:
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('ad0fcb0d-6d89-41ad-9ed7-82292757c496', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', 'e2883c0e-5972-4225-a805-27410a2866f4', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('9186ccf4-bfac-43fd-a200-7484f4d7b0b5', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', '1e4f29b9-3183-4f54-a4ee-96c2347d7e06', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('bfeb5c18-545e-4a47-b412-25df077bfc19', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', 'dae0f68f-11c6-438c-8312-aca4d95731fc', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('cc1392c8-bf80-474b-9cb8-e01e9ef067b6', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', 'cd2c5d93-9b57-4a8b-b789-84dd567e0fa2', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('43d33928-08c5-4869-bed0-4e81a3c8e0c3', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', '7e1ee8bb-3d5d-481a-b0f9-5c3fae3f4e38', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('3189a1d0-d7d1-45a5-a002-f34dda0c9916', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', 'e5b569e6-bb2a-4f00-a9ca-f85ef0a3a339', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('619b6650-57bd-4850-a543-9fd0d1a1d3bc', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', '12b1cb22-7686-4c9b-b73b-a25d4cb31663', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('eb257e2f-d6c3-497f-8e0c-614efc068a03', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', 'ace1edd3-6a5b-4b40-a802-79616472b893', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('b0fd8c8b-7d6d-4d4a-a165-b28b06ea3418', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', 'bb8f7964-504a-4544-8638-11a62cc9a2ca', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('25e8ab17-d66a-4de0-b692-dda9ff36c4ee', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', '1bbc9e73-1095-4471-bdb2-726b10e47936', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('dd5a9f19-0a8b-4ea9-885f-dcbda4c7431d', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', '7b758de7-cd54-43fa-baa0-dfbe59e66000', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('534d4b17-1ea6-48eb-8905-6798efe5011e', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', '2efd4eca-bcb2-4cec-b804-3142c8297d65', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('ca324426-78e9-4232-8ef0-46be70757b01', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', '2dac7af0-ba7d-4009-a313-e9a288272e90', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('e3f80441-e8ac-44e3-825a-a4ed0c8118ef', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', '608d6156-b155-487d-bdd3-4e00260b7443', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('6fc151b0-81fb-46d4-a8a4-4cca0ca82e21', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', '94db0a84-e366-4ab8-aeba-171482979f3d', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('aec93434-b0b4-432a-b760-57ff6641564a', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', 'b33f1d5c-170a-4737-ae20-eb9e7aa42d04', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('62ebb315-0fcb-49ad-84ac-469adc36f2ef', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', 'e5d86ab9-c660-4bb6-8b96-4b75bd59a5d8', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('2f74ad0b-a447-4012-8816-d1e234b5b3ee', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', '2c68aeeb-af3b-4f7a-8ecf-6ed11252ec23', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('f1416e42-5aff-4696-9e90-146a0a548b8b', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', 'd2717c9b-b7ab-4085-a169-b95ede048c4c', current_timestamp AT TIME ZONE 'UTC');


-- Create a global configuration record that contains the current database 
-- version. This will get updated at the database is upgraded over time. This
-- version number will be updated whenever the data model changes *or* Q-Free
-- supplied content changes (records are created, updated or deleted).
INSERT INTO reporting.configuration (param_name, role_id, param_type, integer_value, string_value , created_on) VALUES ('DB_VERSION', null, 'INTEGER', 9, '9', current_timestamp AT TIME ZONE 'UTC');
