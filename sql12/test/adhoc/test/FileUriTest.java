/*
 * Copyright (C) 2009 Rob Manning
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
package test;

import java.io.File;
import java.net.URL;

/**
 * Test driver which demonstrates the problem code for bug :
 * 
 * 2480365: Plugin loading fails when installdir has spaces
 * 
 * Pass the quoted absolute to a valid file on WinXP that has spaces in the path and test 2 (File from URI)
 * creates a file that returns false for exists().  test 3 (2nd File from URI) creates a File that 
 * correctly returns true for exists(). 
 */
public class FileUriTest
{

	public static void main(String args[]) throws Exception {
		File f = new File(args[0]);
		if (f.exists()) {
			System.out.println("File ("+args[0]+") exists");
		} else {
			System.out.println("File ("+args[0]+") does not exist");
		}
		URL url = f.toURI().toURL();
		
		// BAD - Converting a URL to a file this way is problematic when the file path contains spaces
		File f2 = new File(url.getFile());
		if (f2.exists()) {
			System.out.println("File from URI ("+url.getFile()+") exists");
		} else {
			System.out.println("File from URI ("+url.getFile()+") does not exist");
		}

		// GOOD - Converting a URL to a file this way is fine when the file path contains spaces
		File f3 = new File(url.toURI());
		if (f3.exists()) {
			System.out.println("2nd File from URI ("+f3.getAbsolutePath()+") exists");
		} else {
			System.out.println("2nd File from URI ("+f3.getAbsolutePath()+") does not exist");
		}
		
	}
}
