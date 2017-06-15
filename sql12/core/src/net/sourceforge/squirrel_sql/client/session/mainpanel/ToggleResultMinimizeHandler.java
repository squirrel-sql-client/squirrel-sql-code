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
      if(10 > Math.abs(_splitPane.getDividerLocation() + _splitPane.getDividerSize() - _splitPane.getBounds().height))
      {
         if(_lastDividerLocation < _splitPane.getBounds().height -10 )
         {
            doSetDividerLocation(_lastDividerLocation);
         }
         else
         {
            doSetDividerLocation((int)(_splitPane.getBounds().height * 2 / 3));
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
