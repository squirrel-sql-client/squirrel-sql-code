package org.squirrelsql.session.graph;

import javafx.stage.Stage;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.services.GuiUtils;
import org.squirrelsql.services.Pref;

public class ConfigureNonDBConstraintCtrl
{
   public ConfigureNonDBConstraintCtrl(LineInteractionInfo currentLineInteractionInfo)
   {
      FxmlHelper<ConfigureNonDBConstraintView> fxmlHelper = new FxmlHelper<ConfigureNonDBConstraintView>(ConfigureNonDBConstraintView.class);

      Stage dlg = GuiUtils.createModalDialog(fxmlHelper.getRegion(), new Pref(getClass()), 780, 570, "ConfigureNonDBConstraint");


      dlg.showAndWait();

   }
}
