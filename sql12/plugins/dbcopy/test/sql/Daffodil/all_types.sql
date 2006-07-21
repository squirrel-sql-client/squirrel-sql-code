DROP TABLE BIGINT_TYPE_TABLE;
DROP TABLE BINARY_TYPE_TABLE;
DROP TABLE BIT_TYPE_TABLE;
DROP TABLE BLOB_TYPE_TABLE;
DROP TABLE BOOLEAN_TYPE_TABLE;
DROP TABLE CHAR_TYPE_TABLE;
DROP TABLE CLOB_TYPE_TABLE;
DROP TABLE DATE_TYPE_TABLE;
DROP TABLE DOUBLE_TYPE_TABLE;
DROP TABLE FLOAT_TYPE_TABLE;
DROP TABLE INTEGER_TYPE_TABLE;
DROP TABLE LONGVARBINARY_TYPE_TABLE;
DROP TABLE LONGVARCHAR_TYPE_TABLE;
DROP TABLE NUMERIC_TYPE_TABLE;
DROP TABLE REAL_TYPE_TABLE;
DROP TABLE SMALLINT_TYPE_TABLE;
DROP TABLE TIME_TYPE_TABLE;
DROP TABLE TIMESTAMP_TYPE_TABLE;
DROP TABLE TINYINT_TYPE_TABLE;
DROP TABLE VARBINARY_TYPE_TABLE;
DROP TABLE VARCHAR_TYPE_TABLE;

create table bigint_type_table (
    bigint_column bigint
);

create table binary_type_table (
    binary_column binary
);

create table bit_type_table (
    bit_column bit
);

create table blob_type_table (
    blob_column blob
);

create table boolean_type_table (
    boolean_column boolean
);

create table char_type_table (
    char_column char(4192)
);

create table clob_type_table (
    clob_column clob(1073741823)
);

create table date_type_table (
    date_column date
);

create table decimal_type_table (
    decimal_column decimal(38,2)
);

create table double_type_table (
    double_column double precision
);

create table float_type_table (
    float_column float(15)
);

create table integer_type_table (
    integer_column integer
);

create table longvarbinary_type_table (
    longvarbinary_column long varbinary(1073741823)
);

create table longvarchar_type_table (
    longvarchar_column long varchar
);

CREATE TABLE NUMERIC_TYPE_TABLE
(
   NUMERIC_COLUMN DECIMAL(38)
);

CREATE TABLE REAL_TYPE_TABLE
(
   REAL_COLUMN DOUBLE PRECISION
);

CREATE TABLE SMALLINT_TYPE_TABLE
(
   SMALLINT_COLUMN SMALLINT
);

CREATE TABLE TIME_TYPE_TABLE
(
   TIME_COLUMN TIME
);
CREATE TABLE TIMESTAMP_TYPE_TABLE
(
   TIMESTAMP_COLUMN TIMESTAMP
);

CREATE TABLE TINYINT_TYPE_TABLE
(
   TINYINT_COLUMN INTEGER
);

CREATE TABLE VARBINARY_TYPE_TABLE
(
   VARBINARY_COLUMN LONG VARBINARY
);

CREATE TABLE VARCHAR_TYPE_TABLE
(
   VARCHAR_COLUMN LONG VARCHAR(4192)
);

