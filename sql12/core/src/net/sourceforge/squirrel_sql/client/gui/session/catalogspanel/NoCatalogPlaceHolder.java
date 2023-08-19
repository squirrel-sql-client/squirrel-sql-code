package net.sourceforge.squirrel_sql.client.gui.session.catalogspanel;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * A place holder for the label "None" which is only intended to appear at startup if:
 * <p>
 * 1) the database uses catalogs and
 * 2) it supports connections where the catalog is not specified.
 * <p>
 * We want to allow the user to switch to any other catalog when in this state. The selected catalog by
 * default is the first item in the list. However, it is confusing to have that be an actual catalog, when
 * in fact the user did not specify one in the connection URL. So, when we connect, if the driver says that
 * the catalog is null, this place-holder is created to take the first position in the combobox to allow
 * the user to choose any other "real" catalog, and when they do, this place-holder gets removed, since it
 * is no longer needed at that point.
 * <p>
 * Note: This placeholder allows us to do instanceof instead of a string comparison, which would prevent
 * the user from ever having and using a catalog called "None" (or whatever the equivalent i18n message
 * label is for their internationalized strings)
 *
 * @author manningr
 */
class NoCatalogPlaceHolder
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(NoCatalogPlaceHolder.class);

   public static final NoCatalogPlaceHolder s_instance = new NoCatalogPlaceHolder();

   private NoCatalogPlaceHolder()
   {
   }

   public String toString()
   {
      return s_stringMgr.getString("SQLCatalogsComboBox.noneLabel");
   }
}
