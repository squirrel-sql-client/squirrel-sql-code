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
package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Window;


/**
 * A specialization of {@link TableExportController}, that honors that the source is a SQL-Statement.
 * The following behavior is changed:
 * <ul>
 * 	<li>Tell the user, that exporting to Excel is dangerous, if the SQL returns a huge data set.</li>
 *  <li>Let the user choose, if he want to export the whole result-set or only the first n lines</li>
 * </ul>
 * 
 * @author Stefan Willinger
 *
 */
public class ResultSetExportController extends TableExportController
{


   public ResultSetExportController(Window owner)
   {
      super(owner);
   }

   /**
	 * @see TableExportController#shouldWarnIfExcel()
	 */
	@Override
	protected boolean shouldWarnIfExcel() {
		return true;
	}

	/**
	 * @see TableExportController#createDialog()
    * @param owner
	 */
	@Override
	protected ResultSetExportDialog createDialog(Window owner)
	{
		final ResultSetExportDialog dlg = new ResultSetExportDialog(owner);

		dlg.radComplete.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (dlg.radComplete.isSelected())
				{
					dlg.txtLimitRows.setEnabled(false);
				}
				else
				{
					dlg.txtLimitRows.setEnabled(true);
				}
			}
		});
		return dlg;
	}

	/**
	 * @see TableExportController#writePrefs()
	 */
	@Override
	protected void writeControlsToPrefs(TableExportPreferences prefs)
	{
		super.writeControlsToPrefs(prefs);

		ResultSetExportDialog dlg = (ResultSetExportDialog) super.getDialog();
		
		//Preferences.put(PREF_KEY_LIMIT_ROWS, dlg.txtLimitRows.getText());
		prefs.setLimitRows(dlg.txtLimitRows.getText());
	}

	/**
	 * @see TableExportController#initSelectionPanel(java.util.prefs.Preferences)
	 */
	@Override
	protected void initSelectionPanel(TableExportPreferences prefs)
	{
		super.initSelectionPanel(prefs);
		 
		ResultSetExportDialog dlg = (ResultSetExportDialog) super.getDialog();

		dlg.txtLimitRows.setText(prefs.getLimitRows());

		if(dlg.radComplete.isSelected())
		{
			dlg.txtLimitRows.setEnabled(false);
		}
	}

	public int getMaxRows()
	{
		ResultSetExportDialog dlg = (ResultSetExportDialog) super.getDialog();
		return dlg.txtLimitRows.getInt();
	}

}
