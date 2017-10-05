package org.squirrelsql.workaround;

import javafx.geometry.Bounds;
import javafx.scene.control.ListCell;

public class ListViewScrollEventWA
{
   private final Runnable _scrollListener;

   public ListViewScrollEventWA(Runnable scrollListener)
   {
      _scrollListener = scrollListener;
   }

   public <T> ListCell<T> registerCell(ListCell<T> listCell)
   {
      listCell.boundsInParentProperty().addListener((observable, oldValue, newValue) -> onCellBoundsInParentChanged(oldValue, newValue));
      return listCell;
   }

   private void onCellBoundsInParentChanged(Bounds oldValue, Bounds newValue)
   {
      if(oldValue.getMinY() != newValue.getMinY())
      {
         fireScrollListener();
      }
   }

   private void fireScrollListener()
   {
      _scrollListener.run();
   }



   // None of the code below worked.
   // There ist lots of discussion of the problem
   // including https://bugs.openjdk.java.net/browse/JDK-8096847

//   private Timeline _timeline;
//
//   private void listenToScrollbar()
//   {
//      System.out.println("ColumnListCtrl.listenToScrollbar");
//
//      _listView.setOnScroll(e -> fireScrollListener());
//      _listView.setOnScrollTo(e -> fireScrollListener());
//
//      _listView.addEventFilter(ScrollEvent.ANY, event -> fireScrollListener());
//      _listView.addEventFilter(ScrollToEvent.ANY, event -> fireScrollListener());
//      _listView.addEventHandler(ScrollEvent.ANY, event -> fireScrollListener());
//      _listView.addEventHandler(ScrollToEvent.ANY, event -> fireScrollListener());
//
//      if(null == getListViewScrollBar(_listView))
//      {
//         _timeline = new Timeline(new KeyFrame(new Duration(500), (e) -> attachListenerToLookupedScrollbar()));
//         _timeline.setAutoReverse(true);
//         _timeline.play();
//      }
//      else
//      {
//         attachListenerToLookupedScrollbar();
//      }
//
//
//   }
//
//   private void attachListenerToLookupedScrollbar()
//   {
//      ScrollBar scrollBar = getListViewScrollBar(_listView);
//
//      if(null == scrollBar)
//      {
//         System.out.println("ListViewScrollEventWA.attachListenerToLookupedScrollbar --> failed");
//         return;
//      }
//
//      if (null != _timeline)
//      {
//         _timeline.stop();
//      }
//      else
//      {
//         System.out.println("ListViewScrollEventWA.attachListenerToLookupedScrollbar --> succeeded on first try");
//      }
//
//      scrollBar.addEventHandler(ScrollToEvent.ANY, e -> fireScrollListener());
//      scrollBar.addEventHandler(ScrollEvent.ANY, e -> fireScrollListener());
//      scrollBar.addEventFilter(ScrollToEvent.ANY, e -> fireScrollListener());
//      scrollBar.addEventFilter(ScrollEvent.ANY, e -> fireScrollListener());
//
//      System.out.println("ListViewScrollEventWA.attachListenerToLookupedScrollbar --> succeeded");
//
//   }
//
//
//
//   private ScrollBar getListViewScrollBar(ListView<?> listView)
//   {
//      ScrollBar scrollbar = null;
//      for (Node node : listView.lookupAll(".scroll-bar"))
//      {
//         if (node instanceof ScrollBar)
//         {
//            ScrollBar bar = (ScrollBar) node;
//            if (bar.getOrientation().equals(Orientation.VERTICAL))
//            {
//               scrollbar = bar;
//            }
//         }
//      }
//      return scrollbar;
//   }
}
