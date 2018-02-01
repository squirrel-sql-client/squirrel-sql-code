package org.squirrelsql.session.sql;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.squirrelsql.AppState;
import org.squirrelsql.services.Settings;
import org.squirrelsql.workaround.KeyMatchWA;
import org.squirrelsql.workaround.RichTextFxWA;

public class CurrentSqlMarker
{
   private Canvas _canvas = new Canvas();
   private SQLTextAreaServices _sqlTextAreaServices;
   private final StackPane _stackPane;

   public CurrentSqlMarker(SQLTextAreaServices sqlTextAreaServices)
   {
      _sqlTextAreaServices = sqlTextAreaServices;

      _stackPane = new StackPane();
      _stackPane.getChildren().add(_sqlTextAreaServices.getTextAreaVirtualScroll());

      _stackPane.getChildren().add(_canvas);
      StackPane.setAlignment(_canvas, Pos.TOP_LEFT);



      _canvas.setWidth(0);
      _canvas.setHeight(0);

      _canvas.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>()
      {
         @Override
         public void handle(MouseEvent event)
         {
            _sqlTextAreaServices.getTextArea().fireEvent(event);
         }
      });

      _canvas.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() // Mouse wheel
      {
         @Override
         public void handle(ScrollEvent event)
         {
            RichTextFxWA.getVirtualFlowContent(_sqlTextAreaServices.getTextArea()).fireEvent(event);
         }
      });


      _sqlTextAreaServices.getTextArea().addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>()
      {
         @Override
         public void handle(KeyEvent event)
         {
            onHandleKeyEvent(event);
         }
      });



      _canvas.setCursor(Cursor.TEXT);

      _stackPane.setPrefHeight(0);
      _stackPane.setPrefWidth(0);

      _stackPane.setMinHeight(0);
      _stackPane.setMinWidth(0);

      sqlTextAreaServices.getTextArea().caretPositionProperty().addListener(new ChangeListener<Integer>()
      {
         @Override
         public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue)
         {
            refreshCurrentSqlMark();
         }
      });

      _stackPane.heightProperty().addListener(new ChangeListener<Number>()
      {
         @Override
         public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
         {
            refreshCurrentSqlMark();
         }
      });

   }

   private void onHandleKeyEvent(KeyEvent event)
   {
      // Needs to be done for all keys that change text but don't move caret.
      if (KeyMatchWA.matches(event, new KeyCodeCombination(KeyCode.DELETE)))
      {
         Platform.runLater(() -> refreshCurrentSqlMark());
      }
   }

   private void refreshCurrentSqlMark()
   {
      CaretBounds currentSqlCaretBounds = _sqlTextAreaServices.getCurrentSqlCaretBounds();

      GraphicsContext gc = _canvas.getGraphicsContext2D();
      gc.clearRect(0,0,_canvas.getWidth(),_canvas.getHeight());

      if(currentSqlCaretBounds.begin == currentSqlCaretBounds.end || _sqlTextAreaServices.hasSelection())
      {
         return;
      }

      Region contentRegion = _sqlTextAreaServices.getTextArea();

      checkBindings(contentRegion);


      Bounds bounds = RichTextFxWA.getBoundsForCaretBounds(currentSqlCaretBounds, _sqlTextAreaServices.getTextArea());

      if(null == bounds)
      {
         return;
      }

      // Shows that as of 1.2.2018 the bounds are not right when scrolling down.
      //System.out.println("Painting " + System.currentTimeMillis() + "    " + bounds);


      Settings settings = AppState.get().getSettingsManager().getSettings();
      Color color = Color.rgb(settings.getCurrentSqlMarkColor_R(), settings.getCurrentSqlMarkColor_G(), settings.getCurrentSqlMarkColor_B());
      gc.setStroke(color);
      gc.strokeLine(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMinY());
      gc.strokeLine(bounds.getMaxX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY());
      gc.strokeLine(bounds.getMaxX(), bounds.getMaxY(), bounds.getMinX(), bounds.getMaxY());
      gc.strokeLine(bounds.getMinX(), bounds.getMaxY(), bounds.getMinX(), bounds.getMinY());


//      gc.setStroke(Color.GREEN);
//      gc.strokeLine(1, 1, _canvas.getWidth()-1, 1);
//      gc.strokeLine(_canvas.getWidth()-1, 1, _canvas.getWidth()-1, _canvas.getHeight()-1);
//      gc.strokeLine(_canvas.getWidth()-1, _canvas.getHeight()-1, 1, _canvas.getHeight()-1);
//      gc.strokeLine(1, _canvas.getHeight()-1, 1, 1);
   }

   private void checkBindings(Region contentRegion)
   {
      if(false == _canvas.widthProperty().isBound())
      {
         _canvas.widthProperty().bind(contentRegion.widthProperty());
         _canvas.heightProperty().bind(contentRegion.heightProperty());

         RichTextFxWA.getScrollbar(_sqlTextAreaServices.getTextAreaVirtualScroll(), Orientation.VERTICAL).valueProperty().addListener(new ChangeListener<Number>()
         {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
            {
               refreshCurrentSqlMark();
            }
         });

         RichTextFxWA.getScrollbar(_sqlTextAreaServices.getTextAreaVirtualScroll(), Orientation.HORIZONTAL).valueProperty().addListener(new ChangeListener<Number>()
         {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
            {
               refreshCurrentSqlMark();
            }
         });
      }
   }

   public StackPane getTextAreaStackPane()
   {
      return _stackPane;
   }
}
