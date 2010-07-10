package net.sourceforge.squirrel_sql.fw.gui;
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
import java.awt.BorderLayout;
import java.awt.Container;
import java.util.HashMap;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import net.sourceforge.squirrel_sql.fw.gui.action.SelectInternalFrameAction;

public class BaseMDIParentFrame extends JFrame {

    private JDesktopPane _desktop;
    private IInternalFramePositioner _internalFramePositioner;
    private HashMap _children = new HashMap();

    private MyInternalFrameListener _childListener = new MyInternalFrameListener();


    protected BaseMDIParentFrame(JDesktopPane desktop) {
        this(null, desktop);
    }

    protected BaseMDIParentFrame(String title, JDesktopPane desktop) {
        super(title);
        if (desktop == null) {
            throw new IllegalArgumentException("null JDesktopPane passed");
        }
        _desktop = desktop;
        //setContentPane(desktop);
    }

    public void addInternalFrame(JInternalFrame child) {
        child.setTitle(createTitleForChild(child));
        _desktop.add(child);
        if (!GUIUtils.isToolWindow(child)) {
            positionNewInternalFrame(child);
            final JMenu menu = getWindowsMenu();
            JMenuItem menuItem = null;
            if (menu != null) {
                menuItem = menu.add(new SelectInternalFrameAction(child));
            }
            _children.put(child.getTitle(), new ChildInfo(child, menuItem));
            child.addInternalFrameListener(_childListener);
        }
    }

    public void internalFrameClosed(JInternalFrame child) {
        if (!GUIUtils.isToolWindow(child)) {
            child.removeInternalFrameListener(_childListener);
            ChildInfo ci = (ChildInfo)_children.remove(child.getTitle());
            if (ci != null && ci._menuItem != null) {
                final JMenu menu = getWindowsMenu();
                if (menu != null) {
                    menu.remove(ci._menuItem);
                }
            }
        }
    }

    public JDesktopPane getDesktopPane() {
        return _desktop;
    }

    public JMenu getWindowsMenu() {
        return null;
    }

    protected void positionNewInternalFrame(JInternalFrame child) {
        getInternalFramePositioner().positionInternalFrame(child);
    }

    protected IInternalFramePositioner getInternalFramePositioner() {
        if (_internalFramePositioner == null) {
             _internalFramePositioner = new CascadeInternalFramePositioner();
        }
        return _internalFramePositioner;
    }

    private String createTitleForChild(JInternalFrame child) {
        String title = child.getTitle();
        String origTitle = title;
        int index = 0;
        while (_children.get(title) != null) {
            title = origTitle + "(" + ++index + ")";
        }
        return title;
    }

    private class MyInternalFrameListener extends InternalFrameAdapter {
        public void internalFrameClosed(InternalFrameEvent evt) {
            BaseMDIParentFrame.this.internalFrameClosed((JInternalFrame)evt.getSource());
        }
    }

    private static class ChildInfo {
        private JInternalFrame _child;
        private JMenuItem _menuItem;

        ChildInfo(JInternalFrame child, JMenuItem menuItem) {
            super();
            _child = child;
            _menuItem = menuItem;
        }
    }
}