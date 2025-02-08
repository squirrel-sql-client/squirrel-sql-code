package net.sourceforge.squirrel_sql.client.gui.db.mainframetitle;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.util.function.Supplier;

public enum PositionInMainFrameTitle
{
   POS_NONE(Integer.MAX_VALUE, () -> I18n.s_stringMgr.getString("PosInMainFrameTitle.none")),
   POS_1(1, () -> I18n.s_stringMgr.getString("PosInMainFrameTitle.1")),
   POS_2(2, () -> I18n.s_stringMgr.getString("PosInMainFrameTitle.2")),
   POS_3(3, () -> I18n.s_stringMgr.getString("PosInMainFrameTitle.3")),
   POS_4(4, () -> I18n.s_stringMgr.getString("PosInMainFrameTitle.4")),
   POS_5(5, () -> I18n.s_stringMgr.getString("PosInMainFrameTitle.5")),
   POS_6(6, () -> I18n.s_stringMgr.getString("PosInMainFrameTitle.6"));

   private static class I18n
   {
      private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(MainFrameTitlePrefsPanel.class);
   }

   private final int _order;
   private final Supplier<String> _i18nedNameSupplier;

   PositionInMainFrameTitle(int order, Supplier<String> i18nedNameSupplier)
   {
      _order = order;
      _i18nedNameSupplier = i18nedNameSupplier;
   }

   public int getOrder()
   {
      return _order;
   }

   @Override
   public String toString()
   {
      return _i18nedNameSupplier.get();
   }

   public static PositionInMainFrameTitle getApplicationNamePos()
   {
      return PositionInMainFrameTitle.valueOf(Main.getApplication().getSquirrelPreferences().getMainFrameTitlePosApplicationName());
   }

   public static PositionInMainFrameTitle getVersionPos()
   {
      return PositionInMainFrameTitle.valueOf(Main.getApplication().getSquirrelPreferences().getMainFrameTitlePosVersion());
   }

   public static PositionInMainFrameTitle getUserDirPos()
   {
      return PositionInMainFrameTitle.valueOf(Main.getApplication().getSquirrelPreferences().getMainFrameTitlePosUserDir());
   }

   public static PositionInMainFrameTitle getHomeDirPos()
   {
      return PositionInMainFrameTitle.valueOf(Main.getApplication().getSquirrelPreferences().getMainFrameTitlePosHomeDir());
   }

   public static PositionInMainFrameTitle getSessionNamePos()
   {
      return PositionInMainFrameTitle.valueOf(Main.getApplication().getSquirrelPreferences().getMainFrameTitlePosSessionName());
   }

   public static PositionInMainFrameTitle getSavedSessionOrGroupNamePos()
   {
      return PositionInMainFrameTitle.valueOf(Main.getApplication().getSquirrelPreferences().getMainFrameTitlePosSavedSessionOrGroupName());
   }
}
