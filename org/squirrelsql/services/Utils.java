package org.squirrelsql.services;

import com.google.common.base.Strings;

public class Utils
{
   public static boolean isFilledString(String text)
   {
      return Strings.isNullOrEmpty(text) || Strings.isNullOrEmpty(text.trim());
   }
}
