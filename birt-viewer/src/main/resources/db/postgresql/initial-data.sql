BEGIN TRANSACTION;


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


insert into reporting.report_category (report_category_id, description, abbreviation, active, created_on) values ('7a482694-51d2-42d0-b0e2-19dd13bbbc64', 'Accounting'       , 'ACCT' , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.report_category (report_category_id, description, abbreviation, active, created_on) values ('bb2bc482-c19a-4c19-a087-e68ffc62b5a0', 'Q-Free internal'  , 'QFREE', true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.report_category (report_category_id, description, abbreviation, active, created_on) values ('5c3cc664-b685-4f6e-8d9a-2927c6bcffdc', 'Manual validation', 'MIR'  , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.report_category (report_category_id, description, abbreviation, active, created_on) values ('72d7cb27-1770-4cc7-b301-44d39ccf1e76', 'Traffic'          , 'TRA'  , true, current_timestamp AT TIME ZONE 'UTC');


insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('08de9764-735f-4c82-bbe9-3981b29cc133', 'Queued'   , 'QUEUED'   , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('a613aae2-836a-4b03-a75d-cfb8303eaad5', 'Running'  , 'RUNNING'  , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('f378fc09-35e4-4096-b1d1-2db14756b098', 'Completed', 'COMPLETED', true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('2a9cd697-af00-45bc-aa6a-053284b9d9e4', 'Failed'   , 'FAILED'   , true, current_timestamp AT TIME ZONE 'UTC');


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
INSERT INTO reporting.configuration (param_name, role_id, param_type, integer_value, string_value , created_on) VALUES ('DB_VERSION', null, 'INTEGER', 2, '2', current_timestamp AT TIME ZONE 'UTC');

COMMIT;
