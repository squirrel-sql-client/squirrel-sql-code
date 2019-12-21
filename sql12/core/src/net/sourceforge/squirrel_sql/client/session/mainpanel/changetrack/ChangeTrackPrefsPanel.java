package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class ChangeTrackPrefsPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ChangeTrackPrefsPanelController.class);
   private final JCheckBox chkEnableChangeTracking;
   private final JCheckBox chkGitCommitMsgManually;
   private final JTextField txtGitCommitMsgDefault;

   private JButton btnDeltedForeground;
   private JToggleButton btnDeletedItalics;
   private JToggleButton btnDeletedBold;


   public ChangeTrackPrefsPanel()
   {
      super(new GridBagLayout());
      setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("ChangeTrackPrefsPanel.title")));

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5), 0,0);
      add(new MultipleLineLabel(s_stringMgr.getString("ChangeTrackPrefsPanel.description")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5), 0,0);
      chkEnableChangeTracking = new JCheckBox(s_stringMgr.getString("ChangeTrackPrefsPanel.chk.enableChangeTracking"));
      add(chkEnableChangeTracking, gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10,5,0,5), 0,0);
      chkGitCommitMsgManually = new JCheckBox(s_stringMgr.getString("ChangeTrackPrefsPanel.chk.edit.git.message.manually"));
      add(chkGitCommitMsgManually, gbc);

      gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5), 0,0);
      txtGitCommitMsgDefault = new JTextField();
      add(txtGitCommitMsgDefault, gbc);

      gbc = new GridBagConstraints(0,4,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10,5,0,5), 0,0);
      add(createChangedPopupStylePanel(), gbc);

      gbc = new GridBagConstraints(0,5,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0);
      add(new JPanel(), gbc);



   }

   private JPanel createChangedPopupStylePanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      SquirrelResources rsrc = Main.getApplication().getResources();

      GridBagConstraints gbc;

      // Row 1
      gbc = new GridBagConstraints(0,0,4,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,5,0), 0,0);
      ret. add(new JLabel(s_stringMgr.getString("ChangeTrackPrefsPanel.change.popup.style")), gbc);


      // Row 2
      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      ret. add(new JLabel(s_stringMgr.getString("ChangeTrackPrefsPanel.deletions.mark")), gbc);

      gbc = new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0);
      btnDeletedBold = new JToggleButton(rsrc.getIcon(SquirrelResources.IImageNames.BOLD));
      ret. add(GUIUtils.styleAsToolbarButton(btnDeletedBold), gbc);

      gbc = new GridBagConstraints(2,1,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      btnDeletedItalics = new JToggleButton(rsrc.getIcon(SquirrelResources.IImageNames.ITALIC));
      ret. add(GUIUtils.styleAsToolbarButton(btnDeletedItalics), gbc);

      gbc = new GridBagConstraints(3,1,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      btnDeltedForeground = new JButton(rsrc.getIcon(SquirrelResources.IImageNames.PEN));
      ret. add(GUIUtils.styleAsToolbarButton(btnDeltedForeground), gbc);



      gbc = new GridBagConstraints(1,2,4,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0);
      ret.add(new JPanel(), gbc);

      return ret;
   }
}
