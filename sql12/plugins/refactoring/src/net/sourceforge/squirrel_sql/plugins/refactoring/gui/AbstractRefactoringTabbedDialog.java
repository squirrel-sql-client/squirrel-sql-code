package net.sourceforge.squirrel_sql.plugins.refactoring.gui;
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

import net.sourceforge.squirrel_sql.client.gui.db.IDisposableDialog;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A tabbed dialog.
 */
public abstract class AbstractRefactoringTabbedDialog extends JDialog implements IDisposableDialog {

    protected JButton executeButton = null;
    protected JButton editSQLButton = null;
    protected JButton showSQLButton = null;
    protected JButton cancelButton = null;

    /**
     * The constraint that was used to add the last component.
     */
    protected GridBagConstraints c = null;

    protected final Dimension mediumField = new Dimension(126, 20);

    /**
     * the panel in which subclasses may add tabs.
     */
    protected final JTabbedPane pane = new JTabbedPane();

    protected final EmptyBorder emptyBorder = new EmptyBorder(new Insets(5, 5, 5, 5));

    /**
     * Internationalized strings for this class.
     */
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AbstractRefactoringTabbedDialog.class);


    public AbstractRefactoringTabbedDialog(Dimension size) {
        defaultInit(size);
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
     *
     * @param size dimension of the dialog
     */
    protected void defaultInit(Dimension size) {
        super.setModal(true);
        setSize(size);

        pane.setBorder(new EmptyBorder(10, 10, 0, 10));

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = -1;


        setLayout(new BorderLayout());
        add(pane, BorderLayout.CENTER);
        add(getButtonPanel(), BorderLayout.SOUTH);

        GUIUtils.enableCloseByEscape(this);
    }


    protected JPanel getButtonPanel() {
        JPanel result = new JPanel();
        executeButton = new JButton(s_stringMgr.getString("AbstractRefactoringDialog.executeButtonLabel"));
        result.add(executeButton);

        editSQLButton = new JButton(s_stringMgr.getString("AbstractRefactoringDialog.editButtonLabel"));
        result.add(editSQLButton);
        showSQLButton = new JButton(s_stringMgr.getString("AbstractRefactoringDialog.showButtonLabel"));
        result.add(showSQLButton);
        cancelButton = new JButton(s_stringMgr.getString("AbstractRefactoringDialog.cancelButtonLabel"));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
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


    protected void setAllButtonEnabled(boolean enable) {
        executeButton.setEnabled(enable);
        editSQLButton.setEnabled(enable);
        showSQLButton.setEnabled(enable);
    }

}
