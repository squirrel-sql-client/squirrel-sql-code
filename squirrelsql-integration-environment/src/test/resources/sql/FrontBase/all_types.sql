drop table bigint_type_table cascade;
drop table binary_type_table cascade;
drop table bit_type_table cascade;
drop table blob_type_table cascade;
drop table boolean_type_table cascade;
drop table char_type_table cascade;
drop table clob_type_table cascade;
drop table date_type_table cascade;
drop table decimal_type_table cascade;
drop table double_type_table cascade;
drop table float_type_table cascade;
drop table integer_type_table cascade;
drop table longvarbinary_type_table cascade;
drop table longvarchar_type_table cascade;
drop table numeric_type_table cascade;
drop table real_type_table cascade;
drop table smallint_type_table cascade;
drop table time_type_table cascade;
drop table timestamp_type_table cascade;
drop table tinyint_type_table cascade;
drop table varbinary_type_table cascade;
drop table varchar_type_table cascade;

create table bigint_type_table (
    bigint_column integer
);

create table binary_type_table (
    binary_column bit varying(2000000000)
);

create table bit_type_table (
    bit_column bit
);

create table blob_type_table {
    blob_column bit varying(2000000000)
};

create table boolean_type_table (   
    boolean_column bit(1)
);

create table char_type_table (
    char_column char(2000)
);

create table clob_type_table (
    clob_column varchar(31982)
);

create table date_type_table (
    date_column date
);

create table decimal_type_table (
    decimal_column decimal(36,2)
);

create table double_type_table (
    double_column double precision
);

create table float_type_table (
    float_column float
);

create table integer_type_table (
    integer_column integer
);

create table longvarbinary_type_table (
    longvarbinary_column bit varying(2000000000)
);

create table longvarchar_type_table (
    longvarchar_column varchar(31982)
);

create table numeric_type_table (
    numeric_column numeric(17,2)
);

create table real_type_table (
    real_column real
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
    tinyint_column tinyint
);

create table varbinary_type_table (
    varbinary_column bit varying(2000000000)
);

create table varchar_type_table (
    varchar_column varchar(2000000000)
);


