package net.sourceforge.squirrel_sql.client.session.sqlbounds;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;

import javax.swing.text.JTextComponent;

public class BoundsOfSqlHandler
{
   private JTextComponent _textComponent;
   private ISession _session;

   public BoundsOfSqlHandler(JTextComponent textComponent, ISession session)
   {
      _textComponent = textComponent;
      _session = session;
   }

   public int[] getBoundsOfSQLToBeExecuted()
   {
      int[] bounds = new int[2];
      bounds[0] = _textComponent.getSelectionStart();
      bounds[1] = _textComponent.getSelectionEnd();

      if(bounds[0] == bounds[1])
      {
         bounds = getSqlBoundsBySeparatorRule(_textComponent.getCaretPosition());
      }

      return bounds;
   }

   /**
    * The non selection separator is two new lines. Two new lines with white spaces in between
    * is counted as separator too.
    * @return
    */
   public int[] getSqlBoundsBySeparatorRule(int iCaretPos)
   {
      int[] bounds = new int[2];

      String sql = _textComponent.getText();

      bounds[0] = previousIndexOfStateSep(sql, iCaretPos);
      bounds[1] = nextIndexOfStateSep(sql, iCaretPos);

      return bounds;

   }

   private int nextIndexOfStateSep(String sql, int pos)
   {
      if(Main.getApplication().getSquirrelPreferences().isUseStatementSeparatorAsSqlToExecuteBounds())
      {
         return SQLStatementSeparatorBasedBoundsHandler.nextIndexOfStateSep(_session.getQueryTokenizer(), sql, pos);
      }
      else
      {
         return nextIndexOfNewLineSeparator(sql, pos);
      }
   }

   private int previousIndexOfStateSep(String sql, int pos)
   {
      if(Main.getApplication().getSquirrelPreferences().isUseStatementSeparatorAsSqlToExecuteBounds())
      {
         return SQLStatementSeparatorBasedBoundsHandler.previousIndexOfStateSep(_session.getQueryTokenizer(), sql, pos);
      }
      else
      {
         return previousIndexOfNewLineSeparator(sql, pos);
      }
   }


   private int nextIndexOfNewLineSeparator(String sql, int pos)
   {
      int ix = pos;

      int newLinteCount = 0;
      for(;;)
      {
         if(sql.length() == ix)
         {
            return sql.length();
         }

         if(false == Character.isWhitespace(sql.charAt(ix)))
         {
            newLinteCount = 0;
         }

         if('\n' == sql.charAt(ix))
         {
            ++newLinteCount;
            if(2 == newLinteCount)
            {
               return ix-1;
            }
         }

         ++ix;
      }
   }


   private int previousIndexOfNewLineSeparator(String sql, int pos)
   {
      int ix = pos;

      int newLinteCount = 0;
      for(;;)
      {

         if (ix == sql.length())
         {
            if(ix == 0)
            {
               return ix;
            }
            else
            {
               ix--;
            }
         }



         if(false == Character.isWhitespace(sql.charAt(ix)))
         {
            newLinteCount = 0;
         }


         if('\n' == sql.charAt(ix))
         {
            ++newLinteCount;
            if(2 == newLinteCount)
            {
               return ix+newLinteCount;
            }
         }

         if(0 == ix)
         {
            return 0 + newLinteCount;
         }


         --ix;
      }
   }

   public String getSQLToBeExecuted()
   {
      String sql = _textComponent.getSelectedText();
      if (sql == null || sql.trim().length() == 0)
      {
         sql = _textComponent.getText();
         int[] bounds = getBoundsOfSQLToBeExecuted();

         if(bounds[0] >= bounds[1])
         {
            sql = "";
         }
         else
         {
            sql = sql.substring(bounds[0], bounds[1]).trim();
         }
      }
      return sql != null ? sql : "";
   }


}
