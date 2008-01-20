CREATE TABLE TEST
(
   MYCHAR char(10),
   NODEFAULTVARCHARCOL varchar(20),
   RENAMECOL varchar(20),
   PKCOL int DEFAULT 0 NOT NULL
)
;

create view myviewtest as select * from TEST;


-- Derby's view def query
select v.VIEWDEFINITION 
from sys.SYSVIEWS v, sys.SYSTABLES t, sys.SYSSCHEMAS s 
where v.TABLEID = t.TABLEID 
and s.SCHEMAID = t.SCHEMAID 
and UPPER(t.TABLENAME) = 'MYVIEWTEST'
and UPPER(s.SCHEMANAME) = 'DBCOPYDEST'

