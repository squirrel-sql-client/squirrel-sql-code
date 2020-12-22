package net.sourceforge.squirrel_sql.fw.gui;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.SmallTabButton;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;

import javax.swing.ImageIcon;
import javax.swing.JToolTip;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.Timer;
import java.awt.Dimension;
import java.awt.Point;

public class SmallToolTipInfoButton
{

   private final SmallTabButton _btnShowToolTip;
   private String _infoText;
   private Popup _popup;
   private Timer _timer;

   private int _displayTimeMillis;

   public SmallToolTipInfoButton(String infoText)
   {
      this(infoText,5000);
   }

   public SmallToolTipInfoButton(String infoText, int displayTimeMillis)
   {
      _infoText = infoText;
      _displayTimeMillis = displayTimeMillis;

      _btnShowToolTip = new SmallTabButton(null, getSmallInfoIcon());
      Dimension size = new Dimension(16, 16);
      _btnShowToolTip.setPreferredSize(size);
      _btnShowToolTip.setMinimumSize(size);
      _btnShowToolTip.setMaximumSize(size);
      _btnShowToolTip.addActionListener(e -> showTooltipLikePopup());
   }


   public SmallTabButton getButton()
   {
      return _btnShowToolTip;
   }

   private void showTooltipLikePopup()
   {
      if(null != _popup)
      {
         _popup.hide();
         _popup = null;
         _timer.stop();
         return;
      }


      Point parentScreenLocation = GUIUtils.getScreenLocationFor(_btnShowToolTip);

      JToolTip toolTip = new JToolTip();
      toolTip.setTipText(_infoText);

      toolTip.setComponent(_btnShowToolTip);
      _popup = PopupFactory.getSharedInstance().getPopup(_btnShowToolTip, toolTip, parentScreenLocation.x + _btnShowToolTip.getWidth(), parentScreenLocation.y+ _btnShowToolTip.getHeight());
      _popup.show();

      // create a timer to hide the popup later
      _timer = new Timer(_displayTimeMillis, e -> onTimer());
      _timer.setRepeats(false);
      _timer.start();
   }

   private void onTimer()
   {
      if(null != _popup)
      {
         _popup.hide();
         _popup = null;
      }
   }


   private ImageIcon getSmallInfoIcon()
   {
      return Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SMALL_INFO);
   }

   public void setInfoText(String infoText)
   {
      _infoText = infoText;
   }
}
