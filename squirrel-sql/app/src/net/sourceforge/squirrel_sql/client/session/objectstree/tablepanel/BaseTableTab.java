package net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel;
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
import java.awt.Component;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTextPanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewerDestination;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.Debug;

import net.sourceforge.squirrel_sql.client.session.ISession;

/**
 * Base class for tabs to the added to <TT>TablePanel</TT>. If you are
 * writing a class for a tab to be added to <TT>TablePanel</TT> you don't
 * have to inherit from this class (only implement
 * <TT>net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel.ITablePanelTab</TT>)
 * but it has convenience methods.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public abstract class BaseTableTab implements ITablePanelTab {
    /** Current session. */
    private ISession _session;

    /** Defines the table that info is to be displayed for. */
    private ITableInfo _ti;

    /**
     * Set to <TT>true</TT> if the current <TT>ITableInfo</TT> object
     * has been displayed.
     */
    private boolean _hasBeenDisplayed;

    /**
     * Set the current session.
     *
     * @param    session        Current session.
     *
     * @throws    IllegalArgumentException
     *          Thrown if a <TT>null</TT> ISession</TT> passed.
     */
    public void setSession(ISession session) throws IllegalArgumentException {
        if (session == null) {
            throw new IllegalArgumentException("Null ISession passed");
        }
        _session = session;
    }

    /**
     * Retrieve the current session.
     *
     * @return    Current session.
     */
    public final ISession getSession() {
        return _session;
    }

    /**
     * Set the <TT>ITableInfo</TT> object that specifies the table that
     * is to have its information displayed.
     *
     * @param    value  <TT>ITableInfo</TT> object that specifies the currently
     *                  selected table. This can be <TT>null</TT>.
     */
    public void setTableInfo(ITableInfo value) {
        _ti = value;
        _hasBeenDisplayed = false;
    }

    /**
     * Retrieve the current <TT>ITableInfo</TT> object.
     *
     * @return    Current <TT>ITableInfo</TT> object.
     */
    public final ITableInfo getTableInfo() {
        return _ti;
    }

    /**
     * This tab has been selected. This will call <TT>refreshComponent()</TT>
     * only if it hasn't been called for the current MTT>ITableInfo</TT> object.
     *
     * @throws    IllegalStateException
     *          Thrown if a <TT>null</TT> <TT>ISession</TT> or
     *          <TT>ITableInfo</TT> object is stored here.
     */
    public synchronized void select() throws IllegalStateException {
        if (!_hasBeenDisplayed) {
            if (Debug.isDebugMode()) {
                Debug.println("Refreshing " + getTitle() + " table tab.");
            }
            refreshComponent();
            _hasBeenDisplayed = true;
        }
    }

    /**
     * Refresh the component displaying the <TT>ITableInfo</TT> object.
     */
    protected abstract void refreshComponent();

    /**
     * Create a viewer panel for an <T>IDataSet</TT>. If the passed class
     * name is invalid return a <TT>import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTextPanel</TT>.
     *
     * @param   destClassName   Class Name of panel to be created. This class
     *                          must have a default constructor.
     *
     * @return  The newly created panel.
     */
    protected IDataSetViewerDestination createDestination(String destClassName) {
        IDataSetViewerDestination dest = null;
        try {
            Class destClass = Class.forName(destClassName);
            if (IDataSetViewerDestination.class.isAssignableFrom(destClass) &&
                    Component.class.isAssignableFrom(destClass)) {
                dest = (IDataSetViewerDestination)destClass.newInstance();
            }

        } catch (Exception ignore) {
        }
        if (dest == null) {
            dest = new DataSetViewerTextPanel();
        }
        return dest;
    }

}

