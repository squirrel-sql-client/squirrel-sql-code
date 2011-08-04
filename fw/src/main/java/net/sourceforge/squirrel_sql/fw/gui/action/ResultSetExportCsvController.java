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


/**
 * A specialization of {@link TableExportCsvController}, that tells the user, that exporting to Excel is dangerous, if the SQL returns a huge data set.
 * @author Stefan Willinger
 *
 */
public class ResultSetExportCsvController extends TableExportCsvController {

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.TableExportCsvController#shouldWarnIfExcel()
	 */
	@Override
	protected boolean shouldWarnIfExcel() {
		return true;
	}
	
}
