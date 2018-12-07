package net.sourceforge.squirrel_sql.client.shortcut;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.SimpleType;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.action.RedoAction;
import net.sourceforge.squirrel_sql.client.session.action.UndoAction;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ShortcutManager
{
   /**
    * Implementation of equals() and hashcode() in {@link Shortcut} make sure
    * that in a set they are unique by action name and default keystroke.
    * Note: Sometimes the same action has two key strokes.
    */
   private HashSet<Shortcut> _shortcuts = new HashSet<>();

   private ShortcutsJsonBean _shortcutsJsonBeanLoadedAtStartUp;

   public ShortcutManager()
   {

      try
      {
         File shortCutsJsonBeanFile = new ApplicationFiles().getShortCutsJsonBeanFile();

         if(false == shortCutsJsonBeanFile.exists())
         {
            _shortcutsJsonBeanLoadedAtStartUp = new ShortcutsJsonBean();
            return;
         }


         FileInputStream is = new FileInputStream(shortCutsJsonBeanFile);
         InputStreamReader isr = new InputStreamReader(is, JsonEncoding.UTF8.getJavaName());


         ObjectMapper mapper = new ObjectMapper();
         _shortcutsJsonBeanLoadedAtStartUp = mapper.readValue(isr, SimpleType.construct(ShortcutsJsonBean.class));


         isr.close();
         is.close();
      }
      catch (IOException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   public void preRegisterActionsThatElseWillBeRegisteredLateOnSessionStart()
   {
      registerAccelerator(UndoAction.class);
      registerAccelerator(RedoAction.class);
   }

   private void registerAccelerator(Class<? extends Action> actionClass)
   {
      String actionName = Main.getApplication().getResources().getActionName(actionClass);
      KeyStroke defaultKeyStroke = Main.getApplication().getResources().getKeyStroke(actionClass);
      registerAccelerator(actionName, defaultKeyStroke);
   }

   public List<Shortcut> getShortcuts()
   {
      return new ArrayList<>(_shortcuts);
   }

   public void setAccelerator(JMenuItem item, KeyStroke defaultKeyStroke, Action action)
   {
      setAccelerator(item, defaultKeyStroke, (String) action.getValue(Action.NAME));
   }

   public void setAccelerator(JMenuItem item, KeyStroke defaultKeyStroke, String actionName)
   {
      Shortcut shortcut = registerAccelerator(actionName, defaultKeyStroke);
      item.setAccelerator(shortcut.validKeyStroke());
   }

   private Shortcut registerAccelerator(String actionName, KeyStroke defaultKeyStroke)
   {
      Shortcut ret = new Shortcut(actionName, defaultKeyStroke);

      String userShortCutString = _shortcutsJsonBeanLoadedAtStartUp.getShortcutByKey().get(ret.generateKey());

      if(null != userShortCutString)
      {
         ret.setUserKeyStroke(KeyStroke.getKeyStroke(userShortCutString));
      }


      _shortcuts.add(ret);

      return ret;
   }

   public void save()
   {
      try
      {
         ShortcutsJsonBean bean = new ShortcutsJsonBean();
         for (Shortcut shortcut : _shortcuts)
         {
            if(shortcut.hasUserKeyStroke())
            {
               bean.getShortcutByKey().put(shortcut.generateKey(), shortcut.generateUsrKeyStrokeString());
            }
         }


         File shortCutsJsonBeanFile = new ApplicationFiles().getShortCutsJsonBeanFile();


         FileOutputStream fos = new FileOutputStream(shortCutsJsonBeanFile);

         ObjectMapper mapper = new ObjectMapper();
         ObjectWriter objectWriter = mapper.writerWithDefaultPrettyPrinter();

         // This version of objectWriter.writeValue() ensures,
         // that objects are written in JsonEncoding.UTF8
         // and thus that there won't be encoding problems
         // that makes the loadObjects methods crash.
         objectWriter.writeValue(fos, bean);

         fos.close();
      }
      catch (IOException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }



}
