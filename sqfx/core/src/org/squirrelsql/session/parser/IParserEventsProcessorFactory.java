package org.squirrelsql.session.parser;

import org.squirrelsql.session.Session;

public interface IParserEventsProcessorFactory
{
   /**
    * Will be called several times with the same parameters.
    */
   IParserEventsProcessor getParserEventsProcessor(int tabContextIdentifier, Session sess);
}
