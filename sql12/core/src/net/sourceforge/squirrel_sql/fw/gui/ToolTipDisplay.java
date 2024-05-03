package net.sourceforge.squirrel_sql.fw.gui;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.*;
import java.awt.*;

public class ToolTipDisplay
{
   private final JComponent _parent;
   private Popup _currentlyDisplayingPopup;
   private Timer _hideTimer;

   public ToolTipDisplay(JComponent parent)
   {
      this(parent, ToolTipManager.sharedInstance().getDismissDelay());
   }
   public ToolTipDisplay(JComponent parent, int displayTimeMillis)
   {
      _parent = parent;
      _hideTimer = new Timer(displayTimeMillis, e -> closeToolTip());
   }

   public void displayToolTip(int xRelativeToParent, int yRelativeToParent, String toolTipText)
   {
      if( null != _currentlyDisplayingPopup )
      {
         closeToolTip();
      }

      if(StringUtilities.isEmpty(toolTipText, true))
      {
         return;
      }

      Point parentScreenLocation = GUIUtils.getScreenLocationFor(_parent);
      JToolTip toolTip = new JToolTip();
      toolTip.setTipText(toolTipText);
      toolTip.setComponent(_parent);


      _currentlyDisplayingPopup = PopupFactory.getSharedInstance().getPopup(_parent, toolTip, parentScreenLocation.x + xRelativeToParent, parentScreenLocation.y + yRelativeToParent);
      _currentlyDisplayingPopup.show();

      _hideTimer.setRepeats(false);
      _hideTimer.start();
   }

   /**
    * @param displayMark If tool tip is displaying and displayMark is the same as last call's then we keep displaying the current tool tip
    */
   public void displayToolTip(int xRelativeToParent, int yRelativeToParent, String toolTipText, Object displayMark)
   {
      if( null != _currentlyDisplayingPopup )
      {
         closeToolTip();
      }

      Point parentScreenLocation = GUIUtils.getScreenLocationFor(_parent);
      JToolTip toolTip = new JToolTip();
      toolTip.setTipText(toolTipText);
      toolTip.setComponent(_parent);


      _currentlyDisplayingPopup = PopupFactory.getSharedInstance().getPopup(_parent, toolTip, parentScreenLocation.x + xRelativeToParent, parentScreenLocation.y + yRelativeToParent);
      _currentlyDisplayingPopup.show();

      _hideTimer.setRepeats(false);
      _hideTimer.start();

   }

   public void closeToolTip()
   {
      if( null == _currentlyDisplayingPopup )
      {
         return;
      }

      _currentlyDisplayingPopup.hide();
      _currentlyDisplayingPopup = null;
      _hideTimer.stop();
   }
}
