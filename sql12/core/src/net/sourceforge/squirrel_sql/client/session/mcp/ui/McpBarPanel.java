package net.sourceforge.squirrel_sql.client.session.mcp.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class McpBarPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(McpBarPanel.class);

   final JRadioButton radAiQueryAsResTab;
   final JRadioButton radExecuteSqlViaDirectJdbcApi;
   final JCheckBox chkApplyAliasesReadOnlyRules;
   final JCheckBox chkAllowJdbcExecuteQueryOnly;

   final JCheckBox chkApproveAllAiCalls;
   final JCheckBox chkAllowAccessFormLocalhostOnly;
   final JToggleButton btnStartStopMcpServer;
   final JTextField txtMcpPort;
   final JButton btnCopyAiInfoPrompt;

   public McpBarPanel()
   {
      super(new GridBagLayout());

      GridBagConstraints gbc;

      // gridx=0
      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,0), 0,0);
      radAiQueryAsResTab = new JRadioButton(s_stringMgr.getString("McpBarPanel.ai.query.as.result.tab"));
      radAiQueryAsResTab.setToolTipText(s_stringMgr.getString("McpBarPanel.ai.query.as.result.tab.tooltip"));
      add(radAiQueryAsResTab, gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,5,5,0), 0,0);
      radExecuteSqlViaDirectJdbcApi = new JRadioButton(s_stringMgr.getString("McpBarPanel.execute.sql.via.jdbc"));
      radExecuteSqlViaDirectJdbcApi.setToolTipText(s_stringMgr.getString("McpBarPanel.execute.sql.via.jdbc.tooltip"));
      add(radExecuteSqlViaDirectJdbcApi, gbc);

      ButtonGroup buttonGroup = new ButtonGroup();
      buttonGroup.add(radAiQueryAsResTab);
      buttonGroup.add(radExecuteSqlViaDirectJdbcApi);


      // gridx=1
      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,0), 0,0);
      chkApplyAliasesReadOnlyRules = new JCheckBox(s_stringMgr.getString("McpBarPanel.apply.aliases.read.only.rule"));
      chkApplyAliasesReadOnlyRules.setToolTipText(s_stringMgr.getString("McpBarPanel.apply.aliases.read.only.rule.tooltip"));
      add(chkApplyAliasesReadOnlyRules, gbc);

      gbc = new GridBagConstraints(1,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,5,5,0), 0,0);
      chkAllowJdbcExecuteQueryOnly = new JCheckBox(s_stringMgr.getString("McpBarPanel.allow.jdbc.executeQuery.only"));
      chkAllowJdbcExecuteQueryOnly.setToolTipText(s_stringMgr.getString("McpBarPanel.allow.jdbc.executeQuery.only.tooltip"));
      add(chkAllowJdbcExecuteQueryOnly, gbc);


      // gridx=2
      gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,0), 0,0);
      chkApproveAllAiCalls = new JCheckBox(s_stringMgr.getString("McpBarPanel.approve.all.ai.calls"));
      chkApproveAllAiCalls.setToolTipText(s_stringMgr.getString("McpBarPanel.approve.all.ai.calls.tooltip"));
      add(chkApproveAllAiCalls, gbc);

      gbc = new GridBagConstraints(2,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,5,5,0), 0,0);
      chkAllowAccessFormLocalhostOnly = new JCheckBox(s_stringMgr.getString("McpBarPanel.allow.access.from.localhost.only"));
      chkAllowAccessFormLocalhostOnly.setToolTipText(s_stringMgr.getString("McpBarPanel.allow.access.from.localhost.only.tooltip"));
      add(chkAllowAccessFormLocalhostOnly, gbc);

      // gridx=3
      gbc = new GridBagConstraints(3,0,1,2,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,15,5,0), 0,0);
      btnStartStopMcpServer = new JToggleButton(s_stringMgr.getString("McpBarPanel.start.stop.mcp.server"));
      btnStartStopMcpServer.setToolTipText(s_stringMgr.getString("McpBarPanel.start.stop.mcp.server.tooltip"));
      add(GUIUtils.setPreferredWidth(btnStartStopMcpServer, 180), gbc);

      // gridx=4
      gbc = new GridBagConstraints(4,0,1,2,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,0), 0,0);
      JLabel lblMcpPort = new JLabel(s_stringMgr.getString("McpBarPanel.mcp.port.label"));
      add(lblMcpPort, gbc);

      // gridx=5
      gbc = new GridBagConstraints(5,0,1,2,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,0), 0,0);
      txtMcpPort = new JTextField();
      txtMcpPort.setEditable(false);
      add(GUIUtils.setPreferredWidth(GUIUtils.setMinimumWidth(txtMcpPort, 55), 55), gbc);

      // gridx=6
      gbc = new GridBagConstraints(6,0,1,2,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,0), 0,0);
      btnCopyAiInfoPrompt = new JButton(s_stringMgr.getString("McpBarPanel.copy.as.prompt"));
      btnCopyAiInfoPrompt.setToolTipText(s_stringMgr.getString("McpBarPanel.copy.as.info.to.paste.to.ai.prompt.tooltip"));
      add(btnCopyAiInfoPrompt, gbc);

      // gridx=7
      gbc = new GridBagConstraints(7,0,1,2,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,15,5,0), 0,0);
      JButton btnSaveAiConfigMd= new JButton(s_stringMgr.getString("McpBarPanel.save.ai.config.md"));
      btnSaveAiConfigMd.setToolTipText(s_stringMgr.getString("McpBarPanel.save.ai.config.md.tooltip"));
      add(btnSaveAiConfigMd, gbc);


      // gridx=8 to left
      gbc = new GridBagConstraints(8,0,2,2,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,0), 0,0);
      add(new JPanel(), gbc);



   }
}
