package net.sourceforge.squirrel_sql.client.session.schemainfo;

import net.sourceforge.squirrel_sql.client.mainframe.action.AliasPropertiesCommand;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class SessionStartupTimeHintController
{
   private ISession _session;
   private SessionStartupTimeHintDlg _dlg;

   public SessionStartupTimeHintController(ISession session)
   {
      _session = session;
      _dlg = new SessionStartupTimeHintDlg(_session.getApplication().getMainFrame(), session.getApplication());

      _dlg.btnClose.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            close();
         }
      });

      _dlg.btnShowProps.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            showAliasProperties();
         }
      });

      GUIUtils.enableCloseByEscape(_dlg, dw -> savePrefs());

      _dlg.setSize(350, 180);
      GUIUtils.centerWithinParent(_dlg);
      _dlg.setVisible(true);
   }

   private void showAliasProperties()
   {
      close();

      SwingUtilities.invokeLater(() -> new AliasPropertiesCommand(_session.getAlias(), _session.getApplication()).execute());
   }

   private void close()
   {
      _dlg.dispose();
      savePrefs();
   }

   private void savePrefs()
   {
      SquirrelPreferences squirrelPreferences = _session.getApplication().getSquirrelPreferences();
      squirrelPreferences.setShowSessionStartupTimeHint(false == _dlg.chkDontShowAgain.isSelected());
   }

}
