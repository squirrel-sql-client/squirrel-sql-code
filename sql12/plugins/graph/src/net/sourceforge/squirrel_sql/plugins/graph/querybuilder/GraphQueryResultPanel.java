package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLResultExecutorPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.graph.HideDockButtonHandler;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class GraphQueryResultPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GraphQueryResultPanel.class);

   SQLResultExecutorPanel resultExecuterPanel;
   JCheckBox chkAutoSyncSQL;
   JButton btnSyncSQLResultNow;


   public GraphQueryResultPanel(ISession session, HideDockButtonHandler hideDockButtonHandler)
   {
      setLayout(new BorderLayout());

      resultExecuterPanel = new SQLResultExecutorPanel(session);
      add(createButtonPanel(hideDockButtonHandler), BorderLayout.NORTH);
      add(resultExecuterPanel, BorderLayout.CENTER);
   }

   private JPanel createButtonPanel(HideDockButtonHandler hideDockButtonHandler)
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,10),0,0);
      ret.add(hideDockButtonHandler.getHideButton(), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,0,5,5),0,0);
      chkAutoSyncSQL = new JCheckBox(s_stringMgr.getString("graph.GraphQueryResultPanel.autoSyncResult"));
      ret.add(chkAutoSyncSQL, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,0,5,5),0,0);
      btnSyncSQLResultNow = new JButton(s_stringMgr.getString("graph.GraphQueryResultPanel.syncResultNow"));
      ret.add(btnSyncSQLResultNow, gbc);

      gbc = new GridBagConstraints(3,0,1,1,1,1,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,0,5,5),0,0);
      ret.add(new JPanel(), gbc);

      return ret;
   }


   public void graphClosed()
   {
      //To change body of created methods use File | Settings | File Templates.
   }
}
