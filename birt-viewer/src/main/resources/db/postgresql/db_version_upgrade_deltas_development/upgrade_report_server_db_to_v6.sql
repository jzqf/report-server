BEGIN TRANSACTION;

-- Update the DB schema from v5 to v6:

-- Add column "enabled" to "role" table:
ALTER TABLE reporting.role ADD COLUMN enabled boolean NOT NULL DEFAULT true;
-- Remove the default value that was set in the previous line. This default 
-- value was specified only so that we would set a non-null value for the column
-- for existing rows of the table; otherwise, a constraint violation would 
-- occur.
ALTER TABLE reporting.role ALTER COLUMN enabled DROP DEFAULT;

-- Add column "active" to "role" table:
ALTER TABLE reporting.role ADD COLUMN active boolean NOT NULL DEFAULT true;
-- Remove the default value that was set in the previous line. This default 
-- value was specified only so that we would set a non-null value for the column
-- for existing rows of the table; otherwise, a constraint violation would 
-- occur.
ALTER TABLE reporting.role ALTER COLUMN active DROP DEFAULT;

-- Increase the length of the "encoded_password" column of the "role" table, as
-- well as making it nullable.
ALTER TABLE reporting.role ALTER COLUMN encoded_password DROP NOT NULL;
ALTER TABLE reporting.role ALTER COLUMN encoded_password TYPE character varying(64);

-- Make report_version column "file_name" unique.
ALTER TABLE reporting.report_version ADD CONSTRAINT uc_reportversion_filename UNIQUE (file_name);


-- Create "authority" table:
-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX


-- Create "role_authority" table:
-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX



-- Update the DB content from v5 to v6:


-- Create "authority" rows:
-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX


-- Create "role_authority" rows:
-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX


-- Create an "Administrator" role that can be used in a new installation to 
-- perform administration tasks:
-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX


-- Give the "Administrator" role all authorities:
-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX


-- Set new values for role.encoded_password for existing role records that have
-- been computed using Spring Security's BCrypt password encoder.
--
-- role "user1". Unencoded password = "password1".
UPDATE reporting.role SET encoded_password='$2a$08$SIVVleb01XxPHvn72ThPo.NAgKrbMuq5r/JvcWj5g6LIPDqnuVeT2' WHERE role_id='29fe8a1f-7826-4df0-8bfd-151b54198655';
-- role "user2". Unencoded password = "password2".
UPDATE reporting.role SET encoded_password='$2a$08$SjqsEsbb86MU/eggjgjfuuvQ0WnDhKm1oJ9IJn/uc04J51ksXNbN6' WHERE role_id='fa06393a-d341-4bf6-b047-1a8c6a383483';
-- role "user3". Unencoded password = "password3".
UPDATE reporting.role SET encoded_password='$2a$08$qmN5yWFDL9MT8kjftuVeguljomDvAMkSDaFnw9oSwKdV0RHmNg5TG' WHERE role_id='b85fd129-17d9-40e7-ac11-7541040f8627';
-- role "user4". Unencoded password = "password4".
UPDATE reporting.role SET encoded_password='$2a$08$Co4iHF1kcy1j3r7nd6hBgOAJUgDKaIitNx.BTW8gbVukqwRZr6MLu' WHERE role_id='46e477dc-085f-4714-a24f-742428579fcc';


-- Update global configuration record for "DB_VERSION" to reflect the new version.
UPDATE reporting.configuration SET (integer_value, string_value) = (6, '6') WHERE param_name='DB_VERSION' AND role_id IS NULL;

--ROLLBACK;
COMMIT;