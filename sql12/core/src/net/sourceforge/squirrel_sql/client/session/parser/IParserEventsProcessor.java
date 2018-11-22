package net.sourceforge.squirrel_sql.client.session.parser;

public interface IParserEventsProcessor
{
	void addParserEventsListener(ParserEventsListener l);
	void removeParserEventsListener(ParserEventsListener l);
   void triggerParser();
}
