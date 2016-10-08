package org.squirrelsql.session.graph;

import javafx.stage.Stage;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.services.GuiUtils;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.Pref;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.table.RowObjectTableLoader;

import java.util.ArrayList;
import java.util.List;

public class ConfigureNonDBConstraintCtrl
{

   private final I18n _i18n = new I18n(getClass());

   private final ConfigureNonDBConstraintView _view;
   private final RowObjectTableLoader<ColumnPairRow> _tableLoader;
   private final Stage _dlg;
   private final String _constraintsNonDbFkId;
   private final GraphFinder _graphFinder;

   public ConfigureNonDBConstraintCtrl(LineInteractionInfo currentLineInteractionInfo, GraphFinder graphFinder)
   {
      FxmlHelper<ConfigureNonDBConstraintView> fxmlHelper = new FxmlHelper<>(ConfigureNonDBConstraintView.class);


      _tableLoader = new RowObjectTableLoader<>();

      _tableLoader.initColsByAnnotations(ColumnPairRow.class);

      LineSpec lineSpec = currentLineInteractionInfo.getClickedOnLineSpec();

      _constraintsNonDbFkId = lineSpec.getFkSpec().getFkNameOrId();


      _view = fxmlHelper.getView();

      _view.txtFkTableName.setEditable(false);
      _view.txtPkTableName.setEditable(false);


      ArrayList<GraphColumn> fkColsInColPairTable = new ArrayList<>();
      ArrayList<GraphColumn> pkColsInColPairTable = new ArrayList<>();

      _graphFinder = graphFinder;
      for (FkPoint fkPoint : lineSpec.getFkSpec().getFkPoints())
      {
         GraphColumn fkCol = fkPoint.getGraphColumn();
         GraphColumn pkCol = _graphFinder.findNonDbPkCol(fkCol, _constraintsNonDbFkId);
         _tableLoader.addRowObject(new ColumnPairRow(fkCol, pkCol));

         fkColsInColPairTable.add(fkCol);
         pkColsInColPairTable.add(pkCol);

         _view.txtFkTableName.setText(fkCol.getColumnInfo().getFullTableName());
         _view.txtPkTableName.setText(pkCol.getColumnInfo().getFullTableName());
      }

      _tableLoader.load(_view.tblColumnPairs);


      _view.lblFkColumns.setText(_i18n.t("nondb.cols.label.fk", _view.txtFkTableName.getText()));
      _view.lblPkColumns.setText(_i18n.t("nondb.cols.label.pk", _view.txtPkTableName.getText()));


      List<GraphColumn> fkTableCols = _graphFinder.getAllColumnsForTable(_view.txtFkTableName.getText());
      fkTableCols.removeAll(fkColsInColPairTable);
      fkTableCols.sort((c1,c2) -> compareCols(c1, c2));
      _view.cboFkColumn.getItems().addAll(fkTableCols);
      _view.cboFkColumn.getSelectionModel().select(0);

      List<GraphColumn> pkTableCols = _graphFinder.getAllColumnsForTable(_view.txtPkTableName.getText());
      pkTableCols.removeAll(pkColsInColPairTable);
      pkTableCols.sort((c1,c2) -> compareCols(c1, c2));
      _view.cboPkColumn.getItems().addAll(pkTableCols);
      _view.cboPkColumn.getSelectionModel().select(0);

      _view.btnAdd.setOnAction(e -> onAdd());
      _view.btnRemoveSelectedEntry.setOnAction(e -> onRemoveSelected());


      _view.btnOk.setOnAction(e -> close(true));
      _view.btnCancel.setOnAction(e -> close(false));


      _dlg = GuiUtils.createModalDialog(fxmlHelper.getRegion(), new Pref(getClass()), 650, 570, "ConfigureNonDBConstraint");

      _dlg.setTitle(_i18n.t("configure.nondb.constraint"));

      _dlg.showAndWait();

   }

   private void close(boolean ok)
   {
      _dlg.close();


      if(false  == ok)
      {
         return;
      }

      for (GraphColumn graphColumn : _graphFinder.getAllColumnsForTable(_view.txtPkTableName.getText()))
      {
         graphColumn.removeNonDbFkId(_constraintsNonDbFkId);
      }

      for (GraphColumn graphColumn : _graphFinder.getAllColumnsForTable(_view.txtFkTableName.getText()))
      {
         graphColumn.removeNonDbFkId(_constraintsNonDbFkId);
      }


      for (ColumnPairRow cpr : _tableLoader.getRowObjects())
      {
         TableInfo pkTableInfo = _graphFinder.getTable(_view.txtPkTableName.getText());

         GraphUtils.connectColumns(_constraintsNonDbFkId, cpr.getFkGraphColumn(), pkTableInfo, cpr.getPkGraphColumn());
      }
   }

   private void onRemoveSelected()
   {
      //ColumnPairRow selectedItem = (ColumnPairRow) _view.tblColumnPairs.getSelectionModel().getSelectedItem();
      ColumnPairRow selectedItem = _tableLoader.getSelectedRow();

      if(null == selectedItem)
      {
         return;
      }

      _tableLoader.removeRow(selectedItem);

      _view.cboFkColumn.getItems().add(selectedItem.getFkGraphColumn());
      _view.cboFkColumn.getItems().sort((c1,c2) -> compareCols(c1, c2));
      _view.cboFkColumn.getSelectionModel().select(0);


      _view.cboPkColumn.getItems().add(selectedItem.getPkGraphColumn());
      _view.cboPkColumn.getItems().sort((c1,c2) -> compareCols(c1, c2));
      _view.cboPkColumn.getSelectionModel().select(0);
   }

   private void onAdd()
   {
      GraphColumn selFkCol = _view.cboFkColumn.getSelectionModel().getSelectedItem();
      GraphColumn selPkCol = _view.cboPkColumn.getSelectionModel().getSelectedItem();


      if(null == selFkCol || null == selPkCol)
      {
         return;
      }


      _tableLoader.addRowObject(new ColumnPairRow(selFkCol, selPkCol));

      _view.cboFkColumn.getItems().remove(selFkCol);
      if (0 <_view.cboFkColumn.getItems().size())
      {
         _view.cboFkColumn.getSelectionModel().select(0);
      }

      _view.cboPkColumn.getItems().remove(selPkCol);
      if (0 <_view.cboPkColumn.getItems().size())
      {
         _view.cboPkColumn.getSelectionModel().select(0);
      }

   }

   private int compareCols(GraphColumn c1, GraphColumn c2)
   {
      return c1.getColumnInfo().getColName().toUpperCase().compareTo(c2.getColumnInfo().getColName().toUpperCase());
   }

}
