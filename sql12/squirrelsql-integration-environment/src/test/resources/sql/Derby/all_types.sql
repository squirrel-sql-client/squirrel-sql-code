drop table all_types;

create table all_types (
bigint_column bigint,
binary_column char for bit data,
bit_column smallint,
blob_column blob,
boolean_column smallint,
char_column char(10), 
clob_column clob,
date_column date,
decimal_column decimal(31),
double_column float(48),
float_column float(48),
integer_column int, 
longvarbinary_column long varchar for bit data,
longvarchar_column long varchar,
numeric_column bigint,
real_column real,
smallint_column smallint,
time_column time,
timestamp_column timestamp,
tinyint_column smallint,
varbinary_column long varchar for bit data,
varchar_column varchar(4000)
)