package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class TableAndAliasParseResult
{
   private List<TableAliasParseInfo> _tableAliasParseInfos = new ArrayList<>();
   private List<TableParseInfo> _tableParseInfos = new ArrayList<>();

   public List<TableAliasParseInfo> getTableAliasParseInfosReadOnly()
   {
      return List.copyOf(_tableAliasParseInfos);
   }

   public List<TableParseInfo> getTableParseInfosReadOnly()
   {
      return List.copyOf(_tableParseInfos);
   }

   public void removeFromTableParseInfos(TableAliasParseInfo tableAliasParseInfo)
   {
      _tableParseInfos.removeIf(tpi -> tpi.matches(tableAliasParseInfo.getTableQualifier(), tableAliasParseInfo.getStatBegin()));
   }

   public List<JoinOnClauseParseInfo> getAllJoinOnClauseParseInfosReadOnly()
   {
      ArrayList<JoinOnClauseParseInfo> ret = new ArrayList<>();
      ret.addAll(_tableParseInfos);
      ret.addAll(_tableAliasParseInfos);

      return ret;
   }

   public boolean isEmpty()
   {
      return _tableAliasParseInfos.isEmpty() && _tableParseInfos.isEmpty();
   }

   public void addTableAliasInfo(TableAliasParseInfo tableAliasParseInfo)
   {
      if(tableAliasParseInfo.getAliasName().startsWith("#"))
      {
         return;
      }

      _tableAliasParseInfos.add(tableAliasParseInfo);
   }

   public void addTableParseInfo(TableParseInfo tableParseInfo)
   {
      _tableParseInfos.add(tableParseInfo);
   }

   public void addParseResult(TableAndAliasParseResult toAdd)
   {
      _tableAliasParseInfos.addAll(toAdd._tableAliasParseInfos);
      _tableParseInfos.addAll(toAdd._tableParseInfos);
   }

   public TableAliasParseInfo getAliasInStatementAt(String token, int pos)
   {
      if(StringUtils.isEmpty(token))
      {
         return null;
      }

      return
            _tableAliasParseInfos.stream()
                                 .filter(i -> StringUtils.endsWithIgnoreCase(token, i.getAliasName()) && i.getStatBegin() <= pos && pos <= i.getStatEnd())
                                 .findFirst()
                                 .orElse(null);
   }

   public TableParseInfo getTableInStatementAt(String token, int pos)
   {
      if(StringUtils.isEmpty(token))
      {
         return null;
      }

      return
            _tableParseInfos.stream()
                                 .filter(i -> StringUtils.endsWithIgnoreCase(token, i.getTableName()) && i.getStatBegin() <= pos && pos <= i.getStatEnd())
                                 .findFirst()
                                 .orElse(null);
   }
}
