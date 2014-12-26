package org.squirrelsql.session.action;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.squirrelsql.AppState;
import org.squirrelsql.services.*;
import org.squirrelsql.session.sql.SQLTextAreaServices;

public class EscapeDateCtrl
{
   public EscapeDateCtrl(SQLTextAreaServices sqlTextAreaServices)
   {
      Stage dialog = new Stage();

      FxmlHelper<EscapeDateView> fxmlHelper = new FxmlHelper<>(EscapeDateView.class);

      dialog.setScene(new Scene(fxmlHelper.getRegion()));

      dialog.initModality(Modality.WINDOW_MODAL);

      dialog.initOwner(AppState.get().getPrimaryStage());

      GuiUtils.makeEscapeClosable(fxmlHelper.getRegion());

      new StageDimensionSaver("EscapeDateView", dialog, new Pref(getClass()), 190, 350, AppState.get().getPrimaryStage());

      fxmlHelper.getView().btnTimestamp.setOnAction(e -> System.out.println("#### btnTimestamp"));

      dialog.showAndWait();

   }
}
