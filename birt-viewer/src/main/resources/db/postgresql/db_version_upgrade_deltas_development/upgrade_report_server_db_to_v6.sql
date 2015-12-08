BEGIN TRANSACTION;

-- Update the DB schema from v5 to v6:



-- Update the DB content from v5 to v6:




-- Update global configuration record for "DB_VERSION" to reflect the new version.
UPDATE reporting.configuration SET (integer_value, string_value) = (6, '6') WHERE param_name='DB_VERSION' AND role_id IS NULL;

COMMIT;