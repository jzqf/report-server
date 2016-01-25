BEGIN TRANSACTION;

-- Update the DB schema from v7 to v8:



-- Update the DB content from v7 to v8:




-- Update global configuration record for "DB_VERSION" to reflect the new version.
UPDATE reporting.configuration SET (integer_value, string_value) = (8, '8') WHERE param_name='DB_VERSION' AND role_id IS NULL;

--ROLLBACK;
COMMIT;