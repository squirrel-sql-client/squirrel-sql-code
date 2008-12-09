package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasSchemaDetailProperties;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Arrays;

public class SchemaTableModel extends DefaultTableModel
{
   private static final long serialVersionUID = 1L;
    
   static final int IX_SCHEMA_NAME = 0;
   static final int IX_TABLE = 1;
	static final int IX_VIEW = 2;
   static final int IX_PROCEDURE = 3;
   private SQLAliasSchemaDetailProperties[] _schemaDetails;



   public SchemaTableModel(SQLAliasSchemaDetailProperties[] schemaDetails)
   {
      _schemaDetails = schemaDetails;
   }



   public Object getValueAt(int row, int column)
   {
      SQLAliasSchemaDetailProperties buf = _schemaDetails[row];
      switch(column)
      {
         case IX_SCHEMA_NAME:
            return buf.getSchemaName();
         case IX_TABLE:
            return SchemaTableCboItem.getItemForID(buf.getTable());
         case IX_VIEW:
            return SchemaTableCboItem.getItemForID(buf.getView());
         case IX_PROCEDURE:
            return SchemaTableCboItem.getItemForID(buf.getProcedure());
         default:
            throw new IllegalArgumentException("Unkown column index " + column);

      }
   }

   public void setValueAt(Object aValue, int row, int column)
   {
      SQLAliasSchemaDetailProperties buf = _schemaDetails[row];

      switch(column)
      {
         case IX_TABLE:
            buf.setTable(((SchemaTableCboItem)aValue).getID());
            break;
         case IX_VIEW:
            buf.setView(((SchemaTableCboItem)aValue).getID());
            break;
         case IX_PROCEDURE:
            buf.setProcedure(((SchemaTableCboItem)aValue).getID());
            break;
         default:
            throw new IllegalArgumentException("Unkown column index " + column);

      }

      fireTableCellUpdated(row, column);
   }

   /**
	 * @see javax.swing.table.DefaultTableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount()
	{
		return 4;
	}


   public boolean isCellEditable(int row, int column)
   {
      return IX_SCHEMA_NAME != column && column < getColumnCount();
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

   public void updateSchemas(String[] schemaNames)
   {
      ArrayList<SQLAliasSchemaDetailProperties> newDetails = 
          new ArrayList<SQLAliasSchemaDetailProperties>();

      for (int i = 0; i < schemaNames.length; i++)
      {

         boolean found = false;
         for (int j = 0; j < _schemaDetails.length; j++)
         {
            if(_schemaDetails[j].getSchemaName().equalsIgnoreCase(schemaNames[i]))
            {
               newDetails.add(_schemaDetails[j]);
               found = true;
               break;
            }
         }

         if(false == found)
         {
            SQLAliasSchemaDetailProperties buf = new SQLAliasSchemaDetailProperties();
            buf.setSchemaName(schemaNames[i]);
            newDetails.add(buf);
         }

      }

      _schemaDetails = newDetails.toArray(new SQLAliasSchemaDetailProperties[newDetails.size()]);
      Arrays.sort(_schemaDetails);

      fireTableDataChanged();
   }

   public void setColumnTo(int modelIndex, SchemaTableCboItem toItem)
   {
      for (int i = 0; i < _schemaDetails.length; i++)
      {
         if(IX_TABLE == modelIndex)
         {
            _schemaDetails[i].setTable(toItem.getID());
         }
         else if(IX_VIEW == modelIndex)
         {
            _schemaDetails[i].setView(toItem.getID());
         }
         else if(IX_PROCEDURE == modelIndex)
         {
            _schemaDetails[i].setProcedure(toItem.getID());
         }
      }

      fireTableDataChanged();
   }
}
