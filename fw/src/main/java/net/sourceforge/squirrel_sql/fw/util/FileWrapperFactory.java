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
 * Interface for the factory that produces FileWrappers which mimic the interface of java.io.File. All of the
 * public constructors of java.io.File can be accessed using the analogous factory methods given here which
 * accept and return FileWrappers instead of Files. The original java.io.File constructor javadoc comments are
 * used here to help choose the right factory method; also I'm just plain lazy.  Use of this interface allows
 * classes to avoid tight coupling between themselves and the java.io.File library. 
 *  
 * @author manningr
 *
 */
public interface FileWrapperFactory
{

	/**
	 * Copy constructor factory method. This is a shallow copy as the underlying file is not cloned.
	 * 
	 * @param impl
	 *           the instance to copy.
	 * @return a shallow copy of the specified instance.
	 */
	FileWrapper create(FileWrapperImpl impl);

	/**
	 * Creates a FileWrapper from the specified File.
	 * 
	 * @param f the File to wrap.
	 * @return a new FileWrapper instance.
	 */
	FileWrapper create(File f);
	
	/**
	 * Creates a new <code>File</code> instance by converting the given pathname string into an abstract
	 * pathname. If the given string is the empty string, then the result is the empty abstract pathname.
	 * 
	 * @param pathname
	 *           A pathname string
	 * @throws NullPointerException
	 *            If the <code>pathname</code> argument is <code>null</code>
	 */
	FileWrapper create(String pathname);

	/**
	 * Creates a new <code>File</code> instance from a parent pathname string and a child pathname string.
	 * <p>
	 * If <code>parent</code> is <code>null</code> then the new <code>File</code> instance is created as if by
	 * invoking the single-argument <code>File</code> constructor on the given <code>child</code> pathname
	 * string.
	 * <p>
	 * Otherwise the <code>parent</code> pathname string is taken to denote a directory, and the
	 * <code>child</code> pathname string is taken to denote either a directory or a file. If the
	 * <code>child</code> pathname string is absolute then it is converted into a relative pathname in a
	 * system-dependent way. If <code>parent</code> is the empty string then the new <code>File</code> instance
	 * is created by converting <code>child</code> into an abstract pathname and resolving the result against a
	 * system-dependent default directory. Otherwise each pathname string is converted into an abstract
	 * pathname and the child abstract pathname is resolved against the parent.
	 * 
	 * @param parent
	 *           The parent pathname string
	 * @param child
	 *           The child pathname string
	 * @throws NullPointerException
	 *            If <code>child</code> is <code>null</code>
	 */
	FileWrapper create(String parent, String child);

	/**
	 * Creates a new <code>File</code> instance from a parent abstract pathname and a child pathname string.
	 * <p>
	 * If <code>parent</code> is <code>null</code> then the new <code>File</code> instance is created as if by
	 * invoking the single-argument <code>File</code> constructor on the given <code>child</code> pathname
	 * string.
	 * <p>
	 * Otherwise the <code>parent</code> abstract pathname is taken to denote a directory, and the
	 * <code>child</code> pathname string is taken to denote either a directory or a file. If the
	 * <code>child</code> pathname string is absolute then it is converted into a relative pathname in a
	 * system-dependent way. If <code>parent</code> is the empty abstract pathname then the new
	 * <code>File</code> instance is created by converting <code>child</code> into an abstract pathname and
	 * resolving the result against a system-dependent default directory. Otherwise each pathname string is
	 * converted into an abstract pathname and the child abstract pathname is resolved against the parent.
	 * 
	 * @param parent
	 *           The parent abstract pathname
	 * @param child
	 *           The child pathname string
	 * @throws NullPointerException
	 *            If <code>child</code> is <code>null</code>
	 */
	FileWrapper create(FileWrapper parent, String child);

	/**
	 * Creates a new <tt>File</tt> instance by converting the given <tt>file:</tt> URI into an abstract
	 * pathname.
	 * <p>
	 * The exact form of a <tt>file:</tt> URI is system-dependent, hence the transformation performed by this
	 * constructor is also system-dependent.
	 * <p>
	 * For a given abstract pathname <i>f</i> it is guaranteed that <blockquote><tt>
	 * new File(</tt><i>&nbsp;f</i>
	 * <tt>.{@link #toURI() toURI}()).equals(</tt><i>&nbsp;f</i>
	 * <tt>.{@link #getAbsoluteFile() getAbsoluteFile}())
	 * </tt></blockquote> so long as the original abstract pathname, the URI, and the new abstract pathname are
	 * all created in (possibly different invocations of) the same Java virtual machine. This relationship
	 * typically does not hold, however, when a <tt>file:</tt> URI that is created in a virtual machine on one
	 * operating system is converted into an abstract pathname in a virtual machine on a different operating
	 * system.
	 * 
	 * @param uri
	 *           An absolute, hierarchical URI with a scheme equal to <tt>"file"</tt>, a non-empty path
	 *           component, and undefined authority, query, and fragment components
	 * @throws NullPointerException
	 *            If <tt>uri</tt> is <tt>null</tt>
	 * @throws IllegalArgumentException
	 *            If the preconditions on the parameter do not hold
	 * @see #toURI()
	 * @see java.net.URI
	 * @since 1.4
	 */
	FileWrapper create(URI uri);

	
	/**
	 * @param prefix
	 * @param suffix
	 * @param directory
	 * @return
	 * @throws IOException
	 */
	FileWrapper createTempFile(String prefix, String suffix, FileWrapper directory)
		throws IOException;

	/**
	 * @param prefix
	 * @param suffix
	 * @return
	 */
	FileWrapper createTempFile(String prefix, String suffix) throws IOException;
	
}