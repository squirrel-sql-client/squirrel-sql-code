package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasSchemaDetailProperties;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class SchemaTableModel extends DefaultTableModel
{
  static final int IX_SCHEMA_NAME = 0;
  static final int IX_TABLE = 1;
  static final int IX_VIEW = 2;
  static final int IX_FUNCTION = 3;
   private SQLAliasSchemaDetailProperties[] _schemaDetails;


   public SchemaTableModel(SQLAliasSchemaDetailProperties[] schemaDetails)
   {
      _schemaDetails = schemaDetails;
   }

   

   public Object getValueAt(int row, int column)
   {
      SQLAliasSchemaDetailProperties buf = (SQLAliasSchemaDetailProperties) _schemaDetails[row];
      switch(column)
      {
         case IX_SCHEMA_NAME:
            return buf.getSchemaName();
         case IX_TABLE:
            return SchemaTableCboItem.getItemForID(buf.getTable());
         case IX_VIEW:
            return SchemaTableCboItem.getItemForID(buf.getView());
         case IX_FUNCTION:
            return SchemaTableCboItem.getItemForID(buf.getFunction());
         default:
            throw new IllegalArgumentException("Unkown column index " + column);

      }
   }

   public void setValueAt(Object aValue, int row, int column)
   {
      SQLAliasSchemaDetailProperties buf = (SQLAliasSchemaDetailProperties) _schemaDetails[row];

      switch(column)
      {
         case IX_TABLE:
            buf.setTable(((SchemaTableCboItem)aValue).getID());
            break;
         case IX_VIEW:
            buf.setView(((SchemaTableCboItem)aValue).getID());
            break;
         case IX_FUNCTION:
            buf.setFunction(((SchemaTableCboItem)aValue).getID());
            break;
         default:
            throw new IllegalArgumentException("Unkown column index " + column);

      }

      fireTableCellUpdated(row, column);
   }


   public boolean isCellEditable(int row, int column)
   {
      return IX_SCHEMA_NAME != column;
   }


   public int getRowCount()
   {
      if(null == _schemaDetails)
      {
         return 0;
      }
      else
      {
         return _schemaDetails.length;
      }
   }



   public SQLAliasSchemaDetailProperties[] getData()
   {
      return _schemaDetails;
   }

   public void updateSchemas(SQLAliasSchemaDetailProperties[] details)
   {
      // TODO keep old data
      _schemaDetails = details;
      fireTableDataChanged();
   }
}
