package net.sourceforge.squirrel_sql.client.gui.db;

import java.io.Serializable;

public class AliasFolder implements Serializable
{
   public static final int NO_COLOR_RGB = -1;

   private String _folderName;

   private int _colorRGB;

   public AliasFolder(String folderName, int colorRGB)
   {
      _folderName = folderName;
      _colorRGB = colorRGB;
   }

   public String getFolderName()
   {
      return _folderName;
   }

   public int getColorRGB()
   {
      return _colorRGB;
   }

   public void setColorRGB(int colorRGB)
   {
      _colorRGB = colorRGB;
   }

   @Override
   public String toString()
   {
      return _folderName;
   }
}
