package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import javax.swing.JTable;
import javax.swing.SwingUtilities;

public class BlobClobLoadCheck
{
   public static void check(IDataTypeComponent dataTypeObject, JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIx, int columnIx)
   {
      if(dataTypeObject instanceof DataTypeBlob)
      {
         if( false == ((DataTypeBlob)dataTypeObject).wasWholeBlobRead(value))
         {
            DataTypeBlobProperties blobProperties = ((DataTypeBlob)dataTypeObject).getProperties();

            if(blobProperties.isReadBlobsOnCellVisible())
            {
               ((DataTypeBlob)dataTypeObject).tryReadWholeBlob(value);
               SwingUtilities.invokeLater(() -> table.repaint());
            }
            else if(hasFocus && blobProperties.isReadBlobsOnCellFocused())
            {
               ((DataTypeBlob)dataTypeObject).tryReadWholeBlob(value);
               SwingUtilities.invokeLater(() -> table.repaint());
            }
         }
      }
      else if(dataTypeObject instanceof DataTypeClob)
      {
         if(false == ((DataTypeClob)dataTypeObject).wasWholeClobRead(value))
         {
            DataTypeClobProperties blobProperties = ((DataTypeClob)dataTypeObject).getProperties();

            if(blobProperties.isReadClobsOnCellVisible())
            {
               ((DataTypeClob)dataTypeObject).tryReadWholeClob(value);
               SwingUtilities.invokeLater(() -> table.repaint());
            }
            else if(hasFocus && blobProperties.isReadClobsOnCellFocused())
            {
               ((DataTypeClob)dataTypeObject).tryReadWholeClob(value);
               SwingUtilities.invokeLater(() -> table.repaint());
            }
         }
      }
   }
}
