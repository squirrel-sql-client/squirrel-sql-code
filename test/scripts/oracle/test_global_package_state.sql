
drop package Pkg_Test;

CREATE OR REPLACE PACKAGE Pkg_Test IS
      FUNCTION Prc_Test RETURN NUMBER;
      FUNCTION my_func2 RETURN NUMBER;
END;
/

CREATE OR REPLACE PACKAGE BODY Pkg_Test IS
    b_var NUMBER := 456;
    
    FUNCTION Prc_Test RETURN NUMBER IS 
    BEGIN 
        RETURN b_var;
    END; 

    FUNCTION my_func2 RETURN NUMBER IS
    BEGIN
        RETURN 0;
    END;
    
END Pkg_Test;
/

select Pkg_Test.Prc_Test() as foo from dual;

begin
     DBMS_SESSION.MODIFY_PACKAGE_STATE(DBMS_SESSION.REINITIALIZE);
end;
/