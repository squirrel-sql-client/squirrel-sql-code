drop table pub.bigint_type_table;
drop table pub.binary_type_table;
drop table pub.bit_type_table;
drop table pub.blob_type_table;
drop table pub.boolean_type_table;
drop table pub.char_type_table;
drop table pub.clob_type_table;
drop table pub.date_type_table;
drop table pub.decimal_type_table;
drop table pub.double_type_table;
drop table pub.float_type_table;
drop table pub.longvarbinary_type_table;
drop table pub.longvarchar_type_table;
drop table pub.numeric_type_table;
drop table pub.real_type_table;
drop table pub.smallint_type_table;
drop table pub.time_type_table;
drop table pub.timestamp_type_table;
drop table pub.tinyint_type_table;
drop table pub.varbinary_type_table;
drop table pub.varchar_type_table;

create table pub.bigint_type_table (
    bigint_column integer
);

create table pub.binary_type_table (
    binary_column binary(2000)
);

create table pub.bit_type_table (
    bit_column bit
);

create table pub.blob_type_table {
    blob_column lvarbinary(2000000000)
}

create table pub.boolean_type_table (   
    boolean_column bit
);

create table pub.char_type_table (
    char_column char(2000)
);

create table pub.clob_type_table (
    clob_column varchar(31982)
);

create table pub.date_type_table (
    date_column date
);

create table pub.decimal_type_table (
    decimal_column numeric(32,2)
);

create table pub.double_type_table (
    double_column double precision
);

create table pub.float_type_table (
    float_column float
);

create table pub.integer_type_table (
    integer_column integer
);

create table pub.longvarbinary_type_table (
    longvarbinary_column lvarbinary(2000000000)
);

create table pub.longvarchar_type_table (
    longvarchar_column varchar(31982)
);

create table pub.numeric_type_table (
    numeric_column numeric(32,2)
);

create table pub.real_type_table (
    real_column real
);

create table pub.smallint_type_table (
    smallint_column smallint
);

create table pub.time_type_table (
    time_column time
);

create table pub.timestamp_type_table (
    timestamp_column timestamp
);

create table pub.tinyint_type_table (
    tinyint_column tinyint
);

create table pub.varbinary_type_table (
    varbinary_column varbinary(31982)
);

create table pub.varchar_type_table (
    varchar_column varchar(31982)
);


