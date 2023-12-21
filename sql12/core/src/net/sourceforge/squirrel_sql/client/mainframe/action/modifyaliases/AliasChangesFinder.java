package net.sourceforge.squirrel_sql.client.mainframe.action.modifyaliases;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasProp;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import org.apache.commons.lang3.StringUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;

public class AliasChangesFinder
{
   public static AliasChangesHandler findChanges(Object template, Object edited)
   {
      AliasChangesHandler changesHandler = new AliasChangesHandler();
      _findChanges(template, edited, changesHandler);
      return changesHandler;

   }
   private static void _findChanges(Object template, Object edited, AliasChangesHandler changesHandler)
   {
      try
      {
         if (false == Objects.equals(template.getClass(), edited.getClass()))
         {
            String msg =
                  "template(class=" + template.getClass().getName() +
                        ") and edited(class=" + edited.getClass().getName() + ") " +
                        "are of different classes.";
            throw new IllegalArgumentException(msg);
         }

         BeanInfo beanInfo = Introspector.getBeanInfo(template.getClass());

         PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
         for (PropertyDescriptor pd : propertyDescriptors)
         {
            if (false == pd.getPropertyType().isPrimitive()
                  && StringUtils.containsIgnoreCase(pd.getPropertyType().getTypeName(), "squirrel"))
            {
               changesHandler.indentInnerBean();
               _findChanges(pd.getReadMethod().invoke(template), pd.getReadMethod().invoke(edited), changesHandler);
               changesHandler.unindentInnerBean();
            }

            Method getter = pd.getReadMethod();
            Annotation[] annotations = getter.getDeclaredAnnotations();
            for (Annotation annotation : annotations)
            {
               if (annotation instanceof SQLAliasProp)
               {
                  SQLAliasProp sqlAliasProp = (SQLAliasProp) annotation;

                  Object previousAliasPropValue = getter.invoke(template);
                  Object editedAliasPropValue = getter.invoke(edited);

                  if (false == sqlAliasProp.sqlAliasPropType().equals(previousAliasPropValue, editedAliasPropValue))
                  {
                     changesHandler.addChange(sqlAliasProp.sqlAliasPropType(), pd, previousAliasPropValue, editedAliasPropValue);
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
