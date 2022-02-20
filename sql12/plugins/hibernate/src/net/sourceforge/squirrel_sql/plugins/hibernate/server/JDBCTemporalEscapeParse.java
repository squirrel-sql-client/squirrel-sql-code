package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.sql.Timestamp;
import java.util.Date;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JDBCTemporalEscapeParse
{

   private String _hql;
   private TreeMap<String, Date> _datesByParamName = new TreeMap<>();
   private TemporalData[] _temporalDatas = new TemporalData[]{new TimeStampData(), new DateData(), new TimeData()};


   public JDBCTemporalEscapeParse(String hql)
   {
      _hql = hql;

      for (TemporalData temporalData : _temporalDatas)
      {
         Matcher matcher = temporalData.getPattern().matcher(_hql);
         int startIx = 0;
         
         while(matcher.find(startIx))
         {
            if(isInLiteral(matcher.start(), _hql))
            {
               startIx = matcher.start() + 1;
               
               if(startIx >= _hql.length())
               {
                  break;
               }
            }
            else
            {
               Date date = temporalData.valueOf(matcher.group(1));


               String paramName = "p" + _datesByParamName.size();
               _hql = _hql.substring(0, matcher.start()) + ":" + paramName + _hql.substring(matcher.end());

               _datesByParamName.put(paramName, date);

               matcher = temporalData.getPattern().matcher(_hql);
            }
         }
      }
   }

   private static boolean isInLiteral(int pos, String str)
   {

      int countLiteralDelims = 0;
      for (int i = 0; i < pos; i++)
      {
          if('\'' == str.charAt(i))
          {
             ++countLiteralDelims;
          }
      }

      return 1 == countLiteralDelims % 2;
   }

   public String getMessagePanelInfoText()
   {
      String ret = "   " + _hql;

      for (String paramName : _datesByParamName.keySet())
      {
         ret += "\n   " + paramName + "=" +_datesByParamName.get(paramName);
      }
      
      return ret;
   }

   public boolean hasEscapes()
   {
      return 0 < _datesByParamName.size();
   }

   private static interface TemporalData
   {
      Date valueOf(String hq1l);
      Pattern getPattern();
   }
   
   
   private static class TimeStampData implements TemporalData
   {

      @Override
      public Date valueOf(String str)
      {
         try
         {
            return Timestamp.valueOf(str);
         }
         catch (Exception e)
         {
            throw new IllegalArgumentException("Could not interpret " + str + " as " + java.sql.Timestamp.class.getName(), e);
         }
      }

      @Override
      public Pattern getPattern()
      {
         return Pattern.compile("\\{ts\\s*'([0-9[:-][\\s][\\.]]+\\s*)'\\}");
      }
   }

   private static class DateData implements TemporalData
   {

      @Override
      public Date valueOf(String str)
      {
         try
         {
            return java.sql.Date.valueOf(str);
         }
         catch (Exception e)
         {
            throw new IllegalArgumentException("Could not interpret " + str + " as " + java.sql.Date.class.getName(), e);
         }
      }

      @Override
      public Pattern getPattern()
      {
         return Pattern.compile("\\{d\\s*'([0-9[:-][\\s][\\.]]+\\s*)'\\}");
      }
   }

   private static class TimeData implements TemporalData
   {

      @Override
      public Date valueOf(String str)
      {
         try
         {
            return java.sql.Time.valueOf(str);
         }
         catch (Exception e)
         {
            throw new IllegalArgumentException("Could not interpret " + str + " as " + java.sql.Time.class.getName(), e);
         }
      }

      @Override
      public Pattern getPattern()
      {
         return Pattern.compile("\\{t\\s*'([0-9[:-][\\s][\\.]]+\\s*)'\\}");
      }
   }


   public String getHql()
   {
      return _hql;
   }

   public TreeMap<String, Date> getDatesByParamName()
   {
      return _datesByParamName;
   }

   public static void main(String[] args)
   {
      JDBCTemporalEscapeParse jep;

      jep = new JDBCTemporalEscapeParse("dfd {ts '2012-03-08 19:52:21'} sdfdss d  'asdfs  {t '20:29:34'}' select  {d '2012-03-08'}  ");
      System.out.println(jep.getHql());
      for (Date date : jep.getDatesByParamName().values())
      {
         System.out.println("   date = " + date);
      }

      jep = new JDBCTemporalEscapeParse("'{t '20:29:34'}'");
      System.out.println(jep.getHql());
      for (Date date : jep.getDatesByParamName().values())
      {
         System.out.println("   date = " + date);
      }

      jep = new JDBCTemporalEscapeParse("{t '20:29:34'}");
      System.out.println(jep.getHql());
      for (Date date : jep.getDatesByParamName().values())
      {
         System.out.println("   date = " + date);
      }

      jep = new JDBCTemporalEscapeParse("'{t '20:29:34'}'{t '20:29:34'}");
      System.out.println(jep.getHql());
      for (Date date : jep.getDatesByParamName().values())
      {
         System.out.println("   date = " + date);
      }

      jep = new JDBCTemporalEscapeParse("{t '20:29:34'}'{t '20:29:34'}'");
      System.out.println(jep.getHql());
      for (Date date : jep.getDatesByParamName().values())
      {
         System.out.println("   date = " + date);
      }

      jep = new JDBCTemporalEscapeParse("dfd {ts '2012-03-08 19:52:21'} sdfdss d  'asdfs  {t '20:29:34'}'  select  {t '20:29:34'} {d '2012-03-08'}  ");
      System.out.println(jep.getHql());
      for (Date date : jep.getDatesByParamName().values())
      {
         System.out.println("   date = " + date);
      }

      jep = new JDBCTemporalEscapeParse("dfd {ts '2012-03-08 19:52:21'} sdfdss d  'asdfs  '  select  {t '20:29:34'} {d '2012-03-08'}  ");
      System.out.println(jep.getHql());
      for (Date date : jep.getDatesByParamName().values())
      {
         System.out.println("   date = " + date);
      }

      jep = new JDBCTemporalEscapeParse("dfd sdfdss d  'asdfs  {t '20:29:34'} '  select   ");
      System.out.println(">" + jep.getHql() + "<");
      for (Date date : jep.getDatesByParamName().values())
      {
         System.out.println("   date = " + date);
      }



   }


}
