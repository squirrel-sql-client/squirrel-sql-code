/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * <code>ThemeDescription</code> describes a TinyLaF theme by
 * its URL and its name.
 * <p>
 * You can call
 * {@link de.muntjak.tinylookandfeel.Theme#getAvailableThemes()}
 * to get an array of <code>ThemeDescription</code> objects, each
 * describing a unique TinyLaF theme resource.
 * 
 * @author Hans Bickel
 * @version 1.0
 */
public class ThemeDescription {
	
	private URI uri;
	private URL url;
	private File file;
	private boolean fileHasBeenSet = false;
	private String name = null;

	/**
	 * Constructs a <code>ThemeDescription</code> using the specified URL.
	 * @param url a non-null URL
	 * @throws IllegalArgumentException if argument is <code>null</code>
	 */
	public ThemeDescription(URL url) {
		if(url == null) {
			throw new IllegalArgumentException("url may not be null");
		}

		try {
			// Note: When using getPath() the "jar:" prefix
			// of JAR URLs is omitted
			if(!"file".equals(url.getProtocol())) {
				uri = new URI(url.getPath());
			}
			else {
				uri = new URI(url.toExternalForm());
			}
			
			// no error
			this.url = url;
		}
		catch(URISyntaxException ex) {
			uri = null;
		}
	}
	
	/**
	 * Constructs a <code>ThemeDescription</code> using the specified URI.
	 * <p>
	 * Note: You can get an URI from a file by calling <code>File.toURI()</code>.
	 * @param uri a non-null URI
	 * @throws IllegalArgumentException if argument is <code>null</code>
	 */
	public ThemeDescription(URI uri) {
		if(uri == null) {
			throw new IllegalArgumentException("uri may not be null");
		}

		try {
			url = uri.toURL();
			
			// no error
			this.uri = uri;
		}
		catch(MalformedURLException ex) {
			url = null;
		}
	}
	
	/**
	 * Two <code>ThemeDescription</code> objects are equal if
	 * their URIs are equal.
	 */
	public boolean equals(Object o) {
		if(o == this) return true;
		if(o == null || !(o instanceof ThemeDescription)) return false;
		
		ThemeDescription other = (ThemeDescription)o;
		
		if(isValid() != other.isValid()) return false;
		
		if(isValid()) return uri.equals(other.uri);
		
		// both are invalid -> null equals null
		return true;
	}
	
	/**
	 * Returns <code>true</code> if the <code>URI</code> or <code>URL</code>
	 * argument given at construction time was valid,
	 * <code>false</code> otherwise.
	 * @return <code>true</code> if the <code>URI</code> or <code>URL</code>
	 * argument given at construction time was valid,
	 * <code>false</code> otherwise.
	 */
	public boolean isValid() {
		return (uri != null && url != null);
	}
	
	/**
	 * Returns the name of the TinyLaF theme, for example: &quot;Golden&quot; 
	 * (for a URL of <code>.../Golden.theme</code>).
	 * The returned string will be <code>null</code> if the <code>URL</code> or
	 * <code>URI</code> specified at construction time was invalid.
	 * @return the name of the TinyLaF theme
	 */
	public String getName() {
		if(uri == null) return "? URI == null ?";
		
		if(name == null) {
			name = getName(uri.getPath());
		}
		
		return name;
	}
	
	private static String getName(String path) {
		if(path == null) return "? uri.getPath() == null ?";
		
		String namePart = path.substring(path.lastIndexOf("/") + 1);
		int index = namePart.indexOf(".");
		
		if(index == -1) return namePart;
		
		return namePart.substring(0, index);
	}
	
	/**
	 * Returns the URL of the TinyLaF theme. Note that,
	 * if {@link #isValid()} returns <code>false</code>,
	 * the returned URL is probably <code>null</code>.
	 * @return the URL of the TinyLaF theme.
	 */
	public URL getURL() {
		return url;
	}
	
	/**
	 * Returns <code>true</code> if the URI or URL
	 * argument given at construction time was a file URI/URL,
	 * <code>false</code> otherwise.
	 * @return <code>true</code> if the URI or URL
	 * argument given at construction time was a file URI/URL,
	 * <code>false</code> otherwise.
	 */
	public boolean isFile() {
		if(!isValid()) return false;
		if(Theme.YQ_URL.equals(url)) return false;

		return "file".equals(url.getProtocol());
	}
	
	/**
	 * Returns the theme file or <code>null</code>.
	 * @return the theme file or <code>null</code>.
	 * @see #isFile()
	 */
	public File getFile() {
		if(fileHasBeenSet) return file;
		
		fileHasBeenSet = true;
		
		if(!isFile()) return null;	// file stays null

		try {
			file = new File(uri);
		}
		catch(IllegalArgumentException ex) {
			System.err.println(getClass().getName() + ".getFile() " + ex.toString());
			System.err.println("URI=" + uri + "\nURL=" + url);
		}
		
		return file;
	}
	
	/**
	 * Returns the same string as would be returned from {@link #getName()}.
	 */
	public String toString() {
		return getName();
	}
}
