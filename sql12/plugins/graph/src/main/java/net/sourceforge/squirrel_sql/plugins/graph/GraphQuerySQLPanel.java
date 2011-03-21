package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class GraphQuerySQLPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GraphQuerySQLPanel.class);

   JCheckBox chkAutoSyncSQL;
   JButton btnSyncSQLNow;

   public GraphQuerySQLPanel(JComponent editor, HideDockButtonHandler hideDockButtonHandler)
   {
      setLayout(new BorderLayout());
      add(createButtonPanel(hideDockButtonHandler), BorderLayout.NORTH);
      editor.setBorder(BorderFactory.createEtchedBorder());
      add(editor, BorderLayout.CENTER);
   }

   private JPanel createButtonPanel(HideDockButtonHandler hideDockButtonHandler)
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,10),0,0);
      ret.add(hideDockButtonHandler.getHideButton(), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,0,5,5),0,0);
      chkAutoSyncSQL = new JCheckBox(s_stringMgr.getString("graph.GraphQuerySQLPanel.autoSyncSQL"));
      ret.add(chkAutoSyncSQL, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,0,5,5),0,0);
      btnSyncSQLNow = new JButton(s_stringMgr.getString("graph.GraphQuerySQLPanel.syncSQLNow"));
      ret.add(btnSyncSQLNow, gbc);

      gbc = new GridBagConstraints(3,0,1,1,1,1,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,0,5,5),0,0);
      ret.add(new JPanel(), gbc);

      return ret;
   }
}
