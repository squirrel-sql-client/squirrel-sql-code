package net.sourceforge.squirrel_sql.plugins.codecompletion;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.client.session.SQLTokenListener;
import net.sourceforge.squirrel_sql.plugins.codecompletion.CodeCompletionInfo;
import net.sourceforge.squirrel_sql.plugins.codecompletion.CodeCompletionInfoCollection;

import java.util.*;
import java.sql.SQLException;

public class CodeCompletorModel
{
   private ISession _session;
   private ILogger _log = LoggerController.createLogger(CodeCompletorModel.class);
   private CodeCompletionInfoCollection _codeCompletionInfos;

	// The methods performTableOrViewFound() and setLastSelectedCompletion() take care
	// that only one of these two attributes is not null.
   private CodeCompletionInfo _lastSelectedCompletion;
	private String _lastSelectedCompletionName;

   CodeCompletorModel(ISession session, CodeCompletionInfoCollection codeCompletionInfos)
   {
      try
      {
         _session = session;
         _codeCompletionInfos = codeCompletionInfos;
      }
      catch(Exception e)
      {
         _log.error("Could not get DB-Meta data", e);
      }
   }

   CodeCompletionInfo[] getCompletionInfos(String beginning)
   {
      StringTokenizer st = new StringTokenizer(beginning, ".");
      Vector buf = new Vector();
      while(st.hasMoreTokens())
      {
         buf.add(st.nextToken());
      }

      String tableNamePat = "";
      String colNamePat = null;

      if(1 < buf.size())
      {
         tableNamePat = (String)buf.get(buf.size() - 2);
         colNamePat = (String)buf.get(buf.size() - 1);
      }
      else if( 1 == buf.size())
      {
         tableNamePat = (String)buf.get(0);
         if(beginning.endsWith("."))
         {
            colNamePat = "";
         }
      }

      if(null == colNamePat)
      {
			// The colums of the last completed table/view that match the tableNamePat
			// will be returned on top of the collection
			Vector ret = new Vector();
			ret.addAll( Arrays.asList(getColumnsFromLasteSelectionStartingWith(tableNamePat)) );
			ret.addAll( Arrays.asList(_codeCompletionInfos.getInfosStartingWith(tableNamePat)) );
			return (CodeCompletionInfo[])ret.toArray(new CodeCompletionInfo[0]);
      }
      else
      {
			return getColumnsForName(tableNamePat, colNamePat);
      }
   }

	private CodeCompletionInfo[] getColumnsForName(String name, String colNamePat)
	{
		CodeCompletionInfo[] infos = _codeCompletionInfos.getInfosStartingWith(name);
		String upperCaseTableNamePat = name.toUpperCase();
		for(int i=0; i < infos.length; ++i)
		{
			if( infos[i].upperCaseCompletionStringEquals(upperCaseTableNamePat))
			{
				try
				{
					return infos[i].getColumns(_session.getSQLConnection().getSQLMetaData().getJDBCMetaData(), colNamePat);
				}
				catch(SQLException e)
				{
					_log.error("Error retrieving columns", e);
				}
			}
		}
		return new CodeCompletionInfo[0];
	}



   private CodeCompletionInfo[] getColumnsFromLasteSelectionStartingWith(String colNamePat)
   {
      try
      {
			if(null != _lastSelectedCompletion)
			{
				return _lastSelectedCompletion.getColumns(_session.getSQLConnection().getSQLMetaData().getJDBCMetaData(), colNamePat);
			}
			else if(null != _lastSelectedCompletionName)
			{
				return getColumnsForName(_lastSelectedCompletionName, colNamePat);
			}

			return new CodeCompletionInfo[0];
      }
      catch(SQLException e)
      {
         _log.error("Error retrieving columns", e);
         return new CodeCompletionInfo[0];
      }
   }

   public void setLastSelectedCompletion(CodeCompletionInfo lastSelectedCompletion)
   {
      if(lastSelectedCompletion.hasColumns())
      {
			_lastSelectedCompletionName = null;
         _lastSelectedCompletion = lastSelectedCompletion;
      }
   }

	public SQLTokenListener getSQLTokenListener()
	{
		return
			new SQLTokenListener()
			{
				public void tableOrViewFound(String name)
				{performTableOrViewFound(name);}
			};
	}

	private void performTableOrViewFound(String name)
	{
		_lastSelectedCompletion = null;
   	_lastSelectedCompletionName = name;
	}


////////////////////////////////////////////////////////////
// TEST ONLY
///////////////////////////////////////////////////////////


//   Vector _data = null;
//   String[] getCompletionStrings(String beginning)
//   {
//      if(null == _data)
//      {
//         _data = new Vector();
//         for(int i=0; i < 20; ++i)
//         {
//            _data.add("Birgit" + i);
//            _data.add("Sara" + i);
//            _data.add("Anna" + i);
//            _data.add("Gerd" + i);
//         }
//      }
//      return (String[])_data.toArray(new String[0]);
//   }
//
//   String getCompletionStringAt(int index)
//   {
//      return (String)_data.get(index);
//   }
}
