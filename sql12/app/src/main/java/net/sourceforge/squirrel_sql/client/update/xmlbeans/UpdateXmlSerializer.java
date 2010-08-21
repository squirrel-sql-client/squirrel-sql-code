/*
 * Copyright (C) 2008 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.client.update.xmlbeans;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import net.sourceforge.squirrel_sql.fw.util.FileWrapper;

public interface UpdateXmlSerializer {

	/**
	 * Writes the specified Release XMLBean to the specified file.
	 * 
	 * @param channelBean
	 *           the bean to write.
	 * @param filename
	 *           the filename of the file to write to.
	 * @throws FileNotFoundException
	 *            if the specified file could not be written to
	 * 
	 */
	void write(ChannelXmlBean channelBean, String filename) throws FileNotFoundException;

	/**
	 * Writes the specified ChangeList XMLBean to the specified file.
	 * 
	 * @param changeBean
	 *           the bean to write
	 * @param filename
	 *           the filename of the file to write to
	 * 
	 * @throws FileNotFoundException
	 *            if the specified file could not be written to
	 */
	void write(ChangeListXmlBean changeBean, String filename) throws FileNotFoundException;

	/**
	 * Writes the specified ChangeList XMLBean to the specified file.
	 * 
	 * @param changeBean
	 *           the bean to write
	 * @param filename
	 *           the filename of the file to write to
	 * 
	 * @throws FileNotFoundException
	 *            if the specified file could not be written to
	 */
	void write(ChangeListXmlBean changeBean, FileWrapper file) throws FileNotFoundException;

	/**
	 * Reads a Channel XMLBean from the specified file.
	 * 
	 * @param filename
	 *           the filename of the file to read the XML bean from.
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * 
	 * @throws Exception
	 *            if any IO exceptions occurr.
	 */
	ChannelXmlBean readChannelBean(String filename) throws FileNotFoundException, IOException;

	ChannelXmlBean readChannelBean(FileWrapper fileWrapper) throws FileNotFoundException, IOException;

	/**
	 * Reads a Channel XMLBean from the specified InputStream.
	 * 
	 * @param is
	 *           the InputStream to read the XML bean from.
	 * 
	 * @throws Exception
	 *            if any IO exceptions occurr.
	 */
	ChannelXmlBean readChannelBean(InputStream is) throws IOException;

	ChangeListXmlBean readChangeListBean(FileWrapper file) throws FileNotFoundException;

}