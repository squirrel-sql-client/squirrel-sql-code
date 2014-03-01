package net.sourceforge.squirrel_sql.plugins.codecompletion;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.codecompletion.prefs.CodeCompletionPreferences;
import net.sourceforge.squirrel_sql.plugins.codecompletion.prefs.PrefixedConfig;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import java.util.Vector;


public class CodeCompletionStoredProcedureInfo extends CodeCompletionInfo
{
   private String _procName;
   private int _procType;
   private ISession _session;
   private boolean _useCompletionPrefs;
   private String _catalog;
   private String _schema;
	private int _moveCarretBackCount = 0;

   private String _toString;
   
   private String _params = null;

	/**
	 * This Sessions prefs. Note it is just a reference. Contents change when user changes Session properties.
	 */
	private CodeCompletionPreferences _prefs;

   public CodeCompletionStoredProcedureInfo(String procName, int procType, ISession session, String catalog, String schema, boolean useCompletionPrefs, CodeCompletionPreferences prefs)
   {
      _procName = procName;
      _procType = procType;
      _session = session;
      _useCompletionPrefs = useCompletionPrefs;
      _prefs = prefs;
      _catalog = catalog;
      _schema = schema;

      _toString = _procName + " (SP)";

   }

   public String getCompareString()
   {
      return _procName;
   }

   public String getCompletionString()
   {
      try
      {
         if(false == _useCompletionPrefs)
         {
            _moveCarretBackCount = 0;
            return _procName;   
         }


         String ret = "";

			int completionConfig = getCopmpletionConfig();

			_moveCarretBackCount = 0;

			String params = getParams();
			switch (completionConfig)
			{
				case CodeCompletionPreferences.CONFIG_SP_WITH_PARARMS:
					ret = "{call " + _procName + "(" + params + ")}";
					if (0 < params.length())
					{
						_moveCarretBackCount = params.length() + 2;
					}
					break;
				case CodeCompletionPreferences.CONFIG_SP_WITHOUT_PARARMS:
					ret = "{call " + _procName + "()}";
					if (0 < params.length())
					{
						_moveCarretBackCount = 2;
					}
					break;
				case CodeCompletionPreferences.CONFIG_UDF_WITH_PARARMS:
					ret = _procName + "(" + params + ")";
					if (0 < params.length())
					{
						_moveCarretBackCount = params.length() + 1;
					}
					break;
				case CodeCompletionPreferences.CONFIG_UDF_WITHOUT_PARARMS:
					if (0 < params.length())
					{
						_moveCarretBackCount = 1;
					}
					ret = _procName + "()";
					break;
			}

			return ret;
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

	private int getCopmpletionConfig()
	{
		PrefixedConfig[] prefixedConfigs = _prefs.getPrefixedConfigs();

		for (int i = 0; i < prefixedConfigs.length; i++)
		{
			if(_procName.toUpperCase().startsWith(prefixedConfigs[i].getPrefix().toUpperCase()))
			{
				return prefixedConfigs[i].getCompletionConfig();
			}
		}

		return _prefs.getGeneralCompletionConfig();
	}


	private String getParams() throws SQLException
	{
		if (null == _params)
		{
			ResultSet res = _session.getSQLConnection().getConnection().getMetaData().getProcedureColumns(_catalog, _schema, _procName, null);

			Vector<String> ret = new Vector<String>();
			while (res.next())
			{
				switch (res.getInt("COLUMN_TYPE"))
				{
					case DatabaseMetaData.procedureColumnIn:
					case DatabaseMetaData.procedureColumnOut:
					case DatabaseMetaData.procedureColumnInOut:
						ret.add(getParamString(res));
				}
			}
			res.close();

			String[] _paramStrings = ret.toArray(new String[ret.size()]);


			_params = "";
			if (0 < _paramStrings.length)
			{
				_params += _paramStrings[0];
			}

			for (int i = 1; i < _paramStrings.length; i++)
			{
				_params += ", " + _paramStrings[i];
			}
		}

		return _params;
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
      return _toString;
   }

	/**
	 * Will be called after getCompletionString()
	 * @return Position to move the carret back counted from the end of the completion string.
	 */
	public int getMoveCarretBackCount()
	{
		return _moveCarretBackCount;
	}

    /**
     * @return the _procType
     */
    public int getProcType() {
        return _procType;
    }

    /**
     * @param type the _procType to set
     */
    public void setProcType(int type) {
        _procType = type;
    }
}
