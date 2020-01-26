package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import net.sourceforge.squirrel_sql.client.mainframe.action.AliasPropertiesCommand;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class TabbedStyleHintController
{
   private TabbedStyleHintDlg _dlg;
   private boolean _useTabbedLayout = false;

   public TabbedStyleHintController()
   {
      _dlg = new TabbedStyleHintDlg();

      _dlg.btnNo.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            no();
         }
      });

      _dlg.btnYes.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            yes();
         }
      });

      GUIUtils.enableCloseByEscape(_dlg);


      _dlg.setSize(350, 250);
      GUIUtils.centerWithinParent(_dlg);
      _dlg.setVisible(true);
   }

   private void yes()
   {
      _useTabbedLayout = true;
      _dlg.dispose();
   }

   private void no()
   {
      _dlg.dispose();
   }

   public boolean isUseTabbedLayout()
   {
      return _useTabbedLayout;
   }

   public boolean isDontShowAgain()
   {
      return _dlg.chkDontShowAgain.isSelected();
   }
}