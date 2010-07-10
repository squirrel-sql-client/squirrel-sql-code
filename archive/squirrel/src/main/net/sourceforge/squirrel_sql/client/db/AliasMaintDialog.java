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
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.OkCancelPanel;
import net.sourceforge.squirrel_sql.fw.gui.OkCancelPanelEvent;
import net.sourceforge.squirrel_sql.fw.gui.OkCancelPanelListener;
import net.sourceforge.squirrel_sql.fw.gui.PropertyPanel;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.ObjectCacheChangeEvent;
import net.sourceforge.squirrel_sql.fw.util.ObjectCacheChangeListener;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;

public class AliasMaintDialog extends JDialog {
    /**
     * Maintenance types.
     */
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
        String ADD = "Add Alias";
        String CHANGE = "Change Alias";
        String DRIVER = "Driver:";
        String NAME = "Name:";
        String URL = "URL:";
        String USER_NAME = "User Name:";
    }

    /** Application API. */
    private IApplication _app;

    /** The <TT>ISQLAlias</TT> being maintained. */
    private ISQLAlias _sqlAlias;

    /**
     * The requested type of maintenace.
     * @see MaintenanceType
     */
    private int _maintType;

    /** Alias name. */
    private JTextField _aliasName = new JTextField();

    /** Dropdown of all the drivers in the system. */
    private DriversCombo _drivers;

    /** URL to the data source. */
    private JTextField _url = new JTextField();

    /** User name */
    private JTextField _userName = new JTextField();

    /**
     * Ctor.
     *
     * @param   app         Application API.
     * @param   owner       The owning <TT>Frame</TT>.
     * @param   sqlAlias    The <TT>ISQLAlias</TT> to be maintained.
     * @param   maintType   The maintenance type.
     */
    public AliasMaintDialog(IApplication app, Frame owner, ISQLAlias sqlAlias,
                                int maintType) {
        super(owner, maintType == MaintenanceType.MODIFY ? i18n.CHANGE : i18n.ADD, true);
        if (app == null) {
            throw new IllegalArgumentException("Null IApplication passed");
        }

        _app = app;
        _sqlAlias = sqlAlias;
        _maintType = maintType;

        createUserInterface();
        loadData();
    }

    private void loadData() {
        _aliasName.setText(_sqlAlias.getName());
        _userName.setText(_sqlAlias.getUserName());
        if (_maintType != MaintenanceType.NEW) {
            _drivers.setSelectedItem(_sqlAlias.getDriverIdentifier());
            _url.setText(_sqlAlias.getUrl());
        } else {
            ISQLDriver driver = _drivers.getSelectedDriver();
            if (driver != null) {
                _url.setText(driver.getUrl());
            }
        }
    }

    private void performCancel() {
        dispose();
    }

    /**
     * OK button pressed. Edit data and if ok save to aliases model
     * and then close dialog.
     */
    private void performOk() {
        try {
            applyFromDialog(_sqlAlias);
            _sqlAlias.assignFrom(_sqlAlias);
            if (_maintType == MaintenanceType.NEW ||
                    _maintType == MaintenanceType.COPY) {
                _app.getDataCache().addAlias(_sqlAlias);
            }
            dispose();
        } catch(ValidationException ex) {
            new ErrorDialog(this, ex).show();
        } catch(DuplicateObjectException ex) {
            new ErrorDialog(this, ex).show();
        }
    }

    private void applyFromDialog(ISQLAlias alias)
            throws ValidationException {
        ISQLDriver driver = _drivers.getSelectedDriver();
        if (driver == null) {
            throw new ValidationException("Must select driver");//i18n
        }
        alias.setName(_aliasName.getText().trim());
        alias.setDriverIdentifier(_drivers.getSelectedDriver().getIdentifier());
        alias.setUrl(_url.getText().trim());
        alias.setUserName(_userName.getText().trim());
    }

    private void showNewDriverDialog() {
        final DataCache cache = _app.getDataCache();
        final IdentifierFactory idFactory = IdentifierFactory.getInstance();
        final ISQLDriver driver = cache.createDriver(idFactory.createIdentifier());
        DriverMaintDialog dlog = new DriverMaintDialog(_app, GUIUtils.getOwningFrame(this), driver,
                                    DriverMaintDialog.MaintenanceType.NEW);
        dlog.show();
    }

    private void createUserInterface() {
        PropertyPanel dataEntryPnl = new PropertyPanel();

        JLabel lbl = new JLabel(i18n.NAME, SwingConstants.RIGHT);
        dataEntryPnl.add(lbl, _aliasName);

        _drivers = new  DriversCombo();
        _drivers.addItemListener(new DriversComboItemListener());
        lbl = new JLabel(i18n.DRIVER, SwingConstants.RIGHT);
        JPanel driverPnl = new JPanel(new BorderLayout());
        driverPnl.add(_drivers, BorderLayout.CENTER);
        JButton newDriverBtn = new JButton("New");
        newDriverBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                showNewDriverDialog();
            }
        });
        driverPnl.add(newDriverBtn, BorderLayout.EAST);
        dataEntryPnl.add(lbl, driverPnl);

        lbl = new JLabel(i18n.URL, SwingConstants.RIGHT);
        dataEntryPnl.add(lbl, _url );

        lbl = new JLabel(i18n.USER_NAME, SwingConstants.RIGHT);
        dataEntryPnl.add(lbl, _userName);

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

    private final class DriversComboItemListener implements ItemListener {
        public void itemStateChanged(ItemEvent evt) {
            ISQLDriver driver = (ISQLDriver)evt.getItem();
            if (driver != null) {
                _url.setText(driver.getUrl());
            }
        }
    }

    private final class DriversCombo extends JComboBox {
        private Map _map = new HashMap();

        DriversCombo() {
            super();
            List list = new ArrayList();
            for (Iterator it = AliasMaintDialog.this._app.getDataCache().drivers(); it.hasNext();) {
                ISQLDriver sqlDriver = ((ISQLDriver)it.next());
                _map.put(sqlDriver.getIdentifier(), sqlDriver);
                list.add(sqlDriver);
            }
            Collections.sort(list);
            for (Iterator it = list.iterator(); it.hasNext();) {
                addItem(it.next());
            }
        }


        void setSelectedItem(IIdentifier id) {
            super.setSelectedItem(_map.get(id));
        }


        ISQLDriver getSelectedDriver() {
            return (ISQLDriver)getSelectedItem();
        }
    }
}
