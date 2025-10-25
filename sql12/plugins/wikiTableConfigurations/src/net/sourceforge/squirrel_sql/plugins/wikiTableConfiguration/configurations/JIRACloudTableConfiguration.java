package net.sourceforge.squirrel_sql.plugins.wikiTableConfiguration.configurations;

import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.GenericWikiTableConfigurationBean;
import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration;
import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableTransformer;

public class JIRACloudTableConfiguration extends GenericWikiTableConfigurationBean
{
   public JIRACloudTableConfiguration()
   {
      super("JIRA/Cloud",
            IWikiTableConfiguration.UNUSED,
            IWikiTableConfiguration.UNUSED,
            IWikiTableConfiguration.UNUSED,
            IWikiTableConfiguration.UNUSED,
            IWikiTableConfiguration.UNUSED,
            IWikiTableConfiguration.UNUSED,
            IWikiTableConfiguration.UNUSED,
            IWikiTableConfiguration.UNUSED,
            IWikiTableConfiguration.UNUSED);
   }

   @Override
   public boolean isReadOnly()
   {
      return true;
   }

   @Override
   public
   IWikiTableConfiguration cloneConfiguration()
   {
      JIRACloudTableConfiguration config = new JIRACloudTableConfiguration();
      config.setEnabled(isEnabled());
      return config;
   }

   @Override
   public IWikiTableTransformer createTransformer()
   {
      return new JIRACloudTransformer();
   }
}
