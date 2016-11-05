BEGIN TRANSACTION;

-- Update the DB *schema* from v8 to v9:




-- Update the DB *content* from v8 to v9:



-- Update global configuration record for "DB_VERSION" to reflect the new version.
UPDATE reporting.configuration SET (integer_value, string_value) = (9, '9') WHERE param_name='DB_VERSION' AND role_id IS NULL;

--ROLLBACK;
COMMIT;