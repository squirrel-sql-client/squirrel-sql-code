select 'X' from dual;

create table test ( 
	thing varchar(100)
);

update test set /*PARAM1*/ thing /*C*/ = 'default value' /*/PARAM1*/;

create table testdate ( personName varchar(200), mydate date );

select 'X' from dual;

create procedure fooproc (Person_name IN varchar2) 
AS 
BEGIN 
    insert into testdate 
            (personName, mydate) 
    values 
            (Person_name, sysdate); 
END; 
 
/ 

select 'X' from dual;
