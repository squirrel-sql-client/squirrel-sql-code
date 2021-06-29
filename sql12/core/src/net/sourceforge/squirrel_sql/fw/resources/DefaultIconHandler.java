package net.sourceforge.squirrel_sql.fw.resources;

import javax.swing.ImageIcon;
import java.net.URL;

public class DefaultIconHandler implements IconHandler
{
   public ImageIcon createImageIcon(URL iconUrl)
   {
      return new ImageIcon(iconUrl);
   }

   public int iconScale_round(int size)
   {
      return size;
   }

   public int iconScale_ceil(int size)
   {
      return size;
   }
}
