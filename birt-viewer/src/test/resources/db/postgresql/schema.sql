-- PostgreSQL:

DROP TABLE IF EXISTS report;
DROP TABLE IF EXISTS report_category;
DROP TABLE IF EXISTS report_parameter;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS role_role;

CREATE TABLE report (
    report_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    name character varying(80) NOT NULL,
    rptdesign text NOT NULL,
    report_category_id uuid NOT NULL
);

CREATE TABLE report_category (
    report_category_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    abbreviation character varying(32) NOT NULL,
    active boolean NOT NULL,
    description character varying(32) NOT NULL
);

CREATE TABLE report_parameter (
    report_parameter_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    abbreviation character varying(32) NOT NULL,
    active boolean NOT NULL,
    description character varying(32) NOT NULL
);

CREATE TABLE role (
    role_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    encoded_password character varying(32) NOT NULL,
    full_name character varying(32),
    login_role boolean NOT NULL,
    username character varying(32) NOT NULL
);

CREATE TABLE role_role (
    role_role_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    created_on timestamp without time zone NOT NULL,
    child_role_id uuid NOT NULL,
    parent_role_id uuid NOT NULL
);

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
-- Name: role_pkey; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY role
    ADD CONSTRAINT role_pkey PRIMARY KEY (role_id);


--
-- Name: role_role_pkey; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY role_role
    ADD CONSTRAINT role_role_pkey PRIMARY KEY (role_role_id);


--
-- Name: uc_role_role_parent_child; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY role_role
    ADD CONSTRAINT uc_role_role_parent_child UNIQUE (parent_role_id, child_role_id);


--
-- Name: uc_role_username; Type: CONSTRAINT; Schema: reporting; Owner: dbtest; Tablespace: 
--

ALTER TABLE ONLY role
    ADD CONSTRAINT uc_role_username UNIQUE (username);


--
-- Name: fk_3t7udj2hlyu0k9deq1np51jui; Type: FK CONSTRAINT; Schema: reporting; Owner: dbtest
--

ALTER TABLE ONLY role_role
    ADD CONSTRAINT fk_3t7udj2hlyu0k9deq1np51jui FOREIGN KEY (child_role_id) REFERENCES role(role_id);


--
-- Name: fk_e3r9jheol1woc5jcnv8cxvlv; Type: FK CONSTRAINT; Schema: reporting; Owner: dbtest
--

ALTER TABLE ONLY report
    ADD CONSTRAINT fk_e3r9jheol1woc5jcnv8cxvlv FOREIGN KEY (report_category_id) REFERENCES report_category(report_category_id);


--
-- Name: fk_k5d94vtbmeqth523ke4fi64cc; Type: FK CONSTRAINT; Schema: reporting; Owner: dbtest
--

ALTER TABLE ONLY role_role
    ADD CONSTRAINT fk_k5d94vtbmeqth523ke4fi64cc FOREIGN KEY (parent_role_id) REFERENCES role(role_id);
