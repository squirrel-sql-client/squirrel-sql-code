package org.squirrelsql.services;

public enum SquirrelProperty
{
   //USER_DIR("userdir", new File(new File(System.getProperty("user.dir")), ".squirrelfx/").getAbsolutePath());
   USER_DIR("userdir", "/home/gerd/work/java/squirrel/sqfx_userdir/");



   private final String _key;
   private final String _defaultValue;

   SquirrelProperty(String key, String defaultValue)
   {
      _key = key;
      _defaultValue = defaultValue;
   }

   public String getKey()
   {
      return _key;
   }

   public String getDefaultValue()
   {
      return _defaultValue;
   }
}
