package org.squirrelsql.session.graph;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.squirrelsql.AppState;
import org.squirrelsql.Props;
import org.squirrelsql.globalicons.GlobalIconNames;
import org.squirrelsql.services.*;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.graph.graphdesktop.Window;
import org.squirrelsql.session.objecttree.ObjectTreeFilterCtrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GraphPaneCtrl
{

   private final ToolBar _toolbar;
   private BorderPane _pane = new BorderPane();

   private final ScrollPane _scrollPane;
   private GraphChannel _graphChannel;
   private Session _session;
   private GraphPersistenceWrapper _graphPersistenceWrapper;
   private final Pane _desktopPane = new Pane();
   private final DrawLinesCtrl _drawLinesCtrl;

   private final Props _props = new Props(getClass());
   private final I18n _i18n = new I18n(getClass());
   private Button _toolbarBtnAddTable;


   public GraphPaneCtrl(GraphChannel graphChannel, Session session, GraphPersistenceWrapper graphPersistenceWrapper)
   {
      _graphChannel = graphChannel;
      _session = session;
      _graphPersistenceWrapper = graphPersistenceWrapper;

      graphChannel.setDesktopPane(_desktopPane);
      initDrop(_desktopPane);

      // init and show the stage
      // create a scene that displays the desktopPane (resolution 600,600)
      StackPane stackPane = new StackPane();

      _scrollPane = new ScrollPane();
      _drawLinesCtrl = new DrawLinesCtrl(_desktopPane, _scrollPane, _graphChannel);
      _drawLinesCtrl.setHideNoJoins(graphPersistenceWrapper.getDelegate().isHideNoJoins());


      Canvas sizingDummyPane = new Canvas();
      stackPane.getChildren().add(sizingDummyPane);
      SizeBindingHelper.bindSizingDummyCanvasToScrollPane(_scrollPane, sizingDummyPane);

      stackPane.getChildren().add(_drawLinesCtrl.getCanvas());
      stackPane.getChildren().add(_desktopPane);

      _desktopPane.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> _drawLinesCtrl.mouseClicked(e));
      _desktopPane.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> _drawLinesCtrl.mousePressed(e));
      _desktopPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> _drawLinesCtrl.mouseDragged(e));
      _desktopPane.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> _drawLinesCtrl.mouseReleased(e));

      graphChannel.setShowtoolbarListener(this::onShowToolbar);

      _scrollPane.setContent(stackPane);

      _toolbar = createToolbar();

      _pane.setTop(_toolbar);
      _pane.setCenter(_scrollPane);

      Platform.runLater(() -> loadExistingTables(_graphPersistenceWrapper));
   }

   private void loadExistingTables(GraphPersistenceWrapper graphPersistenceWrapper)
   {
      for (GraphTablePersistence graphTablePersistence : graphPersistenceWrapper.getDelegate().getGraphTablePersistences())
      {
         List<TableInfo> tableInfos = _session.getSchemaCacheValue().get().getTablesByFullyQualifiedName(graphTablePersistence.getCatalog(), graphTablePersistence.getSchema(), graphTablePersistence.getName());

         if(0 == tableInfos.size())
         {
            MessageHandler mh = new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_PANEL);
            String qualifiedTableName = SQLUtil.getQualifiedName(graphTablePersistence.getCatalog(), graphTablePersistence.getSchema(), graphTablePersistence.getName());
            mh.warning(_i18n.t("graph.table.of.graph.does.not.exist", qualifiedTableName, graphPersistenceWrapper.getTabTitle()));
            continue;
         }

         HashMap<String, FkProps> fkProps = FkPropsPersistence.toFkProps(graphTablePersistence.getPersistentFkPropsPersistenceByFkName());

         List<ColumnPersistence> columnPersistences = graphTablePersistence.getColumnPersistences();

         addTableToDesktop(tableInfos.get(0), graphTablePersistence.getMinX(), graphTablePersistence.getMinY(), graphTablePersistence.getWidth(), graphTablePersistence.getHeight(), fkProps, columnPersistences);
      }

      _graphChannel.fireAllTablesAdded();

      // This call will prevent DND of tables into the query builder window from working. We have to find a different solution for that.
      //_drawLinesCtrl.doDraw();
   }

   private void onShowToolbar(boolean b)
   {
      if (b)
      {
         _pane.setTop(_toolbar);
      }
      else
      {
         _pane.setTop(null);
      }
   }

   private ToolBar createToolbar()
   {
      ToolBar toolBar = new ToolBar();

      _toolbarBtnAddTable = addToolBarButton(toolBar, "addTable.png", new Tooltip(_i18n.t("graph.add.table.button.tooltip")), e -> onAddTables());
      toolBar.getItems().add(new Separator());
      addToolBarButton(toolBar, GlobalIconNames.FILE_SAVE, new Tooltip(_i18n.t("graph.save.graph")), e -> onSaveGraph());
      addToolBarButton(toolBar, GlobalIconNames.FILE_SAVE_AS, new Tooltip(_i18n.t("graph.save.graph.as")), e -> onSaveGraphAs());
      addToolBarButton(toolBar, "trash_delete.png", new Tooltip(_i18n.t("graph.delete.graph")), e -> onDeleteGraph());
      toolBar.getItems().add(new Separator());

      CheckBox chkHideNoJoins = new CheckBox(_i18n.t("hide.no.joins"));
      chkHideNoJoins.setGraphic(new ImageView(_props.getImage(GlobalIconNames.EQUAL_CROSSED)));
      chkHideNoJoins.setContentDisplay(ContentDisplay.RIGHT);
      chkHideNoJoins.setSelected(_graphPersistenceWrapper.getDelegate().isHideNoJoins());
      chkHideNoJoins.setOnAction(e -> onChkHideNoJoins(chkHideNoJoins));

      toolBar.getItems().add(chkHideNoJoins);


      toolBar.getItems().add(new Separator());


      return toolBar;
   }

   private void onChkHideNoJoins(CheckBox chkHideNoJoins)
   {
      _drawLinesCtrl.setHideNoJoins(chkHideNoJoins.isSelected());
      _drawLinesCtrl.doDraw();
      _graphPersistenceWrapper.getDelegate().setHideNoJoins(chkHideNoJoins.isSelected());
   }

   private Button addToolBarButton(ToolBar toolBar, String iconFileName, Tooltip value, EventHandler<ActionEvent> actionEventEventHandler)
   {
      Button btn = new Button();
      btn.setGraphic(_props.getImageView(iconFileName));
      btn.setTooltip(value);
      btn.setOnAction(actionEventEventHandler);
      toolBar.getItems().add(btn);
      return btn;
   }

   private void onDeleteGraph()
   {
      String query = _i18n.t("query.delete.graph", _graphPersistenceWrapper.getTabTitle());

      if( FXMessageBox.YES.equals(FXMessageBox.showYesNo(AppState.get().getPrimaryStage(), query)) )
      {
         Dao.deleteGraphPersistence(_graphPersistenceWrapper, _session.getAlias());
         _graphChannel.removeGraphTab();
      }
   }


   private void onSaveGraphAs()
   {
      if(_graphPersistenceWrapper.isNew())
      {
         onSaveGraph();
         return;
      }

      String graphName = new GraphNameCtrl(_graphPersistenceWrapper.getTabTitle()).getGraphName();

      if(null == graphName)
      {
         return;
      }

      Dao.deleteGraphPersistence(_graphPersistenceWrapper, _session.getAlias());

      _graphPersistenceWrapper.getDelegate().setTabTitle(graphName);

      _saveIntern();
   }

   private void onSaveGraph()
   {
      if(_graphPersistenceWrapper.isNew())
      {
         String graphName = new GraphNameCtrl().getGraphName();
         _graphPersistenceWrapper.getDelegate().setTabTitle(graphName);

         if(null == graphName)
         {
            return;
         }
      }

      _saveIntern();
   }


   private void _saveIntern()
   {
      _graphPersistenceWrapper.getDelegate().getGraphTablePersistences().clear();

      for (Node pkNode : _desktopPane.getChildren())
      {
         TableWindowCtrl ctrl = ((Window) pkNode).getCtrl();
         GraphTablePersistence gti = new GraphTablePersistence(ctrl);
         _graphPersistenceWrapper.getDelegate().getGraphTablePersistences().add(gti);
      }

      MessageHandler mh = new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_PANEL);

      mh.info(_i18n.t("graph.wrote.file.to", Dao.writeGraphPersistence(_graphPersistenceWrapper, _session.getAlias()).getPath()));

      _graphChannel.setTabTitle(_graphPersistenceWrapper.getDelegate().getTabTitle());
   }

   private void onAddTables()
   {
      new ObjectTreeFilterCtrl(_session, "", _graphChannel, tableInfo -> addTableToDesktop(tableInfo, 0, 0));
   }

   private void initDrop(Pane desktopPane)
   {
      desktopPane.setOnDragDropped(this::onDragDroppedOfTableFromObjectTreeDialogToDesktop);
      desktopPane.setOnDragOver(e -> onDragOverOfTableFromObjectTreeDialogToDesktop(e));
   }

   private void onDragOverOfTableFromObjectTreeDialogToDesktop(DragEvent dragEvent)
   {
      if (   ObjectTreeFilterCtrl.DRAGGING_TO_QUERY_BUILDER.equals(dragEvent.getDragboard().getString())
          || ColumnListCtrl.DRAGGING_COLUMS.equals(dragEvent.getDragboard().getString()) )
      {
         dragEvent.acceptTransferModes(TransferMode.MOVE);
      }
      dragEvent.consume();
   }

   private void onDragDroppedOfTableFromObjectTreeDialogToDesktop(DragEvent dragEvent)
   {
      if( ObjectTreeFilterCtrl.DRAGGING_TO_QUERY_BUILDER.equals(dragEvent.getDragboard().getString()) )
      {
         List<TableInfo> tableInfos = _graphChannel.getLastDroppedTableInfos();

         double offset = 0;
         for (TableInfo tableInfo : tableInfos)
         {
            double x = dragEvent.getX() + offset;
            double y = dragEvent.getY() + offset;

            addTableToDesktop(tableInfo, x, y);

            offset += 10;
         }

         _drawLinesCtrl.doDraw();
      }
      dragEvent.consume();
   }

   private void addTableToDesktop(TableInfo tableInfo, double x, double y)
   {
      addTableToDesktop(tableInfo, x, y, 300, 200);
   }

   private void addTableToDesktop(TableInfo tableInfo, double x, double y, double width, double height)
   {
      addTableToDesktop(tableInfo, x, y, width, height, new HashMap<>(), new ArrayList<>());
   }

   private void addTableToDesktop(TableInfo tableInfo, double x, double y, double width, double height, HashMap<String, FkProps> fkPropsByFkName, List<ColumnPersistence> columnPersistences)
   {
      TableWindowCtrl tableWindowCtrl = new TableWindowCtrl(_session, _graphChannel, tableInfo, x, y, width, height, fkPropsByFkName, columnPersistences, ctrl -> _drawLinesCtrl.doDraw());

      _desktopPane.getChildren().add(tableWindowCtrl.getWindow());
   }


   public Node getPane()
   {
      return _pane;
   }

   public void pushAddTableBtn()
   {
      _toolbarBtnAddTable.fire();
   }
}
