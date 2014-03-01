package net.sourceforge.squirrel_sql.plugins.graph.querybuilder.sqlgen;

import net.sourceforge.squirrel_sql.plugins.graph.TableFrameController;

import java.util.ArrayList;
import java.util.HashMap;

public class FromClauseRes
{
   private StringBuffer _from;
   private ArrayList<TableFrameController> _tables = new ArrayList<TableFrameController>();

   private HashMap<TableFrameController, Integer> _aliasNrByTfc = new HashMap<TableFrameController, Integer>();

   public FromClauseRes(TableFrameController tfcStart)
   {
      _from = new StringBuffer(" FROM " + tfcStart.getTableInfo().getSimpleName());
      _tables.add(tfcStart);
   }

   public String getFromClause()
   {
      return _from.toString();
   }

   public void addTable(TableFrameController tfc)
   {
      if(false == _tables.contains(tfc))
      {
         _tables.add(tfc);
      }
   }

   public boolean contains(TableFrameController tfc)
   {
      return _tables.contains(tfc);
   }

   public void append(String s)
   {
      _from.append(s);
   }


   public ArrayList<TableFrameController> getTables()
   {
      return _tables;
   }

   public String getAliasForNextJoinIfNeeded(TableFrameController tfc)
   {
      if(false == _tables.contains(tfc))
      {
         return null;
      }

      Integer nr = _aliasNrByTfc.get(tfc);
      if(null == nr)
      {
         nr = 1;
      }
      else
      {
         ++nr;
      }
      _aliasNrByTfc.put(tfc, nr);

      return generateAliasName(tfc, nr);
   }

   private String generateAliasName(TableFrameController tfc, Integer nr)
   {
      return tfc.getTableInfo().getSimpleName() + "_" + nr;
   }

   public String getCurrentAlias(TableFrameController tfc)
   {
      Integer nr = _aliasNrByTfc.get(tfc);
      if(null == nr)
      {
         return null;
      }

      return generateAliasName(tfc, nr);

   }
}
