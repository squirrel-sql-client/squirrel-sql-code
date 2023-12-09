package net.sourceforge.squirrel_sql.client.session.action.dataimport.importer.excel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.List;

/**
 * MS Excel sheets may contain null rows.
 * This class allows to skip non-null rows transparently.
 */
public class ExcelRowReader
{
   private final Sheet _sheet;

   private List<Integer> _nonNullIndexes = new ArrayList<>();

   public ExcelRowReader(Sheet sheet)
   {
      _sheet = sheet;
   }

   public int getNumberOfNonNullRows()
   {
      return _sheet.getPhysicalNumberOfRows();
   }

   public Row getNonNullRow(int rowIndex)
   {
      if(rowIndex < _nonNullIndexes.size())
      {
         return _sheet.getRow(_nonNullIndexes.get(rowIndex));
      }

      if(_nonNullIndexes.size() >= getNumberOfNonNullRows())
      {
         throw new IndexOutOfBoundsException("There are only " + _nonNullIndexes.size() + " non null rows in the sheet. " +
               "So " + rowIndex + " is out of bounds.");
      }

      int nextNonNullRowSerachStartIx = 0;
      if(false == _nonNullIndexes.isEmpty())
      {
         nextNonNullRowSerachStartIx = _nonNullIndexes.get(_nonNullIndexes.size() - 1) + 1;
      }

      for (int i = nextNonNullRowSerachStartIx; ;i++)
      {
         Row row = _sheet.getRow(i);
         if(null != row)
         {
            _nonNullIndexes.add(i);
            return row;
         }
      }
   }
}
