package net.sourceforge.squirrel_sql.client.db;
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
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.OkCancelPanel;
import net.sourceforge.squirrel_sql.fw.gui.OkCancelPanelEvent;
import net.sourceforge.squirrel_sql.fw.gui.OkCancelPanelListener;
import net.sourceforge.squirrel_sql.fw.gui.PropertyPanel;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverClassLoader;
import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;

import net.sourceforge.squirrel_sql.client.IApplication;

public class DriverMaintDialog extends JDialog {

    public interface MaintenanceType {
        int NEW = 1;
        int MODIFY = 2;
        int COPY = 3;
    }

    /**
     * This interface defines locale specific strings. This should be
     * replaced with a property file.
     */
    private interface i18n {
        String ADD = "Add Driver";
        String CHANGE = "Change Driver";
        String DRIVER = "Driver Class Name:";
        String JAR_FILE = "JAR File:";
        String LOAD_WHERE = "Load Driver from CLASSPATH:";
        String NAME = "Name:";
        String URL = "Example URL:";
    }

    private IApplication _app;

    private ISQLDriver _sqlDriver;
    private int _maintType;

    private JTextField _driverName = new JTextField();
    private JCheckBox _usesClassPathChk = new JCheckBox();
    private JTextField _jarFileName = new JTextField();
    private JComboBox _driverClassCmb = new JComboBox();
    private JTextField _url = new JTextField();

    private JButton _searchBtn = new JButton("...");

    private String _currentJarFileText = "";

    public DriverMaintDialog(IApplication app, Frame owner, ISQLDriver sqlDriver,
                                int maintType) {
        super(owner, maintType == MaintenanceType.MODIFY ? i18n.CHANGE : i18n.ADD, true);
        _app = app;
        String jarFileName = sqlDriver.getJarFileName();
        _sqlDriver = sqlDriver;
        _maintType = maintType;
        createUserInterface();
        loadData();
    }


    private void loadData() {
        _driverName.setText(_sqlDriver.getName());
        _usesClassPathChk.setSelected(_sqlDriver.getUsesClassPath());
        setJarFileName(_sqlDriver.getJarFileName());
        //_driverClassName.setText(_sqlDriver.getDriverClassName());
        _driverClassCmb.setSelectedItem(_sqlDriver.getDriverClassName());
        _url.setText(_sqlDriver.getUrl());
    }

    private void performCancel() {
        dispose();
    }

    /**
     * OK button pressed. Edit data and if ok save to drivers model
     * and then close dialog.
     */
    private void performOk() {
        try {
            applyFromDialog();
            if (_maintType == MaintenanceType.NEW ||
                    _maintType == MaintenanceType.COPY) {
                _app.getDataCache().addDriver(_sqlDriver);
            }
            dispose();
        } catch(Exception ex) {
            displayErrorMessage(ex);
        }
    }

    private void applyFromDialog()
            throws ValidationException {
        _sqlDriver.setName(_driverName.getText().trim());
        _sqlDriver.setUsesClassPath(_usesClassPathChk.isSelected());
        _sqlDriver.setJarFileName(_jarFileName.getText().trim());
        _sqlDriver.setDriverClassName(((String)_driverClassCmb.getSelectedItem()).trim());
        _sqlDriver.setUrl(_url.getText().trim());
    }

    private void loadDriversCombo() {
        _driverClassCmb.removeAllItems();
        if (!_usesClassPathChk.isSelected()) {
            try {
                SQLDriverClassLoader cl = new SQLDriverClassLoader(new File(_jarFileName.getText().trim()).toURL());
                Class[] classes = cl.getDriverClasses(_app.getLogger());
                for (int i = 0; i < classes.length; ++i) {
                    _driverClassCmb.addItem(classes[i].getName());
                }
            } catch (MalformedURLException ex) {
                displayErrorMessage(ex);
            } catch (IOException ex) {
                displayErrorMessage(ex);
            }
        }
    }

    private void setJarFileName(String fileName) {
        if (fileName != null && !_currentJarFileText.equals(fileName)) {
            _jarFileName.setText(fileName);
            loadDriversCombo();
            _currentJarFileText = fileName;
        }
    }

    /**
     * Display an error msg in a dialog. Uses
     * <TT>SwingUtilities.invokeLater()</TT> because this may be called
     * before the main dialog is displayed.
     *
     * @param   ex      The <TT>Exception</TT> containing the error
     *                  message.
     */
    private void displayErrorMessage(final Exception ex) {
        final DriverMaintDialog typedThis = this;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ErrorDialog(typedThis, ex).show();
            }
        });
    }

    private void createUserInterface() {
        PropertyPanel dataEntryPnl = new PropertyPanel();

        JLabel lbl = new JLabel(i18n.NAME, SwingConstants.RIGHT);
        dataEntryPnl.add(lbl, _driverName);
        _driverName.setColumns(25);

        lbl = new JLabel(i18n.LOAD_WHERE, SwingConstants.RIGHT);
        dataEntryPnl.add(lbl, _usesClassPathChk);

        lbl = new JLabel(i18n.JAR_FILE, SwingConstants.RIGHT);
        dataEntryPnl.add(lbl, _jarFileName, _searchBtn);

        lbl = new JLabel(i18n.DRIVER, SwingConstants.RIGHT);
        dataEntryPnl.add(lbl, _driverClassCmb);

        lbl = new JLabel(i18n.URL, SwingConstants.RIGHT);
        dataEntryPnl.add(lbl, _url);

        _driverClassCmb.setEditable(true);

        _usesClassPathChk.addChangeListener(new UsesClassPathCheckBoxListener());
        _searchBtn.addActionListener(new MySearchButtonListener());
        _jarFileName.addFocusListener(new JarFileFocusListener());

        // Ok and cancel buttons at bottom of dialog.
        OkCancelPanel btnsPnl = new OkCancelPanel();
        btnsPnl.addListener(new MyOkCancelPanelListener());
        getRootPane().setDefaultButton(btnsPnl.getOkButton());

        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(dataEntryPnl, BorderLayout.NORTH);
        contentPane.add(btnsPnl, BorderLayout.CENTER);

        pack();
        GUIUtils.centerWithinParent(this);
        setResizable(false);
    }

    private final class MyOkCancelPanelListener implements OkCancelPanelListener {
        public void okPressed(OkCancelPanelEvent evt) {
            performOk();
        }

        public void cancelPressed(OkCancelPanelEvent evt) {
            performCancel();
        }
    }

    private final class MySearchButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            JFileChooser chooser = new JFileChooser(_jarFileName.getText().trim());
            chooser.addChoosableFileFilter(new FileExtensionFilter("JAR files", new String[] {".jar", ".zip"}));
            int returnVal = chooser.showOpenDialog(getParent());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                setJarFileName(chooser.getSelectedFile().getAbsolutePath());
            }
        }
    }

    private final class UsesClassPathCheckBoxListener implements ChangeListener {
        public void stateChanged(ChangeEvent evt) {
            boolean allowJarFileEntry = !((JCheckBox)evt.getSource()).isSelected();
            _jarFileName.setEnabled(allowJarFileEntry);
            _searchBtn.setEnabled(allowJarFileEntry);
        }
    }


    private final class JarFileFocusListener implements FocusListener {

        public void focusGained(FocusEvent evt) {
        }

        public void focusLost(FocusEvent evt) {
            setJarFileName(_jarFileName.getText().trim());
        }
    }
}
