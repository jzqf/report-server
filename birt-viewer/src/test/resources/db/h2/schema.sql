-- H2:

-- These commands are not necessary for an embedded database that is used for
-- testing because we create a new datbase on each run.
--DROP TABLE IF EXISTS reporting.report;
--DROP TABLE IF EXISTS reporting.report_category;
-- ...
--DROP SCHEMA IF EXISTS reporting;

CREATE SCHEMA reporting;

CREATE TABLE reporting.report (
    report_id uuid DEFAULT RANDOM_UUID() NOT NULL,
    created_on timestamp NOT NULL,
    name character varying(80) NOT NULL,
    rptdesign text NOT NULL,
    report_category_id uuid NOT NULL
);

CREATE TABLE reporting.report_category (
    report_category_id uuid DEFAULT RANDOM_UUID() NOT NULL,
    abbreviation character varying(32) NOT NULL,
    active boolean NOT NULL,
    description character varying(32) NOT NULL
);

CREATE TABLE reporting.report_parameter (
    report_parameter_id uuid DEFAULT RANDOM_UUID() NOT NULL,
    abbreviation character varying(32) NOT NULL,
    active boolean NOT NULL,
    description character varying(32) NOT NULL
);

CREATE TABLE reporting.role (
    role_id uuid DEFAULT RANDOM_UUID() NOT NULL,
    created_on timestamp NOT NULL,
    encoded_password character varying(32) NOT NULL,
    full_name character varying(32),
    login_role boolean NOT NULL,
    username character varying(32) NOT NULL
);

CREATE TABLE reporting.role_role (
    role_role_id uuid DEFAULT RANDOM_UUID() NOT NULL,
    created_on timestamp NOT NULL,
    child_role_id uuid NOT NULL,
    parent_role_id uuid NOT NULL
);

--
-- Name: report_category_pkey; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE reporting.report_category
    ADD CONSTRAINT report_category_pkey PRIMARY KEY (report_category_id);


--
-- Name: report_parameter_pkey; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE reporting.report_parameter
    ADD CONSTRAINT report_parameter_pkey PRIMARY KEY (report_parameter_id);


--
-- Name: report_pkey; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE reporting.report
    ADD CONSTRAINT report_pkey PRIMARY KEY (report_id);


--
-- Name: role_pkey; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE reporting.role
    ADD CONSTRAINT role_pkey PRIMARY KEY (role_id);


--
-- Name: role_role_pkey; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE reporting.role_role
    ADD CONSTRAINT role_role_pkey PRIMARY KEY (role_role_id);


--
-- Name: uc_role_role_parent_child; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE reporting.role_role
    ADD CONSTRAINT uc_role_role_parent_child UNIQUE (parent_role_id, child_role_id);


--
-- Name: uc_role_username; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE reporting.role
    ADD CONSTRAINT uc_role_username UNIQUE (username);


--
-- Name: fk_3t7udj2hlyu0k9deq1np51jui; Type: FK CONSTRAINT; Schema: reporting; Owner: dbtest
--

ALTER TABLE reporting.role_role
    ADD CONSTRAINT fk_3t7udj2hlyu0k9deq1np51jui FOREIGN KEY (child_role_id) REFERENCES role(role_id);


--
-- Name: fk_e3r9jheol1woc5jcnv8cxvlv; Type: FK CONSTRAINT; Schema: reporting; Owner: dbtest
--

ALTER TABLE reporting.report
    ADD CONSTRAINT fk_e3r9jheol1woc5jcnv8cxvlv FOREIGN KEY (report_category_id) REFERENCES report_category(report_category_id);


--
-- Name: fk_k5d94vtbmeqth523ke4fi64cc; Type: FK CONSTRAINT; Schema: reporting; Owner: dbtest
--

ALTER TABLE reporting.role_role
    ADD CONSTRAINT fk_k5d94vtbmeqth523ke4fi64cc FOREIGN KEY (parent_role_id) REFERENCES role(role_id);
