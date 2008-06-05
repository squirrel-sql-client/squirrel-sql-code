package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.fw.gui.SortedListModel;
import net.sourceforge.squirrel_sql.client.ApplicationListener;
import net.sourceforge.squirrel_sql.client.IApplication;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.prefs.Preferences;

public abstract class BaseList implements IBaseList
{


   private ArrayList<ListDataListener> _listeners = new ArrayList<ListDataListener>();
   private JList _list = new JList()
   {
      public String getToolTipText(MouseEvent event)
      {
         return BaseList.this.getToolTipText(event); 
      }
   };
   private JScrollPane _comp = new JScrollPane(_list);


   public BaseList(SortedListModel sortedListModel, IApplication app)
   {
      _list.setModel(sortedListModel);
      getList().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      // Add listener to listen for items added/removed from list.
      _list.getModel().addListDataListener(new ListDataListener()
      {
         public void intervalAdded(ListDataEvent evt)
         {
            onIntervalAdded(evt);
         }
         public void intervalRemoved(ListDataEvent evt)
         {
            onIntervalRemoved(evt);
         }
         public void contentsChanged(ListDataEvent evt)
         {
            onContentsChanged(evt);
         }
      });

      _comp.setPreferredSize(new Dimension(100, 100));

      app.addApplicationListener(new ApplicationListener()
      {
         public void saveApplicationState()
         {
            onSaveApplicationState();
         }
      });


      setSelIxFromPrefs();
   }

   private void setSelIxFromPrefs()
   {
      int selIx = Preferences.userRoot().getInt(getSelIndexPrefKey(), 0);
      if(selIx >= 0 && selIx < _list.getModel().getSize())
      {
         _list.setSelectedIndex(selIx);
      }
   }

   private void onSaveApplicationState()
   {
      Preferences.userRoot().putInt(getSelIndexPrefKey(), getList().getSelectedIndex());
   }

   protected JList getList()
   {
      return _list;
   }

   protected abstract String getToolTipText(MouseEvent event);


   private void onContentsChanged(ListDataEvent evt)
   {
      for (ListDataListener listener : _listeners.toArray(new ListDataListener[0]))
      {
         listener.contentsChanged(evt);
      }
   }

   private void onIntervalRemoved(ListDataEvent evt)
   {
      int nextIdx = evt.getIndex0();
      int lastIdx = _list.getModel().getSize() - 1;
      if (nextIdx > lastIdx)
      {
         nextIdx = lastIdx;
      }
      _list.setSelectedIndex(nextIdx);


      for (ListDataListener listener : _listeners.toArray(new ListDataListener[0]))
      {
         listener.intervalRemoved(evt);
      }
   }

   private void onIntervalAdded(ListDataEvent evt)
   {
      _list.setSelectedIndex(evt.getIndex0()); // select the one just added.

      for (ListDataListener listener : _listeners.toArray(new ListDataListener[0]))
      {
         listener.intervalAdded(evt);
      }

   }

   public JComponent getComponent()
   {
      return _comp;
   }


   public void selectListEntryAtPoint(Point point)
   {
      int selectEventListIndex = _list.locationToIndex(point);
      if (_list.getSelectionMode() == ListSelectionModel.SINGLE_SELECTION)
      {
         // Only a single selection can be made at a time.
         int selectedIndex = _list.getSelectedIndex();
         if (selectedIndex != selectEventListIndex)
         {
            _list.setSelectedIndex(selectEventListIndex);
         }
      }
      else
      {
         // More than one selection is allowed - check to see if we should change the selection
         int[] selectedIndices = _list.getSelectedIndices();
         boolean alreadySelected = false;
         for (int selectedIndex : selectedIndices)
         {
            if (selectedIndex == selectEventListIndex)
            {
               alreadySelected = true;
            }
         }
         if (!alreadySelected)
         {
            _list.setSelectedIndex(selectEventListIndex);
         }
      }
   }

   public void addMouseListener(MouseListener mouseListener)
   {
      _list.addMouseListener(mouseListener);
   }

   public void removeMouseListener(MouseListener mouseListener)
   {
      _list.removeMouseListener(mouseListener);
   }

   public abstract String getSelIndexPrefKey();
}
