BEGIN TRANSACTION;

-- Update the DB schema from v2 to v3:



-- Update the DB content from v2 to v3:



-- Update global configuration record for "DB_VERSION" to reflect the new version.
UPDATE configuration SET (integer_value, string_value) = (3, '3') WHERE param_name='DB_VERSION' AND role_id IS NULL;

COMMIT;