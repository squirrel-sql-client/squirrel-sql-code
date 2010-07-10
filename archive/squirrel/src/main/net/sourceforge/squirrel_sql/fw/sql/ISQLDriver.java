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
import net.sourceforge.squirrel_sql.fw.util.beanwrapper.StringWrapper;

public interface ISQLDriver extends IHasIdentifier, Comparable {
    /**
     * JavaBean property names for this class.
     */
    public interface IPropertyNames {
        String DRIVER_CLASS = "driverClassName";
        String ID = "identifier";
        String JARFILE_NAME = "jarFileName";
        String NAME = "name";
        String URL = "url";
        String USES_CLASSPATH = "usesClassPath";
        String PLUGIN_NAMES = "pluginNames";
    }

    /**
     * Assign data from the passed <CODE>ISQLDriver</CODE> to this one.
     *
     * @param   rhs     <CODE>ISQLDriver</CODE> to copy data from.
     *
     * @exception   ValidationException
     *                  Thrown if an error occurs assigning data from
     *                  <CODE>rhs</CODE>.
     */
    void assignFrom(ISQLDriver rhs) throws ValidationException;

    /**
     * Compare this <TT>ISQLDriver</TT> to another object. If the passed object
     * is a <TT>ISQLDriver</TT>, then the <TT>getName()</TT> functions of the two
     * <TT>ISQLDriver</TT> objects are used to compare them. Otherwise, it throws a
     * ClassCastException (as <TT>ISQLDriver</TT> objects are comparable only to
     * other <TT>ISQLDriver</TT> objects).
     */
    public int compareTo(Object rhs);

    public IIdentifier getIdentifier();

    public String getDriverClassName();

    public void setDriverClassName(String driverClassName) throws ValidationException;

    public boolean getUsesClassPath();

    public void setUsesClassPath(boolean data) throws ValidationException;

    public URL getJarFileURL();

    public String getJarFileName();

    public void setJarFileName(String value) throws ValidationException;

    public String getUrl();

    public void setUrl(String url) throws ValidationException;

    public String getName();

    public void setName(String name) throws ValidationException;

    public StringWrapper[] getPluginNames();
    public void setPluginNames(StringWrapper[] names) throws ValidationException;

    void addPropertyChangeListener(PropertyChangeListener listener);
    void removePropertyChangeListener(PropertyChangeListener listener);
}
