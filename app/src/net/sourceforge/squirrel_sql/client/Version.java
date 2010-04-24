package net.sourceforge.squirrel_sql.client;
/*
 * Copyright (C) 2001-2006 Colin Bell
 * colbell@users.sourceforge.net
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
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
/**
 * Application version information.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class Version
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(Version.class);

	private static final String APP_NAME = s_stringMgr.getString("Version.appname");
	private static final int MAJOR_VERSION = 3;
	private static final int MINOR_VERSION = 1;
	private static final int RELEASE = 1;

	private static final String COPYRIGHT = s_stringMgr.getString("Version.copyright");

	private static final String WEB_SITE = s_stringMgr.getString("Version.website");

	public static String getApplicationName()
	{
		return APP_NAME;
	}

	public static String getShortVersion()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(MAJOR_VERSION)
			.append(".")
			.append(MINOR_VERSION);
			
		if (RELEASE != 0)
		{
            buf.append(".");
			buf.append(RELEASE);
		}
		return buf.toString();
	}

	public static String getVersion()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(APP_NAME)
			.append(" Version ")
			.append(getShortVersion());
		return buf.toString();
	}

	/**
	 * Returns a boolean value indicating whether or not this is a snapshot release or a stable release.
	 * 
	 * @return true if this is snapshot release; false otherwise.  Note: Since snapshot releases replace this
	 * version with Version.java.template, this version should always return false.  This makes it possible to 
	 * determine what channel to use for updates when SQuirreL is installed for the very first time and no 
	 * preference is set.  After that the channel can be changed by the user at will.
	 */
	public static boolean isSnapshotVersion() {
		return false;
	}
	
	public static String getCopyrightStatement()
	{
		return COPYRIGHT;
	}

	public static String getWebSite()
	{
		return WEB_SITE;
	}

   public static boolean supportsUsedJDK()
   {
      String vmVer = System.getProperty("java.vm.version");

      if(   vmVer.startsWith("0")
         || vmVer.startsWith("1.0")
         || vmVer.startsWith("1.1")
         || vmVer.startsWith("1.2")
         || vmVer.startsWith("1.3")
         || vmVer.startsWith("1.4")
         || vmVer.startsWith("1.5"))
      {
         return false;
      }
      else
      {
         return true;
      }
   }

   public static String getUnsupportedJDKMessage()
   {
      String[] params = new String[]
         {
            System.getProperty("java.vm.version"),
            System.getProperty("java.home")
         };

      return s_stringMgr.getString("Application.error.unsupportedJDKVersion", params);
   }

   public static boolean isJDK14()
   {
      String vmVer = System.getProperty("java.vm.version");

      if(vmVer.startsWith("1.4"))
      {
         return true;
      }
      else
      {
         return false;
      }
   }
   
   public static boolean isJDK16OrAbove()
   {
      String vmVer = System.getProperty("java.vm.version").substring(0, 3);

      if(vmVer.compareTo("1.6") >= 0)
      {
         return true;
      }
      else
      {
         return false;
      }
   }

}
