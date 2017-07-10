package org.squirrelsql.session.graph;

import javafx.stage.Stage;
import org.squirrelsql.services.*;
import org.squirrelsql.session.ColumnInfo;

public class FilterCtrl
{

   private final Stage _dlg;
   private final FilterView _view;
   private FilterPersistence _filterPersistence;
   private QueryChannel _queryChannel;

   public FilterCtrl(FilterPersistence filterPersistence, ColumnInfo columnInfo, QueryChannel queryChannel)
   {
      _filterPersistence = filterPersistence;
      _queryChannel = queryChannel;

      FxmlHelper<FilterView> fxmlHelper = new FxmlHelper(FilterView.class);

      _view = fxmlHelper.getView();

      _view.cboOperator.getItems().addAll(Operator.values());
      _view.cboOperator.getSelectionModel().select(Operator.valueOf(filterPersistence.getOperatorAsString()));

      _view.cboOperator.valueProperty().addListener((observable, oldValue, newValue) -> onOperatorChanged());
      onOperatorChanged();

      _view.txtValue.setText(filterPersistence.getFilter());

      _view.btnCancel.setCancelButton(true);
      _view.btnOk.setDefaultButton(true);

      _view.btnCancel.setOnAction(e -> onCancel());
      _view.btnOk.setOnAction(e -> onOk());


      _dlg = GuiUtils.createModalDialog(fxmlHelper.getRegion(), new Pref(getClass()), 470, 170, "FilterCtrl");

      _dlg.setTitle(columnInfo.getQualifiedTableColumnName());

      _dlg.showAndWait();
   }

   private void onOperatorChanged()
   {
      Operator value = (Operator) _view.cboOperator.getValue();
      _view.txtValue.setDisable(!value.requiresValue());
   }

   private void onOk()
   {
      String text = _view.txtValue.getText();

      if (Utils.isEmptyString(text))
      {
         _filterPersistence.setFilter(null);
      }
      else
      {
         _filterPersistence.setFilter(text);
      }

      Operator selectedItem = (Operator)_view.cboOperator.getSelectionModel().getSelectedItem();
      _filterPersistence.setOperatorAsString( selectedItem.name());
      _dlg.close();

      _queryChannel.fireChanged();
   }

   private void onCancel()
   {
      _dlg.close();
   }
}
