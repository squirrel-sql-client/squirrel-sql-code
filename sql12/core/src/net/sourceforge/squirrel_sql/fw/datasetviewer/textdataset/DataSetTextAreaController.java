package net.sourceforge.squirrel_sql.fw.datasetviewer.textdataset;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.gui.TextPopupMenu;

import java.awt.*;
import java.util.Arrays;

public class DataSetTextAreaController
{
   private final static int COLUMN_PADDING = 2;

	private DataSetTextArea _outText = null;
	private int _rowCount = 0;
   private ColumnDisplayDefinition[] _colDefs;

   public DataSetTextAreaController()
	{
      _outText = new DataSetTextArea();
   }

   public void init(ColumnDisplayDefinition[] colDefs, boolean showHeadings)
   {
      _colDefs = colDefs;
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

   public void clear()
	{
		_outText.setText("");
		_rowCount = 0;
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

	public void moveToTop()
	{
		_outText.select(0, 0);
	}

   /**
	 * Get the component for this viewer.
	 *
	 * @return	The component for this viewer.
	 */
	public Component getComponent()
	{
		return _outText;
	}

	/*
	 * @see IDataSetViewer#getRowCount()
	 */
	public int getRowCount()
	{
		return _rowCount;
	}

	private void addLine(String line)
	{
		_outText.append(line);
		_outText.append("\n");
	}

	private String format(String data, int displaySize, char fillChar)
	{
		data = data.replace('\n', ' ');
		data = data.replace('\r', ' ');
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

//   public TextPopupMenu getPopupMenu()
//   {
//      return _outText.getPopupMenu();
//   }
}
