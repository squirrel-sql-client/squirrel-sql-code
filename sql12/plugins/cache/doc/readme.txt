Plugin to show query statistics and plan for the Intersystems Cache database.

As a prerequisite for this Plugin a class written in COS stated below has to be imported into the Cache server.



Class DBUtilities.ShowPlan Extends %RegisteredObject
{

/// Author : Martin Weissenborn
/// Date   : 29.09.2021
/// --------------------------------
/// Parameter :
/// 1 = Namespace
/// 2 = SQL-Querry
///
/// Discription :
/// Displaying an Execution Plan as  SQL procedure
/// You can use Show Plan to display the execution plan for a query
///
///
ClassMethod GetPlan(pNamespace As %String = "SHD", pSQL As %String = "") As %String [ SqlName = GetPlan, SqlProc ]
{
   #dim tXml,tResult as %Library.String=""
   #dim tEx as %Exception.AbstractException
   #dim tSC as %Status = $$$OK
   Try {
   /// ---------------------------------------------------------------------------------------------------------------------------------
     new $NAMESPACE
     set $NAMESPACE = pNamespace
     DO $SYSTEM.SQL.SetSQLStatsJob(2)
    ; First check if the SQL can be executed.
    ; If the SQL is faulty, the error message is returned because the command "DO $ SYSTEM.SQL.ShowPlan (.mysql, 1,1)"
    ; causes a close of the session in SQuirreL.

    ; If it is a select query and there is no where and no top in the select, then a select top 1000 is inserted,
    ; so that the query doesn't run forever.
     If $SYSTEM.SQL.SQLUPPER(pSQL)'["WHERE"&($SYSTEM.SQL.SQLUPPER(pSQL)'["TOP")&($SYSTEM.SQL.SQLUPPER(pSQL)["SELECT") { set pSQL=$Replace($SYSTEM.SQL.SQLUPPER(pSQL),"SELECT ","SELECT TOP 1000 ") }

     SET myquery = pSQL
     SET tStatement = ##class(%SQL.Statement).%New()
     SET qStatus = tStatement.%Prepare(myquery)
     IF qStatus'=1 { set tResult= "SQL Error : "_$SYSTEM.Status.GetErrorText(qStatus) QUIT}

     SET oldstat=$SYSTEM.SQL.SetSQLStatsJob(3)

     SET mysql=1
     set mysql(1)=pSQL
     ; The 2 parameter controls the output of the plan in an array or display with write
     ; Optional, 1 or 0, default is 0.
     ; With 1, the resulting plan lines are in the% plan () array, otherwise the plan output is displayed with write commands.
     ; Parameter 3 controls the output of the statistics as part of the plan text
     ; Optional, 1 or 0, default is 0.
     ; At 1, run the SQL query to generate statistics and output the statistics as part of the plan text.
     DO $SYSTEM.SQL.ShowPlan(.mysql,1,1)
     if mysql(1)["SQLCODE" { set tResult=" !,Error : "_mysql(1) quit }

     DO $SYSTEM.SQL.SetSQLStatsJob(oldstat)
     set tXml="<?xml version=""1.0"" encoding=""UTF-8""?>"
     f i=1:1:%plan {
        s tXml=tXml_%plan(i)_$C(13,10)
        }
     s tResult=tXml
   }
   catch(tEx)
   {
        set tResult="$ZERROR: "_$ZERROR_"$ECODE: "_$ECODE

   }

   quit tResult
}

}
