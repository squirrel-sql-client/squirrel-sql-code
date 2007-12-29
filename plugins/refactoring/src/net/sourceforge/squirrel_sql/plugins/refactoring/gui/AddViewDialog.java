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

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class AddViewDialog extends AbstractRefactoringTabbedDialog {

    /**
     * Internationalized strings for this class.
     */
    private static final StringManager s_stringMgr =
            StringManagerFactory.getStringManager(AddViewDialog.class);
    /**
     * Logger for this class.
     */
    private final static ILogger log =
            LoggerController.createLogger(AddViewDialog.class);


    protected interface i18n {

        String DIALOG_TITLE =
                s_stringMgr.getString("AddViewDialog.title");

        String TABBEDPANE_PROPERTIES_LABEL =
                s_stringMgr.getString("AddViewDialog.propertiesTabname");

        String TABBEDPANE_DEFINITION_LABEL =
                s_stringMgr.getString("AddViewDialog.definitionTabName");

        String PROPERTIES_NAME_LABEL =
                s_stringMgr.getString("AddViewDialog.propertiesNameLabel");

        String PROPERTIES_CHECK_OPTION_LABEL =
                s_stringMgr.getString("AddViewDialog.checkOptionLabel");

        String PROPERTIES_LOCAL =
                s_stringMgr.getString("AddViewDialog.checkOptionLocal");

        String PROPERTIES_CASCADED =
                s_stringMgr.getString("AddViewDialog.checkOptionCascaded");

    }

    private DefinitionTab _definitionTab;
    private PropertiesTab _propertiesTab;
    private boolean _defintionComplete = false;


    public AddViewDialog() {
        super(new Dimension(400, 250));

        init();
    }


    private void init() {
        _propertiesTab = new PropertiesTab();
        _definitionTab = new DefinitionTab();
        pane.addTab(i18n.TABBEDPANE_PROPERTIES_LABEL, _propertiesTab);
        pane.addTab(i18n.TABBEDPANE_DEFINITION_LABEL, _definitionTab);
        setAllButtonEnabled(false);
        setTitle(AddViewDialog.i18n.DIALOG_TITLE);
    }


    private void checkInputCompletion() {
        if (_propertiesTab._viewNameField.getText().equals("") || !_defintionComplete)
            setAllButtonEnabled(false);
        else // if the check gets till here we have all the needed information
            setAllButtonEnabled(true);
    }


    public void enableCheckOptions(boolean enable) {
        _propertiesTab.enableCheckOptions(enable);
    }


    public String getViewName() {
        return _propertiesTab.getViewName();
    }


    public String getViewDefinition() {
        return _definitionTab.getViewDefinition();
    }


    public String getCheckOption() {
        return _propertiesTab.getCheckOption();
    }


    private class PropertiesTab extends JPanel {
        private JTextField _viewNameField;
        private JCheckBox _checkOptionBox;
        private JRadioButton _localRadio;
        private JRadioButton _cascadeRadio;
        private ButtonGroup _radioGroup;


        public PropertiesTab() {
            init();
            enableCheckOptions(false);
        }


        private void init() {
            setLayout(new GridBagLayout());

            JLabel nameLabel = getBorderedLabel(i18n.PROPERTIES_NAME_LABEL, emptyBorder);

            _viewNameField = new JTextField();
            _viewNameField.setPreferredSize(mediumField);
            _viewNameField.addKeyListener(new KeyListener() {
                public void keyTyped(KeyEvent keyEvent) {
                }


                public void keyPressed(KeyEvent keyEvent) {
                }


                public void keyReleased(KeyEvent keyEvent) {
                    checkInputCompletion();
                }
            });

            JLabel checkOptionLabel = getBorderedLabel(i18n.PROPERTIES_CHECK_OPTION_LABEL, emptyBorder);
            _checkOptionBox = new JCheckBox();
            _checkOptionBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    enableCheckOptionRadios(_checkOptionBox.isSelected());
                }
            });

            _localRadio = new JRadioButton(i18n.PROPERTIES_LOCAL);
            _localRadio.setActionCommand(i18n.PROPERTIES_LOCAL);

            _cascadeRadio = new JRadioButton(i18n.PROPERTIES_CASCADED);
            _cascadeRadio.setActionCommand(i18n.PROPERTIES_CASCADED);
            _cascadeRadio.setSelected(true);

            _radioGroup = new ButtonGroup();
            _radioGroup.add(_localRadio);
            _radioGroup.add(_cascadeRadio);

            Insets boxesInsets = new Insets(5, 5, 0, 5);

            add(nameLabel, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, boxesInsets, 0, 0));
            add(_viewNameField, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, boxesInsets, 0, 0));


            add(checkOptionLabel, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, boxesInsets, 0, 0));
            add(_checkOptionBox, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, boxesInsets, 0, 0));


            add(_localRadio, new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, boxesInsets, 0, 0));
            boxesInsets.bottom = 5;
            add(_cascadeRadio, new GridBagConstraints(1, 3, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, boxesInsets, 0, 0));
        }


        private void enableCheckOptionRadios(boolean enable) {
            _localRadio.setEnabled(enable);
            _cascadeRadio.setEnabled(enable);
        }


        public void enableCheckOptions(boolean enable) {
            _checkOptionBox.setSelected(enable);
            _checkOptionBox.setEnabled(enable);
            enableCheckOptionRadios(enable);
        }


        public String getViewName() {
            return _viewNameField.getText();
        }


        public String getCheckOption() {
            if (!_checkOptionBox.isSelected()) return null;
            return _radioGroup.getSelection().getActionCommand();
        }
    }

    class DefinitionTab extends JPanel {
        JTextArea _definitionArea;


        public DefinitionTab() {
            init();
        }


        private void init() {
            setLayout(new GridBagLayout());

            _definitionArea = new JTextArea();
            _definitionArea.setBorder(BorderFactory.createLineBorder(Color.black));
            _definitionArea.setPreferredSize(mediumField);
            _definitionArea.setLineWrap(true);
            _definitionArea.setWrapStyleWord(true);

            _definitionArea.addKeyListener(new KeyListener() {

                public void keyTyped(KeyEvent keyEvent) {
                }


                public void keyPressed(KeyEvent keyEvent) {
                }


                public void keyReleased(KeyEvent keyEvent) {
                    checkQuery();
                }
            });

            JScrollPane scrollTablePane = new JScrollPane(_definitionArea);

            //adding all Colums together
            add(scrollTablePane, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        }


        private void checkQuery() {
            String lowQuery = _definitionArea.getText().toLowerCase();

            _defintionComplete = lowQuery.contains("select") && lowQuery.contains("from");
            checkInputCompletion();
        }


        public String getViewDefinition() {
            return _definitionArea.getText();
        }
    }


    public static void main(String[] args) {
        AddViewDialog dialog = new AddViewDialog();
        dialog.setVisible(true);
    }

}
