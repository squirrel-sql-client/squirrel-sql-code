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
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * An interface that abstracts the java.io.File interface, so that the actual implementation can vary. Since
 * java.io.File is a class, it is not easy to write tests for code that deals with Files without having those
 * files actually exist on the filesystem. Since filesystems are OS-specific, this tends to make those tests
 * non-portable. This is my attempt to alleviate this issue. The companion class that implements this
 * interface in FileWrapperImpl, and it can be used to wrap an actual java.io.File, delegating method calls to
 * the underlying File.
 * 
 * @author manningr
 */
public interface FileWrapper
{

	/**
	 * Returns the name of the file or directory denoted by this abstract pathname. This is just the last name
	 * in the pathname's name sequence. If the pathname's name sequence is empty, then the empty string is
	 * returned.
	 * 
	 * @return The name of the file or directory denoted by this abstract pathname, or the empty string if this
	 *         pathname's name sequence is empty
	 */
	String getName();

	/**
	 * Returns the pathname string of this abstract pathname's parent, or <code>null</code> if this pathname
	 * does not name a parent directory.
	 * <p>
	 * The <em>parent</em> of an abstract pathname consists of the pathname's prefix, if any, and each name in
	 * the pathname's name sequence except for the last. If the name sequence is empty then the pathname does
	 * not name a parent directory.
	 * 
	 * @return The pathname string of the parent directory named by this abstract pathname, or
	 *         <code>null</code> if this pathname does not name a parent
	 */
	String getParent();

	/**
	 * Returns the abstract pathname of this abstract pathname's parent, or <code>null</code> if this pathname
	 * does not name a parent directory.
	 * <p>
	 * The <em>parent</em> of an abstract pathname consists of the pathname's prefix, if any, and each name in
	 * the pathname's name sequence except for the last. If the name sequence is empty then the pathname does
	 * not name a parent directory.
	 * 
	 * @return The abstract pathname of the parent directory named by this abstract pathname, or
	 *         <code>null</code> if this pathname does not name a parent
	 * @since 1.2
	 */
	FileWrapper getParentFile();

	/**
	 * Converts this abstract pathname into a pathname string. The resulting string uses the {@link #separator
	 * default name-separator character} to separate the names in the name sequence.
	 * 
	 * @return The string form of this abstract pathname
	 */
	String getPath();

	/**
	 * Tests whether this abstract pathname is absolute. The definition of absolute pathname is system
	 * dependent. On UNIX systems, a pathname is absolute if its prefix is <code>"/"</code>. On Microsoft
	 * Windows systems, a pathname is absolute if its prefix is a drive specifier followed by <code>"\\"</code>
	 * , or if its prefix is <code>"\\\\"</code>.
	 * 
	 * @return <code>true</code> if this abstract pathname is absolute, <code>false</code> otherwise
	 */
	boolean isAbsolute();

	/**
	 * Returns the absolute pathname string of this abstract pathname.
	 * <p>
	 * If this abstract pathname is already absolute, then the pathname string is simply returned as if by the
	 * <code>{@link #getPath}</code> method. If this abstract pathname is the empty abstract pathname then the
	 * pathname string of the current user directory, which is named by the system property
	 * <code>user.dir</code>, is returned. Otherwise this pathname is resolved in a system-dependent way. On
	 * UNIX systems, a relative pathname is made absolute by resolving it against the current user directory.
	 * On Microsoft Windows systems, a relative pathname is made absolute by resolving it against the current
	 * directory of the drive named by the pathname, if any; if not, it is resolved against the current user
	 * directory.
	 * 
	 * @return The absolute pathname string denoting the same file or directory as this abstract pathname
	 * @throws SecurityException
	 *            If a required system property value cannot be accessed.
	 * @see java.io.File#isAbsolute()
	 */
	String getAbsolutePath();

	/**
	 * Returns the absolute form of this abstract pathname. Equivalent to
	 * <code>new&nbsp;File(this.{@link #getAbsolutePath}())</code>.
	 * 
	 * @return The absolute abstract pathname denoting the same file or directory as this abstract pathname
	 * @throws SecurityException
	 *            If a required system property value cannot be accessed.
	 * @since 1.2
	 */
	FileWrapper getAbsoluteFile();

	/**
	 * Returns the canonical pathname string of this abstract pathname.
	 * <p>
	 * A canonical pathname is both absolute and unique. The precise definition of canonical form is
	 * system-dependent. This method first converts this pathname to absolute form if necessary, as if by
	 * invoking the {@link #getAbsolutePath} method, and then maps it to its unique form in a system-dependent
	 * way. This typically involves removing redundant names such as <tt>"."</tt> and <tt>".."</tt> from the
	 * pathname, resolving symbolic links (on UNIX platforms), and converting drive letters to a standard case
	 * (on Microsoft Windows platforms).
	 * <p>
	 * Every pathname that denotes an existing file or directory has a unique canonical form. Every pathname
	 * that denotes a nonexistent file or directory also has a unique canonical form. The canonical form of the
	 * pathname of a nonexistent file or directory may be different from the canonical form of the same
	 * pathname after the file or directory is created. Similarly, the canonical form of the pathname of an
	 * existing file or directory may be different from the canonical form of the same pathname after the file
	 * or directory is deleted.
	 * 
	 * @return The canonical pathname string denoting the same file or directory as this abstract pathname
	 * @throws IOException
	 *            If an I/O error occurs, which is possible because the construction of the canonical pathname
	 *            may require filesystem queries
	 * @throws SecurityException
	 *            If a required system property value cannot be accessed, or if a security manager exists and
	 *            its <code>{@link java.lang.SecurityManager#checkRead}</code> method denies read access to the
	 *            file
	 * @since JDK1.1
	 */
	String getCanonicalPath() throws IOException;

	/**
	 * Returns the canonical form of this abstract pathname. Equivalent to
	 * <code>new&nbsp;File(this.{@link #getCanonicalPath}())</code>.
	 * 
	 * @return The canonical pathname string denoting the same file or directory as this abstract pathname
	 * @throws IOException
	 *            If an I/O error occurs, which is possible because the construction of the canonical pathname
	 *            may require filesystem queries
	 * @throws SecurityException
	 *            If a required system property value cannot be accessed, or if a security manager exists and
	 *            its <code>{@link
	 *          java.lang.SecurityManager#checkRead}</code> method denies read access to the
	 *            file
	 * @since 1.2
	 */
	FileWrapper getCanonicalFile() throws IOException;

	/**
	 * Converts this abstract pathname into a <code>file:</code> URL. The exact form of the URL is
	 * system-dependent. If it can be determined that the file denoted by this abstract pathname is a
	 * directory, then the resulting URL will end with a slash.
	 * <p>
	 * <b>Usage note:</b> This method does not automatically escape characters that are illegal in URLs. It is
	 * recommended that new code convert an abstract pathname into a URL by first converting it into a URI, via
	 * the {@link #toURI() toURI} method, and then converting the URI into a URL via the
	 * {@link java.net.URI#toURL() URI.toURL} method.
	 * 
	 * @return A URL object representing the equivalent file URL
	 * @throws MalformedURLException
	 *            If the path cannot be parsed as a URL
	 * @see #toURI()
	 * @see java.net.URI
	 * @see java.net.URI#toURL()
	 * @see java.net.URL
	 * @since 1.2
	 */
	URL toURL() throws MalformedURLException;

	/**
	 * Constructs a <tt>file:</tt> URI that represents this abstract pathname.
	 * <p>
	 * The exact form of the URI is system-dependent. If it can be determined that the file denoted by this
	 * abstract pathname is a directory, then the resulting URI will end with a slash.
	 * <p>
	 * For a given abstract pathname <i>f</i>, it is guaranteed that <blockquote><tt>
	 * new {@link #File(java.net.URI) File}(</tt><i>&nbsp;f</i><tt>.toURI()).equals(</tt><i>&nbsp;f</i>
	 * <tt>.{@link #getAbsoluteFile() getAbsoluteFile}())
	 * </tt></blockquote> so long as the original abstract pathname, the URI, and the new abstract pathname are
	 * all created in (possibly different invocations of) the same Java virtual machine. Due to the
	 * system-dependent nature of abstract pathnames, however, this relationship typically does not hold when a
	 * <tt>file:</tt> URI that is created in a virtual machine on one operating system is converted into an
	 * abstract pathname in a virtual machine on a different operating system.
	 * 
	 * @return An absolute, hierarchical URI with a scheme equal to <tt>"file"</tt>, a path representing this
	 *         abstract pathname, and undefined authority, query, and fragment components
	 * @see #File(java.net.URI)
	 * @see java.net.URI
	 * @see java.net.URI#toURL()
	 * @since 1.4
	 */
	URI toURI();

	/**
	 * Tests whether the application can read the file denoted by this abstract pathname.
	 * 
	 * @return <code>true</code> if and only if the file specified by this abstract pathname exists
	 *         <em>and</em> can be read by the application; <code>false</code> otherwise
	 * @throws SecurityException
	 *            If a security manager exists and its <code>{@link
	 *          java.lang.SecurityManager#checkRead(java.lang.String)}</code>
	 *            method denies read access to the file
	 */
	boolean canRead();

	/**
	 * Tests whether the application can modify the file denoted by this abstract pathname.
	 * 
	 * @return <code>true</code> if and only if the file system actually contains a file denoted by this
	 *         abstract pathname <em>and</em> the application is allowed to write to the file;
	 *         <code>false</code> otherwise.
	 * @throws SecurityException
	 *            If a security manager exists and its <code>{@link
	 *          java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
	 *            method denies write access to the file
	 */
	boolean canWrite();

	/**
	 * Tests whether the file or directory denoted by this abstract pathname exists.
	 * 
	 * @return <code>true</code> if and only if the file or directory denoted by this abstract pathname exists;
	 *         <code>false</code> otherwise
	 * @throws SecurityException
	 *            If a security manager exists and its <code>{@link
	 *          java.lang.SecurityManager#checkRead(java.lang.String)}</code>
	 *            method denies read access to the file or directory
	 */
	boolean exists();

	/**
	 * Tests whether the file denoted by this abstract pathname is a directory.
	 * 
	 * @return <code>true</code> if and only if the file denoted by this abstract pathname exists <em>and</em>
	 *         is a directory; <code>false</code> otherwise
	 * @throws SecurityException
	 *            If a security manager exists and its <code>{@link
	 *          java.lang.SecurityManager#checkRead(java.lang.String)}</code>
	 *            method denies read access to the file
	 */
	boolean isDirectory();

	/**
	 * Tests whether the file denoted by this abstract pathname is a normal file. A file is <em>normal</em> if
	 * it is not a directory and, in addition, satisfies other system-dependent criteria. Any non-directory
	 * file created by a Java application is guaranteed to be a normal file.
	 * 
	 * @return <code>true</code> if and only if the file denoted by this abstract pathname exists <em>and</em>
	 *         is a normal file; <code>false</code> otherwise
	 * @throws SecurityException
	 *            If a security manager exists and its <code>{@link
	 *          java.lang.SecurityManager#checkRead(java.lang.String)}</code>
	 *            method denies read access to the file
	 */
	boolean isFile();

	/**
	 * Tests whether the file named by this abstract pathname is a hidden file. The exact definition of
	 * <em>hidden</em> is system-dependent. On UNIX systems, a file is considered to be hidden if its name
	 * begins with a period character (<code>'.'</code>). On Microsoft Windows systems, a file is considered to
	 * be hidden if it has been marked as such in the filesystem.
	 * 
	 * @return <code>true</code> if and only if the file denoted by this abstract pathname is hidden according
	 *         to the conventions of the underlying platform
	 * @throws SecurityException
	 *            If a security manager exists and its <code>{@link
	 *          java.lang.SecurityManager#checkRead(java.lang.String)}</code>
	 *            method denies read access to the file
	 * @since 1.2
	 */
	boolean isHidden();

	/**
	 * Returns the time that the file denoted by this abstract pathname was last modified.
	 * 
	 * @return A <code>long</code> value representing the time the file was last modified, measured in
	 *         milliseconds since the epoch (00:00:00 GMT, January 1, 1970), or <code>0L</code> if the file
	 *         does not exist or if an I/O error occurs
	 * @throws SecurityException
	 *            If a security manager exists and its <code>{@link
	 *          java.lang.SecurityManager#checkRead(java.lang.String)}</code>
	 *            method denies read access to the file
	 */
	long lastModified();

	/**
	 * Returns the length of the file denoted by this abstract pathname. The return value is unspecified if
	 * this pathname denotes a directory.
	 * 
	 * @return The length, in bytes, of the file denoted by this abstract pathname, or <code>0L</code> if the
	 *         file does not exist
	 * @throws SecurityException
	 *            If a security manager exists and its <code>{@link
	 *          java.lang.SecurityManager#checkRead(java.lang.String)}</code>
	 *            method denies read access to the file
	 */
	long length();

	/**
	 * Atomically creates a new, empty file named by this abstract pathname if and only if a file with this
	 * name does not yet exist. The check for the existence of the file and the creation of the file if it does
	 * not exist are a single operation that is atomic with respect to all other filesystem activities that
	 * might affect the file.
	 * <P>
	 * Note: this method should <i>not</i> be used for file-locking, as the resulting protocol cannot be made
	 * to work reliably. The {@link java.nio.channels.FileLock FileLock} facility should be used instead.
	 * 
	 * @return <code>true</code> if the named file does not exist and was successfully created;
	 *         <code>false</code> if the named file already exists
	 * @throws IOException
	 *            If an I/O error occurred
	 * @throws SecurityException
	 *            If a security manager exists and its <code>
	 *            {@link java.lang.SecurityManager#checkWrite(java.lang.String)}</code> method denies write
	 *            access to the file
	 * @since 1.2
	 */
	boolean createNewFile() throws IOException;

	/**
	 * Deletes the file or directory denoted by this abstract pathname. If this pathname denotes a directory,
	 * then the directory must be empty in order to be deleted.
	 * 
	 * @return <code>true</code> if and only if the file or directory is successfully deleted;
	 *         <code>false</code> otherwise
	 * @throws SecurityException
	 *            If a security manager exists and its <code>{@link
	 *          java.lang.SecurityManager#checkDelete}</code>
	 *            method denies delete access to the file
	 */
	boolean delete();

	/**
	 * Requests that the file or directory denoted by this abstract pathname be deleted when the virtual
	 * machine terminates. Deletion will be attempted only for normal termination of the virtual machine, as
	 * defined by the Java Language Specification.
	 * <p>
	 * Once deletion has been requested, it is not possible to cancel the request. This method should therefore
	 * be used with care.
	 * <P>
	 * Note: this method should <i>not</i> be used for file-locking, as the resulting protocol cannot be made
	 * to work reliably. The {@link java.nio.channels.FileLock FileLock} facility should be used instead.
	 * 
	 * @throws SecurityException
	 *            If a security manager exists and its <code>{@link java.lang.SecurityManager#checkDelete}
	 *            </code> method denies delete access to the file
	 * @see #delete
	 * @since 1.2
	 */
	void deleteOnExit();

	/**
	 * Returns an array of strings naming the files and directories in the directory denoted by this abstract
	 * pathname.
	 * <p>
	 * If this abstract pathname does not denote a directory, then this method returns <code>null</code>.
	 * Otherwise an array of strings is returned, one for each file or directory in the directory. Names
	 * denoting the directory itself and the directory's parent directory are not included in the result. Each
	 * string is a file name rather than a complete path.
	 * <p>
	 * There is no guarantee that the name strings in the resulting array will appear in any specific order;
	 * they are not, in particular, guaranteed to appear in alphabetical order.
	 * 
	 * @return An array of strings naming the files and directories in the directory denoted by this abstract
	 *         pathname. The array will be empty if the directory is empty. Returns <code>null</code> if this
	 *         abstract pathname does not denote a directory, or if an I/O error occurs.
	 * @throws SecurityException
	 *            If a security manager exists and its <code>
	 *            {@link java.lang.SecurityManager#checkRead(java.lang.String)}</code> method denies read
	 *            access to the directory
	 */
	String[] list();

	/**
	 * Returns an array of strings naming the files and directories in the directory denoted by this abstract
	 * pathname that satisfy the specified filter. The behavior of this method is the same as that of the
	 * <code>{@link #list()}</code> method, except that the strings in the returned array must satisfy the
	 * filter. If the given <code>filter</code> is <code>null</code> then all names are accepted. Otherwise, a
	 * name satisfies the filter if and only if the value <code>true</code> results when the <code>{@link
	 * FilenameFilter#accept}</code>
	 * method of the filter is invoked on this abstract pathname and the name of a file or directory in the
	 * directory that it denotes.
	 * 
	 * @param filter
	 *           A filename filter
	 * @return An array of strings naming the files and directories in the directory denoted by this abstract
	 *         pathname that were accepted by the given <code>filter</code>. The array will be empty if the
	 *         directory is empty or if no names were accepted by the filter. Returns <code>null</code> if this
	 *         abstract pathname does not denote a directory, or if an I/O error occurs.
	 * @throws SecurityException
	 *            If a security manager exists and its <code>{@link
	 *          java.lang.SecurityManager#checkRead(java.lang.String)}</code>
	 *            method denies read access to the directory
	 */
	String[] list(FilenameFilter filter);

	/**
	 * Returns an array of abstract pathnames denoting the files in the directory denoted by this abstract
	 * pathname.
	 * <p>
	 * If this abstract pathname does not denote a directory, then this method returns <code>null</code>.
	 * Otherwise an array of <code>File</code> objects is returned, one for each file or directory in the
	 * directory. Pathnames denoting the directory itself and the directory's parent directory are not included
	 * in the result. Each resulting abstract pathname is constructed from this abstract pathname using the
	 * <code>{@link #File(java.io.File, java.lang.String)
	 * File(File,&nbsp;String)}</code> constructor. Therefore if this pathname is absolute then each resulting
	 * pathname is absolute; if this pathname is relative then each resulting pathname will be relative to the
	 * same directory.
	 * <p>
	 * There is no guarantee that the name strings in the resulting array will appear in any specific order;
	 * they are not, in particular, guaranteed to appear in alphabetical order.
	 * 
	 * @return An array of abstract pathnames denoting the files and directories in the directory denoted by
	 *         this abstract pathname. The array will be empty if the directory is empty. Returns
	 *         <code>null</code> if this abstract pathname does not denote a directory, or if an I/O error
	 *         occurs.
	 * @throws SecurityException
	 *            If a security manager exists and its <code>
	 *            {@link java.lang.SecurityManager#checkRead(java.lang.String)}</code> method denies read
	 *            access to the directory
	 * @since 1.2
	 */
	FileWrapper[] listFiles();

	/**
	 * Returns an array of abstract pathnames denoting the files and directories in the directory denoted by
	 * this abstract pathname that satisfy the specified filter. The behavior of this method is the same as
	 * that of the <code>{@link #listFiles()}</code> method, except that the pathnames in the returned array
	 * must satisfy the filter. If the given <code>filter</code> is <code>null</code> then all pathnames are
	 * accepted. Otherwise, a pathname satisfies the filter if and only if the value <code>true</code> results
	 * when the <code>{@link FilenameFilter#accept}</code> method of the filter is invoked on this abstract
	 * pathname and the name of a file or directory in the directory that it denotes.
	 * 
	 * @param filter
	 *           A filename filter
	 * @return An array of abstract pathnames denoting the files and directories in the directory denoted by
	 *         this abstract pathname. The array will be empty if the directory is empty. Returns
	 *         <code>null</code> if this abstract pathname does not denote a directory, or if an I/O error
	 *         occurs.
	 * @throws SecurityException
	 *            If a security manager exists and its <code>{@link
	 *          java.lang.SecurityManager#checkRead(java.lang.String)}</code>
	 *            method denies read access to the directory
	 * @since 1.2
	 */
	FileWrapper[] listFiles(FilenameFilter filter);

	/**
	 * Returns an array of abstract pathnames denoting the files and directories in the directory denoted by
	 * this abstract pathname that satisfy the specified filter. The behavior of this method is the same as
	 * that of the <code>{@link #listFiles()}</code> method, except that the pathnames in the returned array
	 * must satisfy the filter. If the given <code>filter</code> is <code>null</code> then all pathnames are
	 * accepted. Otherwise, a pathname satisfies the filter if and only if the value <code>true</code> results
	 * when the <code>{@link FileFilter#accept(java.io.File)}</code> method of the filter is invoked on the
	 * pathname.
	 * 
	 * @param filter
	 *           A file filter
	 * @return An array of abstract pathnames denoting the files and directories in the directory denoted by
	 *         this abstract pathname. The array will be empty if the directory is empty. Returns
	 *         <code>null</code> if this abstract pathname does not denote a directory, or if an I/O error
	 *         occurs.
	 * @throws SecurityException
	 *            If a security manager exists and its <code>{@link
	 *          java.lang.SecurityManager#checkRead(java.lang.String)}</code>
	 *            method denies read access to the directory
	 * @since 1.2
	 */
	FileWrapper[] listFiles(FileFilter filter);

	/**
	 * Creates the directory named by this abstract pathname.
	 * 
	 * @return <code>true</code> if and only if the directory was created; <code>false</code> otherwise
	 * @throws SecurityException
	 *            If a security manager exists and its <code>{@link
	 *          java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
	 *            method does not permit the named directory to be created
	 */
	boolean mkdir();

	/**
	 * Creates the directory named by this abstract pathname, including any necessary but nonexistent parent
	 * directories. Note that if this operation fails it may have succeeded in creating some of the necessary
	 * parent directories.
	 * 
	 * @return <code>true</code> if and only if the directory was created, along with all necessary parent
	 *         directories; <code>false</code> otherwise
	 * @throws SecurityException
	 *            If a security manager exists and its <code>{@link
	 *          java.lang.SecurityManager#checkRead(java.lang.String)}</code>
	 *            method does not permit verification of the existence of the named directory and all necessary
	 *            parent directories; or if the <code>{@link 
	 *          java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
	 *            method does not permit the named directory and all necessary parent directories to be created
	 */
	boolean mkdirs();

	/**
	 * Renames the file denoted by this abstract pathname.
	 * <p>
	 * Many aspects of the behavior of this method are inherently platform-dependent: The rename operation
	 * might not be able to move a file from one filesystem to another, it might not be atomic, and it might
	 * not succeed if a file with the destination abstract pathname already exists. The return value should
	 * always be checked to make sure that the rename operation was successful.
	 * 
	 * @param dest
	 *           The new abstract pathname for the named file
	 * @return <code>true</code> if and only if the renaming succeeded; <code>false</code> otherwise
	 * @throws SecurityException
	 *            If a security manager exists and its <code>
	 *            {@link java.lang.SecurityManager#checkWrite(java.lang.String)}</code> method denies write
	 *            access to either the old or new pathnames
	 * @throws NullPointerException
	 *            If parameter <code>dest</code> is <code>null</code>
	 */
	boolean renameTo(FileWrapper dest);

	/**
	 * Sets the last-modified time of the file or directory named by this abstract pathname.
	 * <p>
	 * All platforms support file-modification times to the nearest second, but some provide more precision.
	 * The argument will be truncated to fit the supported precision. If the operation succeeds and no
	 * intervening operations on the file take place, then the next invocation of the
	 * <code>{@link #lastModified}</code> method will return the (possibly truncated) <code>time</code>
	 * argument that was passed to this method.
	 * 
	 * @param time
	 *           The new last-modified time, measured in milliseconds since the epoch (00:00:00 GMT, January 1,
	 *           1970)
	 * @return <code>true</code> if and only if the operation succeeded; <code>false</code> otherwise
	 * @throws IllegalArgumentException
	 *            If the argument is negative
	 * @throws SecurityException
	 *            If a security manager exists and its <code>
	 *            {@link java.lang.SecurityManager#checkWrite(java.lang.String)}</code> method denies write
	 *            access to the named file
	 * @since 1.2
	 */
	boolean setLastModified(long time);

	/**
	 * Marks the file or directory named by this abstract pathname so that only read operations are allowed.
	 * After invoking this method the file or directory is guaranteed not to change until it is either deleted
	 * or marked to allow write access. Whether or not a read-only file or directory may be deleted depends
	 * upon the underlying system.
	 * 
	 * @return <code>true</code> if and only if the operation succeeded; <code>false</code> otherwise
	 * @throws SecurityException
	 *            If a security manager exists and its <code>{@link
	 *          java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
	 *            method denies write access to the named file
	 * @since 1.2
	 */
	boolean setReadOnly();

	/**
	 * Tests this abstract pathname for equality with the given object. Returns <code>true</code> if and only
	 * if the argument is not <code>null</code> and is an abstract pathname that denotes the same file or
	 * directory as this abstract pathname. Whether or not two abstract pathnames are equal depends upon the
	 * underlying system. On UNIX systems, alphabetic case is significant in comparing pathnames; on Microsoft
	 * Windows systems it is not.
	 * 
	 * @param obj
	 *           The object to be compared with this abstract pathname
	 * @return <code>true</code> if and only if the objects are the same; <code>false</code> otherwise
	 */
	boolean equals(Object obj);

	/**
	 * Computes a hash code for this abstract pathname. Because equality of abstract pathnames is inherently
	 * system-dependent, so is the computation of their hash codes. On UNIX systems, the hash code of an
	 * abstract pathname is equal to the exclusive <em>or</em> of the hash code of its pathname string and the
	 * decimal value <code>1234321</code>. On Microsoft Windows systems, the hash code is equal to the
	 * exclusive <em>or</em> of the hash code of its pathname string converted to lower case and the decimal
	 * value <code>1234321</code>.
	 * 
	 * @return A hash code for this abstract pathname
	 */
	int hashCode();

	/**
	 * Returns the pathname string of this abstract pathname. This is just the string returned by the
	 * <code>{@link #getPath}</code> method.
	 * 
	 * @return The string form of this abstract pathname
	 */
	String toString();

	/**
	 * Returns a FileInputStream constructed using the File object that this FileWrapper implementation wraps.
	 * 
	 * @return a new FileInputStream
	 * @throws FileNotFoundException
	 *            if the wrapped File could not be found to build an InputStream from.
	 */
	FileInputStream getFileInputStream() throws FileNotFoundException;

	/**
	 * Returns a FileOutputStream constructed using the File object that this FileWrapper implementation wraps.
	 * 
	 * @return a new FileOutputStream
	 * @throws FileNotFoundException
	 *            if the wrapped File could not be found to build an OutputStream to.
	 */
	FileOutputStream getFileOutputStream() throws FileNotFoundException;

	/**
	 * Returns a FileWriter constructed using the File object that this FileWrapper implementation wraps.
	 * 
	 * @return a new FileWriter
	 * @throws IOException
	 */
	FileWriter getFileWriter() throws IOException;

	/**
	 * Returns a FileWriter constructed using the File object that this FileWrapper implementation wraps.
	 * 
	 * @return a new FileReader
	 * @throws IOException
	 */
	FileReader getFileReader() throws IOException;
	
	/**
	 * Returns a BufferedReader constructed using the File object that this FileWrapper implementation wraps.
	 * 
	 * @return a new BufferedReader
	 * @throws IOException
	 */
	BufferedReader getBufferedReader() throws IOException;
	
	/**
	 * Returns a PrintWriter constructed using the File object that this FileWrapper implementation wraps.
	 * 
	 * @return a new PrintWriter
	 * @throws IOException
	 */	
	PrintWriter getPrintWriter() throws IOException;
}