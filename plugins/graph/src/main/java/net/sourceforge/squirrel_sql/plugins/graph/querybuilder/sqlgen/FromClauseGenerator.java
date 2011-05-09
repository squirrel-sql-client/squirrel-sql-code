package net.sourceforge.squirrel_sql.plugins.graph.querybuilder.sqlgen;

import net.sourceforge.squirrel_sql.plugins.graph.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class FromClauseGenerator
{
   FromClauseRes createFrom(TableFramesModel tableFramesModel)
   {
      TableFrameController tfcStart = getStartTable(tableFramesModel);

      if(null == tfcStart)
      {
         return null;
      }



      HashSet<TableFrameController> relatives = getRelatives(tableFramesModel, tfcStart);


      FromClauseRes fromClauseRes = new FromClauseRes(tfcStart);

      for (TableFrameController tfcTo : relatives)
      {
         appendJoins(fromClauseRes, tfcStart, tfcTo, tableFramesModel);
      }

      return fromClauseRes;
   }

   private TableFrameController getStartTable(TableFramesModel tableFramesModel)
   {
      if(0 == tableFramesModel.getTblCtrls().size())
      {
         return null;
      }

      TableFrameController ret = tableFramesModel.getTblCtrls().get(0);

      for (TableFrameController tfc : tableFramesModel.getTblCtrls())
      {
         if(getDistTo0Square(ret) > getDistTo0Square(tfc))
         {
            ret = tfc;
         }
      }

      return ret;
   }


   private void appendJoins(FromClauseRes fromClauseRes, TableFrameController tfcFrom, TableFrameController tfcTo, TableFramesModel tableFramesModel)
   {
      ConstraintView[] bauf;

      String tableNameFrom = tfcFrom.getTableInfo().getSimpleName();
      String tableNameTo = tfcTo.getTableInfo().getSimpleName();

      ArrayList<ConstraintView> constraints = new ArrayList<ConstraintView>();


      bauf = findConstraintViews(tfcFrom, tfcTo.getTableInfo().getSimpleName());
      constraints.addAll(Arrays.asList(bauf));

      bauf = findConstraintViews(tfcTo, tfcFrom.getTableInfo().getSimpleName());
      constraints.addAll(Arrays.asList(bauf));

      writeConstraints(fromClauseRes, constraints, tableNameFrom, tableNameTo);

      fromClauseRes.addTable(tfcFrom);
      fromClauseRes.addTable(tfcTo);
      HashSet<TableFrameController> relatives = getRelatives(tableFramesModel, tfcTo);

      for (TableFrameController relative : relatives)
      {
         if(fromClauseRes.contains(relative))
         {
            continue;
         }
         appendJoins(fromClauseRes, tfcTo, relative, tableFramesModel);
      }

   }

   private void writeConstraints(FromClauseRes fromClauseRes, ArrayList<ConstraintView> constrains, String tableNameFrom, String tableNameTo)
   {

      int aliasCount = 0;

      for (ConstraintView constraint : constrains)
      {
         ConstraintData data = constraint.getData();
         if(data.getConstraintQueryData().isNoJoin())
         {
            continue;
         }

         String tableNameTo_aliased = tableNameTo;

         if( 0 < aliasCount++)
         {
            tableNameTo_aliased = tableNameTo + "_" + aliasCount;
            fromClauseRes.append(" " + getJoinType(data, tableNameFrom, tableNameTo) + " JOIN " + tableNameTo + " " + tableNameTo_aliased + " ON ");
         }
         else
         {
            fromClauseRes.append(" " + getJoinType(data, tableNameFrom, tableNameTo) + " JOIN " + tableNameTo + " ON ");
         }



         for (int j = 0; j < data.getFkColumnInfos().length; j++)
         {
            ColumnInfo fkCol = data.getFkColumnInfos()[j];
            ColumnInfo pkCol = data.getPkColumnInfos()[j];

            if(0 < j)
            {
               fromClauseRes.append(" AND ");
            }

            String fromCol;
            String toCol;
            if(tableNameFrom.equalsIgnoreCase(data.getPkTableName()))
            {
               fromCol = pkCol.getColumnName();
               toCol = fkCol.getColumnName();
            }
            else
            {
               toCol = pkCol.getColumnName();
               fromCol = fkCol.getColumnName();
            }

            fromClauseRes.append(tableNameFrom + "." + fromCol);
            fromClauseRes.append(" = ");
            fromClauseRes.append(tableNameTo_aliased + "." + toCol);

         }
      }
   }

   private String getJoinType(ConstraintData data, String tableNameFrom, String tableNameTo)
   {
      if(data.getConstraintQueryData().isOuterJoinFor(tableNameTo))
      {
         return "RIGHT";
      }
      else if(data.getConstraintQueryData().isOuterJoinFor(tableNameFrom))
      {
         return "LEFT";
      }

      return "INNER";
   }

   private HashSet<TableFrameController> getRelatives(TableFramesModel tableFramesModel, TableFrameController tfc)
   {
      HashSet<TableFrameController> ret = new HashSet<TableFrameController>();

      for (TableFrameController buf : tableFramesModel.getTblCtrls())
      {
         if(   0 < findConstraintViews(tfc, buf.getTableInfo().getSimpleName()).length
            || 0 < findConstraintViews(buf, tfc.getTableInfo().getSimpleName()).length )
         {
            ret.add(buf);
         }
      }

      return ret;

   }

   private ConstraintView[] findConstraintViews(TableFrameController tfc, String simpleTableName)
   {
      ConstraintView[] constraintViews = tfc.findConstraintViews(simpleTableName);
      ArrayList<ConstraintView> ret = new ArrayList<ConstraintView>();

      for (ConstraintView constraintView : constraintViews)
      {
         if(false == constraintView.getData().getConstraintQueryData().isNoJoin())
         {
            ret.add(constraintView);
         }
      }


      return ret.toArray(new ConstraintView[ret.size()]);
   }


   private double getDistTo0Square(TableFrameController tfc)
   {
      double x = tfc.getFrame().getLocation().getX();
      double y = tfc.getFrame().getLocation().getY();
      return x*x + y*y;
   }

}
