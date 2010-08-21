package de.ixdb.squirrel_sql.plugins.cache;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Vector;

public class ScriptFunctionCommand
{
   private ISession _session;
   private CachePlugin _plugin;

   private static ILogger s_log;

   public ScriptFunctionCommand(ISession session, CachePlugin plugin)
   {
      _session = session;
      _plugin = plugin;
      if (s_log == null)
      {
         s_log = LoggerController.createLogger(getClass());
      }
   }

   public void execute()
   {
      if(false == VersionInfo.is5(_session))
      {
         VersionInfo.showNotSupported(_session);
         return;
      }
      

      String[] funcNames = getSelectedFunctions();

      if(0 == funcNames.length)
      {
         return;
      }

      String list = null;
      for (int i = 0; i < funcNames.length; i++)
      {


         if(null == list)
         {
            list = "User.func" + funcNames[i];
         }
         else
         {
            list += ",User.func" + funcNames[i];
         }

      }

      CdlAccessor.getSearchStringForCdlAccess(_session.getSessionInternalFrame().getObjectTreeAPI().getSelectedDatabaseObjects());


      Document doc = CdlAccessor.getDefinitionAsXmlDoc(list, _session.getSQLConnection().getConnection());

      StringBuffer script = new StringBuffer();
      for (int i = 0; i < funcNames.length; i++)
      {
         String ddl = getDDL(doc, funcNames[i]);
         script.append(ddl).append(getStatementSeparator()).append("\n");
      }

      if(0 < script.length())
      {
         _session.getSessionInternalFrame().getSQLPanelAPI().appendSQLScript(script.toString());
         _session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
      }

   }

   private String getDDL(Document doc, String funcName)
   {
      Element documentElement = doc.getDocumentElement();

      NodeList elementsByTagName = documentElement.getElementsByTagName("Class");


      StringBuffer ret = new StringBuffer("");
      for (int i = 0; i < elementsByTagName.getLength(); i++)
      {
         Element elem =  (Element) elementsByTagName.item(i);

         if(elem.getAttribute("name").trim().endsWith(funcName))
         {

            ret.append("CREATE FUNCTION ").append(funcName);

            Element methElem = (Element) elem.getElementsByTagName("Method").item(0);

            Element elemParams = (Element) methElem.getElementsByTagName("FormalSpec").item(0);
            String cdlParamString = elemParams.getFirstChild().getNodeValue();

            ret.append(getParams(cdlParamString));

            Element retElem = (Element) methElem.getElementsByTagName("ReturnType").item(0);
            String cdlRetString = retElem.getFirstChild().getNodeValue();
            String retString = getType(cdlRetString);
            ret.append("\nRETURNS ").append(retString).append("\nLANGUAGE COS\n{");

            Element implElem = (Element) methElem.getElementsByTagName("Implementation").item(0);
            String impl = implElem.getFirstChild().getNodeValue();
            ret.append(impl);
            ret.append("\n}");
         }
      }

      return ret.toString();
   }

   private String getParams(String cdlParamString)
   {
      String clean = removeBrackets(cdlParamString);

      String[] params = clean.split(",");

      String ret = "(";
      for (int i = 0; i < params.length; i++)
      {
         String[] pieces = params[i].split(":");

         String paramDef = pieces[0] + " " + getType(pieces[1]);
         if(0 == i)
         {
            ret += paramDef;
         }
         else
         {
            ret += ", " + paramDef;
         }
      }

      ret += ")";

      return ret;
   }

   private String removeBrackets(String inParams)
   {
      StringBuffer ret = new StringBuffer();
      int openIndex = inParams.indexOf('(');
      if(-1 == openIndex)
      {
         return inParams;
      }
      while(-1 != openIndex)
      {
         ret.append(inParams.substring(0, openIndex));
         inParams = inParams.substring(inParams.indexOf(')') + 1 , inParams.length());
         openIndex = inParams.indexOf('(');
      }
      return ret.toString();
   }


   private String getType(String cacheType)
   {
      if (cacheType.equals("%Library.TimeStamp") || cacheType.equals("%TimeStamp"))
      {
         return "DATETIME";
      }
      else if (cacheType.startsWith("%Library.String") || cacheType.startsWith("%String"))
      {
         // Die 200 braucht nur SQL Server 7.0,
         // SQL Server 2000 nicht mehr sagt SAN
         return "VARCHAR(200)";
      }
      else if (cacheType.startsWith("%Library.Integer") || cacheType.startsWith("%Integer"))
      {
         return "INTEGER";
      }
      else if (cacheType.equals("%Library.Numeric") || cacheType.equals("%Numeric"))
      {
         return "NUMERIC(18,2)";
      }
      else
      {
         return "Unbekannter Parametertyp:>" + cacheType + "<";
      }
   }



   private String getStatementSeparator()
   {
      String statementSeparator = _session.getProperties().getSQLStatementSeparator();

      if (1 < statementSeparator.length())
      {
         statementSeparator = "\n" + statementSeparator + "\n";
      }

      return statementSeparator;
   }


   private String[] getSelectedFunctions()
   {
      IDatabaseObjectInfo[] dbObjs = _session.getSessionInternalFrame().getObjectTreeAPI().getSelectedDatabaseObjects();

      Vector ret = new Vector();
      for (int i = 0; i < dbObjs.length; i++)
      {
         if (dbObjs[i] instanceof IProcedureInfo)
         {
            IProcedureInfo pi = (IProcedureInfo) dbObjs[i];
            String sFunc = pi.getSimpleName();
            ret.add(sFunc);
         }
      }
      return (String[]) ret.toArray(new String[0]);
   }
}
