package net.sourceforge.squirrel_sql.client.session.mcp.ui;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class McpCallApproveDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(McpCallApproveDlg.class);

   final ISQLEntryPanel sqlEntryPanel;
   final JButton btnFormat = new JButton(s_stringMgr.getString("McpCallApproveDlg.format"));
   final JButton btnDisapprove = new JButton(s_stringMgr.getString("McpCallApproveDlg.disapprove"));
   final JButton btnApprove = new JButton(s_stringMgr.getString("McpCallApproveDlg.approve"));


   public McpCallApproveDlg(Frame owner, ISQLEntryPanel sqlEntryPanel)
   {
      super(owner, true);
      this.sqlEntryPanel = sqlEntryPanel;
      setTitle(s_stringMgr.getString("McpCallApproveDlg.approve.ai.call.title"));

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,5),0,0);
      getContentPane().add(new JLabel(s_stringMgr.getString("McpCallApproveDlg.approve.ai.call.label")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,0,5),0,0);
      getContentPane().add(new JScrollPane(sqlEntryPanel.getTextComponent()), gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      getContentPane().add(createButtonPanel(), gbc);
   }

   private JPanel createButtonPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0),0,0);
      ret.add(btnFormat, gbc);

      gbc = new GridBagConstraints(1,0,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0),0,0);
      ret.add(new JPanel(), gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0),0,0);
      ret.add(btnDisapprove, gbc);

      gbc = new GridBagConstraints(3,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,0,0),0,0);
      ret.add(btnApprove, gbc);

      return ret;
   }
}
