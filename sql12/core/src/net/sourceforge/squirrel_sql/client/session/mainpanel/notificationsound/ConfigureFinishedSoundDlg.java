package net.sourceforge.squirrel_sql.client.session.mainpanel.notificationsound;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

public class ConfigureFinishedSoundDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ConfigureFinishedSoundDlg.class);

   JCheckBox chkPLaySoundAfter;
   IntegerField txtTime;
   JButton btnChooseSoundToPlay;
   JToggleButton btnTestSound;
   JTextField txtSoundFile;
   JButton btnOk;
   JButton btnCancel;
   JComboBox<TimeUnit> cboTimeUnit;

   public ConfigureFinishedSoundDlg(JComponent parentComp)
   {
      super(GUIUtils.getOwningFrame(parentComp), s_stringMgr.getString("ConfigureFinishedSoundDlg.title"));

      getContentPane().setLayout(new GridLayout(1,1));
      getContentPane().add(createContentPane());

      GUIUtils.initLocation(this, 500, 180);

      GUIUtils.enableCloseByEscape(this);
   }

   private JPanel createContentPane()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      chkPLaySoundAfter = new JCheckBox(s_stringMgr.getString("ConfigureFinishedSoundDlg.check.play.sound.after"));
      ret.add(chkPLaySoundAfter, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      txtTime = new IntegerField(10, 1);
      GUIUtils.forceWidth(txtTime, 80);
      ret.add(txtTime, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      cboTimeUnit = new JComboBox<>(TimeUnit.values());
      ret.add(cboTimeUnit, gbc);


      gbc = new GridBagConstraints(0,1,GridBagConstraints.REMAINDER, 1,1,0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0,0);
      ret.add(createSoundFilePanel(), gbc);

      gbc = new GridBagConstraints(0,2,GridBagConstraints.REMAINDER, 1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(15,5,5,5), 0,0);
      ret.add(createOkCancelPanel(), gbc);

      return ret;
   }

   private JPanel createSoundFilePanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;
      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      btnChooseSoundToPlay = new JButton(s_stringMgr.getString("ConfigureFinishedSoundDlg.choose.sound.to.play"));
      ret.add(btnChooseSoundToPlay, gbc);

      gbc = new GridBagConstraints(1,0,1, 1,1,0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      txtSoundFile = new JTextField();
      txtSoundFile.setEditable(false);
      ret.add(txtSoundFile, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      btnTestSound = new JToggleButton(s_stringMgr.getString("ConfigureFinishedSoundDlg.sound.test"));
      ret.add(btnTestSound, gbc);

      return ret;
   }

   private JPanel createOkCancelPanel()
   {
      JPanel ret = new JPanel(new GridLayout(1,2, 5,5));

      btnOk = new JButton(s_stringMgr.getString("ConfigureFinishedSoundDlg.ok"));
      ret.add(btnOk);

      btnCancel = new JButton(s_stringMgr.getString("ConfigureFinishedSoundDlg.cancel"));
      ret.add(btnCancel);

      return ret;
   }
}
