BEGIN TRANSACTION;

-- Update the DB schema from v1 to v2:

-- The DDL below does the following:
--
-- Update: [report_parameter]description --> [report_parameter]prompt_text
-- Remove: [widget]multiple_select

-- This is OK. We could have also used ALTER TABLE ... RENAME COLUMN ...
ALTER TABLE reporting.report_parameter DROP COLUMN IF EXISTS description CASCADE;
ALTER TABLE reporting.report_parameter ADD COLUMN prompt_text character varying(80) NOT NULL;

ALTER TABLE reporting.widget DROP COLUMN IF EXISTS multiple_select CASCADE;  -- <- DELETE WHEN WE DROP widget TABLE!!!!!!!!


-- Update the DB content from v1 to v2:


-- Update global configuration record for "DB_VERSION" to reflect the new version.
UPDATE configuration SET (integer_value, string_value) = (2, '2') WHERE param_name='DB_VERSION' AND role_id IS NULL;

COMMIT;