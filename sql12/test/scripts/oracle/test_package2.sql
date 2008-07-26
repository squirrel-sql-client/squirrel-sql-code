
--drop table tech_value;

create table tech_value ( id integer, id2 integer, p_tech_par_id NUMBER, rp_service integer, value varchar2(100));

--drop table tech_value_range;

create table tech_value_range (id integer, value varchar2(100), min varchar2(100), max varchar2(100));

--drop sequence sequence_tech_value;

create sequence sequence_tech_value;

--drop sequence sequence_rp_service;

create sequence  sequence_rp_service;

create or replace package RBA_VALUES is
   procedure create_tech_value_and_range(p_tech_par_id IN NUMBER,
       p_value IN VARCHAR2, p_value_min IN VARCHAR2, p_value_max IN VARCHAR2  );
end RBA_VALUES;
/
create or replace package body RBA_VALUES is
procedure create_tech_value_and_range(p_tech_par_id IN NUMBER,
 p_value IN VARCHAR2, p_value_min IN VARCHAR2, p_value_max IN VARCHAR2  ) is
begin

 INSERT INTO tech_value VALUES (sequence_tech_value.nextval, 1, p_tech_par_id , sequence_rp_service.currval, p_value );

 if p_value_min is not NULL and p_value_max is not NULL then
   insert into tech_value_range values(sequence_tech_value.currval, p_value, p_value_min, p_value_max ) ;
 end if;
end;
end RBA_VALUES;
/