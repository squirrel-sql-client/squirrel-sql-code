package net.sourceforge.squirrel_sql.plugins.sqlscript;
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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.MutableComboBoxModel;
import javax.swing.SwingConstants;

import net.sourceforge.squirrel_sql.fw.gui.LookAndFeelComboBox;
import net.sourceforge.squirrel_sql.fw.gui.PropertyPanel;
import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;

/**
 * The SQL Script preferences panel for the Global Preferences dialog.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLScriptPreferencesPanel implements IGlobalPreferencesPanel {
    /** Plugin preferences object. */
    private SQLScriptPreferences _prefs;

    /** Component to display in the Global preferences dialog. */
    private MyPanel _myPanel;

    /** Application API. */
    private IApplication _app;

    /**
     * Ctor.
     */
    public SQLScriptPreferencesPanel(SQLScriptPreferences prefs)
            throws IllegalArgumentException {
        super();
        if (prefs == null) {
            throw new IllegalArgumentException("Null SQLScriptPreferences");
        }
        _prefs = prefs;

        // Create the actual panel that will be displayed in dialog.
        _myPanel = new MyPanel(prefs);
    }

    /**
     * Load panel with data from plugin preferences.
     *
     * @param   app     Application API.
     *
     * @throws  IllegalArgumentException
     *          if <TT>IApplication</TT> is <TT>null</TT>.
     */
    public void initialize(IApplication app)
            throws IllegalArgumentException {
        if (app == null) {
            throw new IllegalArgumentException("Null IApplication passed");
        }

        _app = app;
        _myPanel.loadData();
    }

    /**
     * Return the component to be displayed in the Preferences dialog.
     *
     * @return  the component to be displayed in the Preferences dialog.
     */
    public Component getPanelComponent() {
        return _myPanel;
    }

    /**
     * User has pressed OK or Apply in the dialog so save data from
     * panel.
     */
    public void applyChanges() {
        _myPanel.applyChanges();
    }

    /**
     * Return the title for this panel.
     *
     * @return  the title for this panel.
     */
    public String getTitle() {
        return MyPanel.i18n.TAB_TITLE;
    }

    /**
     * Return the hint for this panel.
     *
     * @return  the hint for this panel.
     */
    public String getHint() {
        return MyPanel.i18n.TAB_HINT;
    }

    /**
     * Component to be displayed in the preferences dialog.
     */
    private static final class MyPanel extends JPanel {
        /**
         * This interface defines locale specific strings. This should be
         * replaced with a property file.
         */
        interface i18n {
            String OPEN_PREV = "Open in previous directory";
            String OPEN_SELECTED = "Open in specified directory";
            String TAB_TITLE = "SQL Script";
            String TAB_HINT = "SQL Script settings";
        }

        private JRadioButton _openInPrev = new JRadioButton(i18n.OPEN_PREV);
        private JRadioButton _openInSel = new JRadioButton(i18n.OPEN_SELECTED);
        private JTextField _selectedDirectory = new JTextField();
        private JButton _searchBtn = new JButton("...");
        private SQLScriptPreferences _prefs;

        MyPanel(SQLScriptPreferences prefs) {
            super();
            _prefs = prefs;
            createUserInterface();
        }

        void loadData() {
            _openInPrev.setSelected(_prefs.getOpenInPreviousDirectory());
            _openInSel.setSelected(_prefs.getOpenInSpecifiedDirectory());
            _selectedDirectory.setText(_prefs.getSpecifiedDirectory());
            _selectedDirectory.setEnabled(_openInSel.isSelected());
            _searchBtn.setEnabled(_openInSel.isSelected());
        }

        void applyChanges() {
            _prefs.setOpenInPreviousDirectory(_openInPrev.isSelected());
            _prefs.setOpenInSpecifiedDirectory(_openInSel.isSelected());
            _prefs.setSpecifiedDirectory(_selectedDirectory.getText());
        }

        private void createUserInterface() {
            setLayout(new BorderLayout());
            PropertyPanel pnl = new PropertyPanel();
            ButtonGroup grp = new ButtonGroup();
            grp.add(_openInPrev);
            grp.add(_openInSel);
            pnl.add(_openInPrev);

            pnl.add(_openInSel, _selectedDirectory, _searchBtn);
            _searchBtn.addActionListener(new MySearchButtonListener());

            _openInPrev.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    _selectedDirectory.setEnabled(_openInSel.isEnabled());
                    _searchBtn.setEnabled(_openInSel.isSelected());
                }
            });
            _openInSel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    _selectedDirectory.setEnabled(_openInSel.isEnabled());
                    _searchBtn.setEnabled(_openInSel.isSelected());
                }
            });

            add(pnl, BorderLayout.CENTER);
        }

        private final class MySearchButtonListener implements ActionListener {
            public void actionPerformed(ActionEvent evt) {
                JFileChooser chooser = new JFileChooser(_selectedDirectory.getText());
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = chooser.showOpenDialog(getParent());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    _selectedDirectory.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        }
    }
}


