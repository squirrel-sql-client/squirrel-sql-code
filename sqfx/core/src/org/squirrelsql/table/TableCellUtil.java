package org.squirrelsql.table;

import org.squirrelsql.AppState;

public class TableCellUtil
{
   public static CellProperties getCellProperties(Object cellItem)
   {
      String value = cellItem.toString();
      CellProperties ret = new CellProperties(value, null, false);

      if(AppState.get().getSettingsManager().getSettings().isMultibleLinesInCells())
      {
         return ret;
      }

      int nlPos = value.indexOf("\n");
      int crnlPos = value.indexOf("\r\n");

      if(-1 < nlPos || -1 < crnlPos)
      {
         int cutPos = nlPos;

         if(-1 == nlPos || (-1 < crnlPos && crnlPos < nlPos))
         {
            cutPos = crnlPos;
         }

         value = value.substring(0, Math.max(cutPos, 0));

         ret = new CellProperties(value, "-fx-background-color: cyan;", true);
      }

      return ret;
   }


   public static void main(String[] args)
   {
      System.out.println(getCellProperties("Hallo\n").getValue());
      System.out.println(getCellProperties("Hallo\r\n").getValue());
      System.out.println(getCellProperties("Hallo\r\ndd\ndd").getValue());
      System.out.println(getCellProperties("Hallo\nddd\r\ndd").getValue());

      System.out.println(">" + getCellProperties("\nHallo\n").getValue() + "<");
      System.out.println(">" + getCellProperties("\r\nHallo\r\n").getValue() + "<");
      System.out.println(getCellProperties("Hallo\r\ndd\ndd\r\n").getValue());
      System.out.println(getCellProperties("Hallo\nddd\r\ndd\n").getValue());
   }
}
