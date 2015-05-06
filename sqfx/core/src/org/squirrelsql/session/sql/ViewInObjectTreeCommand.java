package org.squirrelsql.session.sql;

import org.squirrelsql.AppState;
import org.squirrelsql.services.FXMessageBox;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.Utils;
import org.squirrelsql.session.ObjectTreeTabCtrl;
import org.squirrelsql.session.objecttree.QualifiedObjectName;

import java.util.ArrayList;

public class ViewInObjectTreeCommand
{
   public ViewInObjectTreeCommand(SQLTextAreaServices sqlTextAreaServices, ObjectTreeTabCtrl objectTreeTabCtrl)
   {
      if(false == AppState.get().getSessionManager().getCurrentlyActiveOrActivatingContext().isSessionMainTab())
      {
         return;
      }

      String tokenAtCaret = sqlTextAreaServices.getTokenAtCaret();

      if(Utils.isEmptyString(tokenAtCaret))
      {
         FXMessageBox.showInfoOk(AppState.get().getPrimaryStage(), new I18n(getClass()).t("no.string.at.caret", tokenAtCaret.trim()));
         return;
      }

      ArrayList<QualifiedObjectName> qualifiedObjectNames = getQualifiedObjectNames(tokenAtCaret);

      for (QualifiedObjectName qualifiedObjectName : qualifiedObjectNames)
      {
         if(objectTreeTabCtrl.selectObjectInTree(qualifiedObjectName))
         {
            return;
         }
      }

      FXMessageBox.showInfoOk(AppState.get().getPrimaryStage(), new I18n(getClass()).t("no.object.found.in.object.tree", tokenAtCaret.trim()));
   }


   private ArrayList<QualifiedObjectName> getQualifiedObjectNames(String objectName)
   {
      ArrayList<QualifiedObjectName> ret = new ArrayList<>();

      String[] splits = objectName.split("\\.");

      String object = null;
      String schema = null;
      String catalog = null;

      if(splits.length > 3)
      {
         catalog = removeQuotes(splits[0]);
         schema = removeQuotes(splits[1]);
         object = removeQuotes(splits[2]);
         ret.add(new QualifiedObjectName(catalog, schema, object));
      }
      else if(splits.length == 3)
      {
         catalog = removeQuotes(splits[0]);
         schema = removeQuotes(splits[1]);
         object = removeQuotes(splits[2]);
         ret.add(new QualifiedObjectName(catalog, schema, object));

         // last token may be a column
         catalog = null;
         schema = removeQuotes(splits[0]);
         object = removeQuotes(splits[1]);
         ret.add(new QualifiedObjectName(catalog, schema, object));

      }
      else if(splits.length == 2)
      {
         catalog = null;
         schema = removeQuotes(splits[0]);
         object = removeQuotes(splits[1]);
         ret.add(new QualifiedObjectName(catalog, schema, object));

         // last token may be a column
         catalog = null;
         schema = null;
         object = removeQuotes(splits[0]);
         ret.add(new QualifiedObjectName(catalog, schema, object));
      }
      else
      {
         catalog = null;
         schema = null;
         object = removeQuotes(splits[0]);
         ret.add(new QualifiedObjectName(catalog, schema, object));
      }

      return ret;
   }

   private String removeQuotes(String objectName)
   {
      String ret = objectName.trim();


      while(ret.startsWith("\"") || ret.startsWith("/"))
      {
         ret = ret.substring(1);
      }

      while(ret.endsWith("\"") || ret.endsWith("/"))
      {
         ret = ret.substring(0,ret.length()-1);
      }

      return ret;
   }


}
