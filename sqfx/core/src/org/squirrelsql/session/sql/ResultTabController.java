package org.squirrelsql.session.sql;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.Utils;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.action.ActionUtil;
import org.squirrelsql.session.action.StdActionCfg;
import org.squirrelsql.session.sql.tablesearch.TableSearchCtrl;
import org.squirrelsql.table.*;

public class ResultTabController
{
   private I18n _i18n = new I18n(getClass());

   private final Tab _containerTab;
   private TableDecorator _sqlResultTableDecorator;
   private Session _session;
   private TableSearchCtrl _tableSearchCtrl;

   public ResultTabController(Session session, SQLResult sqlResult, String sql, SQLCancelTabCtrl sqlCancelTabCtrl, TableState tableState)
   {
      _session = session;

      String tabText = Utils.createSqlShortText(sql, 50);

      _containerTab = new Tab();
      _containerTab.setGraphic(new Label(tabText));

      _containerTab.setContent(createContainerPane(sqlResult, sql, sqlCancelTabCtrl, tableState));

      _containerTab.setUserData(new ResultTabUserData(sql, _sqlResultTableDecorator.getTableLoader()));

   }

   private BorderPane createContainerPane(SQLResult sqlResult, String sql, SQLCancelTabCtrl sqlCancelTabCtrl, TableState tableState)
   {

      FxmlHelper<SQLResultHeaderView> headerFxmlHelper = new FxmlHelper(SQLResultHeaderView.class);


      String headertext;

      if(sqlResult.isMaxResultsReached())
      {
         headertext = _i18n.t("outputtab.max.results.reached", sqlResult.getMaxResults(), Utils.createSqlShortText(sql, 200));
      }
      else
      {
         headertext = _i18n.t("outputtab.max.results.below", sqlResult.getResultTableLoader().size(), Utils.createSqlShortText(sql, 200));
      }


      headerFxmlHelper.getView().lblHeader.setText(headertext);

      ActionUtil.addActionToToolbar(headerFxmlHelper.getView().resultToolBar, StdActionCfg.RERUN_SQL.getActionCfg());

      _tableSearchCtrl = new TableSearchCtrl(sqlResult.getResultTableLoader());
      headerFxmlHelper.getView().resultToolBar.getItems().add(_tableSearchCtrl.getSearchButton());


      _sqlResultTableDecorator = new TableDecorator(_session, sql, tableState);
      headerFxmlHelper.getView().resultToolBar.getItems().add(_sqlResultTableDecorator.getEditButton());

      BorderPane bp = new BorderPane();

      bp.setTop(createTopPanel(headerFxmlHelper, _tableSearchCtrl));

      bp.setCenter(createContainerTabPane(sqlResult, sqlCancelTabCtrl));

      return bp;
   }

   private Node createTopPanel(FxmlHelper<SQLResultHeaderView> headerFxmlHelper, TableSearchCtrl tableSearchCtrl)
   {
      BorderPane bp = new BorderPane();

      bp.setTop(headerFxmlHelper.getRegion());

      tableSearchCtrl.setOnShowSearchPanel((panel, visible) -> onShowSearchPanel(panel, visible, bp));

      return bp;
   }

   private void onShowSearchPanel(Node panel, boolean visible, BorderPane bp)
   {
      if(visible)
      {
         _tableSearchCtrl.setActive(true);
         bp.setCenter(panel);
      }
      else
      {
         _tableSearchCtrl.setActive(false);
         bp.setCenter(null);
      }
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
      TableLoader tableLoader = sqlResult.getResultMetaDataTableLoader();

      Tab outputTab = new Tab(_i18n.t("outputtab.result.metadata"));
      outputTab.setContent(TableDecorator.decorateNonSqlEditableTable(tableLoader));
      outputTab.setClosable(false);

      return outputTab;
   }

   private Tab createResultsTab(SQLResult sqlResult)
   {
      StackPane stackPane = _sqlResultTableDecorator.decorateTable(sqlResult);
      Tab outputTab = new Tab(_i18n.t("outputtab.results"));
      outputTab.setContent(stackPane);
      outputTab.setClosable(false);
      return outputTab;
   }



   public Tab getTab()
   {
      return _containerTab;
   }
}
