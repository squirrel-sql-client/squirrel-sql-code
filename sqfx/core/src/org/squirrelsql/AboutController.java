package org.squirrelsql;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.squirrelsql.services.GuiUtils;
import org.squirrelsql.services.I18n;
import org.squirrelsql.session.sql.TableDecorator;
import org.squirrelsql.splash.Version;
import org.squirrelsql.table.TableLoader;

import java.util.ArrayList;
import java.util.Map;


public class AboutController
{
   public AboutController()
   {
      Stage dialog = new Stage();
      dialog.setTitle(new I18n(getClass()).t("about.title"));
      dialog.initModality(Modality.WINDOW_MODAL);
      dialog.initOwner(AppState.get().getPrimaryStage());

      TabPane tabPane = new TabPane();

      I18n i18n = new I18n(getClass());

      Tab tab;

      tab = new Tab(i18n.t("about.tab.about.title"), createAboutPane());
      tab.setClosable(false);
      tabPane.getTabs().add(tab);

      tab = new Tab(i18n.t("about.tab.systemInfo.title"), createSystemTable());
      tab.setClosable(false);
      tabPane.getTabs().add(tab);


      Platform.runLater(() -> GuiUtils.centerWithinParent(dialog));

      GuiUtils.makeEscapeClosable(tabPane);
      dialog.setScene(new Scene(tabPane));
      dialog.showAndWait();

   }

   private StackPane createSystemTable()
   {
      TableLoader tableLoader = new TableLoader();

      tableLoader.addColumn("Key");
      tableLoader.addColumn("Value");

      for (Map.Entry<Object, Object> entry : System.getProperties().entrySet())
      {
         ArrayList<String> list = new ArrayList<>();
         list.add((String) entry.getKey());
         list.add((String) entry.getValue());
         tableLoader.addRow(list);
      }
      return TableDecorator.decorateNonSqlEditableTable(tableLoader);
   }

   private BorderPane createAboutPane()
   {
      ImageView imageView = new Props(getClass()).getImageView("splash.jpg");

      BorderPane bp = new BorderPane();
      bp.setCenter(imageView);

      String text = Version.getVersion() + "\n" + Version.getCopyrightStatement();
      Label label = new Label(text);
      label.setStyle("-fx-text-alignment: center; -fx-background-color: #AEB0C5");
      bp.setStyle("-fx-background-color: #AEB0C5");
      label.setFocusTraversable(true);

      BorderPane.setAlignment(label, Pos.CENTER);
      bp.setBottom(label);


      bp.setPrefWidth(imageView.getFitWidth());
      bp.setPrefHeight(imageView.getFitHeight());
      return bp;
   }
}
