package net.sourceforge.squirrel_sql.plugins.syntax;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;


public class AutoCorrectDlg extends JDialog
{
   JTable tblAutoCorrects;
   JCheckBox chkEnable;
   JButton btnApply;
   JButton btnAddRow;
   JButton btnRemoveRows;


   public AutoCorrectDlg(JFrame parent)
   {
      super(parent, "Configure auto correct /abreviation");

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      chkEnable = new JCheckBox("Enable auto correct / abreviation");
      gbc = new GridBagConstraints(0,0,3,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      getContentPane().add(chkEnable, gbc);

      tblAutoCorrects = new JTable();
      gbc = new GridBagConstraints(0,1,3,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5),0,0);
      getContentPane().add(new JScrollPane(tblAutoCorrects), gbc);


      btnApply = new JButton("Apply");
      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      getContentPane().add(btnApply, gbc);

      btnAddRow = new JButton("Add row");
      gbc = new GridBagConstraints(1,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      getContentPane().add(btnAddRow, gbc);

      btnRemoveRows = new JButton("remove selected rows");
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
