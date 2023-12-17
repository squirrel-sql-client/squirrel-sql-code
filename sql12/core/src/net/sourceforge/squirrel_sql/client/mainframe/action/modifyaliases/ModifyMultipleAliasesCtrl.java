package net.sourceforge.squirrel_sql.client.mainframe.action.modifyaliases;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.AliasInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.db.AliasWindowFactory;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasProp;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifierFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import org.apache.commons.lang3.StringUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;

public class ModifyMultipleAliasesCtrl
{

   private final ModifyMultipleAliasesDlg _dlg;
   private final SQLAlias _selectedAlias;

   public ModifyMultipleAliasesCtrl(SQLAlias selectedAlias)
   {
      _selectedAlias = selectedAlias;
      _dlg = new ModifyMultipleAliasesDlg();

      GUIUtils.initLocation(_dlg, 400, 400);
      GUIUtils.enableCloseByEscape(_dlg);

      _dlg.btnEditAliases.addActionListener(e -> onEditAliases());

      _dlg.setVisible(true);
   }

   private void onEditAliases()
   {
      IIdentifierFactory factory = IdentifierFactory.getInstance();
      SQLAlias newAlias = Main.getApplication().getAliasesAndDriversManager().createAlias(factory.createIdentifier());
      newAlias.assignFrom(_selectedAlias, false);

      AliasInternalFrame modifyMultipleSheet = AliasWindowFactory.getModifyMultipleSheet(newAlias, _dlg);

      modifyMultipleSheet.setOkListener(() -> onAliasSheetOk(_selectedAlias, newAlias));

      modifyMultipleSheet.setVisible(true);

   }

   private void onAliasSheetOk(SQLAlias templateAlias, SQLAlias editedAlias)
   {
      try
      {
         AliasChangesReport changes = new AliasChangesReport();

         findChanges(templateAlias, editedAlias, changes);

         if(false == changes.isEmpty())
         {
            _dlg.txtChangeReport.setText(changes.getReport());
         }
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private static void findChanges(Object template, Object edited, AliasChangesReport changes)
   {
      try
      {
         if(false == Objects.equals(template.getClass(), edited.getClass()))
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
            if(    false == pd.getPropertyType().isPrimitive()
                && StringUtils.containsIgnoreCase(pd.getPropertyType().getTypeName(), "squirrel"))
            {
               changes.indentInnerBean(pd);
               findChanges(pd.getReadMethod().invoke(template), pd.getReadMethod().invoke(edited), changes);
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

                  if(false == Objects.equals(previousAliasPropValue, editedAliasPropValue))
                  {
                     changes.addChange(sqlAliasProp,previousAliasPropValue, editedAliasPropValue);
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
