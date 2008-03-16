/*
 * Copyright (C) 2008 Dieter Engelhardt
 * dieter@ew6.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sourceforge.squirrel_sql.plugins.sqlreplace;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * @author Dieter
 *
 */
public class SQLReplacePreferencesController implements IGlobalPreferencesPanel {


	   /**
	    * The main panel for preference administration
	    */
	   protected SQLReplacementPreferencesPanel _pnlPrefs;

	   /**
	    * Handle to the main application
	    */
	   protected IApplication _app;

	   /**
	    * Handle to the plugin
	    */
	   protected SQLReplacePlugin _plugin;
	   
	/**
	 * @param _plugin
	 */
	public SQLReplacePreferencesController(SQLReplacePlugin _plugin) {
		super();
		this._plugin = _plugin;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel#initialize(net.sourceforge.squirrel_sql.client.IApplication)
	 */
	public void initialize(IApplication app) {
	      this._app = app;

	      _pnlPrefs.btnSave.addActionListener(new ActionListener()
	      {
	         public void actionPerformed(ActionEvent e)
	         {
	            onSave();
	         }
	      });
	      _pnlPrefs.replacementEditor.setText(_plugin.getReplacementManager().getContent()); 
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel#uninitialize(net.sourceforge.squirrel_sql.client.IApplication)
	 */
	public void uninitialize(IApplication app) {
	     
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#applyChanges()
	 */
	public void applyChanges() {
		if(_pnlPrefs.hasChanged())
		{
			onSave();
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#getHint()
	 */
	public String getHint() {
	      return _plugin.getResourceString("prefs.hint");
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#getPanelComponent()
    * Return the panel that will contain the prefernces ui.
    *
    * @return Panel containing preferences.
    */
  	public Component getPanelComponent() {

  		// this gets called before initialize()		
		_pnlPrefs = new SQLReplacementPreferencesPanel(_plugin);
		return _pnlPrefs;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#getTitle()
	 */
	public String getTitle() {
	      return _plugin.getResourceString("prefs.title");
	}

	private void onSave()
	{
		String content = _pnlPrefs.replacementEditor.getText();
		ReplacementManager repman = _plugin.getReplacementManager();
		repman.setContentFromEditor(content);
	}
	 
}
