package net.sourceforge.squirrel_sql.client.cli;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.encryption.AliasPasswordHandler;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.QueryTokenizer;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.sql.SQLException;

public class CliSession extends CliSessionAdapter
{
   private final SQLConnection _sqlConnection;
   private final QueryTokenizer _tokenizer;
   private final SessionProperties _sessionProperties;
   private final SQLAlias _aliasToConnectTo;

   public CliSession(SQLAlias aliasToConnectTo)
   {
      try
      {
         _aliasToConnectTo = aliasToConnectTo;

         IIdentifier driverID = aliasToConnectTo.getDriverIdentifier();
         ISQLDriver sqlDriver = Main.getApplication().getAliasesAndDriversManager().getDriver(driverID);


         SQLDriverManager sqlDriverManager = Main.getApplication().getSQLDriverManager();


         SQLDriverPropertyCollection props = aliasToConnectTo.getDriverPropertiesClone();

         if (!aliasToConnectTo.getUseDriverProperties())
         {
            props.clear();
         }


         _sqlConnection = sqlDriverManager.getConnection(sqlDriver, aliasToConnectTo, aliasToConnectTo.getUserName(), AliasPasswordHandler.getPassword(aliasToConnectTo), props);


         _sessionProperties = Main.getApplication().getSquirrelPreferences().getSessionProperties();

         _tokenizer = getNewQueryTokenizer();

      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   public QueryTokenizer getNewQueryTokenizer()
   {
      return new QueryTokenizer(_sessionProperties.getSQLStatementSeparator(),
                                _sessionProperties.getStartOfLineComment(),
                                _sessionProperties.getRemoveMultiLineComment(),
                                _sessionProperties.getRemoveLineComment());
   }

   @Override
   public ISQLConnection getSQLConnection()
   {
      return _sqlConnection;
   }

   @Override
   public ISQLDatabaseMetaData getMetaData()
   {
      return _sqlConnection.getSQLMetaData();
   }

   @Override
   public IQueryTokenizer getQueryTokenizer()
   {
      return _tokenizer;
   }

   @Override
   public SessionProperties getProperties()
   {
      return _sessionProperties;
   }

   @Override
   public void close() throws SQLException
   {
      // Close on _sqlConnection will start threads
      // that will prevent ending the process when in Batch mode.
      _sqlConnection.getConnection().close();
   }

   @Override
   public SQLAlias getAlias()
   {
      return _aliasToConnectTo;
   }
}
