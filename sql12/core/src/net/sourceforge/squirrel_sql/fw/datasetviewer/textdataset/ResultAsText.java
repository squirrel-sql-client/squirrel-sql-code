package net.sourceforge.squirrel_sql.fw.datasetviewer.textdataset;

import java.sql.Types;

import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.CellStyle.AbbreviationStyle;
import org.nocrala.tools.texttablefmt.CellStyle.HorizontalAlign;
import org.nocrala.tools.texttablefmt.CellStyle.NullStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;

//drop table t;
//
//create table t (
//  a int,
//  b decimal(10, 2),
//  c varchar(10),
//  d char(6),
//  employment_date date,
//  f timestamp,
//  g time
//);
//
//insert into t (a, b, c, d, employment_date, f, g) values (123, 456.10, 'Chicago', 'Four', curdate(), current_timestamp(), curtime());
//
//insert into t (a, b, c, d, employment_date, f, g) values (-108, 1234567.13, 'Detroit', 'Eleven', curdate(), current_timestamp(), curtime());
//
//select * from t;

public class ResultAsText
{
   private static final int MAX_CELL_WIDTH = 1000000;

   private ColumnDisplayDefinition[] _colDefs;
   private int _rowCount = 0;
   private ResultAsTextLineCallback _resultAsTextLineCallback;

   private Table _table;
   private CellStyle[] _cellStyles;
   private String _text;

   public ResultAsText(final ColumnDisplayDefinition[] colDefs, final boolean showHeadings, final ResultAsTextLineCallback resultAsTextLineCallback)
   {

      _colDefs = colDefs;
      _rowCount = 0;
      _resultAsTextLineCallback = resultAsTextLineCallback;

      _cellStyles = new CellStyle[_colDefs.length];
      for (int i = 0; i < _colDefs.length; ++i)
      {
         _cellStyles[i] = getCellStyle(colDefs[i]);
      }

      if (showHeadings)
      {
         _table = new Table(_colDefs.length, BorderStyle.DESIGN_FORMAL_WIDE, ShownBorders.HEADER_AND_COLUMNS);
         for (int i = 0; i < _colDefs.length; ++i)
         {
            _table.addCell(colDefs[i].getColumnHeading(), _cellStyles[i]);
         }
      }
      else
      {
         _table = new Table(_colDefs.length, BorderStyle.DESIGN_FORMAL_WIDE, ShownBorders.NONE);
      }
      for (int i = 0; i < _colDefs.length; ++i)
      {
         _table.setColumnWidth(i, 1, MAX_CELL_WIDTH);
      }
   }

   public void addRow(Object[] row)
   {
      this._rowCount++;
      for (int i = 0; i < _colDefs.length; ++i)
      {
         String cellValue = CellComponentFactory.renderObject(row[i], this._colDefs[i]);
         _table.addCell(cellValue, _cellStyles[i]);
      }
   }

   public void clear()
   {
      this._rowCount = 0;
      this._text = null;
   }

   public int getRowCount()
   {
      return this._rowCount;
   }

   public String getText()
   {
      close();
      return "n/a";
   }

   public void close()
   {
      if (this._text == null)
      {
         this._text = _table.render();
      }
      this._resultAsTextLineCallback.addLine(this._text);
   }

   private CellStyle LEFT_ALIGN = new CellStyle(HorizontalAlign.LEFT, AbbreviationStyle.CROP, NullStyle.NULL_TEXT, false);
   private CellStyle CENTER_ALIGN = new CellStyle(HorizontalAlign.CENTER, AbbreviationStyle.CROP, NullStyle.NULL_TEXT, false);
   private CellStyle RIGHT_ALIGN = new CellStyle(HorizontalAlign.RIGHT, AbbreviationStyle.CROP, NullStyle.NULL_TEXT, false);

   private CellStyle getCellStyle(final ColumnDisplayDefinition def)
   {

     // Numbers
     
     if (def.getSqlType() == Types.BIGINT || //
         def.getSqlType() == Types.BIT || //
         def.getSqlType() == Types.DECIMAL || //
         def.getSqlType() == Types.DOUBLE || //
         def.getSqlType() == Types.FLOAT || //
         def.getSqlType() == Types.INTEGER || //
         def.getSqlType() == Types.NUMERIC || //
         def.getSqlType() == Types.REAL || //
         def.getSqlType() == Types.SMALLINT || //
         def.getSqlType() == Types.TINYINT)
      {
         return RIGHT_ALIGN;
      }
     
      // Dates, times, timestamps

     if (def.getSqlType() == Types.DATE|| //
         def.getSqlType() == Types.TIME|| //
         def.getSqlType() == Types.TIMESTAMP|| //
         def.getSqlType() == Types.TIME_WITH_TIMEZONE|| //
         def.getSqlType() == Types.TIMESTAMP_WITH_TIMEZONE)
      {
         return CENTER_ALIGN;
      }

      // Char, varchar, and the rest
     
      return LEFT_ALIGN;
   }
}
