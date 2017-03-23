BEGIN TRANSACTION;

-- Update the DB *schema* from v8 to v9:




-- Update the DB *content* from v8 to v9:

insert into reporting.report_category (report_category_id, description, abbreviation, active, created_on) values ('0a8f19bd-b710-4cd5-b7aa-440f3fe644cf', 'Service Level Agreement', 'SLA'  , true, current_timestamp AT TIME ZONE 'UTC');

insert into reporting.asset_type (asset_type_id, name, abbreviation, directory, active, created_on) values ('bbeaf653-2c06-4128-a49d-1d42f13cc443', 'JAR file'        , 'JAR'       , 'jars'       , true, current_timestamp AT TIME ZONE 'UTC');


-- Update global configuration record for "DB_VERSION" to reflect the new version.
UPDATE reporting.configuration SET (integer_value, string_value) = (9, '9') WHERE param_name='DB_VERSION' AND role_id IS NULL;

--ROLLBACK;
COMMIT;