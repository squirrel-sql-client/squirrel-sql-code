package net.sourceforge.squirrel_sql.fw.sql.databasemetadata;

/**
 * Full or partial names of various JDBC driivers that can be matched to <tt>getDriverName()</tt>.
 */
interface IDriverNames
{
   String AS400 = "AS/400 Toolbox for Java JDBC Driver";

   /* work-around for bug which means we must use "dbo" for schema */
   String FREE_TDS = "InternetCDS Type 4 JDBC driver for MS SQLServer";

   String OPTA2000 = "i-net OPTA 2000";
}
