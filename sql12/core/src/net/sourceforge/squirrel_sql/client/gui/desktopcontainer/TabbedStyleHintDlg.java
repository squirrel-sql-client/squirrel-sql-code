package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.IApplication;

import javax.swing.*;
import java.awt.*;

public class TabbedStyleHintDlg extends JDialog
{

   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(TabbedStyleHintDlg.class);

   JCheckBox chkDontShowAgain;
   JButton btnYes;
   JButton btnNo;


   public TabbedStyleHintDlg()
   {
      super((Frame) null, s_stringMgr.getString("TabbedStyleHintDlg.title"),true);

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10,10,5,10), 0,0);
      getContentPane().add(new MultipleLineLabel(s_stringMgr.getString("TabbedStyleHintDlg.text")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,10,5,10), 0,0);
      chkDontShowAgain = new JCheckBox(s_stringMgr.getString("TabbedStyleHintDlg.dontShowAgain"));
      getContentPane().add(chkDontShowAgain, gbc);


      gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      getContentPane().add(createButtonPanel(), gbc);
   }

   private JPanel createButtonPanel()
   {
      JPanel ret = new JPanel();
      ret.setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0,0);
      btnYes = new JButton(s_stringMgr.getString("SessionStartupTimeHintDlg.yes"));
      ret.add(btnYes, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0,0);
      btnNo = new JButton(s_stringMgr.getString("SessionStartupTimeHintDlg.no"));
      ret.add(btnNo, gbc);

      return ret;
   }
}