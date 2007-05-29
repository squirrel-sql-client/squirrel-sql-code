package net.sourceforge.squirrel_sql.client.session.parser;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

public interface IParserEventsProcessorFactory
{
   /**
    * Will be called several times with the same parameters.
    */
   IParserEventsProcessor getParserEventsProcessor(IIdentifier sqlEntryPanelIdentifier, ISession sess);
}
