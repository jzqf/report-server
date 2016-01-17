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
insert into reporting.asset_type (asset_type_id, name, abbreviation, directory, active, created_on) values ('f2f4b13f-9c45-4515-bb4b-62e8ccc3d95c', 'CSS file'        , 'CSS'     , 'css'       , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.asset_type (asset_type_id, name, abbreviation, directory, active, created_on) values ('1e7ddbbc-8b40-4373-bfc5-6e6d3d5964d8', 'Image file'      , 'IMAGE'   , 'images'    , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.asset_type (asset_type_id, name, abbreviation, directory, active, created_on) values ('1c488bcf-15ab-4fcb-8ab8-23cda9342a77', 'JavaScript file' , 'JS'      , 'js'        , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.asset_type (asset_type_id, name, abbreviation, directory, active, created_on) values ('26835616-00e2-475d-89ce-11c12e659605', 'BIRT Library'    , 'BIRT LIB', 'libraries' , true, current_timestamp AT TIME ZONE 'UTC');


-- Update global configuration record for "DB_VERSION" to reflect the new version.
UPDATE reporting.configuration SET (integer_value, string_value) = (7, '7') WHERE param_name='DB_VERSION' AND role_id IS NULL;

--ROLLBACK;
COMMIT;