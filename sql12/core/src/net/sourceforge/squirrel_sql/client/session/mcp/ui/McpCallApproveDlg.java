package net.sourceforge.squirrel_sql.client.session.mcp.ui;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class McpCallApproveDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(McpCallApproveDlg.class);

   final ISQLEntryPanel sqlEntryPanel;
   final JButton btnFormat = new JButton(s_stringMgr.getString("McpCallApproveDlg.format"));
   JButton btnEditAIResponseMessage;
   JButton btnRun;
   JButton btnFindInResult;
   final JButton btnDisapprove = new JButton(s_stringMgr.getString("McpCallApproveDlg.disapprove"));
   final JButton btnApprove = new JButton(s_stringMgr.getString("McpCallApproveDlg.approve"));

   JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
   JPanel lowerSplitPanel;

   public McpCallApproveDlg(Frame owner, ISQLEntryPanel sqlEntryPanel)
   {
      super(owner, true);
      this.sqlEntryPanel = sqlEntryPanel;
      setTitle(s_stringMgr.getString("McpCallApproveDlg.approve.ai.call.title"));

      getContentPane().setLayout(new GridLayout(1,1));
      getContentPane().add(splitPane);
      splitPane.setDividerSize(0);
      splitPane.setDividerLocation(getContentPane().getHeight());
      getContentPane().addComponentListener(new ComponentAdapter()
      {
         @Override
         public void componentResized(ComponentEvent e)
         {
            if(0 == splitPane.getDividerSize())
            {
               splitPane.setDividerLocation(getContentPane().getHeight());
            }
         }
      });


      JPanel upperSplitPanel = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,5),0,0);
      upperSplitPanel.add(new JLabel(s_stringMgr.getString("McpCallApproveDlg.approve.ai.call.label")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,0,5),0,0);
      upperSplitPanel.add(new JScrollPane(sqlEntryPanel.getTextComponent()), gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      upperSplitPanel.add(createButtonPanel(), gbc);

      splitPane.setLeftComponent(upperSplitPanel);

      lowerSplitPanel = new JPanel(new GridLayout(1,1));
      GUIUtils.setMinimumHeight(lowerSplitPanel, 0);
      splitPane.setRightComponent(lowerSplitPanel);
   }

   private JPanel createButtonPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0),0,0);
      ret.add(btnFormat, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,20,0,0),0,0);
      btnEditAIResponseMessage = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.EDIT_NOTE));
      btnEditAIResponseMessage.setToolTipText(s_stringMgr.getString("McpCallApproveDlg.edit.user.disapprove.info.for.ai"));
      ret.add(GUIUtils.styleAsToolbarButton(btnEditAIResponseMessage, true, true, btnFormat.getPreferredSize().height), gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,20,0,0),0,0);
      btnRun = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.RUN));
      btnRun.setToolTipText(s_stringMgr.getString("McpCallApproveDlg.runCall"));
      ret.add(GUIUtils.styleAsToolbarButton(btnRun, true, true, btnFormat.getPreferredSize().height), gbc);

      gbc = new GridBagConstraints(3,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,0),0,0);
      btnFindInResult = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.FIND));
      btnFindInResult.setToolTipText(s_stringMgr.getString("McpCallApproveDlg.findInResult"));
      btnFindInResult.setEnabled(false);
      ret.add(GUIUtils.styleAsToolbarButton(btnFindInResult, true, true, btnFormat.getPreferredSize().height), gbc);

      gbc = new GridBagConstraints(4,0,1,1,1,0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0),0,0);
      ret.add(new JPanel(), gbc);

      gbc = new GridBagConstraints(5,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0),0,0);
      ret.add(btnDisapprove, gbc);

      gbc = new GridBagConstraints(6,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,0),0,0);
      ret.add(btnApprove, gbc);

      return ret;
   }

   public void displayResult(JComponent comp)
   {
      lowerSplitPanel.removeAll();
      lowerSplitPanel.add(comp);
      splitPane.setDividerSize(new JSplitPane().getDividerSize());
      splitPane.setDividerLocation(getContentPane().getHeight() / 2);
      btnRun.setEnabled(false);
   }
}
