BEGIN TRANSACTION;


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


insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('08de9764-735f-4c82-bbe9-3981b29cc133', 'Queued'    , 'QUEUED'    , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('a613aae2-836a-4b03-a75d-cfb8303eaad5', 'Running'   , 'RUNNING'   , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('24274a10-6d83-4f0f-a807-2c96d86ce5d6', 'Delivering', 'DELIVERING', true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('f378fc09-35e4-4096-b1d1-2db14756b098', 'Completed' , 'COMPLETED' , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('2a9cd697-af00-45bc-aa6a-053284b9d9e4', 'Failed'    , 'FAILED'    , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('5125c537-e178-42de-b4dd-e538fa3da802', 'Canceled'  , 'CANCELED'  , true, current_timestamp AT TIME ZONE 'UTC');


-- Create "authority" rows:
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('dae0f68f-11c6-438c-8312-aca4d95731fc', 'MANAGE_CATEGORIES'   , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('12b1cb22-7686-4c9b-b73b-a25d4cb31663', 'MANAGE_JOBS'         , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('ace1edd3-6a5b-4b40-a802-79616472b893', 'DELETE_JOBS'         , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('7b758de7-cd54-43fa-baa0-dfbe59e66000', 'MANAGE_REPORTS'      , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('2efd4eca-bcb2-4cec-b804-3142c8297d65', 'UPLOAD_REPORTS'      , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('2dac7af0-ba7d-4009-a313-e9a288272e90', 'MANAGE_ROLES'        , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('608d6156-b155-487d-bdd3-4e00260b7443', 'MANAGE_SUBSCRIPTIONS', true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('94db0a84-e366-4ab8-aeba-171482979f3d', 'DELETE_SUBSCRIPTIONS', true, current_timestamp AT TIME ZONE 'UTC');


-- Insert  tree of [role] records:
--
-- Admin role with login privileges. encoded_password is BCrypt.encode('reportadmin')
INSERT INTO reporting.role (role_id, username, login_role, encoded_password, full_name, email_address, time_zone_id, enabled, active, created_on) VALUES ('54aa1d35-f67d-47e6-8bea-cadd6085796e', 'reportadmin', true , '$2a$08$PjY.jVSaf0x2HCnETH6aj.0Gb3RWX9VMunMcpIfrJGBGjsYA2S6vC', 'Report Administrator', null                  , 'CET'           , true, true, current_timestamp AT TIME ZONE 'UTC');
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


-- Give the "Administrator" role all authorities by creating "role_authority" rows:
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('62c78032-5ec7-440c-8107-386b745b9ab9', '54aa1d35-f67d-47e6-8bea-cadd6085796e', 'dae0f68f-11c6-438c-8312-aca4d95731fc', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('222e6e22-f3e3-4631-847f-31896139987a', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '12b1cb22-7686-4c9b-b73b-a25d4cb31663', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('d3a80355-b464-4763-97ab-cd76af70b687', '54aa1d35-f67d-47e6-8bea-cadd6085796e', 'ace1edd3-6a5b-4b40-a802-79616472b893', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('8e90d278-a555-4a3a-981a-8e329eac63d4', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '7b758de7-cd54-43fa-baa0-dfbe59e66000', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('659e6aa3-14bf-4ba2-8630-538da94f6ffc', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '2efd4eca-bcb2-4cec-b804-3142c8297d65', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('2ef4aaf6-9ea4-48d0-a5a4-79670a264b79', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '2dac7af0-ba7d-4009-a313-e9a288272e90', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('fb5685ae-2470-449f-bfac-a2a291b863b4', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '608d6156-b155-487d-bdd3-4e00260b7443', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('13e14dbc-d30d-4006-95ed-b8de417a2315', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '94db0a84-e366-4ab8-aeba-171482979f3d', current_timestamp AT TIME ZONE 'UTC');


-- Create a global configuration record that contains the current database 
-- version. This will get updated at the database is upgraded over time. This
-- version number will be updated whenever the data model changes *or* Q-Free
-- supplied content changes (records are created, updated or deleted).
INSERT INTO reporting.configuration (param_name, role_id, param_type, integer_value, string_value , created_on) VALUES ('DB_VERSION', null, 'INTEGER', 5, '5', current_timestamp AT TIME ZONE 'UTC');

--ROLLBACK;
COMMIT;
