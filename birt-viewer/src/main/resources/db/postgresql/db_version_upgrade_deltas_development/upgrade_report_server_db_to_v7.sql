BEGIN TRANSACTION;

-- Update the DB schema from v6 to v7:

-- Create table [Document], plus its associated constraints (foreign key, unique):
XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

-- Create table [AssetTree], plus its associated constraints (foreign key, unique):
XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

-- Create table [AssetType], plus its associated constraints (foreign key, unique):
XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

-- Create table [Asset], plus its associated constraints (foreign key, unique):
XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX


-- Update the DB content from v6 to v7:

-- Create [asset_tree] rows:
--
-- "qfree" directory for assets used by Q-Free-written reports.
insert into reporting.asset_tree (asset_tree_id, name, abbreviation, directory, active, created_on) values ('7f9d0216-48d7-49ba-b043-ec48db03c938', 'Q-Free'  , 'QFREE', 'qfree' , true, current_timestamp AT TIME ZONE 'UTC');
-- "assets" directory for assets used by customer-written reports.
insert into reporting.asset_tree (asset_tree_id, name, abbreviation, directory, active, created_on) values ('272199f9-d407-492f-a147-41a2b7d0cd02', 'Customer', 'CUST' , 'assets', true, current_timestamp AT TIME ZONE 'UTC');
 

-- Create [asset_type] rows:
insert into reporting.asset_type (asset_type_id, name, abbreviation, directory, active, created_on) values ('f2f4b13f-9c45-4515-bb4b-62e8ccc3d95c', 'CSS file'        , 'CSS'       , 'css'        , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.asset_type (asset_type_id, name, abbreviation, directory, active, created_on) values ('2603ed74-94b5-4c39-ad36-a7e16a374237', 'HTML page'       , 'HTML'      , 'html'       , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.asset_type (asset_type_id, name, abbreviation, directory, active, created_on) values ('1e7ddbbc-8b40-4373-bfc5-6e6d3d5964d8', 'Image file'      , 'IMAGE'     , 'images'     , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.asset_type (asset_type_id, name, abbreviation, directory, active, created_on) values ('1c488bcf-15ab-4fcb-8ab8-23cda9342a77', 'JavaScript file' , 'JS'        , 'js'         , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.asset_type (asset_type_id, name, abbreviation, directory, active, created_on) values ('26835616-00e2-475d-89ce-11c12e659605', 'BIRT Library'    , 'BIRT LIB'  , 'libraries'  , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.asset_type (asset_type_id, name, abbreviation, directory, active, created_on) values ('4b811357-d664-4e44-bc75-197e6946abcd', 'Properties file' , 'PROPERTIES', 'properties' , true, current_timestamp AT TIME ZONE 'UTC');
 

-- Add new [Authority] rows:
--
-- The authorities 'MANAGE_ASSETTREES', 'MANAGE_ASSETTYPES' & 'MANAGE_DOCUMENTS'
-- are set to "inactive" so that they will not be available to be granted to roles.
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('b33f1d5c-170a-4737-ae20-eb9e7aa42d04', 'MANAGE_ASSETS'       , true, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('e5d86ab9-c660-4bb6-8b96-4b75bd59a5d8', 'MANAGE_ASSETTREES'   , false, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('2c68aeeb-af3b-4f7a-8ecf-6ed11252ec23', 'MANAGE_ASSETTYPES'   , false, current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.authority (authority_id, name, active, created_on) VALUES ('d2717c9b-b7ab-4085-a169-b95ede048c4c', 'MANAGE_DOCUMENTS'    , false, current_timestamp AT TIME ZONE 'UTC');

-- Grant these new [Authority] rows, as appropriate:
--
-- Grant 'MANAGE_ASSETS' to "reportadmin":
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('71ead393-41d2-4dec-b983-aef26ef62827', '54aa1d35-f67d-47e6-8bea-cadd6085796e', 'b33f1d5c-170a-4737-ae20-eb9e7aa42d04', current_timestamp AT TIME ZONE 'UTC');
--INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('5c3f8ac9-0dd1-4ffd-b840-63146e469b78', '54aa1d35-f67d-47e6-8bea-cadd6085796e', 'e5d86ab9-c660-4bb6-8b96-4b75bd59a5d8', current_timestamp AT TIME ZONE 'UTC');
--INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('13680bc1-19c3-4dff-9a9e-720f0ab65ff9', '54aa1d35-f67d-47e6-8bea-cadd6085796e', '2c68aeeb-af3b-4f7a-8ecf-6ed11252ec23', current_timestamp AT TIME ZONE 'UTC');
--INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('5099855c-4ea5-4203-b9c7-972995b72c66', '54aa1d35-f67d-47e6-8bea-cadd6085796e', 'd2717c9b-b7ab-4085-a169-b95ede048c4c', current_timestamp AT TIME ZONE 'UTC');
--
-- Grant 'MANAGE_ASSETS', 'MANAGE_ASSETTREES', 'MANAGE_ASSETTYPES' & 'MANAGE_DOCUMENTS' to "qfree-reportserver-admin":
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('ef7b2b61-8bfc-4a14-8b41-718a12b115c6', '10ab3537-0b12-44fa-a27b-6cf1aac14282', 'b33f1d5c-170a-4737-ae20-eb9e7aa42d04', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('faac077d-af19-4e00-97da-7c0bf71f07e8', '10ab3537-0b12-44fa-a27b-6cf1aac14282', 'e5d86ab9-c660-4bb6-8b96-4b75bd59a5d8', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('37aaf7d3-1a9f-4e1b-8707-1aaf3ac5bd87', '10ab3537-0b12-44fa-a27b-6cf1aac14282', '2c68aeeb-af3b-4f7a-8ecf-6ed11252ec23', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('7bcdf522-dac7-4e2a-bc42-6d6872a8391d', '10ab3537-0b12-44fa-a27b-6cf1aac14282', 'd2717c9b-b7ab-4085-a169-b95ede048c4c', current_timestamp AT TIME ZONE 'UTC');
--
-- Grant 'MANAGE_ASSETS', 'MANAGE_ASSETTREES', 'MANAGE_ASSETTYPES' & 'MANAGE_DOCUMENTS' to "reportserver-restadmin":
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('aec93434-b0b4-432a-b760-57ff6641564a', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', 'b33f1d5c-170a-4737-ae20-eb9e7aa42d04', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('62ebb315-0fcb-49ad-84ac-469adc36f2ef', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', 'e5d86ab9-c660-4bb6-8b96-4b75bd59a5d8', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('2f74ad0b-a447-4012-8816-d1e234b5b3ee', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', '2c68aeeb-af3b-4f7a-8ecf-6ed11252ec23', current_timestamp AT TIME ZONE 'UTC');
INSERT INTO reporting.role_authority (role_authority_id, role_id, authority_id, created_on) VALUES ('f1416e42-5aff-4696-9e90-146a0a548b8b', '689833f9-e55c-4eaf-aba6-79f8b1d1a058', 'd2717c9b-b7ab-4085-a169-b95ede048c4c', current_timestamp AT TIME ZONE 'UTC');



-- Update global configuration record for "DB_VERSION" to reflect the new version.
UPDATE reporting.configuration SET (integer_value, string_value) = (7, '7') WHERE param_name='DB_VERSION' AND role_id IS NULL;

--ROLLBACK;
COMMIT;