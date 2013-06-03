package org.squirrelsql.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ProgressUtil
{
   public static <T> void  start(final ProgressTask<T> pt, Stage stage)
   {
      final Task<T> concurrentTask = new Task<T>()
      {
         @Override
         protected T call() throws Exception
         {
            return pt.call();
         }
      };

      Service<T> service = new Service<T>()
      {
         @Override
         protected Task<T> createTask()
         {
            return concurrentTask;
         }
      };

      service.setOnSucceeded(new EventHandler<WorkerStateEvent>()
      {
         @Override
         public void handle(WorkerStateEvent workerStateEvent)
         {
            pt.goOn(concurrentTask.getValue());
         }
      });

      service.setOnFailed(new EventHandler<WorkerStateEvent>()
      {
         @Override
         public void handle(WorkerStateEvent workerStateEvent)
         {
            Throwable t = new RuntimeException("Progress task execution failed");

            try
            {
               concurrentTask.get();
            }
            catch (Throwable tOrig)
            {
               t = tOrig;
            }

            throw new RuntimeException(t);
         }
      });

      service.setOnCancelled(new EventHandler<WorkerStateEvent>()
      {
         @Override
         public void handle(WorkerStateEvent workerStateEvent)
         {
            throw new UnsupportedOperationException("NYI");
         }
      });


      if(false == stage.getScene().getRoot() instanceof StackPane)
      {
         throw new IllegalArgumentException("The stage parameter is not progressible. Use ProgressUtil.makeProgressible()");
      }

      StackPane sp = (StackPane) stage.getScene().getRoot();


      ProgressIndicator progressIndicator = null;
      ProgressRegion veil = null;
      Node applicationNode = null;

      for (Node node : sp.getChildren())
      {
         if(node instanceof  ProgressIndicator)
         {
            progressIndicator = (ProgressIndicator) node;
         }
         else if(node instanceof ProgressRegion)
         {
            veil = (ProgressRegion) node;
         }
         else
         {
            if(null == applicationNode)
            {
               applicationNode = node;
            }
            else
            {
               throw new IllegalArgumentException("More than one node in StackPane. Use ProgressUtil.makeProgressible()");
            }
         }
      }

      if(null == veil || null == progressIndicator)
      {
         throw new IllegalArgumentException("The stage parameter is not progressible. Use ProgressUtil.makeProgressible()");
      }

      veil.visibleProperty().unbind();
      progressIndicator.visibleProperty().unbind();

      veil.visibleProperty().bind(service.runningProperty());
      progressIndicator.visibleProperty().bind(service.runningProperty());

      service.start();

   }

   public static ProgressibleStage makeProgressible(Stage stage)
   {
      return new ProgressibleStage(stage);
   }

}