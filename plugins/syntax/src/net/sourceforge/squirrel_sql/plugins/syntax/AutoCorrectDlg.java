package net.sourceforge.squirrel_sql.plugins.syntax;


import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;


public class AutoCorrectDlg extends JDialog
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AutoCorrectDlg.class);


   JTable tblAutoCorrects;
   JCheckBox chkEnable;
   JButton btnApply;
   JButton btnAddRow;
   JButton btnRemoveRows;


   public AutoCorrectDlg(JFrame parent)
   {
		// i18n[syntax.configAutoCorr=Configure auto correct /abreviation]
		super(parent, s_stringMgr.getString("syntax.configAutoCorr"));

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;


		// i18n[syntax.enableAutoCorr=Enable auto correct / abreviation]
		chkEnable = new JCheckBox(s_stringMgr.getString("syntax.enableAutoCorr"));
      gbc = new GridBagConstraints(0,0,3,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      getContentPane().add(chkEnable, gbc);

      tblAutoCorrects = new JTable();
      gbc = new GridBagConstraints(0,1,3,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5),0,0);
      getContentPane().add(new JScrollPane(tblAutoCorrects), gbc);


		// i18n[syntax.autoCorrApply=Apply]
      btnApply = new JButton(s_stringMgr.getString("syntax.autoCorrApply"));
      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      getContentPane().add(btnApply, gbc);

		// i18n[syntax.addRow=Add row]
      btnAddRow = new JButton(s_stringMgr.getString("syntax.addRow"));
      gbc = new GridBagConstraints(1,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      getContentPane().add(btnAddRow, gbc);

		// i18n[syntax.removeRows=remove selected rows]
      btnRemoveRows = new JButton(s_stringMgr.getString("syntax.removeRows"));
      gbc = new GridBagConstraints(2,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      getContentPane().add(btnRemoveRows, gbc);

      getRootPane().setDefaultButton(btnApply);

      AbstractAction closeAction = new AbstractAction()
      {
         public void actionPerformed(ActionEvent actionEvent)
         {
            setVisible(false);
            dispose();
         }
      };
      KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
      getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "CloseAction");
      getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "CloseAction");
      getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "CloseAction");
      getRootPane().getActionMap().put("CloseAction", closeAction);

   }

}
