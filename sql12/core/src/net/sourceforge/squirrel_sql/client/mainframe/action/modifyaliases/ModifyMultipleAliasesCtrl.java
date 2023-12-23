package net.sourceforge.squirrel_sql.client.mainframe.action.modifyaliases;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.AliasInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.db.AliasWindowFactory;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifierFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

public class ModifyMultipleAliasesCtrl
{

   private final ModifyMultipleAliasesDlg _dlg;
   private final SQLAlias _selectedAlias;
   private AliasChangesHandler _aliasChangesHandler;

   public ModifyMultipleAliasesCtrl(SQLAlias selectedAlias)
   {
      _selectedAlias = selectedAlias;
      _dlg = new ModifyMultipleAliasesDlg();

      GUIUtils.initLocation(_dlg, 400, 400);
      GUIUtils.enableCloseByEscape(_dlg);

      _dlg.btnEditTemplateAlias.addActionListener(e -> onEditAliases());
      _dlg.btnApplyChanges.addActionListener(e -> onApplyChanges());

      updateApplyButton();

      _dlg.setVisible(true);
   }

   private void onApplyChanges()
   {
      // FOR AliasChangesHandler.applyChanges() TEST ONLY

      //SQLAlias newSelectedAlias = new AliasesList(Main.getApplication()).getSelectedAlias(null);
      SQLAlias newSelectedAlias = Main.getApplication().getWindowManager().getAliasesListInternalFrame().getAliasesList().getSelectedAlias(null);

      IIdentifierFactory factory = IdentifierFactory.getInstance();
      SQLAlias newAlias = Main.getApplication().getAliasesAndDriversManager().createAlias(factory.createIdentifier());
      newAlias.assignFrom(newSelectedAlias, false);


      _aliasChangesHandler.applyChanges(newAlias);

      AliasInternalFrame modifyMultipleSheet = AliasWindowFactory.getModifyMultipleSheet(newAlias, _dlg);
      modifyMultipleSheet.setVisible(true);


      //_aliasChangesHandler = null;
      //updateApplyButton();
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
         AliasChangesHandler aliasChangesHandler = AliasChangesFinder.findChanges(templateAlias, editedAlias);

         _dlg.txtChangeReport.setText(null);
         if(false == aliasChangesHandler.isEmpty())
         {
            _dlg.txtChangeReport.setText(aliasChangesHandler.getChangeReport().getString());
            _aliasChangesHandler = aliasChangesHandler;
            updateApplyButton();
         }


      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private void updateApplyButton()
   {
      _dlg.btnApplyChanges.setEnabled(null != _aliasChangesHandler);
   }

}
