package org.squirrelsql.aliases;

import org.apache.commons.lang3.SerializationUtils;
import org.squirrelsql.AppState;
import org.squirrelsql.services.Dao;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.MessageHandler;
import org.squirrelsql.services.MessageHandlerDestination;

public class AliasDecorator implements AliasTreeNode
{
   private Alias _alias;
   private AliasPropertiesDecorator _aliasPropertiesDecorator;

   public AliasDecorator(Alias alias)
   {
      _alias = alias;
   }

   public Alias getAlias()
   {
      return _alias;
   }

   @Override
   public String getId()
   {
      return _alias.getId();
   }

   @Override
   public String getName()
   {
      return _alias.getName();
   }

   @Override
   public String toString()
   {
      return _alias.toString();
   }


   public AliasDecorator copy()
   {
      Alias clone = SerializationUtils.clone(_alias);
      clone.initAfterClone();

      AliasDecorator ret = new AliasDecorator(clone);

      if(null == _aliasPropertiesDecorator && Dao.hasAliasProperties(_alias.getId()))
      {
         _aliasPropertiesDecorator = getAliasPropertiesDecorator();
      }

      if(AppState.get().getSettingsManager().getSettings().isCopyAliasProperties() && null != _aliasPropertiesDecorator)
      {
         MessageHandler mh = new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_PANEL);
         I18n i18n = new I18n(getClass());

         mh.warning(i18n.t("alias.properties.copy.warning"));


         AliasPropertiesDecorator targetApd = _aliasPropertiesDecorator.copyToAlias(clone);
         ret.updateAliasPropertiesDecorator(targetApd);
      }


      return ret;
   }

   public void updateAlias(Alias alias)
   {
      _alias = alias;
   }

   public void updateAliasPropertiesDecorator(AliasPropertiesDecorator changedAliasProperties)
   {
      _aliasPropertiesDecorator = changedAliasProperties;
   }

   public AliasPropertiesDecorator getAliasPropertiesDecorator()
   {
      // This handling prevents that the default AliasProperties are saved for every existing Alias.

      if(null == _aliasPropertiesDecorator)
      {
         // Returns the default AliasProperties if non are defined.
         return Dao.loadAliasProperties(_alias.getId());
      }

      return _aliasPropertiesDecorator;
   }
}
