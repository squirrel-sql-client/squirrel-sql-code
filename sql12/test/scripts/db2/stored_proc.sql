-- I obtained this sample procedure def from the sample chapter (no. 5)
-- of the book "DB2 SQL Procedure Language for Linux, UNIX, and Windows"
-- by Drew Bradstock, et. al., which is available on IBM's website
-- (http://www.tinyurl.com/ys66u3) RMM (20070704)

drop table employee
|

create table employee ( midinit CHAR, empno CHAR(6) )
|

drop procedure simple_error
|

create procedure simple_error ( IN p_midinit CHAR
                               ,IN p_empno CHAR(6) )
specific simple_error
LANGUAGE SQL
se: BEGIN
       DECLARE SQLSTATE CHAR(5) DEFAULT '00000';
       DECLARE X CHAR(1);
       DECLARE SQLCODE INT DEFAULT 0;
       UPDATE employee
               SET midinit = p_midinit
               WHERE empno = p_empno;
       IF SUBSTR(SQLSTATE, 2) NOT in ('00', '01', '02') THEN
            SET X = 'X';
       END IF;
END se
|
