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

      AbstractAction closeAction = new AbstractAction()
      {
         public void actionPerformed(ActionEvent actionEvent)
         {
            no();
         }
      };
      KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
      _dlg.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "CloseAction");
      _dlg.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "CloseAction");
      _dlg.getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "CloseAction");
      _dlg.getRootPane().getActionMap().put("CloseAction", closeAction);



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