--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: reporting; Type: SCHEMA; Schema: -; Owner: report_server_app
--

CREATE SCHEMA reporting;


ALTER SCHEMA reporting OWNER TO report_server_app;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


--
-- Name: uuid-ossp; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA reporting;


--
-- Name: EXTENSION "uuid-ossp"; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION "uuid-ossp" IS 'generate universally unique identifiers (UUIDs)';


SET search_path = reporting, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: authority; Type: TABLE; Schema: reporting; Owner: report_server_app; Tablespace: 
--

CREATE TABLE authority (
    authority_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    active boolean NOT NULL,
    created_on timestamp without time zone NOT NULL,
    name character varying(50) NOT NULL
);


ALTER TABLE authority OWNER TO report_server_app;

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


ALTER TABLE configuration OWNER TO report_server_app;

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


ALTER TABLE document_format OWNER TO report_server_app;

--
-- Name: job; Type: TABLE; Schema: reporting; Owner: report_server_app; Tablespace: 
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


ALTER TABLE job OWNER TO report_server_app;

--
-- Name: job_job_id_seq; Type: SEQUENCE; Schema: reporting; Owner: report_server_app
--

CREATE SEQUENCE job_job_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE job_job_id_seq OWNER TO report_server_app;

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


ALTER TABLE job_parameter OWNER TO report_server_app;

--
-- Name: job_parameter_job_parameter_id_seq; Type: SEQUENCE; Schema: reporting; Owner: report_server_app
--

CREATE SEQUENCE job_parameter_job_parameter_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE job_parameter_job_parameter_id_seq OWNER TO report_server_app;

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


ALTER TABLE job_parameter_value OWNER TO report_server_app;

--
-- Name: job_parameter_value_job_parameter_value_id_seq; Type: SEQUENCE; Schema: reporting; Owner: report_server_app
--

CREATE SEQUENCE job_parameter_value_job_parameter_value_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE job_parameter_value_job_parameter_value_id_seq OWNER TO report_server_app;

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


ALTER TABLE job_status OWNER TO report_server_app;

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


ALTER TABLE parameter_group OWNER TO report_server_app;

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


ALTER TABLE report OWNER TO report_server_app;

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


ALTER TABLE report_category OWNER TO report_server_app;

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


ALTER TABLE report_parameter OWNER TO report_server_app;

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


ALTER TABLE report_version OWNER TO report_server_app;

--
-- Name: role; Type: TABLE; Schema: reporting; Owner: report_server_app; Tablespace: 
--

CREATE TABLE role (
    role_id uuid DEFAULT uuid_generate_v4() NOT NULL,
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


ALTER TABLE role OWNER TO report_server_app;

--
-- Name: role_authority; Type: TABLE; Schema: reporting; Owner: report_server_app; Tablespace: 
--

CREATE TABLE role_authority (
    role_authority_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    authority_id uuid NOT NULL,
    role_id uuid NOT NULL
);


ALTER TABLE role_authority OWNER TO report_server_app;

--
-- Name: role_parameter; Type: TABLE; Schema: reporting; Owner: report_server_app; Tablespace: 
--

CREATE TABLE role_parameter (
    role_parameter_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    report_parameter_id uuid NOT NULL,
    role_id uuid NOT NULL
);


ALTER TABLE role_parameter OWNER TO report_server_app;

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


ALTER TABLE role_parameter_value OWNER TO report_server_app;

--
-- Name: role_report; Type: TABLE; Schema: reporting; Owner: report_server_app; Tablespace: 
--

CREATE TABLE role_report (
    role_report_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    report_id uuid NOT NULL,
    role_id uuid NOT NULL
);


ALTER TABLE role_report OWNER TO report_server_app;

--
-- Name: role_role; Type: TABLE; Schema: reporting; Owner: report_server_app; Tablespace: 
--

CREATE TABLE role_role (
    role_role_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    child_role_id uuid NOT NULL,
    parent_role_id uuid NOT NULL
);


ALTER TABLE role_role OWNER TO report_server_app;

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


ALTER TABLE selection_list_value OWNER TO report_server_app;

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
    email_address character varying(160),
    enabled boolean NOT NULL,
    document_format_id uuid NOT NULL,
    report_version_id uuid NOT NULL,
    role_id uuid NOT NULL
);


ALTER TABLE subscription OWNER TO report_server_app;

--
-- Name: subscription_parameter; Type: TABLE; Schema: reporting; Owner: report_server_app; Tablespace: 
--

CREATE TABLE subscription_parameter (
    subscription_parameter_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    report_parameter_id uuid NOT NULL,
    subscription_id uuid NOT NULL
);


ALTER TABLE subscription_parameter OWNER TO report_server_app;

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


ALTER TABLE subscription_parameter_value OWNER TO report_server_app;

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
-- Data for Name: authority; Type: TABLE DATA; Schema: reporting; Owner: report_server_app
--

COPY authority (authority_id, active, created_on, name) FROM stdin;
e2883c0e-5972-4225-a805-27410a2866f4	t	2016-01-12 14:52:30.241347	USE_RESTAPI
1e4f29b9-3183-4f54-a4ee-96c2347d7e06	t	2016-01-12 14:52:30.241347	MANAGE_AUTHORITIES
dae0f68f-11c6-438c-8312-aca4d95731fc	t	2016-01-12 14:52:30.241347	MANAGE_CATEGORIES
cd2c5d93-9b57-4a8b-b789-84dd567e0fa2	t	2016-01-12 14:52:30.241347	MANAGE_FILEFORMATS
7e1ee8bb-3d5d-481a-b0f9-5c3fae3f4e38	t	2016-01-12 14:52:30.241347	MANAGE_FILESYNCING
e5b569e6-bb2a-4f00-a9ca-f85ef0a3a339	t	2016-01-12 14:52:30.241347	MANAGE_JOBPROCESSOR
12b1cb22-7686-4c9b-b73b-a25d4cb31663	t	2016-01-12 14:52:30.241347	MANAGE_JOBS
ace1edd3-6a5b-4b40-a802-79616472b893	t	2016-01-12 14:52:30.241347	DELETE_JOBS
bb8f7964-504a-4544-8638-11a62cc9a2ca	t	2016-01-12 14:52:30.241347	MANAGE_JOBSTATUSES
1bbc9e73-1095-4471-bdb2-726b10e47936	t	2016-01-12 14:52:30.241347	MANAGE_PREFERENCES
7b758de7-cd54-43fa-baa0-dfbe59e66000	t	2016-01-12 14:52:30.241347	MANAGE_REPORTS
2efd4eca-bcb2-4cec-b804-3142c8297d65	t	2016-01-12 14:52:30.241347	UPLOAD_REPORTS
2dac7af0-ba7d-4009-a313-e9a288272e90	t	2016-01-12 14:52:30.241347	MANAGE_ROLES
608d6156-b155-487d-bdd3-4e00260b7443	t	2016-01-12 14:52:30.241347	MANAGE_SUBSCRIPTIONS
94db0a84-e366-4ab8-aeba-171482979f3d	t	2016-01-12 14:52:30.241347	DELETE_SUBSCRIPTIONS
\.


--
-- Data for Name: configuration; Type: TABLE DATA; Schema: reporting; Owner: report_server_app
--

COPY configuration (configuration_id, boolean_value, bytea_value, created_on, date_value, datetime_value, double_value, float_value, integer_value, long_value, param_name, param_type, string_value, text_value, time_value, role_id) FROM stdin;
dd8ac737-a9bc-495d-8f57-f7ddf1136ffa	\N	\N	2016-01-12 14:52:30.241347	\N	\N	\N	\N	6	\N	DB_VERSION	INTEGER	6	\N	\N	\N
\.


--
-- Data for Name: document_format; Type: TABLE DATA; Schema: reporting; Owner: report_server_app
--

COPY document_format (document_format_id, active, binary_data, birt_format, created_on, file_extension, internet_media_type, name) FROM stdin;
bc5169e0-3d36-483c-a7b5-a76766587991	t	t	doc	2016-01-12 14:52:30.241347	doc	application/msword	Microsoft Word
d0225349-1642-46e3-a949-4ce39795907f	t	t	docx	2016-01-12 14:52:30.241347	docx	application/vnd.openxmlformats-officedocument.wordprocessingml.document	Office Open XML Document
e1d0b3f2-f639-4521-a055-d5465dce29a2	f	f	html	2016-01-12 14:52:30.241347	html	text/html	HTML
38b73b21-cb66-42cf-932b-1cdf7937525c	t	t	odp	2016-01-12 14:52:30.241347	odp	application/vnd.oasis.opendocument.presentation	OpenDocument Presentation
05a4ad8d-6f30-4d6d-83d5-995345a8dc58	t	t	ods	2016-01-12 14:52:30.241347	ods	application/vnd.oasis.opendocument.spreadsheet	OpenDocument Spreadsheet
b4f2249d-f52e-47e2-871c-daf35f4ba78e	t	t	odt	2016-01-12 14:52:30.241347	odt	application/vnd.oasis.opendocument.text	OpenDocument Text
30800d77-5fdd-44bc-94a3-1502bd307c1d	t	t	pdf	2016-01-12 14:52:30.241347	pdf	application/pdf	PDF
597f34fb-10d8-4408-971a-1b67472ac588	t	t	ppt	2016-01-12 14:52:30.241347	ppt	application/vnd.ms-powerpoint	PowerPoint
d7ccb194-91c6-4dce-bbfe-6424f079dc07	t	t	pptx	2016-01-12 14:52:30.241347	pptx	application/vnd.openxmlformats-officedocument.presentationml.presentation	Office Open XML Presentation
25762ba8-1688-4100-b323-b9e74eba396c	t	t	xls	2016-01-12 14:52:30.241347	xls	application/vnd.ms-excel	Microsoft Excel
c78ac922-2f37-4855-83ae-b708d453b005	t	t	xlsx	2016-01-12 14:52:30.241347	xlsx	application/vnd.openxmlformats-officedocument.spreadsheetml.sheet	Office Open XML Workbook
\.


--
-- Data for Name: job; Type: TABLE DATA; Schema: reporting; Owner: report_server_app
--

COPY job (job_id, created_on, document, email_address, encoded, file_name, job_status_remarks, job_status_set_at, report_emailed_at, report_ran_at, url, document_format_id, job_status_id, report_version_id, role_id, subscription_id) FROM stdin;
\.


--
-- Name: job_job_id_seq; Type: SEQUENCE SET; Schema: reporting; Owner: report_server_app
--

SELECT pg_catalog.setval('job_job_id_seq', 1, false);


--
-- Data for Name: job_parameter; Type: TABLE DATA; Schema: reporting; Owner: report_server_app
--

COPY job_parameter (job_parameter_id, created_on, job_id, report_parameter_id) FROM stdin;
\.


--
-- Name: job_parameter_job_parameter_id_seq; Type: SEQUENCE SET; Schema: reporting; Owner: report_server_app
--

SELECT pg_catalog.setval('job_parameter_job_parameter_id_seq', 1, false);


--
-- Data for Name: job_parameter_value; Type: TABLE DATA; Schema: reporting; Owner: report_server_app
--

COPY job_parameter_value (job_parameter_value_id, boolean_value, created_on, date_value, datetime_value, float_value, integer_value, string_value, time_value, job_parameter_id) FROM stdin;
\.


--
-- Name: job_parameter_value_job_parameter_value_id_seq; Type: SEQUENCE SET; Schema: reporting; Owner: report_server_app
--

SELECT pg_catalog.setval('job_parameter_value_job_parameter_value_id_seq', 1, false);


--
-- Data for Name: job_status; Type: TABLE DATA; Schema: reporting; Owner: report_server_app
--

COPY job_status (job_status_id, abbreviation, active, created_on, description) FROM stdin;
08de9764-735f-4c82-bbe9-3981b29cc133	QUEUED	t	2016-01-12 14:52:30.241347	Queued
a613aae2-836a-4b03-a75d-cfb8303eaad5	RUNNING	t	2016-01-12 14:52:30.241347	Running
24274a10-6d83-4f0f-a807-2c96d86ce5d6	DELIVERING	t	2016-01-12 14:52:30.241347	Delivering
f378fc09-35e4-4096-b1d1-2db14756b098	COMPLETED	t	2016-01-12 14:52:30.241347	Completed
2a9cd697-af00-45bc-aa6a-053284b9d9e4	FAILED	t	2016-01-12 14:52:30.241347	Failed
5125c537-e178-42de-b4dd-e538fa3da802	CANCELED	t	2016-01-12 14:52:30.241347	Canceled
\.


--
-- Data for Name: parameter_group; Type: TABLE DATA; Schema: reporting; Owner: report_server_app
--

COPY parameter_group (parameter_group_id, created_on, group_type, name, prompt_text) FROM stdin;
\.


--
-- Data for Name: report; Type: TABLE DATA; Schema: reporting; Owner: report_server_app
--

COPY report (report_id, active, created_on, name, number, sort_order, report_category_id) FROM stdin;
\.


--
-- Data for Name: report_category; Type: TABLE DATA; Schema: reporting; Owner: report_server_app
--

COPY report_category (report_category_id, abbreviation, active, created_on, description) FROM stdin;
7a482694-51d2-42d0-b0e2-19dd13bbbc64	ACCT	t	2016-01-12 14:52:30.241347	Accounting
bb2bc482-c19a-4c19-a087-e68ffc62b5a0	QFREE	t	2016-01-12 14:52:30.241347	Q-Free internal
5c3cc664-b685-4f6e-8d9a-2927c6bcffdc	MIR	t	2016-01-12 14:52:30.241347	Manual validation
72d7cb27-1770-4cc7-b301-44d39ccf1e76	TRA	t	2016-01-12 14:52:30.241347	Traffic
\.


--
-- Data for Name: report_parameter; Type: TABLE DATA; Schema: reporting; Owner: report_server_app
--

COPY report_parameter (report_parameter_id, alignment, allow_new_values, auto_suggest_threshold, control_type, created_on, data_type, default_value, display_format, display_in_fixed_order, display_name, help_text, hidden, multivalued, name, order_index, parameter_type, prompt_text, required, selection_list_type, value_concealed, value_expr, parameter_group_id, report_version_id) FROM stdin;
\.


--
-- Data for Name: report_version; Type: TABLE DATA; Schema: reporting; Owner: report_server_app
--

COPY report_version (report_version_id, active, created_on, file_name, rptdesign, version_code, version_name, report_id) FROM stdin;
\.


--
-- Data for Name: role; Type: TABLE DATA; Schema: reporting; Owner: report_server_app
--

COPY role (role_id, active, created_on, email_address, enabled, encoded_password, full_name, login_role, time_zone_id, username) FROM stdin;
54aa1d35-f67d-47e6-8bea-cadd6085796e	t	2016-01-12 14:52:30.241347	\N	t	$2a$08$PjY.jVSaf0x2HCnETH6aj.0Gb3RWX9VMunMcpIfrJGBGjsYA2S6vC	Report Administrator	t	CET	reportadmin
29fe8a1f-7826-4df0-8bfd-151b54198655	t	2016-01-12 14:52:30.241347	user1@somedomain.com	t	$2a$08$SIVVleb01XxPHvn72ThPo.NAgKrbMuq5r/JvcWj5g6LIPDqnuVeT2	User Number 1	t	CET	user1
fa06393a-d341-4bf6-b047-1a8c6a383483	t	2016-01-12 14:52:30.241347	user2@somedomain.com	t	$2a$08$SjqsEsbb86MU/eggjgjfuuvQ0WnDhKm1oJ9IJn/uc04J51ksXNbN6	User Number 2	f	CET	user2
b85fd129-17d9-40e7-ac11-7541040f8627	t	2016-01-12 14:52:30.241347	user3@somedomain.com	t	$2a$08$qmN5yWFDL9MT8kjftuVeguljomDvAMkSDaFnw9oSwKdV0RHmNg5TG	User Number 3	t	Canada/Pacific	user3
46e477dc-085f-4714-a24f-742428579fcc	t	2016-01-12 14:52:30.241347	user4@somedomain.com	t	$2a$08$Co4iHF1kcy1j3r7nd6hBgOAJUgDKaIitNx.BTW8gbVukqwRZr6MLu	User Number 4	t	GMT	user4
10ab3537-0b12-44fa-a27b-6cf1aac14282	t	2016-01-12 14:52:30.241347	\N	t	$2a$08$53v/RUtexw7Ovdx4i2F44O4XcyOLLZklf39XZW1C4jT3JjBJQ8fi6	Q-Free Administrator	t	CET	qfree-reportserver-admin
689833f9-e55c-4eaf-aba6-79f8b1d1a058	t	2016-01-12 14:52:30.241347	\N	t	$2a$08$4sGkB9oqsz0ws5liaPYofem6WwiQzmZ6DzDP7cuvwWN24ycZZefbq	ReST Administrator	t	CET	reportserver-restadmin
\.


--
-- Data for Name: role_authority; Type: TABLE DATA; Schema: reporting; Owner: report_server_app
--

COPY role_authority (role_authority_id, created_on, authority_id, role_id) FROM stdin;
f812aab5-2390-43f3-add3-a3d673939a73	2016-01-12 14:52:30.241347	1e4f29b9-3183-4f54-a4ee-96c2347d7e06	54aa1d35-f67d-47e6-8bea-cadd6085796e
62c78032-5ec7-440c-8107-386b745b9ab9	2016-01-12 14:52:30.241347	dae0f68f-11c6-438c-8312-aca4d95731fc	54aa1d35-f67d-47e6-8bea-cadd6085796e
eb8e56c2-7a8e-4dbe-97ca-a0e6166b258a	2016-01-12 14:52:30.241347	cd2c5d93-9b57-4a8b-b789-84dd567e0fa2	54aa1d35-f67d-47e6-8bea-cadd6085796e
7560ef1c-4acf-43bd-af27-b5ad9f992f57	2016-01-12 14:52:30.241347	7e1ee8bb-3d5d-481a-b0f9-5c3fae3f4e38	54aa1d35-f67d-47e6-8bea-cadd6085796e
44dd6566-a02c-47eb-b889-31016fe1d8ed	2016-01-12 14:52:30.241347	e5b569e6-bb2a-4f00-a9ca-f85ef0a3a339	54aa1d35-f67d-47e6-8bea-cadd6085796e
222e6e22-f3e3-4631-847f-31896139987a	2016-01-12 14:52:30.241347	12b1cb22-7686-4c9b-b73b-a25d4cb31663	54aa1d35-f67d-47e6-8bea-cadd6085796e
d3a80355-b464-4763-97ab-cd76af70b687	2016-01-12 14:52:30.241347	ace1edd3-6a5b-4b40-a802-79616472b893	54aa1d35-f67d-47e6-8bea-cadd6085796e
69ee95d1-f6b0-4805-b653-e33ad3955609	2016-01-12 14:52:30.241347	bb8f7964-504a-4544-8638-11a62cc9a2ca	54aa1d35-f67d-47e6-8bea-cadd6085796e
bbaddcf3-ef9c-49d1-9e5a-d9197a106b9e	2016-01-12 14:52:30.241347	1bbc9e73-1095-4471-bdb2-726b10e47936	54aa1d35-f67d-47e6-8bea-cadd6085796e
8e90d278-a555-4a3a-981a-8e329eac63d4	2016-01-12 14:52:30.241347	7b758de7-cd54-43fa-baa0-dfbe59e66000	54aa1d35-f67d-47e6-8bea-cadd6085796e
659e6aa3-14bf-4ba2-8630-538da94f6ffc	2016-01-12 14:52:30.241347	2efd4eca-bcb2-4cec-b804-3142c8297d65	54aa1d35-f67d-47e6-8bea-cadd6085796e
2ef4aaf6-9ea4-48d0-a5a4-79670a264b79	2016-01-12 14:52:30.241347	2dac7af0-ba7d-4009-a313-e9a288272e90	54aa1d35-f67d-47e6-8bea-cadd6085796e
fb5685ae-2470-449f-bfac-a2a291b863b4	2016-01-12 14:52:30.241347	608d6156-b155-487d-bdd3-4e00260b7443	54aa1d35-f67d-47e6-8bea-cadd6085796e
13e14dbc-d30d-4006-95ed-b8de417a2315	2016-01-12 14:52:30.241347	94db0a84-e366-4ab8-aeba-171482979f3d	54aa1d35-f67d-47e6-8bea-cadd6085796e
be6a207f-10f7-4e86-a174-392b4934d2a0	2016-01-12 14:52:30.241347	e2883c0e-5972-4225-a805-27410a2866f4	10ab3537-0b12-44fa-a27b-6cf1aac14282
4cb38206-bd6c-4db1-a596-0944b386c093	2016-01-12 14:52:30.241347	1e4f29b9-3183-4f54-a4ee-96c2347d7e06	10ab3537-0b12-44fa-a27b-6cf1aac14282
e2ef968a-7fd6-4e9e-b74a-58fc6f208a50	2016-01-12 14:52:30.241347	dae0f68f-11c6-438c-8312-aca4d95731fc	10ab3537-0b12-44fa-a27b-6cf1aac14282
143e65a8-3051-4512-bf02-940455f25744	2016-01-12 14:52:30.241347	cd2c5d93-9b57-4a8b-b789-84dd567e0fa2	10ab3537-0b12-44fa-a27b-6cf1aac14282
e40a6bc3-3888-49ec-a9f5-6c17df4907a2	2016-01-12 14:52:30.241347	7e1ee8bb-3d5d-481a-b0f9-5c3fae3f4e38	10ab3537-0b12-44fa-a27b-6cf1aac14282
6603e5e2-0b71-4e0c-8983-6c1c50efb0d6	2016-01-12 14:52:30.241347	e5b569e6-bb2a-4f00-a9ca-f85ef0a3a339	10ab3537-0b12-44fa-a27b-6cf1aac14282
89bc2070-aedf-4959-9492-8f1a051f2c47	2016-01-12 14:52:30.241347	12b1cb22-7686-4c9b-b73b-a25d4cb31663	10ab3537-0b12-44fa-a27b-6cf1aac14282
d806bc73-1744-471c-8f24-14af227f780b	2016-01-12 14:52:30.241347	ace1edd3-6a5b-4b40-a802-79616472b893	10ab3537-0b12-44fa-a27b-6cf1aac14282
43c97218-f645-47be-ac64-7004e34ee31c	2016-01-12 14:52:30.241347	bb8f7964-504a-4544-8638-11a62cc9a2ca	10ab3537-0b12-44fa-a27b-6cf1aac14282
bdb0077e-ddd1-4651-8584-7d67f6a5d7e6	2016-01-12 14:52:30.241347	1bbc9e73-1095-4471-bdb2-726b10e47936	10ab3537-0b12-44fa-a27b-6cf1aac14282
f3f5546e-d819-4a4a-92be-c6ee150e02d0	2016-01-12 14:52:30.241347	7b758de7-cd54-43fa-baa0-dfbe59e66000	10ab3537-0b12-44fa-a27b-6cf1aac14282
3d1392ab-b5b8-45b8-96e1-cf7060836924	2016-01-12 14:52:30.241347	2efd4eca-bcb2-4cec-b804-3142c8297d65	10ab3537-0b12-44fa-a27b-6cf1aac14282
ce4ffcfd-7802-4e1b-bdf1-6d2b70062cef	2016-01-12 14:52:30.241347	2dac7af0-ba7d-4009-a313-e9a288272e90	10ab3537-0b12-44fa-a27b-6cf1aac14282
54246034-83ed-488c-b447-05fd0b1e57b4	2016-01-12 14:52:30.241347	608d6156-b155-487d-bdd3-4e00260b7443	10ab3537-0b12-44fa-a27b-6cf1aac14282
0fff6274-a9dc-4262-aa29-5feff9414810	2016-01-12 14:52:30.241347	94db0a84-e366-4ab8-aeba-171482979f3d	10ab3537-0b12-44fa-a27b-6cf1aac14282
ad0fcb0d-6d89-41ad-9ed7-82292757c496	2016-01-12 14:52:30.241347	e2883c0e-5972-4225-a805-27410a2866f4	689833f9-e55c-4eaf-aba6-79f8b1d1a058
9186ccf4-bfac-43fd-a200-7484f4d7b0b5	2016-01-12 14:52:30.241347	1e4f29b9-3183-4f54-a4ee-96c2347d7e06	689833f9-e55c-4eaf-aba6-79f8b1d1a058
bfeb5c18-545e-4a47-b412-25df077bfc19	2016-01-12 14:52:30.241347	dae0f68f-11c6-438c-8312-aca4d95731fc	689833f9-e55c-4eaf-aba6-79f8b1d1a058
cc1392c8-bf80-474b-9cb8-e01e9ef067b6	2016-01-12 14:52:30.241347	cd2c5d93-9b57-4a8b-b789-84dd567e0fa2	689833f9-e55c-4eaf-aba6-79f8b1d1a058
43d33928-08c5-4869-bed0-4e81a3c8e0c3	2016-01-12 14:52:30.241347	7e1ee8bb-3d5d-481a-b0f9-5c3fae3f4e38	689833f9-e55c-4eaf-aba6-79f8b1d1a058
3189a1d0-d7d1-45a5-a002-f34dda0c9916	2016-01-12 14:52:30.241347	e5b569e6-bb2a-4f00-a9ca-f85ef0a3a339	689833f9-e55c-4eaf-aba6-79f8b1d1a058
619b6650-57bd-4850-a543-9fd0d1a1d3bc	2016-01-12 14:52:30.241347	12b1cb22-7686-4c9b-b73b-a25d4cb31663	689833f9-e55c-4eaf-aba6-79f8b1d1a058
eb257e2f-d6c3-497f-8e0c-614efc068a03	2016-01-12 14:52:30.241347	ace1edd3-6a5b-4b40-a802-79616472b893	689833f9-e55c-4eaf-aba6-79f8b1d1a058
b0fd8c8b-7d6d-4d4a-a165-b28b06ea3418	2016-01-12 14:52:30.241347	bb8f7964-504a-4544-8638-11a62cc9a2ca	689833f9-e55c-4eaf-aba6-79f8b1d1a058
25e8ab17-d66a-4de0-b692-dda9ff36c4ee	2016-01-12 14:52:30.241347	1bbc9e73-1095-4471-bdb2-726b10e47936	689833f9-e55c-4eaf-aba6-79f8b1d1a058
dd5a9f19-0a8b-4ea9-885f-dcbda4c7431d	2016-01-12 14:52:30.241347	7b758de7-cd54-43fa-baa0-dfbe59e66000	689833f9-e55c-4eaf-aba6-79f8b1d1a058
534d4b17-1ea6-48eb-8905-6798efe5011e	2016-01-12 14:52:30.241347	2efd4eca-bcb2-4cec-b804-3142c8297d65	689833f9-e55c-4eaf-aba6-79f8b1d1a058
ca324426-78e9-4232-8ef0-46be70757b01	2016-01-12 14:52:30.241347	2dac7af0-ba7d-4009-a313-e9a288272e90	689833f9-e55c-4eaf-aba6-79f8b1d1a058
e3f80441-e8ac-44e3-825a-a4ed0c8118ef	2016-01-12 14:52:30.241347	608d6156-b155-487d-bdd3-4e00260b7443	689833f9-e55c-4eaf-aba6-79f8b1d1a058
6fc151b0-81fb-46d4-a8a4-4cca0ca82e21	2016-01-12 14:52:30.241347	94db0a84-e366-4ab8-aeba-171482979f3d	689833f9-e55c-4eaf-aba6-79f8b1d1a058
\.


--
-- Data for Name: role_parameter; Type: TABLE DATA; Schema: reporting; Owner: report_server_app
--

COPY role_parameter (role_parameter_id, created_on, report_parameter_id, role_id) FROM stdin;
\.


--
-- Data for Name: role_parameter_value; Type: TABLE DATA; Schema: reporting; Owner: report_server_app
--

COPY role_parameter_value (role_parameter_value_id, boolean_value, created_on, date_value, datetime_value, float_value, integer_value, string_value, time_value, role_parameter_id) FROM stdin;
\.


--
-- Data for Name: role_report; Type: TABLE DATA; Schema: reporting; Owner: report_server_app
--

COPY role_report (role_report_id, created_on, report_id, role_id) FROM stdin;
\.


--
-- Data for Name: role_role; Type: TABLE DATA; Schema: reporting; Owner: report_server_app
--

COPY role_role (role_role_id, created_on, child_role_id, parent_role_id) FROM stdin;
3b2c7b99-cf00-43c6-85a7-f4e17bbea386	2016-01-12 14:52:30.241347	46e477dc-085f-4714-a24f-742428579fcc	54aa1d35-f67d-47e6-8bea-cadd6085796e
\.


--
-- Data for Name: selection_list_value; Type: TABLE DATA; Schema: reporting; Owner: report_server_app
--

COPY selection_list_value (selection_list_value_id, created_on, order_index, value_assigned, value_displayed, report_parameter_id) FROM stdin;
\.


--
-- Data for Name: subscription; Type: TABLE DATA; Schema: reporting; Owner: report_server_app
--

COPY subscription (subscription_id, active, created_on, delivery_cron_schedule, delivery_datetime_run_at, delivery_time_zone_id, description, email_address, enabled, document_format_id, report_version_id, role_id) FROM stdin;
\.


--
-- Data for Name: subscription_parameter; Type: TABLE DATA; Schema: reporting; Owner: report_server_app
--

COPY subscription_parameter (subscription_parameter_id, created_on, report_parameter_id, subscription_id) FROM stdin;
\.


--
-- Data for Name: subscription_parameter_value; Type: TABLE DATA; Schema: reporting; Owner: report_server_app
--

COPY subscription_parameter_value (subscription_parameter_value_id, boolean_value, created_on, date_value, datetime_value, day_of_month_number, day_of_week_in_month_number, day_of_week_in_month_ordinal, day_of_week_number, days_ago, duration_subtract_one_day_for_dates, duration_to_add_days, duration_to_add_hours, duration_to_add_minutes, duration_to_add_months, duration_to_add_seconds, duration_to_add_weeks, duration_to_add_years, float_value, integer_value, month_number, months_ago, string_value, time_value, weeks_ago, year_number, years_ago, subscription_parameter_id) FROM stdin;
\.


--
-- Name: authority_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY authority
    ADD CONSTRAINT authority_pkey PRIMARY KEY (authority_id);


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
-- Name: role_authority_pkey; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY role_authority
    ADD CONSTRAINT role_authority_pkey PRIMARY KEY (role_authority_id);


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
-- Name: uc_authority_name; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY authority
    ADD CONSTRAINT uc_authority_name UNIQUE (name);


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
-- Name: uc_reportversion_filename; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY report_version
    ADD CONSTRAINT uc_reportversion_filename UNIQUE (file_name);


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
-- Name: uc_roleauthority_role_authority; Type: CONSTRAINT; Schema: reporting; Owner: report_server_app; Tablespace: 
--

ALTER TABLE ONLY role_authority
    ADD CONSTRAINT uc_roleauthority_role_authority UNIQUE (role_id, authority_id);


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


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

