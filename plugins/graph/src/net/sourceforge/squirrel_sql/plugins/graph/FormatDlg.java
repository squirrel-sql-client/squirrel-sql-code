package net.sourceforge.squirrel_sql.plugins.graph;

import javax.swing.*;
import java.awt.*;


public class FormatDlg extends JDialog
{
   JList lstFormats;
   JTextField txtName;
   JTextField txtHeight;
   JTextField txtWidth;
   JButton btnSave;
   JButton btnNew;
   JComboBox cboUnit;

   public FormatDlg(JFrame parent)
   {
      super(parent, "Formats", false);

      getContentPane().setLayout(new GridLayout(1,2,10,0));

      lstFormats = new JList();
      getContentPane().add(new JScrollPane(lstFormats));

      JPanel pnlEdit = new JPanel();

      pnlEdit.setLayout(new GridLayout(4,1));

      JPanel pnlName = new JPanel(new BorderLayout());
      JLabel lblName = new JLabel("Name");
      pnlName.add(lblName, BorderLayout.WEST);
      txtName = new JTextField();
      pnlName.add(txtName, BorderLayout.CENTER);

      pnlEdit.add(pnlName);

      JPanel pnlHeight = new JPanel(new BorderLayout());
      JLabel lblHeight = new JLabel("Height");
      pnlHeight.add(lblHeight, BorderLayout.WEST);
      txtHeight = new JTextField();
      pnlHeight.add(txtHeight, BorderLayout.CENTER);

      pnlEdit.add(pnlHeight);

      JPanel pnlWidth = new JPanel(new BorderLayout());
      JLabel lblWidth = new JLabel("Width");
      pnlWidth.add(lblWidth, BorderLayout.WEST);
      txtWidth = new JTextField();
      pnlWidth.add(txtWidth, BorderLayout.CENTER);

      pnlEdit.add(pnlWidth);

      JPanel pnlUnit = new JPanel(new BorderLayout());
      JLabel lblUnit = new JLabel("Unit");
      pnlUnit.add(lblUnit, BorderLayout.WEST);
      cboUnit = new JComboBox();
      pnlUnit.add(cboUnit, BorderLayout.CENTER);

      pnlEdit.add(pnlUnit);


      lblName.setPreferredSize(lblHeight.getPreferredSize());
      lblWidth.setPreferredSize(lblHeight.getPreferredSize());
      lblUnit.setPreferredSize(lblHeight.getPreferredSize());

      JPanel pnlLeft = new JPanel();
      pnlLeft.setLayout(new BorderLayout());
      pnlLeft.add(pnlEdit, BorderLayout.NORTH);

      pnlLeft.add(new JPanel(), BorderLayout.CENTER);

      JPanel pnlButtons = new JPanel(new GridLayout(1,2));
      btnSave = new JButton("Save");
      btnNew = new JButton("New");
      pnlButtons.add(btnNew);
      pnlButtons.add(btnSave);

      pnlLeft.add(pnlButtons, BorderLayout.SOUTH);


      getContentPane().add(pnlLeft);

      setSize(440, 200);

      setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);


   }



}
