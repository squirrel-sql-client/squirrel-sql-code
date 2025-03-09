package net.sourceforge.squirrel_sql.plugins.codecompletion;

import net.sourceforge.squirrel_sql.client.session.parser.kernel.JoinOnClauseParseInfo;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.TableAliasParseInfo;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.TableParseInfo;

public class JoinLookupResult
{
   private boolean _endsWithJoinKeyword;
   private TableAliasParseInfo _tableAliasParseInfo;
   private TableParseInfo _tableParseInfo;
   private boolean _afterOnKeyword;

   private JoinLookupResult()
   {
   }

   public boolean isEndsWithJoinKeyword()
   {
      return _endsWithJoinKeyword;
   }

   public TableAliasParseInfo getTableAliasParseInfo()
   {
      return _tableAliasParseInfo;
   }

   public TableParseInfo getTableParseInfo()
   {
      return _tableParseInfo;
   }

   public JoinOnClauseParseInfo getJoinOnClauseParseInfo()
   {
      if(null != _tableAliasParseInfo)
      {
         return _tableAliasParseInfo;
      }
      else if(null != _tableParseInfo)
      {
         return _tableParseInfo;
      }

      return null;
   }

   public boolean isAfterOnKeyword()
   {
      return _afterOnKeyword;
   }

   public static JoinLookupResult ofEndsWithJoinKeyword(boolean afterOnKeyword)
   {
      JoinLookupResult ret = new JoinLookupResult();
      ret._endsWithJoinKeyword = true;
      ret._afterOnKeyword = afterOnKeyword;
      return ret;
   }

   public static JoinLookupResult ofTableAlias(TableAliasParseInfo tableAliasParseInfo, boolean afterOnKeyword)
   {
      JoinLookupResult ret = new JoinLookupResult();
      ret._tableAliasParseInfo = tableAliasParseInfo;
      ret._afterOnKeyword = afterOnKeyword;
      return ret;
   }

   public static JoinLookupResult ofTable(TableParseInfo tableParseInfo, boolean afterOnKeyword)
   {
      JoinLookupResult ret = new JoinLookupResult();
      ret._tableParseInfo = tableParseInfo;
      ret._afterOnKeyword = afterOnKeyword;
      return ret;
   }

   public static JoinLookupResult empty()
   {
      return new JoinLookupResult();
   }

}
