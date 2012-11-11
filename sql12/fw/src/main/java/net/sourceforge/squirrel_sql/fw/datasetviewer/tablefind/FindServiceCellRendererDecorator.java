package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.HashMap;

public class FindServiceCellRendererDecorator implements TableCellRenderer
{
   private TableCellRenderer _delegate;
   private FindServiceRenderCallBack _findServiceRenderCallBack;


   private Point _pointBuffer = new Point();
   private HashMap<Point, Color> _originalColorsByCell = new HashMap<Point, Color>();

   public FindServiceCellRendererDecorator(TableCellRenderer delegate, FindServiceRenderCallBack findServiceRenderCallBack)
   {
      _delegate = delegate;
      _findServiceRenderCallBack = findServiceRenderCallBack;
   }

   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
   {
      FindMarkColor findMarkColor = _findServiceRenderCallBack.getBackgroundColor(row, column);

      Component tableCellRendererComponent = _delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

      if(null != findMarkColor)
      {
         if(false == tableCellRendererComponent.getBackground() instanceof FindMarkColor)
         {
            // We see if we must backup an original color.

            _pointBuffer.setLocation(row, column);
            if(_originalColorsByCell.containsKey(_pointBuffer))
            {
               // We already have backup. So the underlying table has refreshed the color. So we should stop backuping.
               if(null != _originalColorsByCell.get(_pointBuffer))
               {
                  _originalColorsByCell.put(new Point(_pointBuffer), null);
               }
            }
            else
            {
               _originalColorsByCell.put(new Point(_pointBuffer), tableCellRendererComponent.getBackground());
            }
         }

         tableCellRendererComponent.setBackground(findMarkColor);
      }
      else if(tableCellRendererComponent.getBackground() instanceof FindMarkColor)
      {
         _pointBuffer.setLocation(row, column);
         Color originalBackground = _originalColorsByCell.get(_pointBuffer);
         if (null != originalBackground)
         {
            tableCellRendererComponent.setBackground(originalBackground);
         }
      }

      return tableCellRendererComponent;
   }
}
