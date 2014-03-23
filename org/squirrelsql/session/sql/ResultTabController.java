package org.squirrelsql.session.sql;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import org.squirrelsql.services.I18n;
import org.squirrelsql.table.TableLoader;

public class ResultTabController
{
   private I18n _i18n = new I18n(getClass());

   private final Tab _containerTab;

   public ResultTabController(SQLResult sqlResult, String sql, SQLCancelTabCtrl sqlCancelTabCtrl)
   {

      String sqlAsTabText = sql.replaceAll("\n", " ");
      _containerTab = new Tab(sqlAsTabText);

      _containerTab.setContent(createContainerPane(sqlResult, sqlAsTabText, sqlCancelTabCtrl));

   }

   private BorderPane createContainerPane(SQLResult sqlResult, String sqlAsTabText, SQLCancelTabCtrl sqlCancelTabCtrl)
   {

      BorderPane bp = new BorderPane();

      String headertext;

      if(sqlResult.isMaxResultsReached())
      {
         headertext = _i18n.t("outputtab.max.results.reached", sqlResult.getMaxResults(), sqlAsTabText);
      }
      else
      {
         headertext = _i18n.t("outputtab.max.results.below", sqlResult.getResultTableLoader().size(), sqlAsTabText);
      }


      Label label = new Label(headertext);
      BorderPane.setMargin(label, new Insets(10,0,0,5));
      bp.setTop(label);

      bp.setCenter(createContainerTabPane(sqlResult, sqlCancelTabCtrl));

      return bp;
   }

   private TabPane createContainerTabPane(SQLResult sqlResult, SQLCancelTabCtrl sqlCancelTabCtrl)
   {
      TabPane containerTabPane = new TabPane();
      containerTabPane.getTabs().add(createResultsTab(sqlResult));
      containerTabPane.getTabs().add(createResultMetaDataTab(sqlResult));

      if (null != sqlCancelTabCtrl)
      {
         containerTabPane.getTabs().add(sqlCancelTabCtrl.getTab());
      }

      return containerTabPane;
   }

   private Tab createResultMetaDataTab(SQLResult sqlResult)
   {
      return _createResultTableTab(sqlResult.getResultMetaDateTableLoader(), "outputtab.result.metadata");
   }

   private Tab createResultsTab(SQLResult sqlResult)
   {
      return _createResultTableTab(sqlResult.getResultTableLoader(), "outputtab.results");
   }


   private Tab _createResultTableTab(TableLoader tableLoader, String tabTextI18nKey)
   {
      Tab outputTab = new Tab(_i18n.t(tabTextI18nKey));

      TableView tv = new TableView();
      tableLoader.load(tv);

      outputTab.setContent(tv);
      outputTab.setClosable(false);

      return outputTab;
   }


   public Tab getTab()
   {
      return _containerTab;
   }
}
