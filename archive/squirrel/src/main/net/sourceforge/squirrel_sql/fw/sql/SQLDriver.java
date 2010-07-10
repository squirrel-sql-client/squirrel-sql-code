package net.sourceforge.squirrel_sql.fw.sql;
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
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.util.PropertyChangeReporter;
import net.sourceforge.squirrel_sql.fw.util.beanwrapper.StringWrapper;

public class SQLDriver implements ISQLDriver, Cloneable, Serializable {
    /**
     * This interface defines locale specific strings. This should be
     * replaced with a property file.
     */
    private interface i18n {
        String ERR_BLANK_NAME = "Name cannot be blank.";
        String ERR_BLANK_DRIVER = "JDBC Driver cannot be blank.";
        String ERR_BLANK_URL = "JDBC URL cannot be blank.";
    }

    /** The <CODE>IIdentifier</CODE> that uniquely identifies this object. */
    private IIdentifier _id;

    /** The name of this driver. */
    private String _name;

    /** <CODE>true</CODE> if driver is loaded from the CLASSPATH. */
    private boolean _usesClassPath = true;

    /**
     * Jar Fle to load driver from. Only used if _usesClassPath
     * is <CODE>false</CODE>.
     */
    private URL _jarFileURL = null;

    /**
     * File name associated with <CODE>_jarFileURL</CODE>.
     */
    private String _jarFileName = null;

    /** The class name of the JDBC driver. */
    private String _driverClassName;

    /** Default URL required to access the database. */
    private String _url;

    /** Array of names of plugins relevant to this driver. */
    private StringWrapper[] _pluginNames;

    /** Object to handle property change events. */
    private PropertyChangeReporter _propChgReporter = new PropertyChangeReporter(this);

    /**
     * Ctor specifying the identifier.
     *
     * @param   id  Uniquely identifies this object.
     */
    public SQLDriver(IIdentifier id) {
        super();
        _id = id;
        _name = "";
        _usesClassPath = true;
        _jarFileName = null;
        _jarFileURL = null;
        _driverClassName = null;
        _url = "";
    }

    public SQLDriver() {
        super();
    }

    /**
     * Ctor specifying attributes.
     */
/*  public SQLDriver(IIdentifier id, String name, boolean usesClasspath,
                    String jarFileName, String driverClassName, String url) {
        super();
        _id = id;
        _name = name;
        _usesClassPath = usesClasspath;
        _jarFileName = jarFileName;
        _driverClassName = driverClassName;
        _url = url;
        if (_jarFileName != null) {
            _jarFileURL = new File(name).toURL();
        }
    }
*/

    /**
     * Assign data from the passed <CODE>ISQLDriver</CODE> to this one.
     *
     * @param   rhs     <CODE>ISQLDriver</CODE> to copy data from.
     *
     * @exception   ValidationException
     *                  Thrown if an error occurs assigning data from
     *                  <CODE>rhs</CODE>.
     */
    public synchronized void assignFrom(ISQLDriver rhs)
            throws ValidationException {
        setName(rhs.getName());
        setUsesClassPath(rhs.getUsesClassPath());
        setJarFileURL(rhs.getJarFileURL());
        setJarFileName(rhs.getJarFileName());
        setDriverClassName(rhs.getDriverClassName());
        setUrl(rhs.getUrl());
    }

    /**
     * Returns <TT>true</TT> if this objects is equal to the passed one. Two
     * <TT>ISQLDriver</TT> objects are considered equal if they have the same
     * identifier.
     */
    public boolean equals(Object rhs) {
        boolean rc = false;
        if (rhs != null && rhs.getClass().equals(getClass())) {
            rc = ((ISQLDriver)rhs).getIdentifier().equals(getIdentifier());
        }
        return rc;
    }

    /**
     * Returns a hash code value for this object.
     */
    public synchronized int hashCode() {
        return getIdentifier().hashCode();
    }

    /**
     * Returns the name of this <TT>ISQLDriver</TT>.
     */
    public String toString() {
        return getName();
    }

    /**
     * Return a clone of this object.
     */
    public Object clone() {
        try {
            return super.clone();
        } catch(CloneNotSupportedException ex) {
            throw new InternalError(ex.getMessage());   // Impossible.
        }
    }

    /**
     * Compare this <TT>ISQLDriver</TT> to another object. If the passed object
     * is a <TT>ISQLDriver</TT>, then the <TT>getName()</TT> functions of the two
     * <TT>ISQLDriver</TT> objects are used to compare them. Otherwise, it throws a
     * ClassCastException (as <TT>ISQLDriver</TT> objects are comparable only to
     * other <TT>ISQLDriver</TT> objects).
     */
    public int compareTo(Object rhs) {
        return _name.compareTo(((ISQLDriver)rhs).getName());
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        _propChgReporter.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        _propChgReporter.removePropertyChangeListener(listener);
    }

    public void setReportPropertyChanges(boolean report) {
        _propChgReporter.setNotify(report);
    }

    public IIdentifier getIdentifier() {
        return _id;
    }

    public void setIdentifier(IIdentifier id) {
        _id = id;
    }

    public String getDriverClassName() {
        return _driverClassName;
    }

    public void setDriverClassName(String driverClassName)
            throws ValidationException {
        String data = getString(driverClassName);
        if (data.length() == 0) {
            throw new ValidationException(i18n.ERR_BLANK_DRIVER);
        }
        if (_driverClassName != data) {
            final String oldValue = _driverClassName;
            _driverClassName = data;
            _propChgReporter.firePropertyChange(ISQLDriver.IPropertyNames.DRIVER_CLASS, oldValue, _driverClassName);
        }
    }

    public boolean getUsesClassPath() {
        return _usesClassPath;
    }

    public void setUsesClassPath(boolean data)
            throws ValidationException {
        if (_usesClassPath != data) {
            final boolean oldValue = _usesClassPath;
            _usesClassPath = data;
            _propChgReporter.firePropertyChange(ISQLDriver.IPropertyNames.USES_CLASSPATH, oldValue, _usesClassPath);
        }
    }

    public URL getJarFileURL() {
        return _jarFileURL;
    }


    public String getJarFileName() {
        return _jarFileName;
    }

    public void setJarFileName(String value)
            throws ValidationException {
        if (value == null) {
            value = "";
        }
//      if ((_jarFileName == null && value != null) || !_jarFileName.equals(value)) {
        if (_jarFileName == null || !_jarFileName.equals(value)) {
            try {
                _jarFileURL = new File(value).toURL();
            } catch (MalformedURLException ex) {
                throw new ValidationException("Invalid file name"); //i18n
            }
            final String oldValue = _jarFileName;
            _jarFileName = value;
            _propChgReporter.firePropertyChange(ISQLDriver.IPropertyNames.JARFILE_NAME, oldValue, _jarFileName);
        }
    }

    public String getUrl() {
        return _url;
    }
    public void setUrl(String url)
            throws ValidationException {
        String data = getString(url);
        if (data.length() == 0) {
            throw new ValidationException(i18n.ERR_BLANK_URL);
        }
        if (_url != data) {
            final String oldValue = _url;
            _url = data;
            _propChgReporter.firePropertyChange(ISQLDriver.IPropertyNames.URL, oldValue, _url);
        }
    }

    public String getName() {
        return _name;
    }

    public void setName(String name)
            throws ValidationException {
        String data = getString(name);
        if (data.length() == 0) {
            throw new ValidationException(i18n.ERR_BLANK_NAME);
        }
        if (_name != data) {
            final String oldValue = _name;
            _name = data;
            _propChgReporter.firePropertyChange(ISQLDriver.IPropertyNames.NAME, oldValue, _name);
        }
    }

    public StringWrapper[] getPluginNames() {
        return _pluginNames;
    }

    public void setPluginNames(StringWrapper[] names) throws ValidationException {
        final StringWrapper[] oldValue = _pluginNames;
        _pluginNames = names;
        _propChgReporter.firePropertyChange(ISQLDriver.IPropertyNames.PLUGIN_NAMES, oldValue, _pluginNames);
    }

    private void setJarFileURL(URL value) {
        _jarFileURL = value;
    }

    private String getString(String data) {
        return data != null ? data.trim() : "";
    }

}
