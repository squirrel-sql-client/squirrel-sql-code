drop table basicTypes;

create table basicTypes(
	cint integer,
	clong bigint,
	cpresc decimal(9,2),
	cfloat float,
	cvarchar2 varchar(50),
	cdate date,
	ctimestamp timestamp
);



-- an simple
insert into basicTypes values (1,12, 12.98,12.98,'simple', current_date(), current_timestamp());
-- escapaes within an mysql
insert into basicTypes values (2,12, 12.98,12.98,'insert '' an quote', current_date(), current_timestamp());
insert into basicTypes values (3,12, 12.98,12.98,'insert  an ''quote'' word', current_date(), current_timestamp());
insert into basicTypes values (4,12, 12.98,12.98,'insert  an double ''''quote'''' word', current_date(), current_timestamp());
insert into basicTypes values (5,12, 12.98,12.98,'insert  an \\ backslash', current_date(), current_timestamp());
insert into basicTypes values (6,12, 12.98,12.98,'insert  an \\'' backslash quote', current_date(), current_timestamp());
insert into basicTypes values (7,12, 12.98,12.98,'insert  an \r \n CR LF', current_date(), current_timestamp());
insert into basicTypes values (8,12, 12.98,12.98,'insert  an \t tab', current_date(), current_timestamp());
insert into basicTypes values (9,12, 12.98,12.98,'insert  an \b backspace', current_date(), current_timestamp());
-- some others
insert into basicTypes values (20,12, 12.98,12.98,'insert  an % percent', current_date(), current_timestamp());
insert into basicTypes values (21,12, 12.98,12.98,'insert  an _ underscore', current_date(), current_timestamp());
insert into basicTypes values (22,12, 12.98,12.98,'insert  an & ampercent', current_date(), current_timestamp());
insert into basicTypes values (23,12, 12.98,12.98,'insert  an ? question mark', current_date(), current_timestamp());
insert into basicTypes values (24,12, 12.98,12.98,'insert  an :1 double point 1', current_date(), current_timestamp());
insert into basicTypes values (25,12, 12.98,12.98,'insert  an | vertical bar', current_date(), current_timestamp());
insert into basicTypes values (26,12, 12.98,12.98,'insert  an $ dollar', current_date(), current_timestamp());

