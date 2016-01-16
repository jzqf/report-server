BEGIN TRANSACTION;

-- Update the DB schema from v6 to v7:

-- Create table [file], plus its associated constraints:
XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX


-- Update the DB content from v6 to v7:




-- Update global configuration record for "DB_VERSION" to reflect the new version.
UPDATE reporting.configuration SET (integer_value, string_value) = (7, '7') WHERE param_name='DB_VERSION' AND role_id IS NULL;

--ROLLBACK;
COMMIT;