package net.sourceforge.squirrel_sql.fw.util;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.NumberFormat;
/**
 * General purpose utilities functions.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class Utilities
{
	private static final String EMPTY_STRING = "";

	/**
	 * Ctor. <TT>private</TT> as all methods are static.
	 */
	private Utilities()
	{
		super();
	}

	/**
	 * Print the current stack trace to <TT>ps</TT>.
	 *
	 * @param	ps	The <TT>PrintStream</TT> to print stack trace to.
	 *
	 * @throws	IllegalArgumentException	If a null <TT>ps</TT> passed.
	 */
	public static void printStackTrace(PrintStream ps)
	{
		if (ps == null)
		{
			throw new IllegalArgumentException("PrintStream == null");
		}

		try
		{
			throw new Exception();
		}
		catch (Exception ex)
		{
			ps.println(getStackTrace(ex));
		}
	}

	/**
	 * Return the stack trace from the passed exception as a string
	 *
	 * @param   th  The exception to retrieve stack trace for.
	 */
	public static String getStackTrace(Throwable th)
	{
		if (th == null)
		{
			throw new IllegalArgumentException("Throwable == null");
		}

		StringWriter sw = new StringWriter();
		try
		{
			PrintWriter pw = new PrintWriter(sw);
			try
			{
				th.printStackTrace(pw);
				return sw.toString();
			}
			finally
			{
				pw.close();
			}
		}
		finally
		{
			try
			{
				sw.close();
			}
			catch (IOException ignore)
			{
			}
		}
	}

	/**
	 * Change the passed class name to its corresponding file name. E.G.
	 * change &quot;Utilities&quot; to &quot;Utilities.class&quot;.
	 *
	 * @param	name	Class name to be changed.
	 *
	 * @throws	IllegalArgumentException	If a null <TT>name</TT> passed.
	 */
	public static String changeClassNameToFileName(String name)
	{
		if (name == null)
		{
			throw new IllegalArgumentException("Class Name == null");
		}
		return name.replace('.', '/').concat(".class");
	}

	/**
	 * Change the passed file name to its corresponding class name. E.G.
	 * change &quot;Utilities.class&quot; to &quot;Utilities&quot;.
	 *
	 * @param	name	Class name to be changed. If this does not represent
	 *					a Java class then <TT>null</TT> is returned.
	 *
	 * @throws  IllegalArgumentException	If a null <TT>name</TT> passed.
	 */
	public static String changeFileNameToClassName(String name)
	{
		if (name == null)
		{
			throw new IllegalArgumentException("File Name == null");
		}
		String className = null;
		if (name.toLowerCase().endsWith(".class"))
		{
			className = name.replace('/', '.');
			className = className.replace('\\', '.');
			className = className.substring(0, className.length() - 6);
		}
		return className;
	}

	/**
	 * Clean the passed string. Replace new line characters and tabs with
	 * single spaces. If a <TT>null</TT> string passed return an empty
	 * string.
	 *
	 * @param	str	String to be cleaned.
	 *
	 * @return	Cleaned string.
	 */
	public static String cleanString(String str)
	{
		if (str == null)
		{
			return EMPTY_STRING;
		}
		String newStr = str.replace('\n', ' ');
		newStr = newStr.replace('\r', ' ');
		newStr = newStr.replace('\t', ' ');
		return newStr;
	}

	/**
	 * Return whether the 2 passed strings are equal. This function
	 * allows for <TT>null</TT> strings. If <TT>s1</TT> and <TT>s1</TT> are
	 * both <TT>null</TT> they are considered equal.
	 */
	public static boolean areStringsEqual(String s1, String s2)
	{
		if (s1 == null && s2 == null)
		{
			return true;
		}
		if (s1 != null)
		{
			return s1.equals(s2);
		}
		return s2.equals(s1);
	}

	/**
	 * Return the suffix of the passed file name.
	 *
	 * @param	fileName	File name to retrieve suffix for.
	 *
	 * @return	Suffix for <TT>fileName</TT> or an empty string
	 * 			if unable to get the suffix.
	 *
	 * @throws	IllegalArgumentException	if <TT>null</TT> file name passed.
	 */
	public static String getFileNameSuffix(String fileName)
	{
		if (fileName == null)
		{
			throw new IllegalArgumentException("file name == null");
		}
		int pos = fileName.lastIndexOf('.');
		if (pos > 0 && pos < fileName.length() - 1)
		{
			return fileName.substring(pos + 1);
		}
		return EMPTY_STRING;
	}


	/**
	 * Remove the suffix from the passed file name.
	 *
	 * @param	fileName	File name to remove suffix from.
	 *
	 * @return	<TT>fileName</TT> without a suffix.
	 *
	 * @throws	IllegalArgumentException	if <TT>null</TT> file name passed.
	 */
	public static String removeFileNameSuffix(String fileName)
	{
		if (fileName == null)
		{
			throw new IllegalArgumentException("file name == null");
		}
		int pos = fileName.lastIndexOf('.');
		if (pos > 0 && pos < fileName.length() - 1)
		{
			return fileName.substring(0, pos);
		}
		return fileName;
	}

	public static String formatSize(long longSize)
	{
		return formatSize(longSize, -1);
	}

	public static String formatSize(long longSize, int decimalPos)
	{
		NumberFormat fmt = NumberFormat.getNumberInstance();
		if (decimalPos >= 0)
		{
			fmt.setMaximumFractionDigits(decimalPos);
		}
		final double size = longSize;
		double val = size / (1024 * 1024);
		if (val > 1)
		{
			return fmt.format(val).concat(" MB");
		}
		val = size / 1024;
		if (val > 10)
		{
			return fmt.format(val).concat(" KB");
		}
		return fmt.format(val).concat(" bytes");
	}
}
