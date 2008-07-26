


create table proctest ( 
     id number not null,
     name varchar2(100), 
     description varchar2(200),
     primary key (id)
)

create sequence procseq start with 1;

select procseq.nextval from dual;

create procedure insertproc (name IN varchar2)
AS
    BEGIN
        insert into PROCTEST
            (id, name, description)
        VALUES
            (procseq.nextval, name, null);
    END;
/
