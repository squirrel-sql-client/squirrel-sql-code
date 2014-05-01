package org.squirrelsql.session.parser.kernel;

public interface ParsingFinishedListener
{
	void parsingFinished();
	void parserExitedOnException(Throwable e);
}
