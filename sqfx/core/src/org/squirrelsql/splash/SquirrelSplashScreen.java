package org.squirrelsql.splash;

import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;

public class SquirrelSplashScreen
{

   private final Stage _stage;
   private ProgressBar _progressBar;
   private int _progressStep;
   private double _numberOffCallsToindicateNewTask;

   public SquirrelSplashScreen(int numberOffCallsToindicateNewTask)
   {
      _numberOffCallsToindicateNewTask = numberOffCallsToindicateNewTask;

      URL resource = SquirrelSplashScreen.class.getResource("/org/squirrelsql/globalicons/splash.jpg");


      Image image = new Image(resource.toString());
      BorderPane borderPane = new BorderPane(new ImageView(image));
      borderPane.setStyle("-fx-background-color: #AEB0C5");

      BorderPane bottom = createBottom();
      borderPane.setBottom(bottom);


      _stage = new Stage(StageStyle.UNDECORATED);
      _stage.setScene(new Scene(borderPane));
      _stage.setWidth(image.getWidth());
      _stage.setHeight(image.getHeight() + 60);

      _stage.show();
      Platform.runLater(() ->_stage.toFront());

   }

   private BorderPane createBottom()
   {
      BorderPane ret = new BorderPane();


      Label lblVersion = new Label(Version.getCopyrightStatement());
      BorderPane.setAlignment(lblVersion, Pos.CENTER);
      ret.setCenter(lblVersion);

      _progressBar = new ProgressBar();

      _progressBar.setPrefWidth(Double.MAX_VALUE);
      BorderPane.setAlignment(_progressBar, Pos.CENTER);
      BorderPane.setMargin(_progressBar, new Insets(5,0,0,0));
      ret.setBottom(_progressBar);

      return ret;
   }


   public void indicateNewTask(final String text)
   {
      double value = ((double) (++_progressStep)) / _numberOffCallsToindicateNewTask;
      _progressBar.setProgress(value);
   }

   public void close()
   {
      _stage.close();
   }
}
