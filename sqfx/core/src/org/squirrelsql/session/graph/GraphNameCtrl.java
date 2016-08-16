package org.squirrelsql.session.graph;

import javafx.stage.Stage;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.services.GuiUtils;
import org.squirrelsql.services.Pref;
import org.squirrelsql.services.Utils;

public class GraphNameCtrl
{
   private String _graphName;

   public GraphNameCtrl()
   {
      FxmlHelper<GraphNameView> fxmlHelper = new FxmlHelper<>(GraphNameView.class);

      Stage dlg = GuiUtils.createModalDialog(fxmlHelper.getRegion(), new Pref(getClass()), 285, 135, "GraphNameDialog");

      fxmlHelper.getView().btnOk.setOnAction(e -> onOk(fxmlHelper, dlg));
      fxmlHelper.getView().btnCancel.setOnAction(e -> dlg.close());

      dlg.showAndWait();
   }

   private void onOk(FxmlHelper<GraphNameView> fxmlHelper, Stage dlg)
   {
      _graphName = fxmlHelper.getView().txtFileName.getText();
      dlg.close();
   }

   public String getGraphName()
   {
      if(Utils.isEmptyString(_graphName))
      {
         return null;
      }

      return _graphName;
   }
}
