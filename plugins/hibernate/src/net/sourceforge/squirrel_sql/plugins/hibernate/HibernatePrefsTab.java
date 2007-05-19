package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.hibernate.configuration.HibernateController;

import java.awt.*;

public class HibernatePrefsTab implements IGlobalPreferencesPanel
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(HibernatePrefsTab.class);


   private HibernateController _ctrl;

   public HibernatePrefsTab(HibernateController ctrl)
   {
      _ctrl = ctrl;
   }

   public void initialize(IApplication app)
   {
      _ctrl.initialize();
   }

   public void uninitialize(IApplication app)
   {
   }

   public void applyChanges()
   {
      _ctrl.applyChanges();
   }

   public String getTitle()
   {
      //i18n[HibernatePrefsTab.title=Hibernate]
      return s_stringMgr.getString("HibernatePrefsTab.title");
   }

   public String getHint()
   {
      //i18n[HibernatePrefsTab.hint=Hibernate configurations]
      return s_stringMgr.getString("HibernatePrefsTab.hint");
   }

   public Component getPanelComponent()
   {
      return _ctrl.getPanel();
   }
}
