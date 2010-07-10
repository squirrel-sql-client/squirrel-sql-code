package net.sourceforge.squirrel_sql.client.preferences;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.sql.DriverManager;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.fw.util.PropertyChangeReporter;
import net.sourceforge.squirrel_sql.fw.util.ProxySettings;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;

import net.sourceforge.squirrel_sql.client.action.ActionKeys;
import net.sourceforge.squirrel_sql.client.mainframe.MainFrameWindowState;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;

public class SquirrelPreferences implements Serializable
{
	public interface IPropertyNames
	{
		String ACTION_KEYS = "actionKeys";
		String SESSION_PROPERTIES = "sessionProperties";
		String LOGIN_TIMEOUT = "loginTimeout";
		String DEBUG_JDBC = "debugJdbc";
		String MAIN_FRAME_STATE = "mainFrameWindowState";
		String PLUGIN_OBJECTS = "pluginObjects";
		String PROXY = "proxyPerferences";
		String SCROLLABLE_TABBED_PANES = "useScrollableTabbedPanes";
		String SHOW_ALIASES_TOOL_BAR = "showAliasesToolBar";
		String SHOW_CONTENTS_WHEN_DRAGGING = "showContentsWhenDragging";
		String SHOW_DRIVERS_TOOL_BAR = "showDriversToolBar";
		String SHOW_MAIN_STATUS_BAR = "showMainStatusBar";
		String SHOW_MAIN_TOOL_BAR = "showMainToolBar";
		String SHOW_TOOLTIPS = "showToolTips";
	}

	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(SquirrelPreferences.class);

	/** Bounds of the main frame. */
	private MainFrameWindowState _mainFrameState = new MainFrameWindowState();

	/** Properties for new sessions. */
	private SessionProperties _sessionProps = new SessionProperties();

	/**
	 * Show contents of internal frames when dragging. <CODE>false</CODE> makes
	 * dragging faster.
	 */
	private boolean _showContentsWhenDragging = false;

	/** Debug JDBC */
	private boolean _debugJdbc = false;

	/** Login timeout (seconds). */
	private int _loginTimeout = 30;

	/** Show tooltips for controls. */
	private boolean _showToolTips = true;

	/** Use scrollable tabbed panes. JDK 1.4 and above only. */
	private boolean _useScrollableTabbedPanes = false;

	/** Show main statusbar. */
	private boolean _showMainStatusBar = true;

	/** Show main toolbar. */
	private boolean _showMainToolBar = true;

	/** Show toolbar in the drivers window. */
	private boolean _showDriversToolBar = true;

	/** Show toolbar in the aliases window. */
	private boolean _showAliasesToolBar = true;

	/** Accelerators and mnemonics for actions. */
	private ActionKeys[] _actionsKeys = new ActionKeys[0];

	/** Proxy settings. */
	private ProxySettings _proxySettings = new ProxySettings();

	/**
	 * Objects stored by plugins. Each element of this collection is a <TT>Map</TT>
	 * keyed by the plugin's internal name and containing all objects for that
	 * plugin.
	 */
	//private Map _allPluginObjects = new HashMap();

	/** Object to handle property change events. */
	private transient PropertyChangeReporter _propChgReporter;

	/**
	 * Default ctor.
	 */
	public SquirrelPreferences()
	{
		super();
		loadDefaults();
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		getPropertyChangeReporter().addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		getPropertyChangeReporter().removePropertyChangeListener(listener);
	}

	public SessionProperties getSessionProperties()
	{
		return _sessionProps;
	}

	public synchronized void setSessionProperties(SessionProperties data)
	{
		if (_sessionProps != data)
		{
			final SessionProperties oldValue = _sessionProps;
			_sessionProps = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.SESSION_PROPERTIES,
												oldValue, _sessionProps);
		}
	}

	public MainFrameWindowState getMainFrameWindowState()
	{
		return _mainFrameState;
	}

	public synchronized void setMainFrameWindowState(MainFrameWindowState data)
	{
		final MainFrameWindowState oldValue = _mainFrameState;
		_mainFrameState = data;
		getPropertyChangeReporter().firePropertyChange(IPropertyNames.MAIN_FRAME_STATE,
											oldValue, _mainFrameState);
	}

	public boolean getShowContentsWhenDragging()
	{
		return _showContentsWhenDragging;
	}

	public synchronized void setShowContentsWhenDragging(boolean data)
	{
		final boolean oldValue = _showContentsWhenDragging;
		_showContentsWhenDragging = data;
		getPropertyChangeReporter().firePropertyChange(IPropertyNames.SHOW_CONTENTS_WHEN_DRAGGING,
											oldValue, _showContentsWhenDragging);
	}

	public boolean getShowMainStatusBar()
	{
		return _showMainStatusBar;
	}

	public synchronized void setShowMainStatusBar(boolean data)
	{
		final boolean oldValue = _showMainStatusBar;
		_showMainStatusBar = data;
		getPropertyChangeReporter().firePropertyChange(IPropertyNames.SHOW_MAIN_STATUS_BAR,
											oldValue, _showMainStatusBar);
	}

	public boolean getShowMainToolBar()
	{
		return _showMainToolBar;
	}

	public synchronized void setShowMainToolBar(boolean data)
	{
		final boolean oldValue = _showMainToolBar;
		_showMainToolBar = data;
		getPropertyChangeReporter().firePropertyChange(IPropertyNames.SHOW_MAIN_TOOL_BAR,
											oldValue, _showMainToolBar);
	}

	public boolean getShowAliasesToolBar()
	{
		return _showAliasesToolBar;
	}

	public synchronized void setShowAliasesToolBar(boolean data)
	{
		final boolean oldValue = _showAliasesToolBar;
		_showAliasesToolBar = data;
		getPropertyChangeReporter().firePropertyChange(IPropertyNames.SHOW_ALIASES_TOOL_BAR,
											oldValue, _showAliasesToolBar);
	}

	public boolean getShowDriversToolBar()
	{
		return _showDriversToolBar;
	}

	public synchronized void setShowDriversToolBar(boolean data)
	{
		final boolean oldValue = _showDriversToolBar;
		_showDriversToolBar = data;
		getPropertyChangeReporter().firePropertyChange(IPropertyNames.SHOW_DRIVERS_TOOL_BAR,
											oldValue, _showDriversToolBar);
	}

	public int getLoginTimeout()
	{
		return _loginTimeout;
	}

	public synchronized void setLoginTimeout(int data)
	{
		final int oldValue = _loginTimeout;
		_loginTimeout = data;
		getPropertyChangeReporter().firePropertyChange(IPropertyNames.LOGIN_TIMEOUT,
											oldValue, _loginTimeout);
	}

	public boolean getDebugJdbc()
	{
		return _debugJdbc;
	}

	public synchronized void setDebugJdbc(boolean data)
	{
		final boolean oldValue = _debugJdbc;
		_debugJdbc = data;
		getPropertyChangeReporter().firePropertyChange(IPropertyNames.DEBUG_JDBC, oldValue, _debugJdbc);
	}

	public boolean getShowToolTips()
	{
		return _showToolTips;
	}

	public synchronized void setShowToolTips(boolean data)
	{
		final boolean oldValue = _showToolTips;
		_showToolTips = data;
		getPropertyChangeReporter().firePropertyChange(IPropertyNames.SHOW_TOOLTIPS,
												oldValue, _showToolTips);
	}

	public boolean useScrollableTabbedPanes()
	{
		return _useScrollableTabbedPanes;
	}

	public synchronized void setUseScrollableTabbedPanes(boolean data)
	{
		final boolean oldValue = _useScrollableTabbedPanes;
		_useScrollableTabbedPanes = data;
		getPropertyChangeReporter().firePropertyChange(IPropertyNames.SCROLLABLE_TABBED_PANES,
											oldValue, _useScrollableTabbedPanes);
	}

	public ActionKeys[] getActionKeys()
	{
		return _actionsKeys;
	}

	public ActionKeys getActionKeys(int idx)
	{
		return _actionsKeys[idx];
	}

	public synchronized void setActionKeys(ActionKeys[] data)
	{
		final ActionKeys[] oldValue = _actionsKeys;
		_actionsKeys = data != null ? data : new ActionKeys[0];
		getPropertyChangeReporter().firePropertyChange(IPropertyNames.ACTION_KEYS,
											oldValue, _actionsKeys);
	}

	public void setActionKeys(int idx, ActionKeys value) {
		final ActionKeys[] oldValue = _actionsKeys;
		_actionsKeys[idx] = value;
		getPropertyChangeReporter().firePropertyChange(IPropertyNames.ACTION_KEYS,
											oldValue, _actionsKeys);
	}

	/**
	 * Retrieve the proxy settings. Noet that this method returns a clone
	 * of the actual proxy settings used.
	 * 
	 * @return	<TT>ProxySettings</TT> object.
	 */
	public ProxySettings getProxySettings()
	{
		return (ProxySettings)_proxySettings.clone();
	}

	public synchronized void setProxySettings(ProxySettings data)
	{
		if (data == null)
		{
			data = new ProxySettings();
		}
		final ProxySettings oldValue = _proxySettings;
		_proxySettings= data;
		getPropertyChangeReporter().firePropertyChange(IPropertyNames.PROXY,
											oldValue, _proxySettings);
	}

	/*
	public synchronized PluginObjectWrapper[] getPluginObjects() {
		//	  return (Folder[])_subFolders.toArray(new Folder[_subFolders.size()]);
		int pluginCount = _allPluginObjects.size();
		PluginObjectWrapper[] wrappers = new PluginObjectWrapper[pluginCount];
		int wrappersIdx = 0;
		for (Iterator pluginIt = _allPluginObjects.keySet().iterator(); pluginIt.hasNext();) {
			String pluginInternalName = (String)pluginIt.next();
			PluginObjectWrapper pow = new PluginObjectWrapper();
			pow.setPluginInternalName(pluginInternalName);
			Map objsMap = (Map)_allPluginObjects.get(pluginInternalName);
			//Object[] objsAr = objsMap.values().toArray(new Object[objsMap.size()]);
			Set entrySet = objsMap.entrySet();
			StringWrapper[] keysAr = new StringWrapper[entrySet.size()];
			Object[] objsAr = new Object[entrySet.size()];
			int entriesIdx = 0;
			for (Iterator entryIt = entrySet.iterator(); entryIt.hasNext();) {
				Map.Entry entry = (Map.Entry)entryIt.next();
				keysAr[entriesIdx] = new StringWrapper((String)entry.getKey());
				objsAr[entriesIdx++] = entry.getValue();
			}
	
			pow.setKeys(keysAr);
			pow.setObjects(objsAr);
			wrappers[wrappersIdx++] = pow;
		}
	
	
		return wrappers;
	}
	*/

	/*
		public PluginObjectWrapper getPluginObjectByIndex(int idx) {
			return null;
		}
	*/

	/*
		public void setPluginObjects(PluginObjectWrapper[] parm) {
			_allPluginObjects = new HashMap();
			for (int i = 0; i < parm.length; ++i) {
				Map map = new HashMap();
				StringWrapper[] keys = parm[i].getKeys();
				Object[] objects = parm[i].getObjects();
				for (int j = 0; j < keys.length; ++j) {
					map.put(keys[j], objects[j]);
				}
				_allPluginObjects.put(parm[i].getPluginInternalName(), map);
			}
		}
	*/

	/*
		public void setPluginObjectByIndex(int idx, PluginObjectWrapper value) {
			//_objects[idx] = value;
		}
	*/

	/*
		public synchronized Object getPluginObject(IPlugin plugin, String key) {
			String pluginName = plugin.getInternalName();
			Map pluginValues = (Map)_allPluginObjects.get(pluginName);
			if (pluginValues == null) {
				pluginValues = new HashMap();
				_allPluginObjects.put(pluginName, pluginValues);
			}
			return pluginValues.get(key);
		}
	*/

	/*
		public synchronized Object putPluginObject(IPlugin plugin, String key, Object obj) {
			String pluginName = plugin.getInternalName();
			Map pluginValues = (Map)_allPluginObjects.get(pluginName);
			if (pluginValues == null) {
				pluginValues = new HashMap();
				_allPluginObjects.put(pluginName, pluginValues);
			}
			return pluginValues.put(key, obj);
		}
	*/

	/*
		public synchronized Object removePluginObject(IPlugin plugin, String key) {
			Object obj = getPluginObject(plugin, key);
			if (obj != null) {
				((Map)_allPluginObjects.get(plugin.getInternalName())).remove(obj);
			}
			return obj;
		}
	*/

	public static SquirrelPreferences load()
	{
		File prefsFile = new ApplicationFiles().getUserPreferencesFile();
		try
		{
			XMLBeanReader doc = new XMLBeanReader();
			doc.load(prefsFile);
			Iterator it = doc.iterator();
			if (it.hasNext())
			{
				return (SquirrelPreferences)it.next();

			}
		}
		catch (FileNotFoundException ignore)
		{
			// property file not found for user - first time user ran pgm.
		}
		catch (Exception ex)
		{
			s_log.error("Error occured reading from preferences file: " + prefsFile.getPath(), ex);
			//i18n
		}
		return new SquirrelPreferences();
	}

	/**
	 * Save preferences to disk.
	 */
	public synchronized void save()
	{
		File prefsFile = new ApplicationFiles().getUserPreferencesFile();
		try
		{
			XMLBeanWriter wtr = new XMLBeanWriter(this);
			wtr.save(prefsFile);
		}
		catch (Exception ex)
		{
			s_log.error("Error occured writing to preferences file: " + prefsFile.getPath(), ex);
			//i18n
		}
	}

	private void loadDefaults()
	{
		if (_loginTimeout == -1)
		{
			_loginTimeout = DriverManager.getLoginTimeout();
		}
	}

	private synchronized PropertyChangeReporter getPropertyChangeReporter()
	{
		if (_propChgReporter == null)
		{
			_propChgReporter = new PropertyChangeReporter(this); 
		}
		return _propChgReporter;
	}

	/*
		public static final class PluginObjectWrapper {
			private String _pluginInternalName;
			private StringWrapper[] _keys;
			private Object[] _objects;
	
			public String getPluginInternalName() {
				return _pluginInternalName;
			}
	
	
			public void setPluginInternalName(String value) {
				_pluginInternalName = value;
			}
	
	
			public Object[] getObjects() {
				return _objects;
			}
	
	
			public Object getObjects(int idx) {
				return _objects[idx];
			}
	
	
			public void setObjects(Object[] value) {
				_objects = value;
			}
	
	
			public void setObjects(int idx, Object value) {
				_objects[idx] = value;
			}
	
	
			public StringWrapper[] getKeys() {
				return _keys;
			}
	
	
			public StringWrapper getKeys(int idx) {
				return _keys[idx];
			}
	
	
			public void setKeys(StringWrapper[] value) {
				_keys = value;
			}
	
	
			public void setKeys(int idx, StringWrapper value) {
				_keys[idx] = value;
			}
		}
	
	
		public class PluginObjectWrapperBeanInfo extends SimpleBeanInfo {
			private static final String PLUGIN_INTERNAL_NAME = "pluginInternalName";
			private static final String KEYS = "keys";
			private static final String OBJECTS = "objects";
	
	
			private PropertyDescriptor[] s_dscrs;
	
			private Class cls = PluginObjectWrapper.class;
	
			public PluginObjectWrapperBeanInfo() throws IntrospectionException {
				super();
				if (s_dscrs == null) {
					s_dscrs = new PropertyDescriptor[3];
					int idx = 0;
					s_dscrs[idx++] = new PropertyDescriptor(PLUGIN_INTERNAL_NAME, cls, "getPluginInternalName", "setPluginInternalName");
					s_dscrs[idx++] = new IndexedPropertyDescriptor(KEYS, cls, "getKeys", "setKeys", "getKeys", "setKeys");
					s_dscrs[idx++] = new IndexedPropertyDescriptor(OBJECTS, cls, "getObjects", "setObjects", "getObjects", "setObjects");
				}
			}
	
			public PropertyDescriptor[] getPropertyDescriptors() {
				return s_dscrs;
			}
		}
	*/
}