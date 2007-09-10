package de.ixdb.squirrel_sql.plugins.cache;

import com.intersys.cache.CacheObject;
import com.intersys.cache.Dataholder;
import com.intersys.cache.jbind.JBindDatabase;
import com.intersys.classes.CharacterStream;
import com.intersys.objects.CacheDatabase;
import com.intersys.objects.CacheReader;
import com.intersys.objects.Database;
import com.intersys.objects.CacheException;
import net.n3.nanoxml.*;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.util.Resources;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.StringReader;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.Statement;


public class ShowQueryPlanCommand
{
   private ISession _session;

   public static final String HREF_CLOSE_QUERY_PLAN = "#close query plan";
   private QueryPlanTab _queryPlanTab;

   public ShowQueryPlanCommand(ISession session)
	{
      _session = session;
   }

	public void execute()
	{
      try
      {

         String selectSQL = _session.getSessionInternalFrame().getSQLPanelAPI().getSQLScriptToBeExecuted();

         if(null == selectSQL || 0 == selectSQL.trim().length())
         {
            JOptionPane.showMessageDialog(_session.getApplication().getMainFrame(), "No statement selected.\nCan not retrieve query plan.\nPlease select a statement");
            return;
         }


         String xml = getExecutionPlanXmlFromCache(selectSQL);

         if(null == xml || 0 == xml.trim().length())
         {
            JOptionPane.showMessageDialog(_session.getApplication().getMainFrame(), "Cache returned an empty execution plan.\nThere is probably something wrong with your query.\nTry to execute your query first to test it.");
            return;
         }

         String excutionPlanHTML;

         Exception toThrowAtEnde = null;
         try
         {
            excutionPlanHTML = createExcutionPlanHTML(xml, selectSQL);
         }
         catch (Exception e)
         {
            _session.showErrorMessage("Failed to create a HTML page from Cache's query plan xml:\n"  + e.toString());
            excutionPlanHTML = createPlainXmlPlanHTML(xml);
            toThrowAtEnde = e;
         }

         QueryPlanTabListener qtl = new QueryPlanTabListener()
         {
            public void closeRequested()
            {
               _session.getSessionSheet().removeMainTab(_queryPlanTab);
            }
         };

         _queryPlanTab = new QueryPlanTab(excutionPlanHTML, qtl);
         int index = _session.getSessionSheet().addMainTab(_queryPlanTab);
         _session.getSessionSheet().selectMainTab(index);

         if(null != toThrowAtEnde)
         {
            throw new RuntimeException(toThrowAtEnde);
         }

      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private String createPlainXmlPlanHTML(String xml)
   {

      xml = xml.replaceAll("<", "&lt;");
      xml = xml.replaceAll(">", "&gt;");

      StringBuffer html = new StringBuffer("<HTML>");

      html.append("<h4 align=\"right\"><a href=\"" + HREF_CLOSE_QUERY_PLAN + "\">close query plan</a></h4>\n");
      html.append("<h3>Failed to create a HTML page from Cache's query plan xml.  Please inform de.ixdb about the error. Below is the raw xml.</h3>\n");
      html.append("<h4><pre>\n").append(xml).append("\n</pre></h4>");
      html.append("</HTML>");

      return html.toString();

   }

   private String getExecutionPlanXmlFromCache(String selectSQL)
   {
      try
      {
         Dataholder[] argv = new Dataholder[1];
         argv[0] = Dataholder.create(selectSQL);

         Database conn =  (JBindDatabase) CacheDatabase.getDatabase(_session.getSQLConnection().getConnection());
         //Dataholder res = conn.runClassMethod("%Library.CMUtilities", "getExecutionPlan", argv, Database.RET_OBJECT);

         Dataholder res = null;
         try
         {
            res = conn.runClassMethod("CM.methM2", "M2", argv, Database.RET_OBJECT);
         }
         catch (CacheException e)
         {
            Statement stat = _session.getSQLConnection().createStatement();
            stat.executeUpdate
            (

            "CREATE METHOD CM.M2(IN sql %String)" +
                     "  RETURNS %GlobalCharacterStream" +
                     "  LANGUAGE COS" +
                     "  {" +
                     "     quit:$get(sql)=\"\" \"\"" +
                     "     kill %plan\n" +
                     "     set outStream = ##class(%GlobalCharacterStream).%New()" +
                     "     set sql(1)=sql,sql=1" +
                     "     do ShowPlan^%apiSQL(.sql,1)" +
                     "     set nSub = 1,line = \"\"" +
                     "     set to = +$g(%plan)+1" +
                     "     while nSub<to " +
                     "     {" +
                     "        set line = $g(%plan(nSub))" +
                     "        do outStream.WriteLine(line)" +
                     "        set nSub = nSub + 1" +
                     "     }" +
                     "     kill %plan\n" +
                     "     quit outStream" +
                     "  }"
            );
            stat.close();

            res = conn.runClassMethod("CM.methM2", "M2", argv, Database.RET_OBJECT);
         }


         CacheObject cobj = res.getCacheObject();
         CharacterStream characterStream = (CharacterStream) (cobj.newJavaInstance());

         CacheReader reader = characterStream.getReader();

         StringBuffer sb = new StringBuffer();
         sb.append("");


         char[] buf = new char[50];
         int count = reader.read(buf);
         while(true)
         {

            for (int i = 0; i < count; i++)
            {
               sb.append(buf[i]);
            }

            if(count < 50)
            {
               break;
            }

            count = reader.read(buf);
         }
         return sb.toString();
      }
      catch (Exception e)
      {
         _session.showErrorMessage("Failed to retrieve execution plan from Cache:\n" + e);
         throw new RuntimeException(e);
      }
   }

   private String createExcutionPlanHTML(String execPlanXml, String selectSql)
      throws ClassNotFoundException, InstantiationException, IllegalAccessException, XMLException
   {
      StringReader sr = new StringReader(execPlanXml);
      IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
      parser.setReader(new StdXMLReader(sr));
      IXMLElement element = (IXMLElement) parser.parse();

      StringBuffer html = new StringBuffer("<HTML>");

      html.append("<h4 align=\"right\"><a href=\"" + HREF_CLOSE_QUERY_PLAN + "\">close query plan</a></h4>\n");


      Vector vKids = element.getChildren();
      IXMLElement ePlan = (IXMLElement) vKids.get(2);
      if(null != ePlan.getName())
      {
         throw new IllegalStateException("Could not find untaged text element containing the plan. Expected this element to be #3 of the plan tag's kids.");
      }
      html.append(getHeader("query plan", "#66FF99"));
      String plan = ePlan.getContent();
      html.append(createHtmlFromText(plan));


      Vector vModules = element.getChildrenNamed("module");
      for (int i = 0; i < vModules.size(); i++)
      {
         IXMLElement eModule = (IXMLElement) vModules.elementAt(i);
         String module = eModule.getContent();
         html.append(getHeader("module " + eModule.getAttribute("name", ""), "#FFFF99"));
         html.append(createHtmlFromText(module));
      }

      Vector vSubqueries = element.getChildrenNamed("subquery");
      for (int i = 0; i < vSubqueries.size(); i++)
      {
         IXMLElement eSubquery = (IXMLElement) vSubqueries.elementAt(i);
         String subquery = eSubquery.getContent();
         html.append(getHeader("subquery", "#C0C0C0"));
         html.append(createHtmlFromText(subquery));
      }


      Vector vExpressions = element.getChildrenNamed("expression");
      for (int i = 0; i < vExpressions.size(); i++)
      {
         IXMLElement eExpression = (IXMLElement) vExpressions.elementAt(i);
         String expression = eExpression.getContent();
         html.append(getHeader("expression", "#7FFFD4"));
         html.append(createHtmlFromText(expression));
      }





      Vector vCost = element.getChildrenNamed("cost");
      String cost = "unknown";
      if(1 == vCost.size())
      {
         IXMLElement eCost = (IXMLElement) vCost.get(0);
         cost = eCost.getAttribute("value", "");
      }
      html.append(getHeader("cost", "#FAEBD7"));
      html.append("<h4><ul><li>" + cost + "</li></ul></h4>");


      html.append(getHeader("sql", "#CCCCFF"));
      html.append("<h4><ul><li><pre>").append(selectSql).append("</pre></li></ul></h4>");

      html.append("</HTML>");

      return html.toString();
   }

   private String createHtmlFromText(String text)
   {
      Pattern pattern = Pattern.compile("module [A-Z]");
      Matcher matcher = pattern.matcher(text);

      while(matcher.find())
      {
         String group = matcher.group();
         text = text.replaceAll(group, "<a href=\"#" + group + "\">" + group + "</a>");
      }

      String[] lines = text.split("\n");

      StringBuffer ret = new StringBuffer();

      ret.append("<h4>");
      ret.append("<ul><li>");
      int indentDepth = getIndentDepth(lines[0]);
      for (int i = 0; i < lines.length; i++)
      {

         int newIndentDepth = indentDepth;
         if(i < lines.length - 1)
         {
            newIndentDepth = getIndentDepth(lines[i+1]);
         }

         if(newIndentDepth > indentDepth)
         {
            if(lines[i].trim().endsWith(":"))
            {
               ret.append("<br><br>").append(lines[i]).append("\n");
            }
            else
            {
               ret.append(lines[i]).append("\n");
            }

            ret.append("</li><ul><li>");
         }
         else if(newIndentDepth < indentDepth)
         {
            ret.append(lines[i]).append("\n");
            ret.append("</li></ul><li>");
         }
         else //  newIndentDepth == indentDepth
         {
            ret.append(lines[i]).append("\n");
         }

         indentDepth = newIndentDepth;
      }


      for(int i=0; i < indentDepth; ++i)
      {
         ret.append("</ul>");
      }
      ret.append("</h4>");

     return ret.toString();
   }

   private int getIndentDepth(String line)
   {
      int leadingspaceCount = 0;

      for (int i = 0; i < line.length() && ' ' == line.charAt(i); i++)
      {
         ++leadingspaceCount;
      }

      return leadingspaceCount / 4 + 1;

   }

   private String getHeader(String name, String bgColor)
   {
      return
         "<P>\n" +
         "<A NAME=\"#" + name + "\"></A>\n" +
         "<TABLE BORDER=\"1\" WIDTH=\"100%\" CELLPADDING=\"3\" CELLSPACING=\"0\" SUMMARY=\"\">\n" +
         "<TR BGCOLOR=\"" + bgColor + "\" CLASS=\"TableHeadingColor\">\n" +
         "<TH ALIGN=\"left\" COLSPAN=\"2\"><FONT SIZE=\"+2\">\n" +
         "<B>" + name + "</B></FONT></TH>\n" +
         "</TR>\n" +
         "</TABLE>\n" +
         "</P>\n";

   }


}
