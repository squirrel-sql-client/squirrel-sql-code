

create or replace package SQUIRREL_PACKAGE
AS
    function getPackageVar RETURN NUMBER;
    procedure setPackageVar (newValue IN NUMBER);
end SQUIRREL_PACKAGE;
/



create or replace package body SQUIRREL_PACKAGE
AS
    -- a global variable
    global_package_var NUMBER(19,0);

    -- a getter function
    function getPackageVar RETURN NUMBER
        IS
        BEGIN
            RETURN global_package_var;
        END getPackageVar;
    -- a setter procedure
    procedure setPackageVar (newValue IN NUMBER)
    IS
        BEGIN
            squirrel_package.global_package_var := newValue;
        END setPackageVar;

END squirrel_package;
/