package net.sourceforge.squirrel_sql.client.gui.db.aliastransfer;

import net.sourceforge.squirrel_sql.client.gui.db.IToogleableAliasesList;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;

import javax.swing.JDialog;
import java.util.List;

public class AliasImportUpdateCtrl
{
   private static final String PREF_OPT_UPDATE_OLDER = "AliasImportUpdateCtrl.opt.update.older";
   private final AliasImportUpdateDlg _dlg;
   private final ExportImportTreeHandler _exportImportTreeHandler;
   private final IToogleableAliasesList _aliasesList;


   public AliasImportUpdateCtrl(JDialog parent, ExportImportTreeHandler exportImportTreeHandler, IToogleableAliasesList aliasesList)
   {
      _dlg = new AliasImportUpdateDlg(parent);
      _exportImportTreeHandler = exportImportTreeHandler;
      _aliasesList = aliasesList;

      GUIUtils.enableCloseByEscape(_dlg, dlg -> updatePrefs());

      GUIUtils.initLocation(_dlg, 500, 180);

      final boolean updateOlder = Props.getBoolean(PREF_OPT_UPDATE_OLDER, true);
      _dlg.optUpdateOlderMatches.setSelected(updateOlder);
      _dlg.optUpdateAllMatches.setSelected(!updateOlder);

      _dlg.btnCancel.addActionListener(e -> close());
      _dlg.btnUpdate.addActionListener(e -> update());

      _dlg.setVisible(true);
   }

   private void updatePrefs()
   {
      Props.putBoolean(PREF_OPT_UPDATE_OLDER, _dlg.optUpdateOlderMatches.isSelected());
   }

   private void update()
   {
      final List<SQLAlias> sqlAliasList = _exportImportTreeHandler.getSqlAliasList();

      final List<SQLAlias> updatedAliases = _aliasesList.updateAliasesByImport(sqlAliasList, _dlg.optUpdateOlderMatches.isSelected());

      _exportImportTreeHandler.removeAliases(updatedAliases);

      close();
   }

   private void close()
   {
      updatePrefs();
      _dlg.setVisible(false);
      _dlg.dispose();
   }
}
