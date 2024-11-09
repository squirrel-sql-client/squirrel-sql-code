package net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabheader;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformatorConfigFactory;
import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NormalizedSqlCompareCache
{
   private CodeReformator _codeReformator;

   private int _resultTabCounter = 0;
   private int _editorSqlCounter = 0;

   private HashMap<IntegerIdentifier, NormalizedSqlCompareCacheEntry> _resultTabIdToEntry = new HashMap<>();
   private HashMap<String, NormalizedSqlCompareCacheEntry> _editorSqlToEntry = new HashMap<>();

   public String getResultTabSqlNormalized(ISession session, IResultTab sqlResultTab)
   {
      NormalizedSqlCompareCacheEntry resultTabEntry = _resultTabIdToEntry.get(sqlResultTab.getIdentifier());

      if(null == resultTabEntry)
      {
         resultTabEntry = new NormalizedSqlCompareCacheEntry(getReformator(session).getNormalizedSql(sqlResultTab.getSqlString()), ++_resultTabCounter);
         _resultTabIdToEntry.put(sqlResultTab.getIdentifier(), resultTabEntry);
      }

      return resultTabEntry.getNormalizedSql();
   }

   public String getEditorSqlNormalized(ISession session, String editorSql)
   {
      NormalizedSqlCompareCacheEntry editorEntry = _editorSqlToEntry.get(editorSql);

      if(null == editorEntry)
      {
         editorEntry = new NormalizedSqlCompareCacheEntry(getReformator(session).getNormalizedSql(editorSql), ++_editorSqlCounter);
         _editorSqlToEntry.put(editorSql, editorEntry);
      }
      return editorEntry.getNormalizedSql();
   }


   private CodeReformator getReformator(ISession session)
   {
      if( null == _codeReformator)
      {
         _codeReformator = new CodeReformator(CodeReformatorConfigFactory.createConfig(session));
      }

      doHouseKeeping(_editorSqlToEntry, _editorSqlCounter);
      doHouseKeeping(_resultTabIdToEntry, _resultTabCounter);

      return _codeReformator;
   }

   private void doHouseKeeping(HashMap<?, NormalizedSqlCompareCacheEntry> cache, int currentCounter)
   {
      if(60 > cache.size())
      {
         return;
      }

      int minRemainCacheCounter = currentCounter - 40;

      ArrayList<Object> resTabKeyToRemove = new ArrayList<>();

      for(Map.Entry<?, NormalizedSqlCompareCacheEntry> entry : cache.entrySet())
      {
         if(minRemainCacheCounter >= entry.getValue().getCountNo())
         {
            resTabKeyToRemove.add(entry.getKey());
         }
      }
      resTabKeyToRemove.forEach(key -> cache.remove(key));
   }
}
