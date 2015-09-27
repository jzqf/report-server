BEGIN TRANSACTION;

-- Update the DB schema from v2 to v3:



-- Update the DB content from v2 to v3:

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


-- Update global configuration record for "DB_VERSION" to reflect the new version.
UPDATE configuration SET (integer_value, string_value) = (3, '3') WHERE param_name='DB_VERSION' AND role_id IS NULL;

COMMIT;