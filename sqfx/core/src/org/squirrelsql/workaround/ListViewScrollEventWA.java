package org.squirrelsql.workaround;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollToEvent;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;
import org.squirrelsql.session.graph.GraphColumn;

public class ListViewScrollEventWA
{
   private final Runnable _scrollListener;
   private final ListView<GraphColumn> _listView;
   private Timeline _timeline;

   public ListViewScrollEventWA(Runnable scrollListener, ListView<GraphColumn> listView)
   {
      _scrollListener = scrollListener;
      _listView = listView;

      //listenToScrollbar();
   }

   private void listenToScrollbar()
   {
      // None of the code below worked.
      // There ist lots of discussion of the problem
      // including https://bugs.openjdk.java.net/browse/JDK-8096847
      System.out.println("ColumnListCtrl.listenToScrollbar");

      _listView.setOnScroll(e -> fireScrollListener());
      _listView.setOnScrollTo(e -> fireScrollListener());

      _listView.addEventFilter(ScrollEvent.ANY, event -> fireScrollListener());
      _listView.addEventFilter(ScrollToEvent.ANY, event -> fireScrollListener());
      _listView.addEventHandler(ScrollEvent.ANY, event -> fireScrollListener());
      _listView.addEventHandler(ScrollToEvent.ANY, event -> fireScrollListener());

      if(null == getListViewScrollBar(_listView))
      {
         _timeline = new Timeline(new KeyFrame(new Duration(500), (e) -> attachListenerToLookupedScrollbar()));
         _timeline.setAutoReverse(true);
         _timeline.play();
      }
      else
      {
         attachListenerToLookupedScrollbar();
      }


   }

   private void attachListenerToLookupedScrollbar()
   {
      ScrollBar scrollBar = getListViewScrollBar(_listView);

      if(null == scrollBar)
      {
         System.out.println("ListViewScrollEventWA.attachListenerToLookupedScrollbar --> failed");
         return;
      }

      if (null != _timeline)
      {
         _timeline.stop();
      }
      else
      {
         System.out.println("ListViewScrollEventWA.attachListenerToLookupedScrollbar --> succeeded on first try");
      }

      scrollBar.addEventHandler(ScrollToEvent.ANY, e -> fireScrollListener());
      scrollBar.addEventHandler(ScrollEvent.ANY, e -> fireScrollListener());
      scrollBar.addEventFilter(ScrollToEvent.ANY, e -> fireScrollListener());
      scrollBar.addEventFilter(ScrollEvent.ANY, e -> fireScrollListener());

      System.out.println("ListViewScrollEventWA.attachListenerToLookupedScrollbar --> succeeded");

   }

   private void fireScrollListener()
   {
      System.out.println("######ListViewScrollEventWA.fireScrollListener");
      _scrollListener.run();
   }


   private ScrollBar getListViewScrollBar(ListView<?> listView)
   {
      ScrollBar scrollbar = null;
      for (Node node : listView.lookupAll(".scroll-bar"))
      {
         if (node instanceof ScrollBar)
         {
            ScrollBar bar = (ScrollBar) node;
            if (bar.getOrientation().equals(Orientation.VERTICAL))
            {
               scrollbar = bar;
            }
         }
      }
      return scrollbar;
   }

}
