package net.sourceforge.squirrel_sql.client.session;

import javax.swing.text.JTextComponent;

public class BoundsOfSqlHandler
{
   private JTextComponent _textComponent;

   public BoundsOfSqlHandler(JTextComponent textComponent)
   {
      _textComponent = textComponent;
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

      bounds[0] = lastIndexOfStateSep(sql, iCaretPos);
      bounds[1] = indexOfStateSep(sql, iCaretPos);

      return bounds;

   }

   private int indexOfStateSep(String sql, int pos)
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

   private int lastIndexOfStateSep(String sql, int pos)
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

}
