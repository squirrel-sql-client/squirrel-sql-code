
What is this:
-------------
The scripts
- squirrelcli.sh for Unix/Linux
- squirrelcli.bat for Windows
give access to SQuirreL's Command Line Interface (CLI).
It allows to connect to databases and execute SQLs (or SQL scripts) on the command line.
SQL results are written as text to STDOUT or to files.


What to do:
-----------
To get more information execute
- squirrelcli.sh -help on Unix/Linux shell
- squirrelcli.bat -help on Windows CMD


Troubleshooting:
----------------
If the script doesn't work try setting the JAVA_HOME variable to your Java installation directory.
Preferably you should use Java 9.


Details:
--------
The squirrelcli* scripts offers two modes:
1. The batch mode which, as mentioned above, you can learn about by entering squirrelcli* -help on your command line.
2. The Java 9 JShell based mode which can be entered by executing the script with
   no parameter (or -userdir only). This requires Java 9.
