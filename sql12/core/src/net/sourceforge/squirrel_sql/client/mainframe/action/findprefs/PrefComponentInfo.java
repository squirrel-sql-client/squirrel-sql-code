package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

public class PrefComponentInfo
{
   private Component _component;
   private String _text;
   private PrefComponentInfo _parent;
   private FindableComponentInfoType _findableComponentInfoType;

   public PrefComponentInfo(Component component, String text, PrefComponentInfo parent)
   {
      this(component, text, parent, FindableComponentInfoType.LEAVE_COMPONENT);
   }

   public PrefComponentInfo(Component component, String text, PrefComponentInfo parent, FindableComponentInfoType findableComponentInfoType)
   {
      _component = component;
      _text = text;
      _parent = parent;
      _findableComponentInfoType = findableComponentInfoType;
   }

   public String getText()
   {
      return _text;
   }

   public Component getComponent()
   {
      return _component;
   }

   public FindableComponentInfoType getFindableComponentInfoType()
   {
      return _findableComponentInfoType;
   }

   public static PrefComponentInfo createParentForTabComponent(String tabName, Component globalPrefTabComponent)
   {
      return new PrefComponentInfo(globalPrefTabComponent, tabName, null, FindableComponentInfoType.PARENT_TAB_CONTAINER);
   }

   public List<String> getPath()
   {
      ArrayList<String> path = new ArrayList<>();

      fillPath(path);

      return path;
   }

   public boolean isLeaf()
   {
      return _findableComponentInfoType == FindableComponentInfoType.LEAVE_COMPONENT;
   }

   private void fillPath(ArrayList<String> path)
   {
      if(null != _parent)
      {
         _parent.fillPath(path);
      }

      if(false == hasEmptyText())
      {
         path.add(_text);
      }
   }

   public boolean hasEmptyText()
   {
      return StringUtilities.isEmpty(_text, true);
   }
}
