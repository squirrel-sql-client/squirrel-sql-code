DROP TABLE bigint_type_table;
DROP TABLE binary_type_table;
DROP TABLE bit_type_table;
DROP TABLE blob_type_table;
DROP TABLE boolean_type_table;
DROP TABLE char_type_table;
DROP TABLE clob_type_table;
DROP TABLE date_type_table;
DROP TABLE decimal_type_table;
DROP TABLE double_type_table;
DROP TABLE float_type_table;
DROP TABLE integer_type_table;
DROP TABLE longvarbinary_type_table;
DROP TABLE longvarchar_type_table;
DROP TABLE numeric_type_table;
DROP TABLE real_type_table;
DROP TABLE smallint_type_table;
DROP TABLE time_type_table;
DROP TABLE timestamp_type_table;
DROP TABLE tinyint_type_table;
DROP TABLE varbinary_type_table;
DROP TABLE varchar_type_table;

CREATE TABLE bigint_type_table
(
   bigint_column BIGINT
);
CREATE TABLE binary_type_table
(
   binary_column BINARY(2000000000)
);
CREATE TABLE bit_type_table
(
   bit_column BIT
);
CREATE TABLE blob_type_table
(
   blob_column blob(2000000000)
);
CREATE TABLE boolean_type_table
(
   boolean_column BOOLEAN
);
CREATE TABLE char_type_table
(
   char_column VARCHAR(8000)
);
CREATE TABLE clob_type_table
(
   clob_column CLOB(1000000000)
);
CREATE TABLE date_type_table
(
   date_column DATE
);
CREATE TABLE decimal_type_table
(
   decimal_column DECIMAL(1024,512)
);
CREATE TABLE double_type_table
(
   double_column DOUBLE(58)
);
CREATE TABLE float_type_table
(
   float_column FLOAT(48)
);
CREATE TABLE integer_type_table
(
   integer_column INTEGER(1024,0)
);
CREATE TABLE longvarbinary_type_table
(
   longvarbinary_column longvarbinary(32000)
);
CREATE TABLE longvarchar_type_table
(
   longvarchar_column longvarchar(4000)
);
CREATE TABLE numeric_type_table
(
   numeric_column NUMERIC(1024,512)
);
CREATE TABLE real_type_table
(
   real_column REAL
);
CREATE TABLE smallint_type_table
(
   smallint_column SMALLINT
);
CREATE TABLE time_type_table
(
   time_column TIME
);
CREATE TABLE timestamp_type_table
(
   timestamp_column TIMESTAMP
);
CREATE TABLE tinyint_type_table
(
   tinyint_column tinyint
);
CREATE TABLE varbinary_type_table
(
   varbinary_column VARBINARY(8192)
);
CREATE TABLE varchar_type_table
(
   varchar_column VARCHAR(8192)
);

