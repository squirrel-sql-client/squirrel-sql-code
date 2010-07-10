@echo off
@rem ======================================
@rem DOS Batch file to invoke the frontend
@rem ======================================

@rem Debug call
@rem java -classpath "..\lib\compiler.jar;..\lib\frontend.jar;..\lib\kunststoff.jar" com.izforge.izpack.frontend.Frontend

@rem Release call
@start javaw -classpath "..\lib\compiler.jar;..\lib\frontend.jar;..\lib\kunststoff.jar" com.izforge.izpack.frontend.Frontend

@echo on
