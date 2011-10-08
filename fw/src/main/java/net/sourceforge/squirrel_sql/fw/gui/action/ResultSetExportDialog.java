/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.fw.gui.action;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * A customized {@link TableExportCsvDlg}.
 * This customized dialog offer the option to export the whole or only the frist n rows of the result-set.
 * @author Stefan Willinger
 *
 */
public class ResultSetExportDialog extends TableExportCsvDlg {
	
	static final StringManager s_stringMgr = StringManagerFactory
			.getStringManager(ResultSetExportCommand.class);

	static interface i18n {
		String EXPORT_COMPLETE = s_stringMgr.getString("ResultSetExportDialog.executingQuery");
		String LIMIT_ROWS = s_stringMgr.getString("ResultSetExportDialog.limitRows");
	}

	IntegerField txtLimitRows;
	
	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.TableExportCsvDlg#getSelelectionPanel()
	 */
	@Override
	protected Component getSelelectionPanel() {
		JPanel ret = new JPanel(new GridBagLayout());

	      GridBagConstraints gbc;

	      radComplete = new JRadioButton(i18n.EXPORT_COMPLETE);
	      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0);
	      ret.add(radComplete, gbc);

	      radSelection = new JRadioButton(i18n.LIMIT_ROWS);
	      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
	      ret.add(radSelection, gbc);

	      gbc = new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
	      txtLimitRows = new IntegerField(5);
	      ret.add(txtLimitRows, gbc);
	      
	      gbc = new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
	      ret.add(new JPanel(), gbc);


	      ButtonGroup bg = new ButtonGroup();
	      bg.add(radComplete);
	      bg.add(radSelection);

	      return ret;
	}

}
