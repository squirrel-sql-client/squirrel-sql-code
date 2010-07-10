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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

public class OkCancelPanel extends JPanel {

    private boolean _showOk = true;
    private boolean _showCancel = true;

    private EventListenerList _listenerList = new EventListenerList();

    /**
     * This interface defines locale specific strings. This should be
     * replaced with a property file.
     */
    private interface i18n {
        String OK = "OK";
        String CANCEL = "Cancel";
    }

    private JButton _okBtn = new JButton(i18n.OK);
    private JButton _cancelBtn = new JButton(i18n.CANCEL);

    public OkCancelPanel() {
        super();
        createUserInterface();
    }

    public void showOkButton(boolean show) {
        if (_showOk != show) {
            if (_showOk) {
                remove(_okBtn);
            } else {
                add(_okBtn);
            }
            _showOk = show;
        }
    }

    public void showCancelButton(boolean show) {
        if (_showCancel != show) {
            if (_showCancel) {
                remove(_cancelBtn);
            } else {
                add(_cancelBtn);
            }
            _showCancel = show;
        }
    }

    /**
     * Adds a listener for actions in this panel.
     *
     * @param   lis a OkCancelPanelListener that will be notified when
     *          actions are performed in this panel.
     */
    public void addListener(OkCancelPanelListener lis) {
        _listenerList.add(OkCancelPanelListener.class, lis);
    }

    public JButton getOkButton() {
        return _okBtn;
    }

    private void fireButtonPressed(JButton btn) {
        // Guaranteed to be non-null.
        Object[] listeners = _listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event.
        OkCancelPanelEvent evt = null;
        for (int i = listeners.length - 2; i >= 0; i-=2 ) {
            if (listeners[i] == OkCancelPanelListener.class) {
                // Lazily create the event:
                if (evt == null) {
                    evt = new OkCancelPanelEvent(this, btn);
                }
                if (btn == _okBtn) {
                    ((OkCancelPanelListener)listeners[i + 1]).okPressed(evt);
                } else {
                    ((OkCancelPanelListener)listeners[i + 1]).cancelPressed(evt);
                }
            }
        }
    }

    private void createUserInterface() {
        add(_okBtn);
        add(_cancelBtn);
        _okBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                fireButtonPressed(_okBtn);
            }
        });
        _cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                fireButtonPressed(_cancelBtn);
            }
        });

        net.sourceforge.squirrel_sql.fw.gui.GUIUtils.setJButtonSizesTheSame(new JButton[] {_okBtn, _cancelBtn});
    }
}
