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

import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

/**
 * Provides utility methods for working with Locale objects.
 * 
 * @author manningr
 */
public class LocaleUtils
{

	/**
	 * Returns an array of Locale objects that are sorted according to their toString value.
	 * 
	 * @return an array of Locale objects that are available in the JVM - this will include even Locales that
	 * we don't have translations for at the moment.
	 */
	public static Locale[] getAvailableLocales() {
      Locale[] availableLocales = Locale.getAvailableLocales();

      Arrays.sort(availableLocales, new Comparator<Locale>()
      {
         public int compare(Locale o1, Locale o2)
         {
            return o1.toString().compareTo(o2.toString());
         }
      });
      return availableLocales;
	}
	
	/**
	 * Returns available Locales as an array of Strings, using their toString values.
	 * 
	 * @return an array of Strings representing Locales.
	 */
	public static String[] getAvailableLocaleStrings() {
		Locale[] availableLocales = getAvailableLocales();
		String[] result = new String[availableLocales.length];
		for (int i = 0; i < availableLocales.length; i++) {
			result[i] = availableLocales[i].toString();
		}
		return result;
	}
}
