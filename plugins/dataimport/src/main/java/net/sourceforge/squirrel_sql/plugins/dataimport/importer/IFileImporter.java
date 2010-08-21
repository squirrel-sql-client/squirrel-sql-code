package net.sourceforge.squirrel_sql.plugins.dataimport.importer;
/*
 * Copyright (C) 2007 Thorsten Mürell
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
import java.io.IOException;
import java.util.Date;

import javax.swing.JComponent;

/**
 * This interface describes an importer for a file type.
 * 
 * @author Thorsten Mürell
 */
public interface IFileImporter {
	/**
	 * Opens the file.
	 * 
	 * This method has to used in order to read the file.
	 * 
	 * @return <code>true</code> on success, <code>false</code> otherwise
	 * @throws IOException If an I/O error occurs, this exception is thrown
	 */
	public boolean open() throws IOException;
	/**
	 * Closes the file.
	 * 
	 * This method has to be called after you finished reading the file.
	 * 
	 * @return <code>true</code> on success, <code>false</code> otherwise
	 * @throws IOException If an I/O error occurs, this exception is thrown
	 */
	public boolean close() throws IOException;
	/**
	 * Returns a preview of the importer's data in string format
	 * 
	 * @param noOfLines The lines to return
	 * @return A table with noOfLines rows
	 * @throws IOException If an error reading the import file occurred.
	 */
	public String[][] getPreview(int noOfLines) throws IOException;
	
	/**
	 * Returns the number of rows in the file.
	 * 
	 * @return The number of rows in the file or -1 if the value is unknown.
	 * @throws IOException If an I/O error occurs, this exception is thrown
	 */
	public int getRows() throws IOException;
	
	/**
	 * Resets the file pointer to the first row.
	 * 
	 * @return <code>true</code> on success, <code>false</code> otherwise
	 * @throws IOException If an I/O error occurs, this exception is thrown
	 */
	public boolean reset() throws IOException;
	
	/**
	 * Moves the file pointer to the next row.
	 * 
	 * @return <code>true</code> on success, <code>false</code> otherwise
	 * @throws IOException If an I/O error occurs, this exception is thrown
	 */
	public boolean next() throws IOException;
	
	/**
	 * Returns the given column as a string
	 * 
	 * @param column The column number to retrieve
	 * @return The string value of the column
	 * @throws IOException If an I/O error occurs, this exception is thrown
	 */
	public String getString(int column) throws IOException;
	
	/**
	 * Returns the given column as a long
	 * 
	 * @param column The column number to retrieve
	 * @return The long value of the column
	 * @throws IOException If an I/O error occurs, this exception is thrown
	 * @throws UnsupportedFormatException If the column cannot be converted to a 
	 *                                    long
	 */
	public Long getLong(int column) throws IOException, UnsupportedFormatException;
	
	/**
	 * Returns the given column as an integer
	 * 
	 * @param column The column number to retrieve
	 * @return The integer value of the column
	 * @throws IOException If an I/O error occurs, this exception is thrown
	 * @throws UnsupportedFormatException If the column cannot be converted to an 
	 *                                    integer
	 */
	public Integer getInt(int column) throws IOException, UnsupportedFormatException;
	
	/**
	 * Returns the given column as a date
	 * 
	 * @param column The column number to retrieve
	 * @return The date value of the column
	 * @throws IOException If an I/O error occurs, this exception is thrown
	 * @throws UnsupportedFormatException If the column cannot be converted to a 
	 *                                    date
	 */
	public Date getDate(int column) throws IOException, UnsupportedFormatException;
	
	/**
	 * This method returns the panel that is used to configure the importer.
	 * 
	 * The importer is not required to show this panel. So you should provide reasonable
	 * defaults for the setting values.
	 * 
	 * E.g. the importer can ask for columns seperators.
	 * 
	 * @return The panel to include in the question dialog. Return <code>null</code> to
	 *         signal, that no configuration is possible and no dialog should be shown.
	 */
	public JComponent getConfigurationPanel();
}
