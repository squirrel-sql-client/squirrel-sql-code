package net.sourceforge.squirrel_sql.plugins.graph.querybuilder.sqlgen;

import net.sourceforge.squirrel_sql.plugins.graph.TableFrameController;

import java.util.ArrayList;

public class FromClauseRes
{
   private StringBuffer _from;
   private ArrayList<TableFrameController> _tables = new ArrayList<TableFrameController>();

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
}
