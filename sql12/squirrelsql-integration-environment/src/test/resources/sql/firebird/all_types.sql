drop table bigint_type_table;
drop table binary_type_table;
drop table bit_type_table;
drop table blob_type_table;
drop table boolean_type_table;
drop table char_type_table;
drop table clob_type_table;
drop table date_type_table;
drop table decimal_type_table;
drop table double_type_table;
drop table float_type_table;
drop table integer_type_table;
drop table longvarbinary_type_table;
drop table longvarchar_type_table;
drop table numeric_type_table;
drop table real_type_table;
drop table smallint_type_table;
drop table time_type_table;
drop table timestamp_type_table;
drop table tinyint_type_table;
drop table varbinary_type_table;
drop table varchar_type_table;

create table bigint_type_table (
    bigint_column bigint
);

create table binary_type_table (
    binary_column blob sub_type 0
);

create table bit_type_table (
    bit_column char(1)
);

create table blob_type_table (
    blob_column blob sub_type -1
);

create table boolean_type_table (
    boolean_column char(1)
);

create table char_type_table (
    char_column char(32767)
);

create table clob_type_table (
    clob_column blob sub_type text
);

create table date_type_table (
    date_column date
);

create table decimal_type_table (
    decimal_column decimal(18)
);

create table double_type_table (
    double_column float
);

create table float_type_table (
    float_column float
);

create table integer_type_table (
    integer_column integer
);

create table longvarbinary_type_table (
    longvarbinary_column blob sub_type 0
);


create table longvarchar_type_table (
    longvarchar_column blob sub_type 1
);


create table numeric_type_table (
    numeric_column numeric
);

create table real_type_table (
    real_column float
);

create table smallint_type_table (
    smallint_column smallint
);

create table time_type_table (
    time_column time
);

create table timestamp_type_table (
    timestamp_column timestamp
);

create table tinyint_type_table (
    tinyint_column smallint
);

create table varbinary_type_table (
    varbinary_column blob sub_type -1
);

create table varchar_type_table (
    varchar_column varchar(32765)
);

