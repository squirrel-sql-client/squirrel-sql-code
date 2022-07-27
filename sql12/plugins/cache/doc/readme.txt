Plugin to show query statistics and plan for the Intersystems Cache/IRIS database.

As a prerequisite for this Plugin a class written in COS stated below has to be imported into the Cache/IRIS server.



<?xml version="1.0" encoding="UTF-8"?>
<Export generator="Cache" version="25" zv="Cache for Windows (x86-64) 2018.1.7 (Build 721_0_21622U)" ts="2022-07-26 13:05:29">
<Class name="DBUtilities.ShowPlan">
<Super>%RegisteredObject</Super>
<TimeChanged>66316,46246.004463</TimeChanged>
<TimeCreated>65468,42221.307951</TimeCreated>

<Method name="GetPlan">
<Description>
Author : Martin Weißenborn
Date   : 29.09.2021
--------------------------------
Parameter :
1 = Namespace
2 = SQL-Querry

Discription :
Displaying an Execution Plan as  SQL procedure
You can use Show Plan to display the execution plan for a query


Call :
w ##class(DBUtilities.ShowPlan).GetPlan(Namepace,Query)
</Description>
<ClassMethod>1</ClassMethod>
<FormalSpec>pNamespace:%String="SHD",pSQL:%String=""</FormalSpec>
<ReturnType>%String</ReturnType>
<SqlName>GetPlan</SqlName>
<SqlProc>1</SqlProc>
<Implementation><![CDATA[
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
	  If $ZConvert(pSQL,"U")'["WHERE"&($ZConvert(pSQL,"U")'["TOP")&($ZConvert(pSQL,"U")'["DISTINCT")&($ZConvert(pSQL,"U")["SELECT") { set pSQL=$Replace($ZConvert(pSQL,"U"),"SELECT ","SELECT TOP 1000 ") }
	  SET myquery = pSQL
	  SET tStatement = ##class(%SQL.Statement).%New()
	  SET qStatus = tStatement.%Prepare(myquery)
	  IF qStatus'=1 { set tResult= "SQL Error : "_$SYSTEM.Status.GetErrorText(qStatus)_" SQL "_pSQL QUIT}

	  SET oldstat=$SYSTEM.SQL.SetSQLStatsJob(3)

	  SET mysql=1
	  set mysql(1)=pSQL
	  ; The 2 parameter controls the output of the plan in an array or display with write
	  ; Optional, 1 or 0, default is 0.
	  ; With 1, the resulting plan lines are in the% plan() array, otherwise the plan output is displayed with write commands.
	  ; Parameter 3 controls the output of the statistics as part of the plan text
	  ; Optional, 1 or 0, default is 0.
	  ; At 1, run the SQL query to generate statistics and output the statistics as part of the plan text.
	  DO $SYSTEM.SQL.ShowPlan(.mysql,1,1)
	  if mysql(1)["SQLCODE" { set tResult=" !,Error : "_mysql(1) quit }

	  DO $SYSTEM.SQL.SetSQLStatsJob(oldstat)
	  set tXml="<?xml version=""1.0"" encoding=""UTF-8""?>"
	  f i=1:1:%plan {
		  /// MWE 25.07.2022
		  /// Wenn IRIS dann Blanks einfügen wegen besserer Übersichtlichkeit.
		  if $SYSTEM.Version.GetMajor() >= 2022 {
			  set %plan(i)= $Replace(%plan(i),">",">      ")
			  set %plan(i)= $Replace(%plan(i),"</","      </")
		  }
		  s tXml=tXml_%plan(i)_$C(13,10)
		  }
	  s tResult=tXml
	}
	catch(tEx)
	{
        set tResult="$ZERROR: "_$ZERROR_"$ECODE: "_$ECODE

	}

	quit tResult
]]></Implementation>
</Method>
</Class>
</Export>
