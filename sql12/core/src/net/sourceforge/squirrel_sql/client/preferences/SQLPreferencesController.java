package net.sourceforge.squirrel_sql.client.preferences;
/*
 * Copyright (C) 2001-2003 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import java.awt.Color;
import java.awt.Component;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.ChangeTrackPrefsPanelController;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * This preferences panel allows maintenance of SQL preferences.
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLPreferencesController implements IGlobalPreferencesPanel
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SQLPreferencesController.class);

	/** Panel to be displayed in preferences dialog. */
	private SQLPreferencesPanel _panel;
   private JScrollPane _scrollPane;

	private ChangeTrackPrefsPanelController _changeTrackPrefsPanelController = new ChangeTrackPrefsPanelController();

	public SQLPreferencesController()
	{
   }

	public void initialize(IApplication app)
	{
		getPanelComponent();
		loadData();

      _panel.fileOpenInPreviousDir.addActionListener(e -> updateFilePanel(_panel));

      _panel.fileOpenInSpecifiedDir.addActionListener(e -> updateFilePanel(_panel));

      updateFilePanel(_panel);


      _panel.fileChooseDir.addActionListener(e -> onChooseDir(_panel));

	}

	void loadData()
	{
		SquirrelPreferences prefs = Main.getApplication().getSquirrelPreferences();

		_panel.loginTimeout.setInt(prefs.getLoginTimeout());
		_panel.largeScriptStmtCount.setInt(prefs.getLargeScriptStmtCount());
		_panel.queryTimeout.setInt(prefs.getQueryTimeout());
		_panel.chkCopyQuotedSqlsToClip.setSelected(prefs.getCopyQuotedSqlsToClip());
		_panel.chkAllowRunAllSQLsInEditor.setSelected(prefs.getAllowRunAllSQLsInEditor());

		_panel.chkMarkCurrentSql.setSelected(prefs.isMarkCurrentSql());
		_panel.getCurrentSqlMarkColorIcon().setColor(new Color(prefs.getCurrentSqlMarkColorRGB()));
		initCurrentMarkGui();

		_panel.chkUseStatementSeparatorAsSqlToExecuteBounds.setSelected(prefs.isUseStatementSeparatorAsSqlToExecuteBounds());

		_panel.chkReloadSqlContentsSql.setSelected(prefs.isReloadSqlContents());
		_panel.txtMaxTextOutputColumnWidth.setInt(prefs.getMaxTextOutputColumnWidth());

		_panel.chkNotifyExternalFileChanges.setSelected(prefs.isNotifyExternalFileChanges());


		_panel.debugJdbcStream.setSelected(prefs.isJdbcDebugToStream());
		_panel.debugJdbcWriter.setSelected(prefs.isJdbcDebugToWriter());
		_panel.debugJdbcDont.setSelected(prefs.isJdbcDebugDontDebug());
		_panel.jdbcDebugLogFileNameLbl.setText(new ApplicationFiles().getJDBCDebugLogFile().getPath());
		_panel.fileOpenInPreviousDir.setSelected(prefs.isFileOpenInPreviousDir());
		_panel.fileOpenInSpecifiedDir.setSelected(prefs.isFileOpenInSpecifiedDir());
		_panel.fileSpecifiedDir.setText(prefs.getFileSpecifiedDir());

		_changeTrackPrefsPanelController.loadData(prefs);
	}

	public void applyChanges()
	{
		SquirrelPreferences prefs = Main.getApplication().getSquirrelPreferences();

		prefs.setLoginTimeout(_panel.loginTimeout.getInt());
		prefs.setLargeScriptStmtCount(_panel.largeScriptStmtCount.getInt());
		prefs.setQueryTimeout(_panel.queryTimeout.getInt());

		prefs.setCopyQuotedSqlsToClip(_panel.chkCopyQuotedSqlsToClip.isSelected());
		prefs.setAllowRunAllSQLsInEditor(_panel.chkAllowRunAllSQLsInEditor.isSelected());

		prefs.setMarkCurrentSql(_panel.chkMarkCurrentSql.isSelected());
		prefs.setCurrentSqlMarkColorRGB((_panel.getCurrentSqlMarkColorIcon()).getColor().getRGB());

		prefs.setUseStatementSeparatorAsSqlToExecuteBounds(_panel.chkUseStatementSeparatorAsSqlToExecuteBounds.isSelected());

		prefs.setReloadSqlContents(_panel.chkReloadSqlContentsSql.isSelected());


		int maxTextOutputColumnWidth = _panel.txtMaxTextOutputColumnWidth.getInt();
		if (IDataSetViewer.MIN_COLUMN_WIDTH <= maxTextOutputColumnWidth)
		{
			prefs.setMaxTextOutputColumnWidth(maxTextOutputColumnWidth);
		}

		prefs.setNotifyExternalFileChanges(_panel.chkNotifyExternalFileChanges.isSelected());

		if (_panel.debugJdbcStream.isSelected())
		{
			prefs.doJdbcDebugToStream();
		}
		else if (_panel.debugJdbcWriter.isSelected())
		{
			prefs.doJdbcDebugToWriter();
		}
		else
		{
			prefs.dontDoJdbcDebug();
		}

		prefs.setFileOpenInPreviousDir(_panel.fileOpenInPreviousDir.isSelected());
		prefs.setFileOpenInSpecifiedDir(_panel.fileOpenInSpecifiedDir.isSelected());
		String specDir = _panel.fileSpecifiedDir.getText();
		prefs.setFileSpecifiedDir(null == specDir ? "" : specDir);

		_changeTrackPrefsPanelController.applyChanges(prefs);
	}


	private void initCurrentMarkGui()
	{
		_panel.btnCurrentSqlMarkColorRGB.setEnabled(_panel.chkMarkCurrentSql.isSelected());

		_panel.chkMarkCurrentSql.addActionListener(e -> _panel.btnCurrentSqlMarkColorRGB.setEnabled(_panel.chkMarkCurrentSql.isSelected()));

		_panel.btnCurrentSqlMarkColorRGB.addActionListener(e -> onChooseCurrentMarkColor());
	}

	private void onChooseCurrentMarkColor()
	{
		String title = s_stringMgr.getString("SQLPreferencesPanel.current.sql.mark.color.choose");
		Color color = JColorChooser.showDialog(_panel, title, _panel.getCurrentSqlMarkColorIcon().getColor());

		if (null != color)
		{
			_panel.getCurrentSqlMarkColorIcon().setColor(color);
		}
	}



	public void uninitialize(IApplication app)
   {
      
   }


   public void onChooseDir(SQLPreferencesPanel pnl)
   {
      JFileChooser chooser = new JFileChooser(pnl.fileSpecifiedDir.getText());
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      int returnVal = chooser.showOpenDialog(Main.getApplication().getMainFrame());
      if (returnVal == JFileChooser.APPROVE_OPTION)
      {
         pnl.fileSpecifiedDir.setText(chooser.getSelectedFile().getAbsolutePath());
      }
   }


   private void updateFilePanel(SQLPreferencesPanel pnl)
   {
      pnl.fileChooseDir.setEnabled(pnl.fileOpenInSpecifiedDir.isSelected());
      pnl.fileSpecifiedDir.setEnabled(pnl.fileOpenInSpecifiedDir.isSelected());
   }

   public Component getPanelComponent()
	{
		if (_panel == null)
		{
			_panel = new SQLPreferencesPanel(_changeTrackPrefsPanelController.getPanel());
			_scrollPane = new JScrollPane(_panel);

			GUIUtils.forceScrollToBegin(_scrollPane);
		}
		return _scrollPane;
	}

	public String getTitle()
	{
		return s_stringMgr.getString("SQLPreferencesPanel.title");
	}

	public String getHint()
	{
		return s_stringMgr.getString("SQLPreferencesPanel.hint");
	}

}

