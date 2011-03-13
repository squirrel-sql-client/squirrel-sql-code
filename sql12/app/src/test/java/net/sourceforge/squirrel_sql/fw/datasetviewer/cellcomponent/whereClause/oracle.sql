


drop TABLE basicTypes;

create table basicTypes(
	cint number(7),
	clong number(19),
	cpresc NUMBER(9,2),
	cfloat BINARY_FLOAT,
	cvarchar2 varchar2(50),
	cdate date,
	ctimestamp timestamp
);


-- an simple
insert into basicTypes values (1,12, 12.98,12.98,'simple', sysdate, systimestamp);
-- escapaes within an mysql
insert into basicTypes values (2,12, 12.98,12.98,'insert '' an quote', sysdate, systimestamp);
insert into basicTypes values (3,12, 12.98,12.98,'insert  an ''quote'' word', sysdate, systimestamp);
insert into basicTypes values (4,12, 12.98,12.98,'insert  an double ''''quote'''' word', sysdate, systimestamp);
insert into basicTypes values (5,12, 12.98,12.98,'insert  an \ backslash', sysdate, systimestamp);
insert into basicTypes values (6,12, 12.98,12.98,'insert  an \'' backslash quote', sysdate, systimestamp);
insert into basicTypes values (7,12, 12.98,12.98,'insert  an '|| CHR(13) || CHR(10) || ' CR LF', sysdate, systimestamp);
insert into basicTypes values (8,12, 12.98,12.98,'insert  an '|| CHR(09) || ' tab', sysdate, systimestamp);
insert into basicTypes values (9,12, 12.98,12.98,'insert  an '|| CHR(08) || ' backspace', sysdate, systimestamp);
-- some others
insert into basicTypes values (20,12, 12.98,12.98,'insert  an % percent', sysdate, systimestamp);
insert into basicTypes values (21,12, 12.98,12.98,'insert  an _ underscore', sysdate, systimestamp);
insert into basicTypes values (22,12, 12.98,12.98,'insert  an & ampercent', sysdate, systimestamp);
insert into basicTypes values (23,12, 12.98,12.98,'insert  an ? question mark', sysdate, systimestamp);
insert into basicTypes values (24,12, 12.98,12.98,'insert  an :1 double point 1', sysdate, systimestamp);
insert into basicTypes values (25,12, 12.98,12.98,'insert  an | vertical bar', sysdate, systimestamp);
insert into basicTypes values (26,12, 12.98,12.98,'insert  an $ dollar', sysdate, systimestamp);



