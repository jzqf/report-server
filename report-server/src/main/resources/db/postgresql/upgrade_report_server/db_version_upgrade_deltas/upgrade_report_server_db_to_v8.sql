BEGIN TRANSACTION;

-- Update the DB *schema* from v7 to v8:

-- Add columns:
--   custom_report_parameter_{1,8}_name  and
--   custom_report_parameter_{1,8}_value
-- to the subscription table:
ALTER TABLE reporting.subscription ADD COLUMN custom_report_parameter_1_name  varchar(64) NULL;
ALTER TABLE reporting.subscription ADD COLUMN custom_report_parameter_1_value varchar(64) NULL;
--
ALTER TABLE reporting.subscription ADD COLUMN custom_report_parameter_2_name  varchar(64) NULL;
ALTER TABLE reporting.subscription ADD COLUMN custom_report_parameter_2_value varchar(64) NULL;
--
ALTER TABLE reporting.subscription ADD COLUMN custom_report_parameter_3_name  varchar(64) NULL;
ALTER TABLE reporting.subscription ADD COLUMN custom_report_parameter_3_value varchar(64) NULL;
--
ALTER TABLE reporting.subscription ADD COLUMN custom_report_parameter_4_name  varchar(64) NULL;
ALTER TABLE reporting.subscription ADD COLUMN custom_report_parameter_4_value varchar(64) NULL;
--
ALTER TABLE reporting.subscription ADD COLUMN custom_report_parameter_5_name  varchar(64) NULL;
ALTER TABLE reporting.subscription ADD COLUMN custom_report_parameter_5_value varchar(64) NULL;
--
ALTER TABLE reporting.subscription ADD COLUMN custom_report_parameter_6_name  varchar(64) NULL;
ALTER TABLE reporting.subscription ADD COLUMN custom_report_parameter_6_value varchar(64) NULL;
--
ALTER TABLE reporting.subscription ADD COLUMN custom_report_parameter_7_name  varchar(64) NULL;
ALTER TABLE reporting.subscription ADD COLUMN custom_report_parameter_7_value varchar(64) NULL;
--
ALTER TABLE reporting.subscription ADD COLUMN custom_report_parameter_8_name  varchar(64) NULL;
ALTER TABLE reporting.subscription ADD COLUMN custom_report_parameter_8_value varchar(64) NULL;



-- Update the DB *content* from v7 to v8:



-- Update global configuration record for "DB_VERSION" to reflect the new version.
UPDATE reporting.configuration SET (integer_value, string_value) = (8, '8') WHERE param_name='DB_VERSION' AND role_id IS NULL;

--ROLLBACK;
COMMIT;