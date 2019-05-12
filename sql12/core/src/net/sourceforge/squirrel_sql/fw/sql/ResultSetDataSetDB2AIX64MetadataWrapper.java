/*
 * Copyright (C) 2019 Stefan Mueller
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sourceforge.squirrel_sql.fw.sql;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

public class ResultSetDataSetDB2AIX64MetadataWrapper implements IDataSet
{
   public static final String DATABASE_PRODUCT_NAME_DB_2_AIX_64 = "DB2/AIX64";

   private ResultSetDataSet resultSetDataSet;

   public ResultSetDataSetDB2AIX64MetadataWrapper(ResultSetDataSet resultSetDataSet)
   {
      this.resultSetDataSet = resultSetDataSet;
   }

   @Override
   public int getColumnCount() throws DataSetException
   {
      return resultSetDataSet.getColumnCount();
   }

   @Override
   public DataSetDefinition getDataSetDefinition() throws DataSetException
   {
      return resultSetDataSet.getDataSetDefinition();
   }

   @Override
   public boolean next(IMessageHandler msgHandler) throws DataSetException
   {
      return resultSetDataSet.next(msgHandler);
   }

   @Override
   public Object get(int columnIndex) throws DataSetException
   {
      if (columnIndex != 4)
      {
         return resultSetDataSet.get(columnIndex);
      }
      // Workaround for DB2/AIX64 driver: COLUMN_SIZE is not CHAR_OCTET_LENGTH for double-bytes datatypes GRAPHIC/VARGRAPHIC
      Integer columnSize = (Integer) resultSetDataSet.get(4);
      if ((((Integer) resultSetDataSet.get(7)) == 1 && ((String) resultSetDataSet.get(1)).equals("GRAPHIC")
            || ((Integer) resultSetDataSet.get(7)) == 12 && ((String) resultSetDataSet.get(1)).equals("VARGRAPHIC")) &&
            columnSize.equals(resultSetDataSet.get(13)) &&
            columnSize % 2 == 0)
      {
         columnSize = columnSize / 2;
      }
      return columnSize;
   }

}
