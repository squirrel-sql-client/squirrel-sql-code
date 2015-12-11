package org.squirrelsql.aliases.dbconnector;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.squirrelsql.Props;
import org.squirrelsql.aliases.AliasDecorator;
import org.squirrelsql.aliases.AliasPropertiesEditCtrl;
import org.squirrelsql.services.I18n;

public class SessionLoadingTimeHintController
{
   public SessionLoadingTimeHintController(BorderPane availableArea, AliasDecorator aliasDecorator)
   {
      I18n i18n = new I18n(getClass());
      Props props = new Props(getClass());

      availableArea.setCenter(new Label(i18n.t("session.loading.time.hint")));

      Button button = new Button(i18n.t("open.alias.properties"), props.getImageView("alias_properties.png"));
      availableArea.setBottom(button);
      BorderPane.setAlignment(button, Pos.BOTTOM_RIGHT);

      button.setOnAction(e -> openAliasProperties(aliasDecorator));
   }

   private void openAliasProperties(AliasDecorator aliasDecorator)
   {
      new AliasPropertiesEditCtrl(aliasDecorator);
   }
}
