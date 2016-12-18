package org.squirrelsql.session.graph;

import javafx.stage.Stage;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.services.GuiUtils;
import org.squirrelsql.services.Pref;

public class FilterCtrl
{

   private final Stage _dlg;
   private final FilterView _view;
   private FilterData _filterData;

   public FilterCtrl(FilterData filterData)
   {
      _filterData = filterData;

      FxmlHelper<FilterView> fxmlHelper = new FxmlHelper(FilterView.class);

      _view = fxmlHelper.getView();

      _view.cboOperator.getItems().addAll(Operator.values());
      _view.cboOperator.getSelectionModel().select(Operator.valueOf(filterData.getOperatorAsString()));

      _view.cboOperator.valueProperty().addListener((observable, oldValue, newValue) -> onOperatorChanged());
      onOperatorChanged();

      _view.txtValue.setText(filterData.getFilter());

      _view.btnCancel.setCancelButton(true);
      _view.btnOk.setDefaultButton(true);

      _view.btnCancel.setOnAction(e -> onCancel());
      _view.btnOk.setOnAction(e -> onOk());

      _dlg = GuiUtils.createModalDialog(fxmlHelper.getRegion(), new Pref(getClass()), 470, 170, "FilterCtrl");

      _dlg.showAndWait();
   }

   private void onOperatorChanged()
   {
      Operator value = (Operator) _view.cboOperator.getValue();
      _view.txtValue.setDisable(!value.requiresValue());
   }

   private void onOk()
   {
      _filterData.setFilter(_view.txtValue.getText());
      Operator selectedItem = (Operator)_view.cboOperator.getSelectionModel().getSelectedItem();
      _filterData.setOperatorAsString( selectedItem.name());
      _dlg.close();
   }

   private void onCancel()
   {
      _dlg.close();
   }
}
