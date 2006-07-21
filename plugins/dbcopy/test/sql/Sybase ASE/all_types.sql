drop table bigint_type_table
GO
drop table binary_type_table
GO
drop table bit_type_table
GO
drop table blob_type_table
GO
drop table boolean_type_table
GO
drop table char_type_table
GO
drop table clob_type_table
GO
drop table date_type_table
GO
drop table decimal_type_table
GO
drop table double_type_table
GO
drop table float_type_table
GO
drop table integer_type_table
GO
drop table longvarbinary_type_table
GO
drop table longvarchar_type_table
GO
drop table numeric_type_table
GO
drop table real_type_table
GO
drop table smallint_type_table
GO
drop table time_type_table
GO
drop table timestamp_type_table
GO
drop table tinyint_type_table
GO
drop table varbinary_type_table
GO
drop table varchar_type_table
GO

create table bigint_type_table (
    bigint_column numeric(38) NULL
)
GO
create table binary_type_table (
    binary_column image NULL
)
GO
create table bit_type_table (
    bit_column bit NULL
)
GO
create table tinyint_type_table (
    tinyint_column tinyint NULL
)
GO
create table blob_type_table (
blob_column image NULL
)
GO
create table boolean_type_table (
    boolean_column tinyint NULL
)
GO
create table char_type_table (
    char_column char(10) NULL
)
GO
create table clob_type_table (
    clob_column text NULL
)
GO
create table date_type_table (
    date_column datetime NULL
)
GO
create table decimal_type_table (
    decimal_column decimal(38) NULL
)
GO
create table double_type_table (
    double_column float(48) NULL
)
GO
create table float_type_table (
    float_column float(48) NULL
)
GO
create table integer_type_table (
    integer_column int NULL
)
GO
create table longvarbinary_type_table (
    longvarbinary_column image NULL
)
GO
create table longvarchar_type_table (
    longvarchar_column text NULL
)
GO
create table numeric_type_table (
    numeric_column numeric(38) NULL
)
GO
create table real_type_table (
    real_column real NULL
)
GO
create table smallint_type_table (
    smallint_column smallint NULL
)
GO
create table time_type_table (
    time_column datetime NULL
)
GO
create table timestamp_type_table (
    timestamp_column datetime NULL
) 
GO
create table tinyint_type_table (
    tinyint_column tinyint NULL
)
GO
create table varbinary_type_table (
    varbinary_column image NULL
)
GO
create table varchar_type_table (
    varchar_column varchar(25) NULL
)
GO
