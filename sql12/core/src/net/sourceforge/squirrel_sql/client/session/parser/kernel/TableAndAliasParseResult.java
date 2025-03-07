package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import java.util.ArrayList;
import java.util.List;

public class TableAndAliasParseResult
{
   private List<TableAliasParseInfo> _tableAliasParseInfos = new ArrayList<>();
   private List<TableParseInfo> _tableParseInfo = new ArrayList<>();

   public List<TableAliasParseInfo> getTableAliasParseInfosReadOnly()
   {
      return List.copyOf(_tableAliasParseInfos);
   }

   public List<TableParseInfo> getTableParseInfosReadOnly()
   {
      return List.copyOf(_tableParseInfo);
   }

   public void removeFromTableParseInfos(TableAliasParseInfo tableAliasParseInfo)
   {
      _tableParseInfo.removeIf(tpi -> tpi.matches(tableAliasParseInfo.getTableQualifier(), tableAliasParseInfo.getStatBegin()));
   }

   public boolean isEmpty()
   {
      return _tableAliasParseInfos.isEmpty() && _tableParseInfo.isEmpty();
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
      _tableParseInfo.add(tableParseInfo);
   }
}
