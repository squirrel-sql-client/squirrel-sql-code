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

      AbstractAction closeAction = new AbstractAction()
      {
         public void actionPerformed(ActionEvent actionEvent)
         {
            close();
         }
      };
      KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
      _dlg.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "CloseAction");
      _dlg.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "CloseAction");
      _dlg.getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "CloseAction");
      _dlg.getRootPane().getActionMap().put("CloseAction", closeAction);



      _dlg.setSize(350, 180);
      GUIUtils.centerWithinParent(_dlg);
      _dlg.setVisible(true);
   }

   private void showAliasProperties()
   {
      close();

      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            new AliasPropertiesCommand(_session.getAlias(), _session.getApplication()).execute();
         }
      });
   }

   private void close()
   {
      _dlg.dispose();
      SquirrelPreferences squirrelPreferences = _session.getApplication().getSquirrelPreferences();
      squirrelPreferences.setShowSessionStartupTimeHint(false == _dlg.chkDontShowAgain.isSelected());
   }

}
