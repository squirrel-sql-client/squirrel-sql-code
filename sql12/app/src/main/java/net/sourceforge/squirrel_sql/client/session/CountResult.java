package net.sourceforge.squirrel_sql.client.session;

import java.util.ArrayList;

public class CountResult
{
   private int _count;
   private String _sql;
   private ArrayList<String> _whereClauseParts = new ArrayList<String>();
   private ArrayList _paramValues = new ArrayList();

   public void setCount(int count)
   {
      _count = count;
   }

   public int getCount()
   {
      return _count;
   }

   public void setSql(String sql)
   {
      _sql = sql;
   }

   public void addWhereClausePart(String whereClausePart, Object paramValue)
   {
      _whereClauseParts.add(whereClausePart);
      _paramValues.add(paramValue);
   }

   private String getParamValClassName(Object paramValue)
   {
      if(null == paramValue)
      {
         return "<unknown>";
      }

      return paramValue.getClass().getName();
   }

   private String getValAsString(Object paramValue)
   {
      if(null == paramValue)
      {
         return "<null>";
      }

      String ret = "" + paramValue;
      return ret;
   }

   public String getCheckSQLDetails()
   {
      String ret = _sql + "\n ";

      for (int i = 0; i < _whereClauseParts.size(); i++)
      {
         String whereClausePart = _whereClauseParts.get(i);

         ret += "\n" + whereClausePart + " with parameter value: \"" + getValAsString(_paramValues.get(i)) + "\" of type: " + getParamValClassName(_paramValues.get(i));

      }

      return ret;
   }
}
