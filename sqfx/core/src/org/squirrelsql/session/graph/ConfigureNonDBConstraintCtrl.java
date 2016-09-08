package org.squirrelsql.session.graph;

import javafx.stage.Stage;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.services.GuiUtils;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.Pref;
import org.squirrelsql.table.RowObjectTableLoader;

public class ConfigureNonDBConstraintCtrl
{

   private final I18n _i18n = new I18n(getClass());

   public ConfigureNonDBConstraintCtrl(LineInteractionInfo currentLineInteractionInfo, GraphColumnFinder graphColumnFinder)
   {
      FxmlHelper<ConfigureNonDBConstraintView> fxmlHelper = new FxmlHelper<>(ConfigureNonDBConstraintView.class);


      RowObjectTableLoader<ColumnPairRow> tableLoader = new RowObjectTableLoader<>();

      tableLoader.initColsByAnnotations(ColumnPairRow.class);

      LineSpec lineSpec = currentLineInteractionInfo.getClickedOnLineSpec();

      String fkId = lineSpec.getFkSpec().getFkNameOrId();


      ConfigureNonDBConstraintView view = fxmlHelper.getView();

      view.txtFkTableName.setEditable(false);
      view.txtPkTableName.setEditable(false);

      for (FkPoint fkPoint : lineSpec.getFkSpec().getFkPoints())
      {
         GraphColumn fkCol = fkPoint.getGraphColumn();
         GraphColumn pkCol = graphColumnFinder.findNonDbPkCol(fkCol, fkId);
         tableLoader.addRowObject(new ColumnPairRow(fkCol, pkCol));

         view.txtFkTableName.setText(fkCol.getColumnInfo().getFullTableName());
         view.txtPkTableName.setText(pkCol.getColumnInfo().getFullTableName());
      }

      tableLoader.load(view.tblColumnPairs);


      view.lblFkColumns.setText(_i18n.t("nondb.cols.label.fk", view.txtFkTableName.getText()));
      view.lblPkColumns.setText(_i18n.t("nondb.cols.label.pk", view.txtPkTableName.getText()));

      Stage dlg = GuiUtils.createModalDialog(fxmlHelper.getRegion(), new Pref(getClass()), 650, 570, "ConfigureNonDBConstraint");

      dlg.setTitle(_i18n.t("configure.nondb.constraint"));

      dlg.showAndWait();

   }
}
