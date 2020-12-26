package net.sourceforge.squirrel_sql.plugins.oracle.tab;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourcePanel;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformatException;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformatorConfigFactory;
import net.sourceforge.squirrel_sql.client.util.codereformat.CommentSpec;
import net.sourceforge.squirrel_sql.fw.dialects.CreateScriptPreferences;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

final class OracleSourcePanel extends BaseSourcePanel
{
   private final static ILogger s_log = LoggerController.createLogger(OracleSourceTab.class);

   private static CommentSpec[] commentSpecs = new CommentSpec[]
         {
               new CommentSpec("/*", "*/"),
               new CommentSpec("--", "\n")
         };

   private static CodeReformator formatter = new CodeReformator(CodeReformatorConfigFactory.createConfig(";", commentSpecs));


   private int _sourceType;
   private DatabaseObjectInfoProvider _databaseObjectInfoProvider;

   OracleSourcePanel(ISession session, int sourceType, DatabaseObjectInfoProvider databaseObjectInfoProvider)
   {
      super(session);
      _sourceType = sourceType;
      _databaseObjectInfoProvider = databaseObjectInfoProvider;
   }

   public void load(ISession session, PreparedStatement stmt)
   {
      ResultSet rs = null;
      try
      {
         rs = stmt.executeQuery();
         StringBuffer buf = new StringBuffer(4096);
         while (rs.next())
         {
            String line1 = rs.getString(1);
            String line2 = rs.getString(2);
            buf.append(line1.trim() + " ");
            buf.append(line2.trim() + " ");
         }
         String source = "";
         if (buf.length() == 0 && _sourceType == OracleSourceTab.TABLE_TYPE)
         {
            ISQLDatabaseMetaData md = session.getMetaData();
            // TODO: Need to define a better approach to getting dialects.
            // That is, we don't really want to ever prompt the user in this
            // case.  It's always Oracle.  Yet, we may have a new OracleDialect
            // at some point.
            HibernateDialect dialect = DialectFactory.getDialect("Oracle");

            // TODO: How to let the user customize this??
            CreateScriptPreferences prefs = new CreateScriptPreferences();

            ITableInfo[] tabs = new ITableInfo[]{(ITableInfo) _databaseObjectInfoProvider.getDatabaseObjectInfo()};
            List<ITableInfo> tables = Arrays.asList(tabs);
            // Handle table source
            List<String> sqls = dialect.getCreateTableSQL(tables, md, prefs, false);
            String sep = session.getQueryTokenizer().getSQLStatementSeparator();
            for (String sql : sqls)
            {
               buf.append(sql);
               buf.append(sep);
               buf.append("\n");
            }
            source = buf.toString();
         }
         else
         {
            source = buf.toString();
            try
            {
               source = formatter.reformat(buf.toString());
            }
            catch (CodeReformatException e)
            {
               Main.getApplication().getMessageHandler().showErrorMessage(e);
               s_log.error(e);
            }
         }
         getTextArea().setText(source);
         getTextArea().setCaretPosition(0);
      }
      catch (SQLException ex)
      {
         s_log.error("Unexpected exception: " + ex.getMessage(), ex);
         session.showErrorMessage(ex);
      }
      finally
      {
         SQLUtilities.closeResultSet(rs);
      }

   }


}
