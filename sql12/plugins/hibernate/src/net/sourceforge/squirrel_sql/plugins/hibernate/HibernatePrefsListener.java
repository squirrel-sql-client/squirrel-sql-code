package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.plugins.hibernate.configuration.HibernateConfiguration;

import java.util.ArrayList;

public interface HibernatePrefsListener
{
   void configurationChanged(ArrayList<HibernateConfiguration> changedCfg);

   HibernateConfiguration getPreselectedCfg();

}
