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
import java.awt.*;

import javax.swing.*;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;

/**
 * This preferences panel allows maintenance of SQL preferences.
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLPreferencesController implements IGlobalPreferencesPanel
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SQLPreferencesController.class);

	/** Panel to be displayed in preferences dialog. */
	private SQLPreferencesPanel _myPanel;
   private JScrollPane _myScrollPane;

   private MainFrame _mainFrame;
   
	public SQLPreferencesController(MainFrame mainFrame)
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

		getPanelComponent();
		_myPanel.loadData(Main.getApplication().getSquirrelPreferences());

      _myPanel.fileOpenInPreviousDir.addActionListener(e -> updateFilePanel(_myPanel));

      _myPanel.fileOpenInSpecifiedDir.addActionListener(e -> updateFilePanel(_myPanel));

      updateFilePanel(_myPanel);


      _myPanel.fileChooseDir.addActionListener(e -> onChooseDir(_myPanel));

	}

   public void uninitialize(IApplication app)
   {
      
   }


   public void onChooseDir(SQLPreferencesPanel pnl)
   {
      JFileChooser chooser = new JFileChooser(pnl.fileSpecifiedDir.getText());
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      int returnVal = chooser.showOpenDialog(_mainFrame);
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
		if (_myPanel == null)
		{
			_myPanel = new SQLPreferencesPanel();
			_myScrollPane = new JScrollPane(_myPanel);

			SwingUtilities.invokeLater(() -> _myPanel.scrollRectToVisible(new Rectangle(0,0,1,1)));
		}
		return _myScrollPane;
	}

	public void applyChanges()
	{
		_myPanel.applyChanges(Main.getApplication().getSquirrelPreferences());
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

