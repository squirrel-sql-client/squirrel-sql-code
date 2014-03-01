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
package net.sourceforge.squirrel_sql.fw.gui.action.exportData;

/**
 * A {@link IDataExportWriter} is responsible for exporting a data structure of {@link IExportData}.
 * @author Stefan Willinger
 *
 */
public interface IDataExportWriter {
	/**
	 * Exports the data structure.
	 * @param data The data to export
	 * @return the number of written data rows or a negative value, if not the whole data are exported.
	 * @throws Exception if any Exception occurs
	 */
	long write(IExportData data) throws Exception;
}
