BEGIN TRANSACTION;

-- Update the DB *schema* from v9 to v10:




-- Update the DB *content* from v9 to v10:




-- Update global configuration record for "DB_VERSION" to reflect the new version.
UPDATE reporting.configuration SET (integer_value, string_value) = (10, '10') WHERE param_name='DB_VERSION' AND role_id IS NULL;

--ROLLBACK;
COMMIT;