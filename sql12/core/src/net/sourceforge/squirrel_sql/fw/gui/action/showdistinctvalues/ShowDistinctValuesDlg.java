package net.sourceforge.squirrel_sql.fw.gui.action.showdistinctvalues;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.SmallToolTipInfoButton;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class ShowDistinctValuesDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ShowDistinctValuesDlg.class);

   JRadioButton optDistinctInColumn;
   JRadioButton optDistinctInSelection;
   JRadioButton optDistinctInSelectedRows;
   JRadioButton optDistinctInTable;

   JRadioButton optDistinctInColumns;
   JRadioButton optDistinctInRows;

   JScrollPane distinctTableScrollPane;
   JTextField lblStatus;
   SmallToolTipInfoButton btnStatusBarInfoToolTip;


   public ShowDistinctValuesDlg(JFrame owner, String selectedColumnName)
   {
      super(owner);

      setTitle(s_stringMgr.getString("ShowDistinctValuesDlg.title"));

      getContentPane().setLayout(new BorderLayout(5,5));
      getContentPane().add(createTopPanel(selectedColumnName), BorderLayout.NORTH);

      distinctTableScrollPane = new JScrollPane();
      getContentPane().add(distinctTableScrollPane, BorderLayout.CENTER);

      getContentPane().add(createStatusBar(), BorderLayout.SOUTH);
   }

   private JPanel createStatusBar()
   {
      JPanel ret = new JPanel(new BorderLayout());

      btnStatusBarInfoToolTip = new SmallToolTipInfoButton("");
      ret.add(btnStatusBarInfoToolTip.getButton(), BorderLayout.WEST);

      lblStatus = GUIUtils.styleTextFieldToCopyableLabel(new JTextField());
      ret.add(lblStatus, BorderLayout.CENTER);

      return ret;
   }

   private JPanel createTopPanel(String selectedColumnName)
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      ret.add(createDistinctInColumnPanel(selectedColumnName), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,35,0,5), 0,0);
      ret.add(createDistinctInRowsPanel(), gbc);

      gbc = new GridBagConstraints(1,2,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      ret.add(new JPanel(), gbc);

      return ret;
   }

   private JPanel createDistinctInColumnPanel(String selectedColumnName)
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      optDistinctInColumn = new JRadioButton(s_stringMgr.getString("ShowDistinctValuesDlg.optDistinctInColumn", selectedColumnName));
      ret.add(optDistinctInColumn, gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,0), 0,0);
      optDistinctInSelection = new JRadioButton(s_stringMgr.getString("ShowDistinctValuesDlg.optDistinctInSelection", selectedColumnName));
      ret.add(optDistinctInSelection, gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,0), 0,0);
      optDistinctInSelectedRows = new JRadioButton(s_stringMgr.getString("ShowDistinctValuesDlg.optDistinctInSelectedRows", selectedColumnName));
      ret.add(optDistinctInSelectedRows, gbc);

      gbc = new GridBagConstraints(0,3,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,0), 0,0);
      optDistinctInTable = new JRadioButton(s_stringMgr.getString("ShowDistinctValuesDlg.optDistinctInTable", selectedColumnName));
      ret.add(optDistinctInTable, gbc);

      ButtonGroup grpInSelection = new ButtonGroup();
      grpInSelection.add(optDistinctInColumn);
      grpInSelection.add(optDistinctInSelection);
      grpInSelection.add(optDistinctInSelectedRows);
      grpInSelection.add(optDistinctInTable);


      gbc = new GridBagConstraints(2,4,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      ret.add(new JPanel(), gbc);

      ret.setBorder(BorderFactory.createEtchedBorder());

      return ret;
   }

   private JPanel createDistinctInRowsPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,5), 0,0);
      optDistinctInColumns = new JRadioButton(s_stringMgr.getString("ShowDistinctValuesDlg.distinctInColumns"));
      ret.add(optDistinctInColumns, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      optDistinctInRows = new JRadioButton(s_stringMgr.getString("ShowDistinctValuesDlg.distinctInRows"));
      ret.add(optDistinctInRows, gbc);

      ButtonGroup grpInRows = new ButtonGroup();
      grpInRows.add(optDistinctInColumns);
      grpInRows.add(optDistinctInRows);


//      gbc = new GridBagConstraints(2,1,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
//      ret.add(new JPanel(), gbc);

      ret.setBorder(BorderFactory.createEtchedBorder());

      return ret;
   }

}
