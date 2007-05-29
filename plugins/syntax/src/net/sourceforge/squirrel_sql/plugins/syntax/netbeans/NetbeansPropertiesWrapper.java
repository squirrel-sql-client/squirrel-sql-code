package net.sourceforge.squirrel_sql.plugins.syntax.netbeans;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessor;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessorFactory;

import java.util.HashMap;

public class NetbeansPropertiesWrapper
{
   private HashMap _props;

   public NetbeansPropertiesWrapper(HashMap props)
   {
      _props = props;
   }

   public IParserEventsProcessor getParserEventsProcessor(IIdentifier sqlEntryPanelIdentifier, ISession sess)
   {
      if(false == _props.containsKey(IParserEventsProcessorFactory.class.getName()))
      {
         return sess.getParserEventsProcessor(sqlEntryPanelIdentifier);
      }
      else if(null == _props.get(IParserEventsProcessorFactory.class.getName()))
      {
         return null;
      }
      else
      {
         IParserEventsProcessorFactory fact = (IParserEventsProcessorFactory) _props.get(IParserEventsProcessorFactory.class.getName());
         return fact.getParserEventsProcessor(sqlEntryPanelIdentifier, sess);
      }
   }
}
