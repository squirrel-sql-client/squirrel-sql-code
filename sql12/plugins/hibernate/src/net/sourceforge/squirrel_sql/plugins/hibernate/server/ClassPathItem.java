package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.io.Serializable;

public class ClassPathItem implements Serializable
{
   private boolean _jarDir;
   private String _path;

   public void setPath(String path)
   {
      _path = path;
   }

   public void setJarDir(boolean b)
   {
      _jarDir = b;
   }

   public boolean isJarDir()
   {
      return _jarDir;
   }

   public String getPath()
   {
      return _path;
   }
}
