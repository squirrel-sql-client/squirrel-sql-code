package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

import org.apache.commons.lang3.StringUtils;

import javax.swing.JTable;
import javax.swing.JTree;
import java.util.List;

public class PathEntry
{
   private String _pathEntryString;
   private boolean _thisEntryMatchesFilter;
   private List<PrefComponentInfo> _componentInfoList;

   public PathEntry(String pathEntryString, boolean thisEntryMatchesFilter)
   {
      _pathEntryString = pathEntryString;
      _thisEntryMatchesFilter = thisEntryMatchesFilter;
   }

   public boolean isThisEntryMatchesFilter()
   {
      return _thisEntryMatchesFilter;
   }

   @Override
   public String toString()
   {
      return StringUtils.abbreviate(_pathEntryString, 80);
   }

   public boolean isSame(String pathEntryString)
   {
      return pathEntryString.equals(_pathEntryString);
   }

   public String getPathEntryString()
   {
      return _pathEntryString;
   }

   public void setComponentInfoList(List<PrefComponentInfo> componentInfoList)
   {
      _componentInfoList = componentInfoList;
   }

   public List<PrefComponentInfo> getComponentInfoList()
   {
      return _componentInfoList;
   }

   public boolean detailsTextNeedsLineWrap()
   {
      return false == (_componentInfoList.get(0).getComponent() instanceof JTree || _componentInfoList.get(0).getComponent() instanceof JTable);
   }
}
