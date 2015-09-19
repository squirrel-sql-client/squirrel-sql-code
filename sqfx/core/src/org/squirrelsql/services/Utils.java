package org.squirrelsql.services;

import com.google.common.base.Strings;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javax.swing.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class Utils
{
   public static boolean isEmptyString(String text)
   {
      return Strings.isNullOrEmpty(text) || Strings.isNullOrEmpty(text.trim());
   }


   public static boolean compareRespectEmpty(Object o1, Object o2)
   {
      return compareRespectEmpty(o1,o2, (t1,t2) -> t1.equals(t2));
   }

   public static boolean compareRespectEmpty(String s1, String s2)
   {
      return compareRespectEmpty(s1,s2, (t1,t2) -> t1.equalsIgnoreCase(t2));
   }

   /**
    * @param comparator Is called only if o1 and o2 are not null.
    */
   private static <T> boolean compareRespectEmpty(T o1, T o2, BiFunction<T, T, Boolean> comparator)
   {
      if(null == o1 && null == o2)
      {
         return true;
      }

      if(o1 == null && o2 != null)
      {
         return false;
      }

      if(o1 != null && o2 == null)
      {
         return false;
      }

      return comparator.apply(o1, o2);
   }

   public static void close(ResultSet res)
   {
      if(null == res)
      {
         return;
      }

      try
      {
         res.close();
      }
      catch(Throwable t)
      {
      }
   }

   public static void close(FileOutputStream fos)
   {
      if(null == fos)
      {
         return;
      }

      try
      {
         fos.flush();
         fos.close();
      }
      catch (IOException e)
      {
      }

      try
      {
         fos.close();
      }
      catch (IOException e)
      {
      }
   }


   public static void close(Statement stat)
   {
      if(null == stat)
      {
         return;
      }

      try
      {
         stat.close();
      }
      catch(Throwable t)
      {
      }
   }

   public static <T> List<T> asArray(T ... ts)
   {
      List<T> ret = new ArrayList<>();

      for (T t : ts)
      {
         ret.add(t);
      }
      return ret;
   }

   public static void makePositiveIntegerField(TextField txtField)
   {
      txtField.addEventFilter(KeyEvent.KEY_TYPED, Utils::onCheckInt);
   }

   private static void onCheckInt(KeyEvent t)
   {
      char ar[] = t.getCharacter().toCharArray();
      char ch = ar[t.getCharacter().toCharArray().length - 1];
      if (!(ch >= '0' && ch <= '9'))
      {
         t.consume();
      }
   }

   public static String getStackString(Throwable t)
   {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      t.printStackTrace(pw);

      pw.flush();
      sw.flush();
      String msg = sw.toString();

      try
      {
         pw.close();
         sw.close();
      }
      catch (IOException e)
      {

      }
      return msg;
   }

   public static void runOnSwingEDT(Runnable runnable)
   {

      Runnable wrapper = new Runnable()
      {
         @Override
         public void run()
         {
            try
            {
               runnable.run();
            }
            catch(Throwable t)
            {
               Platform.runLater(() -> new MessageHandler(Utils.class, MessageHandlerDestination.MESSAGE_LOG).error(t));
            }
         }
      };

      SwingUtilities.invokeLater(wrapper);

   }

   public static boolean isDoubleClick(MouseEvent mouseEvent)
   {
      return mouseEvent.getButton().equals(MouseButton.PRIMARY)  && mouseEvent.getClickCount() >= 2;
   }

   public static String createSqlShortText(String sql, int len)
   {
      String buf = removeNewLines(sql.trim());
      String tabText = buf.substring(0, Math.min(len, buf.length()));

      if(tabText.length() < buf.length())
      {
         tabText += " ...";
      }
      return tabText;
   }

   public static String removeNewLines(String s)
   {
      return s.replaceAll("\r\n", " ").replaceAll("\n", " ");
   }

   public static boolean isZero(double d)
   {
      return Math.abs(d) < 0.0000001d;
   }

   public static  <T> List<T> convertNullToArray(List<T> arr)
   {
      if (null == arr)
      {
         return new ArrayList<>();
      }

      return arr;
   }
   
  public static void gc(){
	  System.gc();
  }
}
