package net.sourceforge.squirrel_sql.client.preferences;
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
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.sql.DriverManager;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sourceforge.squirrel_sql.fw.util.Logger;
import net.sourceforge.squirrel_sql.fw.util.PropertyChangeReporter;
import net.sourceforge.squirrel_sql.fw.util.beanwrapper.StringWrapper;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.mainframe.MainFrameWindowState;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;

public class SquirrelPreferences implements Serializable {

    public interface IPropertyNames {
        String SESSION_PROPERTIES = "sessionProperties";
        String LOGIN_TIMEOUT = "loginTimeout";
        String DEBUG_JDBC = "debugJdbc";
        String MAIN_FRAME_STATE = "mainFrameWindowState";
        String SHOW_CONTENTS_WHEN_DRAGGING = "showContentsWhenDragging";
        String SHOW_TOOLTIPS = "showToolTips";
        String DEBUG_MODE = "debugMode";
        String PLUGIN_OBJECTS = "pluginObjects";
    }

    /** Application API. */
    private transient IApplication _app;

    /** Bounds of the main frame. */
    private MainFrameWindowState _mainFrameState = new MainFrameWindowState();

    private boolean _debugMode = false;

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

    /**
     * Objects stored by plugins. Each element of this collection is a <TT>Map</TT>
     * keyed by the plugin's internal name and containing all objects for that
     * plugin.
     */
    //private Map _allPluginObjects = new HashMap();

    /** Object to handle property change events. */
    private PropertyChangeReporter _propChgReporter = new PropertyChangeReporter(this);

    /**
     * Default ctor. This is a JavaBean and must have a default ctor.
     */
    public SquirrelPreferences() {
        super();
    }

    /**
     * Assign contents of the passed prefernces object to this one.
     */
    public void assignFrom(SquirrelPreferences rhs) {
        try {
            setApplication(rhs.getApplication());
        } catch (IllegalArgumentException ignore) {
            // When loading from prefs file this will be null.
        }
        setDebugMode(rhs.isDebugMode());
        setSessionProperties(rhs.getSessionProperties());
        setMainFrameWindowState(rhs.getMainFrameWindowState());
        setShowContentsWhenDragging(rhs.getShowContentsWhenDragging());
        setLoginTimeout(rhs.getLoginTimeout());
        setDebugJdbc(rhs.getDebugJdbc());
        setShowToolTips(rhs.getShowToolTips());
//      setPluginObjects(rhs.getPluginObjects());
    }

    /**
     * Set Application API to use.
     *
     * @param   app     Application API.
     *
     * @throws  IllegalArgumentException
     *              Thrown if <TT>null</TT> <TT>IApplication</TT> passed.
     */
    public void setApplication(IApplication app) throws IllegalArgumentException {
        if (app == null) {
            throw new IllegalArgumentException("Null IApplication passed");
        }
        _app = app;
    }

    public IApplication getApplication() {
        return _app;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        _propChgReporter.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        _propChgReporter.removePropertyChangeListener(listener);
    }

    public boolean isDebugMode() {
        return _debugMode;
    }

    public synchronized void setDebugMode(boolean data) {
        if (_debugMode != data) {
            final boolean oldValue = _debugMode;
            _debugMode = data;
            _propChgReporter.firePropertyChange(IPropertyNames.DEBUG_MODE,
                                oldValue, _debugMode);
        }
    }

    public SessionProperties getSessionProperties() {
        return _sessionProps;
    }

    public synchronized void setSessionProperties(SessionProperties data) {
        if (_sessionProps != data) {
            final SessionProperties oldValue = _sessionProps;
            _sessionProps = data;
            _propChgReporter.firePropertyChange(IPropertyNames.SESSION_PROPERTIES,
                                oldValue, _sessionProps);
        }
    }

    public MainFrameWindowState getMainFrameWindowState() {
        return _mainFrameState;
    }

    public synchronized void setMainFrameWindowState(MainFrameWindowState data) {
        final MainFrameWindowState oldValue = _mainFrameState;
        _mainFrameState = data;
        _propChgReporter.firePropertyChange(IPropertyNames.MAIN_FRAME_STATE,
                                oldValue, _mainFrameState);
    }

    public boolean getShowContentsWhenDragging() {
        return _showContentsWhenDragging;
    }

    public synchronized void setShowContentsWhenDragging(boolean data) {
        final boolean oldValue = _showContentsWhenDragging;
        _showContentsWhenDragging = data;
        _propChgReporter.firePropertyChange(IPropertyNames.SHOW_CONTENTS_WHEN_DRAGGING,
                                oldValue, _showContentsWhenDragging);
    }

    public int getLoginTimeout() {
        return _loginTimeout;
    }

    public synchronized void setLoginTimeout(int data) {
        final int oldValue = _loginTimeout;
        _loginTimeout = data;
        _propChgReporter.firePropertyChange(IPropertyNames.LOGIN_TIMEOUT,
                                oldValue, _loginTimeout);
    }

    public boolean getDebugJdbc() {
        return _debugJdbc;
    }

    public synchronized void setDebugJdbc(boolean data) {
        final boolean oldValue = _debugJdbc;
        _debugJdbc = data;
        _propChgReporter.firePropertyChange(IPropertyNames.DEBUG_JDBC,
                                oldValue, _debugJdbc);
    }

    public boolean getShowToolTips() {
        return _showToolTips;
    }


    public synchronized void setShowToolTips(boolean data) {
        final boolean oldValue = _showToolTips;
        _showToolTips = data;
        _propChgReporter.firePropertyChange(IPropertyNames.SHOW_TOOLTIPS,
                                oldValue, _showToolTips);
    }

    /*
    public synchronized PluginObjectWrapper[] getPluginObjects() {
        //      return (Folder[])_subFolders.toArray(new Folder[_subFolders.size()]);
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

    public void load() {
        try {
            XMLBeanReader doc = new XMLBeanReader();
            doc.load(ApplicationFiles.USER_PREFS_FILE_NAME);
            Iterator it = doc.iterator();
            if (it.hasNext()) {
                assignFrom((SquirrelPreferences)it.next());
            }
        } catch(FileNotFoundException ignore) {
            // property file not found for user - first time user ran pgm.
        } catch(Exception ex) {
            Logger logger = _app.getLogger();
            logger.showMessage(Logger.ILogTypes.ERROR, "Error occured reading from preferences file: "
                                                + ApplicationFiles.USER_PREFS_FILE_NAME); //i18n
            logger.showMessage(Logger.ILogTypes.ERROR, ex);
        }
        loadDefaults();
    }

    /**
     * Save preferences to disk.
     */
    public synchronized void save() {
        try {
            XMLBeanWriter wtr = new XMLBeanWriter(this);
            wtr.save(ApplicationFiles.USER_PREFS_FILE_NAME);
        } catch (FileNotFoundException ex) {
            // ?? Report to user. could be readonly etc.
        } catch(Exception ex) {
            Logger logger = _app.getLogger();
            logger.showMessage(Logger.ILogTypes.ERROR, "Error occured writing to preferences file: "
                                                + ApplicationFiles.USER_PREFS_FILE_NAME); //i18n
            logger.showMessage(Logger.ILogTypes.ERROR, ex);
        }
    }

    private void loadDefaults() {
        if (_loginTimeout == -1) {
            _loginTimeout = DriverManager.getLoginTimeout();
        }
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
