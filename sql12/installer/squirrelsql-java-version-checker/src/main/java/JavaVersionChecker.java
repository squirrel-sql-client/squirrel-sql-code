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
	/** The system property which gives the install location of the java that this small app runs in */ 
	private static final String JAVA_HOME_PROPERTY = "java.home";
	
	/** The system property which gives the version of java that this small application runs in */
	private static final String JAVA_VERSION_PROPERTY = "java.version";

	/**
	 * @param args should contain at least one version of Java which is the acceptable minimum version.  If 
	 * that version is not the latest version of Java available, then newer versions (which are also 
	 * acceptable) should also be specified after the minimum acceptable version.
	 */
	public static void main(String[] args)
	{
		if (args.length == 0) {
			System.err.println("JavaVersionChecker: Must specify one or more minimum JVM versions");
			System.exit(1);
		}
				
		String jvmVersion = System.getProperty(JAVA_VERSION_PROPERTY);
		if (!checkVersion(jvmVersion, args)) {
			String javaHome = System.getProperty(JAVA_HOME_PROPERTY);
			JOptionPane.showMessageDialog(null, 
				"Your Java Virtual Machine must be at least "+args[0]+" to run SQuirreL 3.x and above\n" +				
				"  JVM Version used: "+jvmVersion+ "\n" +
				"  JVM Location: "+javaHome);
			System.exit(1);
		}
		System.exit(0);
	}
	
	/**
	 * Check that the specified JVM version matches one of the jvm versions specified in minimumJavaVersions.
	 * 
	 * @param jvmVersion the version of the JVM that this app runs in  
	 * @param minimumJavaVersions one or more acceptable versions of the JVM
	 * @return true if the JVM matches one or more of the minimum JVM versions; false otherwise.
	 */
	private static boolean checkVersion(String jvmVersion, String[] minimumJavaVersions) {
		
		if (jvmVersion == null) {
			System.err.println("jvm version could not be determined. The "+JAVA_VERSION_PROPERTY+
				" system property is null");
		}
		
		boolean result = false;
		for (int i = 0; i < minimumJavaVersions.length; i++) {
			if (jvmVersion.startsWith(minimumJavaVersions[i])) {
				result = true;
			}
		}
		return result;
	}

}
