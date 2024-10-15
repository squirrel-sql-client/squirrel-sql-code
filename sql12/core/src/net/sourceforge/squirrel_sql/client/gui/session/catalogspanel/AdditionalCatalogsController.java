package net.sourceforge.squirrel_sql.client.gui.session.catalogspanel;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.checkedlistbox.CheckedListBoxHandler;
import net.sourceforge.squirrel_sql.fw.gui.checkedlistbox.CheckedListBoxListener;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import org.apache.commons.lang3.StringUtils;

import javax.swing.JCheckBox;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class AdditionalCatalogsController
{

   private final AdditionalCatalogsDlg _dlg;
   private boolean _ok;

   public AdditionalCatalogsController(ISession session)
   {
      try
      {
         _dlg = new AdditionalCatalogsDlg(GUIUtils.getOwningWindow(session.getSessionPanel()));

         String[] catalogs = session.getSQLConnection().getSQLMetaData().getCatalogs();

         AliasCatalogLoadModelJsonBean bean = Main.getApplication().getCatalogLoadModelManager().getAliasCatalogLoadModelJsonBean(session.getAlias());

         CheckedListBoxHandler<CatalogChecked> checkedListBoxHandler =
               new CheckedListBoxHandler<>(_dlg.chkLstCatalogs, new CheckedListBoxListener<>()
               {
                  @Override
                  public void listBoxItemToInvert(CatalogChecked catClicked)
                  {
                     catClicked.setChecked(!catClicked.isChecked());
                  }

                  @Override
                  public void listBoxItemToRender(CatalogChecked cat, JCheckBox renderer)
                  {
                     onRenderCheckBox(cat, renderer);
                  }
               });

         checkedListBoxHandler.setItems(List.of(catalogs).stream().map(cat -> createCatalogChecked(cat, bean)).collect(Collectors.toList()));

         _dlg.btnSelectAll.addActionListener(e -> onSelectAll(checkedListBoxHandler));
         _dlg.btnInvertSelection.addActionListener(e -> onInvertSelection(checkedListBoxHandler));
         _dlg.btnOk.addActionListener(e -> onOk(session, checkedListBoxHandler));

         GUIUtils.initLocation(_dlg, 400, 600);
         GUIUtils.enableCloseByEscape(_dlg);

         _dlg.setVisible(true);


      }
      catch (SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private static void onRenderCheckBox(CatalogChecked cat, JCheckBox renderer)
   {
      renderer.setSelected(cat.isChecked());
      renderer.setText(cat.getCatalog());
   }

   private void onInvertSelection(CheckedListBoxHandler<CatalogChecked> checkedListBoxHandler)
   {
      checkedListBoxHandler.getAllItems().forEach(i -> i.setChecked(!i.isChecked()));
      checkedListBoxHandler.repaint();
   }

   private void onSelectAll(CheckedListBoxHandler<CatalogChecked> checkedListBoxHandler)
   {
      checkedListBoxHandler.getAllItems().forEach(i -> i.setChecked(true));
      checkedListBoxHandler.repaint();
   }


   public boolean isOk()
   {
      return _ok;
   }

   private void onOk(ISession session, CheckedListBoxHandler<CatalogChecked> checkedListBoxHandler)
   {
      AliasCatalogLoadModelJsonBean bean = Main.getApplication().getCatalogLoadModelManager().getAliasCatalogLoadModelJsonBean(session.getAlias());

      bean.getAdditionalUserChosenCatalogs().clear();

      checkedListBoxHandler.getAllItems()
                           .stream().filter(CatalogChecked::isChecked)
                           .forEach(i -> bean.getAdditionalUserChosenCatalogs().add(i.getCatalog()));

      _ok = true;

      _dlg.setVisible(false);
      _dlg.dispose();
   }

   private static CatalogChecked createCatalogChecked(String catalog, AliasCatalogLoadModelJsonBean bean)
   {
      boolean checked =
            bean.getAdditionalUserChosenCatalogs().stream().anyMatch(c -> StringUtils.equalsIgnoreCase(c, catalog));

      return new CatalogChecked(catalog, checked);
   }
}
