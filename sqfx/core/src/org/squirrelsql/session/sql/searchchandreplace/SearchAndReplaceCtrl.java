package org.squirrelsql.session.sql.searchchandreplace;

import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import org.squirrelsql.Props;
import org.squirrelsql.globalicons.GlobalIconNames;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.session.action.StdActionCfg;

public class SearchAndReplaceCtrl
{
   private BorderPane _borderPane;

   public SearchAndReplaceCtrl(BorderPane borderPane)
   {
      _borderPane = borderPane;
      StdActionCfg.SEARCH_IN_TEXT.setAction(this::onSearch);

   }

   private void onSearch()
   {
      FxmlHelper<SearchAndReplaceView> fxmlHelper = new FxmlHelper<>(SearchAndReplaceView.class);


      fxmlHelper.getView().btnClose.setGraphic(new Props(getClass()).getImageView(GlobalIconNames.CLOSE));
      fxmlHelper.getView().btnFindNext.setGraphic(new Props(getClass()).getImageView(GlobalIconNames.ARROW_DOWN));
      fxmlHelper.getView().btnFindPrevious.setGraphic(new Props(getClass()).getImageView(GlobalIconNames.ARROW_UP));

      fxmlHelper.getView().btnClose.setOnAction(e -> onCLose());
      _borderPane.setTop(fxmlHelper.getRegion());

   }

   private void onCLose()
   {
      _borderPane.setTop(null);
   }
}

