package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;

public class DesktopStyle
{

   private StyleEnum _desktopStyle = null;

   public DesktopStyle(SquirrelPreferences prefs)
   {
      _desktopStyle = StyleEnum.INTERNAL_FRAME_STYLE;
      if(prefs.getTabbedStyle())
      {
         _desktopStyle = StyleEnum.DOCK_TAB_STYLE;
      }
      else if(prefs.getShowTabbedStyleHint())
      {
         TabbedStyleHintController ctrlHint = new TabbedStyleHintController();

         if(ctrlHint.isUseTabbedLayout())
         {
            prefs.setTabbedStyle(true);
            prefs.save();
            _desktopStyle = StyleEnum.DOCK_TAB_STYLE;
         }
         else if(ctrlHint.isDontShowAgain())
         {
            prefs.setShowTabbedStyleHint(false);
            prefs.save();
         }
      }
   }

   public  boolean isDockTabStyle()
   {
      return StyleEnum.DOCK_TAB_STYLE == _desktopStyle;
   }

   public boolean isInternalFrameStyle()
   {
      return StyleEnum.INTERNAL_FRAME_STYLE == _desktopStyle;
   }

   public boolean supportsLayers()
   {
      return _desktopStyle._supportsLayers;
   }

   public static enum StyleEnum
   {
      DOCK_TAB_STYLE(false),
      INTERNAL_FRAME_STYLE(true),;

      private boolean _supportsLayers;

      StyleEnum(boolean supportsLayers)
      {
         _supportsLayers = supportsLayers;
      }
   }
}
