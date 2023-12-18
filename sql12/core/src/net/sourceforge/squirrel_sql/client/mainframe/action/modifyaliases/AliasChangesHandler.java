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

public class AliasChangesHandler
{
   public static AliasChangesReport findChanges(Object template, Object edited)
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

         AliasChangesReport changes = new AliasChangesReport();


         BeanInfo beanInfo = Introspector.getBeanInfo(template.getClass());

         PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
         for (PropertyDescriptor pd : propertyDescriptors)
         {
            if (false == pd.getPropertyType().isPrimitive()
                  && StringUtils.containsIgnoreCase(pd.getPropertyType().getTypeName(), "squirrel"))
            {
               changes.indentInnerBean(pd);
               findChanges(pd.getReadMethod().invoke(template), pd.getReadMethod().invoke(edited));
               changes.unindentInnerBean(pd);
            }

            Method getter = pd.getReadMethod();
            Annotation[] annotations = getter.getDeclaredAnnotations();
            for (Annotation annotation : annotations)
            {
               if (annotation instanceof SQLAliasProp)
               {
                  SQLAliasProp sqlAliasProp = (SQLAliasProp) annotation;

                  switch (sqlAliasProp.sqlAliasPropI18n())
                  {
                     case driverPropertyCollection:
                     case schemaProp_schemaDetails:
                     case schemaProp_globalState:
                     case colorProp_overrideToolbarBackgroundColor:
                     case colorProp_toolbarBackgroundColor:
                     case colorProp_overrideObjectTreeBackgroundColor:
                     case colorProp_objectTreeBackgroundColor:
                     case colorProp_overrideStatusBarBackgroundColor:
                     case colorProp_statusBarBackgroundColor:
                     case colorProp_overrideAliasBackgroundColor:
                     case colorProp_aliasBackgroundColor:
                        continue;
                  }

                  Object previousAliasPropValue = getter.invoke(template);
                  Object editedAliasPropValue = getter.invoke(edited);

                  if (false == Objects.equals(previousAliasPropValue, editedAliasPropValue))
                  {
                     changes.addChange(sqlAliasProp, previousAliasPropValue, editedAliasPropValue);
                  }
               }
            }
         }

         return changes;
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
