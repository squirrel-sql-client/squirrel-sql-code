package org.squirrelsql.session.sql;

import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.services.I18n;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.sql.makeeditable.EditButtonCtrl;
import org.squirrelsql.table.TableLoader;

public class ResultTabController
{
   private I18n _i18n = new I18n(getClass());

   private final Tab _containerTab;
   private EditButtonCtrl _editButtonCtrl;
   private Session _session;

   public ResultTabController(Session session, SQLResult sqlResult, String sql, SQLCancelTabCtrl sqlCancelTabCtrl)
   {
      _session = session;

      String sqlAsTabText = sql.replaceAll("\n", " ");
      _containerTab = new Tab(sqlAsTabText);

      _containerTab.setContent(createContainerPane(sqlResult, sqlAsTabText, sqlCancelTabCtrl));

   }

   private BorderPane createContainerPane(SQLResult sqlResult, String sqlAsTabText, SQLCancelTabCtrl sqlCancelTabCtrl)
   {

      FxmlHelper<SQLResultHeaderView> headerFxmlHelper = new FxmlHelper(SQLResultHeaderView.class);


      String headertext;

      if(sqlResult.isMaxResultsReached())
      {
         headertext = _i18n.t("outputtab.max.results.reached", sqlResult.getMaxResults(), sqlAsTabText);
      }
      else
      {
         headertext = _i18n.t("outputtab.max.results.below", sqlResult.getResultTableLoader().size(), sqlAsTabText);
      }


      headerFxmlHelper.getView().lblHeader.setText(headertext);

      _editButtonCtrl = new EditButtonCtrl(_session, sqlAsTabText);
      headerFxmlHelper.getView().resultToolBar.getItems().add(_editButtonCtrl.getEditButton());

      BorderPane bp = new BorderPane();

      //headerFxmlHelper.getView().lblHeader.setStyle("-fx-border-color: blue;");
      bp.setTop(headerFxmlHelper.getRegion());

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
      TableLoader tableLoader = sqlResult.getResultMetaDataTableLoader();
      Tab outputTab = new Tab(_i18n.t("outputtab.result.metadata"));

      TableView tv = new TableView();

      tableLoader.load(tv);

      outputTab.setContent(tv);
      outputTab.setClosable(false);

      return outputTab;
   }

   private Tab createResultsTab(SQLResult sqlResult)
   {
      TableLoader tableLoader = sqlResult.getResultTableLoader();
      Tab outputTab = new Tab(_i18n.t("outputtab.results"));

      TableView tv = new TableView();

      tv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

      SQLResultRightMouseMenuHandler sqlResultRightMouseMenuHandler = new SQLResultRightMouseMenuHandler(tv);

      sqlResultRightMouseMenuHandler.addMenu(new I18n(getClass()).t("sqlresult.popup.CopyAsInStat"),() -> onCopyAsInStat(tv));



      if (_editButtonCtrl.allowsEditing())
      {
         _editButtonCtrl.displayAndPrepareEditing(sqlResult, tv);
      }
      else
      {
         tableLoader.load(tv);
      }

      outputTab.setContent(tv);
      outputTab.setClosable(false);

      return outputTab;
   }

   private void onCopyAsInStat(TableView tv)
   {
      System.out.println("ResultTabController.onCopyAsInStat");
   }


   public Tab getTab()
   {
      return _containerTab;
   }
}
