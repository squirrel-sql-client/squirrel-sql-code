package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

import java.util.List;
import java.util.TreeMap;

public class PrefsFindInfo
{
   private SessionPropertiesDialogFindInfo _sessionPropertiesDialogFindInfo;
   private GlobalPreferencesDialogFindInfo _globalPreferencesDialogFindInfo;
   private AliasPropertiesDialogFindInfo _aliasPropertiesDialogFindInfo;
   private TreeMap<List<String>, List<PrefComponentInfo>> _componentInfoByPath;

   /**
    * E.g the shortcut table changes when the preference window is opened.
    * That is why we refresh the FindInfo when {@link #openDialogAndShowComponentAtPath(List)} is called.
    */
   private PrefsFindInfo _prefsFindInfoUpdate;

   public PrefsFindInfo(GlobalPreferencesDialogFindInfo globalPreferencesDialogFindInfo,
                        SessionPropertiesDialogFindInfo sessionPropertiesDialogFindInfo,
                        AliasPropertiesDialogFindInfo aliasPropertiesDialogFindInfo, // null when no Alias exists.
                        SavedSessionMoreDialogFindInfo savedSessionFindInfo, // unused, only to keep symetry.
                        TreeMap<List<String>, List<PrefComponentInfo>> componentInfoByPath)
   {
      _globalPreferencesDialogFindInfo = globalPreferencesDialogFindInfo;
      _sessionPropertiesDialogFindInfo = sessionPropertiesDialogFindInfo;
      _aliasPropertiesDialogFindInfo = aliasPropertiesDialogFindInfo;
      _componentInfoByPath = componentInfoByPath;
   }

   public TreeMap<List<String>, List<PrefComponentInfo>> getComponentInfoByPath()
   {
      return _componentInfoByPath;
   }

   /**
    *
    * @param The first path entry (path.subList(0,1)) is the dialog. The second if present is the tab components path.
    * @return The tab component If the path contained a tab component else null.
    */
   public PrefComponentInfo openDialogAndShowComponentAtPath(List<String> path)
   {
      final DialogFindInfo dialogFindInfo = getPrefComponentInfoByPath(path.subList(0,1)).getDialogFindInfo();

      PrefComponentInfo showingPrarentsComponentInfo = null;

      if(dialogFindInfo instanceof GlobalPreferencesDialogFindInfo )
      {
         _prefsFindInfoUpdate = ComponentInfoByPathUtil.createPrefsFindInfo(dialogFindInfo.getDialogToOpenConstant());

         if(1 < path.size())
         {
            showingPrarentsComponentInfo = getPrefComponentInfoByPath(path.subList(0, 2));
            showingPrarentsComponentInfo = _prefsFindInfoUpdate.getPrefComponentInfoByPath(showingPrarentsComponentInfo.getPath());
            _prefsFindInfoUpdate._globalPreferencesDialogFindInfo.selectTabOfPathComponent(showingPrarentsComponentInfo.getComponent());
         }
      }
      else if(dialogFindInfo instanceof SessionPropertiesDialogFindInfo)
      {
         _prefsFindInfoUpdate = ComponentInfoByPathUtil.createPrefsFindInfo(dialogFindInfo.getDialogToOpenConstant());

         if(1 < path.size())
         {
            showingPrarentsComponentInfo = getPrefComponentInfoByPath(path.subList(0,2));
            showingPrarentsComponentInfo = _prefsFindInfoUpdate.getPrefComponentInfoByPath(showingPrarentsComponentInfo.getPath());
            _prefsFindInfoUpdate._sessionPropertiesDialogFindInfo.selectTabOfPathComponent(showingPrarentsComponentInfo.getComponent());
         }
      }
      else if(dialogFindInfo instanceof AliasPropertiesDialogFindInfo)
      {
         _prefsFindInfoUpdate = ComponentInfoByPathUtil.createPrefsFindInfo(dialogFindInfo.getDialogToOpenConstant());

         if(1 < path.size())
         {
            showingPrarentsComponentInfo = getPrefComponentInfoByPath(path.subList(0,2));
            showingPrarentsComponentInfo = _prefsFindInfoUpdate.getPrefComponentInfoByPath(showingPrarentsComponentInfo.getPath());

            _prefsFindInfoUpdate._aliasPropertiesDialogFindInfo.selectTabOfPathComponent(showingPrarentsComponentInfo.getComponent());
         }
      }
      else if(dialogFindInfo instanceof SavedSessionMoreDialogFindInfo)
      {
         _prefsFindInfoUpdate = ComponentInfoByPathUtil.createPrefsFindInfo(dialogFindInfo.getDialogToOpenConstant());

         if(0 < path.size())
         {
            showingPrarentsComponentInfo = getPrefComponentInfoByPath(path.subList(0,1));
            showingPrarentsComponentInfo = _prefsFindInfoUpdate.getPrefComponentInfoByPath(showingPrarentsComponentInfo.getPath());

            // SavedSessionMoreDialog has no tabs
            //_prefsFindInfoUpdate._savedSessionFindInfo.selectTabOfPathComponent(tabComponentInfo.getComponent());
         }
      }
      else
      {
         throw new IllegalStateException("Unknown DialogFindInfo class: " + dialogFindInfo.getClass().getName());
      }

      return showingPrarentsComponentInfo;
   }

   public PrefsFindInfo getPrefsFindInfoUpdate()
   {
      if(null != _prefsFindInfoUpdate)
      {
         return _prefsFindInfoUpdate;
      }

      return this;
   }

   public PrefComponentInfo getComponentInfoByPath(List<String> path)
   {
      PrefComponentInfo prefComponentInfo = getPrefComponentInfoByPath(path);
      return prefComponentInfo;
   }

   public PrefComponentInfo getPrefComponentInfoByPath(List<String> path)
   {
      final List<PrefComponentInfo> componentInfoList = getPrefComponentInfoListByPath(path);
      if(componentInfoList == null)
      {
         return null;
      }

      PrefComponentInfo prefComponentInfo = componentInfoList.get(0);
      return prefComponentInfo;
   }

   public List<PrefComponentInfo> getPrefComponentInfoListByPath(List<String> path)
   {
      return _componentInfoByPath.get(path);
   }

}
