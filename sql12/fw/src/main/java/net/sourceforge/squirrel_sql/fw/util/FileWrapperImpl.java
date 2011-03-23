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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * A wrapper for java.io.File which simply delegates all calls to the wrapped File. Use the Factory to
 * instantiate this class to avoid coupling.
 */
public class FileWrapperImpl implements Serializable, Comparable<FileWrapperImpl>, FileWrapper
{

	/**
	 * The File that is wrapped by this class. All public methods delegate to this File, unwrapping inputs and
	 * wrapping outputs.
	 */
	private File _wrappedFile = null;

	/* -- Constructors -- */

	/**
	 * Copy constructor factory method. This is a shallow copy as the underlying file is not cloned.
	 * 
	 * @param impl
	 *           the instance to copy.
	 * @return a shallow copy of the specified instance.
	 */
	public FileWrapperImpl(FileWrapperImpl impl)
	{
		this._wrappedFile = impl._wrappedFile;
	}

	/**
	 * Used internally, so this is not exposed.
	 * 
	 * @param wrappedFile
	 *           the file that is to be wrapped by the new instance.
	 */
	public FileWrapperImpl(File wrappedFile)
	{
		this._wrappedFile = wrappedFile;
	}

	/**
	 * Creates a new <code>File</code> instance by converting the given pathname string into an abstract
	 * pathname. If the given string is the empty string, then the result is the empty abstract pathname.
	 * 
	 * @param pathname
	 *           A pathname string
	 * @throws NullPointerException
	 *            If the <code>pathname</code> argument is <code>null</code>
	 */
	public FileWrapperImpl(String pathname)
	{
		_wrappedFile = new File(pathname);
	}

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
	public FileWrapperImpl(String parent, String child)
	{
		_wrappedFile = new File(parent, child);
	}

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
	public FileWrapperImpl(FileWrapper parent, String child)
	{
		
		if (parent != null) {
			FileWrapperImpl parentImpl = (FileWrapperImpl) parent;
			_wrappedFile = new File(parentImpl._wrappedFile, child);
		} else {
			_wrappedFile = new File((File)null, child);
		}
	}

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
	public FileWrapperImpl(URI uri)
	{
		_wrappedFile = new File(uri);
	}

	/* -- Path-component accessors -- */

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#getName()
	 */
	public String getName()
	{
		return _wrappedFile.getName();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#getParent()
	 */
	public String getParent()
	{
		return _wrappedFile.getParent();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#getParentFile()
	 */
	public FileWrapper getParentFile()
	{
		if (_wrappedFile.getParentFile() == null) {
			return null;
		}
		return new FileWrapperImpl(_wrappedFile.getParentFile());
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#getPath()
	 */
	public String getPath()
	{
		return _wrappedFile.getPath();
	}

	/* -- Path operations -- */

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#isAbsolute()
	 */
	public boolean isAbsolute()
	{
		return _wrappedFile.isAbsolute();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#getAbsolutePath()
	 */
	public String getAbsolutePath()
	{
		return _wrappedFile.getAbsolutePath();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#getAbsoluteFile()
	 */
	public FileWrapper getAbsoluteFile()
	{
		return new FileWrapperImpl(_wrappedFile.getAbsoluteFile());
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#getCanonicalPath()
	 */
	public String getCanonicalPath() throws IOException
	{
		return _wrappedFile.getCanonicalPath();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#getCanonicalFile()
	 */
	public FileWrapper getCanonicalFile() throws IOException
	{
		return new FileWrapperImpl(_wrappedFile.getCanonicalFile());
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#toURL()
	 */
	public URL toURL() throws MalformedURLException
	{
		return _wrappedFile.toURI().toURL();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#toURI()
	 */
	public URI toURI()
	{
		return _wrappedFile.toURI();
	}

	/* -- Attribute accessors -- */

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#canRead()
	 */
	public boolean canRead()
	{
		return _wrappedFile.canRead();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#canWrite()
	 */
	public boolean canWrite()
	{
		return _wrappedFile.canWrite();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#exists()
	 */
	public boolean exists()
	{
		return _wrappedFile.exists();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#isDirectory()
	 */
	public boolean isDirectory()
	{
		return _wrappedFile.isDirectory();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#isFile()
	 */
	public boolean isFile()
	{
		return _wrappedFile.isFile();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#isHidden()
	 */
	public boolean isHidden()
	{
		return _wrappedFile.isHidden();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#lastModified()
	 */
	public long lastModified()
	{
		return _wrappedFile.lastModified();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#length()
	 */
	public long length()
	{
		return _wrappedFile.length();
	}

	/* -- File operations -- */

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#createNewFile()
	 */
	public boolean createNewFile() throws IOException
	{
		return _wrappedFile.createNewFile();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#delete()
	 */
	public boolean delete()
	{
		return _wrappedFile.delete();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#deleteOnExit()
	 */
	public void deleteOnExit()
	{
		_wrappedFile.deleteOnExit();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#list()
	 */
	public String[] list()
	{
		return _wrappedFile.list();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#list(java.io.FilenameFilter)
	 */
	public String[] list(FilenameFilter filter)
	{
		return _wrappedFile.list(filter);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#listFiles()
	 */
	public FileWrapper[] listFiles()
	{
		return wrapFiles(_wrappedFile.listFiles());
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#listFiles(java.io.FilenameFilter)
	 */
	public FileWrapper[] listFiles(FilenameFilter filter)
	{
		return wrapFiles(_wrappedFile.listFiles(filter));
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#listFiles(java.io.FileFilter)
	 */
	public FileWrapper[] listFiles(FileFilter filter)
	{
		return wrapFiles(_wrappedFile.listFiles(filter));
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#mkdir()
	 */
	public boolean mkdir()
	{
		return _wrappedFile.mkdir();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#mkdirs()
	 */
	public boolean mkdirs()
	{
		return _wrappedFile.mkdirs();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#renameTo(net.sourceforge.squirrel_sql.fw.util.FileWrapper)
	 */
	public boolean renameTo(FileWrapper dest)
	{
		return _wrappedFile.renameTo(((FileWrapperImpl) dest)._wrappedFile);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#setLastModified(long)
	 */
	public boolean setLastModified(long time)
	{
		return _wrappedFile.setLastModified(time);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#setReadOnly()
	 */
	public boolean setReadOnly()
	{
		return _wrappedFile.setReadOnly();
	}

	/* -- Filesystem interface -- */

	/**
	 * List the available filesystem roots.
	 * <p>
	 * A particular Java platform may support zero or more hierarchically-organized file systems. Each file
	 * system has a <code>root</code> directory from which all other files in that file system can be reached.
	 * Windows platforms, for example, have a root directory for each active drive; UNIX platforms have a
	 * single root directory, namely <code>"/"</code>. The set of available filesystem roots is affected by
	 * various system-level operations such as the insertion or ejection of removable media and the
	 * disconnecting or unmounting of physical or virtual disk drives.
	 * <p>
	 * This method returns an array of <code>File</code> objects that denote the root directories of the
	 * available filesystem roots. It is guaranteed that the canonical pathname of any file physically present
	 * on the local machine will begin with one of the roots returned by this method.
	 * <p>
	 * The canonical pathname of a file that resides on some other machine and is accessed via a
	 * remote-filesystem protocol such as SMB or NFS may or may not begin with one of the roots returned by
	 * this method. If the pathname of a remote file is syntactically indistinguishable from the pathname of a
	 * local file then it will begin with one of the roots returned by this method. Thus, for example,
	 * <code>File</code> objects denoting the root directories of the mapped network drives of a Windows
	 * platform will be returned by this method, while <code>File</code> objects containing UNC pathnames will
	 * not be returned by this method.
	 * <p>
	 * Unlike most methods in this class, this method does not throw security exceptions. If a security manager
	 * exists and its <code>{@link
     * java.lang.SecurityManager#checkRead(java.lang.String)}</code> method denies read access
	 * to a particular root directory, then that directory will not appear in the result.
	 * 
	 * @return An array of <code>File</code> objects denoting the available filesystem roots, or
	 *         <code>null</code> if the set of roots could not be determined. The array will be empty if there
	 *         are no filesystem roots.
	 * @since 1.2
	 */
	public static FileWrapper[] listRoots()
	{
		return wrapFiles(File.listRoots());
	}

	/**
	 * <p>
	 * Creates a new empty file in the specified directory, using the given prefix and suffix strings to
	 * generate its name. If this method returns successfully then it is guaranteed that:
	 * <ol>
	 * <li>The file denoted by the returned abstract pathname did not exist before this method was invoked, and
	 * <li>Neither this method nor any of its variants will return the same abstract pathname again in the
	 * current invocation of the virtual machine.
	 * </ol>
	 * This method provides only part of a temporary-file facility. To arrange for a file created by this
	 * method to be deleted automatically, use the <code>{@link #deleteOnExit}</code> method.
	 * <p>
	 * The <code>prefix</code> argument must be at least three characters long. It is recommended that the
	 * prefix be a short, meaningful string such as <code>"hjb"</code> or <code>"mail"</code>. The
	 * <code>suffix</code> argument may be <code>null</code>, in which case the suffix <code>".tmp"</code> will
	 * be used.
	 * <p>
	 * To create the new file, the prefix and the suffix may first be adjusted to fit the limitations of the
	 * underlying platform. If the prefix is too long then it will be truncated, but its first three characters
	 * will always be preserved. If the suffix is too long then it too will be truncated, but if it begins with
	 * a period character (<code>'.'</code>) then the period and the first three characters following it will
	 * always be preserved. Once these adjustments have been made the name of the new file will be generated by
	 * concatenating the prefix, five or more internally-generated characters, and the suffix.
	 * <p>
	 * If the <code>directory</code> argument is <code>null</code> then the system-dependent default
	 * temporary-file directory will be used. The default temporary-file directory is specified by the system
	 * property <code>java.io.tmpdir</code>. On UNIX systems the default value of this property is typically
	 * <code>"/tmp"</code> or <code>"/var/tmp"</code>; on Microsoft Windows systems it is typically
	 * <code>"C:\\WINNT\\TEMP"</code>. A different value may be given to this system property when the Java
	 * virtual machine is invoked, but programmatic changes to this property are not guaranteed to have any
	 * effect upon the temporary directory used by this method.
	 * 
	 * @param prefix
	 *           The prefix string to be used in generating the file's name; must be at least three characters
	 *           long
	 * @param suffix
	 *           The suffix string to be used in generating the file's name; may be <code>null</code>, in which
	 *           case the suffix <code>".tmp"</code> will be used
	 * @param directory
	 *           The directory in which the file is to be created, or <code>null</code> if the default
	 *           temporary-file directory is to be used
	 * @return An abstract pathname denoting a newly-created empty file
	 * @throws IllegalArgumentException
	 *            If the <code>prefix</code> argument contains fewer than three characters
	 * @throws IOException
	 *            If a file could not be created
	 * @throws SecurityException
	 *            If a security manager exists and its <code>
	 *            {@link java.lang.SecurityManager#checkWrite(java.lang.String)}</code> method does not allow a
	 *            file to be created
	 * @since 1.2
	 */
	public static FileWrapper createTempFile(String prefix, String suffix, FileWrapper directory)
		throws IOException
	{
		if (directory == null)
		{
			return new FileWrapperImpl(File.createTempFile(prefix, suffix, null));
		}
		else
		{
			return new FileWrapperImpl(File.createTempFile(prefix, suffix,
				((FileWrapperImpl) directory)._wrappedFile));
		}
	}

	/**
	 * Creates an empty file in the default temporary-file directory, using the given prefix and suffix to
	 * generate its name. Invoking this method is equivalent to invoking <code>
	 * {@link #createTempFile(java.lang.String, java.lang.String, java.io.File)
	 * createTempFile(prefix,&nbsp;suffix,&nbsp;null)}</code>.
	 * 
	 * @param prefix
	 *           The prefix string to be used in generating the file's name; must be at least three characters
	 *           long
	 * @param suffix
	 *           The suffix string to be used in generating the file's name; may be <code>null</code>, in which
	 *           case the suffix <code>".tmp"</code> will be used
	 * @return An abstract pathname denoting a newly-created empty file
	 * @throws IllegalArgumentException
	 *            If the <code>prefix</code> argument contains fewer than three characters
	 * @throws IOException
	 *            If a file could not be created
	 * @throws SecurityException
	 *            If a security manager exists and its <code>
	 *            {@link java.lang.SecurityManager#checkWrite(java.lang.String)}</code> method does not allow a
	 *            file to be created
	 * @since 1.2
	 */
	public static FileWrapper createTempFile(String prefix, String suffix) throws IOException
	{
		return createTempFile(prefix, suffix, null);
	}

	/* -- Basic infrastructure -- */

	/**
	 * Compares two abstract pathnames lexicographically. The ordering defined by this method depends upon the
	 * underlying system. On UNIX systems, alphabetic case is significant in comparing pathnames; on Microsoft
	 * Windows systems it is not.
	 * 
	 * @param pathname
	 *           The abstract pathname to be compared to this abstract pathname
	 * @return Zero if the argument is equal to this abstract pathname, a value less than zero if this abstract
	 *         pathname is lexicographically less than the argument, or a value greater than zero if this
	 *         abstract pathname is lexicographically greater than the argument
	 * @since 1.2
	 */
	public int compareTo(FileWrapperImpl pathname)
	{
		return _wrappedFile.compareTo(pathname._wrappedFile);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#toString()
	 */
	@Override
	public String toString()
	{
		return _wrappedFile.toString();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_wrappedFile == null) ? 0 : _wrappedFile.hashCode());
		return result;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		FileWrapperImpl other = (FileWrapperImpl) obj;
		if (_wrappedFile == null)
		{
			if (other._wrappedFile != null) { return false; }
		}
		else if (!_wrappedFile.equals(other._wrappedFile)) { return false; }
		return true;
	}

	/**
	 * WriteObject is called to save this filename. The separator character is saved also so it can be replaced
	 * in case the path is reconstituted on a different host type.
	 */
	private synchronized void writeObject(java.io.ObjectOutputStream s) throws IOException
	{
	}

	/**
	 * readObject is called to restore this filename. The original separator character is read. If it is
	 * different than the separator character on this system, then the old separator is replaced by the local
	 * separator.
	 */
	private void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException
	{

	}

	/** use serialVersionUID from JDK 1.0.2 for interoperability */
	private static final long serialVersionUID = 301077366599181567L;

	private static FileWrapper[] wrapFiles(File[] resultFiles)
	{
		if (resultFiles == null) {
			return null;
		}
		FileWrapper[] wrappedFiles = new FileWrapper[resultFiles.length];
		for (int i = 0; i < resultFiles.length; i++)
		{
			wrappedFiles[i] = new FileWrapperImpl(resultFiles[i]);
		}
		return wrappedFiles;
	}

	/**
	 * @param prefix
	 * @param suffix
	 * @param directory
	 * @return
	 * @throws IOException
	 */
	public static FileWrapperImpl createTempFile(String prefix, String suffix, FileWrapperImpl directory)
		throws IOException
	{
		if (directory != null) {
			return new FileWrapperImpl(File.createTempFile(prefix, suffix, directory._wrappedFile));
		} else {
			return new FileWrapperImpl(File.createTempFile(prefix, suffix, null));
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#getFileInputStream()
	 */
	@Override
	public FileInputStream getFileInputStream() throws FileNotFoundException
	{
		return new FileInputStream(this._wrappedFile);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#getFileOutputStream()
	 */
	@Override
	public FileOutputStream getFileOutputStream() throws FileNotFoundException
	{
		return new FileOutputStream(this._wrappedFile);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#getFileWriter()
	 */
	@Override
	public FileWriter getFileWriter() throws IOException
	{
		return new FileWriter(this._wrappedFile);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#getFileReader()
	 */
	@Override
	public FileReader getFileReader() throws IOException
	{
		return new FileReader(this._wrappedFile);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#getBufferedReader()
	 */
	@Override
	public BufferedReader getBufferedReader() throws IOException
	{
		return new BufferedReader(getFileReader());
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.FileWrapper#getPrintWriter()
	 */
	@Override
	public PrintWriter getPrintWriter() throws IOException
	{
		return new PrintWriter(getFileWriter());
	}

}
