BEGIN TRANSACTION;

-- Update the DB schema from v4 to v5:



-- Update the DB content from v4 to v5:

--insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('5125c537-e178-42de-b4dd-e538fa3da802', 'Canceled' , 'CANCELED' , true, current_timestamp AT TIME ZONE 'UTC');


-- Update global configuration record for "DB_VERSION" to reflect the new version.
UPDATE reporting.configuration SET (integer_value, string_value) = (5, '5') WHERE param_name='DB_VERSION' AND role_id IS NULL;

COMMIT;