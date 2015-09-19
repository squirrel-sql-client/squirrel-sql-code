package org.squirrelsql.session.sql.tablesearch;

public class SearchCell
{
   public static final SearchCell INVALID = new SearchCell(-1, -1);

   private final int _row;
   private final int _col;

   public SearchCell(int row, int col)
   {
      _row = row;
      _col = col;
   }

   public SearchCell()
   {
      this(0,0);
   }

   public int getRow()
   {
      return _row;
   }

   public int getCol()
   {
      return _col;
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      SearchCell that = (SearchCell) o;

      if (_row != that._row) return false;
      return _col == that._col;

   }

   @Override
   public int hashCode()
   {
      int result = _row;
      result = 31 * result + _col;
      return result;
   }

   public SearchCell createNextCell(int rowCount, int columnCount)
   {
      if(columnCount - 1 > _col)
      {
         return new SearchCell(_row, _col + 1);
      }
      else if(rowCount - 1 > _row)
      {
         return new SearchCell(_row + 1, 0);
      }

      return new SearchCell(0,0);
   }
}
