drop table if exists report;
drop table if exists report_category;

create table report_category (
  id identity,
  username varchar(25) not null,
  password varchar(25) not null,
  fullName varchar(100) not null,
  email varchar(50) not null,
  updateByEmail boolean not null,
  status varchar(10) not null
);

create table report (
  id integer identity primary key,
  spitter integer not null,
  message varchar(2000) not null,
  postedTime datetime not null,
  foreign key (spitter) references report_category(id)
);
