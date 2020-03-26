package net.sourceforge.squirrel_sql.plugins.sqlbookmark.exportimport;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;

import java.awt.Window;

public class ImportDuplicateNameCtrl
{
   private static final String PREF_KEY_LAST_IMPORT_OPTION = "BookmarksPugin.ImportDuplicateNameCtrl.last.import.option";
   private BookmarkImportConflictOption _chosenOption;

   public ImportDuplicateNameCtrl(Window owningWindow)
   {
      ImportDuplicateNameDlg dlg = new ImportDuplicateNameDlg(owningWindow);

      final BookmarkImportConflictOption importOption = BookmarkImportConflictOption.valueOf(Props.getString(PREF_KEY_LAST_IMPORT_OPTION, BookmarkImportConflictOption.IGNORE.name()));

      switch (importOption)
      {
         case IGNORE:
            dlg.radIgnore.setSelected(true);
            break;
         case COPY:
            dlg.radCopy.setSelected(true);
            break;
         case UPDATE:
            dlg.radUpdate.setSelected(true);
            break;
         default:
            throw new IllegalStateException("Unknown import option " + importOption);
      }

      dlg.btnOk.addActionListener(e -> onOk(dlg));
      dlg.btnCancel.addActionListener(e -> close(dlg));


      GUIUtils.enableCloseByEscape(dlg);
      GUIUtils.initLocation(dlg, 400, 220);

      dlg.setVisible(true);
   }

   public BookmarkImportConflictOption getChosenOption()
   {
      return _chosenOption;
   }

   private void close(ImportDuplicateNameDlg dlg)
   {
      dlg.setVisible(false);
      dlg.dispose();
   }

   private void onOk(ImportDuplicateNameDlg dlg)
   {
      if (dlg.radIgnore.isSelected())
      {
         _chosenOption = BookmarkImportConflictOption.IGNORE;
      }
      else if (dlg.radCopy.isSelected())
      {
         _chosenOption = BookmarkImportConflictOption.COPY;
      }
      else if (dlg.radUpdate.isSelected())
      {
         _chosenOption = BookmarkImportConflictOption.UPDATE;
      }
      else
      {
         throw new IllegalStateException("Shouldn't get here");
      }

      Props.putString(PREF_KEY_LAST_IMPORT_OPTION, _chosenOption.name());

      close(dlg);
   }

}
