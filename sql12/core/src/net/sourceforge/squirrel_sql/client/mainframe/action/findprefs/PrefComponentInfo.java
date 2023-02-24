package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

public class PrefComponentInfo
{
   /**
    * Not null when {@link  #_findableComponentInfoType == FindableComponentInfoType.DIALOG}
    */
   private DialogFindInfo _dialogFindInfo;

   /**
    * null when {@link  #_findableComponentInfoType == FindableComponentInfoType.DIALOG}
    */
   private Component _component;
   private String _text;

   /**
    * null when {@link  #_findableComponentInfoType == FindableComponentInfoType.DIALOG}
    */
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

   public PrefComponentInfo(DialogFindInfo dialogFindInfo)
   {
      this(null, dialogFindInfo);
   }

   public PrefComponentInfo(Component component, DialogFindInfo dialogFindInfo)
   {
      _component = component;
      _dialogFindInfo = dialogFindInfo;
      _text = dialogFindInfo.getDialogTitle();
      _parent = null;
      _findableComponentInfoType = FindableComponentInfoType.DIALOG;
   }

   public String getText()
   {
      return _text;
   }

   public Component getComponent()
   {
      return _component;
   }

   public DialogFindInfo getDialogFindInfo()
   {
      return _dialogFindInfo;
   }

   public FindableComponentInfoType getFindableComponentInfoType()
   {
      return _findableComponentInfoType;
   }

   public static PrefComponentInfo createParentForTabComponent(DialogFindInfo dialogFindInfo, String tabName, Component globalPrefTabComponent)
   {
      PrefComponentInfo dialogComponentInfo = new PrefComponentInfo(dialogFindInfo);
      return new PrefComponentInfo(globalPrefTabComponent, tabName, dialogComponentInfo, FindableComponentInfoType.PARENT_TAB_CONTAINER);
   }

   public static PrefComponentInfo createParentForDialog(DialogFindInfo dialogFindInfo, Component dialogContentComponent)
   {
      return new PrefComponentInfo(dialogContentComponent, dialogFindInfo);
   }


   public List<String> getPath()
   {
      ArrayList<String> path = new ArrayList<>();

      fillPath(path);

      return path;
   }

   public PrefComponentInfo getParent()
   {
      return _parent;
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
