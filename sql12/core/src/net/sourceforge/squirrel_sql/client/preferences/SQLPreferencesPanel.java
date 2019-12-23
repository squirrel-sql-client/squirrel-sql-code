package net.sourceforge.squirrel_sql.client.preferences;

import com.jidesoft.swing.MultilineLabel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.ChangeTrackPrefsPanelController;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.gui.OutputLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

final class SQLPreferencesPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SQLPreferencesPanel.class);


   JRadioButton fileOpenInPreviousDir = new JRadioButton(s_stringMgr.getString("SQLPreferencesPanel.fileOpenInPreviousDir"));
   JRadioButton fileOpenInSpecifiedDir = new JRadioButton(s_stringMgr.getString("SQLPreferencesPanel.fileOpenInSpecifiedDir"));

   JTextField fileSpecifiedDir = new JTextField();
   JButton fileChooseDir = new JButton("...");

   private IntegerField _loginTimeout = new IntegerField();
   private IntegerField _largeScriptStmtCount = new IntegerField();
   private JCheckBox _chkCopyQuotedSqlsToClip = new JCheckBox(s_stringMgr.getString("SQLPreferencesPanel.copy.quoted.sql.to.clip"));
   private JCheckBox _chkAllowRunAllSQLsInEditor = new JCheckBox(s_stringMgr.getString("SQLPreferencesPanel.allow.run.all.sqls.in.editor"));
   private JCheckBox _chkMarkCurrentSql = new JCheckBox(s_stringMgr.getString("SQLPreferencesPanel.mark.current.sql"));
   private JButton _btnCurrentSqlMarkColorRGB = new JButton();

   private JCheckBox _chkReloadSqlContentsSql = new JCheckBox(s_stringMgr.getString("SQLPreferencesPanel.reload.sql.contents"));
   private IntegerField _txtMaxTextOutputColumnWidth = new IntegerField();

   private JRadioButton _debugJdbcDont = new JRadioButton(s_stringMgr.getString("SQLPreferencesPanel.jdbcdebugdont"));
   private JRadioButton _debugJdbcStream = new JRadioButton(s_stringMgr.getString("SQLPreferencesPanel.jdbcdebugstream"));
   private JRadioButton _debugJdbcWriter = new JRadioButton(s_stringMgr.getString("SQLPreferencesPanel.jdbcdebugwriter"));
   private JLabel _jdbcDebugLogFileNameLbl = new OutputLabel(" ");

   SQLPreferencesPanel()
   {
      super(new GridBagLayout());
      createUserInterface();
   }

   void loadData(SquirrelPreferences prefs)
   {
      final ApplicationFiles appFiles = new ApplicationFiles();
      _loginTimeout.setInt(prefs.getLoginTimeout());
      _largeScriptStmtCount.setInt(prefs.getLargeScriptStmtCount());
      _chkCopyQuotedSqlsToClip.setSelected(prefs.getCopyQuotedSqlsToClip());
      _chkAllowRunAllSQLsInEditor.setSelected(prefs.getAllowRunAllSQLsInEditor());

      _chkMarkCurrentSql.setSelected(prefs.isMarkCurrentSql());
      getCurrentSqlMarkColorIcon().setColor(new Color(prefs.getCurrentSqlMarkColorRGB()));

      initCurrentMarkGui();

      _chkReloadSqlContentsSql.setSelected(prefs.isReloadSqlContents());
      _txtMaxTextOutputColumnWidth.setInt(prefs.getMaxTextOutputColumnWidth());


      _debugJdbcStream.setSelected(prefs.isJdbcDebugToStream());
      _debugJdbcWriter.setSelected(prefs.isJdbcDebugToWriter());
      _debugJdbcDont.setSelected(prefs.isJdbcDebugDontDebug());
      _jdbcDebugLogFileNameLbl.setText(appFiles.getJDBCDebugLogFile().getPath());
      fileOpenInPreviousDir.setSelected(prefs.isFileOpenInPreviousDir());
      fileOpenInSpecifiedDir.setSelected(prefs.isFileOpenInSpecifiedDir());
      fileSpecifiedDir.setText(prefs.getFileSpecifiedDir());
   }

   private ColorIcon getCurrentSqlMarkColorIcon()
   {
      return (ColorIcon) _btnCurrentSqlMarkColorRGB.getIcon();
   }

   private void initCurrentMarkGui()
   {
      _btnCurrentSqlMarkColorRGB.setEnabled(_chkMarkCurrentSql.isSelected());

      _chkMarkCurrentSql.addActionListener(e -> _btnCurrentSqlMarkColorRGB.setEnabled(_chkMarkCurrentSql.isSelected()));

      _btnCurrentSqlMarkColorRGB.addActionListener(e -> onChooseCurrentMarkColor());
   }

   private void onChooseCurrentMarkColor()
   {
      String title = s_stringMgr.getString("SQLPreferencesPanel.current.sql.mark.color.choose");
      Color color = JColorChooser.showDialog(this, title, getCurrentSqlMarkColorIcon().getColor());

      if (null != color)
      {
         getCurrentSqlMarkColorIcon().setColor(color);
      }
   }

   void applyChanges(SquirrelPreferences prefs)
   {
      prefs.setLoginTimeout(_loginTimeout.getInt());
      prefs.setLargeScriptStmtCount(_largeScriptStmtCount.getInt());

      prefs.setCopyQuotedSqlsToClip(_chkCopyQuotedSqlsToClip.isSelected());
      prefs.setAllowRunAllSQLsInEditor(_chkAllowRunAllSQLsInEditor.isSelected());

      prefs.setMarkCurrentSql(_chkMarkCurrentSql.isSelected());
      prefs.setCurrentSqlMarkColorRGB((getCurrentSqlMarkColorIcon()).getColor().getRGB());

      prefs.setReloadSqlContents(_chkReloadSqlContentsSql.isSelected());


      int maxTextOutputColumnWidth = _txtMaxTextOutputColumnWidth.getInt();
      if (IDataSetViewer.MIN_COLUMN_WIDTH <= maxTextOutputColumnWidth)
      {
         prefs.setMaxTextOutputColumnWidth(maxTextOutputColumnWidth);
      }

      if (_debugJdbcStream.isSelected())
      {
         prefs.doJdbcDebugToStream();
      }
      else if (_debugJdbcWriter.isSelected())
      {
         prefs.doJdbcDebugToWriter();
      }
      else
      {
         prefs.dontDoJdbcDebug();
      }

      prefs.setFileOpenInPreviousDir(fileOpenInPreviousDir.isSelected());
      prefs.setFileOpenInSpecifiedDir(fileOpenInSpecifiedDir.isSelected());
      String specDir = fileSpecifiedDir.getText();
      prefs.setFileSpecifiedDir(null == specDir ? "" : specDir);


   }

   private void createUserInterface()
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
      add(new ChangeTrackPrefsPanelController().getPanel(), gbc);
      ++gbc.gridy;
      add(createDebugPanel(), gbc);
   }

   private JPanel createGeneralPanel()
   {
      JPanel pnl = new JPanel(new GridBagLayout());
      pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("SQLPreferencesPanel.general")));

      _loginTimeout.setColumns(4);

      GridBagConstraints gbc = new GridBagConstraints();
      gbc.anchor = GridBagConstraints.NORTHWEST;
      gbc.fill = GridBagConstraints.NONE;
      gbc.insets = new Insets(4, 4, 4, 4);

      gbc.gridx = 0;
      gbc.gridy = 0;
      pnl.add(new JLabel(s_stringMgr.getString("SQLPreferencesPanel.logintimeout")), gbc);

      ++gbc.gridx;
      pnl.add(_loginTimeout, gbc);

      ++gbc.gridx;
      gbc.weightx = 1;
      pnl.add(new JLabel(s_stringMgr.getString("SQLPreferencesPanel.zerounlimited")), gbc);

      _largeScriptStmtCount.setColumns(4);

      gbc = new GridBagConstraints();
      gbc.anchor = GridBagConstraints.NORTHWEST;
      gbc.fill = GridBagConstraints.NONE;
      gbc.insets = new Insets(4, 4, 4, 4);

      gbc.gridx = 0;
      gbc.gridy = 1;
      // i18n[SQLPreferencesPanel.largeScriptStmtCount=Large Script Statement Count: ]
      pnl.add(new JLabel(s_stringMgr.getString("SQLPreferencesPanel.largeScriptStmtCount")), gbc);

      ++gbc.gridx;
      pnl.add(_largeScriptStmtCount, gbc);

      ++gbc.gridx;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      pnl.add(new MultilineLabel(s_stringMgr.getString("SQLPreferencesPanel.largeScriptStmtCount.note")), gbc);


      gbc.gridx = 0;
      gbc.gridy = 2;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      pnl.add(_chkCopyQuotedSqlsToClip, gbc);

      gbc.gridx = 0;
      gbc.gridy = 3;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      pnl.add(_chkAllowRunAllSQLsInEditor, gbc);

      gbc.gridx = 0;
      gbc.gridy = 4;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gbc.fill = GridBagConstraints.NONE;
      pnl.add(createCurrentSqlMarkPanel(), gbc);

      gbc.gridx = 0;
      gbc.gridy = 5;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gbc.fill = GridBagConstraints.NONE;
      pnl.add(_chkReloadSqlContentsSql, gbc);

      gbc.gridx = 0;
      gbc.gridy = 6;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gbc.fill = GridBagConstraints.NONE;
      pnl.add(createMaxTextOutputColumnWidthPanel(), gbc);

      return pnl;
   }

   private JPanel createMaxTextOutputColumnWidthPanel()
   {
      //JPanel ret = new JPanel(new GridLayout(1,2,5,0));
      JPanel ret = new JPanel(new BorderLayout(5, 0));

      ret.add(new JLabel(s_stringMgr.getString("SQLPreferencesPanel.MaxTextOutputColumnWidthPanel.label", IDataSetViewer.MIN_COLUMN_WIDTH)), BorderLayout.WEST);
      ret.add(_txtMaxTextOutputColumnWidth, BorderLayout.CENTER);

      return ret;
   }

   private JPanel createCurrentSqlMarkPanel()
   {
      JPanel ret = new JPanel(new BorderLayout(5, 0));

      ret.add(_chkMarkCurrentSql, BorderLayout.WEST);
      ret.add(_btnCurrentSqlMarkColorRGB, BorderLayout.CENTER);

      _btnCurrentSqlMarkColorRGB.setHorizontalTextPosition(JButton.LEFT);
      _btnCurrentSqlMarkColorRGB.setIcon(new ColorIcon(16, 16));
      _btnCurrentSqlMarkColorRGB.setText(s_stringMgr.getString("SQLPreferencesPanel.current.sql.mark.color"));

      return ret;
   }

   private JPanel createDebugPanel()
   {
      final ButtonGroup btnGroup = new ButtonGroup();
      btnGroup.add(_debugJdbcDont);
      btnGroup.add(_debugJdbcStream);
      btnGroup.add(_debugJdbcWriter);

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
      pnl.add(_debugJdbcDont, gbc);

      ++gbc.gridy;
      pnl.add(_debugJdbcStream, gbc);

      ++gbc.gridy;
      pnl.add(_debugJdbcWriter, gbc);

      gbc.gridx = 0;
      ++gbc.gridy;
      gbc.gridwidth = 1;
      pnl.add(new JLabel(s_stringMgr.getString("SQLPreferencesPanel.jdbcdebugfile"), SwingConstants.RIGHT), gbc);

      ++gbc.gridx;
      gbc.weightx = 1;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      pnl.add(_jdbcDebugLogFileNameLbl, gbc);

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
