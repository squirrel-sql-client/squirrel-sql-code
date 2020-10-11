package net.sourceforge.squirrel_sql.client.preferences;

import com.jidesoft.swing.MultilineLabel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.ChangeTrackPrefsPanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.gui.OutputLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public final class SQLPreferencesPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SQLPreferencesPanel.class);

   JRadioButton fileOpenInPreviousDir = new JRadioButton(s_stringMgr.getString("SQLPreferencesPanel.fileOpenInPreviousDir"));
   JRadioButton fileOpenInSpecifiedDir = new JRadioButton(s_stringMgr.getString("SQLPreferencesPanel.fileOpenInSpecifiedDir"));

   JTextField fileSpecifiedDir = new JTextField();
   JButton fileChooseDir = new JButton("...");

   IntegerField loginTimeout = new IntegerField();
   IntegerField largeScriptStmtCount = new IntegerField();
   JCheckBox chkCopyQuotedSqlsToClip = new JCheckBox(s_stringMgr.getString("SQLPreferencesPanel.copy.quoted.sql.to.clip"));
   JCheckBox chkAllowRunAllSQLsInEditor = new JCheckBox(s_stringMgr.getString("SQLPreferencesPanel.allow.run.all.sqls.in.editor"));
   JCheckBox chkMarkCurrentSql = new JCheckBox(s_stringMgr.getString("SQLPreferencesPanel.mark.current.sql"));
   JButton btnCurrentSqlMarkColorRGB = new JButton();

   JCheckBox chkReloadSqlContentsSql = new JCheckBox(s_stringMgr.getString("SQLPreferencesPanel.reload.sql.contents"));
   IntegerField txtMaxTextOutputColumnWidth = new IntegerField();

   JCheckBox chkNotifyExternalFileChanges = new JCheckBox(s_stringMgr.getString("SQLPreferencesPanel.notify.external.file.changes"));

   JRadioButton debugJdbcDont = new JRadioButton(s_stringMgr.getString("SQLPreferencesPanel.jdbcdebugdont"));
   JRadioButton debugJdbcStream = new JRadioButton(s_stringMgr.getString("SQLPreferencesPanel.jdbcdebugstream"));
   JRadioButton debugJdbcWriter = new JRadioButton(s_stringMgr.getString("SQLPreferencesPanel.jdbcdebugwriter"));
   JLabel jdbcDebugLogFileNameLbl = new OutputLabel(" ");

   SQLPreferencesPanel(ChangeTrackPrefsPanel changeTrackPrefsPanel)
   {
      super(new GridBagLayout());
      createUserInterface(changeTrackPrefsPanel);
   }


   ColorIcon getCurrentSqlMarkColorIcon()
   {
      return (ColorIcon) btnCurrentSqlMarkColorRGB.getIcon();
   }

   private void createUserInterface(ChangeTrackPrefsPanel panel)
   {
      final GridBagConstraints gbc = new GridBagConstraints();
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.insets = new Insets(4, 4, 4, 4);
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.weightx = 1;
      add(createGeneralPanel(), gbc);
      ++gbc.gridy;
      add(createFilePanel(), gbc);
      ++gbc.gridy;
      add(panel, gbc);
      ++gbc.gridy;
      add(createDebugPanel(), gbc);
   }

   private JPanel createGeneralPanel()
   {
      JPanel pnl = new JPanel(new GridBagLayout());
      pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("SQLPreferencesPanel.general")));

      loginTimeout.setColumns(4);

      GridBagConstraints gbc = new GridBagConstraints();
      gbc.anchor = GridBagConstraints.NORTHWEST;
      gbc.fill = GridBagConstraints.NONE;
      gbc.insets = new Insets(4, 4, 4, 4);

      gbc.gridx = 0;
      gbc.gridy = 0;
      pnl.add(new JLabel(s_stringMgr.getString("SQLPreferencesPanel.logintimeout")), gbc);

      ++gbc.gridx;
      pnl.add(loginTimeout, gbc);

      ++gbc.gridx;
      gbc.weightx = 1;
      pnl.add(new JLabel(s_stringMgr.getString("SQLPreferencesPanel.zerounlimited")), gbc);

      largeScriptStmtCount.setColumns(4);

      gbc = new GridBagConstraints();
      gbc.anchor = GridBagConstraints.NORTHWEST;
      gbc.fill = GridBagConstraints.NONE;
      gbc.insets = new Insets(4, 4, 4, 4);

      gbc.gridx = 0;
      gbc.gridy = 1;
      // i18n[SQLPreferencesPanel.largeScriptStmtCount=Large Script Statement Count: ]
      pnl.add(new JLabel(s_stringMgr.getString("SQLPreferencesPanel.largeScriptStmtCount")), gbc);

      ++gbc.gridx;
      pnl.add(largeScriptStmtCount, gbc);

      ++gbc.gridx;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      pnl.add(new MultilineLabel(s_stringMgr.getString("SQLPreferencesPanel.largeScriptStmtCount.note")), gbc);


      gbc.gridx = 0;
      gbc.gridy = 2;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      pnl.add(chkCopyQuotedSqlsToClip, gbc);

      gbc.gridx = 0;
      gbc.gridy = 3;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      pnl.add(chkAllowRunAllSQLsInEditor, gbc);

      gbc.gridx = 0;
      gbc.gridy = 4;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gbc.fill = GridBagConstraints.NONE;
      pnl.add(createCurrentSqlMarkPanel(), gbc);

      gbc.gridx = 0;
      gbc.gridy = 5;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      pnl.add(createReloadSQLContentsPanel(), gbc);

      gbc.gridx = 0;
      gbc.gridy = 6;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gbc.fill = GridBagConstraints.NONE;
      pnl.add(createMaxTextOutputColumnWidthPanel(), gbc);

      gbc.gridx = 0;
      gbc.gridy = 7;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gbc.fill = GridBagConstraints.NONE;
      pnl.add(chkNotifyExternalFileChanges, gbc);

      return pnl;
   }

   private JPanel createReloadSQLContentsPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,3,3,3), 0,0);
      ret.add(chkReloadSqlContentsSql, gbc);

      gbc = new GridBagConstraints(0,1,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,3,3,3), 0,0);
      ret.add(new MultilineLabel(s_stringMgr.getString("SQLPreferencesPanel.reload.changetrack.hint")), gbc);

      ret.setBorder(BorderFactory.createEtchedBorder());
      return ret;
   }

   private JPanel createMaxTextOutputColumnWidthPanel()
   {
      //JPanel ret = new JPanel(new GridLayout(1,2,5,0));
      JPanel ret = new JPanel(new BorderLayout(5, 0));

      ret.add(new JLabel(s_stringMgr.getString("SQLPreferencesPanel.MaxTextOutputColumnWidthPanel.label", IDataSetViewer.MIN_COLUMN_WIDTH)), BorderLayout.WEST);
      ret.add(txtMaxTextOutputColumnWidth, BorderLayout.CENTER);

      return ret;
   }

   private JPanel createCurrentSqlMarkPanel()
   {
      JPanel ret = new JPanel(new BorderLayout(5, 0));

      ret.add(chkMarkCurrentSql, BorderLayout.WEST);
      ret.add(btnCurrentSqlMarkColorRGB, BorderLayout.CENTER);

      btnCurrentSqlMarkColorRGB.setHorizontalTextPosition(JButton.LEFT);
      btnCurrentSqlMarkColorRGB.setIcon(new ColorIcon(16, 16));
      btnCurrentSqlMarkColorRGB.setText(s_stringMgr.getString("SQLPreferencesPanel.current.sql.mark.color"));

      return ret;
   }

   private JPanel createDebugPanel()
   {
      final ButtonGroup btnGroup = new ButtonGroup();
      btnGroup.add(debugJdbcDont);
      btnGroup.add(debugJdbcStream);
      btnGroup.add(debugJdbcWriter);

      JPanel pnl = new JPanel(new GridBagLayout());
      pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("SQLPreferencesPanel.debug")));

      final GridBagConstraints gbc = new GridBagConstraints();
      gbc.anchor = GridBagConstraints.WEST;
      gbc.insets = new Insets(4, 4, 4, 4);

      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.gridx = 0;
      ++gbc.gridy;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      pnl.add(debugJdbcDont, gbc);

      ++gbc.gridy;
      pnl.add(debugJdbcStream, gbc);

      ++gbc.gridy;
      pnl.add(debugJdbcWriter, gbc);

      gbc.gridx = 0;
      ++gbc.gridy;
      gbc.gridwidth = 1;
      pnl.add(new JLabel(s_stringMgr.getString("SQLPreferencesPanel.jdbcdebugfile"), SwingConstants.RIGHT), gbc);

      ++gbc.gridx;
      gbc.weightx = 1;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      pnl.add(jdbcDebugLogFileNameLbl, gbc);

      return pnl;
   }

   private Component createFilePanel()
   {
      final ButtonGroup btnGroup = new ButtonGroup();
      btnGroup.add(fileOpenInPreviousDir);
      btnGroup.add(fileOpenInSpecifiedDir);

      JPanel pnl = new JPanel(new GridBagLayout());
      pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("SQLPreferencesPanel.file")));

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0);
      pnl.add(fileOpenInPreviousDir, gbc);

      gbc = new GridBagConstraints(1, 0, 2, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0);
      pnl.add(new JPanel(), gbc);


      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0);
      pnl.add(fileOpenInSpecifiedDir, gbc);

      gbc = new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0);
      pnl.add(fileSpecifiedDir, gbc);

      gbc = new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0);
      pnl.add(fileChooseDir, gbc);

      return pnl;

   }


}
