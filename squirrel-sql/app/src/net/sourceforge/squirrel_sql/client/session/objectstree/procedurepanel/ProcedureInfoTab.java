package net.sourceforge.squirrel_sql.client.session.objectstree.procedurepanel;
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

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.sourceforge.squirrel_sql.fw.gui.PropertyPanel;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;

/**
 * This is the tab for table information.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ProcedureInfoTab extends BaseProcedurePanelTab {
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n {
		String TITLE = "Info";
		String HINT = "Info";
        String NAME = "Procedure Name:";
        String CATALOG = "Catalogue:";
        String SCHEMA = "Schema:";
        String TYPE = "Type:";
        String REMARKS = "Remarks:";
	}

	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(ProcedureInfoTab.class);

	/** Component to be displayed. */
	private MyComponent _comp;

	/**
	 * Return the title for the tab.
	 *
	 * @return	The title for the tab.
	 */
	public String getTitle() {
		return i18n.TITLE;
	}

	/**
	 * Return the hint for the tab.
	 *
	 * @return	The hint for the tab.
	 */
	public String getHint() {
		return i18n.HINT;
	}

	/**
	 * Return the component to be displayed in the panel.
	 *
	 * @return	The component to be displayed in the panel.
	 */
	public synchronized Component getComponent() {
		if (_comp == null) {
			_comp = new MyComponent();
		}
		return _comp;
	}

	/**
	 * Refresh the component displaying the <TT>ITableInfo</TT> object.
	 */
	public synchronized void refreshComponent() throws IllegalStateException {
		ISession session = getSession();
		if (session == null) {
			throw new IllegalStateException("Null ISession");
		}
		IProcedureInfo pi = getProcedureInfo();
		if ( pi == null) {
			throw new IllegalStateException("Null IProcedureInfo");
		}
		((MyComponent)getComponent()).load(session, pi);
	}

	/**
	 * Component for this tab.
	 */
	private class MyComponent extends PropertyPanel {
		private boolean _fullyCreated = false;
		private JLabel _nameLbl = new JLabel();
		private JLabel _catalogLbl = new JLabel();
		private JLabel _schemaLbl = new JLabel();
		private JLabel _typeLbl = new JLabel();
		private JLabel _remarksLbl = new JLabel();

		MyComponent() {
			super(/*new BorderLayout()*/);
		}

		void load(ISession session, IProcedureInfo pi) {
			try {
				// Lazily create the user interface.
				if (!_fullyCreated) {
					createUserInterface();
					_fullyCreated = true;
				}
				_nameLbl.setText(pi != null ? getString(pi.getSimpleName()) : "" );
				_catalogLbl.setText(pi != null ? getString(pi.getCatalogName()) : "" );
				_schemaLbl.setText(pi != null ? getString(pi.getSchemaName()) : "" );
				_typeLbl.setText(pi != null ? getString(pi.getTypeDescription()) : "" );
				_remarksLbl.setText(pi != null ? getString(pi.getRemarks()) : "" );
			} catch (Exception ex) {
				s_log.error("Error", ex);
			}
		}

		private void createUserInterface() {
	        add(new JLabel(i18n.NAME, SwingConstants.RIGHT), _nameLbl);
	        add(new JLabel(i18n.CATALOG, SwingConstants.RIGHT), _catalogLbl);
	        add(new JLabel(i18n.SCHEMA, SwingConstants.RIGHT), _schemaLbl);
	        add(new JLabel(i18n.TYPE, SwingConstants.RIGHT), _typeLbl);
	        add(new JLabel(i18n.REMARKS, SwingConstants.RIGHT), _remarksLbl);
		}
	}

    private String getString(String str) {
        return str != null ? str : "";
    }
}

