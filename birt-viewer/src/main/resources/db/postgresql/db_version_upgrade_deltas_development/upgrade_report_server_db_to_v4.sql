BEGIN TRANSACTION;

-- Update the DB schema from v3 to v4:



-- Update the DB content from v3 to v4:



-- Update global configuration record for "DB_VERSION" to reflect the new version.
UPDATE reporting.configuration SET (integer_value, string_value) = (4, '4') WHERE param_name='DB_VERSION' AND role_id IS NULL;

COMMIT;