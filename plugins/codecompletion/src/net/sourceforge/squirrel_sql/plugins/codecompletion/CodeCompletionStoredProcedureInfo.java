package net.sourceforge.squirrel_sql.plugins.codecompletion;

import net.sourceforge.squirrel_sql.client.session.ISession;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import java.util.Vector;


public class CodeCompletionStoredProcedureInfo extends CodeCompletionInfo
{
   private String _procName;
   private int _procType;
   private ISession _session;
   private String _catalog;
   private String _schema;
   private String _completionString;

   public CodeCompletionStoredProcedureInfo(String procName, int procType, ISession session, String catalog, String schema)
   {
      _procName = procName;
      _procType = procType;
      _session = session;
      _catalog = catalog;
      _schema = schema;
   }

   public String getCompareString()
   {
      return _procName;
   }

   public String getCompletionString()
   {
      try
      {
         if(null == _completionString)
         {
            _completionString = "{call " + _procName + "(";
            ResultSet res = _session.getSQLConnection().getConnection().getMetaData().getProcedureColumns(_catalog, _schema, _procName, null);

            String[] paramStrings = getParamStrings(res);

            if(0 < paramStrings.length)
            {
               _completionString += paramStrings[0];
            }

            for (int i = 1; i < paramStrings.length; i++)
            {
               _completionString += ", " + paramStrings[i];
            }

            _completionString += ")}";

         }

         return _completionString;
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   private String[] getParamStrings(ResultSet res) throws SQLException
   {
      Vector ret = new Vector();
      while(res.next())
      {
         switch(res.getInt("COLUMN_TYPE"))
         {
            case DatabaseMetaData.procedureColumnIn:
            case DatabaseMetaData.procedureColumnOut:
            case DatabaseMetaData.procedureColumnInOut:
               ret.add(getParamString(res));
         }
      }

      return (String[]) ret.toArray(new String[ret.size()]);
   }

   private String getParamString(ResultSet res) throws SQLException
   {
      String ret = "<";

      switch(res.getInt("COLUMN_TYPE"))
      {
         case DatabaseMetaData.procedureColumnIn:
            ret += "IN ";
            break;
         case DatabaseMetaData.procedureColumnOut:
            ret += "OUT ";
            break;
         case DatabaseMetaData.procedureColumnInOut:
            ret += "INOUT ";
            break;
      }

      ret += res.getString("TYPE_NAME") + " ";

      ret += res.getString("COLUMN_NAME");

      String remarks = res.getString("REMARKS");

      if(null != remarks)
      {
         ret += " " + remarks.replace('\n', ' ');
      }

      ret += ">";

      return ret;
   }

   public String toString()
   {
      return _procName + "(SP)";
   }
}
