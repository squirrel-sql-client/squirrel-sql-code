package net.sourceforge.squirrel_sql.client.gui.db.modifyaliases;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasProp;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import org.apache.commons.lang3.StringUtils;

public class AliasChangesFinder
{
   public static AliasChangesHandler findChanges(SQLAlias uneditedAlias, SQLAlias editedAlias)
   {
      AliasChangesHandler changesHandler = new AliasChangesHandler();
      _findChanges(uneditedAlias, editedAlias, changesHandler);
      return changesHandler;

   }
   private static void _findChanges(Object unedited, Object edited, AliasChangesHandler changesHandler)
   {
      try
      {
         if (false == Objects.equals(unedited.getClass(), edited.getClass()))
         {
            String msg =
                  "unedited(class=" + unedited.getClass().getName() +
                        ") and edited(class=" + edited.getClass().getName() + ") " +
                        "are of different classes.";
            throw new IllegalArgumentException(msg);
         }

         BeanInfo beanInfo = Introspector.getBeanInfo(unedited.getClass());

         PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
         for (PropertyDescriptor pd : propertyDescriptors)
         {
            if (false == pd.getPropertyType().isPrimitive()
                  && StringUtils.containsIgnoreCase(pd.getPropertyType().getTypeName(), "squirrel"))
            {
               _findChanges(pd.getReadMethod().invoke(unedited), pd.getReadMethod().invoke(edited), changesHandler);
            }

            Method getter = pd.getReadMethod();
            Annotation[] annotations = getter.getDeclaredAnnotations();
            for (Annotation annotation : annotations)
            {
               if (annotation instanceof SQLAliasProp)
               {
                  SQLAliasProp sqlAliasProp = (SQLAliasProp) annotation;

                  Object uneditedAliasPropValue = getter.invoke(unedited);
                  Object editedAliasPropValue = getter.invoke(edited);

                  if (false == sqlAliasProp.sqlAliasPropType().equals(uneditedAliasPropValue, editedAliasPropValue))
                  {
                     changesHandler.addChange(sqlAliasProp.sqlAliasPropType(), pd, uneditedAliasPropValue, editedAliasPropValue);
                  }
               }
            }
         }
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
