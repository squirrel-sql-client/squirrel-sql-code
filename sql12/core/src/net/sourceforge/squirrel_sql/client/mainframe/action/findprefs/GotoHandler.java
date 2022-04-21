package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.util.List;
import java.util.TreeMap;

public class GotoHandler
{
   private GlobalPreferencesDialogFindInfo _openDialogsFindInfo;
   private TreeMap<List<String>, List<PrefComponentInfo>> _globalPrefsComponentInfoByPath;

   public GotoHandler(GlobalPreferencesDialogFindInfo openDialogsFindInfo)
   {
      _openDialogsFindInfo = openDialogsFindInfo;
      _globalPrefsComponentInfoByPath = ComponentInfoByPathUtil.globalPrefsFindInfoToComponentInfoByPath(openDialogsFindInfo);
   }

   public void gotoPath(List<String> path)
   {
      Component tabComponent = getTabComponent(path);
      _openDialogsFindInfo.selectTabOfPathComponent(tabComponent);

      Component component = getComponent(path);

      if(tabComponent instanceof JScrollPane)
      {
         int x = component.getX();
         int y = component.getY();

         Container parent = component.getParent();
         while (parent != tabComponent)
         {
            x += parent.getX();
            y += parent.getY();
            parent = parent.getParent();
         }

         final Rectangle rect = new Rectangle(x, y, component.getWidth(), component.getHeight());

         GUIUtils.forceProperty(() -> {
            JComponent compInScrollPane = (JComponent) ((JScrollPane)tabComponent).getViewport().getView();
            compInScrollPane.scrollRectToVisible(rect);
            final boolean contains = ((JScrollPane) tabComponent).getVisibleRect().contains(rect);
            return contains;
         });
      }
      component.setBackground(Color.green);

   }

   private Component getComponent(List<String> path)
   {
      return _globalPrefsComponentInfoByPath.get(path).get(0).getComponent();
   }

   private Component getTabComponent(List<String> path)
   {
      final List<PrefComponentInfo> prefComponentInfoList = _globalPrefsComponentInfoByPath.get(path.subList(0,1));
      return prefComponentInfoList.get(0).getComponent();
   }
}
