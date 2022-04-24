package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

public class FindInPreferencesDlg extends JDialog
{
   private StringManager s_stringMgr = StringManagerFactory.getStringManager(FindInPreferencesDlg.class);

   JSplitPane splitPane;
   JTree tree;
   JTextField txtFind;
   JButton btnGoTo;
   JTextArea txtDetails;

   public FindInPreferencesDlg(MainFrame parent)
   {
      super(parent);

      setTitle(s_stringMgr.getString("FindInPreferencesDlg.title"));

      getContentPane().setLayout(new GridLayout(1,1));

      splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
      getContentPane().add(splitPane);

      splitPane.add(createLeftPanel());

      splitPane.add(createRightPanel());

      getRootPane().setDefaultButton(btnGoTo);
   }

   private JPanel createRightPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1, 0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5, 0, 5), 0,0);
      ret.add(new JLabel(s_stringMgr.getString("FindInPreferencesDlg.details")), gbc);

      gbc = new GridBagConstraints(0,1,1,1, 1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(3,5, 5, 5), 0,0);
      txtDetails = new JTextArea();
      Font font = new Font(Font.MONOSPACED, Font.PLAIN, txtDetails.getFont().getSize());
      txtDetails.setFont(font);
      txtDetails.setEditable(false);
      ret.add(new JScrollPane(txtDetails), gbc);
      return ret;
   }

   private JPanel createLeftPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1, 0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5, 0, 5), 0,0);
      ret.add(new JLabel(s_stringMgr.getString("FindInPreferencesDlg.enter.text.to.find")), gbc);

      gbc = new GridBagConstraints(0,1,1,1, 0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(3,5, 0, 5), 0,0);
      txtFind = new JTextField();
      ret.add(txtFind, gbc);

      gbc = new GridBagConstraints(0,2,1,1, 1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5, 5, 5), 0,0);
      tree = new JTree();
      ret.add(new JScrollPane(tree), gbc);

      gbc = new GridBagConstraints(0,3,1,1, 0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5, 5, 5), 0,0);
      btnGoTo = new JButton(s_stringMgr.getString("FindInPreferencesDlg.goto"));
      btnGoTo.setToolTipText(s_stringMgr.getString("FindInPreferencesDlg.goto.toolTip"));
      ret.add(btnGoTo, gbc);

      ret.setMinimumSize(new Dimension(20,20));
      return ret;
   }
}
