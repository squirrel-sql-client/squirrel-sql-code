package net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class RerunWithTimerRepeatsDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RerunWithTimerRepeatsDlg.class);

   IntegerField txtSeconds;
   JButton btnOK;
   JButton btnCancel;

   public RerunWithTimerRepeatsDlg(Frame parentWindow)
   {
      super(parentWindow, s_stringMgr.getString("RerunWithTimerRepeatsDlg.title"), true);
      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,5), 0,0);
      getContentPane().add(new JLabel(s_stringMgr.getString("RerunWithTimerRepeatsDlg.enter.repeat.interval.in.seconds")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,5), 0,0);
      txtSeconds = new IntegerField(5, 0);
      GUIUtils.setPreferredWidth(txtSeconds, 100);
      GUIUtils.setMinimumWidth(txtSeconds, 100);
      getContentPane().add(txtSeconds, gbc);

      gbc = new GridBagConstraints(0,2,2,1,0,0,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10,5,0,5), 0,0);
      getContentPane().add(createButtonPanel(), gbc);

      gbc = new GridBagConstraints(0,3,2,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0);
      getContentPane().add(new JPanel(), gbc);

      getRootPane().setDefaultButton(btnOK);
   }

   private JPanel createButtonPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      btnOK = new JButton(s_stringMgr.getString("RerunWithTimerRepeatsDlg.ok"));
      ret.add(btnOK, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0);
      btnCancel = new JButton(s_stringMgr.getString("RerunWithTimerRepeatsDlg.cancel"));
      ret.add(btnCancel, gbc);

      return ret;
   }
}
