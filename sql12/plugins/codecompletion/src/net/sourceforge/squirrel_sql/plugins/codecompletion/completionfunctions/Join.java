package net.sourceforge.squirrel_sql.plugins.codecompletion.completionfunctions;

import java.util.Hashtable;
import java.util.Vector;

import net.sourceforge.squirrel_sql.client.session.ExtendedColumnInfo;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.codecompletion.CodeCompletionInfo;


public class Join extends AbstractJoin
{
   private ISession _session;
   private boolean _returnedLeftJoinBefore;

   public Join(ISession session)
   {
      super(session);
      _session = session;
   }

   public String getCompareString()
   {
      return "#j";
   }

   public String getCompletionString()
   {
      return "#j,<table1>,<table2>,...<tableN>,";
   }

   public String toString()
   {
      return getCompletionString() + " inner/left join";
   }

   public CodeCompletionInfo[] getFunctionResults(String functionSting)
   {
      _returnedLeftJoinBefore = false;
      return super.getFunctionResults(functionSting);
   }

   @Override
   protected String getJoinClause(String fkName,    
                                  String table1, 
                                  String table2, 
                                  Hashtable<String, Vector<ColBuffer>> colBuffersByFkName)
   {
      if(_returnedLeftJoinBefore)
      {
         return "LEFT JOIN "; 
      }


      ExtendedColumnInfo[] extCols1 = _session.getSchemaInfo().getExtendedColumnInfos(table1);
      ExtendedColumnInfo[] extCols2 = _session.getSchemaInfo().getExtendedColumnInfos(table2);

      if(null == fkName)
      {
         return "INNER JOIN ";
      }

      Vector<ColBuffer> colBufs = colBuffersByFkName.get(fkName);

      for (int i = 0; i < colBufs.size(); i++)
      {
         ColBuffer colBuf = colBufs.get(i);

         if(colBuf.tableName.equalsIgnoreCase(table1))
         {
            if(isNullable(colBuf.colName , extCols1))
            {
               _returnedLeftJoinBefore = true;
               return "LEFT JOIN ";
            }
         }

         if(colBuf.tableName.equalsIgnoreCase(table2))
         {
            if(isNullable(colBuf.colName , extCols2))
            {
               _returnedLeftJoinBefore = true;
               return "LEFT JOIN ";
            }
         }
      }
      return "INNER JOIN ";

   }

   private boolean isNullable(String colName, ExtendedColumnInfo[] extCols)
   {
      for (int i = 0; i < extCols.length; i++)
      {
         if(extCols[i].getColumnName().equalsIgnoreCase(colName))
         {
            return extCols[i].isNullable();
         }
      }

      throw new IllegalArgumentException("Column " + colName + " not found");
   }



}
