package net.sourceforge.squirrel_sql.client.preferences.shortcut;

import com.jidesoft.swing.MultilineLabel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class ShortcutPrefsPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ShortcutPrefsPanel.class);

   JTextField txtSelectedShortcut;

   JTextField txtShortcut = new JTextField(20);
   JButton btnApply = new JButton(s_stringMgr.getString("ShortcutPrefsPanel.btn.apply"));
   JButton btnRestoreDefault = new JButton(s_stringMgr.getString("ShortcutPrefsPanel.btn.restore.default"));;
   JButton btnRestoreAll = new JButton(s_stringMgr.getString("ShortcutPrefsPanel.btn.restore.all"));;

   DataSetViewerTablePanel tblShortcuts = new DataSetViewerTablePanel();

   public ShortcutPrefsPanel()
   {
      super(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      add(createEditPanel(), gbc);



      gbc = new GridBagConstraints(0,1,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0);
      tblShortcuts.init(null, null);
      add(new JScrollPane(tblShortcuts.getComponent()), gbc);

   }

   private JPanel createEditPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,5), 0,0);
      ret.add(new JLabel(s_stringMgr.getString("ShortcutPrefsPanel.lbl.edit.shortcut")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5), 0,0);
      txtSelectedShortcut = new JTextField();
      txtSelectedShortcut.setEditable(false);
      ret.add(txtSelectedShortcut, gbc);

      txtShortcut.setMinimumSize(new Dimension(40, txtShortcut.getMinimumSize().height));
      txtShortcut.setPreferredSize(new Dimension(40, txtShortcut.getPreferredSize().height));
      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      ret.add(txtShortcut, gbc);
      GUIUtils.forceProperty(() -> checkAndForceExecCounterSize());


      gbc = new GridBagConstraints(1,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      ret.add(btnApply, gbc);

      gbc = new GridBagConstraints(1,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      ret.add(btnRestoreDefault, gbc);

      gbc = new GridBagConstraints(1,3,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      ret.add(btnRestoreAll, gbc);

      gbc = new GridBagConstraints(0,4,2,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      ret.add(new MultipleLineLabel(s_stringMgr.getString("ShortcutPrefsPanel.notes")), gbc);


      return ret;
   }

   private boolean checkAndForceExecCounterSize()
   {
      int width = 120;
      txtShortcut.setPreferredSize(new Dimension(width, txtShortcut.getPreferredSize().height));
      txtShortcut.setMinimumSize(new Dimension(width, txtShortcut.getMinimumSize().height));
      //txtShortcut.setSize(txtShortcut.getPreferredSize());

      return txtShortcut.getSize().width == width;
   }

}
