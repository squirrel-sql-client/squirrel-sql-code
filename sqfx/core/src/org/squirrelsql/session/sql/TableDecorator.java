package org.squirrelsql.session.sql;

import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.rightmousemenuhandler.RightMouseMenuHandler;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.sql.copysqlpart.InStatCreator;
import org.squirrelsql.session.sql.copysqlpart.InsertStatCreator;
import org.squirrelsql.session.sql.makeeditable.EditButtonCtrl;
import org.squirrelsql.table.TableLoader;
import org.squirrelsql.table.TableState;
import org.squirrelsql.table.tableexport.ExportResultsCtrl;
import org.squirrelsql.table.tableselection.ExtendedTableSelectionHandler;

public class TableDecorator
{
   private EditButtonCtrl _editButtonCtrl;
   private TableLoader _tableLoader;
   private TableState _tableState;

   public TableDecorator(Session session, String sql, TableState tableState)
   {
      _tableState = tableState;
      _editButtonCtrl = new EditButtonCtrl(session, sql);
   }

   private TableDecorator()
   {
   }

   public ToggleButton getEditButton()
   {
      if(null == _editButtonCtrl)
      {
         throw new IllegalArgumentException("Initialization by non parameter constructor does not support editing");
      }

      return _editButtonCtrl.getEditButton();
   }

   public StackPane decorateTable(SQLResult sqlResult)
   {
      _tableLoader = sqlResult.getResultTableLoader();

      TableView tv = new TableView();
      RightMouseMenuHandler sqlResultRightMouseMenuHandler = new RightMouseMenuHandler(tv);
      ExtendedTableSelectionHandler extendedTableSelectionHandler = doStandardConfigs(_tableLoader, tv, sqlResultRightMouseMenuHandler);

      if (null != _editButtonCtrl && _editButtonCtrl.allowsEditing())
      {
         _editButtonCtrl.displayAndPrepareEditing(sqlResult, tv, _tableState);

         sqlResultRightMouseMenuHandler.addSeparator();
         MenuItem mnuDeleteRows = sqlResultRightMouseMenuHandler.addMenu(new I18n(getClass()).t("sqlresult.popup.delete.selected.rows"), () -> _editButtonCtrl.deleteSelectedRows());

         _editButtonCtrl.setDeleteRowsMenuItem(mnuDeleteRows);

      }
      else
      {
         _tableLoader.load(tv);

         if(null != _tableState)
         {
            _tableState.apply(_tableLoader);
         }

      }

      return extendedTableSelectionHandler.getStackPane();
   }

   private StackPane decorateTable(TableLoader tableLoader)
   {
      TableView tv = new TableView();
      RightMouseMenuHandler sqlResultRightMouseMenuHandler = new RightMouseMenuHandler(tv);
      ExtendedTableSelectionHandler extendedTableSelectionHandler = doStandardConfigs(tableLoader, tv, sqlResultRightMouseMenuHandler);

      tableLoader.load(tv);

      return extendedTableSelectionHandler.getStackPane();
   }

   private ExtendedTableSelectionHandler doStandardConfigs(TableLoader tableLoader, TableView tv, RightMouseMenuHandler sqlResultRightMouseMenuHandler)
   {
      tv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
      ExtendedTableSelectionHandler extendedTableSelectionHandler = new ExtendedTableSelectionHandler(tv);

      sqlResultRightMouseMenuHandler.addMenu(new I18n(getClass()).t("sqlresult.popup.Copy"), () -> CopyUtil.copyCells(extendedTableSelectionHandler, false));
      sqlResultRightMouseMenuHandler.addMenu(new I18n(getClass()).t("sqlresult.popup.CopyWithHeader"), () -> CopyUtil.copyCells(extendedTableSelectionHandler, true));
      sqlResultRightMouseMenuHandler.addMenu(new I18n(getClass()).t("sqlresult.popup.CopyAsInStat"),() -> InStatCreator.onCopyAsInStat(extendedTableSelectionHandler));
      sqlResultRightMouseMenuHandler.addMenu(new I18n(getClass()).t("sqlresult.popup.CopyAsInsertStat"),() -> InsertStatCreator.onCopyAsInsertStat(extendedTableSelectionHandler));
      sqlResultRightMouseMenuHandler.addSeparator();
      sqlResultRightMouseMenuHandler.addMenu(new I18n(getClass()).t("sqlresult.popup.ExportResults"),() -> new ExportResultsCtrl(tableLoader));
      return extendedTableSelectionHandler;
   }

   public TableLoader getTableLoader()
   {
      return _tableLoader;
   }

   public static StackPane decorateNonSqlEditableTable(TableLoader tableLoader)
   {
      return new TableDecorator().decorateTable(tableLoader);
   }



}
