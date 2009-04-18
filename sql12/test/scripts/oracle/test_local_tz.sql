drop table test_localtz

CREATE TABLE test_localtz (
  id                    Number                              NOT NULL,
  creation_time         TIMESTAMP(6) WITH LOCAL TIME ZONE  NOT NULL
)

 

Insert into test_localtz (id, creation_time) values (1, timestamp '2009-03-19 00:00:00')

 

SELECT * FROM TEST_LOCALTZ 