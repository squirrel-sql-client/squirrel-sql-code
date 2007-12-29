package net.sourceforge.squirrel_sql.plugins.refactoring.gui;
/*
 * Copyright (C) 2006 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;


/**
 * A dialog that can be used to get column(s) selected by the user
 */
public abstract class AbstractRefactoringDialog extends JDialog {

    private JTextField tableNameTextField = null;

    protected JButton executeButton = null;
    protected JButton editSQLButton = null;
    protected JButton showSQLButton = null;
    protected JButton cancelButton = null;

    /**
     * The constraint that was used to add the last component
     */
    protected GridBagConstraints c = null;

    protected final Dimension mediumField = new Dimension(126, 20);

    /**
     * the panel in which subclasses may add components
     */
    protected JPanel pane = null;

    protected final EmptyBorder emptyBorder = new EmptyBorder(new Insets(5, 5, 5, 5));

    /**
     * Internationalized strings for this class.
     */
    private static final StringManager s_stringMgr =
            StringManagerFactory.getStringManager(AbstractRefactoringDialog.class);


    protected interface i18n {
        //i18n[AbstractRefactoringDialog.cancelButtonLabel=Cancel]
        String CANCEL_BUTTON_LABEL =
                s_stringMgr.getString("AbstractRefactoringDialog.cancelButtonLabel");

        //i18n[AbstractRefactoringDialog.editButtonLabel=Edit SQL]
        String EDIT_BUTTON_LABEL =
                s_stringMgr.getString("AbstractRefactoringDialog.editButtonLabel");

        //i18n[AbstractRefactoringDialog.executeButtonLabel=Execute]
        String EXECUTE_BUTTON_LABEL =
                s_stringMgr.getString("AbstractRefactoringDialog.executeButtonLabel");

        //i18n[AbstractRefactoringDialog.showButtonLabel=Show SQL]
        String SHOWSQL_BUTTON_LABEL =
                s_stringMgr.getString("AbstractRefactoringDialog.showButtonLabel");

        //i18n[AbstractRefactoringDialog.tableNameLabel=Table Name: ]
        String TABLE_NAME_LABEL =
                s_stringMgr.getString("AbstractRefactoringDialog.tableNameLabel");

    }


    public AbstractRefactoringDialog() {
        defaultInit();
    }


    public void setTableName(String tableName) {
        tableNameTextField.setText(tableName);
    }


    public String getTableName() {
        return tableNameTextField.getText();
    }


    public void addShowSQLListener(ActionListener listener) {
        if (listener == null) throw new IllegalArgumentException("listener cannot be null");
        showSQLButton.addActionListener(listener);
    }


    public void addEditSQLListener(ActionListener listener) {
        if (listener == null) throw new IllegalArgumentException("listener cannot be null");
        editSQLButton.addActionListener(listener);
    }


    public void addExecuteListener(ActionListener listener) {
        if (listener == null) throw new IllegalArgumentException("listener cannot be null");
        executeButton.addActionListener(listener);
    }


    public void _setVisible(boolean visible) {
        super.setVisible(visible);
    }


    /**
     * Overridden to make the Execute button have focus when the dialog is
     * displayed.
     */
    public void setVisible(final boolean visible) {
        GUIUtils.processOnSwingEventThread(new Runnable() {
            public void run() {
                AbstractRefactoringDialog.this._setVisible(visible);
            }
        });
        GUIUtils.processOnSwingEventThread(new Runnable() {
            public void run() {
                executeButton.requestFocus();
            }
        });
    }


    protected GridBagConstraints getLabelConstraints(GridBagConstraints c) {
        c.gridx = 0;
        c.gridy++;
        c.anchor = GridBagConstraints.NORTHEAST;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.weighty = 0;
        return c;
    }


    protected GridBagConstraints getFieldConstraints(GridBagConstraints c) {
        c.gridx++;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        return c;
    }


    protected JLabel getBorderedLabel(String text, Border border) {
        JLabel result = new JLabel(text);
        result.setBorder(border);
        result.setPreferredSize(new Dimension(115, 20));
        result.setHorizontalAlignment(SwingConstants.RIGHT);
        return result;
    }


    /**
     * Creates the UI for this dialog.
     */
    protected void defaultInit() {
        super.setModal(true);
        setSize(425, 250);

        pane = new JPanel();
        pane.setLayout(new GridBagLayout());
        pane.setBorder(new EmptyBorder(10, 0, 0, 30));

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = -1;

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(pane, BorderLayout.CENTER);
        contentPane.add(getButtonPanel(), BorderLayout.SOUTH);

        KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "CloseAction");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "CloseAction");
        getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "CloseAction");
        getRootPane().getActionMap().put("CloseAction", new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                setVisible(false);
                dispose();
            }
        });
    }


    protected JPanel getButtonPanel() {
        JPanel result = new JPanel();
        executeButton = new JButton(i18n.EXECUTE_BUTTON_LABEL);
        result.add(executeButton);

        editSQLButton = new JButton(i18n.EDIT_BUTTON_LABEL);
        result.add(editSQLButton);
        showSQLButton = new JButton(i18n.SHOWSQL_BUTTON_LABEL);
        result.add(showSQLButton);
        cancelButton = new JButton(i18n.CANCEL_BUTTON_LABEL);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        result.add(cancelButton);
        return result;
    }


    protected void enable(JButton button) {
        if (button != null) {
            button.setEnabled(true);
        }
    }


    protected void disable(JButton button) {
        if (button != null) {
            button.setEnabled(false);
        }
    }


    protected void enableAllButtons(boolean enable) {
        if (enable) {
            enable(executeButton);
            enable(editSQLButton);
            enable(showSQLButton);

        } else {
            disable(executeButton);
            disable(editSQLButton);
            disable(showSQLButton);
        }
    }

}

/*

KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "CloseAction");
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "CloseAction");
		getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "CloseAction");
		getRootPane().getActionMap().put("CloseAction", closeAction);

		*/