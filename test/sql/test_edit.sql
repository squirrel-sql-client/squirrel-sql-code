
-- Oracle test script for editing issues

create table edit_test (
    myint integer,
    mychar char(10),
    mydate date,
    mytimestamp timestamp,
    myvarchar varchar2(2000),
    myclob clob,
    mynumber number(10,2),
    myfloat float
)

ALTER TABLE EDIT_TEST ADD CONSTRAINT PK_EDIT_TEST PRIMARY KEY (MYINT);

INSERT INTO EDIT_TEST (MYINT,MYCHAR,MYDATE,MYTIMESTAMP,MYVARCHAR,MYCLOB,MYNUMBER,MYFLOAT) 
VALUES (0,'a',sysdate,systimestamp,'a','a',0,0);

INSERT INTO EDIT_TEST (MYINT,MYCHAR,MYDATE,MYTIMESTAMP,MYVARCHAR,MYCLOB,MYNUMBER,MYFLOAT) 
VALUES (1,'b',sysdate,systimestamp,'b','b',1,1);

INSERT INTO EDIT_TEST (MYINT,MYCHAR,MYDATE,MYTIMESTAMP,MYVARCHAR,MYCLOB,MYNUMBER,MYFLOAT) 
VALUES (2,'c',sysdate,systimestamp,'c','c',2,2);


