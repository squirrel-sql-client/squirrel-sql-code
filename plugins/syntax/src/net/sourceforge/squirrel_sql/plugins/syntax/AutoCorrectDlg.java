package net.sourceforge.squirrel_sql.plugins.syntax;


import javax.swing.*;
import java.awt.*;


public class AutoCorrectDlg extends JDialog
{
   JTable tblAutoCorrects;
   JCheckBox chkEnable;
   JButton btnAply;
   JButton btnAddRow;
   JButton btnRemoveRows;


   public AutoCorrectDlg(JFrame parent)
   {
      super(parent, "Configure auto correct");
      tblAutoCorrects = new JTable();

      getContentPane().setLayout(new BorderLayout());
      getContentPane().add(new JScrollPane(tblAutoCorrects), BorderLayout.CENTER);

      JPanel pnlNorth = new JPanel(new GridLayout(3,1));
      JLabel lblNote = new JLabel("Note: Auto corrects will work only with the Netbeans editor.");
      lblNote.setForeground(Color.red);
      pnlNorth.add(lblNote);
      lblNote = new JLabel("See menu File --> New Session Properties --> Syntax");
      lblNote.setForeground(Color.red);
      pnlNorth.add(lblNote);

      chkEnable = new JCheckBox("Enable auto correct");
      pnlNorth.add(chkEnable);

      getContentPane().add(pnlNorth, BorderLayout.NORTH);

      JPanel pnlSouth = new JPanel();
      pnlSouth.setLayout(new GridLayout(1,3));

      btnAply = new JButton("Apply");
      btnAddRow = new JButton("Add row");
      btnRemoveRows = new JButton("remove selected rows");

      pnlSouth.add(btnAply);
      pnlSouth.add(btnAddRow);
      pnlSouth.add(btnRemoveRows);

      getContentPane().add(pnlSouth, BorderLayout.SOUTH);
   }

}
