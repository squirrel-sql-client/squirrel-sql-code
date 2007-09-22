-- create the table that the MQT is selecting against

drop table BIGINT_TYPE_TABLE

CREATE TABLE BIGINT_TYPE_TABLE ( 
    ID integer not null,
    NAME VARCHAR(30) not null,
    BIGINT_COLUMN bigint,
    DESCRIPTION varchar(100),
    CREATE_DATE DATE not null,
    LAST_MODIFIED DATE not null
);

ALTER TABLE "DBCOPY"."BIGINT_TYPE_TABLE" ADD CONSTRAINT PK_BIGINT PRIMARY KEY (ID);

CREATE UNIQUE INDEX BIGINT_NAME_IDX on BIGINT_TYPE_TABLE(NAME);

-- create the materialized query table

CREATE TABLE BIGINT_MQT AS
(
   SELECT
   bigint_column
   FROM BIGINT_TYPE_TABLE
)
DATA INITIALLY DEFERRED REFRESH DEFERRED;

-- refresh it so that it is selectable

refresh table BIGINT_MQT;

