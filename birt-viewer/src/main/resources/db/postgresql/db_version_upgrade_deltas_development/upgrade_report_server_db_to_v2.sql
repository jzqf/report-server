BEGIN TRANSACTION;

-- Update the DB schema from v1 to v2:



-- Update the DB content from v1 to v2:



-- Update global configuration record for "db version" to reflect the new version.
UPDATE configuration SET (integer_value, string_value) = (2, '2') WHERE param_name='db version' AND role_id IS NULL;

COMMIT;