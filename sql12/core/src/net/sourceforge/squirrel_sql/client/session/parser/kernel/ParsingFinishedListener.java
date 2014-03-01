package net.sourceforge.squirrel_sql.client.session.parser.kernel;

public interface ParsingFinishedListener
{
	void parsingFinished();
	void parserExitedOnException(Throwable e);
}
