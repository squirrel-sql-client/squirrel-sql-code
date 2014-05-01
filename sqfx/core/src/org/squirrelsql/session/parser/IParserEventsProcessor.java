package org.squirrelsql.session.parser;

public interface IParserEventsProcessor
{
	public void addParserEventsListener(ParserEventsListener l);
	public void removeParserEventsListener(ParserEventsListener l);
   public void triggerParser();
}
