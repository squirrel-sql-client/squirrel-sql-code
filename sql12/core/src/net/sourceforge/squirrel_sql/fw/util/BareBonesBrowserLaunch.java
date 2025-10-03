package net.sourceforge.squirrel_sql.fw.util;

/////////////////////////////////////////////////////////
//Bare Bones Browser Launch                          //
//Version 1.5                                        //
//December 10, 2005                                  //
//Supports: Mac OS X, GNU/Linux, Unix, Windows XP    //
//Example Usage:                                     //
// String url = "http://www.centerkey.com/";       //
// BareBonesBrowserLaunch.openURL(url);            //
//Public Domain Software -- Free to Use as You Like  //
/////////////////////////////////////////////////////////

import javax.swing.JOptionPane;
import java.lang.reflect.Method;

public class BareBonesBrowserLaunch
{

   private static final String errMsg = "Error attempting to launch web browser";

   public static void openURL(String url)
   {
   }


   /**
    * Test driver
    *
    * @param args the first string in this array is the website url to launch.
    */
   public static void main(String[] args)
   {
      BareBonesBrowserLaunch.openURL(args[0]);
   }

}
