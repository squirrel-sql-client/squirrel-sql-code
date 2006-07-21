drop table all_types;

create table all_types (
bigint_column bigint,
binary_column image,
bit_column tinyint,
blob_column image,
boolean_column tinyint,
char_column char, 
clob_column text,
decimal_column decimal(38),
double_column float(53),
float_column float(53),
integer_column int, 
longvarbinary_column image,
longvarchar_column text,
numeric_column numeric(38),
real_column real,
smallint_column smallint,
time_column datetime,
timestamp_column datetime,
tinyint_column tinyint,
varbinary_column varbinary,
varchar_column varchar(8000)
)