package net.sourceforge.squirrel_sql.client.gui.db.mainframetitle;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

class MainFrameTitlePrefsPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(MainFrameTitlePrefsPanel.class);

   final JComboBox<PositionInMainFrameTitle> cboPosApplicationName;
   final JComboBox<PositionInMainFrameTitle> cboPosVersion;
   final JComboBox<PositionInMainFrameTitle> cboPosUserDir;
   final JComboBox<PositionInMainFrameTitle> cboPosHomeDir;
   final JComboBox<PositionInMainFrameTitle> cboPosSessionName;
   final JComboBox<PositionInMainFrameTitle> cboPosSavedSessionOrGroupName;

   MainFrameTitlePrefsPanel()
   {
      super(new GridBagLayout());
      setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("MainFrameTitlePrefsPanel.main.frame.title.config")));

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,2,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,3,0,0), 0, 0);
      add(new JLabel(s_stringMgr.getString("MainFrameTitlePrefsPanel.choose.information.to.display.in.title.bar")), gbc);


      // 1
      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3,3,0,0), 0, 0);
      add(new JLabel(s_stringMgr.getString("MainFrameTitlePrefsPanel.application.name")), gbc);

      gbc = new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3,3,0,0), 0, 0);
      cboPosApplicationName = createPositionComboBox();
      add(cboPosApplicationName, gbc);


      // 2
      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3,3,0,0), 0, 0);
      add(new JLabel(s_stringMgr.getString("MainFrameTitlePrefsPanel.version")), gbc);

      gbc = new GridBagConstraints(1,2,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3,3,0,0), 0, 0);
      cboPosVersion = createPositionComboBox();
      add(cboPosVersion, gbc);


      // 3
      gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3,3,0,0), 0, 0);
      add(new JLabel(s_stringMgr.getString("MainFrameTitlePrefsPanel.user.dir")), gbc);

      gbc = new GridBagConstraints(1,3,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3,3,0,0), 0, 0);
      cboPosUserDir = createPositionComboBox();
      add(cboPosUserDir, gbc);


      // 4
      gbc = new GridBagConstraints(0,4,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3,3,0,0), 0, 0);
      add(new JLabel(s_stringMgr.getString("MainFrameTitlePrefsPanel.home.dir")), gbc);

      gbc = new GridBagConstraints(1,4,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3,3,0,0), 0, 0);
      cboPosHomeDir = createPositionComboBox();
      add(cboPosHomeDir, gbc);


      // 5
      gbc = new GridBagConstraints(0,5,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3,3,0,0), 0, 0);
      add(new JLabel(s_stringMgr.getString("MainFrameTitlePrefsPanel.Session.name")), gbc);

      gbc = new GridBagConstraints(1,5,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3,3,0,0), 0, 0);
      cboPosSessionName = createPositionComboBox();
      add(cboPosSessionName, gbc);


      // 6
      gbc = new GridBagConstraints(0,6,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3,3,0,0), 0, 0);
      add(new JLabel(s_stringMgr.getString("MainFrameTitlePrefsPanel.saved.session.or.group.name")), gbc);

      gbc = new GridBagConstraints(1,6,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3,3,0,0), 0, 0);
      cboPosSavedSessionOrGroupName = createPositionComboBox();
      add(cboPosSavedSessionOrGroupName, gbc);


      gbc = new GridBagConstraints(2,7,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0);
      add(new JPanel(), gbc);
   }

   private JComboBox<PositionInMainFrameTitle> createPositionComboBox()
   {
      return new JComboBox<>(PositionInMainFrameTitle.values());
   }
}
