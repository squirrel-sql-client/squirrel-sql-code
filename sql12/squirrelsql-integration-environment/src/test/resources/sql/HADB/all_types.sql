
create table bigint_type_table (
    bigint_column double integer primary key 
);

create table binary_type_table (
    myid integer primary key,
    binary_column binary(8000)
);

create table blob_type_table ( 
    myid integer primary key, 
    blob_column blob 
);

create table char_type_table ( 
    myid integer primary key, 
    char_column char(8000) 
);

create table clob_type_table ( 
    myid integer primary key, 
    clob_column clob 
);

create table varchar_type_table ( 
    myid integer primary key, 
    varchar_column varchar(8000) 
);


