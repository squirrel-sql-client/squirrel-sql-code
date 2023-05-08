package net.sourceforge.squirrel_sql.client.gui.jmeld;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.util.function.Supplier;

public enum JMeldCurveType
{
   TYPE_ZERO(0, () -> I18nProvider.s_stringMgr.getString("JMeldCurveType.type0")),
   TYPE_ONE(1, () -> I18nProvider.s_stringMgr.getString("JMeldCurveType.type1")),
   TYPE_TWO(2, () -> I18nProvider.s_stringMgr.getString("JMeldCurveType.type2"));

   private final int _typeId;
   private final Supplier<String> _i18nSup;

   JMeldCurveType(int typeId, Supplier<String> i18nSup)
   {
      this._typeId = typeId;
      this._i18nSup = i18nSup;
   }

   public int getTypeId()
   {
      return _typeId;
   }

   @Override
   public String toString()
   {
      return _i18nSup.get();
   }

   private interface I18nProvider
   {
      StringManager s_stringMgr = StringManagerFactory.getStringManager(JMeldCurveType.class);
   }
}
