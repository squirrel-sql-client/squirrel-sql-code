package net.sourceforge.squirrel_sql.fw.util;
/*
 * Copyright (C) 2001-2003 Colin Bell
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * General purpose utilities functions.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class Utilities
{
   /** Logger for this class. */
   private static ILogger s_log =
      LoggerController.createLogger(Utilities.class);

    private static Pattern spanStartPattern = Pattern.compile(".*\\<span\\>.*");
    private static Pattern spanStartSplitPattern = Pattern.compile("\\<span\\>");
    private static Pattern spanEndPattern = Pattern.compile(".*<\\/span\\>.*");
    private static Pattern spanEndSplitPattern = Pattern.compile("<\\/span\\>");
	
   
   
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
    * @param	th	The exception to retrieve stack trace for.
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
         catch (IOException ex)
         {
            s_log.error("Unexpected error closing StringWriter", ex);
         }
      }
   }

   public static Throwable getDeepestThrowable(Throwable t)
   {
      Throwable parent = t;
      Throwable child = t.getCause();
      while(null != child)
      {
         parent = child;
         child = parent.getCause();
      }

      return parent;

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
    * @throws IllegalArgumentException	If a null <TT>name</TT> passed.
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
    * Clean the passed string. Replace whitespace characters with a single
    * space. If a <TT>null</TT> string passed return an empty string. E.G.
    * replace
    *
    * [pre]
    * \t\tselect\t* from\t\ttab01
    * [/pre]
    *
    * with
    *
    * [pre]
    * select * from tab01
    * [/pre]
    *
    * @deprecated	Use <tt>StringUtilities.cleanString(String)</tt> instead.
    *
    * @param	str	String to be cleaned.
    *
    * @return	Cleaned string.
    */
   public static String cleanString(String str)
   {
      return StringUtilities.cleanString(str);
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
      return "";
   }

   public static boolean equalsRespectNull(Object o1, Object o2)
   {
      if(null == o1 && null == o2)
      {
         return true;
      }
      else if(null == o1 || null == o2)
      {
         return false;
      }
      else
      {
         return o1.equals(o2);
      }

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

   // TODO: i18n
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

   /**
    * Split a string based on the given delimiter, but don't remove
    * empty elements.
    *
    * @deprecated	Use <tt>StringUtilities.split(String, char)</tt>
    *				instead.
    *
    * @param	str			The string to be split.
    * @param	delimiter	Split string based on this delimiter.
    *
    * @return	Array of split strings. Guaranteeded to be not null.
    */
   public static String[] splitString(String str, char delimiter)
   {
      return StringUtilities.split(str, delimiter);
   }

   /**
    * Split a string based on the given delimiter, optionally removing
    * empty elements.
    *
    * @deprecated	Use <tt>StringUtilities.split(String, char, boolean)</tt>
    *				instead.
    *
    * @param	str			The string to be split.
    * @param	delimiter	Split string based on this delimiter.
    * @param	removeEmpty	If <tt>true</tt> then remove empty elements.
    *
    * @return	Array of split strings. Guaranteeded to be not null.
    */
   public static String[] splitString(String str, char delimiter,
                                      boolean removeEmpty)
   {
      return StringUtilities.split(str, delimiter, removeEmpty);
   }

   /**
    * Creates a clone of any serializable object. Collections and arrays
    * may be cloned if the entries are serializable.
    *
    * Caution super class members are not cloned if a super class is not serializable.
    */
   public static Object cloneObject(Object toClone, final ClassLoader classLoader)
   {
      if(null == toClone)
      {
         return null;
      }
      else
      {
         try
         {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            ObjectOutputStream oOut = new ObjectOutputStream(bOut);
            oOut.writeObject(toClone);
            oOut.close();
            ByteArrayInputStream bIn = new ByteArrayInputStream(bOut.toByteArray());
            bOut.close();
            ObjectInputStream oIn = new ObjectInputStream(bIn)
            {
               protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException
               {
            	   return Class.forName(desc.getName(), false, classLoader);
               }
            };
            bIn.close();
            Object copy = oIn.readObject();
            oIn.close();

            return copy;
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }

      }
   }

   /**
    * Internationalizes a line with an embedded I18n key in it.  Suppose that 
    * the line looks like:
    * 
    *  This is a line with a <span>embeddedKey</span> in it. 
    *  
    * Further suppose the our I18NStrings.properties has the line:
    * 
    *   embeddedKey=(string that all would like to read)
    * 
    * This method will return the value:
    * 
    * This is a line with a (string that all would like to read) in it.
    *  
    * Note: This method cannot currently handle more than one embedded I18n 
    *       string in the specified line at this time.  Please put multiple 
    *       keys on separate lines. Otherwise, truncation of the specified 
    *       line could result!  
    * 
    * @param line the line to internationalize
    * @param s_stringMgr the StringManager to use to lookup I18N keys.
    * 
    * @return an internationalized replacement for this line
    */
   public static String replaceI18NSpanLine(String line, 
           StringManager s_stringMgr) {
       String result = line;
       Matcher start = spanStartPattern.matcher(line);
       Matcher end = spanEndPattern.matcher(line);
       if (start.matches() && end.matches()) {
           // line should look like :
           //
           // This is a line with an <span>embedded key</span> in it. 
           //
           StringBuffer tmp = new StringBuffer();
           String[] startparts = spanStartSplitPattern.split(line);

           tmp.append(startparts[0]);

           // startparts[1] contains our I18n string key followed by </span>
           String[] endparts = spanEndSplitPattern.split(startparts[1]);

           String key = endparts[0];

           String value = s_stringMgr.getString(key);
           tmp.append(value);
           tmp.append(endparts[1]);

           result = tmp.toString();
       }
       return result;
   }
   
   /**
	 * This is taken from Eammon McManus' blog:
	 * http://weblogs.java.net/blog/emcmanus/archive/2007/03/getting_rid_of.html This prevents you from having
	 * to place SuppressWarnings throughout your code.
	 * 
	 * @param <T>
	 *           the return type to cast the object to
	 * @param x
	 *           the object to cast.
	 * @return a type-casted version of the specified object.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T cast(Object x) {
	    return (T) x;
	}

	/**
	 * Checks the specified list of argument to see if any are null and throws a runtime exception if one is.
	 * 
	 * @param methodName
	 *           the name of the method checking it's arguments
	 * @param arguments
	 *           the arguments - these should be in name/value pairs
	 */
	public static void checkNull(String methodName, Object... arguments) {
		if (arguments.length % 2 != 0) {
			throw new IllegalArgumentException("Args must be specified in name/value pairs"); 
		}
		for (int i = 0; i < arguments.length-1; i+=2) {
			String name = (String)arguments[i];
			Object value = arguments[i+1];
			if (value == null) {
				throw new IllegalArgumentException(methodName+": Argument "+name+" cannot be null");
			}
		}
	}
	
	/**
	 * Cause the current thread to sleep for the specified number of milliseconds. Exceptions logged.
	 * 
	 * @param millis number of milliseconds to sleep.
	 */
	public static void sleep(long millis)
	{
		if (millis == 0) {
			return;
		}
		try
		{
			Thread.sleep(millis);
		}
		catch (Exception e)
		{
			s_log.error(e);
		}
	}
	
	/**
	 * Run the garbage collector.  We may eventually want this to execute in an app thread and serialize 
	 * many requests using a queue to avoid a performance hit for too many simultaneous calls.
	 */
	public static void garbageCollect() {
		System.gc();
	}

   public static String escapeHtmlChars(String sql)
   {
      return StringUtilities.escapeHtmlChars(sql);
   }

}
