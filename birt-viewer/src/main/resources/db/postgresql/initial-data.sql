BEGIN TRANSACTION;


--insert into reporting.parameter_type (parameter_type_id, description, abbreviation, active, created_on) values ('bfa09b13-ad55-481e-8c29-b047dc5d7f3e', 'Boolean (True/False)'   , 'boolean'  , true, '2015-05-01T12:00:01');
--insert into reporting.parameter_type (parameter_type_id, description, abbreviation, active, created_on) values ('12d3f4f8-468d-4faf-be3a-5c15eaba4eb6', 'Date (no time)'         , 'date'     , true, '2015-05-01T12:00:01');
--insert into reporting.parameter_type (parameter_type_id, description, abbreviation, active, created_on) values ('abce5a38-b1e9-42a3-9962-19227d51dd4a', 'Datetime (date + time)' , 'datetime' , true, '2015-05-01T12:00:01');
--insert into reporting.parameter_type (parameter_type_id, description, abbreviation, active, created_on) values ('f2bfa3f9-f446-49dd-ad0e-6a02b3af1023', 'Decimal'                , 'decimal'  , true, '2015-05-01T12:00:01');
--insert into reporting.parameter_type (parameter_type_id, description, abbreviation, active, created_on) values ('8b0bfc37-5fb4-4dea-87fc-3e2c3313af17', 'Float'                  , 'float'    , true, '2015-05-01T12:00:01');
--insert into reporting.parameter_type (parameter_type_id, description, abbreviation, active, created_on) values ('807c64b1-a59b-465c-998b-a399984b5ef4', 'Integer'                , 'integer'  , true, '2015-05-01T12:00:01');
--insert into reporting.parameter_type (parameter_type_id, description, abbreviation, active, created_on) values ('9b0af697-8bc9-49e2-b8b6-136ced83dbd8', 'String'                 , 'string'   , true, '2015-05-01T12:00:01');
--insert into reporting.parameter_type (parameter_type_id, description, abbreviation, active, created_on) values ('da575eee-e5a3-4149-8ea3-1fd86015bbb9', 'Time'                   , 'time'     , true, '2015-05-01T12:00:01');


--insert into reporting.document_format (document_format_id, name, file_extension, media_type, binary_data, birt_format, active, created_on) values ('bc5169e0-3d36-483c-a7b5-a76766587991', 'Microsoft Word'               , 'doc' , 'application/msword'                                                       , true , 'doc' , true , '2015-06-30T11:59:00');
--insert into reporting.document_format (document_format_id, name, file_extension, media_type, binary_data, birt_format, active, created_on) values ('d0225349-1642-46e3-a949-4ce39795907f', 'Office Open XML Document'     , 'docx', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'  , true , 'docx', true , '2015-06-30T11:59:00');
--insert into reporting.document_format (document_format_id, name, file_extension, media_type, binary_data, birt_format, active, created_on) values ('e1d0b3f2-f639-4521-a055-d5465dce29a2', 'HTML'                         , 'html', 'text/html'                                                                , false, 'html', false, '2015-06-30T11:59:00');
--insert into reporting.document_format (document_format_id, name, file_extension, media_type, binary_data, birt_format, active, created_on) values ('38b73b21-cb66-42cf-932b-1cdf7937525c', 'OpenDocument Presentation'    , 'odp' , 'application/vnd.oasis.opendocument.presentation'                          , true , 'odp' , true , '2015-06-30T11:59:00');
--insert into reporting.document_format (document_format_id, name, file_extension, media_type, binary_data, birt_format, active, created_on) values ('05a4ad8d-6f30-4d6d-83d5-995345a8dc58', 'OpenDocument Spreadsheet'     , 'ods' , 'application/vnd.oasis.opendocument.spreadsheet'                           , true , 'ods' , true , '2015-06-30T11:59:00');
--insert into reporting.document_format (document_format_id, name, file_extension, media_type, binary_data, birt_format, active, created_on) values ('b4f2249d-f52e-47e2-871c-daf35f4ba78e', 'OpenDocument Text'            , 'odt' , 'application/vnd.oasis.opendocument.text'                                  , true , 'odt' , true , '2015-06-30T11:59:00');
--insert into reporting.document_format (document_format_id, name, file_extension, media_type, binary_data, birt_format, active, created_on) values ('30800d77-5fdd-44bc-94a3-1502bd307c1d', 'PDF'                          , 'pdf' , 'application/pdf'                                                          , true , 'pdf' , true , '2015-06-30T11:59:00');
--insert into reporting.document_format (document_format_id, name, file_extension, media_type, binary_data, birt_format, active, created_on) values ('597f34fb-10d8-4408-971a-1b67472ac588', 'PowerPoint'                   , 'ppt' , 'application/vnd.ms-powerpoint'                                            , true , 'ppt' , true , '2015-06-30T11:59:00');
--insert into reporting.document_format (document_format_id, name, file_extension, media_type, binary_data, birt_format, active, created_on) values ('d7ccb194-91c6-4dce-bbfe-6424f079dc07', 'Office Open XML Presentation' , 'pptx', 'application/vnd.openxmlformats-officedocument.presentationml.presentation', true , 'pptx', true , '2015-06-30T11:59:00');
--insert into reporting.document_format (document_format_id, name, file_extension, media_type, binary_data, birt_format, active, created_on) values ('25762ba8-1688-4100-b323-b9e74eba396c', 'Microsoft Excel'              , 'xls' , 'application/vnd.ms-excel'                                                 , true , 'xls' , true , '2015-06-30T11:59:00');
--insert into reporting.document_format (document_format_id, name, file_extension, media_type, binary_data, birt_format, active, created_on) values ('c78ac922-2f37-4855-83ae-b708d453b005', 'Office Open XML Workbook'     , 'xlsx', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'        , true , 'xlsx', true , '2015-06-30T11:59:00');


insert into reporting.report_category (report_category_id, description, abbreviation, active, created_on) values ('7a482694-51d2-42d0-b0e2-19dd13bbbc64', 'Accounting'       , 'ACCT' , true, '2015-04-30T12:00:00');
insert into reporting.report_category (report_category_id, description, abbreviation, active, created_on) values ('bb2bc482-c19a-4c19-a087-e68ffc62b5a0', 'Q-Free internal'  , 'QFREE', true , '2015-05-30T22:00:00');
insert into reporting.report_category (report_category_id, description, abbreviation, active, created_on) values ('5c3cc664-b685-4f6e-8d9a-2927c6bcffdc', 'Manual validation', 'MIR'  , true, '2015-03-01T02:00:00');
insert into reporting.report_category (report_category_id, description, abbreviation, active, created_on) values ('72d7cb27-1770-4cc7-b301-44d39ccf1e76', 'Traffic'          , 'TRA'  , true , '2015-03-01T02:00:10');


--insert into reporting.widget (widget_id, name, description, multiple_select ,active, created_on ) values ('b8e91527-8b0e-4ed2-8cba-8cb8989ba8e2', 'Checkbox', 'Checkbox widget', false, true, '2015-03-31T02:00:00');
--insert into reporting.widget (widget_id, name, description, multiple_select ,active, created_on ) values ('e5b4cebb-1852-41a1-9fdf-bb4b8da82ef9', 'String entry', 'String entry widget', false, true, '2015-03-31T02:00:00');


-- Insert  tree of [role] records:
--
-- encoded_password '44rSFJQ9qtHWTBAvrsKd5K/p2j0=' is Base64(SHA-1('password1'))
INSERT INTO reporting.role (role_id, username, login_role, encoded_password, full_name, created_on) VALUES ('29fe8a1f-7826-4df0-8bfd-151b54198655', 'user1', true , '44rSFJQ9qtHWTBAvrsKd5K/p2j0=', 'User Number 1', '2015-05-07T09:10:00');
-- encoded_password 'KqYKj/f81HPTIeAUav2eJt85UUc=' is Base64(SHA-1('password2'))
INSERT INTO reporting.role (role_id, username, login_role, encoded_password, full_name, created_on) VALUES ('fa06393a-d341-4bf6-b047-1a8c6a383483', 'user2', false, 'KqYKj/f81HPTIeAUav2eJt85UUc=', 'User Number 2', '2015-05-07T09:12:01');
-- encoded_password 'ERnP037iRzV+A0oI2ETuol9v0g8=' is Base64(SHA-1('password3'))
INSERT INTO reporting.role (role_id, username, login_role, encoded_password, full_name, created_on) VALUES ('b85fd129-17d9-40e7-ac11-7541040f8627', 'user3', true , 'ERnP037iRzV+A0oI2ETuol9v0g8=', 'User Number 3', '2015-05-07T09:13:22');
-- encoded_password 'oddYTarKRzjUma1wgohrARFyddg=' is Base64(SHA-1('password4'))
INSERT INTO reporting.role (role_id, username, login_role, encoded_password, full_name, created_on) VALUES ('46e477dc-085f-4714-a24f-742428579fcc', 'user4', true , 'oddYTarKRzjUma1wgohrARFyddg=', 'User Number 4', '2015-05-07T09:14:09');


-- Create a global configuration record that contains the current database 
-- version. This will get updated at the database is upgraded over time. This
-- version number will be updated whenever the data model changes *or* Q-Free
-- supplied content changes (records are created, updated or deleted).
INSERT INTO reporting.configuration (param_name, role_id, param_type, integer_value, string_value , created_on) VALUES ('db version', null, 'INTEGER', 1, '1', current_timestamp AT TIME ZONE 'UTC');

COMMIT;
