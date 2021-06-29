package net.sourceforge.squirrel_sql.fw.resources;

import javax.swing.ImageIcon;
import java.net.URL;

public interface IconHandler
{
   ImageIcon createImageIcon(URL iconUrl);

   int iconScale_round(int size);
   int iconScale_ceil(int size);
}
