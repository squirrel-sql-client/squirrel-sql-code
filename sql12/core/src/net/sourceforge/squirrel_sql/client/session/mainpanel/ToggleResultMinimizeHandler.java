package net.sourceforge.squirrel_sql.client.session.mainpanel;

import javax.swing.*;

public class ToggleResultMinimizeHandler
{
   private JSplitPane _splitPane;
   private int _lastDividerLocation;

   public ToggleResultMinimizeHandler(JSplitPane splitPane)
   {
      _splitPane = splitPane;
   }

   public void toggleMinimizeResults()
   {
      int sizeAccordingToOrientation;
      if (_splitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT)
      {
         sizeAccordingToOrientation = _splitPane.getBounds().width;
      }
      else
      {
         sizeAccordingToOrientation = _splitPane.getBounds().height;
      }

      if(10 > Math.abs(_splitPane.getDividerLocation() + _splitPane.getDividerSize() - sizeAccordingToOrientation))
      {
         if(_lastDividerLocation < sizeAccordingToOrientation -10 )
         {
            doSetDividerLocation(_lastDividerLocation);
         }
         else
         {
            doSetDividerLocation((int)(sizeAccordingToOrientation * 2 / 3));
         }

      }
      else
      {
         _lastDividerLocation = _splitPane.getDividerLocation();
         doSetDividerLocation(Integer.MAX_VALUE);
      }
   }

   private void doSetDividerLocation(int location)
   {
      _splitPane.setDividerLocation(location);
   }
}
