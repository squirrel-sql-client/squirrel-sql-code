
-- DB2 needs the "not null" on primary key columns
create table FKTESTPARENTTABLE ( parentid integer not null primary key, mychar char(10));

insert into foo values 3;

CREATE SEQUENCE myseq  
AS INTEGER  
START WITH 1  
INCREMENT BY 1  
NO MINVALUE  
NO MAXVALUE  
NO CYCLE  
CACHE 10  
ORDER;

SELECT NEXTVAL FOR myseq FROM foo WHERE myid = 3


SELECT SEQSCHEMA,SEQNAME,DEFINER,DEFINERTYPE,OWNER,OWNERTYPE,SEQID,SEQTYPE,INCREMENT,START,MAXVALUE,MINVALUE,
NEXTCACHEFIRSTVALUE,CYCLE,CACHE,ORDER,DATATYPEID,SOURCETYPEID,CREATE_TIME,ALTER_TIME,PRECISION,ORIGIN,REMARKS 
FROM SYSCAT.SEQUENCES
WHERE SEQSCHEMA = 'DBCOPY'


SELECT NEXTCACHEFIRSTVALUE, MAXVALUE, MINVALUE, CACHE, INCREMENT, CYCLE FROM SYSCAT.SEQUENCES WHERE upper(SEQNAME) = upper('myseq') 


ALTER SEQUENCE testSequence
INCREMENT BY 1 MINVALUE 1 MAXVALUE 1000
CACHE cache 10 NO CYCLE

RENAME TABLE FOO TO FOO2;

create view fooview as select * from foo2; 

-- The following doesn't appear to work on DB2 (V9.5 LUW) - "The name used for this operation is not a table. SQL Code: -156, SQL State: 42809"
RENAME TABLE fooview to fooview2;

DROP VIEW FOOVIEW;

create index foo_idx on FOO2(MYID)

drop index foo_idx;

create unique index foo_idx on FOO2(MYID)

CREATE unique  INDEX testIndex ON createIndexTest(mychar)

create table nulltest60 ( myid integer )

ALTER TABLE nulltest60 ALTER COLUMN myid SET NOT NULL

set INTEGRITY FOR testUniqueConstraintTable OFF

ALTER TABLE testUniqueConstraintTable ALTER COLUMN myId SET NOT NULL

SELECT * FROM  testUniqueConstraintTable

set INTEGRITY FOR testUniqueConstraintTable IMMEDIATE CHECKED


SELECT TEXT FROM SYSCAT.VIEWS WHERE VIEWNAME = 'FOOVIEW'

select locate('as', SELECT TEXT FROM SYSCAT.VIEWS WHERE VIEWNAME = 'FOOVIEW') from FOOVIEW

drop table TESTUNIQUECONSTRAINTTABLE;

CREATE TABLE TESTUNIQUECONSTRAINTTABLE
(
   MYID char(10) 
)
;
testUniqueConstraintTable
ALTER TABLE testUniqueConstraintTable ALTER COLUMN myId SET NOT NULL;

REORG "DBCOPY"."testUniqueConstraintTable";

CALL SYSPROC.ADMIN_CMD('REORG TABLE testUniqueConstraintTable')

REORG TABLE testUniqueConstraintTable INPLACE ALLOW WRITE ACCESS START;

alter table TESTUNIQUECONSTRAINTTABLE add constraint foo UNIQUE (myid)



alter table test2 alter column NOTNULLINT rename to foo

alter table test4 drop column notnullvc



INSERT INTO testUniqueConstraintTable
 select distinct myid from integerDataTable


create view fooview as
select
*
from foo2

SELECT  'CREATE VIEW FOOVIEW AS ' || SUBSTR(TEXT ,  LOCATE('as', TEXT)+2, LENGTH(TEXT))
FROM SYSCAT.VIEWS 
WHERE VIEWSCHEMA = 'DBCOPY'
AND VIEWNAME = 'FOOVIEW' 

select UPPER(ADESC) from A 

-- First attempt.  No good way to search case-insensitively from AS in view def
SELECT SUBSTR(TEXT ,  LOCATE('AS', UPPER(TEXT) )+2, LENGTH(TEXT))
 FROM SYSCAT.VIEWS WHERE VIEWSCHEMA = 'DBCOPY' AND VIEWNAME = 'TESTVIEW';

-- Returns the whole definition : create view blah as select ...
-- Just have the app strip off the stuff before AS to create the new view def
SELECT TEXT  FROM SYSCAT.VIEWS WHERE VIEWSCHEMA = 'DBCOPY' AND UPPER(VIEWNAME) = 'TESTVIEW';

