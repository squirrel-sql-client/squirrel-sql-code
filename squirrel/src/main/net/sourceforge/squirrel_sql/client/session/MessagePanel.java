package net.sourceforge.squirrel_sql.client.session;
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
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

import net.sourceforge.squirrel_sql.fw.gui.TextPopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.fw.util.Debug;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.util.Logger;

public class MessagePanel extends JTextArea implements IMessageHandler {
    /** Application API. */
    private IApplication _app;

    /** Popup menu for this component. */
    private TextPopupMenu _popupMenu = new TextPopupMenu();

    public MessagePanel(IApplication app) throws IllegalArgumentException {
        super();
        if (app == null) {
            throw new IllegalArgumentException("Null IApplication passed");
        }

        _app = app;

        _popupMenu.setTextComponent(this);

        // Add mouse listener for displaying popup menu.
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                MessagePanel.this.mousePressed(evt);
            }
            public void mouseReleased(MouseEvent evt) {
                MessagePanel.this.mouseReleased(evt);
            }
        });
    }

    /**
     * Add the passed line to the end of the messages display. Position
     * display so the the newly added line will be displayed.
     *
     * @param   line    The line to be added. If it isn't terminated with
     *                  a newline one will be added.
     */
    public void addLine(String line) {
        append(line);
        if (!line.trim().endsWith("\n")) {
            append("\n");
        }
        int len = getDocument().getLength();
        select(len, len);
    }

    /**
     * Process a mouse press event in this list. If this event is a trigger
     * for a popup menu then display the popup menu.
     *
     * @param   evt     The mouse event being processed.
     */
    public void mousePressed(MouseEvent evt) {
        if (evt.isPopupTrigger()) {
            displayPopupMenu(evt);
        }
    }

    /**
     * Process a mouse released event in this list. If this event is a trigger
     * for a popup menu then display the popup menu.
     *
     * @param   evt     The mouse event being processed.
     */
    public void mouseReleased(MouseEvent evt) {
        if (evt.isPopupTrigger()) {
            displayPopupMenu(evt);
        }
    }

    /**
     * Display the popup menu for this component.
     */
    private void displayPopupMenu(MouseEvent evt) {
        _popupMenu.show(evt);
        //_popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
    }

    public void showMessage(Throwable th) throws IllegalArgumentException {
        if (th == null) {
            throw new IllegalArgumentException("null Throwable");
        }
        showMessage(th.toString());
        if (Debug.isDebugMode()) {
            _app.getLogger().showMessage(Logger.ILogTypes.ERROR, th);
        }
    }

    public void showMessage(String msg) throws IllegalArgumentException {
        if (msg == null) {
            throw new IllegalArgumentException("null Message");
        }
        addLine(msg);
    }
}
