package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.revisionlist.diff;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

public class DiffToLocalPanel extends JPanel
{

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DiffToLocalPanel.class);

   JPanel pnlDiffContainer;
   JLabel lblGitRevision = new JLabel();

   public DiffToLocalPanel()
   {
      setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,10,0,10), 1,1);
      add(createDiffTitlePanel(), gbc);

      gbc = new GridBagConstraints(0,1,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 1,1);
      pnlDiffContainer = new JPanel(new GridLayout(1,1));
      add(pnlDiffContainer, gbc);
   }

   private JPanel createDiffTitlePanel()
   {
      JPanel ret = new JPanel(new BorderLayout());
      ret.add(lblGitRevision, BorderLayout.WEST);
      ret.add(new JPanel(), BorderLayout.CENTER);
      ret.add(new JLabel(s_stringMgr.getString("DiffToLocalPanel.sqlEditor")), BorderLayout.EAST);

      return ret;
   }
}
