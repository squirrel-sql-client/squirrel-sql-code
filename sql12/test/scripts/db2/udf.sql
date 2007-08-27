 CREATE DISTINCT TYPE UDTforColumnUDF AS DECIMAL(9,2) 
 WITH COMPARISONS 


 CREATE FUNCTION scalarUDF ( VARCHAR(20) ) 
 RETURNS int 
 EXTERNAL NAME 'UDFsrv!scalarUDF' 
 LANGUAGE java 
 PARAMETER STYLE db2general 
 DETERMINISTIC 
 FENCED 
 NOT NULL CALL 
 NO SQL 
 NO EXTERNAL ACTION 
 NO SCRATCHPAD 
 NO FINAL CALL 
 ALLOW PARALLEL 
 NO DBINFO



 CREATE FUNCTION columnUDF ( UDTforColumnUDF ) 
 RETURNS UDTforColumnUDF 
 SOURCE "SYSIBM".AVG( DECIMAL() ) 


-- contributed by user osujfpd
CREATE FUNCTION FNGETINDEXES( delimString VARCHAR(1024) )
    RETURNS TABLE (ordinal INTEGER, index INTEGER)
	LANGUAGE SQL
	DETERMINISTIC
	NO EXTERNAL ACTION
	CONTAINS SQL

    RETURN 
		WITH tbl(ordinal, index) AS
		(	VALUES (0, 0) 		-- beginning of string
			UNION ALL
			SELECT ordinal + 1, 
					COALESCE(
						NULLIF(LOCATE(',', delimString, index + 1), 0),
						LENGTH(delimString) + 1)
			FROM tbl			-- recursively locate all delimiters
			WHERE ordinal <= 1024
				AND LOCATE(',', delimString, index + 1) <> 0
		)

	SELECT ordinal, index 
    	FROM tbl
		
	UNION ALL

	SELECT MAX(ordinal) + 1, LENGTH(delimString) + 1
		FROM tbl		-- end of string
;


CREATE FUNCTION TOTALPAY (
        SALARY DECIMAL(9, 2),
        BONUS DECIMAL(9, 2),
        COMMISSION DECIMAL(9, 2))
    RETURNS DECIMAL(9, 2)
    LANGUAGE SQL
    DETERMINISTIC
    NO EXTERNAL ACTION
    CONTAINS SQL

RETURN COALESCE(SALARY, 0) + COALESCE(BONUS, 0) + COALESCE(COMMISSION, 0);


