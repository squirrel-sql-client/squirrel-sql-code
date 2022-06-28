package net.sourceforge.squirrel_sql.client.gui.session;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class CurrentSchemaModel
{
   private static final ILogger s_log = LoggerController.createLogger(CurrentSchemaModel.class);

   private ISession _session;
   private String _currentSchemaString;
   private boolean _noCurrentSchemaAvailable;
   private boolean _failedToLoadSchema;
   private boolean _initialized;

   public CurrentSchemaModel(ISession session)
   {
      _session = session;
   }

   public String refreshSchema(boolean logException)
   {
      _initialized = true;

      _currentSchemaString = null;
      _noCurrentSchemaAvailable = false;
      _failedToLoadSchema = false;

      try
      {
         String  buf = _session.getSQLConnection().getSchema();

         if( StringUtilities.isEmpty(buf, true) )
         {
            _currentSchemaString = "<None>";
            _noCurrentSchemaAvailable = true;
         }
         else
         {
            _currentSchemaString = buf;
         }
      }
      catch (Throwable e)
      {
         _currentSchemaString = "<Not accessible>";
         _failedToLoadSchema = true;
         if ( logException )
         {
            s_log.error("Failed to load current schema name", e);
         }
      }

      return _currentSchemaString;
   }


   public boolean isInitialized()
   {
      return _initialized;
   }

   /**
    * {@link #getCurrentSchemaString()}  will return a real Schema only if this method returns true.
    */
   public boolean isJDBCConnectionProvidesCurrentSchema()
   {
      if(false == _initialized)
      {
         throw new IllegalStateException("Needs to be initialized by calling refreshSchema() first.");
      }

      return false == _failedToLoadSchema && false == _noCurrentSchemaAvailable;
   }

   /**
    * Will return a real Schema only if {@link #isJDBCConnectionProvidesCurrentSchema()} returns true.
    */
   public String getCurrentSchemaString()
   {
      return _currentSchemaString;
   }

}
