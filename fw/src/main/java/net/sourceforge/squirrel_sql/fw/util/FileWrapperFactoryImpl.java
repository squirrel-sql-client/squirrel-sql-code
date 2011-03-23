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
package net.sourceforge.squirrel_sql.fw.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * Implementation of factory that produces FileWrappers which mimic the interface of java.io.File. All of the
 * public constructors of java.io.File can be accessed using the analogous factory methods given here which
 * accept and return FileWrappers instead of Files. The original java.io.File constructor javadoc comments are
 * used here to help choose the right factory method; also I'm just plain lazy.
 * 
 * @author manningr
 */
public class FileWrapperFactoryImpl implements FileWrapperFactory
{

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory#create(net.sourceforge.squirrel_sql.fw.util.FileWrapperImpl)
	 */
	public FileWrapper create(FileWrapperImpl impl)
	{
		return new FileWrapperImpl(impl);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory#create(java.lang.String)
	 */
	public FileWrapper create(String pathname)
	{
		return new FileWrapperImpl(pathname);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory#create(java.lang.String, java.lang.String)
	 */
	public FileWrapper create(String parent, String child)
	{
		return new FileWrapperImpl(parent, child);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory#create(net.sourceforge.squirrel_sql.fw.util.FileWrapper, java.lang.String)
	 */
	public FileWrapper create(FileWrapper parent, String child)
	{
		return new FileWrapperImpl(parent, child);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory#create(java.net.URI)
	 */
	public FileWrapper create(URI uri)
	{
		return new FileWrapperImpl(uri);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory#create(java.io.File)
	 */
	public FileWrapper create(File f)
	{
		return new FileWrapperImpl(f);
	}
	
	/**
	 * @param prefix
	 * @param suffix
	 * @param directory
	 * @return
	 * @throws IOException
	 */
	public FileWrapperImpl createTempFile(String prefix, String suffix, FileWrapper directory)
		throws IOException
	{
		return FileWrapperImpl.createTempFile(prefix, suffix, (FileWrapperImpl) directory);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory#createTempFile(java.lang.String, java.lang.String)
	 */
	@Override
	public FileWrapper createTempFile(String prefix, String suffix) throws IOException
	{
		return FileWrapperImpl.createTempFile(prefix, suffix);
	}
	
	
}
