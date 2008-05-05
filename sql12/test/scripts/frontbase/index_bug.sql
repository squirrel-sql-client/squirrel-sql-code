

--DROP TABLE "DBCOPYDEST"."DBCOPY"."CREATEINDEXTEST" CASCADE;

-- Create a test table
create table CREATEINDEXTEST ( mychar varchar(10) not null, myuniquechar varchar(10));

-- Create a non-unique index
CREATE  INDEX testIndex ON CREATEINDEXTEST(mychar);

-- Create a unique index - DatabaseMetaData returns _I0000000006 as the name of this index
CREATE UNIQUE INDEX testUniqueIndex ON CREATEINDEXTEST(myuniquechar);

-- this works
DROP INDEX testIndex;

-- this fails
DROP INDEX testUniqueIndex;

-- this works
DROP INDEX "_I0000000006";

