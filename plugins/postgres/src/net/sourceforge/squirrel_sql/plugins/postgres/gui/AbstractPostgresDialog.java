package net.sourceforge.squirrel_sql.plugins.postgres.gui;
/*
* Copyright (C) 2007 Daniel Regli & Yannick Winiger
* http://sourceforge.net/projects/squirrel-sql
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

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * A simple dialog with a main and a button panel.
 * Subclasses can use the main panel (_panel) to add their specific component to the dialogs body.
 */
public abstract class AbstractPostgresDialog extends JDialog {
    /** The panel in which subclasses may add components */
    protected JPanel _panel;

    /** The constraint that was used to add the last component */
    protected GridBagConstraints _gbc;

    protected Dimension _mediumField = new Dimension(126, 20);
    protected Dimension _largeField = new Dimension(126, 60);
    protected EmptyBorder _emptyBorder = new EmptyBorder(new Insets(5, 5, 5, 5));

    /** The buttons of the button panel */
    protected JButton _executeButton;
    protected JButton _editButton;
    protected JButton _showButton;
    protected JButton _cancelButton;

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AbstractPostgresDialog.class);

    protected interface i18n {
        String EXECUTE_BUTTON_LABEL =
                AbstractPostgresDialog.s_stringMgr.getString("AbstractPostgresDialog.executeButtonLabel");
        String EDIT_BUTTON_LABEL =
                AbstractPostgresDialog.s_stringMgr.getString("AbstractPostgresDialog.editButtonLabel");
        String SHOW_BUTTON_LABEL =
                AbstractPostgresDialog.s_stringMgr.getString("AbstractPostgresDialog.showButtonLabel");
        String CANCEL_BUTTON_LABEL =
                AbstractPostgresDialog.s_stringMgr.getString("AbstractPostgresDialog.cancelButtonLabel");
    }


    /** Creates the UI for this dialog. */
    protected void defaultInit() {
        setModal(true);
        setSize(425, 250);

        _panel = new JPanel();
        _panel.setLayout(new GridBagLayout());
        _panel.setBorder(new EmptyBorder(10, 0, 0, 30));

        _gbc = new GridBagConstraints();
        _gbc.gridx = 0;
        _gbc.gridy = -1;

        Container contentPane = super.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(_panel, BorderLayout.CENTER);

        contentPane.add(getButtonPanel(), BorderLayout.SOUTH);
    }


    public void addExecuteListener(ActionListener listener) {
        if (listener == null) throw new IllegalArgumentException("ActionListener cannot be null");
        _executeButton.addActionListener(listener);
    }


    public void addEditSQLListener(ActionListener listener) {
        if (listener == null) throw new IllegalArgumentException("ActionListener cannot be null");
        _editButton.addActionListener(listener);
    }


    public void addShowSQLListener(ActionListener listener) {
        if (listener == null) throw new IllegalArgumentException("ActionListener cannot be null");
        _showButton.addActionListener(listener);
    }


    public void addCancelListener(ActionListener listener) {
        if (listener == null) throw new IllegalArgumentException("ActionListener cannot be null");
        _cancelButton.addActionListener(listener);
    }


    public void setVisible(final boolean visible) {
        GUIUtils.processOnSwingEventThread(new Runnable() {
            public void run() {
                AbstractPostgresDialog.super.setVisible(visible);
            }
        });
    }


    protected GridBagConstraints getLabelConstraints(GridBagConstraints c) {
        c.gridx = 0;
        c.gridy++;
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.weighty = 0;
        return c;
    }


    protected GridBagConstraints getFieldConstraints(GridBagConstraints c) {
        c.gridx++;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.weighty = 0;
        return c;
    }


    protected JLabel getBorderedLabel(String text, Border border) {
        JLabel result = new JLabel(text);
        result.setBorder(border);
        result.setPreferredSize(new Dimension(115, 20));
        result.setHorizontalAlignment(SwingConstants.RIGHT);
        return result;
    }


    protected JTextField getSizedTextField(Dimension mediumField) {
        JTextField result = new JTextField();
        result.setPreferredSize(mediumField);
        return result;
    }


    protected JPanel getButtonPanel() {
        JPanel result = new JPanel();

        _executeButton = new JButton(i18n.EXECUTE_BUTTON_LABEL);
        result.add(_executeButton);
        _editButton = new JButton(i18n.EDIT_BUTTON_LABEL);
        result.add(_editButton);
        _showButton = new JButton(i18n.SHOW_BUTTON_LABEL);
        result.add(_showButton);
        _cancelButton = new JButton(i18n.CANCEL_BUTTON_LABEL);
        addCancelListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        result.add(_cancelButton);

        return result;
    }


    protected void enable(JButton button) {
        if (button != null) button.setEnabled(true);
    }


    protected void disable(JButton button) {
        if (button != null) button.setEnabled(false);
    }
}
