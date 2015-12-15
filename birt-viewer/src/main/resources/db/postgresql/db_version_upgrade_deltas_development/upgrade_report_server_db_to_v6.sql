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
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('1e4f29b9-3183-4f54-a4ee-96c2347d7e06', 'MANAGE_AUTHORITIES'  , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('dae0f68f-11c6-438c-8312-aca4d95731fc', 'MANAGE_CATEGORIES'   , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('cd2c5d93-9b57-4a8b-b789-84dd567e0fa2', 'MANAGE_FILEFORMATS'  , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('7e1ee8bb-3d5d-481a-b0f9-5c3fae3f4e38', 'MANAGE_FILESYNCING'  , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('e5b569e6-bb2a-4f00-a9ca-f85ef0a3a339', 'MANAGE_JOBPROCESSOR' , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('12b1cb22-7686-4c9b-b73b-a25d4cb31663', 'MANAGE_JOBS'         , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('ace1edd3-6a5b-4b40-a802-79616472b893', 'DELETE_JOBS'         , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('bb8f7964-504a-4544-8638-11a62cc9a2ca', 'MANAGE_JOBSTATUSES'  , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('1bbc9e73-1095-4471-bdb2-726b10e47936', 'MANAGE_PREFERENCES'  , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('7b758de7-cd54-43fa-baa0-dfbe59e66000', 'MANAGE_REPORTS'      , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('2efd4eca-bcb2-4cec-b804-3142c8297d65', 'UPLOAD_REPORTS'      , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('2dac7af0-ba7d-4009-a313-e9a288272e90', 'MANAGE_ROLES'        , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('608d6156-b155-487d-bdd3-4e00260b7443', 'MANAGE_SUBSCRIPTIONS', true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('94db0a84-e366-4ab8-aeba-171482979f3d', 'DELETE_SUBSCRIPTIONS', true, current_timestamp AT TIME ZONE 'UTC');


-- Create an "Administrator" role that can be used in a new installation to perform
-- administration tasks. encoded_password is BCrypt.encode('reportadmin'):
INSERT INTO reporting.role (role_id, username, login_role, encoded_password, full_name, email_address, time_zone_id, enabled, active, created_on) VALUES ('54aa1d35-f67d-47e6-8bea-cadd6085796e', 'reportadmin', true , '$2a$08$PjY.jVSaf0x2HCnETH6aj.0Gb3RWX9VMunMcpIfrJGBGjsYA2S6vC', 'Report Administrator', null                  , 'CET'           , true, true, current_timestamp AT TIME ZONE 'UTC');
--
-- Make exisiting user "user4" a child of "reportadmin", effectively making "user4" an administrator:
INSERT INTO reporting.role_role (role_role_id, parent_role_id, child_role_id, created_on) VALUES ('3b2c7b99-cf00-43c6-85a7-f4e17bbea386', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '46e477dc-085f-4714-a24f-742428579fcc', current_timestamp AT TIME ZONE 'UTC');
--
-- Give the "Administrator" role all authorities by creating "role_authority" rows:
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('f812aab5-2390-43f3-add3-a3d673939a73', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '1e4f29b9-3183-4f54-a4ee-96c2347d7e06', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('62c78032-5ec7-440c-8107-386b745b9ab9', '54aa1d35-f67d-47e6-8bea-cadd6085796e', 'dae0f68f-11c6-438c-8312-aca4d95731fc', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('eb8e56c2-7a8e-4dbe-97ca-a0e6166b258a', '54aa1d35-f67d-47e6-8bea-cadd6085796e', 'cd2c5d93-9b57-4a8b-b789-84dd567e0fa2', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('7560ef1c-4acf-43bd-af27-b5ad9f992f57', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '7e1ee8bb-3d5d-481a-b0f9-5c3fae3f4e38', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('44dd6566-a02c-47eb-b889-31016fe1d8ed', '54aa1d35-f67d-47e6-8bea-cadd6085796e', 'e5b569e6-bb2a-4f00-a9ca-f85ef0a3a339', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('222e6e22-f3e3-4631-847f-31896139987a', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '12b1cb22-7686-4c9b-b73b-a25d4cb31663', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('d3a80355-b464-4763-97ab-cd76af70b687', '54aa1d35-f67d-47e6-8bea-cadd6085796e', 'ace1edd3-6a5b-4b40-a802-79616472b893', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('69ee95d1-f6b0-4805-b653-e33ad3955609', '54aa1d35-f67d-47e6-8bea-cadd6085796e', 'bb8f7964-504a-4544-8638-11a62cc9a2ca', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('bbaddcf3-ef9c-49d1-9e5a-d9197a106b9e', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '1bbc9e73-1095-4471-bdb2-726b10e47936', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('8e90d278-a555-4a3a-981a-8e329eac63d4', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '7b758de7-cd54-43fa-baa0-dfbe59e66000', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('659e6aa3-14bf-4ba2-8630-538da94f6ffc', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '2efd4eca-bcb2-4cec-b804-3142c8297d65', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('2ef4aaf6-9ea4-48d0-a5a4-79670a264b79', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '2dac7af0-ba7d-4009-a313-e9a288272e90', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('fb5685ae-2470-449f-bfac-a2a291b863b4', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '608d6156-b155-487d-bdd3-4e00260b7443', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('13e14dbc-d30d-4006-95ed-b8de417a2315', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '94db0a84-e366-4ab8-aeba-171482979f3d', current_timestamp AT TIME ZONE 'UTC');
--
--
-- Create a "Q-Free administrator" role that can be used to bypass external
-- authentication by an authentication provider.
-- encoded_password is BCrypt.encode('qfreereportserveradmin_Af5Dj%4$'):
INSERT INTO reporting.role (role_id, username, login_role, encoded_password, full_name, email_address, time_zone_id, enabled, active, created_on) VALUES ('10ab3537-0b12-44fa-a27b-6cf1aac14282', 'qfree-reportserver-admin', true , '$2a$08$53v/RUtexw7Ovdx4i2F44O4XcyOLLZklf39XZW1C4jT3JjBJQ8fi6', 'Q-Free Administrator', null                  , 'CET'           , true, true, current_timestamp AT TIME ZONE 'UTC');
--
-- Give the "Q-Free administrator" role all authorities by creating "role_authority" rows:
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('4cb38206-bd6c-4db1-a596-0944b386c093', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '1e4f29b9-3183-4f54-a4ee-96c2347d7e06', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('e2ef968a-7fd6-4e9e-b74a-58fc6f208a50', '10ab3537-0b12-44fa-a27b-6cf1aac14282', 'dae0f68f-11c6-438c-8312-aca4d95731fc', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('143e65a8-3051-4512-bf02-940455f25744', '10ab3537-0b12-44fa-a27b-6cf1aac14282', 'cd2c5d93-9b57-4a8b-b789-84dd567e0fa2', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('e40a6bc3-3888-49ec-a9f5-6c17df4907a2', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '7e1ee8bb-3d5d-481a-b0f9-5c3fae3f4e38', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('6603e5e2-0b71-4e0c-8983-6c1c50efb0d6', '10ab3537-0b12-44fa-a27b-6cf1aac14282', 'e5b569e6-bb2a-4f00-a9ca-f85ef0a3a339', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('89bc2070-aedf-4959-9492-8f1a051f2c47', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '12b1cb22-7686-4c9b-b73b-a25d4cb31663', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('d806bc73-1744-471c-8f24-14af227f780b', '10ab3537-0b12-44fa-a27b-6cf1aac14282', 'ace1edd3-6a5b-4b40-a802-79616472b893', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('43c97218-f645-47be-ac64-7004e34ee31c', '10ab3537-0b12-44fa-a27b-6cf1aac14282', 'bb8f7964-504a-4544-8638-11a62cc9a2ca', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('bdb0077e-ddd1-4651-8584-7d67f6a5d7e6', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '1bbc9e73-1095-4471-bdb2-726b10e47936', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('f3f5546e-d819-4a4a-92be-c6ee150e02d0', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '7b758de7-cd54-43fa-baa0-dfbe59e66000', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('3d1392ab-b5b8-45b8-96e1-cf7060836924', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '2efd4eca-bcb2-4cec-b804-3142c8297d65', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('ce4ffcfd-7802-4e1b-bdf1-6d2b70062cef', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '2dac7af0-ba7d-4009-a313-e9a288272e90', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('54246034-83ed-488c-b447-05fd0b1e57b4', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '608d6156-b155-487d-bdd3-4e00260b7443', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('0fff6274-a9dc-4262-aa29-5feff9414810', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '94db0a84-e366-4ab8-aeba-171482979f3d', current_timestamp AT TIME ZONE 'UTC');
--
--
-- Create a role has full access to a ReST API endpoints. It will bypass 
-- external authentication by an authentication provider.
-- encoded_password is BCrypt.encode('ReportServer*RESTADMIN'):
INSERT INTO reporting.role (role_id, username, login_role, encoded_password, full_name, email_address, time_zone_id, enabled, active, created_on) VALUES ('689833f9-e55c-4eaf-aba6-79f8b1d1a058', 'reportserver-restadmin', true , '$2a$08$4sGkB9oqsz0ws5liaPYofem6WwiQzmZ6DzDP7cuvwWN24ycZZefbq', 'ReST Administrator', null                  , 'CET'           , true, true, current_timestamp AT TIME ZONE 'UTC');
--
-- Give the "ReST Administrator" role all authorities by creating "role_authority" rows:
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('9186ccf4-bfac-43fd-a200-7484f4d7b0b5', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', '1e4f29b9-3183-4f54-a4ee-96c2347d7e06', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('bfeb5c18-545e-4a47-b412-25df077bfc19', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', 'dae0f68f-11c6-438c-8312-aca4d95731fc', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('cc1392c8-bf80-474b-9cb8-e01e9ef067b6', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', 'cd2c5d93-9b57-4a8b-b789-84dd567e0fa2', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('43d33928-08c5-4869-bed0-4e81a3c8e0c3', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', '7e1ee8bb-3d5d-481a-b0f9-5c3fae3f4e38', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('3189a1d0-d7d1-45a5-a002-f34dda0c9916', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', 'e5b569e6-bb2a-4f00-a9ca-f85ef0a3a339', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('619b6650-57bd-4850-a543-9fd0d1a1d3bc', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', '12b1cb22-7686-4c9b-b73b-a25d4cb31663', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('eb257e2f-d6c3-497f-8e0c-614efc068a03', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', 'ace1edd3-6a5b-4b40-a802-79616472b893', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('b0fd8c8b-7d6d-4d4a-a165-b28b06ea3418', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', 'bb8f7964-504a-4544-8638-11a62cc9a2ca', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('25e8ab17-d66a-4de0-b692-dda9ff36c4ee', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', '1bbc9e73-1095-4471-bdb2-726b10e47936', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('dd5a9f19-0a8b-4ea9-885f-dcbda4c7431d', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', '7b758de7-cd54-43fa-baa0-dfbe59e66000', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('534d4b17-1ea6-48eb-8905-6798efe5011e', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', '2efd4eca-bcb2-4cec-b804-3142c8297d65', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('ca324426-78e9-4232-8ef0-46be70757b01', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', '2dac7af0-ba7d-4009-a313-e9a288272e90', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('e3f80441-e8ac-44e3-825a-a4ed0c8118ef', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', '608d6156-b155-487d-bdd3-4e00260b7443', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('6fc151b0-81fb-46d4-a8a4-4cca0ca82e21', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', '94db0a84-e366-4ab8-aeba-171482979f3d', current_timestamp AT TIME ZONE 'UTC');


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