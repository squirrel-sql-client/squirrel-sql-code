package net.sourceforge.squirrel_sql.client.action;
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
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JInternalFrame;

import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.mainframe.action.*;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionSheet;
import net.sourceforge.squirrel_sql.client.session.action.*;
import net.sourceforge.squirrel_sql.fw.util.Logger;

/**
 * This class represents a collection of <TT>Action</CODE> objects for the
 * application.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public final class ActionCollection {
    /**
     * Classes of actions that are only valid if an internal frame is open.
     */
    private interface IActionClassNames {
        Class CASCADE = CascadeAction.class;
        Class MAXIMIZE = MaximizeAction.class;
        Class TILE = TileAction.class;
    }

    /** Application API. */
    private IApplication _app;

    /** Collection of all Actions keyed by class name. */
    private Map _actionColl = new HashMap();

    /** Current <CODE>ISQLAlias</CODE>. */
    private ISQLAlias _alias;

    /** Current <CODE>ISQLDriver</CODE>. */
    private ISQLDriver _driver;

    /**
     * Ctor. Disable all actions that are not valid when the
     * application is first initialised.
     */
    public ActionCollection(IApplication app) throws IllegalArgumentException {
        super();
        if (app == null) {
            throw new IllegalArgumentException("Null IApplication passed");
        }
        _app = app;
        loadActions();
        enableInternalFrameOptions(false);
    }

    /**
     * Add an <TT>Action</TT> to this collection. Normally <TT>get</TT> will
     * do this &quot;on demand&quot; but this function can be used when
     * there is no default ctor for the <TT>Action</TT>.
     *
     * @param   action      <TT>Action</TT> to be added.
     *
     * @throws  IllegalArgumentException
     *              If a <TT>null</TT> <TT>Action</TT> passed.
     */
    public void add(Action action) throws IllegalArgumentException {
        _actionColl.put(action.getClass(), action);
    }

    /**
     * Returns the instance of the passed <TT>Action</TT> class that is stored
     * in this collection. If one isn't in this collection then an instance
     * of <TT>actionClass</TT> will be created, stored and then returned.
     *
     * @param   actionClass     The <TT>Class</TT> of the <TT>Action</TT>
     *                          required. Because the instance is created
     *                          using <TT>newInstance()</TT> this <TT>Class</TT>
     *                          must have a default ctor.
     *
     * @throws  IllegalArgumentException    Thrown if a null action class passed.
     */
    public synchronized Action get(Class actionClass) throws IllegalArgumentException {
        if (actionClass == null) {
            throw new IllegalArgumentException("null Action Class passed.");
        }

        Action action = (Action)_actionColl.get(actionClass);
        if (action == null) {
            Logger log = _app.getLogger();
            log.showMessage(Logger.ILogTypes.ERROR, "Action " + actionClass.getName()
                        + " not found in ActionCollection. Will attempt to create it"); //i18n
            action = createAction(actionClass);
        }
        return action;
    }

    /**
     * Emable/Disable the instance of the passed <TT>Action</TT> class that is
     * stored in this collection. If one isn't in this collection then an instance
     * of <TT>actionClass</TT> will be created and stored.
     *
     * @param   actionClass     The <TT>Class</TT> of the <TT>Action</TT>
     *                          to eb enabled/disabled. Because the instance
     *                          is created using <TT>newInstance()</TT> this
     *                          <TT>Class</TT> must have a default ctor.
     * @param   enable          If <TT>true</TT> then enable else disable
     *                          the action.
     *
     * @throws  IllegalArgumentException    Thrown if a null action class passed.
     */
    public void enableAction(Class actionClass, boolean enable) throws IllegalArgumentException {
        if (actionClass == null) {
            throw new IllegalArgumentException("null Action Class passed.");
        }

        final Action action = get(actionClass);
        if (action != null) {
            action.setEnabled(enable);
        }
    }

    /**
     * This function should be called whenever an internal frame is
     * opened or closed. It enables/disabled actions that are only
     * applicable to an internal frame.
     *
     * @param   nbrInternalFramesOpen   The count of the internal frames open.
     */
    public void internalFrameOpenedOrClosed(int nbrInternalFramesOpen) {
        enableInternalFrameOptions(nbrInternalFramesOpen > 0);
    }

    /**
     * This function should be called whenever an internal frame is
     * deactivated.
     *
     * @param   frame   The <TT>JInternalFrame</TT> deactivated.
     */
    public void internalFrameDeactivated(JInternalFrame frame) {
        internalFrameActivated(null);
    }

    /**
     * This function should be called whenever an internal frame is
     * activated.
     *
     * @param   frame   The <TT>JInternalFrame</TT> activated.
     */
    public synchronized void internalFrameActivated(JInternalFrame frame) {
        ISession session = null;
        if (frame instanceof SessionSheet) {
            session = ((SessionSheet)frame).getSession();
        }
        for (Iterator it = actions(); it.hasNext();) {
            final Action act = (Action)it.next();
            if (act instanceof ISessionAction) {
                ((ISessionAction)act).setSession(session);
            }
        }
    }

    /**
     * Return an <TT>Iterator</TT> over this collection.
     */
    private Iterator actions() {
        return _actionColl.values().iterator();
    }

    /**
     * Create a new instance of <TT>actionCass</TT> and store in this
     * collection.
     *
     * @param   actionClass     The <TT>Class</TT> of the <TT>Action</TT>
     *                          required. Because the instance is created
     *                          using <TT>newInstance()</TT> this <TT>Class</TT>
     *                          must have a default ctor.
     */
    private Action createAction(Class actionClass) {
        Action action = null;
        try {
            action = (Action)actionClass.newInstance();
            _actionColl.put(actionClass, action);
        } catch (Exception ex) {
            Logger log = _app.getLogger();
            log.showMessage(Logger.ILogTypes.ERROR, "Error occured creating Action: " + actionClass.getName());
            log.showMessage(Logger.ILogTypes.ERROR,ex.toString());
        }
        return action;
    }

    /**
     * Enable/disable actions that are valid only if an internal frame
     * is open.
     */
    private void enableInternalFrameOptions(boolean enable) {
        enableAction(IActionClassNames.CASCADE, enable);
        enableAction(IActionClassNames.MAXIMIZE, enable);
        enableAction(IActionClassNames.TILE, enable);
    }

    /**
     * Load actions.
     */
    private void loadActions() {
        add(new AboutAction(_app));
        add(new CascadeAction(_app));
        add(new CommitAction(_app));
        add(new DisplayPluginSummaryAction(_app));
        add(new ExecuteSqlAction(_app));
        add(new ExitAction(_app));
        add(new GlobalPreferencesAction(_app));
        add(new MaximizeAction(_app));
        add(new RefreshTreeAction(_app));
        add(new RollbackAction(_app));
        add(new SessionPropertiesAction(_app));
        add(new TileAction(_app));
    }
}
