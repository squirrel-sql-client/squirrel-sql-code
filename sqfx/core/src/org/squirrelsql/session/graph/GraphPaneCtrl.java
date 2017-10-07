package org.squirrelsql.session.graph;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
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
import org.squirrelsql.services.Dao;
import org.squirrelsql.services.FXMessageBox;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.MessageHandler;
import org.squirrelsql.services.MessageHandlerDestination;
import org.squirrelsql.services.SQLUtil;
import org.squirrelsql.services.SplitPositionSaver;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.graph.graphdesktop.Window;
import org.squirrelsql.session.graph.whereconfig.WhereConfigCtrl;
import org.squirrelsql.session.objecttree.ObjectTreeFilterCtrl;

import java.util.ArrayList;
import java.util.Arrays;
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
   private final SplitPane _splitPane;
   private SplitPositionSaver _splitPositionSaver = new SplitPositionSaver(getClass(), "graphSplit");
   private ArrayList<ToggleButton> _queryConfigToggleButtons = new ArrayList<>();


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

      _splitPane = new SplitPane(_scrollPane);
      _splitPane.setOrientation(Orientation.VERTICAL);

      _pane.setCenter(_splitPane);

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

      ToggleButton[] buf;

      buf = new ToggleButton[]
      {
         addToolBarToggleButton(toolBar, null, new Tooltip(_i18n.t("graph.query.select")), e -> onConfigQuery((ToggleButton) e.getSource(), QueryConfigType.SELECT)),
         addToolBarToggleButton(toolBar, null, new Tooltip(_i18n.t("graph.query.where")), e -> onConfigQuery((ToggleButton) e.getSource(), QueryConfigType.WHERE)),
         addToolBarToggleButton(toolBar, null, new Tooltip(_i18n.t("graph.query.orderby")), e -> onConfigQuery((ToggleButton) e.getSource(), QueryConfigType.ORDER_BY))
      };

      _queryConfigToggleButtons.addAll(Arrays.asList(buf));

      toolBar.getItems().add(new Separator());

      buf = new ToggleButton[]
      {
            addToolBarToggleButton(toolBar, null, new Tooltip(_i18n.t("graph.query.sql")), e -> onConfigQuery((ToggleButton) e.getSource(), QueryConfigType.SQL)),
            addToolBarToggleButton(toolBar, null, new Tooltip(_i18n.t("graph.query.result")), e -> onConfigQuery((ToggleButton) e.getSource(), QueryConfigType.RESULT)),
      };

      _queryConfigToggleButtons.addAll(Arrays.asList(buf));

      toolBar.getItems().add(new Separator());


      return toolBar;
   }

   private void onConfigQuery(ToggleButton button, QueryConfigType queryConfigType)
   {
      if (2 <= _splitPane.getItems().size() )
      {
         _splitPositionSaver.save(_splitPane);
         _splitPane.getItems().remove(1);
      }

      if(false == button.isSelected())
      {
         return;
      }

      for (ToggleButton queryConfigToggleButton : _queryConfigToggleButtons)
      {
         if(queryConfigToggleButton != button && queryConfigToggleButton.isSelected())
         {
            queryConfigToggleButton.setSelected(false);
         }
      }

      switch (queryConfigType)
      {
         case SELECT:
            SelectConfigCtrl selectConfigCtrl = new SelectConfigCtrl(_graphPersistenceWrapper, _graphChannel.getQueryChannel());
            _splitPane.getItems().add(selectConfigCtrl.getPane());
            break;
         case WHERE:
            WhereConfigCtrl whereConfigCtrl = new WhereConfigCtrl(_graphPersistenceWrapper, _graphChannel.getQueryChannel(), _session);
            _splitPane.getItems().add(whereConfigCtrl.getPane());
            break;
         case ORDER_BY:
            OrderByConfigCtrl  orderByConfigCtrl = new OrderByConfigCtrl(_graphPersistenceWrapper, _graphChannel.getQueryChannel());
            _splitPane.getItems().add(orderByConfigCtrl.getPane());
            break;
         case SQL:
            SqlConfigCtrl  sqlConfigCtrl = new SqlConfigCtrl(_graphPersistenceWrapper);
            _splitPane.getItems().add(sqlConfigCtrl.getPane());
            break;
         case RESULT:
            ResultConfigCtrl  resultConfigCtrl = new ResultConfigCtrl(_graphPersistenceWrapper);
            _splitPane.getItems().add(resultConfigCtrl.getPane());
            break;

      }


      _splitPositionSaver.applyInvertedDefault(_splitPane);
   }

   private void onChkHideNoJoins(CheckBox chkHideNoJoins)
   {
      _drawLinesCtrl.setHideNoJoins(chkHideNoJoins.isSelected());
      _drawLinesCtrl.doDraw();
      _graphPersistenceWrapper.getDelegate().setHideNoJoins(chkHideNoJoins.isSelected());
   }

   private Button addToolBarButton(ToolBar toolBar, String iconFileName, Tooltip tooltip, EventHandler<ActionEvent> actionEventEventHandler)
   {
      return (Button) addToolBarButton(toolBar, iconFileName, tooltip, actionEventEventHandler, false);
   }

   private ToggleButton addToolBarToggleButton(ToolBar toolBar, String iconFileName, Tooltip tooltip, EventHandler<ActionEvent> actionEventEventHandler)
   {
      return (ToggleButton) addToolBarButton(toolBar, iconFileName, tooltip, actionEventEventHandler, true);
   }

   private ButtonBase addToolBarButton(ToolBar toolBar, String iconFileName, Tooltip tooltip, EventHandler<ActionEvent> actionEventEventHandler, boolean toggle)
   {
      ButtonBase btn;

      if (toggle)
      {
         btn = new ToggleButton();
      }
      else
      {
         btn = new Button();
      }

      if (null != iconFileName)
      {
         btn.setGraphic(_props.getImageView(iconFileName));
      }
      else
      {
         btn.setText(tooltip.getText());
      }

      btn.setTooltip(tooltip);
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
      TableWindowCtrl tableWindowCtrl = new TableWindowCtrl(_session, _graphChannel, tableInfo, x, y, width, height, fkPropsByFkName, columnPersistences, ctrl -> _drawLinesCtrl.doDraw(), ctrl -> onTableClosed(ctrl));

      if (0 == columnPersistences.size())
      {
         // A new table is being added. We create a persistence for it to make QuerChannel events work.
         _graphPersistenceWrapper.getDelegate().getGraphTablePersistences().add(new GraphTablePersistence(tableWindowCtrl));
      }

      _desktopPane.getChildren().add(tableWindowCtrl.getWindow());
   }

   private void onTableClosed(TableWindowCtrl ctrl)
   {
      for (GraphTablePersistence graphTablePersistence : _graphPersistenceWrapper.getDelegate().getGraphTablePersistences())
      {
         String qualifiedNamePers = SQLUtil.getQualifiedName(graphTablePersistence.getCatalog(), graphTablePersistence.getSchema(), graphTablePersistence.getName());
         String qualifiedNameTableInfo = SQLUtil.getQualifiedName(ctrl.getTableInfo().getCatalog(), ctrl.getTableInfo().getSchema(), ctrl.getTableInfo().getName());

         if(qualifiedNameTableInfo.equalsIgnoreCase(qualifiedNamePers))
         {
            _graphPersistenceWrapper.getDelegate().getGraphTablePersistences().remove(graphTablePersistence);
            _graphChannel.getQueryChannel().fireChanged();
            return;
         }
      }
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
