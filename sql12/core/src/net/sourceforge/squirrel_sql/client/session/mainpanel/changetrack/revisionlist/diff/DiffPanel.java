package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.revisionlist.diff;

import net.sourceforge.squirrel_sql.client.session.action.dbdiff.gui.ConfigurableMeldPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class DiffPanel extends JPanel
{

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DiffPanel.class);

   JPanel pnlDiffContainer;
   JLabel lblLeftTitle = new JLabel();
   JLabel lblRightTitle = new JLabel();

   public DiffPanel(ConfigurableMeldPanel configurableMeldPanel)
   {
      setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,10,0,10), 1,1);
      add(configurableMeldPanel.getMeldConfigCtrl().getPanel(), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,10,0,10), 1,1);
      add(createDiffTitlePanel(), gbc);

      gbc = new GridBagConstraints(0,2,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 1,1);
      pnlDiffContainer = new JPanel(new GridLayout(1,1));
      add(pnlDiffContainer, gbc);
   }

   private JPanel createDiffTitlePanel()
   {
      JPanel ret = new JPanel(new BorderLayout());
      ret.add(lblLeftTitle, BorderLayout.WEST);
      ret.add(new JPanel(), BorderLayout.CENTER);
      ret.add(lblRightTitle, BorderLayout.EAST);

      return ret;
   }
}
