
drop table tblcontents;

create table tblcontents ( id number, data nvarchar2(100));

insert into tblcontents (id, data) values (1, '?1');

select dump(DBMS_LOB.SUBSTR(data,100), 1016) from tblcontents;



