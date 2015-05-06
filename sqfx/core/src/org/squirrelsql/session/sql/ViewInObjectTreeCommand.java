package org.squirrelsql.session.sql;

import org.squirrelsql.AppState;
import org.squirrelsql.services.FXMessageBox;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.Utils;
import org.squirrelsql.session.ObjectTreeTabCtrl;
import org.squirrelsql.session.objecttree.QualifiedObjectName;

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

      QualifiedObjectName qualifiedObjectName = getQualifiedObjectName(tokenAtCaret);

      if(false == objectTreeTabCtrl.selectObjectInTree(qualifiedObjectName))
      {
         FXMessageBox.showInfoOk(AppState.get().getPrimaryStage(), new I18n(getClass()).t("no.object.found.in.object.tree", tokenAtCaret.trim()));
      }
   }


   private QualifiedObjectName getQualifiedObjectName(String objectName)
   {
      String[] splits = objectName.split("\\.");

      String object = null;
      String schema = null;
      String catalog = null;

      if(splits.length >= 3)
      {
         catalog = removeQuotes(splits[0]);
         schema = removeQuotes(splits[1]);
         object = removeQuotes(splits[2]);
      }
      else if(splits.length == 2)
      {
         schema = removeQuotes(splits[0]);
         object = removeQuotes(splits[1]);
      }
      else
      {
         object = removeQuotes(splits[0]);
      }

      return new QualifiedObjectName(catalog, schema, object);
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
