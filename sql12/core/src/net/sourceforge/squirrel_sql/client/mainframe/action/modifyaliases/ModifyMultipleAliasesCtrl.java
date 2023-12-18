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
         AliasChangesReport changes = AliasChangesHandler.findChanges(templateAlias, editedAlias);

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

}
