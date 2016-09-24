package net.sourceforge.squirrel_sql.fw.datasetviewer.textdataset;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;

import java.util.Arrays;

public class ResultAsText
{
   private final static int COLUMN_PADDING = 2;

   private ColumnDisplayDefinition[] _colDefs;

   private int _rowCount = 0;

   private StringBuffer _text = new StringBuffer();
   private ResultAsTextLineCallback _resultAsTextLineCallback;


   public ResultAsText(ColumnDisplayDefinition[] colDefs, boolean showHeadings, ResultAsTextLineCallback resultAsTextLineCallback)
   {
      _resultAsTextLineCallback = resultAsTextLineCallback;
      if (showHeadings)
      {
         _colDefs = colDefs;
         StringBuffer buf = new StringBuffer();
         for (int i = 0; i < colDefs.length; ++i)
         {
            String headerValue = colDefs[i].getColumnHeading();

            buf.append(format(headerValue, colDefs[i].getDisplayWidth(), ' '));
         }

         addLine(buf.toString());
         buf = new StringBuffer();
         for (int i = 0; i < colDefs.length; ++i)
         {
            buf.append(format("", colDefs[i].getDisplayWidth(), '-'));
         }
         addLine(buf.toString());
      }
   }

   public void addRow(Object[] row)
   {
      _rowCount++;
      StringBuffer buf = new StringBuffer();
      for (int i = 0; i < row.length; ++i)
      {
         String cellValue = CellComponentFactory.renderObject(row[i], _colDefs[i]);
         buf.append(format(cellValue, _colDefs[i].getDisplayWidth(), ' '));
      }
      addLine(buf.toString());
   }

   private void addLine(String line)
   {
      _resultAsTextLineCallback.addLine(line + "\n");
   }

   private String format(String data, int displaySize, char fillChar)
   {
      data = data.replace('\n', ' ');
      data = data.replace('\r', ' ');
      //replace null string character (0x00) with SPACE char, as this character cannot be copied to clipboard
      data = data.replace('\u0000', ' ');
      //replace FF character (0x0C) with SPACE char
      data = data.replace('\u000C', ' ');
      StringBuffer output = new StringBuffer(data);
      if (displaySize > IDataSetViewer.MAX_COLUMN_WIDTH)
      {
         displaySize = IDataSetViewer.MAX_COLUMN_WIDTH;
      }

      if (output.length() > displaySize)
      {
         output.setLength(displaySize);
      }

      displaySize += COLUMN_PADDING;

      int extraPadding = displaySize - output.length();
      if (extraPadding > 0)
      {
         char[] padData = new char[extraPadding];
         Arrays.fill(padData, fillChar);
         output.append(padData);
      }

      return output.toString();
   }

   public void clear()
   {
      _rowCount = 0;
      _text.setLength(0);
   }

   public int getRowCount()
   {
      return _rowCount;
   }

   public String getText()
   {
      return _text.toString();
   }
}
