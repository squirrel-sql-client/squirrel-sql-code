package org.squirrelsql.services;

import com.google.common.base.Strings;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Utils
{
   public static boolean isEmptyString(String text)
   {
      return Strings.isNullOrEmpty(text) || Strings.isNullOrEmpty(text.trim());
   }

   public static boolean compareRespectEmpty(String s1, String s2)
   {
      if(null == s1 && null == s2)
      {
         return true;
      }

      if(s1 == null)
      {
         return false;
      }

      return s1.equalsIgnoreCase(s2);
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

   public static void findNodeForPointInTableView(Parent parent, Class clazz, double x, double y)
   {
      for (Node node : parent.getChildrenUnmodifiable())
      {
         Bounds boundsInParent = node.getBoundsInParent();
         Bounds layoutBounds = node.getLayoutBounds();
         Bounds boundsInLocal = node.getBoundsInLocal();

         double localX;
         double localY;

//         if (parent instanceof Group)
//         {
//            localX = x;
//            localY = y;
//         }
//         else
//         {
            localX = x - (boundsInParent.getMinX() /*- layoutBounds.getMinX()*/ - boundsInLocal.getMinX());
            localY = y - (boundsInParent.getMinY() /*- layoutBounds.getMinY()*/ - boundsInLocal.getMinY());
//         }

         if(node instanceof TableCell && null != ((TableCell) node).getItem() && node.isVisible() && parent.isVisible() && node.contains(localX, localY) )
         {


            System.out.println(node + "X=" + localX + " Y=" + localY + "; In Parent PX=" + boundsInParent.getMinX() + " PY=" + boundsInParent.getMinY()+ " visi" + node.isVisible());
         }

         //System.out.println("   X=" + localX + " Y=" + localY + "; In Parent PX=" + boundsInParent.getMinX() + " PY=" + boundsInParent.getMinY() + " " + parent.getClass().getName() + " -> " + node.getClass().getName());


         if(node instanceof Parent)
         {
            findNodeForPointInTableView((Parent) node, clazz, localX, localY);
         }
      }
   }
}
