package org.squirrelsql.session.sql.features;

import javafx.stage.Stage;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.services.GuiUtils;
import org.squirrelsql.services.Pref;
import org.squirrelsql.session.Session;

public class SqlToTableCtrl
{
   private final Stage _dialog;

   public SqlToTableCtrl(Session session)
   {
      FxmlHelper<SqlToTableView> fxmlHelper = new FxmlHelper<>(SqlToTableView.class);

      _dialog = GuiUtils.createModalDialog(fxmlHelper.getRegion(), new Pref(getClass()), 420, 240, "SqlToTableView");

      _dialog.showAndWait();
   }
}
