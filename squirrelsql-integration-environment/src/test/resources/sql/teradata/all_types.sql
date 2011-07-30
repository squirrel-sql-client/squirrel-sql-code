drop table dbcopysrc.bigint_type_table;
drop table dbcopysrc.binary_type_table;
drop table dbcopysrc.bit_type_table;
drop table dbcopysrc.blob_type_table;
drop table dbcopysrc.boolean_type_table;
drop table dbcopysrc.char_type_table;
drop table dbcopysrc.clob_type_table;
drop table dbcopysrc.date_type_table;
drop table dbcopysrc.decimal_type_table;
drop table dbcopysrc.double_type_table;
drop table dbcopysrc.float_type_table;
drop table dbcopysrc.integer_type_table;
drop table dbcopysrc.longvarbinary_type_table;
drop table dbcopysrc.longvarchar_type_table;
drop table dbcopysrc.numeric_type_table;
drop table dbcopysrc.real_type_table;
drop table dbcopysrc.smallint_type_table;
drop table dbcopysrc.time_type_table;
drop table dbcopysrc.timestamp_type_table;
drop table dbcopysrc.tinyint_type_table;
drop table dbcopysrc.varbinary_type_table;
drop table dbcopysrc.varchar_type_table;


CREATE TABLE dbcopysrc.bigint_type_table
(
   bigint_column bigint
);

CREATE TABLE dbcopysrc.binary_type_table
(
   binary_column bytea
);
CREATE TABLE dbcopysrc.bit_type_table
(
   bit_column bit
);
CREATE TABLE dbcopysrc.blob_type_table
(
   blob_column bytea
);
CREATE TABLE dbcopysrc.boolean_type_table
(
   boolean_column bool
);
CREATE TABLE dbcopysrc.char_type_table
(
   char_column char(32000)
);
CREATE TABLE dbcopysrc.clob_type_table
(
   clob_column text
);
CREATE TABLE dbcopysrc.date_type_table
(
   date_column date
);
CREATE TABLE dbcopysrc.decimal_type_table
(
   decimal_column decimal(38, 2)
);
CREATE TABLE dbcopysrc.double_type_table
(
   double_column float(53)
);
CREATE TABLE dbcopysrc.float_type_table
(
   float_column float(53)
);
CREATE TABLE dbcopysrc.integer_type_table
(
   integer_column int
);
CREATE TABLE dbcopysrc.longvarbinary_type_table
(
   longvarbinary_column bytea
);
CREATE TABLE dbcopysrc.longvarchar_type_table
(
   longvarchar_column text
);
CREATE TABLE dbcopysrc.numeric_type_table
(
   numeric_column numeric(38)
);
CREATE TABLE dbcopysrc.real_type_table
(
   real_column real
);
CREATE TABLE dbcopysrc.smallint_type_table
(
   smallint_column smallint
);
CREATE TABLE dbcopysrc.time_type_table
(
   time_column time
);
CREATE TABLE dbcopysrc.timestamp_type_table
(
   timestamp_column timestamp
);
CREATE TABLE dbcopysrc.tinyint_type_table
(
   tinyint_column int
);
CREATE TABLE dbcopysrc.varbinary_type_table
(
   varbinary_column bytea
);
CREATE TABLE dbcopysrc.varchar_type_table
(
   varchar_column varchar(32000)
);

