package net.sourceforge.squirrel_sql.plugins.jedit;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.Action;

import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.SessionSheet;
import net.sourceforge.squirrel_sql.client.session.properties.ISessionPropertiesPanel;

import net.sourceforge.squirrel_sql.plugins.jedit.textarea.TextAreaDefaults;
import net.sourceforge.squirrel_sql.fw.util.log.*;

/**
 * The jEdit plugin class. This plugin replaces the standard SQL entry text area
 * with the jEdit edia area.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class JeditPlugin extends DefaultSessionPlugin {
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(JeditPlugin.class);

	/** Plugin preferences. */
	private JeditPreferences _prefs;

	/** Folder to store user settings in. */
	private File _userSettingsFolder;

	/** The initial factory for creating SQL entry panels. */
	private ISQLEntryPanelFactory _originalFactory;

	/** Factory that creates jEdit text controls. */
	private ISQLEntryPanelFactory _jeditFactory;

	/**
	 * Return the internal name of this plugin.
	 *
	 * @return  the internal name of this plugin.
	 */
	public String getInternalName() {
		return "jedit";
	}

	/**
	 * Return the descriptive name of this plugin.
	 *
	 * @return  the descriptive name of this plugin.
	 */
	public String getDescriptiveName() {
		return "jEdit Text Area Plugin";
	}

	/**
	 * Returns the current version of this plugin.
	 *
	 * @return  the current version of this plugin.
	 */
	public String getVersion() {
		return "0.1";
	}

	/**
	 * Returns the authors name.
	 *
	 * @return  the authors name.
	 */
	public String getAuthor() {
		return "Colin Bell";
	}

	/**
	 * Initialize this plugin.
	 */
	public synchronized void initialize() throws PluginException {
		super.initialize();

		// Folder to store user settings.
		try {
			_userSettingsFolder = getPluginUserSettingsFolder();
		} catch (IOException ex) {
			throw new PluginException(ex);
		}

		_originalFactory = getApplication().getSQLEntryPanelFactory();

		// Load plugin preferences.
		loadPrefs();
		TextAreaDefaults dfts = TextAreaDefaults.getDefaults();
		dfts.inputHandler = new JeditInputHandler();

		preferencesHaveChanged(null);
		
		_prefs.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				preferencesHaveChanged(evt.getPropertyName());
			}
		});
	}

	/**
	 * Application is shutting down so save preferences.
	 */
	public void unload() {
		savePrefs();
		super.unload();
	}

	/**
	 * Called when a session started. Save a copy of the plugin preferences in
	 * the new session.
	 *
	 * @param	session	The session that is starting.
	 *
	 * @return	<TT>true</TT> to indicate that this plugin is
	 *			applicable to passed session.
	 */
	public boolean sessionStarted(ISession session) {
		boolean rc = super.sessionStarted(session);
		if (rc) {
			try {
				JeditPreferences sessionPrefs = (JeditPreferences)_prefs.clone();
				session.putPluginObject(this, JeditConstants.ISessionKeys.PREFS, sessionPrefs);
				sessionPrefs.addPropertyChangeListener(new SessionPreferencesListener(this, session, sessionPrefs));
			} catch (CloneNotSupportedException ex) {
				throw new InternalError("CloneNotSupportedException for JeditPreferences");
			}
		}
		return rc;
	}

	/**
	 * Create preferences panel for the Global Preferences dialog.
	 *
	 * @return  preferences panel.
	 */
	public IGlobalPreferencesPanel[] getGlobalPreferencePanels() {
		return new IGlobalPreferencesPanel[] {
			new JeditPreferencesPanel(this, _prefs)
		};
	}

	/**
	 * Override this to create panels for the Session Properties dialog.
	 *
	 * @return  <TT>null</TT> to indicate that this plugin doesn't use session property panels.
	 */
	public ISessionPropertiesPanel[] getSessionPropertiesPanels(ISession session) {
		JeditPreferences sessionPrefs = (JeditPreferences)session.getPluginObject(this, JeditConstants.ISessionKeys.PREFS);
		return new ISessionPropertiesPanel[] {
			 new JeditPreferencesPanel(this, sessionPrefs)
		};
	}

	/**
	 * Install the factory to use to create SQL text panels.
	 */
	synchronized void installFactory() {
		if (_prefs.getUseJeditTextControl()) {
			getApplication().setSQLEntryPanelFactory(getJeditFactory());
		} else {
			getApplication().setSQLEntryPanelFactory(_originalFactory);
		}
	}

	synchronized ISQLEntryPanelFactory getJeditFactory() {
		if (_jeditFactory == null) {
			_jeditFactory = new JeditSQLEntryPanelFactory(this, _prefs);
		}
		return _jeditFactory;
	}

	ISQLEntryPanelFactory getOriginalFactory() {
		return _originalFactory;
	}

	private void preferencesHaveChanged(String propName) {
		if (propName == null || propName.equals(
				JeditPreferences.IPropertyNames.USE_JEDIT_CONTROL)) {
			installFactory();
		}
	}

	/**
	 * Load from preferences file.
	 */
	private void loadPrefs() {
		try {
			XMLBeanReader doc = new XMLBeanReader();
			doc.load(
				new File(_userSettingsFolder, JeditConstants.USER_PREFS_FILE_NAME),
				getClass().getClassLoader());
			Iterator it = doc.iterator();
			if (it.hasNext()) {
				_prefs = (JeditPreferences)it.next();
			}
		} catch (FileNotFoundException ignore) {
			// property file not found for user - first time user ran pgm.
		} catch (Exception ex) {
			s_log.error("Error occured reading from preferences file: "
							+ JeditConstants.USER_PREFS_FILE_NAME, ex);
		}
		if (_prefs == null) {
			_prefs = new JeditPreferences();
		}
	}

	/**
	 * Save preferences to disk.
	 */
	private void savePrefs() {
		try {
			XMLBeanWriter wtr = new XMLBeanWriter(_prefs);
			wtr.save(new File(_userSettingsFolder, JeditConstants.USER_PREFS_FILE_NAME));
		} catch (Exception ex) {
			s_log.error("Error occured writing to preferences file: "
					+ JeditConstants.USER_PREFS_FILE_NAME, ex);
		}
	}

	private static final class SessionPreferencesListener implements PropertyChangeListener {
		private JeditPlugin _plugin;
		private ISession _session;
		private JeditPreferences _prefs;
		private boolean _usingJeditControl;		
		SessionPreferencesListener(JeditPlugin plugin, ISession session, JeditPreferences prefs) {
			super();
			_plugin = plugin;
			_session = session;
			_prefs = prefs;
		}

		public void propertyChange(PropertyChangeEvent evt) {
			final String propName = evt.getPropertyName();

			if (propName == null || propName.equals(
					JeditPreferences.IPropertyNames.USE_JEDIT_CONTROL)) {
				ISQLEntryPanelFactory factory = null;
				if (_prefs.getUseJeditTextControl()) {
					factory = _plugin.getJeditFactory();
				} else {
					factory = _plugin.getOriginalFactory();
					_session.putPluginObject(_plugin, JeditConstants.ISessionKeys.JEDIT_SQL_ENTRY_CONTROL, null);
				}
				synchronized (_session) {
					SessionSheet sheet = _session.getSessionSheet();
					if (sheet != null) {
						sheet.replaceSQLEntryPanel(factory.createSQLEntryPanel(_session));
					}
				}
			}

			if (propName == null || propName.equals(
					JeditPreferences.IPropertyNames.KEYWORD1_COLOR)) {
				if (_prefs.getUseJeditTextControl()) {
					JeditSQLEntryPanel pnl = (JeditSQLEntryPanel)_session.getPluginObject(_plugin, JeditConstants.ISessionKeys.JEDIT_SQL_ENTRY_CONTROL);
					if (pnl != null) {
						pnl.updateFromPreferences(_prefs);
					}
				}
			}
		}
	}
}
