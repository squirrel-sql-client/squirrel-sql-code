
drop table a

CREATE TABLE a(    acol int NOT NULL PRIMARY KEY,    adesc varchar(10),    bdesc varchar(10),   joined varchar(20) ) 

SELECT  
       foo_seq.CURRVAL AS last_value,
       T2.owner     AS sequence_owner, 
       T2.tabname   AS sequence_name, 
       T1.min_val   AS min_value, 
       T1.max_val   AS max_value, 
       T1.inc_val   AS increment_by, 
       case T1.cycle 
         when '0' then 'NOCYCLE' 
         else 'CYCLE' 
       end AS cycle_flag, 
       case T1.order 
         when '0' then 'NOORDER' 
         else 'ORDER' 
        end AS order_flag, 
       T1.cache     AS cache_size 
FROM    informix.syssequences AS T1, 
        informix.systables    AS T2 
WHERE   T2.tabid     = T1.tabid 
--and T2.owner = ? 
--and T2.tabname = ? 


CREATE CLUSTER INDEX testIndex ON createIndexTest

DROP INDEX testIndex


create table withnocols ()


ALTER TABLE test
   ADD CONSTRAINT UNIQUE (pkcol) CONSTRAINT u_test 

alter table test drop constraint u_test


ALTER TABLE testUniqueConstraintTable
 ADD CONSTRAINT UNIQUE (myId) CONSTRAINT uniq_constraint

drop table testautoincrementtable

create table testautoincrementtable ( myid integer, desc char(10));

drop sequence testAutoIncrementTable_myid_seq
drop function nextAutoVal
drop trigger myid_trigger

CREATE SEQUENCE testAutoIncrementTable_myid_seq
INCREMENT BY 1 MINVALUE 1 NOMAXVALUE
START WITH 1 NOCYCLE;

CREATE FUNCTION nextAutoVal () RETURNING INTEGER;
   RETURN    testAutoIncrementTable_myid_seq.NEXTVAL;
END FUNCTION;

CREATE TRIGGER myid_trigger  
INSERT ON testAutoIncrementTable 
FOR EACH ROW (execute function nextAutoVal() into myid);

ALTER TABLE dbcopydest:"informix".testautoincrementtable ADD autoinc serial NOT NULL ;
select testAutoIncrementTable_myid_seq.NEXTVAL from testAutoIncrementTable


insert into testautoincrementtable (desc) values ( 'foo - auto')

update testAutoIncrementTable set myid = testAutoIncrementTable_myid_seq.NEXTVAL

INSERT INTO dbcopydest:"informix".testautoincrementtable (myid,desc) VALUES (2,'s3');


SELECT * FROM  dbcopydest:"informix".testautoincrementtable


ALTER TABLE dbcopydest:"informix".testautoincrementtable MODIFY autoinc serial;

create table serialtest (myid integer );

insert into serialtest values (5)

ALTER TABLE dbcopydest:"informix".serialtest MODIFY myid serial

insert into serialtest (newcolumn) values ('fooser')

-- alter table A add constraint foreign key (B_ID) references collection



ALTER TABLE fkTestChildTable
 ADD CONSTRAINT 
 FOREIGN KEY  (fkchildid) REFERENCES fkTestParentTable (parentid)
constraint fk_const_name

alter table fkTestChildTable drop constraint r975_565

SELECT * FROM sysconstraints 
