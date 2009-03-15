import java.util.Enumeration;
import java.util.Properties;

import javax.swing.JOptionPane;

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

/**
 * The purpose of this class is to provide a Java2 (1.2.x) compatible class that can simply check the 
 * JVM version and put up a dialog message if the version isn't sufficiently recent.  This class should be 
 * compiled and jar'd with JDK 1.2.2 so that any JVM 1.2.2 and higher can execute it.  This is so that the 
 * user sees a nice informative message telling them their JVM is old, instead of the dreaded :
 * 
 *  Cannot find main class. Program will exit.
 *  
 */
public class JavaVersionChecker
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Properties props = System.getProperties();
		Enumeration e = props.keys();
		byte lineSep = Character.LINE_SEPARATOR;
		
		while (e.hasMoreElements()) {
			System.out.println(e.nextElement());
		}
		
		String jvmVersion = System.getProperty("java.version");
		if (!jvmVersion.startsWith("1.6") && !jvmVersion.startsWith("1.7")) {
			String javaHome = System.getProperty("java.home");
			JOptionPane.showMessageDialog(null, 
				"Your Java Virtual Machine must be at least 1.6 to run SQuirreL 3.x and above" + lineSep +				
				"  JVM Version used: "+jvmVersion+ lineSep +
				"  JVM Location: "+javaHome);
			System.exit(1);
		}
		System.exit(0);
	}

}
