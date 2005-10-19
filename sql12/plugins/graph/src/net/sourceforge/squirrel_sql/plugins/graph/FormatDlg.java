package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManager;

import javax.swing.*;
import java.awt.*;


public class FormatDlg extends JDialog
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(FormatDlg.class);

	JList lstFormats;
   JTextField txtName;
   JTextField txtWidth;
   JTextField txtHeight;
   JCheckBox chkIsLandscape;
   JButton btnSave;
   JButton btnNew;
   JComboBox cboUnit;


   public FormatDlg(JFrame parent)
   {
		// i18n[graph.formats=Formats]
		super(parent, s_stringMgr.getString("graph.formats"), false);

      getContentPane().setLayout(new GridLayout(1,2,10,0));

      lstFormats = new JList();
      getContentPane().add(new JScrollPane(lstFormats));

      JPanel pnlEdit = new JPanel();

      pnlEdit.setLayout(new GridLayout(5,1));

      JPanel pnlName = new JPanel(new BorderLayout());

		// i18n[graph.name=Name]
      JLabel lblName = new JLabel(s_stringMgr.getString("graph.name"));
      pnlName.add(lblName, BorderLayout.WEST);
      txtName = new JTextField();
      pnlName.add(txtName, BorderLayout.CENTER);

      pnlEdit.add(pnlName);

      JPanel pnlWidth = new JPanel(new BorderLayout());
		// i18n[graph.width=Width]
		JLabel lblWidth = new JLabel(s_stringMgr.getString("graph.width"));
      pnlWidth.add(lblWidth, BorderLayout.WEST);
      txtWidth = new JTextField();
      pnlWidth.add(txtWidth, BorderLayout.CENTER);

      pnlEdit.add(pnlWidth);


      JPanel pnlHeight = new JPanel(new BorderLayout());

		// i18n[graph.height=Height]
		JLabel lblHeight = new JLabel(s_stringMgr.getString("graph.height"));
      pnlHeight.add(lblHeight, BorderLayout.WEST);
      txtHeight = new JTextField();
      pnlHeight.add(txtHeight, BorderLayout.CENTER);

      pnlEdit.add(pnlHeight);

      JPanel pnlUnit = new JPanel(new BorderLayout());
		// i18n[graph.unit=Unit]
		JLabel lblUnit = new JLabel(s_stringMgr.getString("graph.unit"));
      pnlUnit.add(lblUnit, BorderLayout.WEST);
      cboUnit = new JComboBox();
      pnlUnit.add(cboUnit, BorderLayout.CENTER);

      pnlEdit.add(pnlUnit);

		// i18n[graph.chkIsLandscape=Landscape]
		chkIsLandscape = new JCheckBox(s_stringMgr.getString("graph.chkIsLandscape"));
      pnlEdit.add(chkIsLandscape);
      

      lblName.setPreferredSize(lblHeight.getPreferredSize());
      lblWidth.setPreferredSize(lblHeight.getPreferredSize());
      lblUnit.setPreferredSize(lblHeight.getPreferredSize());

      JPanel pnlLeft = new JPanel();
      pnlLeft.setLayout(new BorderLayout());
      pnlLeft.add(pnlEdit, BorderLayout.NORTH);

      pnlLeft.add(new JPanel(), BorderLayout.CENTER);

      JPanel pnlButtons = new JPanel(new GridLayout(1,2));

		// i18n[graph.save=Save]
		btnSave = new JButton(s_stringMgr.getString("graph.save"));
		// i18n[graph.new=New]
      btnNew = new JButton(s_stringMgr.getString("graph.new"));
      pnlButtons.add(btnNew);
      pnlButtons.add(btnSave);

      pnlLeft.add(pnlButtons, BorderLayout.SOUTH);


      getContentPane().add(pnlLeft);

      setSize(440, 200);

      setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);


   }



}
