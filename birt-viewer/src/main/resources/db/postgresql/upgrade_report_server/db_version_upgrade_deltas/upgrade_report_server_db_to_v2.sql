BEGIN TRANSACTION;

-- Update the DB schema from v1 to v2:



-- Update the DB content from v1 to v2:

insert into reporting.parameter_type (parameter_type_id, description, abbreviation, active, created_on) values ('bfa09b13-ad55-481e-8c29-b047dc5d7f3e', 'Boolean (True/False)'   , 'boolean'  , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.parameter_type (parameter_type_id, description, abbreviation, active, created_on) values ('12d3f4f8-468d-4faf-be3a-5c15eaba4eb6', 'Date (no time)'         , 'date'     , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.parameter_type (parameter_type_id, description, abbreviation, active, created_on) values ('abce5a38-b1e9-42a3-9962-19227d51dd4a', 'Datetime (date + time)' , 'datetime' , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.parameter_type (parameter_type_id, description, abbreviation, active, created_on) values ('f2bfa3f9-f446-49dd-ad0e-6a02b3af1023', 'Decimal'                , 'decimal'  , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.parameter_type (parameter_type_id, description, abbreviation, active, created_on) values ('8b0bfc37-5fb4-4dea-87fc-3e2c3313af17', 'Float'                  , 'float'    , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.parameter_type (parameter_type_id, description, abbreviation, active, created_on) values ('807c64b1-a59b-465c-998b-a399984b5ef4', 'Integer'                , 'integer'  , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.parameter_type (parameter_type_id, description, abbreviation, active, created_on) values ('9b0af697-8bc9-49e2-b8b6-136ced83dbd8', 'String'                 , 'string'   , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.parameter_type (parameter_type_id, description, abbreviation, active, created_on) values ('da575eee-e5a3-4149-8ea3-1fd86015bbb9', 'Time'                   , 'time'     , true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.parameter_type (parameter_type_id, description, abbreviation, active, created_on) values ('2bc62461-6ddb-4e86-b46d-080cd5e9cf83', 'Any'                    , 'any'      , true, current_timestamp AT TIME ZONE 'UTC');

insert into reporting.widget (widget_id, name, description, multiple_select ,active, created_on ) values ('b8e91527-8b0e-4ed2-8cba-8cb8989ba8e2', 'Checkbox'    , 'Checkbox widget'    , false, true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.widget (widget_id, name, description, multiple_select ,active, created_on ) values ('e5b4cebb-1852-41a1-9fdf-bb4b8da82ef9', 'String entry', 'String entry widget', false, true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.widget (widget_id, name, description, multiple_select ,active, created_on ) values ('864c60a8-6c48-4efb-84dd-fc79502899fe', 'Radio button', 'Radio button widget', false, true, current_timestamp AT TIME ZONE 'UTC');
insert into reporting.widget (widget_id, name, description, multiple_select ,active, created_on ) values ('4d0842e4-ba65-4064-8ab1-556e90e3953b', 'Listbox'     , 'Listbox widget'     , false, true, current_timestamp AT TIME ZONE 'UTC');


-- Update global configuration record for "DB_VERSION" to reflect the new version.
UPDATE configuration SET (integer_value, string_value) = (2, '2') WHERE param_name='DB_VERSION' AND role_id IS NULL;

COMMIT;