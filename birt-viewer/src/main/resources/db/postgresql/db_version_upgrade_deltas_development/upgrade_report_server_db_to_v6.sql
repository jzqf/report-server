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
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('dae0f68f-11c6-438c-8312-aca4d95731fc', 'MANAGE_CATEGORIES'   , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('12b1cb22-7686-4c9b-b73b-a25d4cb31663', 'MANAGE_JOBS'         , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('ace1edd3-6a5b-4b40-a802-79616472b893', 'DELETE_JOBS'         , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('7b758de7-cd54-43fa-baa0-dfbe59e66000', 'MANAGE_REPORTS'      , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('2efd4eca-bcb2-4cec-b804-3142c8297d65', 'UPLOAD_REPORTS'      , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('2dac7af0-ba7d-4009-a313-e9a288272e90', 'MANAGE_ROLES'        , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('608d6156-b155-487d-bdd3-4e00260b7443', 'MANAGE_SUBSCRIPTIONS', true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('94db0a84-e366-4ab8-aeba-171482979f3d', 'DELETE_SUBSCRIPTIONS', true, current_timestamp AT TIME ZONE 'UTC');


-- Create an "Administrator" role that can be used in a new installation to perform
-- administration tasks. encoded_password is BCrypt.encode('reportadmin'):
INSERT INTO reporting.role (role_id, username, login_role, encoded_password, full_name, email_address, time_zone_id, enabled, active, created_on) VALUES ('54aa1d35-f67d-47e6-8bea-cadd6085796e', 'reportadmin', true , '$2a$08$PjY.jVSaf0x2HCnETH6aj.0Gb3RWX9VMunMcpIfrJGBGjsYA2S6vC', 'Report Administrator', null                  , 'CET'           , true, true, current_timestamp AT TIME ZONE 'UTC');


-- Give the "Administrator" role all authorities by creating "role_authority" rows:
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('62c78032-5ec7-440c-8107-386b745b9ab9', '54aa1d35-f67d-47e6-8bea-cadd6085796e', 'dae0f68f-11c6-438c-8312-aca4d95731fc', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('222e6e22-f3e3-4631-847f-31896139987a', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '12b1cb22-7686-4c9b-b73b-a25d4cb31663', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('d3a80355-b464-4763-97ab-cd76af70b687', '54aa1d35-f67d-47e6-8bea-cadd6085796e', 'ace1edd3-6a5b-4b40-a802-79616472b893', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('8e90d278-a555-4a3a-981a-8e329eac63d4', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '7b758de7-cd54-43fa-baa0-dfbe59e66000', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('659e6aa3-14bf-4ba2-8630-538da94f6ffc', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '2efd4eca-bcb2-4cec-b804-3142c8297d65', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('2ef4aaf6-9ea4-48d0-a5a4-79670a264b79', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '2dac7af0-ba7d-4009-a313-e9a288272e90', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('fb5685ae-2470-449f-bfac-a2a291b863b4', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '608d6156-b155-487d-bdd3-4e00260b7443', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('13e14dbc-d30d-4006-95ed-b8de417a2315', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '94db0a84-e366-4ab8-aeba-171482979f3d', current_timestamp AT TIME ZONE 'UTC');


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