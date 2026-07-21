package net.sourceforge.squirrel_sql.plugins.codecompletion;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;

import net.sourceforge.squirrel_sql.client.session.ExtendedColumnInfo;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.ParenthesedSelectInfo;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.plugins.codecompletion.prefs.CodeCompletionPreferences;

public class CodeCompletionParenthesedSelectInfo extends CodeCompletionTableInfo
{
   private final ParenthesedSelectInfo _parenthesedSelectInfo;
   private final boolean _useCompletionPrefs;
   private final CodeCompletionPreferences _prefs;
   private ArrayList<CodeCompletionColumnInfo> _paranthesedColInfos;

   public CodeCompletionParenthesedSelectInfo(ParenthesedSelectInfo info, ISession session, boolean useCompletionPrefs, CodeCompletionPreferences prefs)
   {
      super(info.getAlias(), "SUB_SELECT", getCatalog(session), getSchema(session), useCompletionPrefs, prefs, session);

      _parenthesedSelectInfo = info;
      _useCompletionPrefs = useCompletionPrefs;
      _prefs = prefs;
   }

   private static String getCatalog(ISession session)
   {
      try
      {
         return session.getSQLConnection().getCatalog();
      }
      catch(SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private static String getSchema(ISession session)
   {
      try
      {
         return session.getSQLConnection().getSchema();
      }
      catch(SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }


   public boolean isInStatementOfParenthesedSelect(int pos)
   {
      return _parenthesedSelectInfo.isInStatementOfParenthesedSelect(pos);
   }

   @Override
   public ArrayList<? extends CodeCompletionInfo> getColumns(SchemaInfo schemaInfo, String colNamePattern)
   {
      if(null == _paranthesedColInfos || _paranthesedColInfos.isEmpty() )
      {
         _paranthesedColInfos = new ArrayList<>();
         for( ExtendedColumnInfo column : _parenthesedSelectInfo.getColumns() )
         {
            CodeCompletionColumnInfo buf =
                  new CodeCompletionColumnInfo(column.getSimpleTableName(), column.getColumnName(), column.getRemarks(), column.getColumnType(),
                                               column.getColumnSize(), column.getDecimalDigits(), column.isNullable(), _prefs);

            _paranthesedColInfos.add(buf);
         }
      }

      String trimmedColNamePattern = colNamePattern.trim();

      ArrayList<CodeCompletionColumnInfo> ret = new ArrayList<>();

      if("".equals(trimmedColNamePattern))
      {
         ret = _paranthesedColInfos;
      }
      else
      {
         for (CodeCompletionColumnInfo colInfo : _paranthesedColInfos)
         {
            if (colInfo.matchesCompletionStringStart(trimmedColNamePattern, CompletionMatchTypeUtil.matchTypeOf(_useCompletionPrefs, _prefs)))
            {
               ret.add(colInfo);
            }
         }
      }

      if (_prefs.isSortColumnsAlphabetically())
      {
         ret.sort(Comparator.comparing(codeCompletionColumnInfo -> codeCompletionColumnInfo.getCompareString().toUpperCase()));
      }

      return ret;
   }
}
