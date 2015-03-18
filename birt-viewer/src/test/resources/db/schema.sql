drop table if exists report;
drop table if exists report_category;

create table report_category (
  id identity,
  username varchar(25) not null,
  unused_report_category_field_1 varchar(25) not null,
  fullName varchar(100) not null,
  unused_report_category_field_2 varchar(50) not null,
  updateByEmail boolean not null,
  unused_report_category_field_3 varchar(10) not null
);

create table report (
  id integer identity primary key,
  spitter integer not null,
  message varchar(2000) not null,
  postedTime datetime not null,
  foreign key (spitter) references report_category(id)
);
