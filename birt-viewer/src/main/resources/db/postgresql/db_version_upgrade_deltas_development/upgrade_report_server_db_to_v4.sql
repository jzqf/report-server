BEGIN TRANSACTION;

-- Update the DB schema from v3 to v4:

-- Rename column "media_type" to "internet_media_type" instead of deleting 
-- column "media_type" and then creating column "internet_media_type". This is
-- so that we preserve the data in this column.
ALTER TABLE document_format RENAME COLUMN media_type TO internet_media_type;


-- Update the DB content from v3 to v4:

insert into reporting.job_status (job_status_id, description, abbreviation, active, created_on) values ('5125c537-e178-42de-b4dd-e538fa3da802', 'Canceled' , 'CANCELED' , true, current_timestamp AT TIME ZONE 'UTC');


-- Update global configuration record for "DB_VERSION" to reflect the new version.
UPDATE reporting.configuration SET (integer_value, string_value) = (4, '4') WHERE param_name='DB_VERSION' AND role_id IS NULL;

COMMIT;