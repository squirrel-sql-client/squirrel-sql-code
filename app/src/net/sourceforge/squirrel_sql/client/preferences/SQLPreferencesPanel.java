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
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.*;

import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.gui.OutputLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
/**
 * This preferences panel allows maintenance of SQL preferences.
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLPreferencesPanel implements IGlobalPreferencesPanel
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SQLPreferencesPanel.class);

	/** Panel to be displayed in preferences dialog. */
	private SQLPrefsPanel _myPanel;
   private JScrollPane _myScrollPane;

   /** Application API. */
	private IApplication _app;
   private MainFrame _mainFrame;
   
   /**
	 * Default ctor.
    * @param mainFrame
    */
	public SQLPreferencesPanel(MainFrame mainFrame)
	{
		super();
      _mainFrame = mainFrame;
   }

	/**
	 * Initialize this panel. Called prior to it being displayed.
	 *
	 * @param	app	Application API.
	 *
	 * @throws	IllegalArgumentException
	 * 			if <TT>null</TT> <TT>IApplication</TT> passed.
	 */
	public void initialize(IApplication app)
	{
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}

		_app = app;

		getPanelComponent();
		_myPanel.loadData(_app, _app.getSquirrelPreferences());

      _myPanel._fileOpenInPreviousDir.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            updateFilePanel(_myPanel);
         }
      });

      _myPanel._fileOpenInSpecifiedDir.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            updateFilePanel(_myPanel);
         }
      });

      updateFilePanel(_myPanel);


      _myPanel._fileChooseDir.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onChooseDir(_myPanel);
         }
      });

	}

   public void uninitialize(IApplication app)
   {
      
   }


   public void onChooseDir(SQLPrefsPanel pnl)
   {
      JFileChooser chooser = new JFileChooser(pnl._fileSpecifiedDir.getText());
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      int returnVal = chooser.showOpenDialog(_mainFrame);
      if (returnVal == JFileChooser.APPROVE_OPTION)
      {
         pnl._fileSpecifiedDir.setText(chooser.getSelectedFile().getAbsolutePath());
      }
   }


   private void updateFilePanel(SQLPrefsPanel pnl)
   {
      pnl._fileChooseDir.setEnabled(pnl._fileOpenInSpecifiedDir.isSelected());
      pnl._fileSpecifiedDir.setEnabled(pnl._fileOpenInSpecifiedDir.isSelected());
   }

   public synchronized Component getPanelComponent()
	{
		if (_myPanel == null)
		{
			_myPanel = new SQLPrefsPanel();
			_myScrollPane = new JScrollPane(_myPanel);
		}
		return _myScrollPane;
	}

	public void applyChanges()
	{
		_myPanel.applyChanges(_app.getSquirrelPreferences());
	}

	public String getTitle()
	{
		return s_stringMgr.getString("SQLPreferencesPanel.title");
	}

	public String getHint()
	{
		return s_stringMgr.getString("SQLPreferencesPanel.hint");
	}

	private static final class SQLPrefsPanel extends JPanel
	{

		private IntegerField _loginTimeout = new IntegerField();
        private IntegerField _largeScriptStmtCount = new IntegerField();
//		private JCheckBox _debugJdbc = new JCheckBox(s_stringMgr.getString("SQLPreferencesPanel.jdbcdebug"));
		private JRadioButton _debugJdbcDont = new JRadioButton(s_stringMgr.getString("SQLPreferencesPanel.jdbcdebugdont"));
		private JRadioButton _debugJdbcStream = new JRadioButton(s_stringMgr.getString("SQLPreferencesPanel.jdbcdebugstream"));
		private JRadioButton _debugJdbcWriter = new JRadioButton(s_stringMgr.getString("SQLPreferencesPanel.jdbcdebugwriter"));
		private JLabel _jdbcDebugLogFileNameLbl = new OutputLabel(" ");
      private JRadioButton _fileOpenInPreviousDir = new JRadioButton(s_stringMgr.getString("SQLPreferencesPanel.fileOpenInPreviousDir"));
      private JRadioButton _fileOpenInSpecifiedDir = new JRadioButton(s_stringMgr.getString("SQLPreferencesPanel.fileOpenInSpecifiedDir"));;
      private JTextField _fileSpecifiedDir = new JTextField();
      private JButton _fileChooseDir = new JButton("...");

      SQLPrefsPanel()
		{
			super(new GridBagLayout());
			createUserInterface();
		}

		void loadData(IApplication app, SquirrelPreferences prefs)
		{
			final ApplicationFiles appFiles = new ApplicationFiles();
			_loginTimeout.setInt(prefs.getLoginTimeout());
            _largeScriptStmtCount.setInt(prefs.getLargeScriptStmtCount());
			_debugJdbcStream.setSelected(prefs.isJdbcDebugToStream());
			_debugJdbcWriter.setSelected(prefs.isJdbcDebugToWriter());
			_debugJdbcDont.setSelected(prefs.isJdbcDebugDontDebug());
			_jdbcDebugLogFileNameLbl.setText(appFiles.getJDBCDebugLogFile().getPath());
         _fileOpenInPreviousDir.setSelected(prefs.isFileOpenInPreviousDir());
         _fileOpenInSpecifiedDir.setSelected(prefs.isFileOpenInSpecifiedDir());
         _fileSpecifiedDir.setText(prefs.getFileSpecifiedDir());
		}

		void applyChanges(SquirrelPreferences prefs)
		{
			prefs.setLoginTimeout(_loginTimeout.getInt());
            prefs.setLargeScriptStmtCount(_largeScriptStmtCount.getInt());
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

         prefs.setFileOpenInPreviousDir(_fileOpenInPreviousDir.isSelected());
         prefs.setFileOpenInSpecifiedDir(_fileOpenInSpecifiedDir.isSelected());
         String specDir = _fileSpecifiedDir.getText();
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
			add(createDebugPanel(), gbc);
         ++gbc.gridy;
         add(createFilePanel(), gbc);
		}

      private JPanel createGeneralPanel()
		{
			JPanel pnl = new JPanel(new GridBagLayout());
			pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("SQLPreferencesPanel.general")));

			_loginTimeout.setColumns(4);

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
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
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.NONE;
            gbc.insets = new Insets(4, 4, 4, 4);

            gbc.gridx = 0;
            gbc.gridy = 1;
            // i18n[SQLPreferencesPanel.largeScriptStmtCount=Large Script Statement Count: ]
            pnl.add(new JLabel(s_stringMgr.getString("SQLPreferencesPanel.largeScriptStmtCount")), gbc);
            
            ++gbc.gridx;
            pnl.add(_largeScriptStmtCount, gbc);
            
			return pnl;
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
         btnGroup.add(_fileOpenInPreviousDir);
         btnGroup.add(_fileOpenInSpecifiedDir);

         JPanel pnl = new JPanel(new GridBagLayout());
         pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("SQLPreferencesPanel.file")));

         GridBagConstraints gbc;

         gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
         pnl.add(_fileOpenInPreviousDir, gbc);

         gbc = new GridBagConstraints(1,0,2,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
         pnl.add(new JPanel(), gbc);


         gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0);
         pnl.add(_fileOpenInSpecifiedDir, gbc);

         gbc = new GridBagConstraints(1,1,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,5,5,5),0,0);
         pnl.add(_fileSpecifiedDir, gbc);

         gbc = new GridBagConstraints(2,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0);
         pnl.add(_fileChooseDir, gbc);

         return pnl;

      }


	}
}

