package net.sourceforge.squirrel_sql.client.session.parser.kernel;


/**
 * This exception exists because I found SQuirreL eating up memory and CPU.
 * I could not reproduce the problem. But I once had the chance to analyze
 * SQuirreL using jconsole when the problem was there. What I found was that for a closed
 * Session the following thread was still alive:
 *
 * net.sourceforge.squirrel_sql.client.session.parser.kernel.ParserThread$IncrementalBuffer.read(ParserThread.java:540)
 *   - locked net.sourceforge.squirrel_sql.client.session.parser.kernel.ParserThread$IncrementalBuffer@12e81c5
 * net.sourceforge.squirrel_sql.client.session.parser.kernel.Scanner.NextCh(Scanner.java:145)
 * net.sourceforge.squirrel_sql.client.session.parser.kernel.Scanner.Scan(Scanner.java:356)
 * net.sourceforge.squirrel_sql.client.session.parser.kernel.Parser.Get(Parser.java:131)
 * net.sourceforge.squirrel_sql.client.session.parser.kernel.Parser.SQLStatement(Parser.java:1851)
 * net.sourceforge.squirrel_sql.client.session.parser.kernel.Parser.squirrelSQL(Parser.java:1860)
 * net.sourceforge.squirrel_sql.client.session.parser.kernel.Parser.parse(Parser.java:1875)
 * net.sourceforge.squirrel_sql.client.session.parser.kernel.ParserThread.runParser(ParserThread.java:430)
 * net.sourceforge.squirrel_sql.client.session.parser.kernel.ParserThread.run(ParserThread.java:367)
 *
 *
 * With this exception I try to force this thread to end.
 *
 */
public class ExitParserThreadRequestException extends RuntimeException
{
}
