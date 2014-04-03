package org.squirrelsql.aliases;

public class AliasUtil
{
   public static String getUserNameRespectAliasConfig(Alias alias)
   {
      if(alias.isUserNull())
      {
         return null;
      }
      else if(alias.isUserEmptyString())
      {
         return "";
      }
      else
      {
         return alias.getUserName();
      }

   }

   public static String getPasswordRespectAliasConfig(Alias alias)
   {
      if(alias.isPasswordNull())
      {
         return null;
      }
      else if(alias.isUserEmptyString())
      {
         return "";
      }
      else
      {
         return alias.getPassword();
      }
   }
}
