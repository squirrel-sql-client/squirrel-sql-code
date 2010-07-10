package net.sourceforge.squirrel_sql.client.mainframe;
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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JComboBox;

import net.sourceforge.squirrel_sql.fw.gui.SortedComboBoxModel;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.ObjectCacheChangeEvent;
import net.sourceforge.squirrel_sql.fw.util.ObjectCacheChangeListener;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.db.DataCache;
import net.sourceforge.squirrel_sql.client.mainframe.action.CascadeAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ExitAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.GlobalPreferencesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.MaximizeAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasCommand;
import net.sourceforge.squirrel_sql.client.mainframe.action.TileAction;

/**
 * Toolbar for <CODE>MainFrame</CODE>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class MainFrameToolBar extends ToolBar {
    private IApplication _app;

    /**
     * ctor specifying the <TT>ActionCollection</TT> object that stores the
     * actions for the application.
     *
     * @throws  IllegalArgumentException
     *              <TT>null</TT> <TT>IApplication</TT> or <TT>MainFrame</TT>
     *              passed.
     */
    MainFrameToolBar(IApplication app, MainFrame frame)
            throws IllegalArgumentException {
        super();
        if (app == null) {
            throw new IllegalArgumentException("null IApplication passed.");
        }
        if (frame == null) {
            throw new IllegalArgumentException("null MainFrame passed.");
        }
        _app = app;
        setUseRolloverButtons(true);
        setFloatable(false);

        ActionCollection actions = _app.getActionCollection();
//      add(actions.get(OpenAliasAction.class));
//      addSeparator();
        add(new AliasesDropDown(frame));
        addSeparator();
        add(actions.get(GlobalPreferencesAction.class));
        addSeparator();
        add(actions.get(TileAction.class));
        add(actions.get(CascadeAction.class));
        add(actions.get(MaximizeAction.class));
        addSeparator();
        add(actions.get(ExitAction.class));
    }

    /**
     * Dropdown holding all the current <TT>ISQLAlias</TT> objects. When one is
     * selected the user will be prompted to connect to it.
     */
    private class AliasesDropDown extends JComboBox implements ActionListener {
        private MainFrame _mainFrame;

        AliasesDropDown(MainFrame mainFrame) {
            super();
            _mainFrame = mainFrame;
            setMaximumSize(new Dimension(150, 150));
            setModel(new AliasesDropDownModel());
            addItem(" Connect To...");
            setSelectedIndex(0);
            addActionListener(this);
        }

        /**
         * An alias has been selected in the list so attempt to connect to it.
         *
         * @param   evt     Describes the event that has just occured.
         */
        public void actionPerformed(ActionEvent evt) {
            try {
                Object obj = getSelectedItem();
                if (obj instanceof ISQLAlias) {
                    new ConnectToAliasCommand(_app, _mainFrame, (ISQLAlias)obj).execute();
                }
            } finally {
                setSelectedIndex(0);
            }
        }
    }

    /**
     * Data model for AliasesDropDown.
     */
    private class AliasesDropDownModel extends SortedComboBoxModel {
        /**
         * Default ctor. Listen to the <TT>DataCache</TT> object for additions
         * and removals of aliases from the cache.
         */
        public AliasesDropDownModel() {
            super();
            load();
            _app.getDataCache().addAliasesListener(new MyAliasesListener(this));
        }

        /**
         * Load from <TT>DataCache</TT>.
         */
        private void load() {
            Iterator it = _app.getDataCache().aliases();
            while (it.hasNext()) {
                addAlias((ISQLAlias)it.next());
            }
        }

        /**
         * Add an <TT>ISQLAlias</TT> to this model.
         *
         * @param   alias   <TT>ISQLAlias</TT> to be added.
         */
        private void addAlias(ISQLAlias alias) {
            addElement(alias);
        }

        /**
         * Remove an <TT>ISQLAlias</TT> from this model.
         *
         * @param   alias   <TT>ISQLAlias</TT> to be removed.
         */
        private void removeAlias(ISQLAlias alias) {
            removeElement(alias);
        }
    }

    /**
     * Listener to changes in <TT>ObjectCache</TT>. As aliases are
     * added to/removed from <TT>DataCache</TT> this model is updated.
     */
    private static class MyAliasesListener implements ObjectCacheChangeListener {
        /** Model that is listening. */
        private AliasesDropDownModel _model;

        /**
         * Ctor specifying the model that is listening.
         */
        MyAliasesListener(AliasesDropDownModel model) {
            super();
            _model = model;
        }

        /**
         * An alias has been added to the cache.
         *
         * @param   evt     Describes the event in the cache.
         */
        public void objectAdded(ObjectCacheChangeEvent evt) {
            Object obj = evt.getObject();
            if (obj instanceof ISQLAlias) {
                _model.addAlias((ISQLAlias)obj);
            }
        }

        /**
         * An alias has been removed from the cache.
         *
         * @param   evt     Describes the event in the cache.
         */
        public void objectRemoved(ObjectCacheChangeEvent evt) {
            Object obj = evt.getObject();
            if (obj instanceof ISQLAlias) {
                _model.removeAlias((ISQLAlias)obj);
            }
        }
    }
}
