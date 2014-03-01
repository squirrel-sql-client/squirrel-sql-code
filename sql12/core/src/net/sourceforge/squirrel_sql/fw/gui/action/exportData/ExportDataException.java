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
 * Exception to indicate a serious problem while dealing with {@link IExportData}.
 * If this exception occurs, there is no way to continue the export progress.  
 * @author Stefan Willinger
 *
 */
public class ExportDataException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor, if the error depends on a other Exception (e.g. a SQLException)
	 * @param message The message
	 * @param cause The cause of the problem.
	 */
	public ExportDataException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor, if the error does not depend on a other Exception.
	 * @param message The occurred error.
	 */
	public ExportDataException(String message) {
		super(message);
	}

}
