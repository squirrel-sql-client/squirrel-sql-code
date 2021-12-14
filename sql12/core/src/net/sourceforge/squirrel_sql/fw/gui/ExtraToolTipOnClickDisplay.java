package net.sourceforge.squirrel_sql.fw.gui;

import java.awt.Point;
import javax.swing.JButton;
import javax.swing.JToolTip;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.Timer;

class ExtraToolTipOnClickDisplay
{
   private Popup _currentlyDisplayingPopup;
   private Timer _hideTimer;

   ExtraToolTipOnClickDisplay(JButton btn, boolean atDefaultToolTipPosition, String toolTipText, int displayTimeMillis)
   {
      btn.addActionListener(e -> onTriggerPop(btn, toolTipText, atDefaultToolTipPosition));
      _hideTimer = new Timer(displayTimeMillis, e -> dispose());
   }

   private void onTriggerPop(JButton btn, String toolTipText, boolean atDefaultToolTipPosition)
   {
      if( null != _currentlyDisplayingPopup )
      {
         dispose();
         return;
      }

      Point screenLocation = GUIUtils.getScreenLocationFor(btn);
      JToolTip toolTip = new JToolTip();
      toolTip.setTipText(toolTipText);
      toolTip.setComponent(btn);


      if( atDefaultToolTipPosition )
      {
         _currentlyDisplayingPopup = PopupFactory.getSharedInstance().getPopup(btn, toolTip, screenLocation.x + btn.getWidth(), screenLocation.y + btn.getHeight());
      }
      else
      {
         _currentlyDisplayingPopup = PopupFactory.getSharedInstance().getPopup(btn, toolTip, screenLocation.x, screenLocation.y - 80);
      }
      _currentlyDisplayingPopup.show();

      _hideTimer.setRepeats(false);
      _hideTimer.start();

   }

   private void dispose()
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
