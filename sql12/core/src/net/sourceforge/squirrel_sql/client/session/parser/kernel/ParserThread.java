package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.sf.jsqlparser.parser.ParseException;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class ParserThread
{
   private final ExecutorService _executorService;
   private ISession _session;
   private ParsingFinishedListener _parsingFinishedListener;
   private Future<?> _currentFuture;
   private List<ErrorInfo> _errorInfos = new ArrayList<>();
   private TableAndAliasParseResult _tableAndAliasParseResult = new TableAndAliasParseResult();
   private ParenthesedSelectParseResult _parenthesedSelectParseResult;

   private ParseTerminateRequestCheck _parseTerminateRequestCheck = () -> onCheckExitThreadRequested();
   private volatile boolean _exitThreadRequested = false;


   public ParserThread(ISession session)
   {
      _session = session;
      _executorService = Executors.newSingleThreadExecutor();
   }

   private void onCheckExitThreadRequested()
   {
      if (_exitThreadRequested)
      {
         throw new ParseTerminateRequestException();
      }
   }


   public void setParsingFinishedListener(ParsingFinishedListener parsingFinishedListener)
   {
      _parsingFinishedListener = parsingFinishedListener;
   }

   public void parseInBackground(String text)
   {
      if(_exitThreadRequested || null != _currentFuture && false == _currentFuture.isDone())
      {
         return;
      }

      _currentFuture = _executorService.submit(() -> onParse(text));
   }

   private void onParse(String text)
   {
      try
      {
         try
         {
            _onParse(text);
            _parsingFinishedListener.parsingFinished();
         }
         catch (ParseTerminateRequestException e)
         {
            // Parsing was requested to finish. We just stop and do nothing.
            //System.out.println("################## FINISHED PARSING ON REQUEST #########################");
         }
      }
      catch (Throwable e)
      {
         if(null != _parsingFinishedListener)
         {
            _parsingFinishedListener.parserExitedOnException(e);
         }
      }
   }

   private void _onParse(String text) throws ParseException
   {
      List<StatementBounds> statementBoundsList = StatementBoundsPrediction.getStatementBoundsList(text, _parseTerminateRequestCheck);

      _parseTerminateRequestCheck.check();

      ArrayList<ErrorInfo> errorInfosBuffer = new ArrayList<>();
      TableAndAliasParseResult allStatementsTableAndAliasParseResultBuffer = new TableAndAliasParseResult();
      ParenthesedSelectParseResult allStatementsParenthesedSelectParseResultBuffer = new ParenthesedSelectParseResult();

      for (StatementBounds statementBounds : statementBoundsList)
      {
         _parseTerminateRequestCheck.check();

         ParsingResult parsingResult = JSqlParserAdapter.executeParsing(statementBounds);

         allStatementsTableAndAliasParseResultBuffer.addParseResult(
               TableAndAliasParseResultCreator.createTableAndAliasParseResultForStatement(_session, statementBounds, parsingResult, errorInfosBuffer, _parseTerminateRequestCheck));

         allStatementsParenthesedSelectParseResultBuffer.addParenthesedSelectInfos(
               ParenthesedSelectInfoCreator.createParenthesedSelectInfosForSingleStatement(_session, statementBounds, parsingResult, errorInfosBuffer, _parseTerminateRequestCheck));

         for (ParseException parseError : parsingResult.getParseErrors())
         {
            _parseTerminateRequestCheck.check();

            int beginPos = statementBounds.getBeginPos() + parseError.currentToken.next.absoluteBegin;
            int endPos = statementBounds.getBeginPos() + parseError.currentToken.next.absoluteEnd;
            errorInfosBuffer.add(new ErrorInfo(parseError.getMessage(), beginPos, endPos));
         }
      }

      _errorInfos = errorInfosBuffer;
      _tableAndAliasParseResult = allStatementsTableAndAliasParseResultBuffer;
      _parenthesedSelectParseResult = allStatementsParenthesedSelectParseResultBuffer;
   }


   public TableAndAliasParseResult getTableAndAliasParseResult()
   {
      return _tableAndAliasParseResult;
   }

   public ParenthesedSelectParseResult getParenthesedSelectParseResult()
   {
      return _parenthesedSelectParseResult;
   }

   public List<ErrorInfo> getErrorInfos()
   {
      return _errorInfos;
   }

   public void exitThread()
   {
      _exitThreadRequested = true;
   }
}
