BEGIN TRANSACTION;

-- Update the DB schema from v4 to v5:

-- Rename column "email" to "email_address" instead of deleting 
-- column "email" and then creating column "email_address". This is
-- so that we preserve the data in this column.
ALTER TABLE job          RENAME COLUMN email TO email_address;
ALTER TABLE role         RENAME COLUMN email TO email_address;
ALTER TABLE subscription RENAME COLUMN email TO email_address;


-- Update the DB content from v4 to v5:

insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('24274a10-6d83-4f0f-a807-2c96d86ce5d6', 'Delivering' , 'DELIVERING' , true, current_timestamp AT TIME ZONE 'UTC');


-- Update global configuration record for "DB_VERSION" to reflect the new version.
UPDATE reporting.configuration SET (integer_value, string_value) = (5, '5') WHERE param_name='DB_VERSION' AND role_id IS NULL;

COMMIT;