package org.squirrelsql.session.graph.filter;

import javafx.stage.Stage;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.services.GuiUtils;
import org.squirrelsql.services.Pref;
import org.squirrelsql.services.Utils;
import org.squirrelsql.session.ColumnInfo;
import org.squirrelsql.session.graph.FilterPersistence;
import org.squirrelsql.session.graph.Operator;
import org.squirrelsql.session.graph.QueryChannel;

public class FilterCtrl
{

   private final Stage _dlg;
   private final FilterView _view;
   private final FilterValueCtrl _filterValueCtrl;
   private FilterPersistence _filterPersistence;
   private QueryChannel _queryChannel;

   public FilterCtrl(FilterPersistence filterPersistence, ColumnInfo columnInfo, QueryChannel queryChannel)
   {
      FxmlHelper<FilterView> fxmlHelper = new FxmlHelper(FilterView.class);
      _view = fxmlHelper.getView();

      _dlg = GuiUtils.createModalDialog(fxmlHelper.getRegion(), new Pref(getClass()), 470, 210, "FilterCtrl");

      _dlg.setTitle(columnInfo.getQualifiedTableColumnName());


      _filterPersistence = filterPersistence;
      _queryChannel = queryChannel;
      _filterValueCtrl = FilterValueCtrlFactory.getCtrl(columnInfo, _view.bpValueContainer, _view.btnDate, _view.lblEncloseApostrophes, _dlg, _view.cboOperator);

      _view.cboOperator.getItems().addAll(Operator.values());
      _view.cboOperator.getSelectionModel().select(Operator.valueOf(filterPersistence.getOperatorAsString()));

      _view.cboOperator.valueProperty().addListener((observable, oldValue, newValue) -> onOperatorChanged());
      onOperatorChanged();

      _filterValueCtrl.setFilterValueString(filterPersistence.getFilter());

      _view.btnCancel.setCancelButton(true);
      _view.btnOk.setDefaultButton(true);

      _view.btnCancel.setOnAction(e -> onCancel());
      _view.btnOk.setOnAction(e -> onOk());


      onOperatorChanged();

      _dlg.showAndWait();
   }

   private void onOperatorChanged()
   {
      Operator value = _view.cboOperator.getValue();
      _filterValueCtrl.setDisable(!value.requiresValue());
   }

   private void onOk()
   {
      String text = _filterValueCtrl.getFilterValueString();

      if (Utils.isEmptyString(text))
      {
         _filterPersistence.setFilter(null);
      }
      else
      {
         _filterPersistence.setFilter(text);
      }

      Operator selectedItem = _view.cboOperator.getSelectionModel().getSelectedItem();
      _filterPersistence.setOperatorAsString( selectedItem.name());
      _dlg.close();

      _queryChannel.fireChanged();
   }

   private void onCancel()
   {
      _dlg.close();
   }
}
